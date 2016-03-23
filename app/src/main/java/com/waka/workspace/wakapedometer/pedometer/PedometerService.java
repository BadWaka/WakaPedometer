package com.waka.workspace.wakapedometer.pedometer;

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

import java.lang.ref.WeakReference;
import java.sql.Date;

import com.waka.workspace.wakapedometer.Constant;
import com.waka.workspace.wakapedometer.R;
import com.waka.workspace.wakapedometer.database.StepInfoDBHelper;
import com.waka.workspace.wakapedometer.utils.LoginInfoUtil;
import com.waka.workspace.wakapedometer.database.DBHelper;
import com.waka.workspace.wakapedometer.pedometer.steplistener.AccelerometerListener;
import com.waka.workspace.wakapedometer.pedometer.steplistener.StepCounterListener;
import com.waka.workspace.wakapedometer.pedometer.steplistener.StepDetectorListener;

/**
 * 计步服务
 * Created by waka on 2016/2/1.
 */
public class PedometerService extends Service {

    private static final String TAG = "Pedometer Service";

    //服务运行标志
    private boolean serviceFlag = false;

    //当前步数
    private int currentStep;//通知栏显示的统一步数，方便其他类调用

    //卡路里步数换算比例
    private static final float SCALE_STEP_CALORIES = 43.22f;

    //数据库操作类
    private int mId;//当前用户id
    private int timeCounter = 0;//用来计时间，过1s加1，到5s变为0,5s一循环，5s更新一次数据库
    private DBHelper mDBHelper;
    private SQLiteDatabase mDB;
    private StepInfoDBHelper mStepInfoDBHelper;

    //通知\前台服务
    private Notification.Builder mNotificationBuilder;
    private Notification mNotification;
    private static final int NOTIFICATION_STEP_SERVICE = 1;//前台服务标记

    //传感器
    private SensorManager mSensorManager;//传感器管理器
    private Sensor mSensor;//最终使用的传感器
    private int mSensorType;//最终使用的传感器类型，作为一个判断标识
    private SensorEventListener mSensorEventListener;//监听器

    //StepCounter传感器步数更新线程
    private int stepCounter = 0;//处理好的步数数据
    private int stepCounterHistory = -1;//数据库中的历史步数
    public int stepCounterRaw = 0;//原始步数数据
    private StepCounterThread mStepCounterThread;
    private static final int MSG_WHAT_STEP_COUNTER_THREAD = 1;//StepCounter传感器步数更新步数通知  message.what

    //StepDetector传感器步数更新线程
    public int stepDetector = 0;//StepDetector传感器计步
    private StepDetectorThread mStepDetectorThread;
    private static final int MSG_WHAT_STEP_DETECTOR_THREAD = 2;//StepDetector传感器步数更新步数通知  message.what

    //加速度传感器步数更新线程
    public int stepAccelerometer = 0;//加速度传感器计步
    private StepAccelerometerThread mStepAccelerometerThread;
    private static final int MSG_WHAT_STEP_ACCELEROMETER_THREAD = 3;//加速度传感器步数更新步数通知  message.what

    //Handler，在主线程更新通知
    private MyHandler mHandler = new MyHandler(PedometerService.this);

    /**
     * 静态内部类Handler，在主线程更新通知
     * <p/>
     * 使用静态内部类和弱引用防止handler占用Service，导致Service无法按时回收
     */
    private static class MyHandler extends Handler {

        private WeakReference<PedometerService> serviceWeakReference;//使用弱引用

        public MyHandler(PedometerService service) {

            serviceWeakReference = new WeakReference<PedometerService>(service);
        }

        @Override
        public void handleMessage(Message msg) {

            PedometerService service = serviceWeakReference.get();

            if (service != null) {

                switch (msg.what) {

                    //StepCounter传感器步数更新通知
                    case MSG_WHAT_STEP_COUNTER_THREAD:

                        service.showNotification(service.stepCounter);

                        break;

                    //StepDetector传感器步数更新通知
                    case MSG_WHAT_STEP_DETECTOR_THREAD:

                        service.showNotification(service.stepDetector);

                        break;

                    //加速度传感器步数更新通知
                    case MSG_WHAT_STEP_ACCELEROMETER_THREAD:

                        service.showNotification(service.stepAccelerometer);

                        break;

                    default:
                        break;
                }

            }

        }
    }

