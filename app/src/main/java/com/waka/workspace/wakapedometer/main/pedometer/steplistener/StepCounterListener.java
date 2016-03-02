package com.waka.workspace.wakapedometer.main.pedometer.steplistener;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.util.Log;

import com.waka.workspace.wakapedometer.main.pedometer.PedometerService;

/**
 * StepCounterListener
 * 步行总数,最理想的健康跟踪传感器，实时返回步数总数，重启手机时清空
 * -----------------------------
 * Created by waka on 2016/2/2.
 */
public class StepCounterListener implements SensorEventListener {

    private static final String TAG = "Pedometer StepCounter";

    private PedometerService mPedometerService;

    /**
     * 构造方法
     *
     * @param pedometerService
     */
    public StepCounterListener(PedometerService pedometerService) {
        mPedometerService = pedometerService;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {//当传感器监测到的数值发生变化时就会调用

        mPedometerService.stepCounterRaw = (int) event.values[0];//直接得到步行总数
        Log.i(TAG, "stepAccelerometer:" + mPedometerService.stepCounterRaw);

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {//当传感器的精度发生变化时就会调用

    }

}
