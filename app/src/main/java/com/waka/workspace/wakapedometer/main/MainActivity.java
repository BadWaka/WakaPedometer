package com.waka.workspace.wakapedometer.main;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AnticipateOvershootInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.waka.workspace.wakapedometer.MyFragmentPagerAdapter;
import com.waka.workspace.wakapedometer.R;
import com.waka.workspace.wakapedometer.main.pedometer.PedometerFragment;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, ViewPager.OnPageChangeListener {

    private static final String TAG = "Pedometer MainActivity";

    //viewPager和Fragment
    private ViewPager viewPager;
    private ArrayList<Fragment> fragmentArrayList;
    private MyFragmentPagerAdapter fragmentPagerAdapter;
    private PedometerFragment pedometerFragment;
    private HistoryFragment historyFragment;
    private DiscoveryFragment discoveryFragment;

    //底部按钮栏
    private LinearLayout layoutPedometer, layoutHistory, layoutDiscovery;
    private ImageView imgPedometer, imgHistory, imgDiscovery;
    private TextView tvPedometer, tvHistory, tvDiscovery;

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

        //viewPager和Fragment
        viewPager = (ViewPager) findViewById(R.id.viewPager);

        //底部按钮栏
        layoutPedometer = (LinearLayout) findViewById(R.id.layoutPedometer);
        layoutHistory = (LinearLayout) findViewById(R.id.layoutHistory);
        layoutDiscovery = (LinearLayout) findViewById(R.id.layoutDiscovery);

        imgPedometer = (ImageView) findViewById(R.id.imgPedometer);
        imgHistory = (ImageView) findViewById(R.id.imgHistory);
        imgDiscovery = (ImageView) findViewById(R.id.imgDiscovery);

        tvPedometer = (TextView) findViewById(R.id.tvPedometer);
        tvHistory = (TextView) findViewById(R.id.tvHistory);
        tvDiscovery = (TextView) findViewById(R.id.tvDiscovery);
    }

    /*initData*/
    private void initData() {

        //viewPager和fragment
        pedometerFragment = PedometerFragment.newInstance(null);
        historyFragment = HistoryFragment.newInstance(null);
        discoveryFragment = DiscoveryFragment.newInstance(null);
        fragmentArrayList = new ArrayList<>();
        fragmentArrayList.add(pedometerFragment);
        fragmentArrayList.add(historyFragment);
        fragmentArrayList.add(discoveryFragment);
        fragmentPagerAdapter = new MyFragmentPagerAdapter(getSupportFragmentManager(), fragmentArrayList);
        viewPager.setAdapter(fragmentPagerAdapter);
        viewPager.setCurrentItem(0);
        tvPedometer.setTextColor(ContextCompat.getColor(MainActivity.this, R.color.colorAccent));
        imgPedometer.setColorFilter(ContextCompat.getColor(MainActivity.this, R.color.colorAccent));
    }

    /*initEvent*/
    private void initEvent() {

        //设置翻页监听
        viewPager.addOnPageChangeListener(this);

        //底部按钮栏点击事件
        layoutPedometer.setOnClickListener(this);
        layoutHistory.setOnClickListener(this);
        layoutDiscovery.setOnClickListener(this);
    }

    /**
     * onClick
     *
     * @param v
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.layoutPedometer:
                viewPager.setCurrentItem(0);
                break;

            case R.id.layoutHistory:
                viewPager.setCurrentItem(1);
                break;

            case R.id.layoutDiscovery:
                viewPager.setCurrentItem(2);
                break;

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
        setTabColor(position);
    }

    /*翻页监听*/
    @Override
    public void onPageScrollStateChanged(int state) {

    }

    /**
     * 设置底部标签的颜色和动画
     *
     * @param tabPosition
     */
    private void setTabColor(int tabPosition) {
        Animator animator = AnimatorInflater.loadAnimator(MainActivity.this, R.animator.anim_rotation_circle);
        switch (tabPosition) {
            case 0:
                //设置旋转动画
                animator.setTarget(imgPedometer);
                animator.setInterpolator(new AnticipateOvershootInterpolator());
                animator.start();
                //设置文字颜色
                tvPedometer.setTextColor(ContextCompat.getColor(MainActivity.this, R.color.colorAccent));
                tvHistory.setTextColor(Color.WHITE);
                tvDiscovery.setTextColor(Color.WHITE);
                //设置图片颜色
                imgPedometer.setColorFilter(ContextCompat.getColor(MainActivity.this, R.color.colorAccent));
                imgHistory.setColorFilter(Color.WHITE);
                imgDiscovery.setColorFilter(Color.WHITE);
                break;
            case 1:
                //设置旋转动画
                animator.setTarget(imgHistory);
                animator.setInterpolator(new AnticipateOvershootInterpolator());
                animator.start();
                //设置文字颜色
                tvPedometer.setTextColor(Color.WHITE);
                tvHistory.setTextColor(ContextCompat.getColor(MainActivity.this, R.color.colorAccent));
                tvDiscovery.setTextColor(Color.WHITE);
                //设置图片颜色
                imgPedometer.setColorFilter(Color.WHITE);
                imgHistory.setColorFilter(ContextCompat.getColor(MainActivity.this, R.color.colorAccent));
                imgDiscovery.setColorFilter(Color.WHITE);
                break;
            case 2:
                //设置旋转动画
                animator.setTarget(imgDiscovery);
                animator.setInterpolator(new AnticipateOvershootInterpolator());
                animator.start();
                //设置文字颜色
                tvPedometer.setTextColor(Color.WHITE);
                tvHistory.setTextColor(Color.WHITE);
                tvDiscovery.setTextColor(ContextCompat.getColor(MainActivity.this, R.color.colorAccent));
                //设置图片颜色
                imgPedometer.setColorFilter(Color.WHITE);
                imgHistory.setColorFilter(Color.WHITE);
                imgDiscovery.setColorFilter(ContextCompat.getColor(MainActivity.this, R.color.colorAccent));
                break;
            default:
                break;
        }
    }
}
