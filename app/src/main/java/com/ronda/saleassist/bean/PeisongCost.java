package com.ronda.saleassist.bean;

/**
 * 获取配送费信息
 *
 * Author: Ronda(1575558177@qq.com)
 * Date: 2016/12/10
 * Version: v1.0
 */

public class PeisongCost {

    /**
     * min : 10
     * id : 1
     * shopid : 100001
     * status : 1
     * cost : 5
     */

    private String min;
    private String id;
    private String shopid;
    private String status;
    private String cost;

    public String getMin() {
        return min;
    }

    public void setMin(String min) {
        this.min = min;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCost() {
        return cost;
    }

    public void setCost(String cost) {
        this.cost = cost;
    }
}
