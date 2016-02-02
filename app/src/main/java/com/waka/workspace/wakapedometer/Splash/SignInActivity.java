package com.waka.workspace.wakapedometer.Splash;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;

import com.waka.workspace.wakapedometer.Constant;
import com.waka.workspace.wakapedometer.R;

/**
 * 登录Activity
 */
public class SignInActivity extends AppCompatActivity implements View.OnClickListener {

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

                break;

            //注册
            case R.id.btnSignUp:
                Intent intent = new Intent(SignInActivity.this, SignUpActivity.class);
                startActivityForResult(intent, Constant.REQUEST_CODE_SIGN_UP_ACTIVITY);
                break;

            default:
                break;
        }
    }
}
