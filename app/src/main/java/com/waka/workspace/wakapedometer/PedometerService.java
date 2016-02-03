package com.waka.workspace.wakapedometer;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.waka.workspace.wakapedometer.steplistener.AccelerometerListener;
import com.waka.workspace.wakapedometer.steplistener.StepCounterListener;
import com.waka.workspace.wakapedometer.steplistener.StepDetectorListener;

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

        return super.onStartCommand(intent, flags, startId);
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
                    Toast.makeText(this, "没有可用的传感器！", Toast.LENGTH_SHORT).show();
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
                mSensorEventListener = new StepCounterListener();
                break;

            //StepDetector  步数探测器，第二选择,适合航迹推算，每走一步+1
            case Sensor.TYPE_STEP_DETECTOR:
                mSensorEventListener = new StepDetectorListener();
                break;

            //Accelerometer  加速度传感器，最差选择，软件层算法模拟，精度最低，但普及率最高
            case Sensor.TYPE_ACCELEROMETER:
                mSensorEventListener = new AccelerometerListener();
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

        mSensorManager.unregisterListener(mSensorEventListener);//取消注册
        Log.i(TAG, "停止计步");

    }

    /**
     * 销毁时调用
     */
    @Override
    public void onDestroy() {
        super.onDestroy();

        //停止计步
        stopCountingStep();

        Log.i(TAG, "服务销毁");
    }

}
