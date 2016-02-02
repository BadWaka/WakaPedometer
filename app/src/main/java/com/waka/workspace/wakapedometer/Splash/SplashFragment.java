package com.waka.workspace.wakapedometer.Splash;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.waka.workspace.wakapedometer.R;

/**
 * SplashFragment!
 * Created by waka on 2016/2/2.
 */
public class SplashFragment extends Fragment {

    private static final String TAG = "SplashFragment";

    private ImageView imgBackground;

    private int imgId = -1;
    private String text;

    /**
     * 构造方法
     */
    public SplashFragment() {

    }

    /**
     * newInstance，可传入数据，推荐用初始化方法
     *
     * @param bundle
     * @return
     */
    public static SplashFragment newInstance(Bundle bundle) {
        SplashFragment fragment = new SplashFragment();
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
            Bundle bundle = getArguments();
            imgId = bundle.getInt("imgId");
            text = bundle.getString("text");
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
        View view = inflater.inflate(R.layout.fragment_splash_in_activity_splash, container, false);
        initView(view);
        initData();
        initEvent();
        return view;
    }

    private void initView(View view) {
        imgBackground = (ImageView) view.findViewById(R.id.imgBackground);
    }

    private void initData() {

        if (imgId != -1) {
            imgBackground.setBackgroundResource(imgId);
        }

    }

    private void initEvent() {

    }

}
