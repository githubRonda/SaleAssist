package com.ronda.saleassist.bean;

/**
 * User: Ronda(1575558177@qq.com)
 * Data: 2016/11/12
 * Version: v1.0
 */

public class PayEvent {
    private int payMethod;
    private String msg;

    public PayEvent(int payMethod, String msg) {
        this.payMethod = payMethod;
        this.msg = msg;
    }

    public int getPayMethod() {
        return payMethod;
    }

    public void setPayMethod(int payMethod) {
        this.payMethod = payMethod;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    @Override
    public String toString() {
        return "PayEvent{" +
                "payMethod=" + payMethod +
                ", msg='" + msg + '\'' +
                '}';
    }
}
