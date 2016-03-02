package com.waka.workspace.wakapedometer.main.pedometer;

import java.util.Observable;

/**
 * 步数被观察者
 * <p/>
 * Created by waka on 2016/3/2.
 */
public class StepObservable extends Observable {

    //单例
    private static StepObservable instance = null;

    public static StepObservable getInstance() {

        if (null == instance) {
            instance = new StepObservable();
        }

        return instance;
    }

    //通知观察者更新数据
    public void notifyStepChange(int step) {

        setChanged();
        notifyObservers(step);
    }

}
