package com.waka.workspace.wakapedometer.weather.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.waka.workspace.wakapedometer.R;
import com.waka.workspace.wakapedometer.weather.WeatherBean;

import java.util.List;

/**
 * 每三小时天气预报，全能版为每小时预报的
 * RecyclerView.Adapter
 * <p/>
 * Created by waka on 2016/4/11.
 */
public class HourlyForecastAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final String TAG = "HourlyForecastAdapter";

    private Context mContext;
    private List<WeatherBean.HourlyForecast> mDatas;

    /**
     * 构造方法
     *
     * @param context
     * @param datas
     */
    public HourlyForecastAdapter(Context context, List<WeatherBean.HourlyForecast> datas) {
        mContext = context;
        mDatas = datas;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        MyViewHolder viewHolder = new MyViewHolder(LayoutInflater.from(mContext).inflate(R.layout.recycler_view_item_hourly_forecast_in_activity_weather, parent, false));
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        MyViewHolder viewHolder = (MyViewHolder) holder;
        String dateTime = mDatas.get(position).getDate();//因为获取到的Date是"2016-04-12 10:00"这种格式的，我们只需要时间不需要日期，所以进行一些处理
        String time = dateTimeToTime(dateTime);
        viewHolder.tvDate.setText(time);
        viewHolder.tvHum.setText(mDatas.get(position).getHum());
        viewHolder.tvPop.setText(mDatas.get(position).getPop());
        viewHolder.tvPres.setText(mDatas.get(position).getPres());
        viewHolder.tvTmp.setText(mDatas.get(position).getTmp());
        viewHolder.tvWindDeg.setText(mDatas.get(position).getWind().getDeg());
        viewHolder.tvWindDir.setText(mDatas.get(position).getWind().getDir());
        viewHolder.tvWindSc.setText(mDatas.get(position).getWind().getSc());
        viewHolder.tvWindSpd.setText(mDatas.get(position).getWind().getSpd());
    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }

    /**
     * 将日期时间转换为时间
     * "2016-04-12 10:00"转为"10:00"
     *
     * @param dateTime
     * @return time
     */
    private String dateTimeToTime(String dateTime) {

        StringBuffer dateTimeBuffer = new StringBuffer(dateTime);
        StringBuffer timeBuffer = dateTimeBuffer.delete(0, 11);
        String time = timeBuffer.toString();
        return time;
    }

    /**
     * MyViewHolder
     */
    static final class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView tvDate;//时间
        private TextView tvTmp;//温度
        private TextView tvWindSc;//风力

        private TextView tvHum;//相对湿度
        private TextView tvPop;//降水概率
        private TextView tvPres;//气压
        private TextView tvWindDeg;//风向度数
        private TextView tvWindDir;//风向
        private TextView tvWindSpd;//风速

        public MyViewHolder(View itemView) {
            super(itemView);

            tvDate = (TextView) itemView.findViewById(R.id.tv_date);//时间
            tvTmp = (TextView) itemView.findViewById(R.id.tv_tmp);//温度
            tvWindSc = (TextView) itemView.findViewById(R.id.tv_wind_sc);//风力

            tvHum = (TextView) itemView.findViewById(R.id.tv_hum);//相对湿度
            tvPop = (TextView) itemView.findViewById(R.id.tv_pop);//降水概率
            tvPres = (TextView) itemView.findViewById(R.id.tv_pres);//气压
            tvWindDeg = (TextView) itemView.findViewById(R.id.tv_wind_deg);//风向度数
            tvWindDir = (TextView) itemView.findViewById(R.id.tv_wind_dir);//风向
            tvWindSpd = (TextView) itemView.findViewById(R.id.tv_wind_spd);//风速
        }

    }
}
