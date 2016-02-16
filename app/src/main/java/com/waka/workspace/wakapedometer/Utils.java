package com.waka.workspace.wakapedometer;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * 工具类
 * Created by waka on 2016/2/16.
 */
public class Utils {

    /**
     * 设置登录Cookie，在SharedPreferences中
     *
     * @param context
     * @param loginCookie
     */
    public static void setLoginCookie(Context context, String loginCookie) {

        //得到SharedPreferences的对象    该方法自动使用当前应用程序的包名作为前缀来命名SharedPreferences文件
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(Constant.COOKIE_LOGIN, loginCookie);
        editor.commit();
    }

    /**
     * 得到登录Cookie
     *
     * @param context
     * @return loginCookie
     */
    public static String getLoginCookie(Context context) {

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        String loginCookie = sp.getString(Constant.COOKIE_LOGIN, "");//如果没有找到就用""来代替
        return loginCookie;
    }
}