    /**
     * onCreate，创建时调用
     */
    @Override
    public void onCreate() {
        super.onCreate();

        Log.i(TAG, "服务创建");

        //服务启动
        serviceFlag = true;

        //初始化数据库
        mId = LoginInfoUtil.getCurrentLoginId(PedometerService.this);
        mDBHelper = new DBHelper(PedometerService.this, Constant.DB, null, 1);
        mDB = mDBHelper.getWritableDatabase();
        mStepInfoDBHelper = new StepInfoDBHelper(mDB);

        //传感器管理器
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        //选择最好的传感器
        chooseBestSensor();

        //初始化监听器
        initListener();

        //开始计步
        startCountingStep();

        //根据不同传感器类型开启不同线程，使用不同步数
        switch (mSensorType) {

            case Sensor.TYPE_STEP_COUNTER:

                //开启前台服务
                showNotification(stepCounter);

                //开启StepCounter传感器步数更新线程
                if (mStepCounterThread == null) {
                    mStepCounterThread = new StepCounterThread(PedometerService.this);
                }
                mStepCounterThread.start();

                break;

            case Sensor.TYPE_STEP_DETECTOR:

                //得到今天的步数
                stepDetector = mStepInfoDBHelper.getStepByIdAndDate(mId, new Date(System.currentTimeMillis()));

                //如果今天没有步数记录
                if (stepDetector == -1) {
                    mStepInfoDBHelper.insert(mId, new Date(System.currentTimeMillis()), 0);
                    stepDetector = 0;
                }

                //开启前台服务
                showNotification(stepDetector);

                //开启StepDetector传感器步数更新线程
                if (mStepDetectorThread == null) {
                    mStepDetectorThread = new StepDetectorThread(PedometerService.this);
                }
                mStepDetectorThread.start();

                break;

            case Sensor.TYPE_ACCELEROMETER:

                //得到今天的步数
                stepAccelerometer = mStepInfoDBHelper.getStepByIdAndDate(mId, new Date(System.currentTimeMillis()));

                //如果今天没有步数记录
                if (stepAccelerometer == -1) {
                    mStepInfoDBHelper.insert(mId, new Date(System.currentTimeMillis()), 0);
                    stepAccelerometer = 0;
                }

                //开启前台服务
                showNotification(stepAccelerometer);

                //开启加速度传感器步数更新线程
                if (mStepAccelerometerThread == null) {
                    mStepAccelerometerThread = new StepAccelerometerThread(PedometerService.this);
                }
                mStepAccelerometerThread.start();

                break;
        }

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

    }

    /**
     * 停止计步（Service）
     */
    private void stopCountingStep() {

        Log.i(TAG, "停止计步");

        mSensorManager.unregisterListener(mSensorEventListener);//取消注册

    }

