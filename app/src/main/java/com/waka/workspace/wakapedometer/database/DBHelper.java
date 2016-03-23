package com.waka.workspace.wakapedometer.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import com.waka.workspace.wakapedometer.Constant;

/**
 * DBHelper
 * Created by waka on 2016/2/3.
 */
public class DBHelper extends SQLiteOpenHelper {

    private static final String TAG = "Pedometer DBHelper";

    private Context mContext;

    //创建人员表
    private static final String CREATE_TABLE_PERSON = "create table " + Constant.TABLE_PERSON + " ("
            + Constant.PERSON_COLUMN_ID + " integer primary key autoincrement, "
            + Constant.PERSON_COLUMN_NICK_NAME + " text, "
            + Constant.PERSON_COLUMN_NAME + " text, "
            + Constant.PERSON_COLUMN_SEX + " integer, "
            + Constant.PERSON_COLUMN_AGE + " integer, "
            + Constant.PERSON_COLUMN_HEIGHT + " real, "
            + Constant.PERSON_COLUMN_WEIGHT + " real, "
            + Constant.PERSON_COLUMN_ACCOUNT + " text unique, "       //account账户不可重复
            + Constant.PERSON_COLUMN_PASSWORD + " text, "
            + Constant.PERSON_COLUMN_HEADICON_URL + " text)";

    //创建步数信息表
    private static final String CREATE_TABLE_STEP_INFO = "create table " + Constant.TABLE_STEP_INFO + " ("
            + Constant.STEP_INFO_COLUMN_PERSON_ID + " integer, "
            + Constant.STEP_INFO_COLUMN_STEP + " integer, "
            + Constant.STEP_INFO_COLUMN_DATE + " text)";

    /**
     * 构造方法
     *
     * @param context
     * @param name
     * @param factory
     * @param version
     */
    public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        Log.i(TAG, "CREATE_TABLE_PERSON---->" + CREATE_TABLE_PERSON);
        db.execSQL(CREATE_TABLE_PERSON);//创建人员表
        Toast.makeText(mContext, "人员表创建成功", Toast.LENGTH_SHORT).show();

        Log.i(TAG, "CREATE_TABLE_STEP_INFO---->" + CREATE_TABLE_STEP_INFO);
        db.execSQL(CREATE_TABLE_STEP_INFO);//创建步数信息表
        Toast.makeText(mContext, "步数信息表创建成功", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
