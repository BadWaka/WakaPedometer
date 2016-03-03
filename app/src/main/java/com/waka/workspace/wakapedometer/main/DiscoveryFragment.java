package com.waka.workspace.wakapedometer.main;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.waka.workspace.wakapedometer.R;

/**
 * Created by waka on 2016/2/16.
 */
public class DiscoveryFragment extends Fragment {


    private WebView webView;

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
        View view = inflater.inflate(R.layout.fragment_discovery_in_activity_main, container, false);
        initView(view);
        initData();
        initEvent();
        return view;
    }

    private void initView(View view) {

        webView = (WebView) view.findViewById(R.id.webView);
    }

    private void initData() {

        //webView设置
        webView.getSettings().setJavaScriptEnabled(true);//让webView支持JavaScript脚本
        webView.setWebViewClient(new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {

                view.loadUrl(url);//根据传入的参数再去加载新的网页
                return true;//表示当前webView可以处理打开新网页的请求，不用借助系统浏览器
            }
        });

    }

    private void initEvent() {

    }

    @Override
    public void onResume() {
        super.onResume();

        webView.loadUrl("https://www.baidu.com/");
    }
}
