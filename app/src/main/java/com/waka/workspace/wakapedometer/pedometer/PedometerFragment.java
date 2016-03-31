package com.waka.workspace.wakapedometer.pedometer;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
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
import android.view.animation.AnticipateOvershootInterpolator;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.waka.workspace.wakapedometer.Constant;
import com.waka.workspace.wakapedometer.R;
import com.waka.workspace.wakapedometer.database.StepInfoDBHelper;
import com.waka.workspace.wakapedometer.utils.DensityUtil;
import com.waka.workspace.wakapedometer.utils.LoginInfoUtil;
import com.waka.workspace.wakapedometer.customview.RoundProgressBar;
import com.waka.workspace.wakapedometer.database.DBHelper;

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

        //圆形进度条栏，用来显示步数
        cardViewProgressBar = (CardView) view.findViewById(R.id.cardview_progressbar);
        layoutProgressBar = (LinearLayout) view.findViewById(R.id.layoutProgressBar);
        roundProgressBar = (RoundProgressBar) view.findViewById(R.id.roundProgressBar);
        etMaxStep = (EditText) view.findViewById(R.id.etMaxStep);
        fabIsShowEtMaxStep = (FloatingActionButton) view.findViewById(R.id.fab_is_show_et_maxstep);

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

        //获得布局原来的高度
        layoutHeightOriginal = layoutProgressBar.getHeight();

        //设置向下标志，用来判断该指上还是指下
        fabIsShowEtMaxStep.setTag(FAB_DOWN);
    }

    /**
     * initEvent
     */
    private void initEvent() {

        //FAB点击事件
        fabIsShowEtMaxStep.setOnClickListener(this);
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
