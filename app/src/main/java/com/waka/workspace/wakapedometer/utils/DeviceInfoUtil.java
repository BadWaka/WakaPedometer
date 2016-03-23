package com.waka.workspace.wakapedometer.utils;

import android.util.Log;

/**
 * 设备信息工具类
 * <p/>
 * Created by waka on 2016/3/3.
 */
public class DeviceInfoUtil {

    public static final String TAG = "DeviceInfoUtil";

    /**
     * 得到应用程序最高可用内存
     *
     * @return 单位为MB
     */
    public static int getMaxMemory() {

        int maxMemory = (int) (Runtime.getRuntime().maxMemory() / (1024 * 1024));

        Log.i(TAG, "Max Memory is " + maxMemory + "MB");

        return maxMemory;
    }
}
