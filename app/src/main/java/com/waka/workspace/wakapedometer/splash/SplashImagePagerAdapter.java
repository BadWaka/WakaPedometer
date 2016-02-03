package com.waka.workspace.wakapedometer.splash;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

/**
 * 图片PagerAdapter,Splash界面专用
 * Created by waka on 2016/2/3.
 */
public class SplashImagePagerAdapter extends PagerAdapter {

    private Context mContext;
    private int[] mImgIds;

    /**
     * 构造方法，需传入context和imgIds数组
     *
     * @param context
     * @param imgIds
     */
    public SplashImagePagerAdapter(Context context, int[] imgIds) {
        mContext = context;
        mImgIds = imgIds;
    }

    @Override
    public int getCount() {
        return mImgIds.length;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        ImageView imageView = new ImageView(mContext);
        imageView.setImageResource(mImgIds[position]);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        container.addView(imageView);
        return imageView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }
}
