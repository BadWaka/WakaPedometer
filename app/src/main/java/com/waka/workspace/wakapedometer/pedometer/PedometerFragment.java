package com.waka.workspace.wakapedometer.pedometer;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.waka.workspace.wakapedometer.Constant;
import com.waka.workspace.wakapedometer.R;
import com.waka.workspace.wakapedometer.database.StepInfoDBHelper;
import com.waka.workspace.wakapedometer.utils.DensityUtil;
import com.waka.workspace.wakapedometer.utils.LoginInfoUtil;
import com.waka.workspace.wakapedometer.customview.RoundProgressBar;
import com.waka.workspace.wakapedometer.database.DBHelper;
import com.waka.workspace.wakapedometer.weather.GetWeatherInfoRequest;
import com.waka.workspace.wakapedometer.weather.WeatherActivity;
import com.waka.workspace.wakapedometer.weather.WeatherBean;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.sql.Date;
import java.util.Observable;
import java.util.Observer;

/**
 * PedometerFragment
 * <p/>
 * 观察者，需要根据步数变化动态更新UI
 * <p/>
 * Created by waka on 2016/2/16.
 */
public class PedometerFragment extends Fragment implements View.OnClickListener, Observer {

    private static final String TAG = "PedometerFragment";

    //数据库操作类
    private int mId;//当前用户id
    private DBHelper mDBHelper;
    private SQLiteDatabase mDB;
    private StepInfoDBHelper mStepInfoDBHelper;

    //Volley网络变量
    private RequestQueue mRequestQueue;//Volley请求队列

    //圆形进度条栏，用来显示步数
    private CardView cardViewProgressBar;//最外层cardView
    private LinearLayout layoutProgressBar;//线性布局
    private RoundProgressBar roundProgressBar;//自定义圆形进度条
    private EditText etMaxStep;//设置最大步数编辑框，默认隐藏
    private FloatingActionButton fabIsShowEtMaxStep;//是否显示最大步数编辑框的FAB
    private int layoutHeightOriginal;//布局原来的高度
    private static final int LAYOUT_HEIGHT_OFFSET = 400;//布局高度偏移量
    private static final int FAB_UP = 1;//FAB指向上
    private static final int FAB_DOWN = 2;//FAB指向下

    //额外步数信息
    private TextView tvCirclePlace;//绕XX多少多少圈的地名
    private TextView tvCircleCounter;//绕XX多少多少圈的圈数
    private TextView tvDistance;//距离
    private TextView tvCalories;//热量
    private TextView tvActiveTime;//活跃时间

    //天气
    private CardView cvWeather;//天气CardView
    private ImageView imgWeather;//小图标
    private TextView tvTemperature;//温度
    private TextView tvAirQuality;//空气质量系数
    private TextView tvPollutionLevel;//污染程度
    private JSONObject weatherInfoJSON;//天气信息，JSON
    private WeatherBean weatherBean;//天气信息，JavaBean

    //地理位置
    private ImageView imgLocation;//小图标
    private TextView tvLocation;//污染程度

    //Handler What
    private static final int WHAT_WEATHER_INFO_FAIL = 0;//获取天气信息失败
    private static final int WHAT_WEATHER_INFO_SUCCESS = 1;//获取天气信息成功

    // Handler
    private MyHandler mHandler = new MyHandler(this);

    /**
     * 静态Handler内部类，避免内存泄漏
     *
     * @author waka
     */
    private static class MyHandler extends Handler {

        // 对Handler持有的对象使用弱引用
        private WeakReference<PedometerFragment> wrPedometerFragment;

        public MyHandler(PedometerFragment pedometerFragment) {
            wrPedometerFragment = new WeakReference<PedometerFragment>(pedometerFragment);
        }

        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {

                //获取天气信息失败
                case WHAT_WEATHER_INFO_FAIL:

                    VolleyError volleyError = (VolleyError) msg.obj;
                    Snackbar.make(wrPedometerFragment.get().cvWeather, volleyError.getMessage(), Snackbar.LENGTH_SHORT).show();

                    break;

                //获取天气信息成功
                case WHAT_WEATHER_INFO_SUCCESS:

                    //获得weatherBean
                    wrPedometerFragment.get().weatherBean = (WeatherBean) msg.obj;

                    //获得详细数据
                    WeatherBean weatherBean = wrPedometerFragment.get().weatherBean;
                    String temperature = weatherBean.getNow().getTmp();//温度
                    String aqi = weatherBean.getAqi().getCity().getAqi();//空气质量指数
                    String quality = weatherBean.getAqi().getCity().getQlty();//空气质量类别

                    //更新UI
                    wrPedometerFragment.get().tvTemperature.setText(temperature);
                    wrPedometerFragment.get().tvAirQuality.setText(aqi);
                    wrPedometerFragment.get().tvPollutionLevel.setText(quality);

                    break;

                default:
                    break;
            }
        }

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

