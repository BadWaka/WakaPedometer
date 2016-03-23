package com.waka.workspace.wakapedometer.database.bean;

import java.sql.Date;

/**
 * 步数信息
 * Created by waka on 2016/2/2.
 */
public class StepInfoBean {
    private int step;//步数
    private int personId;//人员id
    private Date date;//日期

    /**
     * get set 方法
     */
    public int getStep() {
        return step;
    }

    public void setStep(int step) {
        this.step = step;
    }

    public int getPersonId() {
        return personId;
    }

    public void setPersonId(int personId) {
        this.personId = personId;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
