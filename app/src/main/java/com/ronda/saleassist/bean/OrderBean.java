package com.ronda.saleassist.bean;

/**
 * Created by lrd on 0031,2016/7/31.
 */
public class OrderBean {
    private String no; //订单编号
    private String intime;  //入库时间
    private String businessnum;  //订单包含货物件数
    private String income;  //订单总额
    private String goodnum;  //货物总重


    public OrderBean() {
    }


    public OrderBean(String no, String intime, String businessnum, String income, String goodnum) {
        this.no = no;
        this.intime = intime;
        this.businessnum = businessnum;
        this.income = income;
        this.goodnum = goodnum;
    }

    public String getNo() {
        return no;
    }

    public void setNo(String no) {
        this.no = no;
    }

    public String getIntime() {
        return intime;
    }

    public void setIntime(String intime) {
        this.intime = intime;
    }

    public String getBusinessnum() {
        return businessnum;
    }

    public void setBusinessnum(String businessnum) {
        this.businessnum = businessnum;
    }

    public String getIncome() {
        return income;
    }

    public void setIncome(String income) {
        this.income = income;
    }

    public String getGoodnum() {
        return goodnum;
    }

    public void setGoodnum(String goodnum) {
        this.goodnum = goodnum;
    }
}
