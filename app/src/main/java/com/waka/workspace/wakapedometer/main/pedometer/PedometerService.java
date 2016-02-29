package com.waka.workspace.wakapedometer.main.pedometer;

import android.app.Notification;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.hardware.Sensor;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.waka.workspace.wakapedometer.R;
import com.waka.workspace.wakapedometer.database.DBHelper;
import com.waka.workspace.wakapedometer.main.pedometer.steplistener.AccelerometerListener;
import com.waka.workspace.wakapedometer.main.pedometer.steplistener.StepCounterListener;
import com.waka.workspace.wakapedometer.main.pedometer.steplistener.StepDetectorListener;

/**
 * 计步服务
 * Created by waka on 2016/2/1.
 */
public class PedometerService extends Service {

    private static final String TAG = "Pedometer Service";

    //传感器
    private SensorManager mSensorManager;//传感器管理器
    private Sensor mSensor;//最终使用的传感器
    private int mSensorType;//最终使用的传感器类型，作为一个判断标识
    private SensorEventListener mSensorEventListener;//监听器

    //步数
    public int steps = 0;
    private static final float SCALE_STEP_CALORIES = 43.22f;//卡路里步数换算比例

    //通知\前台服务
    private Notification.Builder mNotificationBuilder;
    private Notification mNotification;
    private static final int NOTIFICATION_STEP_SERVICE = 1;//前台服务标记

    //步数更新线程
    private UpdateStepThread mUpdateStepThread;
    private boolean mUpdateStepThreadSwitch = true;//线程开关
    private static final long TIME_INTERVAL_UPDATE_STEP_THREAD = 1000;//1s更新一次步数
    private static final int MSG_WHAT_UPDATE_STEP = 1;//更新步数通知  message.what

    //数据库操作类
    private int mId;//当前用户id
    private DBHelper mDBHelper;
    private SQLiteDatabase mDB;

    //写入数据库线程
    private WriteStepToDBThread mWriteStepToDBThread;
    private boolean mWriteStepToDBThreadSwitch = true;//线程开关
    private static final long TIME_INTERVAL_WRITE_STEP_TO_DBTHREAD = 5000;//5s写入一次数据库
    private static final int MSG_WHAT_WRITE_STEP_TO_DB = 2;//将步数写入DB    message.what

