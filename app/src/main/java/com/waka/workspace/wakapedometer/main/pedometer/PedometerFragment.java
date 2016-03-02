package com.waka.workspace.wakapedometer.main.pedometer;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.waka.workspace.wakapedometer.R;
import com.waka.workspace.wakapedometer.customview.RoundProgressBar;

import java.util.Observable;
import java.util.Observer;

import javax.security.auth.Subject;

/**
 * PedometerFragment
 * <p/>
 * 观察者，需要根据步数变化动态更新UI
 * <p/>
 * Created by waka on 2016/2/16.
 */
public class PedometerFragment extends Fragment implements View.OnClickListener, Observer {

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

    /**
     * initView
     *
     * @param view
     */
    private void initView(View view) {
        roundProgressBar = (RoundProgressBar) view.findViewById(R.id.roundProgressBar);
        btnAdd = (Button) view.findViewById(R.id.btnAdd);
    }

    /**
     * initData
     */
    private void initData() {

    }

    /**
     * initEvent
     */
    private void initEvent() {
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ObjectAnimator animator = ObjectAnimator.ofInt(roundProgressBar, "progress", roundProgressBar.getProgress(), roundProgressBar.getProgress() + 500);
                animator.setDuration(500);
                animator.start();

            }
        });
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
     * onResume
     */
    @Override
    public void onResume() {
        super.onResume();

        //观察者往被观察者中添加订阅事件
        StepObservable.getInstance().addObserver(this);
    }

    /**
     * onPause
     */
    @Override
    public void onPause() {
        super.onPause();

        //观察者从被观察者队列中移除
        StepObservable.getInstance().deleteObserver(this);
    }

    /**
     * 当被观察者发生数据更新时触发
     *
     * @param observable
     * @param data
     */
    @Override
    public void update(Observable observable, Object data) {

        int step = (int) data;

        ObjectAnimator animator = ObjectAnimator.ofInt(roundProgressBar, "progress", roundProgressBar.getProgress(), step);
        animator.setDuration(500);
        animator.start();

        Log.i(TAG, "step=" + step);
    }
}
