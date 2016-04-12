package com.waka.workspace.wakapedometer.weather.adapter;

import android.content.Context;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.waka.workspace.wakapedometer.R;
import com.waka.workspace.wakapedometer.weather.WeatherBean;

import java.util.List;

/**
 * Suggestion建议Adapter
 * Created by waka on 2016/4/12.
 */
public class SuggestionAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final String TAG = "HourlyForecastAdapter";

    private Context mContext;
    private List<WeatherBean.Suggestion.Level> mDatas;

    /**
     * 构造方法
     *
     * @param context
     * @param datas
     */
    public SuggestionAdapter(Context context, List<WeatherBean.Suggestion.Level> datas) {

        mContext = context;
        mDatas = datas;

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        MyViewHolder viewHolder = new MyViewHolder(LayoutInflater.from(mContext).inflate(R.layout.recycler_view_item_suggestion_in_activity_weather, parent, false));
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        MyViewHolder viewHolder = (MyViewHolder) holder;

        viewHolder.imgSuggestion.setImageResource(mDatas.get(position).getIconId());//图标
        viewHolder.tvType.setText(mDatas.get(position).getType());//类型
        viewHolder.tvBrf.setText(mDatas.get(position).getBrf());//类型加简介
        viewHolder.level = mDatas.get(position);//赋值
    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }

    /**
     * MyViewHolder
     */
    static final class MyViewHolder extends RecyclerView.ViewHolder {

        private LinearLayout layout;//总布局
        private ImageView imgSuggestion;//图标
        private TextView tvType;//类型
        private TextView tvBrf;//简介

        private WeatherBean.Suggestion.Level level;

        public MyViewHolder(View itemView) {
            super(itemView);

            layout = (LinearLayout) itemView.findViewById(R.id.layout);
            imgSuggestion = (ImageView) itemView.findViewById(R.id.img_suggestion);//图标
            tvType = (TextView) itemView.findViewById(R.id.tv_type);//类型
            tvBrf = (TextView) itemView.findViewById(R.id.tv_brf);//简介

            layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (level != null) {
                        Snackbar.make(layout, level.getTxt(), Snackbar.LENGTH_LONG).show();
                    }
                }
            });
        }

    }
}
