package com.ronda.saleassist.bean;

import java.io.Serializable;

/**
 * Created by lrd on 0018,2016/9/18.
 */
public class GoodsStatisticBean implements Serializable {
    private String goodId;
    private String goodsName; //商品名
    private String goodsNum; //总量
    private String income; //总收入
    private String businessnum;//交易笔数

    public GoodsStatisticBean(String goodId, String goodsName, String goodsNum, String income, String businessnum) {
        this.goodId = goodId;
        this.goodsName = goodsName;
        this.goodsNum = goodsNum;
        this.income = income;
        this.businessnum = businessnum;
    }

    public String getGoodId() {
        return goodId;
    }

    public void setGoodId(String goodId) {
        this.goodId = goodId;
    }

    public String getGoodsName() {
        return goodsName;
    }

    public void setGoodsName(String goodsName) {
        this.goodsName = goodsName;
    }

    public String getGoodsNum() {
        return goodsNum;
    }

    public void setGoodsNum(String goodsNum) {
        this.goodsNum = goodsNum;
    }

    public String getIncome() {
        return income;
    }

    public void setIncome(String income) {
        this.income = income;
    }

    public String getBusinessnum() {
        return businessnum;
    }

    public void setBusinessnum(String businessnum) {
        this.businessnum = businessnum;
    }

    @Override
    public String toString() {
        return "GoodsStatisticBean{" +
                "goodId='" + goodId + '\'' +
                ", goodsName='" + goodsName + '\'' +
                ", goodsNum='" + goodsNum + '\'' +
                ", income='" + income + '\'' +
                ", businessnum='" + businessnum + '\'' +
                '}';
    }
}
