package com.ronda.saleassist.bean;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * Author: Ronda(1575558177@qq.com)
 * Date: 2016/11/20
 * Version: v1.0
 */

public class MemberBean implements Serializable {

    /**
     "levelcost": "优惠1,",
     "name": "lcwang",
     "userid": "1",
     "levelname": "二星会员",
     "levelid": "7",
     "mobile": "13616551322"
     "money":100
     */

    private String userid;
    private String mobile;
    private String name;

    private String levelid;
    private String levelname;

    private String levelcost;//会员等级对应的优惠

    private String money;//会员余额

    private String bill; //0-->无挂账权限，1-->有挂账权限

    private List<CostBean> month;
    private List<CostBean> all;

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public List<CostBean> getMonth() {
        return month;
    }

    public void setMonth(List<CostBean> month) {
        this.month = month;
    }

    public List<CostBean> getAll() {
        return all;
    }

    public void setAll(List<CostBean> all) {
        this.all = all;
    }


    public double getMonthCost() {
        BigDecimal t = new BigDecimal("0");
        for (CostBean bean : month) {
            t = t.add(new BigDecimal(bean.getCost()));
        }
        return t.doubleValue();
    }

    public double getAllCost() {
        BigDecimal t = new BigDecimal("0");
        for (CostBean bean : all) {
            t = t.add(new BigDecimal(bean.getCost()));
        }
        return t.doubleValue();
    }

    public String getLevelid() {
        return levelid;
    }

    public void setLevelid(String levelid) {
        this.levelid = levelid;
    }

    public String getLevelname() {
        return levelname;
    }

    public void setLevelname(String levelname) {
        this.levelname = levelname;
    }

    public String getLevelcost() {
        return levelcost;
    }

    public void setLevelcost(String levelcost) {
        this.levelcost = levelcost;
    }

    public String getMoney() {
        return money;
    }

    public void setMoney(String money) {
        this.money = money;
    }

    public String getBill() {
        return bill;
    }

    public void setBill(String bill) {
        this.bill = bill;
    }

    public class CostBean implements Serializable {

        /**
         * status : 1
         * cost : 16967.55000698939
         */

        private String status;  //1现金2支付宝3会员11挂账未结账12挂账已结账
        private String cost; //单位元

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getCost() {
            return cost;
        }

        public void setCost(String cost) {
            this.cost = cost;
        }
    }

    @Override
    public String toString() {
        return "MemberBean{" +
                "userid='" + userid + '\'' +
                ", mobile='" + mobile + '\'' +
                ", name='" + name + '\'' +
                ", month=" + month +
                ", all=" + all +
                '}';
    }
}
