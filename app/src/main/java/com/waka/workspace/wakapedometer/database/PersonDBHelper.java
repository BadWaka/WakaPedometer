package com.waka.workspace.wakapedometer.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.waka.workspace.wakapedometer.Constant;
import com.waka.workspace.wakapedometer.database.bean.PersonBean;

/**
 * 专门对人员表进行增删改查的辅助类
 * Created by waka on 2016/2/3.
 */
public class PersonDBHelper {

    private static final String TAG = "Pedometer PersonDBHelper";

    private SQLiteDatabase mDB;

    /**
     * 构造方法，须传入db
     *
     * @param db
     */
    public PersonDBHelper(SQLiteDatabase db) {
        mDB = db;
    }

    /**
     * 增（简版，注册时使用）
     *
     * @param account
     * @param password
     * @return 插入成功return true; 插入失败return false
     */
    public boolean insert(String account, String password, String nickname) {

        //如果账户已存在
        if (isExistAccount(account)) {
            return false;
        }

        ContentValues values = new ContentValues();
        values.put(Constant.PERSON_COLUMN_ACCOUNT, account);
        values.put(Constant.PERSON_COLUMN_PASSWORD, password);
        values.put(Constant.PERSON_COLUMN_NICK_NAME, nickname);
        mDB.insert(Constant.TABLE_PERSON, null, values);
        return true;
    }

    /**
     * 更新headIconUrl
     *
     * @param id
     * @param headIconUrl
     * @return
     */
    public boolean updateHeadIconUrl(int id, String headIconUrl) {

        //如果id不存在
        if (null == queryById(id)) {
            return false;
        }

        //存在则更新
        //UPDATE _person SET _headicon_url = ? WHERE _id = ?
        mDB.execSQL("UPDATE " + Constant.TABLE_PERSON
                        + " SET " + Constant.PERSON_COLUMN_HEADICON_URL + " = ?"
                        + " WHERE " + Constant.PERSON_COLUMN_ID + " = ?",
                new String[]{headIconUrl, "" + id});

        return true;

    }

    /**
     * 根据账号查询人员详细信息
     *
     * @param account
     * @return
     */
    public PersonBean queryByAccount(String account) {

        PersonBean personBean = new PersonBean();

        // SQL语句： select * from _person where _account = '1456683844@qq.com'
        Cursor cursor = mDB.rawQuery("select * from " + Constant.TABLE_PERSON
                + " where " + Constant.PERSON_COLUMN_ACCOUNT + " = ?", new String[]{account});

        if (!cursor.moveToFirst()) {
            cursor.close();
            return null;
        }

        int idDB = cursor.getInt(cursor.getColumnIndex(Constant.PERSON_COLUMN_ID));
        String nameDB = cursor.getString(cursor.getColumnIndex(Constant.PERSON_COLUMN_NAME));
        String nicknameDB = cursor.getString(cursor.getColumnIndex(Constant.PERSON_COLUMN_NICK_NAME));
        int sexDB = cursor.getInt(cursor.getColumnIndex(Constant.PERSON_COLUMN_SEX));
        int ageDB = cursor.getInt(cursor.getColumnIndex(Constant.PERSON_COLUMN_AGE));
        float heightDB = cursor.getFloat(cursor.getColumnIndex(Constant.PERSON_COLUMN_HEIGHT));
        float weightDB = cursor.getFloat(cursor.getColumnIndex(Constant.PERSON_COLUMN_WEIGHT));
        String accountDB = cursor.getString(cursor.getColumnIndex(Constant.PERSON_COLUMN_ACCOUNT));
        String passwordDB = cursor.getString(cursor.getColumnIndex(Constant.PERSON_COLUMN_PASSWORD));
        String headIconUrl = cursor.getString(cursor.getColumnIndex(Constant.PERSON_COLUMN_HEADICON_URL));

        cursor.close();

        personBean.setId(idDB);
        personBean.setName(nameDB);
        personBean.setNickName(nicknameDB);
        personBean.setSex(sexDB);
        personBean.setAge(ageDB);
        personBean.setHeight(heightDB);
        personBean.setWeight(weightDB);
        personBean.setAccount(accountDB);
        personBean.setPassword(passwordDB);
        personBean.setHeadIconUrl(headIconUrl);

        return personBean;
    }

    /**
     * 根据id查询人员详细信息
     *
     * @param id
     * @return
     */
    public PersonBean queryById(int id) {

        // SQL语句： select * from _person where _account = '1456683844@qq.com'
        Cursor cursor = mDB.rawQuery("select * from " + Constant.TABLE_PERSON
                + " where " + Constant.PERSON_COLUMN_ID + " = ?", new String[]{"" + id});

        if (!cursor.moveToFirst()) {
            cursor.close();
            return null;
        }

        int idDB = cursor.getInt(cursor.getColumnIndex(Constant.PERSON_COLUMN_ID));
        String nameDB = cursor.getString(cursor.getColumnIndex(Constant.PERSON_COLUMN_NAME));
        String nicknameDB = cursor.getString(cursor.getColumnIndex(Constant.PERSON_COLUMN_NICK_NAME));
        int sexDB = cursor.getInt(cursor.getColumnIndex(Constant.PERSON_COLUMN_SEX));
        int ageDB = cursor.getInt(cursor.getColumnIndex(Constant.PERSON_COLUMN_AGE));
        float heightDB = cursor.getFloat(cursor.getColumnIndex(Constant.PERSON_COLUMN_HEIGHT));
        float weightDB = cursor.getFloat(cursor.getColumnIndex(Constant.PERSON_COLUMN_WEIGHT));
        String accountDB = cursor.getString(cursor.getColumnIndex(Constant.PERSON_COLUMN_ACCOUNT));
        String passwordDB = cursor.getString(cursor.getColumnIndex(Constant.PERSON_COLUMN_PASSWORD));
        String headIconUrl = cursor.getString(cursor.getColumnIndex(Constant.PERSON_COLUMN_HEADICON_URL));

        cursor.close();

        PersonBean personBean = new PersonBean();
        personBean.setId(idDB);
        personBean.setName(nameDB);
        personBean.setNickName(nicknameDB);
        personBean.setSex(sexDB);
        personBean.setAge(ageDB);
        personBean.setHeight(heightDB);
        personBean.setWeight(weightDB);
        personBean.setAccount(accountDB);
        personBean.setPassword(passwordDB);
        personBean.setHeadIconUrl(headIconUrl);

        return personBean;
    }

    /**
     * 判断数据库中是否存在该用户名
     *
     * @param account
     * @return 存在返回true；    不存在返回false
     */
    public boolean isExistAccount(String account) {

        // SQL语句： select _account from _person where _account = '1456683844@qq.com'
        Cursor cursor = mDB.rawQuery("select " + Constant.PERSON_COLUMN_ACCOUNT + " from " + Constant.TABLE_PERSON
                + " where " + Constant.PERSON_COLUMN_ACCOUNT + " = ?", new String[]{account});
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
        Cursor cursor = mDB.rawQuery("select " + Constant.PERSON_COLUMN_PASSWORD + " from " + Constant.TABLE_PERSON
                + " where " + Constant.PERSON_COLUMN_ACCOUNT + " = ?", new String[]{account});
        if (!cursor.moveToFirst()) {
            cursor.close();
            return false;
        }
        String passwordDB = cursor.getString(cursor.getColumnIndex(Constant.PERSON_COLUMN_PASSWORD));
        cursor.close();
        if (!password.equals(passwordDB)) {
            return false;
        }
        return true;
    }

}
