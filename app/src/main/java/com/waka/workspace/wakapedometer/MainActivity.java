package com.waka.workspace.wakapedometer;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
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
public class MainActivity extends AppCompatActivity implements View.OnClickListener, NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "MainActivity";

    //数据库
    private DBHelper mDBHelper;
    private SQLiteDatabase mDB;
    private PersonDBHelper mPersonDBHelper;

    //人员信息
    private int mId;
    private PersonBean mPersonBean;

    //Toolbar
    private Toolbar toolbar;
    private ActionBarDrawerToggle actionBarDrawerToggle;//工具栏上的侧边栏开关，左上角带感的导航动画

    //侧滑菜单
    private DrawerLayout drawerLayout;//抽屉布局
    private NavigationView navigationView;//导航栏
    private View headerView;//侧边栏头View
    private ImageView imgHeadIcon;//头像
    private TextView tvNickName;//昵称
    private ImageView imgLogout;//登出图标

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

        //Toolbar
        toolbar = (Toolbar) findViewById(R.id.toolbar);

        //侧滑菜单
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.actionbar_drawer_toggle_open_main_activity, R.string.actionbar_drawer_toggle_close_main_activity);
        navigationView = (NavigationView) findViewById(R.id.navigationView);
        headerView = navigationView.getHeaderView(0);
        imgHeadIcon = (ImageView) headerView.findViewById(R.id.img_headicon);
        tvNickName = (TextView) headerView.findViewById(R.id.tvNickName);
        imgLogout = (ImageView) headerView.findViewById(R.id.imgLogout);

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

        //Toolbar
        toolbar.setTitle("");
        this.setSupportActionBar(toolbar);

        //侧滑菜单
        tvNickName.setText(mPersonBean.getNickName());
        navigationView.setItemIconTintList(null);//设置菜单图标回复本来的颜色

    }

    /*initEvent*/
    private void initEvent() {

        //侧滑菜单
        drawerLayout.setDrawerListener(actionBarDrawerToggle);//导航按钮监听
        imgHeadIcon.setOnClickListener(this);
        tvNickName.setOnClickListener(this);

        //侧滑菜单登出监听
        imgLogout.setOnClickListener(this);

        //注册导航栏选择监听事件
        navigationView.setNavigationItemSelectedListener(this);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu_in_activity, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        actionBarDrawerToggle.syncState();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        switch (item.getItemId()) {

            case R.id.action_calendar:
                break;

            case R.id.action_share:
                break;

            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        actionBarDrawerToggle.onConfigurationChanged(newConfig);
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

                Snackbar.make(toolbar, "maxMemory=" + maxMemory + "MB", Snackbar.LENGTH_SHORT).show();

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

    @Override
    /**
     * onActivityResult
     */
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {

            //MineActivity
            case Constant.REQUEST_CODE_MINE_ACTIVITY:

                break;
        }
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
            Snackbar.make(toolbar, R.string.prompt_double_exit_tips_main_activity, Snackbar.LENGTH_SHORT).setAction("delete", new View.OnClickListener() {
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
