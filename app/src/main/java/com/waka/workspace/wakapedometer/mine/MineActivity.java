package com.waka.workspace.wakapedometer.mine;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;

import com.waka.workspace.wakapedometer.R;

/**
 * 个人中心
 */
public class MineActivity extends AppCompatActivity {

    //Toolbar
    private Toolbar toolbar;

    /**
     * onCreate
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mine);
        initView();
        initData();
        initEvent();
    }

    /**
     * initView
     */
    private void initView() {

        //toolbar
        toolbar = (Toolbar) findViewById(R.id.toolbar);
    }

    /**
     * initData
     */
    private void initData() {

        //设置toolbar
        toolbar.setTitle(getString(R.string.title_activity_mine));// 标题的文字需在setSupportActionBar之前，不然会无效
        this.setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    /**
     * initEvent
     */
    private void initEvent() {

    }
}
