package com.ronda.saleassist.bean;

/**
 * 用于销售记录中的订单详情
 *
 * Author: Ronda(1575558177@qq.com)
 * Date: 2016/12/09
 * Version: v1.0
 */

public class BillDetailBean {

    /**
     * uid : 8
     * orderprice : 1.55
     * extmethod : null
     * operatetime : 2016-12-07 00:00:00
     * costprice : 3.1
     * status : 3
     * no : 201612091244373961
     * operater : 刘荣达
     * goodinfo : {"categoryname":"分类1","photo":"/market/Public/upload/file/20160730/1469860896307957.png","name":"胡萝卜"}
     * number : 0.515
     * customer : 7
     * cost : 1
     * total_price : 3.1
     * id : 107267
     * goodid : 11
     * extpay : null
     * shopid : 100001
     * price : 3
     * autopay : 3.1
     * userorderno : 0
     * intime : 2016-12-09 12:44:37
     * goodcategory : 100006
     */

    private String uid;
    private String orderprice;
    private Object extmethod;
    private String operatetime;
    private String costprice;
    private String status;
    private String no;
    private String operater;
    /**
     * categoryname : 分类1
     * photo : /market/Public/upload/file/20160730/1469860896307957.png
     * name : 胡萝卜
     */

    private GoodinfoBean goodinfo;
    private String number;
    private String customer;
    private String cost;
    private String total_price;
    private String id;
    private String goodid;
    private Object extpay;
    private String shopid;
    private String price;
    private String autopay;
    private String userorderno;
    private String intime;
    private String goodcategory;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getOrderprice() {
        return orderprice;
    }

    public void setOrderprice(String orderprice) {
        this.orderprice = orderprice;
    }

    public Object getExtmethod() {
        return extmethod;
    }

    public void setExtmethod(Object extmethod) {
        this.extmethod = extmethod;
    }

    public String getOperatetime() {
        return operatetime;
    }

    public void setOperatetime(String operatetime) {
        this.operatetime = operatetime;
    }

    public String getCostprice() {
        return costprice;
    }

    public void setCostprice(String costprice) {
        this.costprice = costprice;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getNo() {
        return no;
    }

    public void setNo(String no) {
        this.no = no;
    }

    public String getOperater() {
        return operater;
    }

    public void setOperater(String operater) {
        this.operater = operater;
    }

    public GoodinfoBean getGoodinfo() {
        return goodinfo;
    }

    public void setGoodinfo(GoodinfoBean goodinfo) {
        this.goodinfo = goodinfo;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getCustomer() {
        return customer;
    }

    public void setCustomer(String customer) {
        this.customer = customer;
    }

    public String getCost() {
        return cost;
    }

    public void setCost(String cost) {
        this.cost = cost;
    }

    public String getTotal_price() {
        return total_price;
    }

    public void setTotal_price(String total_price) {
        this.total_price = total_price;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getGoodid() {
        return goodid;
    }

    public void setGoodid(String goodid) {
        this.goodid = goodid;
    }

    public Object getExtpay() {
        return extpay;
    }

    public void setExtpay(Object extpay) {
        this.extpay = extpay;
    }

    public String getShopid() {
        return shopid;
    }

    public void setShopid(String shopid) {
        this.shopid = shopid;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getAutopay() {
        return autopay;
    }

    public void setAutopay(String autopay) {
        this.autopay = autopay;
    }

    public String getUserorderno() {
        return userorderno;
    }

    public void setUserorderno(String userorderno) {
        this.userorderno = userorderno;
    }

    public String getIntime() {
        return intime;
    }

    public void setIntime(String intime) {
        this.intime = intime;
    }

    public String getGoodcategory() {
        return goodcategory;
    }

    public void setGoodcategory(String goodcategory) {
        this.goodcategory = goodcategory;
    }

    public static class GoodinfoBean {
        private String categoryname;
        private String photo;
        private String name;

        public String getCategoryname() {
            return categoryname;
        }

        public void setCategoryname(String categoryname) {
            this.categoryname = categoryname;
        }

        public String getPhoto() {
            return photo;
        }

        public void setPhoto(String photo) {
            this.photo = photo;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}
