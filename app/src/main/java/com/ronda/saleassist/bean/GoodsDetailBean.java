package com.ronda.saleassist.bean;

/**
 * Created by lrd on 0020,2016/9/20.
 * 用于统计功能中的的最后一级的数据显示
 */
public class GoodsDetailBean {
    //private String orderId; //订单id

    private String GoodsName; //商品名
    private String price;//售价
    private String discount; //折扣
    private String goodsNumer;//销售量
    private String goodsCost; //该货物的总额 单价*总量*折扣
    private String orderNo; //订单编号
    private String orderCost; //订单总额
    private String payMethod; // status 付款方式（1：现金2：支付宝）
    private String operaterNickname; //收款人昵称

    public GoodsDetailBean(String goodsName, String price, String discount, String goodsNumer, String goodsCost, String orderNo, String orderCost, String payMethod, String operaterNickname) {
        GoodsName = goodsName;
        this.price = price;
        this.discount = discount;
        this.goodsNumer = goodsNumer;
        this.goodsCost = goodsCost;
        this.orderNo = orderNo;
        this.orderCost = orderCost;
        this.payMethod = payMethod;
        this.operaterNickname = operaterNickname;
    }

    public String getGoodsName() {
        return GoodsName;
    }

    public void setGoodsName(String goodsName) {
        GoodsName = goodsName;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }

    public String getGoodsNumer() {
        return goodsNumer;
    }

    public void setGoodsNumer(String goodsNumer) {
        this.goodsNumer = goodsNumer;
    }

    public String getGoodsCost() {
        return goodsCost;
    }

    public void setGoodsCost(String goodsCost) {
        this.goodsCost = goodsCost;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getOrderCost() {
        return orderCost;
    }

    public void setOrderCost(String orderCost) {
        this.orderCost = orderCost;
    }

    public String getPayMethod() {
        return payMethod;
    }

    public void setPayMethod(String payMethod) {
        this.payMethod = payMethod;
    }

    public String getOperaterNickname() {
        return operaterNickname;
    }

    public void setOperaterNickname(String operaterNickname) {
        this.operaterNickname = operaterNickname;
    }
}
