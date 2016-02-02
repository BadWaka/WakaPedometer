package com.waka.workspace.wakapedometer.Splash;

import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.waka.workspace.wakapedometer.Adapter.MyFragmentPagerAdapter;
import com.waka.workspace.wakapedometer.R;

import java.util.ArrayList;

/**
 * 水花界面！等待预加载Activity
 */
public class SplashActivity extends AppCompatActivity {

    //主体Fragment和ViewPager
    private ViewPager mViewPager;
    private ArrayList<Fragment> mFragmentList;
    private SplashFragment mFragment1, mFragment2, mFragment3;
    private MyFragmentPagerAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        initView();
        initData();
        initEvent();
    }

    private void initView() {
        mViewPager = (ViewPager) findViewById(R.id.viewPager);
    }

    private void initData() {

        //初始化ViewPager和Fragment
        mFragment1 = SplashFragment.newInstance(initFragmentData(1, ""));
        mFragment2 = SplashFragment.newInstance(initFragmentData(2, ""));
        mFragment3 = SplashFragment.newInstance(initFragmentData(3, ""));

        mFragmentList = new ArrayList<>();
        mFragmentList.add(mFragment1);
        mFragmentList.add(mFragment2);
        mFragmentList.add(mFragment3);

        mAdapter = new MyFragmentPagerAdapter(getSupportFragmentManager(), mFragmentList);

        mViewPager.setAdapter(mAdapter);
        mViewPager.setCurrentItem(0);

    }

    private void initEvent() {

    }

    /**
     * 初始化Fragment数据
     *
     * @param imgId 背景图片id
     * @param text  文本
     * @return
     */
    private Bundle initFragmentData(int imgId, String text) {
        Bundle bundle = new Bundle();
        bundle.putInt("imgId", imgId);
        bundle.putString("text", text);
        return bundle;
    }
}
