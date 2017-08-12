package com.ronda.saleassist.bean;

import android.text.TextUtils;

import java.io.Serializable;

/**
 * Created by Dragon on 2016-3-21.
 */
public class SubCategory implements Serializable {


    /**
     * bargoods : 0
     * no : 2016100500
     * goods : 10867
     * picurl : /market/Public/upload/file/20161005/1475597691977932.png
     * intro :
     * discount2 : 0.95
     * unit : 公斤
     * discount1 : 1
     * category : {"notes":"分类notes","id":"100006","shopid":"100001","name":"分类1"}
     * discount3 : 0
     * stock : 0
     * price : 2.5
     * checkstatus : 20161205909
     * name : 苦瓜
     * method : 0
     * intime : null
     */

    private boolean isMoving; //移动菜品的标志

    private String bargoods;
    private String no;
    private String goods;
    private String name;
    private String price;
    private String picurl;
    private String intro;
    private String discount1; //折扣
    private String discount2;
    private String discount3;
    private String method;
    private String unit;
    private String checkstatus; //盘库标志
    private String intime; //入库时间
    private String stock;  //库存


    /**
     * notes : 分类notes
     * id : 100006
     * shopid : 100001
     * name : 分类1
     */
    private Category category;

    public boolean isMoving() {
        return isMoving;
    }

    public void setMoving(boolean moving) {
        isMoving = moving;
    }

    public String getBargoods() {
        return bargoods;
    }

    public void setBargoods(String bargoods) {
        this.bargoods = bargoods;
    }

    public String getNo() {
        return no;
    }

    public void setNo(String no) {
        this.no = no;
    }

    public String getGoods() {
        return goods;
    }

    public void setGoods(String goods) {
        this.goods = goods;
    }

    public String getPicurl() {
        if (null == picurl) {
            return null;
        }
        return picurl.replace("\\", "");
    }

    public void setPicurl(String picurl) {
        this.picurl = picurl;
    }

    public String getIntro() {
        return intro;
    }

    public void setIntro(String intro) {
        this.intro = intro;
    }

    public String getDiscount2() {
        return discount2;
    }

    public void setDiscount2(String discount2) {
        this.discount2 = discount2;
    }

    public String getUnit() {
        if (null == unit) {
            return "";
        }
        return unit.replace("\\", "");
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getDiscount1() {
        return discount1;
    }

    public void setDiscount1(String discount1) {
        this.discount1 = discount1;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public String getDiscount3() {
        return discount3;
    }

    public void setDiscount3(String discount3) {
        this.discount3 = discount3;
    }

    public String getStock() {
        return stock;
    }

    public void setStock(String stock) {
        this.stock = stock;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getCheckstatus() {
        return checkstatus;
    }

    public void setCheckstatus(String checkstatus) {
        this.checkstatus = checkstatus;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getIntime() {
        return intime;
    }

    public void setIntime(String intime) {
        this.intime = intime;
    }

    public boolean getDiscount1Enable() {
        if (discount1 == null || discount1.equals("0") || discount1.isEmpty())//真正判断的就是第二项是否等于0
            return false;
        return true;
    }

    public boolean getDiscount2Enable() {
        try {
            if (TextUtils.isEmpty(discount2) || Double.parseDouble(discount2) == 0)
                return false;
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public boolean getDiscount3Enable() {
        if (discount3 == null || discount3.equals("0") || discount3.isEmpty())
            return false;
        return true;
    }
}