    //Handler
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            //更新步数通知
            if (msg.what == MSG_WHAT_UPDATE_STEP) {

                showNotification();

            }
        }
    };

    /**
     * onCreate，创建时调用
     */
    @Override
    public void onCreate() {
        super.onCreate();

        Log.i(TAG, "服务创建");
    }

    /**
     * onBind   这里不需要绑定Activity
     *
     * @param intent
     * @return
     */
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /**
     * onStartCommand   服务启动时调用
     *
     * @param intent
     * @param flags
     * @param startId
     * @return
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "服务启动");

        //传感器管理器
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        //选择最好的传感器
        chooseBestSensor();

        //初始化监听器
        initListener();

        //开始计步
        startCountingStep();

        //显示通知\前台服务
        showNotification();

        return START_STICKY;//粘性服务，在被kill后尝试自动开启
    }

    /**
     * 选择最好的传感器
     */
    private void chooseBestSensor() {

        //根据设备支持的传感器，选择要使用的传感器
        //优先级为  stepCounter > stepDetector >>> accelerometer
        Sensor stepCounter = mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);//步行总数,最理想的健康跟踪传感器，实时返回步数总数，重启手机时清空    http://developer.android.com/reference/android/hardware/Sensor.html#TYPE_STEP_DETECTOR
        if (stepCounter != null) {
            mSensor = stepCounter;
            mSensorType = Sensor.TYPE_STEP_COUNTER;
        } else {
            Sensor stepDetector = mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);//步数探测器，第二选择,适合航迹推算，每走一步+1     http://developer.android.com/reference/android/hardware/Sensor.html#TYPE_STEP_DETECTOR
            if (stepDetector != null) {
                mSensor = stepDetector;
                mSensorType = Sensor.TYPE_STEP_DETECTOR;
            } else {
                Sensor accelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);//加速度传感器，最差选择，软件层算法模拟，精度最低，但普及率最高
                if (accelerometer != null) {
                    mSensor = accelerometer;
                    mSensorType = Sensor.TYPE_ACCELEROMETER;
                } else {
                    Toast.makeText(this, getString(R.string.prompt_no_use_sensor_pedometer_service), Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    /**
     * 初始化监听器
     */
    private void initListener() {

        //判断当前传感器类型，不同传感器要区别对待
        switch (mSensorType) {

            //StepCounter   步行总数,最理想的健康跟踪传感器，实时返回步数总数，重启手机时清空
            case Sensor.TYPE_STEP_COUNTER:
                mSensorEventListener = new StepCounterListener(PedometerService.this);
                break;

            //StepDetector  步数探测器，第二选择,适合航迹推算，每走一步+1
            case Sensor.TYPE_STEP_DETECTOR:
                mSensorEventListener = new StepDetectorListener(PedometerService.this);
                break;

            //Accelerometer  加速度传感器，最差选择，软件层算法模拟，精度最低，但普及率最高
            case Sensor.TYPE_ACCELEROMETER:
                mSensorEventListener = new AccelerometerListener(PedometerService.this);
                break;

            default:
                break;
        }
    }

    /**
     * 开始计步（Service）
     * ------------------------
     * 注册时会抛出一个java.lang.Exception，但是程序不会崩溃，依然可以计步，暂时不明白是怎么回事。。。
     */
    private void startCountingStep() {

        mSensorManager.registerListener(mSensorEventListener, mSensor, SensorManager.SENSOR_DELAY_NORMAL);//注册Listener
        Log.i(TAG, "开始计步");

        //开启步数更新线程
        mUpdateStepThreadSwitch = true;
        if (mUpdateStepThread == null) {
            mUpdateStepThread = new UpdateStepThread();
        }
        mUpdateStepThread.start();

    }

    /**
     * 显示通知\前台服务
     */
    private void showNotification() {

        if (mNotificationBuilder == null) {
            mNotificationBuilder = new Notification.Builder(PedometerService.this);
            mNotificationBuilder.setTicker(getString(R.string.prompt_notification_ticker_pedometer_service));//通知ticker,在通知刚生成时在手机最上方弹出的一闪而过的提示
            mNotificationBuilder.setSmallIcon(getApplicationInfo().icon);//小图标
        }

        mNotificationBuilder.setContentTitle(steps + getString(R.string.prompt_notification_title_step_pedometer_service));//标题
        mNotificationBuilder.setContentText(String.format("%.1f", (steps * SCALE_STEP_CALORIES) / 1000) + getString(R.string.prompt_notification_text_calories_pedometer_service));//内容

        //兼容低版本
        if (Build.VERSION.SDK_INT >= 16) {
            mNotification = mNotificationBuilder.build();
        } else {
            mNotification = mNotificationBuilder.getNotification();
        }

        mNotification.flags = Notification.FLAG_NO_CLEAR;//在点击通知后，通知并不会消失
        startForeground(NOTIFICATION_STEP_SERVICE, mNotification);

    }

    /**
     * 停止计步（Service）
     */
    private void stopCountingStep() {

        mSensorManager.unregisterListener(mSensorEventListener);//取消注册
        Log.i(TAG, "停止计步");

        //停止步数更新线程
        mUpdateStepThreadSwitch = false;

    }

    /**
     * 销毁时调用
     */
    @Override
    public void onDestroy() {
        super.onDestroy();

        //停止计步
        stopCountingStep();

        //停止前台服务
        stopForeground(true);

        Log.i(TAG, "服务销毁");
    }

    /**
     * 步数更新线程
     */
    class UpdateStepThread extends Thread {

        @Override
        public void run() {

            //无限循环
            while (mUpdateStepThreadSwitch) {

                try {

                    sleep(TIME_INTERVAL_UPDATE_STEP_THREAD);
                    mHandler.sendEmptyMessage(MSG_WHAT_UPDATE_STEP);

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 步数写入数据库线程
     */
    class WriteStepToDBThread extends Thread {

        @Override
        public void run() {

            //无限循环
            while (mWriteStepToDBThreadSwitch) {

                try {

                    sleep(TIME_INTERVAL_WRITE_STEP_TO_DBTHREAD);
                    mHandler.sendEmptyMessage(MSG_WHAT_WRITE_STEP_TO_DB);

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
