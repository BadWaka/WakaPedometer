package com.waka.workspace.wakapedometer.customview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;

import com.waka.workspace.wakapedometer.R;

/**
 * 自定义圆形进度条
 * <p/>
 * Created by waka on 2016/2/29.
 */
public class RoundProgressBar extends View {

    private static final String TAG = "RoundProgressBar";

    private int progress;//当前进度

    private Paint mPaint;//画笔

    //自定义属性
    private int roundColor;//圆环颜色
    private int roundProgressColor;//圆环进度颜色
    private float roundWidth;//圆环宽度
    private int textColor;//字体颜色
    private float textSize;//字体大小
    private int max;//最大值


    public RoundProgressBar(Context context) {
        this(context, null);
    }

    public RoundProgressBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    /**
     * 真正的构造方法
     *
     * @param context
     * @param attrs
     * @param defStyleAttr
     */
    public RoundProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mPaint = new Paint();

        //获得typedArray
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.RoundProgressBar);

        //获得自定义属性和默认值
        roundColor = typedArray.getColor(R.styleable.RoundProgressBar_roundColor, Color.BLACK);
        roundProgressColor = typedArray.getColor(R.styleable.RoundProgressBar_roundProgressColor, Color.BLUE);
        roundWidth = typedArray.getDimension(R.styleable.RoundProgressBar_roundWidth, 8);
        textColor = typedArray.getColor(R.styleable.RoundProgressBar_textColor, Color.BLUE);
        textSize = typedArray.getDimension(R.styleable.RoundProgressBar_textSize, 16);
        max = typedArray.getInteger(R.styleable.RoundProgressBar_max, 10000);

        typedArray.recycle();//一定要回收typedArray
    }

    /**
     * 重写onDraw
     *
     * @param canvas
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        //画最外层的大圆
        int center = getWidth() / 2;//获取圆心的x坐标
        int radius = (int) (center - roundWidth / 2);//圆环半径
        mPaint.setColor(roundColor);
        mPaint.setStyle(Paint.Style.STROKE);//空心
        mPaint.setStrokeWidth(roundWidth);//圆环宽度
        mPaint.setAntiAlias(true);//抗锯齿
        canvas.drawCircle(center, center, radius, mPaint);//画圆环

        //画中间文字
        mPaint.setStrokeWidth(0);
        mPaint.setColor(textColor);
        mPaint.setTextSize(textSize);
        mPaint.setTypeface(Typeface.DEFAULT_BOLD);//设置字体
        float textWidth = mPaint.measureText(progress + "步");
        canvas.drawText(progress + "步", center - textWidth / 2, center + textSize / 2, mPaint);//画出文字

        //画圆弧，画圆环的进度
        mPaint.setStrokeWidth(roundWidth);
        mPaint.setColor(roundProgressColor);
        RectF rectF = new RectF(center - radius, center - radius, center + radius, center + radius);//用于定义的圆弧的形状和大小的界限
        canvas.drawArc(rectF, 0, 360 * progress / max, false, mPaint);//画圆弧
    }

    public synchronized int getMax() {
        return max;
    }

    public synchronized void setMax(int max) {

        if (max < 0) {
            throw new IllegalArgumentException("max not less than 0");
        }

        this.max = max;
    }

    public synchronized int getProgress() {
        return progress;
    }

    /**
     * 设置进度，此为线程安全控件，由于考虑多线的问题，需要同步
     * 刷新界面调用postInvalidate()能在非UI线程刷新
     *
     * @param progress
     */
    public synchronized void setProgress(int progress) {

        if (progress < 0) {
            throw new IllegalArgumentException("progress not less than 0");
        }

        if (progress > max) {
            progress = max;
            return;
        }

        this.progress = progress;
        postInvalidate();
    }

    public int getRoundColor() {
        return roundColor;
    }

    public void setRoundColor(int roundColor) {
        this.roundColor = roundColor;
    }

    public int getRoundProgressColor() {
        return roundProgressColor;
    }

    public void setRoundProgressColor(int roundProgressColor) {
        this.roundProgressColor = roundProgressColor;
    }

    public float getRoundWidth() {
        return roundWidth;
    }

    public void setRoundWidth(float roundWidth) {
        this.roundWidth = roundWidth;
    }

    public int getTextColor() {
        return textColor;
    }

    public void setTextColor(int textColor) {
        this.textColor = textColor;
    }

    public float getTextSize() {
        return textSize;
    }

    public void setTextSize(float textSize) {
        this.textSize = textSize;
    }

}
