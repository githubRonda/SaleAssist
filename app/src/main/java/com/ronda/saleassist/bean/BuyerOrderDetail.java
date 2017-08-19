package com.ronda.saleassist.bean;

/**
 * Author: Ronda(1575558177@qq.com)
 * Date: 2016/11/29
 * Version: v1.0
 */

public class BuyerOrderDetail {

    /**
     * uid : 2
     * orderprice : 36.6
     * expectarrivetime : null
     * operatetime : 2016-11-29 14:27:58
     * realprice : 0
     * shopcostprice : 0
     * no : 201611291427589839
     * costorderprice : 0
     * createtime : 2016-11-29 14:27:58
     * id : 362
     * shopid : 100001
     * deliveryfee : 0
     * receiptaddress : 浙江理工大学 6s211
     * 陈旺均 先生 15858184708
     * usercheck : 1
     * paymethod : cash
     * goodcategoryid : 100006
     * realnumber : 0
     * costprice : 3
     * status : 1
     * picurl : /market/Public/upload/file/20160729/1469803792253064.png
     * number : 2
     * cost : 3
     * total_price : 36.6
     * arrivetime : null
     * goodsname : 大白菜
     * message :
     * goodid : 6
     * price : 1
     * newtotalprice : 0
     *
     * method : 0
     * unit : 元/kg
     * <p>
     * <p>
     * <p>
     * checked:false
     *
     */

    private String uid;
    private String orderprice;
    private String expectarrivetime;
    private String operatetime;
    private String realprice;
    private String shopcostprice;
    private String no;
    private String costorderprice;
    private String createtime;
    private String id;
    private String shopid;
    private String deliveryfee;
    private String receiptaddress;
    private String usercheck;
    private String paymethod;
    private String goodcategoryid;
    private String realnumber;
    private String costprice;
    private String status;
    private String picurl;
    private String number;
    private String cost;
    private String total_price;
    private String arrivetime;
    private String goodsname;
    private String message;
    private String goodid;
    private String price;
    private String newtotalprice;
    private int method; //用于标识是称重类还是计件类（0表示称重类，1表示计件类）
    private String unit;

    private boolean checked = false;
    private boolean enable = true;

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

    public Object getExpectarrivetime() {
        return expectarrivetime;
    }

    public void setExpectarrivetime(String expectarrivetime) {
        this.expectarrivetime = expectarrivetime;
    }

    public String getOperatetime() {
        return operatetime;
    }

    public void setOperatetime(String operatetime) {
        this.operatetime = operatetime;
    }

    public String getRealprice() {
        return realprice;
    }

    public void setRealprice(String realprice) {
        this.realprice = realprice;
    }

    public String getShopcostprice() {
        return shopcostprice;
    }

    public void setShopcostprice(String shopcostprice) {
        this.shopcostprice = shopcostprice;
    }

    public String getNo() {
        return no;
    }

    public void setNo(String no) {
        this.no = no;
    }

    public String getCostorderprice() {
        return costorderprice;
    }

    public void setCostorderprice(String costorderprice) {
        this.costorderprice = costorderprice;
    }

    public String getCreatetime() {
        return createtime;
    }

    public void setCreatetime(String createtime) {
        this.createtime = createtime;
    }

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

    public String getDeliveryfee() {
        return deliveryfee;
    }

    public void setDeliveryfee(String deliveryfee) {
        this.deliveryfee = deliveryfee;
    }

    public String getReceiptaddress() {
        return receiptaddress;
    }

    public void setReceiptaddress(String receiptaddress) {
        this.receiptaddress = receiptaddress;
    }

    public String getUsercheck() {
        return usercheck;
    }

    public void setUsercheck(String usercheck) {
        this.usercheck = usercheck;
    }

    public String getPaymethod() {
        return paymethod;
    }

    public void setPaymethod(String paymethod) {
        this.paymethod = paymethod;
    }

    public String getGoodcategoryid() {
        return goodcategoryid;
    }

    public void setGoodcategoryid(String goodcategoryid) {
        this.goodcategoryid = goodcategoryid;
    }

    public String getRealnumber() {
        return realnumber;
    }

    public void setRealnumber(String realnumber) {
        this.realnumber = realnumber;
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

    public String getPicurl() {
        return null == picurl ? null : picurl.replace("\\", "");
    }

    public void setPicurl(String picurl) {
        this.picurl = picurl;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
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

    public Object getArrivetime() {
        return arrivetime;
    }

    public void setArrivetime(String arrivetime) {
        this.arrivetime = arrivetime;
    }

    public String getGoodsname() {
        return goodsname;
    }

    public void setGoodsname(String goodsname) {
        this.goodsname = goodsname;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
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

    public String getNewtotalprice() {
        return newtotalprice;
    }

    public void setNewtotalprice(String newtotalprice) {
        this.newtotalprice = newtotalprice;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public int getMethod() {
        return method;
    }

    public void setMethod(int method) {
        this.method = method;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }
}
