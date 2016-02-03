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
            + Constant.COLUMN_ID + " integer primary key autoincrement, "
            + Constant.COLUMN_NAME + " text, "
            + Constant.COLUMN_SEX + " integer, "
            + Constant.COLUMN_AGE + " integer, "
            + Constant.COLUMN_HEIGHT + " real, "
            + Constant.COLUMN_WEIGHT + " real, "
            + Constant.COLUMN_ACCOUNT + " text unique, "       //account账户不可重复
            + Constant.COLUMN_PASSWORD + " text)";

    public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        Log.i(TAG, "CREATE_TABLE_PERSON---->" + CREATE_TABLE_PERSON);
        db.execSQL(CREATE_TABLE_PERSON);//创建人员表
        Toast.makeText(mContext, "数据库创建成功", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
