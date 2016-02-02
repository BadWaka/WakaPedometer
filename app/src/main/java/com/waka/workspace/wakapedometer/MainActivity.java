package com.waka.workspace.wakapedometer;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.jar.Manifest;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "Pedometer MainActivity";

    private Button btnStart, btnStop;

    /**
     * onCreate
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initData();
        initEvent();
    }

    /*initView*/
    private void initView() {
        btnStart = (Button) findViewById(R.id.btnStart);
        btnStop = (Button) findViewById(R.id.btnStop);
    }

    /*initData*/
    private void initData() {

    }

    /*initEvent*/
    private void initEvent() {

        btnStart.setOnClickListener(this);
        btnStop.setOnClickListener(this);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "Activity onDestroy");
    }

    /**
     * onClick
     *
     * @param v
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            //开始服务
            case R.id.btnStart:
                Intent startIntent = new Intent(MainActivity.this, PedometerService.class);
                startService(startIntent);
                break;

            //停止服务
            case R.id.btnStop:
                Intent stopIntent = new Intent(MainActivity.this, PedometerService.class);
                stopService(stopIntent);
                break;

            default:
                break;
        }
    }
}
