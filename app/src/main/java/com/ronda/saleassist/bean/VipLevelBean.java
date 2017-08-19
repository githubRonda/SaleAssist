package com.ronda.saleassist.bean;

import java.io.Serializable;
import java.util.Arrays;

/**
 * Author: Ronda(1575558177@qq.com)
 * Date: 2016/11/19
 * Version: v1.0
 */

public class VipLevelBean implements Serializable {

    /**
     * id : 7
     * shopid : 100001
     * updatetime : null
     * status : 1
     * name : 一星
     * orderno : 1
     * display : 1
     * cost : 24,27,28
     * inputtime : 2016-11-19 20:17:05
     * intro : 暂无介绍
     */

    private String id;
    private String shopid;
    private String updatetime;
    private String status;
    private String name;
    private String orderno;
    private String display;
    private String cost;
    private String inputtime;
    private String intro;

    private PreferenceBean[] allcost;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getShopid() {
        return shopid;
    }

    public void setShopid(String shopid) {
        this.shopid = shopid;
    }

    public Object getUpdatetime() {
        return updatetime;
    }

    public void setUpdatetime(String updatetime) {
        this.updatetime = updatetime;
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

    public String getOrderno() {
        return orderno;
    }

    public void setOrderno(String orderno) {
        this.orderno = orderno;
    }

    public String getDisplay() {
        return display;
    }

    public void setDisplay(String display) {
        this.display = display;
    }

    public String getCost() {
        return cost;
    }

    public void setCost(String cost) {
        this.cost = cost;
    }

    public String getInputtime() {
        return inputtime;
    }

    public void setInputtime(String inputtime) {
        this.inputtime = inputtime;
    }

    public String getIntro() {
        return intro;
    }

    public void setIntro(String intro) {
        this.intro = intro;
    }

    public PreferenceBean[] getAllcost() {
        return allcost;
    }

    public void setAllcost(PreferenceBean[] allcost) {
        this.allcost = allcost;
    }

    @Override
    public String toString() {
        return "VipLevelBean{" +
                "id='" + id + '\'' +
                ", shopid='" + shopid + '\'' +
                ", updatetime=" + updatetime +
                ", status='" + status + '\'' +
                ", name='" + name + '\'' +
                ", orderno='" + orderno + '\'' +
                ", display='" + display + '\'' +
                ", cost='" + cost + '\'' +
                ", inputtime='" + inputtime + '\'' +
                ", intro='" + intro + '\'' +
                ", allcost=" + Arrays.toString(allcost) +
                '}';
    }

//    @Override
//    public int describeContents() {
//        return 0;
//    }
//
//    @Override
//    public void writeToParcel(Parcel dest, int flags) {
//        dest.writeString(id);
//        dest.writeString(shopid);
//        dest.writeString(updatetime);
//        dest.writeString(status);
//        dest.writeString(name);
//        dest.writeString(orderno);
//        dest.writeString(display);
//        dest.writeString(cost);
//        dest.writeString(inputtime);
//        dest.writeString(intro);
//    }
//
//    public static final Parcelable.Creator<VipLevelBean> CREATOR = new Creator<VipLevelBean>() {
//        @Override
//        public VipLevelBean[] newArray(int size) {
//            return new VipLevelBean[size];
//        }
//
//        @Override
//        public VipLevelBean createFromParcel(Parcel in) {
//            return new VipLevelBean(in);
//        }
//    };
//
//    public VipLevelBean(Parcel in) {
//
//        id = in.readString();
//        shopid= in.readString();
//        updatetime= in.readString();
//        status= in.readString();
//        name= in.readString();
//        orderno= in.readString();
//        display= in.readString();
//        cost= in.readString();
//        inputtime= in.readString();
//        intro= in.readString();
//    }
}
