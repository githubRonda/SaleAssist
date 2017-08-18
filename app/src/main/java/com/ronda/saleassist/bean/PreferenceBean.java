package com.ronda.saleassist.bean;

import java.io.Serializable;

/**
 * author: Ronda(1575558177@qq.com)
 * Date: 2016/11/18
 * Version: v1.0
 *
 * 优惠项的JavaBean。
 * 在优惠项设置(VipPreferenceActivity)和会员等级设置(VipLevelActivity)中都有用到
 */

public class PreferenceBean implements Serializable {

    /**
     * min : 100
     * id : 4
     * gift : 0
     * shopid : 100001
     * status : 1
     * name :
     * score : 0
     * commission : 5
     * cost : 0
     * intro :
     * costtype : 1
     */

    private String min;
    private String id;
    private String gift;
    private String shopid;
    private String status;
    private String name;
    private String score;
    private String commission;
    private String cost;
    private String intro;
    private String costtype;

    private boolean isChecked = false; //添加会员等级的时候用于多选

    public String getMin() {
        return min;
    }

    public void setMin(String min) {
        this.min = min;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getGift() {
        return gift;
    }

    public void setGift(String gift) {
        this.gift = gift;
    }

    public String getShopid() {
        return shopid;
    }

    public void setShopid(String shopid) {
        this.shopid = shopid;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public String getCommission() {
        return commission;
    }

    public void setCommission(String commission) {
        this.commission = commission;
    }

    public String getCost() {
        return cost;
    }

    public void setCost(String cost) {
        this.cost = cost;
    }

    public String getIntro() {
        return intro;
    }

    public void setIntro(String intro) {
        this.intro = intro;
    }

    public String getCosttype() {
        return costtype;
    }

    public void setCosttype(String costtype) {
        this.costtype = costtype;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    @Override
    public String toString() {
        return "PreferenceBean{" +
                "min='" + min + '\'' +
                ", id='" + id + '\'' +
                ", gift='" + gift + '\'' +
                ", shopid='" + shopid + '\'' +
                ", status='" + status + '\'' +
                ", name='" + name + '\'' +
                ", score='" + score + '\'' +
                ", commission='" + commission + '\'' +
                ", cost='" + cost + '\'' +
                ", intro='" + intro + '\'' +
                ", costtype='" + costtype + '\'' +
                '}';
    }
}
