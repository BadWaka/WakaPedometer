package com.waka.workspace.wakapedometer.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.waka.workspace.wakapedometer.Constant;

/**
 * 专门对人员表进行增删改查的辅助类
 * Created by waka on 2016/2/3.
 */
public class PersonDB {

    private static final String TAG = "Pedometer PersonDB";

    private SQLiteDatabase mDB;

    /**
     * 构造方法，须传入db
     *
     * @param db
     */
    public PersonDB(SQLiteDatabase db) {
        mDB = db;
    }

    /**
     * 增（简版，注册时使用）
     *
     * @param account
     * @param password
     * @return 插入成功return true; 插入失败return false
     */
    public boolean add(String account, String password) {

        //如果账户已存在
        if (isExistAccount(account)) {
            return false;
        }

        ContentValues values = new ContentValues();
        values.put(Constant.COLUMN_ACCOUNT, account);
        values.put(Constant.COLUMN_PASSWORD, password);
        mDB.insert(Constant.TABLE_PERSON, null, values);
        return true;
    }

    /**
     * 判断数据库中是否存在该用户名
     *
     * @param account
     * @return 存在返回true；    不存在返回false
     */
    public boolean isExistAccount(String account) {

        // SQL语句： select _account from _person where _account = '1456683844@qq.com'
        Cursor cursor = mDB.rawQuery("select " + Constant.COLUMN_ACCOUNT + " from " + Constant.TABLE_PERSON
                + " where " + Constant.COLUMN_ACCOUNT + " = ?", new String[]{account});
        if (!cursor.moveToFirst()) {
            cursor.close();
            return false;
        }
        cursor.close();
        return true;
    }

    /**
     * 判断账号密码是否匹配
     *
     * @param account
     * @param password
     * @return 匹配返回true；    不匹配返回false
     */
    public boolean isMatching(String account, String password) {

        // SQL语句： select _password from _person where _account = '1456683844@qq.com'
        Cursor cursor = mDB.rawQuery("select " + Constant.COLUMN_PASSWORD + " from " + Constant.TABLE_PERSON
                + " where " + Constant.COLUMN_ACCOUNT + " = ?", new String[]{account});
        if (!cursor.moveToFirst()) {
            cursor.close();
            return false;
        }
        String passwordDB = cursor.getString(cursor.getColumnIndex(Constant.COLUMN_PASSWORD));
        cursor.close();
        if (!password.equals(passwordDB)) {
            return false;
        }
        return true;
    }

}
