package com.waka.workspace.wakapedometer.pedometer.steplistener;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.util.Log;

import com.waka.workspace.wakapedometer.pedometer.PedometerService;

/**
 * StepDetectorListener
 * 步数探测器，第二选择,适合航迹推算，每走一步+1
 * ------------------------------------------
 * Created by waka on 2016/2/2.
 */
public class StepDetectorListener implements SensorEventListener {

    private static final String TAG = "Pedometer StepDetector";

    private PedometerService mPedometerService;

    /**
     * 构造方法
     *
     * @param pedometerService
     */
    public StepDetectorListener(PedometerService pedometerService) {
        mPedometerService = pedometerService;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {//当传感器监测到的数值发生变化时就会调用

        if (event.values[0] == 1) {
            mPedometerService.stepDetector++;//每走一步+1
        }
        Log.i(TAG, "stepAccelerometer:" + mPedometerService.stepDetector);

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {//当传感器的精度发生变化时就会调用

    }
}
