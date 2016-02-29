package com.waka.workspace.wakapedometer.database;

import android.database.sqlite.SQLiteDatabase;

/**
 * 专门对步数表进行增删改查的辅助类
 * Created by waka on 2016/2/29.
 */
public class StepInfoDB {

    private static final String TAG = "Pedometer StepInfoDB";

    private SQLiteDatabase mDB;

    /**
     * 构造方法，须传入db
     *
     * @param db
     */
    public StepInfoDB(SQLiteDatabase db) {
        mDB = db;
    }


}
