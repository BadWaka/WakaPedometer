package com.waka.workspace.wakapedometer.StepListener;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

/**
 * AccelerometerListener
 * 加速度传感器，最差选择，软件层算法模拟，精度最低，但普及率最高
 * ---------------------------------------------------
 * Created by waka on 2016/2/2.
 */
public class AccelerometerListener implements SensorEventListener {

    private static final String TAG = "Pedometer Accelerometer";

    private int steps = 0;//步数
    private float sensitivity = 2; //灵敏度     灵敏度为1的时候轻轻晃一下就计步了，灵敏度为10时得狠狠地晃才行

    private float mLastValues[] = new float[3 * 2];
    private float mScale[] = new float[2];
    private float mYOffset;
    private static long end = 0;
    private static long start = 0;

    private float mLastDirections[] = new float[3 * 2];//最后加速度方向
    private float mLastExtremes[][] = {new float[3 * 2], new float[3 * 2]};
    private float mLastDiff[] = new float[3 * 2];
    private int mLastMatch = -1;

    /**
     * 构造方法
     */
    public AccelerometerListener() {
        super();
        int h = 480;
        mYOffset = h * 0.5f;
        mScale[0] = -(h * 0.5f * (1.0f / (SensorManager.STANDARD_GRAVITY * 2)));
        mScale[1] = -(h * 0.5f * (1.0f / (SensorManager.MAGNETIC_FIELD_EARTH_MAX)));
    }

    @Override
    public void onSensorChanged(SensorEvent event) {//当传感器监测到的数值发生变化时就会调用

        //使用算法模拟
        float vSum = 0;
        for (int i = 0; i < 3; i++) {
            final float v = mYOffset + event.values[i] * mScale[1];
            vSum += v;
        }
        int k = 0;
        float v = vSum / 3;
        float direction = (v > mLastValues[k] ? 1 : (v < mLastValues[k] ? -1 : 0));
        if (direction == -mLastDirections[k]) {
            // Direction changed
            int extType = (direction > 0 ? 0 : 1); // minumum or maximum?
            mLastExtremes[extType][k] = mLastValues[k];
            float diff = Math.abs(mLastExtremes[extType][k] - mLastExtremes[1 - extType][k]);

            if (diff > sensitivity) {

                boolean isAlmostAsLargeAsPrevious = diff > (mLastDiff[k] * 2 / 3);
                boolean isPreviousLargeEnough = mLastDiff[k] > (diff / 3);
                boolean isNotContra = (mLastMatch != 1 - extType);
                if (isAlmostAsLargeAsPrevious && isPreviousLargeEnough && isNotContra) {
                    end = System.currentTimeMillis();

                    if (end - start > 500) {// 此时判断为走了一步
                        steps++;
                        mLastMatch = extType;
                        start = end;
                    }
                } else {
                    mLastMatch = -1;
                }
            }
            mLastDiff[k] = diff;
        }
        mLastDirections[k] = direction;
        mLastValues[k] = v;

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

    public float getSensitivity() {
        return sensitivity;
    }

    public void setSensitivity(float sensitivity) {
        this.sensitivity = sensitivity;
    }
}
