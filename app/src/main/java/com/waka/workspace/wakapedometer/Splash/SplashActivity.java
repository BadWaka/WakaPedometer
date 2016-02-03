package com.waka.workspace.wakapedometer.splash;

import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.waka.workspace.wakapedometer.Constant;
import com.waka.workspace.wakapedometer.R;
import com.waka.workspace.wakapedometer.database.DBHelper;
import com.waka.workspace.wakapedometer.login.SignInActivity;
import com.waka.workspace.wakapedometer.login.SignUpActivity;

/**
 * 水花界面！等待预加载Activity
 */
public class SplashActivity extends AppCompatActivity implements View.OnClickListener {

    //数据库
    private DBHelper mDBHelper;

    //ViewPager
    private ViewPager viewPager;
    private SplashImagePagerAdapter adapter;
    private int[] mImgIds;//图片id数组

    //登录、注册
    private TextView tvSignIn, tvSignUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        initView();
        initData();
        initEvent();
    }

    private void initView() {
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        tvSignIn = (TextView) findViewById(R.id.tvSignIn);
        tvSignUp = (TextView) findViewById(R.id.tvSignUp);
    }

    private void initData() {

        //初始化数据库
        mDBHelper = new DBHelper(SplashActivity.this, Constant.DB, null, 1);
        mDBHelper.getWritableDatabase();

        //给ViewPager设置Adapter
        mImgIds = new int[]{R.drawable.splash_img3, R.drawable.splash_img2, R.drawable.splash_img1};
        adapter = new SplashImagePagerAdapter(SplashActivity.this, mImgIds);
        viewPager.setAdapter(adapter);

    }

    private void initEvent() {
        tvSignIn.setOnClickListener(this);
        tvSignUp.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            //登录
            case R.id.tvSignIn:
                Intent intentSignIn = new Intent(SplashActivity.this, SignInActivity.class);
                startActivityForResult(intentSignIn, Constant.REQUEST_CODE_SIGN_IN_ACTIVITY);
                break;

            //注册
            case R.id.tvSignUp:
                Intent intentSignUp = new Intent(SplashActivity.this, SignUpActivity.class);
                startActivityForResult(intentSignUp, Constant.REQUEST_CODE_SIGN_UP_ACTIVITY);
                break;

            default:
                break;

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {

            //注册Activity回传
            case Constant.REQUEST_CODE_SIGN_UP_ACTIVITY:
                if (resultCode == RESULT_OK) {
                    finish();
                }
                break;

            default:
                break;
        }
    }
}
