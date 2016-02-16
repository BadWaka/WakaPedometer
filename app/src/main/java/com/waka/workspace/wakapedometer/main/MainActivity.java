package com.waka.workspace.wakapedometer.main;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.waka.workspace.wakapedometer.MyFragmentPagerAdapter;
import com.waka.workspace.wakapedometer.R;
import com.waka.workspace.wakapedometer.main.pedometer.PedometerFragment;
import com.waka.workspace.wakapedometer.main.pedometer.PedometerService;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, ViewPager.OnPageChangeListener {

    private static final String TAG = "Pedometer MainActivity";

    //viewPager和Fragment
    private ViewPager viewPager;
    private ArrayList<Fragment> fragmentArrayList;
    private MyFragmentPagerAdapter fragmentPagerAdapter;
    private PedometerFragment pedometerFragment;
    private HistoryFragment historyFragment;
    private MineFragment mineFragment;

    /**
     * onCreate
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initData();
        initEvent();
    }

    /*initView*/
    private void initView() {

        viewPager = (ViewPager) findViewById(R.id.viewPager);
    }

    /*initData*/
    private void initData() {

        //viewPager和fragment
        pedometerFragment = PedometerFragment.newInstance(null);
        historyFragment = HistoryFragment.newInstance(null);
        mineFragment = MineFragment.newInstance(null);
        fragmentArrayList = new ArrayList<>();
        fragmentArrayList.add(pedometerFragment);
        fragmentArrayList.add(historyFragment);
        fragmentArrayList.add(mineFragment);
        fragmentPagerAdapter = new MyFragmentPagerAdapter(getSupportFragmentManager(), fragmentArrayList);
        viewPager.setAdapter(fragmentPagerAdapter);
        viewPager.setCurrentItem(0);
    }

    /*initEvent*/
    private void initEvent() {

        //设置翻页监听
        viewPager.addOnPageChangeListener(this);
    }

    /**
     * onClick
     *
     * @param v
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            default:
                break;
        }
    }

    /**
     * 翻页监听
     *
     * @param position
     * @param positionOffset
     * @param positionOffsetPixels
     */
    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    /*翻页监听*/
    @Override
    public void onPageSelected(int position) {

    }

    /*翻页监听*/
    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
