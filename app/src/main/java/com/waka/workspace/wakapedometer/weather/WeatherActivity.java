package com.waka.workspace.wakapedometer.weather;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.waka.workspace.wakapedometer.Constant;
import com.waka.workspace.wakapedometer.R;

/**
 * 天气Activity
 */
public class WeatherActivity extends AppCompatActivity {

    private static final String TAG = "WeatherActivity";

    private TextView tvData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        /**
         * initView
         */
        tvData = (TextView) findViewById(R.id.tv_data);

        /**
         * initData
         */
        Intent intent = getIntent();
        if (intent != null) {
            String weatherInfoData = intent.getStringExtra(Constant.INTENT_FIELD_NAME_WEATHER_INFO_JSON);
            if (weatherInfoData != null) {

                Log.d(TAG, "【onCreate】weatherInfoData = " + weatherInfoData);
                tvData.setText(weatherInfoData);
            }
        }
    }
}
