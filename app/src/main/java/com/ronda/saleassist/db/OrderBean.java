package com.ronda.saleassist.db;

/**
 * 订单的实体类
 *
 * Author: Ronda(1575558177@qq.com)
 * Date: 2016/11/24
 * Version: v1.0
 */

public class OrderBean {
    public int _id;
    public String shopid;
    public String total;
    public String paycode;
    public String method;
    public String customer;
    public String data;

    public String usermobile;
//    public String operatetime;


//    public String printhexdata; //要打印的订单数据

    public OrderBean(String usermobile, String shopid, String total, String paycode, String method, String customer, String data) {
        this.usermobile = usermobile;
        this.shopid = shopid;
        this.total = total;
        this.paycode = paycode;
        this.method = method;
        this.customer = customer;
        this.data = data;
//        this.printhexdata = printHexData;
    }

    // 从SQLite 中查询到的完整的数据
    public OrderBean(int _id, String usermobile, String shopid, String total, String paycode, String method, String customer, String data) {
        this._id = _id;
        this.usermobile = usermobile;
        this.shopid = shopid;
        this.total = total;
        this.paycode = paycode;
        this.method = method;
        this.customer = customer;
        this.data = data;
//        this.printhexdata = printhexdata;
    }

    @Override
    public String toString() {
        return "OrderBean{" +
                "_id=" + _id +
                ", shopid='" + shopid + '\'' +
                ", total='" + total + '\'' +
                ", paycode='" + paycode + '\'' +
                ", method='" + method + '\'' +
                ", customer='" + customer + '\'' +
                ", data='" + data + '\'' +
//                ", printhexdata='" + printhexdata + '\'' +
                '}';
    }
}
