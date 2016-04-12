package com.waka.workspace.wakapedometer.weather;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
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
import com.waka.workspace.wakapedometer.customview.recyclerview_for_scrollview.FullyGridLayoutManager;
import com.waka.workspace.wakapedometer.customview.recyclerview_for_scrollview.FullyLinearLayoutManager;
import com.waka.workspace.wakapedometer.weather.adapter.DailyForecastAdapter;
import com.waka.workspace.wakapedometer.weather.adapter.HourlyForecastAdapter;
import com.waka.workspace.wakapedometer.weather.adapter.SuggestionAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * 天气Activity
 */
public class WeatherActivity extends AppCompatActivity {

    private static final String TAG = "WeatherActivity";

    //toolbar
    private Toolbar toolbar;

    //volley网络变量
    private RequestQueue mRequestQueue;//Volley请求队列

    //天气数据
    private JSONObject mWeatherInfoJSON;//天气信息，JSON
    private WeatherBean mWeatherBean;//天气信息，JavaBean

    //下拉刷新
    private SwipeRefreshLayout swipeRefreshLayout;

    //TODO
    //展示数据的View
    //今天
    private TextView tvTemperature;//实时温度
    private TextView tvWeatherNowConditionTxt;//天气情况描述
    private TextView tvWeatherAirQualityQlty;//空气质量类别
    private LinearLayout layoutWeatherAirQuality;//空气质量栏
    private ImageView imgWeatherAirQualityArrow;//箭头

    //空气质量详情栏
    private LinearLayout layoutWeatherAirQualityDetail;//空气质量详情栏
    private TextView tvWeatherAirQualityAqi;//空气质量指数
    private TextView tvWeatherAirQualityPm25;//PM2.5
    private TextView tvWeatherAirQualitySO2;//SO₂
    private TextView tvWeatherAirQualityPm10;//PM10
    private TextView tvWeatherAirQualityNO2;//NO₂
    private TextView tvWeatherAirQualityCO;//CO
    private TextView tvWeatherAirQualityO3;//O₃

    //生活指数，仅限国内城市，国际城市无此字段
    private RecyclerView rvSuggestion;
    private SuggestionAdapter suggestionAdapter;
    private List<WeatherBean.Suggestion.Level> suggestionDatas;

    //每三小时天气预报，全能版为每小时预报
    private RecyclerView rvHourlyForecast;
    private HourlyForecastAdapter hourlyForecastAdapter;

    //7天天气预报
    private RecyclerView rvDailyForecast;
    private DailyForecastAdapter dailyForecastAdapter;

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
        private WeakReference<WeatherActivity> wrWeatherActivity;

