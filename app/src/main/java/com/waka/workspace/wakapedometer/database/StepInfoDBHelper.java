package com.waka.workspace.wakapedometer.database;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.waka.workspace.wakapedometer.Constant;

import java.sql.Date;

/**
 * 专门对步数表进行增删改查的辅助类
 * Created by waka on 2016/2/29.
 */
public class StepInfoDBHelper {

    private static final String TAG = "StepInfoDBHelper";

    private SQLiteDatabase mDB;

    /**
     * 构造方法，须传入db
     *
     * @param db
     */
    public StepInfoDBHelper(SQLiteDatabase db) {
        mDB = db;
    }

    /**
     * 添加数据
     *
     * @param personId
     * @param date
     * @param step
     * @return 添加成功返回true，失败返回false
     */
    public synchronized boolean insert(int personId, Date date, int step) {

        //判断数据库中是否存在
        boolean existFlag = isExist(personId, date);

        if (existFlag) {
            return false;
        }

        //不存在则添加
        //INSERT INTO _step_info (_step,_person_id,_date) values(?,?,?)
        mDB.execSQL("insert into " + Constant.TABLE_STEP_INFO
                        + " (" + Constant.STEP_INFO_COLUMN_STEP
                        + "," + Constant.STEP_INFO_COLUMN_PERSON_ID
                        + "," + Constant.STEP_INFO_COLUMN_DATE
                        + ") values (?,?,?)",
                new String[]{"" + step, "" + personId, date.toString()});

        Log.i(TAG, "不存在则添加" + "insert into " + Constant.TABLE_STEP_INFO
                + " (" + Constant.STEP_INFO_COLUMN_STEP
                + "," + Constant.STEP_INFO_COLUMN_PERSON_ID
                + "," + Constant.STEP_INFO_COLUMN_DATE
                + ") values (?,?,?)");

        return true;
    }

    /**
     * 更新数据
     *
     * @param personId
     * @param date
     * @param step
     * @return 更新成功返回true，失败返回false
     */
    public synchronized boolean update(int personId, Date date, int step) {

        //判断数据库中是否存在
        boolean existFlag = isExist(personId, date);

        if (!existFlag) {
            return false;
        }

        //存在则更新
        //UPDATE _step_info SET _step = ? WHERE _person_id = ? AND _date = ?
        mDB.execSQL("UPDATE " + Constant.TABLE_STEP_INFO
                        + " SET " + Constant.STEP_INFO_COLUMN_STEP + " = ?"
                        + " WHERE " + Constant.STEP_INFO_COLUMN_PERSON_ID + " = ?"
                        + " AND " + Constant.STEP_INFO_COLUMN_DATE + " = ?",
                new String[]{"" + step, "" + personId, date.toString()});

        Log.i(TAG, "存在则更新" + "UPDATE " + Constant.TABLE_STEP_INFO
                + " SET " + Constant.STEP_INFO_COLUMN_STEP + " = ?"
                + " WHERE " + Constant.STEP_INFO_COLUMN_PERSON_ID + " = ?"
                + " AND " + Constant.STEP_INFO_COLUMN_DATE + " = ?");

        return true;
    }

    /**
     * 根据人员id和日期获取具体步数
     *
     * @param personId
     * @param date
     * @return
     */
    public synchronized int getStepByIdAndDate(int personId, Date date) {

        //"SELECT * FROM _step_info WHERE _date = ? AND _person_id = ?"
        Cursor cursor = mDB.rawQuery("SELECT * FROM " + Constant.TABLE_STEP_INFO
                + " WHERE " + Constant.STEP_INFO_COLUMN_DATE + " = ? AND "
                + Constant.STEP_INFO_COLUMN_PERSON_ID + " = ?", new String[]{date.toString(), "" + personId});

        //如果不存在
        if (!cursor.moveToFirst()) {
            cursor.close();
            return -1;
        }

        int step = cursor.getInt(cursor.getColumnIndex(Constant.STEP_INFO_COLUMN_STEP));
        cursor.close();

        return step;
    }

    /**
     * 是否已存在同一天同一人
     * <p/>
     * 日期和人员id作为复合主键
     *
     * @param personId
     * @param date
     * @return
     */
    public synchronized boolean isExist(int personId, Date date) {

        //"SELECT * FROM _step_info WHERE _date = ? AND _person_id = ?"
        Cursor cursor = mDB.rawQuery("SELECT * FROM " + Constant.TABLE_STEP_INFO
                + " WHERE " + Constant.STEP_INFO_COLUMN_DATE + " = ? AND "
                + Constant.STEP_INFO_COLUMN_PERSON_ID + " = ?", new String[]{date.toString(), "" + personId});

        //如果不存在
        if (!cursor.moveToFirst()) {
            cursor.close();
            return false;
        }

        cursor.close();
        return true;
    }
}
