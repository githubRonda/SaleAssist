package com.ronda.saleassist.bean;

/**
 * 货物顺序修改上传时的实体类
 *
 * Author: Ronda(1575558177@qq.com)
 * Date: 2017/01/02
 * Version: v1.0
 */

public class GoodsOrder {
    private String gid;
    private int orderid;

    public GoodsOrder(String gid, int orderid) {
        this.gid = gid;
        this.orderid = orderid;
    }

    public String getGid() {
        return gid;
    }

    public void setGid(String gid) {
        this.gid = gid;
    }

    public int getOrderid() {
        return orderid;
    }

    public void setOrderid(int orderid) {
        this.orderid = orderid;
    }
}