    /**
     * 显示通知\前台服务
     *
     * @param step 需要传入步数，因为三种传感器计算方式不同
     */
    private void showNotification(int step) {

        //得到当前步数
        currentStep = step;

        //被观察者数据改变，更新数据
        StepObservable.getInstance().notifyStepChange(currentStep);

        if (mNotificationBuilder == null) {
            mNotificationBuilder = new Notification.Builder(PedometerService.this);
            mNotificationBuilder.setTicker(getString(R.string.prompt_notification_ticker_pedometer_service));//通知ticker,在通知刚生成时在手机最上方弹出的一闪而过的提示
            mNotificationBuilder.setSmallIcon(getApplicationInfo().icon);//小图标
        }

        mNotificationBuilder.setContentTitle(step + getString(R.string.prompt_notification_title_step_pedometer_service));//标题
        mNotificationBuilder.setContentText(String.format("%.1f", (step * SCALE_STEP_CALORIES) / 1000) + getString(R.string.prompt_notification_text_calories_pedometer_service));//内容

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
     * 销毁时调用
     */
    @Override
    public void onDestroy() {

        Log.i(TAG, "服务销毁");

        //停止计步
        stopCountingStep();

        //停止前台服务
        stopForeground(true);

        //设置服务销毁标志，停止线程
        serviceFlag = false;

        //移除messageQueue中的所有message
        mHandler.removeCallbacksAndMessages(null);

        super.onDestroy();
    }

    /**
     * StepCounter传感器步数更新线程 TODO
     * <p/>
     * 使用静态内部类和弱引用来减少内存溢出的可能
     */
    private static class StepCounterThread extends Thread {

        private WeakReference<PedometerService> serviceWeakReference;

        public StepCounterThread(PedometerService service) {

            serviceWeakReference = new WeakReference<PedometerService>(service);
        }

        @Override
        public void run() {

            PedometerService service = serviceWeakReference.get();

            if (service != null) {

                while (service.serviceFlag) {

                    try {

                        //每1s更新通知栏
                        sleep(1000);

                        //循环，5s时timeCounter就会变为0
                        service.timeCounter++;
                        service.timeCounter = service.timeCounter % 5;

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    //获得今天的步数，即时没有获取到也没有关系，没有获取到返回-1，误差很小
                    int step = service.mStepInfoDBHelper.getStepByIdAndDate(service.mId, new Date(System.currentTimeMillis()));

                    //如果stepCounterHistory还未初始化（即等于-1），将系统总步数赋给它
                    if (service.stepCounterHistory == -1) {
                        service.stepCounterHistory = service.stepCounterRaw - step;//核心算法：这个算法很巧妙，即使算出的结果为负数，也是我们需要的
                        Log.i(TAG, "stepCounterHistory---->" + service.stepCounterHistory);
                    }

                    //实际走的步数等于总步数减去历史步数
                    service.stepCounter = service.stepCounterRaw - service.stepCounterHistory;

                    Log.i(TAG, "stepCounter---->" + service.stepCounter);

                    //更新通知栏
                    service.mHandler.sendEmptyMessage(MSG_WHAT_STEP_COUNTER_THREAD);

                    //每过5s，更新数据库
                    if (service.timeCounter == 0) {

                        //写入数据到数据库中
                        boolean updateFlag = service.mStepInfoDBHelper.update(service.mId, new Date(System.currentTimeMillis()), service.stepCounter);

                        //如果更新失败，说明日期已改变
                        if (!updateFlag) {
                            service.mStepInfoDBHelper.insert(service.mId, new Date(System.currentTimeMillis()), 0);//添加新数据
                            service.stepCounterHistory = -1;//重置历史步数
                        }

                    }

                }
            }

        }
    }

    /**
     * StepDetector传感器步数更新线程
     * <p/>
     * 使用静态内部类和弱引用来减少内存溢出的可能
     */
    private static class StepDetectorThread extends Thread {

        private WeakReference<PedometerService> serviceWeakReference;

        public StepDetectorThread(PedometerService service) {

            serviceWeakReference = new WeakReference<PedometerService>(service);
        }

        @Override
        public void run() {

            PedometerService service = serviceWeakReference.get();

            if (service != null) {

                while (service.serviceFlag) {

                    try {

                        //每1s更新通知栏
                        sleep(1000);
                        service.mHandler.sendEmptyMessage(MSG_WHAT_STEP_DETECTOR_THREAD);

                        //循环，5s时timeCounter就会变为0
                        service.timeCounter++;
                        service.timeCounter = service.timeCounter % 5;

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    //每过5s，更新数据库
                    if (service.timeCounter == 0) {

                        //写入数据到数据库中
                        boolean updateFlag = service.mStepInfoDBHelper.update(service.mId, new Date(System.currentTimeMillis()), service.stepDetector);

                        //如果更新失败，说明日期已改变
                        if (!updateFlag) {
                            service.mStepInfoDBHelper.insert(service.mId, new Date(System.currentTimeMillis()), 0);//添加新数据
                            service.stepDetector = 0;//步数重置为0
                        }

                    }

                }
            }

        }
    }

    /**
     * 加速度传感器步数更新线程
     * <p/>
     * 使用静态内部类和弱引用来减少内存溢出的可能
     */
    private static class StepAccelerometerThread extends Thread {

        private WeakReference<PedometerService> serviceWeakReference;

        public StepAccelerometerThread(PedometerService service) {

            serviceWeakReference = new WeakReference<PedometerService>(service);
        }

        @Override
        public void run() {

            PedometerService service = serviceWeakReference.get();

            if (service != null) {

                //无限循环
                while (service.serviceFlag) {

                    try {

                        //每1s更新通知栏
                        sleep(1000);
                        service.mHandler.sendEmptyMessage(MSG_WHAT_STEP_ACCELEROMETER_THREAD);

                        //循环，5s时timeCounter就会变为0
                        service.timeCounter++;
                        service.timeCounter = service.timeCounter % 5;

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    //每过5s，更新数据库
                    if (service.timeCounter == 0) {

                        //写入数据到数据库中
                        boolean updateFlag = service.mStepInfoDBHelper.update(service.mId, new Date(System.currentTimeMillis()), service.stepAccelerometer);

                        //如果更新失败，说明日期已改变
                        if (!updateFlag) {
                            service.mStepInfoDBHelper.insert(service.mId, new Date(System.currentTimeMillis()), 0);//添加新数据
                            service.stepAccelerometer = 0;//步数重置为0
                        }
                    }

                }

            }

        }
    }

    /**
     * 步数改变时调用
     *
     * @param step
     */
    public void stepChange(int step) {

        //判断当前传感器类型，不同传感器要区别对待
        switch (mSensorType) {

            //StepCounter   步行总数,最理想的健康跟踪传感器，实时返回步数总数，重启手机时清空
            case Sensor.TYPE_STEP_COUNTER:

                break;

            //StepDetector  步数探测器，第二选择,适合航迹推算，每走一步+1
            case Sensor.TYPE_STEP_DETECTOR:

                break;

            //Accelerometer  加速度传感器，最差选择，软件层算法模拟，精度最低，但普及率最高
            case Sensor.TYPE_ACCELEROMETER:

                break;

            default:
                break;
        }

    }

}
