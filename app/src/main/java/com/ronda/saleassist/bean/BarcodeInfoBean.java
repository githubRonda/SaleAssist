package com.ronda.saleassist.bean;

/**
 * Created by lrd on 0022,2016/9/22.
 */
public class BarcodeInfoBean {
    private String name;
    private String barcode;
    private String price;
    private String goodid;

    public BarcodeInfoBean(String name, String barcode, String price, String goodid) {
        this.name = name;
        this.barcode = barcode;
        this.price = price;
        this.goodid = goodid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getGoodid() {
        return goodid;
    }

    public void setGoodid(String goodid) {
        this.goodid = goodid;
    }
}
