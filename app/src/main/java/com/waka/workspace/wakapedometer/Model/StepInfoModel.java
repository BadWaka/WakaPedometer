package com.waka.workspace.wakapedometer.Model;

import java.sql.Date;

/**
 * 步数信息
 * Created by waka on 2016/2/2.
 */
public class StepInfoModel {
    private int step;//步数
    private String personId;//人员id
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

    public String getPersonId() {
        return personId;
    }

    public void setPersonId(String personId) {
        this.personId = personId;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