        public MyHandler(WeatherActivity weatherActivity) {
            wrWeatherActivity = new WeakReference<WeatherActivity>(weatherActivity);
        }

        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {

                //获取天气信息失败
                case WHAT_WEATHER_INFO_FAIL:

                    //如果下拉刷新布局在刷新
                    if (wrWeatherActivity.get().swipeRefreshLayout.isRefreshing()) {
                        //停止刷新
                        wrWeatherActivity.get().swipeRefreshLayout.setRefreshing(false);
                    }

                    VolleyError volleyError = (VolleyError) msg.obj;
                    Snackbar.make(wrWeatherActivity.get().toolbar, volleyError.getMessage(), Snackbar.LENGTH_SHORT).show();

                    break;

                //获取天气信息成功
                case WHAT_WEATHER_INFO_SUCCESS:

                    //如果下拉刷新布局在刷新
                    if (wrWeatherActivity.get().swipeRefreshLayout.isRefreshing()) {
                        //停止刷新
                        wrWeatherActivity.get().swipeRefreshLayout.setRefreshing(false);
                    }

                    Snackbar.make(wrWeatherActivity.get().toolbar, "获取天气数据成功！", Snackbar.LENGTH_SHORT).show();

                    //获得weatherBean
                    wrWeatherActivity.get().mWeatherBean = (WeatherBean) msg.obj;

                    //获得详细数据
                    WeatherBean weatherBean = wrWeatherActivity.get().mWeatherBean;
                    String temperature = weatherBean.getNow().getTmp();//温度
                    String aqi = weatherBean.getAqi().getCity().getAqi();//空气质量指数
                    String quality = weatherBean.getAqi().getCity().getQlty();//空气质量类别

                    //更新UI
                    wrWeatherActivity.get().updateUI(wrWeatherActivity.get().mWeatherBean);

                    break;

                default:
                    break;
            }
        }

    }

    @Override
    /**
     * onCreate
     */
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);

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
        setSupportActionBar(toolbar);

        //下拉刷新布局
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_light,
                android.R.color.holo_red_light, android.R.color.holo_orange_light,
                android.R.color.holo_green_light);//设置刷新时动画的颜色，可以设置4个

        //TODO
        //展示数据的View
        //今天
        tvTemperature = (TextView) findViewById(R.id.tv_temperature);//实时温度
        tvWeatherNowConditionTxt = (TextView) findViewById(R.id.tv_weather_now_condition_txt);//天气情况描述
        tvWeatherAirQualityQlty = (TextView) findViewById(R.id.tv_weather_air_quality_qlty);//空气质量类别
        layoutWeatherAirQuality = (LinearLayout) findViewById(R.id.layout_weather_air_quality);//空气质量栏
        imgWeatherAirQualityArrow = (ImageView) findViewById(R.id.img_weather_air_quality_arrow);//箭头

        //空气质量详情栏
        layoutWeatherAirQualityDetail = (LinearLayout) findViewById(R.id.layout_weather_air_quality_detail);//空气质量详情栏
        tvWeatherAirQualityAqi = (TextView) findViewById(R.id.tv_weather_air_quality_aqi);//空气质量指数
        tvWeatherAirQualityPm25 = (TextView) findViewById(R.id.tv_weather_air_quality_pm25);//PM2.5
        tvWeatherAirQualitySO2 = (TextView) findViewById(R.id.tv_weather_air_quality_so2);//SO₂
        tvWeatherAirQualityPm10 = (TextView) findViewById(R.id.tv_weather_air_quality_pm10);//PM10
        tvWeatherAirQualityNO2 = (TextView) findViewById(R.id.tv_weather_air_quality_no2);//NO₂
        tvWeatherAirQualityCO = (TextView) findViewById(R.id.tv_weather_air_quality_co);//CO
        tvWeatherAirQualityO3 = (TextView) findViewById(R.id.tv_weather_air_quality_o3);//O₃

        //生活指数，仅限国内城市，国际城市无此字段
        rvSuggestion = (RecyclerView) findViewById(R.id.recycler_view_suggestion);

        //每三小时天气预报，全能版为每小时预报
        rvHourlyForecast = (RecyclerView) findViewById(R.id.recycler_view_hourly_forecast);

        //7天天气预报
        rvDailyForecast = (RecyclerView) findViewById(R.id.recycler_view_daily_forecast);
    }

    /**
     * initData
     */
    private void initData() {

        //初始化网络
        mRequestQueue = Volley.newRequestQueue(this);//初始化请求队列

        //获取天气信息
        getWeatherInfo();
    }

    /**
     * initEvent
     */
    private void initEvent() {

        //下拉刷新监听
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getWeatherInfoFromInternet("beijing");
            }
        });

        //空气质量栏
        layoutWeatherAirQuality.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (layoutWeatherAirQualityDetail.getVisibility() == View.GONE) {
                    layoutWeatherAirQualityDetail.setVisibility(View.VISIBLE);
                    imgWeatherAirQualityArrow.setImageResource(R.mipmap.ic_keyboard_arrow_up_white_18dp);
                } else {
                    layoutWeatherAirQualityDetail.setVisibility(View.GONE);
                    imgWeatherAirQualityArrow.setImageResource(R.mipmap.ic_keyboard_arrow_down_white_18dp);
                }
            }
        });
    }

    /**
     * 获取天气信息
     */
    private void getWeatherInfo() {

        //尝试从PedometerFragment中获取天气数据
        Intent intent = getIntent();
        if (intent != null) {

            //如果获取到了
            String weatherInfoStr = intent.getStringExtra(Constant.INTENT_FIELD_NAME_WEATHER_INFO_JSON);
            if (weatherInfoStr != null) {

                Log.d(TAG, "【getWeatherInfo】    weatherInfoStr = " + weatherInfoStr);
                try {

                    mWeatherInfoJSON = new JSONObject(weatherInfoStr);
                    new DeserializeWeatherBeanThread(WeatherActivity.this, mWeatherInfoJSON).start();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        //否则，直接从网络上获取天气数据
        else {
            getWeatherInfoFromInternet("beijing");
        }
    }

    /**
     * 从网络上获取天气信息
     *
     * @param cityName
     */
    private void getWeatherInfoFromInternet(String cityName) {

        //创建获取天气数据请求
        GetWeatherInfoRequest getWeatherInfoRequest = GetWeatherInfoRequest.newInstance(cityName,
                new Response.Listener<JSONObject>() {

                    @Override
                    //成功监听
                    public void onResponse(final JSONObject response) {

                        Log.d(TAG, "【getWeatherInfoFromInternet】【onResponse】response = " + response.toString());

                        mWeatherInfoJSON = response;

                        //新建线程用于反序列化
                        new DeserializeWeatherBeanThread(WeatherActivity.this, response).start();

                    }
                }, new Response.ErrorListener() {

                    @Override
                    //失败监听
                    public void onErrorResponse(VolleyError error) {

                        Log.e(TAG, "【getWeatherInfoFromInternet】【onErrorResponse】error = " + error.getMessage(), error);

                        //发送失败消息
                        Message msg = Message.obtain();
                        msg.what = WHAT_WEATHER_INFO_FAIL;
                        msg.obj = error;
                        mHandler.sendMessage(msg);
                    }
                });

        //添加请求进请求队列
        mRequestQueue.add(getWeatherInfoRequest);
    }

    /**
     * 更新UI
     *
     * @param weatherBean
     */
    private void updateUI(WeatherBean weatherBean) {

        if (weatherBean == null) {
            Log.e(TAG, "【updateUI】   weatherBean == null");
            return;
        }

        if (weatherBean.getStatus().equals("false")) {
            Log.e(TAG, "【updateUI】   status == false");
            return;
        }

        //TODO
        tvTemperature.setText(weatherBean.getNow().getTmp());//实时温度
        tvWeatherNowConditionTxt.setText(weatherBean.getNow().getCond().getTxt());//天气情况描述
        tvWeatherAirQualityQlty.setText(weatherBean.getAqi().getCity().getQlty());//空气质量类别

        //空气质量详情
        tvWeatherAirQualityAqi.setText(weatherBean.getAqi().getCity().getAqi());//空气质量指数
        tvWeatherAirQualityPm25.setText(weatherBean.getAqi().getCity().getPm25());//PM2.5
        tvWeatherAirQualitySO2.setText(weatherBean.getAqi().getCity().getSo2());//SO₂
        tvWeatherAirQualityPm10.setText(weatherBean.getAqi().getCity().getPm10());//PM10
        tvWeatherAirQualityNO2.setText(weatherBean.getAqi().getCity().getNo2());//NO₂
        tvWeatherAirQualityCO.setText(weatherBean.getAqi().getCity().getCo());//CO
        tvWeatherAirQualityO3.setText(weatherBean.getAqi().getCity().getO3());//O₃

        //生活指数，仅限国内城市，国际城市无此字段
        WeatherBean.Suggestion suggestion = weatherBean.getSuggestion();
        if (suggestionDatas == null) {
            suggestionDatas = new ArrayList<>();
        } else {
            suggestionDatas.clear();
        }
        WeatherBean.Suggestion.Level comf = weatherBean.getSuggestion().getComf();
        comf.setIconId(R.mipmap.ic_favorite_border_white_48dp);
        comf.setType("舒适度");
        suggestionDatas.add(comf);
        WeatherBean.Suggestion.Level cw = weatherBean.getSuggestion().getCw();
        cw.setIconId(R.mipmap.ic_directions_car_white_48dp);
        cw.setType("洗车");
        suggestionDatas.add(cw);
        WeatherBean.Suggestion.Level drsg = weatherBean.getSuggestion().getDrsg();
        drsg.setIconId(R.mipmap.ic_accessibility_white_48dp);
        drsg.setType("穿衣");
        suggestionDatas.add(drsg);
        WeatherBean.Suggestion.Level flu = weatherBean.getSuggestion().getFlu();
        flu.setIconId(R.mipmap.ic_local_hospital_white_48dp);
        flu.setType("感冒");
        suggestionDatas.add(flu);
        WeatherBean.Suggestion.Level sport = weatherBean.getSuggestion().getSport();
        sport.setIconId(R.mipmap.ic_directions_run_white_48dp);
        sport.setType("运动");
        suggestionDatas.add(sport);
        WeatherBean.Suggestion.Level trav = weatherBean.getSuggestion().getTrav();
        trav.setIconId(R.mipmap.ic_flight_takeoff_white_48dp);
        trav.setType("旅游");
        suggestionDatas.add(trav);
        WeatherBean.Suggestion.Level uv = weatherBean.getSuggestion().getUv();
        uv.setIconId(R.mipmap.ic_flare_white_48dp);
        uv.setType("紫外线");
        suggestionDatas.add(uv);
        FullyGridLayoutManager gridLayoutManager = new FullyGridLayoutManager(WeatherActivity.this, 3);
        rvSuggestion.setLayoutManager(gridLayoutManager);
        if (suggestionAdapter == null) {
            suggestionAdapter = new SuggestionAdapter(WeatherActivity.this, suggestionDatas);
        } else {
            suggestionAdapter.notifyDataSetChanged();
        }
        rvSuggestion.setAdapter(suggestionAdapter);

        //每三小时天气预报，全能版为每小时预报
        FullyLinearLayoutManager linearLayoutManager = new FullyLinearLayoutManager(WeatherActivity.this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        rvHourlyForecast.setLayoutManager(linearLayoutManager);
        if (hourlyForecastAdapter == null) {
            hourlyForecastAdapter = new HourlyForecastAdapter(WeatherActivity.this, weatherBean.getHourly_forecast());
        } else {
            hourlyForecastAdapter.notifyDataSetChanged();
        }
        rvHourlyForecast.setAdapter(hourlyForecastAdapter);

        //7天天气预报
        FullyLinearLayoutManager linearLayoutManager2 = new FullyLinearLayoutManager(WeatherActivity.this);
        linearLayoutManager2.setOrientation(LinearLayoutManager.VERTICAL);
        rvDailyForecast.setLayoutManager(linearLayoutManager2);
        if (dailyForecastAdapter == null) {
            dailyForecastAdapter = new DailyForecastAdapter(WeatherActivity.this, weatherBean.getDaily_forecast());
        } else {
            dailyForecastAdapter.notifyDataSetChanged();
        }
        rvDailyForecast.setAdapter(dailyForecastAdapter);
    }

    @Override
    /**
     * onDestroy
     */
    protected void onDestroy() {
        super.onDestroy();

        //移除请求队列中所有请求
        mRequestQueue.cancelAll(null);

        //移除消息队列中所有消息
        mHandler.removeCallbacksAndMessages(null);
    }

    /**
     * 反序列化线程
     */
    static class DeserializeWeatherBeanThread extends Thread {

        //弱引用
        private WeakReference<WeatherActivity> wrWeatherActivity;

        //需要反序列化的JSON
        private JSONObject mWeatherInfoJSON;

        public DeserializeWeatherBeanThread(WeatherActivity weatherActivity, JSONObject weatherInfoJSON) {

            wrWeatherActivity = new WeakReference<WeatherActivity>(weatherActivity);
            this.mWeatherInfoJSON = weatherInfoJSON;
        }

        @Override
        public void run() {

            //反序列化
            WeatherBean weatherBean = deserializeWeatherBean(mWeatherInfoJSON);
            if (weatherBean != null) {

                //发送成功消息
                Message msg = Message.obtain();
                msg.what = WHAT_WEATHER_INFO_SUCCESS;
                msg.obj = weatherBean;//把weatherBean放进去
                wrWeatherActivity.get().mHandler.sendMessage(msg);
            }
        }

        /**
         * WeatherBean的反序列化
         *
         * @param weatherInfoJSON
         * @return WeatherBean
         */
        private WeatherBean deserializeWeatherBean(JSONObject weatherInfoJSON) {

            try {

                //从原始JSON中用 "HeWeather data service 3.0" 字段取出一个JSONArray
                JSONArray jsonArray = null;
                jsonArray = weatherInfoJSON.getJSONArray(WeatherBean.TOTAL_NAME);

                //一般来说，第一个就是我们要的可以反序列化的JSON数据了
                JSONObject jsonWeather = (JSONObject) jsonArray.get(0);

                //反序列化
                WeatherBean weatherBean = JSON.parseObject(jsonWeather.toString(), WeatherBean.class);
                return weatherBean;

            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }
        }
    }

}
