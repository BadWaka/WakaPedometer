package com.waka.workspace.wakapedometer;

import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.waka.workspace.wakapedometer.customview.RulerView;
import com.waka.workspace.wakapedometer.database.DBHelper;
import com.waka.workspace.wakapedometer.database.PersonDBHelper;
import com.waka.workspace.wakapedometer.database.bean.PersonBean;
import com.waka.workspace.wakapedometer.utils.LoginInfoUtil;

import java.lang.ref.WeakReference;

/**
 * 个人中心
 */
public class MineActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "MineActivity";

    //数据库
    private int mId;
    private DBHelper mDBHelper;
    private SQLiteDatabase mDB;
    private PersonDBHelper mPersonDBHelper;

    //Toolbar
    private Toolbar toolbar;
    private LinearLayout layoutClose, layoutCommit;

    //头像栏
    private ImageView imgHeadIcon;
    private Button btnChangeHeadIcon;

    //昵称栏
    private EditText etNickname;

    //性别栏
    private RadioGroup radioGroup;
    private RadioButton rdbtnMale, rdbtnFemale;

    //身高栏
    private TextView tvHeight;
    private RulerView rulerViewHeight;

    //体重栏
    private TextView tvWeight;
    private RulerView rulerViewWeight;

    /**
     * onCreate
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mine);

        initView();
        initData();
        initEvent();

        new LoadInfoAsyckTask(this).execute();
    }

    /**
     * initView
     */
    private void initView() {

        //toolbar
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        layoutClose = (LinearLayout) findViewById(R.id.toolbar_close);
        layoutCommit = (LinearLayout) findViewById(R.id.toolbar_commit);

        //头像栏
        imgHeadIcon = (ImageView) findViewById(R.id.img_headicon);
        btnChangeHeadIcon = (Button) findViewById(R.id.btn_change_headicon);

        //昵称栏
        etNickname = (EditText) findViewById(R.id.et_nickname);

        //性别栏
        radioGroup = (RadioGroup) findViewById(R.id.radio_group);
        rdbtnMale = (RadioButton) findViewById(R.id.radio_btn_male);
        rdbtnFemale = (RadioButton) findViewById(R.id.radio_btn_female);

        //身高栏
        tvHeight = (TextView) findViewById(R.id.tv_height);
        rulerViewHeight = (RulerView) findViewById(R.id.ruler_view_height);

        //体重栏
        tvWeight = (TextView) findViewById(R.id.tv_weight);
        rulerViewWeight = (RulerView) findViewById(R.id.ruler_view_weight);
    }

    /**
     * initData
     */
    private void initData() {

        //初始化数据库
        mId = LoginInfoUtil.getCurrentLoginId(MineActivity.this);
        mDBHelper = new DBHelper(MineActivity.this, Constant.DB, null, 1);
        mDB = mDBHelper.getWritableDatabase();
        mPersonDBHelper = new PersonDBHelper(mDB);

        //设置toolbar
        this.setSupportActionBar(toolbar);

        //设置身高RulerView
        rulerViewHeight.setStartValue(50);
        rulerViewHeight.setEndValue(250);
        rulerViewHeight.setOriginValue(170);
        rulerViewHeight.setPartitionWidthInDP(50);
        rulerViewHeight.setPartitionValue(1);
        rulerViewHeight.setSmallPartitionCount(1);
        rulerViewHeight.setValueChangeListener(new RulerView.OnValueChangeListener() {
            @Override
            public void onValueChange(int intVal, int fltval) {
                tvHeight.setText("" + intVal);
            }
        });

        //设置体重RulerView
        rulerViewWeight.setStartValue(30);
        rulerViewWeight.setEndValue(200);
        rulerViewWeight.setOriginValue(60);
        rulerViewWeight.setPartitionWidthInDP(30);
        rulerViewWeight.setPartitionValue(1);
        rulerViewWeight.setSmallPartitionCount(1);
        rulerViewWeight.setValueChangeListener(new RulerView.OnValueChangeListener() {
            @Override
            public void onValueChange(int intVal, int fltval) {
                tvWeight.setText("" + intVal);
            }
        });
    }

    /**
     * initEvent
     */
    private void initEvent() {

        //toolbar
        layoutClose.setOnClickListener(this);
        layoutCommit.setOnClickListener(this);

        //头像栏
        btnChangeHeadIcon.setOnClickListener(this);
    }

    /**
     * onClick
     *
     * @param v
     */
    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            //toolbar关闭
            case R.id.toolbar_close:
                finish();
                break;

            //toolbar提交
            case R.id.toolbar_commit:
                break;

            //改变头像
            case R.id.btn_change_headicon:

//                mPersonDBHelper.updateHeadIconUrl(mId, "http://img2.imgtn.bdimg.com/it/u=3791371496,3675101188&fm=11&gp=0.jpg");
//
//                PersonBean personBean = mPersonDBHelper.queryById(mId);
//
//                String headIconUrl = personBean.getHeadIconUrl();
//
//                Snackbar.make(btnChangeHeadIcon, headIconUrl, Snackbar.LENGTH_SHORT).show();

                break;

            default:

                break;
        }
    }

    /**
     * 读取信息AsyckTask
     * <p/>
     * 使用静态内部类和弱引用减少内存泄漏
     */
    private static class LoadInfoAsyckTask extends AsyncTask<Void, Void, PersonBean> {

        //弱引用MineActivity实例
        private WeakReference<MineActivity> wrMineActivity;

        /**
         * 构造方法，须传入MineActivity实例
         *
         * @param mineActivity
         */
        public LoadInfoAsyckTask(MineActivity mineActivity) {
            wrMineActivity = new WeakReference<MineActivity>(mineActivity);
        }

        @Override
        /**
         * onPreExecute
         */
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        /**
         * 后台读取数据库
         */
        protected PersonBean doInBackground(Void... params) {

            PersonBean personBean = wrMineActivity.get().mPersonDBHelper.queryById(wrMineActivity.get().mId);

            return personBean;
        }

        @Override
        /**
         * 前台更新UI
         */
        protected void onPostExecute(PersonBean personBean) {
            super.onPostExecute(personBean);

            if (personBean == null) {
                return;
            }
            Log.i(TAG, personBean.toString());

            //设置昵称
            wrMineActivity.get().etNickname.setText(personBean.getNickName());
            wrMineActivity.get().etNickname.setSelection(wrMineActivity.get().etNickname.getText().length());//设置光标位置

            //设置性别
            switch (personBean.getSex()) {
                case 0:
                    wrMineActivity.get().radioGroup.check(R.id.radio_btn_male);
                    break;
                case 1:
                    wrMineActivity.get().radioGroup.check(R.id.radio_btn_female);
                    break;
            }

            //设置身高
            if (personBean.getHeight() != 0) {
                wrMineActivity.get().tvHeight.setText("" + personBean.getHeight());
                wrMineActivity.get().rulerViewHeight.setOriginValue((int) personBean.getHeight());
            }

            //设置体重
            if (personBean.getWeight() != 0) {
                wrMineActivity.get().tvWeight.setText("" + personBean.getWeight());
                wrMineActivity.get().rulerViewWeight.setOriginValue((int) personBean.getWeight());
            }
        }
    }
}
