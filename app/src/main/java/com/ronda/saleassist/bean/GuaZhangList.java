package com.ronda.saleassist.bean;

/**
 * Created by Administrator on 2016/11/12.
 * 挂账人员列表，暴扣挂账人信息和挂账的总额
 */

public class GuaZhangList {
    private String customer;//买家id
    private String mobile;//手机号
    private String allcount;//总额

    public String getCustomer() {
        return customer;
    }

    public void setCustomer(String customer) {
        this.customer = customer;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getAllcount() {
        return allcount;
    }

    public void setAllcount(String allcount) {
        this.allcount = allcount;
    }
}
