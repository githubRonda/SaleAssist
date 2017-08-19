package com.ronda.saleassist.bean;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/11/13.
 */

public class Allinfo implements Serializable {
    private Good goodinfo;
    private Category category;
    private String no;
    private String orderprice;//该货物交易金额
    private String total_price;//订单交易总额
    private String costprice;//优惠后总额
    private String number;
    private String price;

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public Good getGoodinfo() {
        return goodinfo;
    }

    public void setGoodinfo(Good goodinfo) {
        this.goodinfo = goodinfo;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public String getNo() {
        return no;
    }

    public void setNo(String no) {
        this.no = no;
    }

    public String getOrderprice() {
        return orderprice;
    }

    public void setOrderprice(String orderprice) {
        this.orderprice = orderprice;
    }

    public String getTotal_price() {
        return total_price;
    }

    public void setTotal_price(String total_price) {
        this.total_price = total_price;
    }

    public String getCostprice() {
        return costprice;
    }

    public void setCostprice(String costprice) {
        this.costprice = costprice;
    }


    @Override
    public String toString() {
        return "Allinfo{" +
                "goodinfo=" + goodinfo +
                ", goodcategoryinfo=" + category +
                ", no='" + no + '\'' +
                ", orderprice='" + orderprice + '\'' +
                ", total_price='" + total_price + '\'' +
                ", costprice='" + costprice + '\'' +
                ", number='" + number + '\'' +
                '}';
    }
}
