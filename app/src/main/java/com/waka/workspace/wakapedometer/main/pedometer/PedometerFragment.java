package com.waka.workspace.wakapedometer.main.pedometer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.waka.workspace.wakapedometer.R;
import com.waka.workspace.wakapedometer.customview.RoundProgressBar;

/**
 * Created by waka on 2016/2/16.
 */
public class PedometerFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = "PedometerFragment";

    private RoundProgressBar roundProgressBar;
    private Button btnAdd;

    /**
     * 构造方法
     */
    public PedometerFragment() {

    }

    /**
     * newInstance，可传入数据，推荐用初始化方法
     *
     * @param bundle
     * @return
     */
    public static PedometerFragment newInstance(Bundle bundle) {
        PedometerFragment fragment = new PedometerFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    /**
     * onCreate
     *
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //如果有数据的话，可以取出来
        if (getArguments() != null) {

        }
    }

    /**
     * onCreateView，关联布局,创建View
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Nullable//表示参数可为null
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pedometer_in_activity_main, container, false);
        initView(view);
        initData();
        initEvent();
        return view;
    }

    private void initView(View view) {
        roundProgressBar = (RoundProgressBar) view.findViewById(R.id.roundProgressBar);
        btnAdd = (Button) view.findViewById(R.id.btnAdd);
    }

    private void initData() {

    }

    private void initEvent() {
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                roundProgressBar.setProgress(roundProgressBar.getProgress() + 100);
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            default:
                break;
        }
    }
}
