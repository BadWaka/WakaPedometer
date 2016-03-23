package com.waka.workspace.wakapedometer;

/**
 * 常量类
 * Created by waka on 2016/2/2.
 */
public class Constant {

    /**
     * 登录信息
     */
    public static final String COOKIE_LOGIN = "loginCookie";//登录Cookie在SharedPreference中的关键字
    public static final String CURRENT_LOGIN_ID = "currentLoginId";//当前登录人员的id

    /**
     * 步数改变广播
     */
    public static final String STEP_CHANGE_BROADCAST = "com.waka.workspace.wakapedometer.STEP_CHANGE_BROADCAST";

    /**
     * RequestCode
     */
    public static final int REQUEST_CODE_SIGN_IN_ACTIVITY = 10001;//登录界面，SignInActivity
    public static final int REQUEST_CODE_SIGN_UP_ACTIVITY = 10002;//注册界面，SignUpActivity
    public static final int REQUEST_CODE_MAIN_ACTIVITY = 10003;//主界面，MainActivity
    public static final int REQUEST_CODE_MINE_ACTIVITY = 10004;//个人中心界面，MineActivity

    /**
     * DataBase
     */
    /*  DataBase Name 数据库名  */
    public static final String DB = "WakaPedometer.db";

    /*  Person Table 人员表  */
    //表名
    public static final String TABLE_PERSON = "_person";
    //列名
    public static final String PERSON_COLUMN_ID = "_id";
    public static final String PERSON_COLUMN_NICK_NAME = "_nickname";
    public static final String PERSON_COLUMN_NAME = "_name";
    public static final String PERSON_COLUMN_SEX = "_sex";
    public static final String PERSON_COLUMN_AGE = "_age";
    public static final String PERSON_COLUMN_HEIGHT = "_height";
    public static final String PERSON_COLUMN_WEIGHT = "_weight";
    public static final String PERSON_COLUMN_ACCOUNT = "_account";
    public static final String PERSON_COLUMN_PASSWORD = "_password";
    public static final String PERSON_COLUMN_HEADICON_URL = "_headicon_url";

    /*  StepInfo Table 步数信息表    */
    //表名
    public static final String TABLE_STEP_INFO = "_step_info";
    //列名
    public static final String STEP_INFO_COLUMN_PERSON_ID = "_person_id";
    public static final String STEP_INFO_COLUMN_STEP = "_step";
    public static final String STEP_INFO_COLUMN_DATE = "_date";

}