    @Override
    /**
     * onCreate
     */
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

        //圆形进度条栏，用来显示步数
        cardViewProgressBar = (CardView) view.findViewById(R.id.cardview_progressbar);
        layoutProgressBar = (LinearLayout) view.findViewById(R.id.layoutProgressBar);
        roundProgressBar = (RoundProgressBar) view.findViewById(R.id.roundProgressBar);
        etMaxStep = (EditText) view.findViewById(R.id.etMaxStep);
        fabIsShowEtMaxStep = (FloatingActionButton) view.findViewById(R.id.fab_is_show_et_maxstep);

        //额外步数信息
        tvCirclePlace = (TextView) view.findViewById(R.id.tv_circle_place);//绕XX多少多少圈的地名
        tvCircleCounter = (TextView) view.findViewById(R.id.tv_circle_counter);//绕XX多少多少圈的圈数
        tvDistance = (TextView) view.findViewById(R.id.tv_distance);//距离
        tvCalories = (TextView) view.findViewById(R.id.tv_calories);//热量
        tvActiveTime = (TextView) view.findViewById(R.id.tv_active_time);//活跃时间

        //天气
        cvWeather = (CardView) view.findViewById(R.id.cardview_weather);//天气CardView
        imgWeather = (ImageView) view.findViewById(R.id.img_weather);//小图标
        tvTemperature = (TextView) view.findViewById(R.id.tv_temperature);//温度
        tvAirQuality = (TextView) view.findViewById(R.id.tv_air_quality);//空气质量系数
        tvPollutionLevel = (TextView) view.findViewById(R.id.tv_pollution_level);//污染程度

