package com.waka.workspace.wakapedometer.weather;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * 获取天气数据请求
 * <p/>
 * 网址：http://apistore.baidu.com/apiworks/servicedetail/478.html
 * <p/>
 * Created by waka on 2016/4/9.
 */
public class GetWeatherInfoRequest extends JsonObjectRequest {

    //天气网站 BaseUrl基础地址
    private static final String BASE_URL = "http://apis.baidu.com/heweather/weather/free";

    //网站上申请的apikey
    private static final String WEATHER_API_KEY = "9ce31f980420f39ac53b5f2416e60fd3";

    /**
     * 使用newInstance初始化
     *
     * @param cityName
     * @param listener
     * @param errorListener
     * @return
     */
    public static GetWeatherInfoRequest newInstance(String cityName,
                                                    Response.Listener<JSONObject> listener,
                                                    Response.ErrorListener errorListener) {

        //地址加入城市名
        String url = BASE_URL + "?city=" + cityName;

        //调用构造方法新建请求实例
        GetWeatherInfoRequest getWeatherInfoRequest = new GetWeatherInfoRequest(url, null, listener, errorListener);

        //返回请求实例
        return getWeatherInfoRequest;
    }

    /**
     * 私有构造方法
     *
     * @param url
     * @param jsonRequest
     * @param listener
     * @param errorListener
     */
    private GetWeatherInfoRequest(String url, JSONObject jsonRequest, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener) {
        super(url, jsonRequest, listener, errorListener);
    }

    @Override
    /**
     * 重写getHeaders方法
     * 添加Header
     */
    public Map<String, String> getHeaders() throws AuthFailureError {

        //把apikey添加进header
        Map<String, String> map = new HashMap<String, String>();
        map.put("apikey", WEATHER_API_KEY);
        return map;
    }
}
