package com.waka.workspace.wakapedometer;

/**
 * 常量类
 * Created by waka on 2016/2/2.
 */
public class Constant {

    /**
     * RequestCode
     */
    public static final int REQUEST_CODE_SIGN_IN_ACTIVITY = 10001;//登录界面，SignInActivity
    public static final int REQUEST_CODE_SIGN_UP_ACTIVITY = 10002;//注册界面，SignUpActivity
    public static final int REQUEST_CODE_MAIN_ACTIVITY = 10003;//主界面，MainActivity

    /**
     * DataBase
     */
    /*  DataBase Name 数据库名  */
    public static final String DB = "WakaPedometer.db";

    /*  Person Table 人员表  */
    //表名
    public static final String TABLE_PERSON = "_person";
    //列名
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_NAME = "_name";
    public static final String COLUMN_SEX = "_sex";
    public static final String COLUMN_AGE = "_age";
    public static final String COLUMN_HEIGHT = "_height";
    public static final String COLUMN_WEIGHT = "_weight";
    public static final String COLUMN_ACCOUNT = "_account";
    public static final String COLUMN_PASSWORD = "_password";

}
