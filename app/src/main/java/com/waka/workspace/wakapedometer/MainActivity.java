package com.waka.workspace.wakapedometer;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnticipateOvershootInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.waka.workspace.wakapedometer.database.PersonDBHelper;
import com.waka.workspace.wakapedometer.pedometer.PedometerFragment;
import com.waka.workspace.wakapedometer.pedometer.PedometerService;
import com.waka.workspace.wakapedometer.utils.DeviceInfoUtil;
import com.waka.workspace.wakapedometer.utils.LoginInfoUtil;
import com.waka.workspace.wakapedometer.database.DBHelper;
import com.waka.workspace.wakapedometer.database.bean.PersonBean;
import com.waka.workspace.wakapedometer.splash.SplashActivity;

import java.util.ArrayList;

/**
 * 主界面
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener, ViewPager.OnPageChangeListener, NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "MainActivity";

    //数据库
    private DBHelper mDBHelper;
    private SQLiteDatabase mDB;
    private PersonDBHelper mPersonDBHelper;

    //人员信息
    private int mId;
    private PersonBean mPersonBean;

    //侧滑菜单
    private DrawerLayout drawerLayout;//抽屉布局
    private NavigationView navigationView;//导航栏
    private View headerView;//侧边栏头View
    private ImageView imgHeadIcon;//头像
    private TextView tvNickName;//昵称
    private ImageView imgLogout;//登出图标

    //viewPager和Fragment
    private ViewPager viewPager;
    private ArrayList<Fragment> fragmentArrayList;
    private MyFragmentPagerAdapter fragmentPagerAdapter;
    private PedometerFragment pedometerFragment;
    private HistoryFragment historyFragment;
    private DiscoveryFragment discoveryFragment;

    //底部按钮栏
    private LinearLayout layoutBottomTab;
    private LinearLayout layoutPedometer, layoutHistory, layoutDiscovery;
    private ImageView imgPedometer, imgHistory, imgDiscovery;
    private TextView tvPedometer, tvHistory, tvDiscovery;

    //双击退出
    private long mExitTime = 0;//退出时间，用来实现双击退出功能

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

        //侧滑菜单
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.navigationView);
        headerView = navigationView.getHeaderView(0);
        imgHeadIcon = (ImageView) headerView.findViewById(R.id.img_headicon);
        tvNickName = (TextView) headerView.findViewById(R.id.tvNickName);
        imgLogout = (ImageView) headerView.findViewById(R.id.imgLogout);

        //viewPager和Fragment
        viewPager = (ViewPager) findViewById(R.id.viewPager);

        //底部按钮栏
        layoutBottomTab = (LinearLayout) findViewById(R.id.layoutBottomTab);
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

        //初始化数据库
        mDBHelper = new DBHelper(MainActivity.this, Constant.DB, null, 1);
        mDB = mDBHelper.getWritableDatabase();
        mPersonDBHelper = new PersonDBHelper(mDB);

        //得到人员信息
        mId = LoginInfoUtil.getCurrentLoginId(getApplicationContext());
        mPersonBean = mPersonDBHelper.queryById(mId);

        //开启计步服务
        Intent intentStart = new Intent(MainActivity.this, PedometerService.class);
        startService(intentStart);

        //侧边栏
        tvNickName.setText(mPersonBean.getNickName());

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

        //侧滑菜单头像和昵称监听
        imgHeadIcon.setOnClickListener(this);
        tvNickName.setOnClickListener(this);

        //侧滑菜单登出监听
        imgLogout.setOnClickListener(this);

        //注册导航栏选择监听事件
        navigationView.setNavigationItemSelectedListener(this);

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

            //侧滑菜单头像监听
            case R.id.img_headicon:

                //如果侧边栏是开启状态
                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    //关闭侧边栏
                    drawerLayout.closeDrawer(GravityCompat.START);
                }

                Intent intentMine1 = new Intent(MainActivity.this, MineActivity.class);
                startActivityForResult(intentMine1, Constant.REQUEST_CODE_MINE_ACTIVITY);

                break;

            //侧滑菜单昵称监听
            case R.id.tvNickName:

                //如果侧边栏是开启状态
                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    //关闭侧边栏
                    drawerLayout.closeDrawer(GravityCompat.START);
                }

                Intent intentMine2 = new Intent(MainActivity.this, MineActivity.class);
                startActivityForResult(intentMine2, Constant.REQUEST_CODE_MINE_ACTIVITY);

                break;

            //侧滑菜单登出
            case R.id.imgLogout:

                new AlertDialog.Builder(MainActivity.this).setMessage(R.string.prompt_confirm_logout_main_activity).setPositiveButton(R.string.btn_confirm_main_activity, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        //将LoginCookie设为""，当前登录人员id设为-1
                        LoginInfoUtil.setLoginCookieAndId(MainActivity.this, "", -1);

                        //跳转到SplashActivity
                        Intent intent = new Intent(MainActivity.this, SplashActivity.class);
                        startActivity(intent);

                        //关闭计步服务
                        Intent intentStop = new Intent(MainActivity.this, PedometerService.class);
                        stopService(intentStop);

                        finish();

                    }
                }).setNegativeButton(R.string.btn_cancel_main_activity, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).show();

                break;

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

    /**
     * 导航栏选择监听
     *
     * @param item
     * @return
     */
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch (id) {

            //开启计步服务
            case R.id.nav_start_service:

                Intent intentStart = new Intent(MainActivity.this, PedometerService.class);
                startService(intentStart);

                break;

            //停止计步服务
            case R.id.nav_stop_service:

                Intent intentStop = new Intent(MainActivity.this, PedometerService.class);
                stopService(intentStop);

                break;

            //得到应用程序最大内存
            case R.id.nav_get_max_memory:

                int maxMemory = DeviceInfoUtil.getMaxMemory();

                Snackbar.make(layoutBottomTab, "maxMemory=" + maxMemory + "MB", Snackbar.LENGTH_SHORT).show();

                break;

            default:
                break;
        }

        //如果侧边栏是开启状态
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            //关闭侧边栏
            drawerLayout.closeDrawer(GravityCompat.START);
        }

        return true;
    }

    /**
     * 重写返回键
     */
    @Override
    public void onBackPressed() {

        //如果侧边栏是开启状态
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            //关闭侧边栏
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            doubleClickExit();
        }
    }

    /**
     * 双击退出
     */
    private void doubleClickExit() {

        Log.i(TAG, "doubleClickExit---->" + mExitTime);

        //如果时间间隔大于2秒
        if ((System.currentTimeMillis() - mExitTime) > 2000) {
            Snackbar.make(viewPager, R.string.prompt_double_exit_tips_main_activity, Snackbar.LENGTH_SHORT).setAction("delete", new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            }).show();
            mExitTime = System.currentTimeMillis();
        } else {
            finish();
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);//淡入淡出
        }
    }
}
