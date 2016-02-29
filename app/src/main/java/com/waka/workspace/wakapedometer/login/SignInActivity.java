package com.waka.workspace.wakapedometer.login;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;

import com.waka.workspace.wakapedometer.Constant;
import com.waka.workspace.wakapedometer.Utils;
import com.waka.workspace.wakapedometer.database.model.PersonModel;
import com.waka.workspace.wakapedometer.main.MainActivity;
import com.waka.workspace.wakapedometer.R;
import com.waka.workspace.wakapedometer.database.DBHelper;
import com.waka.workspace.wakapedometer.database.PersonDB;

/**
 * 登录Activity
 */
public class SignInActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "SignInActivity";

    //数据库
    private DBHelper mDBHelper;
    private SQLiteDatabase mDB;
    private PersonDB mPersonDB;

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
        mPersonDB = new PersonDB(mDB);

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

                //判断账户是否存在
                boolean existFlag = mPersonDB.isExistAccount(account);
                if (!existFlag) {
                    actvAccount.setError(getString(R.string.prompt_account_not_exist_sign_in_activity));
                    actvAccount.requestFocus();
                    return;
                }

                //判断用户名密码是否匹配
                boolean matchFlag = mPersonDB.isMatching(account, password);
                if (!matchFlag) {
                    etPassword.setError(getString(R.string.prompt_password_error_sign_in_activity));
                    etPassword.requestFocus();
                    return;
                }

                PersonModel personModel = mPersonDB.queryByAccount(account);
                int id = personModel.getId();

                //在SharedPreferences中设置登录Cookie和当前登录人员id
                if (!Utils.setLoginCookieAndId(getApplicationContext(), "我是loginCookie", id)) {
                    Log.e(TAG, "写入SharedPreferences失败");
                    return;
                }

                Intent intentMain = new Intent(SignInActivity.this, MainActivity.class);
                startActivityForResult(intentMain, Constant.REQUEST_CODE_MAIN_ACTIVITY);
                setResult(RESULT_OK);
                finish();

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

}
