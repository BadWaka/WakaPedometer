package com.waka.workspace.wakapedometer.main;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.waka.workspace.wakapedometer.R;

/**
 * Created by waka on 2016/2/16.
 */
public class DiscoveryFragment extends Fragment {

    /**
     * 构造方法
     */
    public DiscoveryFragment() {

    }

    /**
     * newInstance，可传入数据，推荐用初始化方法
     *
     * @param bundle
     * @return
     */
    public static DiscoveryFragment newInstance(Bundle bundle) {
        DiscoveryFragment fragment = new DiscoveryFragment();
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
        View view = inflater.inflate(R.layout.fragment_mine_in_activity_main, container, false);
        initView(view);
        initData();
        initEvent();
        return view;
    }

    private void initView(View view) {

    }

    private void initData() {

    }

    private void initEvent() {

    }
}
