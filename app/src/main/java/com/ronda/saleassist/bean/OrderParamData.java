package com.ronda.saleassist.bean;

/**
 * 订单上传时，构建data参数的实体类
 *
 * Author: Ronda(1575558177@qq.com)
 * Date: 2016/12/06
 * Version: v1.0
 */

public class OrderParamData {
    private String number;
    private String goodid;
    private String price;
    private String cost; //折扣，不是总价,无折扣时为1
    private String orderprice; //这个是重量*单价*折扣

    public OrderParamData(String number, String goodid, String price, String cost, String orderprice) {
        this.number = number;
        this.goodid = goodid;
        this.price = price;
        this.cost = cost;
        this.orderprice = orderprice;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getGoodid() {
        return goodid;
    }

    public void setGoodid(String goodid) {
        this.goodid = goodid;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getCost() {
        return cost;
    }

    public void setCost(String cost) {
        this.cost = cost;
    }

    public String getOrderprice() {
        return orderprice;
    }

    public void setOrderprice(String orderprice) {
        this.orderprice = orderprice;
    }
}
