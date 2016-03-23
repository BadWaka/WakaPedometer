package com.waka.workspace.wakapedometer.login;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.waka.workspace.wakapedometer.Constant;
import com.waka.workspace.wakapedometer.R;
import com.waka.workspace.wakapedometer.MainActivity;
import com.waka.workspace.wakapedometer.database.PersonDBHelper;
import com.waka.workspace.wakapedometer.utils.LoginInfoUtil;
import com.waka.workspace.wakapedometer.database.bean.PersonBean;
import com.waka.workspace.wakapedometer.database.DBHelper;

/**
 * 注册Activity
 */
public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "SignUpActivity";

    //数据库
    private DBHelper mDBHelper;
    private SQLiteDatabase mDB;
    private PersonDBHelper mPersonDBHelper;

    private EditText etAccount, etPassword, etPasswordAgain, etNickName;
    private Button btnSignUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        initView();
        initData();
        initEvent();
    }

    private void initView() {
        etAccount = (EditText) findViewById(R.id.etAccount);
        etPassword = (EditText) findViewById(R.id.etPassword);
        etPasswordAgain = (EditText) findViewById(R.id.etPasswordAgain);
        etNickName = (EditText) findViewById(R.id.et_nickname);
        btnSignUp = (Button) findViewById(R.id.btnSignUp);
    }

    private void initData() {

        //初始化数据库
        mDBHelper = new DBHelper(SignUpActivity.this, Constant.DB, null, 1);
        mDB = mDBHelper.getWritableDatabase();
        mPersonDBHelper = new PersonDBHelper(mDB);

    }

    private void initEvent() {

        btnSignUp.setOnClickListener(this);

        //为用户名editText注册焦点改变监听
        etAccount.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                String account = etAccount.getText().toString();
                boolean existFlag = mPersonDBHelper.isExistAccount(account);//判断数据库中是否已存在该用户名
                if (existFlag) {//如果存在，则提示该用户名已存在
                    etAccount.setError(getString(R.string.prompt_account_already_exist_sign_up_activity));
                }
            }
        });

        etPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                if (hasFocus) {
                    etPasswordAgain.setVisibility(View.VISIBLE);
                }
            }
        });

        etPasswordAgain.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                if (hasFocus) {
                    etNickName.setVisibility(View.VISIBLE);
                }
            }
        });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            //注册
            case R.id.btnSignUp:

                String account = etAccount.getText().toString();
                String password = etPassword.getText().toString();
                String passwordAgain = etPasswordAgain.getText().toString();
                String nickname = etNickName.getText().toString();

                if (account.isEmpty()) {//用户名为空
                    etAccount.setError(getString(R.string.prompt_account_not_null_sign_up_activity));
                    etAccount.requestFocus();
                    return;
                }
                if (password.isEmpty()) {//密码为空
                    etPassword.setError(getString(R.string.prompt_password_not_null_sign_up_activity));
                    etPassword.requestFocus();
                    return;
                }
                if (passwordAgain.isEmpty()) {//再次输入密码为空
                    etPasswordAgain.setError(getString(R.string.prompt_password_not_null_sign_up_activity));
                    etPasswordAgain.requestFocus();
                    return;
                }
                if (!password.equals(passwordAgain)) {//两次输入的密码不一致
                    etPasswordAgain.setError(getString(R.string.prompt_passwordagain_not_same_sign_up_activity));
                    etPasswordAgain.requestFocus();
                    return;
                }
                if (nickname.isEmpty()) {//昵称为空
                    etNickName.setError(getString(R.string.prompt_nickname_not_null_sign_up_activity));
                    etNickName.requestFocus();
                    return;
                }

                //添加进数据库中
                boolean addFlag = mPersonDBHelper.insert(account, password, nickname);
                if (!addFlag) {//用户名已存在，添加失败
                    etAccount.setError(getString(R.string.prompt_account_already_exist_sign_up_activity));
                    etAccount.requestFocus();
                    return;
                }

                PersonBean personBean = mPersonDBHelper.queryByAccount(account);
                int id = personBean.getId();

                //在SharedPreferences中设置登录Cookie和当前登录人员id
                if (!LoginInfoUtil.setLoginCookieAndId(getApplicationContext(), "我是loginCookie", id)) {
                    Log.e(TAG, "写入SharedPreferences失败");
                    return;
                }

                Intent intentMain = new Intent(SignUpActivity.this, MainActivity.class);
                startActivityForResult(intentMain, Constant.REQUEST_CODE_MAIN_ACTIVITY);
                setResult(RESULT_OK);
                finish();

                break;

            default:
                break;
        }
    }
}
