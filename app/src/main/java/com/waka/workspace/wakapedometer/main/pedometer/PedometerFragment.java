package com.waka.workspace.wakapedometer.main.pedometer;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationSet;
import android.widget.Button;
import android.widget.LinearLayout;

import com.waka.workspace.wakapedometer.Constant;
import com.waka.workspace.wakapedometer.R;
import com.waka.workspace.wakapedometer.utils.DensityUtil;
import com.waka.workspace.wakapedometer.utils.LoginInfoUtil;
import com.waka.workspace.wakapedometer.customview.RoundProgressBar;
import com.waka.workspace.wakapedometer.database.DBHelper;
import com.waka.workspace.wakapedometer.database.StepInfoDB;

import java.sql.Date;
import java.util.Observable;
import java.util.Observer;

/**
 * PedometerFragment
 * <p>
 * 观察者，需要根据步数变化动态更新UI
 * <p>
 * Created by waka on 2016/2/16.
 */
public class PedometerFragment extends Fragment implements View.OnClickListener, Observer {

    private static final String TAG = "PedometerFragment";

    private CardView cardView;
    private LinearLayout layoutProgressBar;
    private int layoutHeightOriginal;//布局原来的高度
    private RoundProgressBar roundProgressBar;//自定义圆形进度条
    private FloatingActionButton fabCardView;//FAB
    private static final int FAB_UP = 1;//FAB指向上
    private static final int FAB_DOWN = 2;//FAB指向下

    private Button btnAdd;

    //数据库操作类
    private int mId;//当前用户id
    private DBHelper mDBHelper;
    private SQLiteDatabase mDB;
    private StepInfoDB mStepInfoDB;

    /**
     * 构造方法
     */
    public PedometerFragment() {

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

        cardView = (CardView) view.findViewById(R.id.cardView);

        layoutProgressBar = (LinearLayout) view.findViewById(R.id.layoutProgressBar);
        layoutHeightOriginal = layoutProgressBar.getHeight();//获得布局原来的高度

        roundProgressBar = (RoundProgressBar) view.findViewById(R.id.roundProgressBar);

        fabCardView = (FloatingActionButton) view.findViewById(R.id.fabCardView);
        fabCardView.setTag(FAB_DOWN);//设置向下标志，用来判断该指上还是指下

        btnAdd = (Button) view.findViewById(R.id.btnAdd);
    }

    /**
     * initData
     */
    private void initData() {

        //初始化数据库
        mId = LoginInfoUtil.getCurrentLoginId(PedometerFragment.this.getActivity());
        mDBHelper = new DBHelper(PedometerFragment.this.getActivity(), Constant.DB, null, 1);
        mDB = mDBHelper.getWritableDatabase();
        mStepInfoDB = new StepInfoDB(mDB);
    }

    /**
     * initEvent
     */
    private void initEvent() {
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ObjectAnimator animator = ObjectAnimator.ofInt(roundProgressBar, "progress", roundProgressBar.getProgress(), roundProgressBar.getProgress() + 500);
                animator.setDuration(500);
                animator.start();

            }
        });

        fabCardView.setOnClickListener(this);
    }

    /**
     * onClick
     *
     * @param v
     */
    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.fabCardView:

                if ((int) fabCardView.getTag() == FAB_DOWN) {

                    //高度变化动画
                    ObjectAnimator heightAnimator = ObjectAnimator.ofInt(layoutProgressBar, "minimumHeight", layoutHeightOriginal, DensityUtil.dip2px(PedometerFragment.this.getActivity(), 440));
                    //FAB旋转动画
                    ObjectAnimator rotateAnimator = ObjectAnimator.ofFloat(fabCardView, "rotation", 0f, 180f);
                    //组合动画
                    AnimatorSet animatorSet = new AnimatorSet();
                    animatorSet.play(rotateAnimator).with(heightAnimator);
                    animatorSet.setDuration(500);
                    animatorSet.start();

                    fabCardView.setTag(FAB_UP);

                } else {

                    //高度变化动画
                    ObjectAnimator heightAnimator = ObjectAnimator.ofInt(layoutProgressBar, "minimumHeight", DensityUtil.dip2px(PedometerFragment.this.getActivity(), 440), layoutHeightOriginal);
                    //FAB旋转动画
                    ObjectAnimator rotateAnimator = ObjectAnimator.ofFloat(fabCardView, "rotation", 180f, 360f);
                    //组合动画
                    AnimatorSet animatorSet = new AnimatorSet();
                    animatorSet.play(rotateAnimator).with(heightAnimator);
                    animatorSet.setDuration(500);
                    animatorSet.start();

                    fabCardView.setTag(FAB_DOWN);

                }

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
        roundProgressBar.setProgress(mStepInfoDB.getStepByIdAndDate(mId, new Date(System.currentTimeMillis())));

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
