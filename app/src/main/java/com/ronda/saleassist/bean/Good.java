package com.ronda.saleassist.bean;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/11/13.
 */

public class Good implements Serializable {
    private String name;
    private String picurl;
    private String price;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPicurl() {
        return picurl;
    }

    public void setPicurl(String picurl) {
        this.picurl = picurl;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }
}
