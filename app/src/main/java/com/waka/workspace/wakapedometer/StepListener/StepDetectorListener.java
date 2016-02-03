package com.waka.workspace.wakapedometer.steplistener;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.util.Log;

/**
 * StepDetectorListener
 * 步数探测器，第二选择,适合航迹推算，每走一步+1
 * ------------------------------------------
 * Created by waka on 2016/2/2.
 */
public class StepDetectorListener implements SensorEventListener {

    private static final String TAG = "Pedometer StepDetector";

    private int steps;//步数

    @Override
    public void onSensorChanged(SensorEvent event) {//当传感器监测到的数值发生变化时就会调用

        if (event.values[0] == 1) {
            steps++;//每走一步+1
        }
        Log.i(TAG, "steps:" + steps);

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {//当传感器的精度发生变化时就会调用

    }

    public int getSteps() {
        return steps;
    }

    public void setSteps(int steps) {
        this.steps = steps;
    }
}
