package com.ronda.saleassist.bean;

/**
 * Author: Ronda(1575558177@qq.com)
 * Date: 2017/08/19
 * Version: v1.0
 */

public class PriceEvent {
    private String price;

    public PriceEvent() {
    }

    public PriceEvent(String price) {
        this.price = price;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }
}