        //地理位置
        imgLocation = (ImageView) view.findViewById(R.id.img_location);//小图标
        tvLocation = (TextView) view.findViewById(R.id.tv_location);//污染程度
    }

    /**
     * initData
     */
    private void initData() {

        //初始化数据库
        mId = LoginInfoUtil.getCurrentLoginId(PedometerFragment.this.getActivity());
        mDBHelper = new DBHelper(PedometerFragment.this.getActivity(), Constant.DB, null, 1);
        mDB = mDBHelper.getWritableDatabase();
        mStepInfoDBHelper = new StepInfoDBHelper(mDB);

        //初始化网络
        mRequestQueue = Volley.newRequestQueue(getActivity());//初始化请求队列

        //圆形进度条栏，用来显示步数
        layoutHeightOriginal = layoutProgressBar.getHeight();//圆形进度条栏，用来显示步数
        fabIsShowEtMaxStep.setTag(FAB_DOWN);//圆形进度条栏，用来显示步数

        //获取天气数据
        GetWeatherInfoRequest getWeatherInfoRequest = GetWeatherInfoRequest.newInstance("beijing",
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(final JSONObject response) {

                        Log.d(TAG, "【initData】【onResponse】response = " + response.toString());

                        weatherInfoJSON = response;

                        //新建线程用于反序列化
                        new Thread() {
                            @Override
                            public void run() {

                                try {

                                    //从原始JSON中用 "HeWeather data service 3.0" 字段取出一个JSONArray
                                    JSONArray jsonArray = null;
                                    jsonArray = response.getJSONArray(WeatherBean.TOTAL_NAME);

                                    //TODO
                                    //一般来说，第一个就是我们要的可以反序列化的JSON数据了
                                    JSONObject jsonWeather = (JSONObject) jsonArray.get(0);

                                    //反序列化
                                    WeatherBean weatherBean = JSON.parseObject(jsonWeather.toString(), WeatherBean.class);

                                    //发送成功消息
                                    Message msg = Message.obtain();
                                    msg.what = WHAT_WEATHER_INFO_SUCCESS;
                                    msg.obj = weatherBean;//把weatherBean放进去
                                    mHandler.sendMessage(msg);

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }.start();

                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {

                        Log.e(TAG, "【initData】【onErrorResponse】error = " + error.getMessage(), error);

                        //发送失败消息
                        Message msg = Message.obtain();
                        msg.what = WHAT_WEATHER_INFO_FAIL;
                        msg.obj = error;
                        mHandler.sendMessage(msg);
                    }
                });
        mRequestQueue.add(getWeatherInfoRequest);
    }

    /**
     * initEvent
     */
    private void initEvent() {

        //FAB点击事件
        fabIsShowEtMaxStep.setOnClickListener(this);

        //天气CardView
        cvWeather.setOnClickListener(this);
    }

    /**
     * onClick
     *
     * @param v
     */
    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            //如果是进度条栏的FAB
            case R.id.fab_is_show_et_maxstep:

                //如果FAB箭头向下
                if ((int) fabIsShowEtMaxStep.getTag() == FAB_DOWN) {

                    //高度变化动画
                    ObjectAnimator heightAnimator = ObjectAnimator.ofInt(layoutProgressBar, "minimumHeight", layoutHeightOriginal, DensityUtil.dip2px(PedometerFragment.this.getActivity(), LAYOUT_HEIGHT_OFFSET));
                    //FAB旋转动画
                    ObjectAnimator rotateAnimator = ObjectAnimator.ofFloat(fabIsShowEtMaxStep, "rotation", 0f, 180f);
                    //组合动画
                    AnimatorSet animatorSet = new AnimatorSet();
                    animatorSet.play(rotateAnimator).with(heightAnimator);
                    animatorSet.setDuration(500);
                    animatorSet.start();
                    animatorSet.addListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {//动画结束时回调
                            super.onAnimationEnd(animation);
                            etMaxStep.setVisibility(View.VISIBLE);
                            etMaxStep.setText("" + roundProgressBar.getMax());
                            etMaxStep.setSelection(etMaxStep.length());
                        }
                    });

                    fabIsShowEtMaxStep.setTag(FAB_UP);

                }

                //如果FAB箭头向上
                else {

                    //如果etMaxStep不为空
                    if (etMaxStep.getText().toString().isEmpty()) {

                        etMaxStep.setError("不能输入空！");
                        return;
                    }

                    //如果输入的最大步数大于步数
                    if (Integer.valueOf(etMaxStep.getText().toString()) < roundProgressBar.getProgress()) {

                        etMaxStep.setError("最大步数小于当前步数！");
                        return;
                    }

                    int maxNew = Integer.valueOf(etMaxStep.getText().toString());
                    Log.i(TAG, "maxNew=" + maxNew);

                    etMaxStep.setVisibility(View.GONE);

                    //最大步数改变动画
                    ObjectAnimator maxChangeAnimator = ObjectAnimator.ofInt(roundProgressBar, "max", roundProgressBar.getMax(), maxNew);
                    //高度变化动画
                    ObjectAnimator heightAnimator = ObjectAnimator.ofInt(layoutProgressBar, "minimumHeight", DensityUtil.dip2px(PedometerFragment.this.getActivity(), LAYOUT_HEIGHT_OFFSET), layoutHeightOriginal);
                    //FAB旋转动画
                    ObjectAnimator rotateAnimator = ObjectAnimator.ofFloat(fabIsShowEtMaxStep, "rotation", 180f, 360f);
                    //组合动画
                    AnimatorSet animatorSet = new AnimatorSet();
                    animatorSet.play(rotateAnimator).with(heightAnimator).with(maxChangeAnimator);
                    animatorSet.setDuration(500);
                    animatorSet.start();

                    fabIsShowEtMaxStep.setTag(FAB_DOWN);

                }

                break;

            //天气CardView
            case R.id.cardview_weather:

                //跳转到天气Activity
                Intent intent = new Intent(getActivity(), WeatherActivity.class);
                if (weatherInfoJSON != null) {
                    intent.putExtra(Constant.INTENT_FIELD_NAME_WEATHER_INFO_JSON, weatherInfoJSON.toString());
                }
                startActivityForResult(intent, Constant.REQUEST_CODE_WEATHER_ACTIVITY);
                break;

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

        //设置步数
        roundProgressBar.setProgress(mStepInfoDBHelper.getStepByIdAndDate(mId, new Date(System.currentTimeMillis())));

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

    @Override
    /**
     * onDestroy
     */
    public void onDestroy() {
        super.onDestroy();

        //移除请求队列中所有请求
        mRequestQueue.cancelAll(null);

        //移除消息队列中所有消息
        mHandler.removeCallbacksAndMessages(null);

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

//        Snackbar.make(roundProgressBar, "step=" + step, Snackbar.LENGTH_SHORT).show();
        Log.i(TAG, "step=" + step);
    }
}
