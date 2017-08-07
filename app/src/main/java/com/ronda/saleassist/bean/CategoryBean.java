package com.ronda.saleassist.bean;

import java.util.List;

/**
 * Created by ronda on 17-8-6/06.
 */

public class CategoryBean {

    /**
     * data : [{"id":"100090","shopid":"100001","notes":"","status":"1","name":"水果4"}]
     * msg : 店铺商品种类信息获取成功
     * status : 1
     */

    private String msg;
    private int            status;
    private List<Category> data;

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public List<Category> getData() {
        return data;
    }

    public void setData(List<Category> data) {
        this.data = data;
    }

}
