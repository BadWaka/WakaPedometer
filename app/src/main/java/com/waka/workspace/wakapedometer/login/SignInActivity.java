package com.waka.workspace.wakapedometer.login;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;

import com.waka.workspace.wakapedometer.Constant;
import com.waka.workspace.wakapedometer.R;
import com.waka.workspace.wakapedometer.database.DBHelper;
import com.waka.workspace.wakapedometer.database.PersonDBHelper;

/**
 * 登录Activity
 */
public class SignInActivity extends AppCompatActivity implements View.OnClickListener {

    //数据库
    private DBHelper mDBHelper;
    private SQLiteDatabase mDB;
    private PersonDBHelper mPersonDBHelper;

    //控件
    private AutoCompleteTextView actvAccount;
    private EditText etPassword;
    private Button btnSignIn, btnSignUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        initView();
        initData();
        initEvent();
    }

    private void initView() {

        actvAccount = (AutoCompleteTextView) findViewById(R.id.actvAccount);
        etPassword = (EditText) findViewById(R.id.etPassword);
        btnSignIn = (Button) findViewById(R.id.btnSignIn);
        btnSignUp = (Button) findViewById(R.id.btnSignUp);

    }

    private void initData() {

        //初始化数据库
        mDBHelper = new DBHelper(SignInActivity.this, Constant.DB, null, 1);
        mDB = mDBHelper.getWritableDatabase();
        mPersonDBHelper = new PersonDBHelper(mDB);

    }

    private void initEvent() {

        btnSignIn.setOnClickListener(this);
        btnSignUp.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            //登录
            case R.id.btnSignIn:

                String account = actvAccount.getText().toString();
                String password = etPassword.getText().toString();

                if (account.isEmpty()) {//用户名为空
                    actvAccount.setError(getString(R.string.prompt_account_not_null_sign_up_activity));
                    actvAccount.requestFocus();
                    return;
                }
                if (password.isEmpty()) {//密码为空
                    etPassword.setError(getString(R.string.prompt_password_not_null_sign_up_activity));
                    etPassword.requestFocus();
                    return;
                }


                break;

            //注册
            case R.id.btnSignUp:

                Intent intent = new Intent(SignInActivity.this, SignUpActivity.class);
                startActivityForResult(intent, Constant.REQUEST_CODE_SIGN_UP_ACTIVITY);
                //overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);//淡入淡出
                finish();

                break;

            default:
                break;
        }
    }

    /**
     * 检测用户名与密码是否匹配
     *
     * @param account
     * @param password
     * @return 匹配, return 1;
     * --------不匹配,return 0;
     * --------用户名不存在,return -1;
     */
    private int isAccountMatchingPassword(String account, String password) {

        // SQL语句： select _account,_password from _person where _account = '1456683844@qq.com'
        Cursor cursor = mDB.rawQuery("select " + Constant.COLUMN_ACCOUNT + "," + Constant.COLUMN_PASSWORD + " from " + Constant.TABLE_PERSON
                + " where " + Constant.COLUMN_ACCOUNT + " = ?", new String[]{account});

        //用户名不存在
        if (!cursor.moveToFirst()) {
            cursor.close();
            return -1;
        }
        String passwordDB = cursor.getString(cursor.getColumnIndex(Constant.COLUMN_PASSWORD));
        cursor.close();

        //如果不匹配
        if (!password.equals(passwordDB)) {
            return 0;
        }
        return 1;
    }
}
