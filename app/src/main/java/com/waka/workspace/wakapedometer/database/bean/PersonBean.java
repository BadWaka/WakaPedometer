package com.waka.workspace.wakapedometer.database.bean;

/**
 * 人员类
 * Created by waka on 2016/2/2.
 */
public class PersonBean {

    private int id;//人员id

    //注册必填
    private String account;//账号
    private String password;//密码
    private String nickName;//昵称

    private String name;//真实姓名
    private int sex;//性别
    private int age;//年龄
    private float height;//身高
    private float weight;//体重
    private String headIconUrl;//头像地址

    /**get set 方法*/

    /**
     * 必填
     */
    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    /**
     * 自动生成
     */
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    /**
     * 可选
     */
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public float getHeight() {
        return height;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public float getWeight() {
        return weight;
    }

    public void setWeight(float weight) {
        this.weight = weight;
    }

    public String getHeadIconUrl() {
        return headIconUrl;
    }

    public void setHeadIconUrl(String headIconUrl) {
        this.headIconUrl = headIconUrl;
    }

    /**
     * toString
     */
    public String toString() {
        String s = "id=" + id
                + "\n" + "name=" + name
                + "\n" + "nickname=" + nickName
                + "\n" + "sex=" + sex
                + "\n" + "age=" + age
                + "\n" + "height=" + height
                + "\n" + "weight=" + weight
                + "\n" + "account=" + account
                + "\n" + "password=" + password
                + "\n" + "headIconUrl=" + headIconUrl;
        return s;
    }
}
