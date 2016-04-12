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
 * 7天天气预报
 * Created by waka on 2016/4/12.
 */
public class DailyForecastAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final String TAG = "HourlyForecastAdapter";

    private Context mContext;
    private List<WeatherBean.DailyForecast> mDatas;

    /**
     * 构造方法
     *
     * @param context
     * @param datas
     */
    public DailyForecastAdapter(Context context, List<WeatherBean.DailyForecast> datas) {
        mContext = context;
        mDatas = datas;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        MyViewHolder viewHolder = new MyViewHolder(LayoutInflater.from(mContext).inflate(R.layout.recycler_view_item_daily_forecast_in_activity_weather, parent, false));
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        MyViewHolder viewHolder = (MyViewHolder) holder;

        viewHolder.tvDate.setText(mDatas.get(position).getDate());
        viewHolder.tvTmpMax.setText(mDatas.get(position).getTmp().getMax());
        viewHolder.tvTmpMin.setText(mDatas.get(position).getTmp().getMin());

        String condTxtD = mDatas.get(position).getCond().getTxt_d();
        String condTxtN = mDatas.get(position).getCond().getTxt_n();
        if (condTxtD.equals(condTxtN)) {//如果早晚天气描述没有变化，直接将分隔符和夜晚赋为""
            viewHolder.tvCondTxtD.setText(condTxtD);
            viewHolder.tvCondTxtN.setText("");
            viewHolder.tvCondTxtDivide.setText("");
        } else {
            viewHolder.tvCondTxtD.setText(mDatas.get(position).getCond().getTxt_d());
            viewHolder.tvCondTxtN.setText(mDatas.get(position).getCond().getTxt_n());
        }
        viewHolder.tvWindSc.setText(mDatas.get(position).getWind().getSc());
        viewHolder.tvWindSpd.setText(mDatas.get(position).getWind().getSpd());

        viewHolder.tvPres.setText(mDatas.get(position).getPres());
        viewHolder.tvWindDeg.setText(mDatas.get(position).getWind().getDeg());
        viewHolder.tvWindDir.setText(mDatas.get(position).getWind().getDir());

        viewHolder.tvHum.setText(mDatas.get(position).getHum());
        viewHolder.tvPop.setText(mDatas.get(position).getPop());

        viewHolder.tvVis.setText(mDatas.get(position).getVis());
        viewHolder.tvPcpn.setText(mDatas.get(position).getPcpn());

    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }

    /**
     * MyViewHolder
     */
    static final class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView tvDate;//日期
        private TextView tvTmpMax;//最大温度
        private TextView tvTmpMin;//最小温度

        private TextView tvCondTxtD;//白天天气状况描述
        private TextView tvCondTxtDivide;//天气状况分隔符
        private TextView tvCondTxtN;//夜间天气状况描述
        private TextView tvWindSc;//风力
        private TextView tvWindSpd;//风速

        private TextView tvPres;//气压
        private TextView tvWindDir;//风向
        private TextView tvWindDeg;//风向度数

        private TextView tvHum;//相对湿度
        private TextView tvPop;//降水概率

        private TextView tvVis;//能见度
        private TextView tvPcpn;//降水量

        public MyViewHolder(View itemView) {
            super(itemView);

            tvDate = (TextView) itemView.findViewById(R.id.tv_date);//时间
            tvTmpMax = (TextView) itemView.findViewById(R.id.tv_tmp_max);//最大温度
            tvTmpMin = (TextView) itemView.findViewById(R.id.tv_tmp_min);//最小温度

            tvCondTxtD = (TextView) itemView.findViewById(R.id.tv_cond_txt_d);//白天天气状况描述
            tvCondTxtDivide = (TextView) itemView.findViewById(R.id.tv_cond_txt_divide);//天气状况分隔符
            tvCondTxtN = (TextView) itemView.findViewById(R.id.tv_cond_txt_n);//夜间天气状况描述
            tvWindSc = (TextView) itemView.findViewById(R.id.tv_wind_sc);//风力
            tvWindSpd = (TextView) itemView.findViewById(R.id.tv_wind_spd);//风速

            tvPres = (TextView) itemView.findViewById(R.id.tv_pres);//气压
            tvWindDir = (TextView) itemView.findViewById(R.id.tv_wind_dir);//风向
            tvWindDeg = (TextView) itemView.findViewById(R.id.tv_wind_deg);//风向度数

            tvHum = (TextView) itemView.findViewById(R.id.tv_hum);//相对湿度
            tvPop = (TextView) itemView.findViewById(R.id.tv_pop);//降水概率

            tvVis = (TextView) itemView.findViewById(R.id.tv_vis);//能见度
            tvPcpn = (TextView) itemView.findViewById(R.id.tv_pcpn);//降水量
        }

    }
}
