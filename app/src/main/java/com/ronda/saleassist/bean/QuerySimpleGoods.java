package com.ronda.saleassist.bean;

import com.ronda.saleassist.local.sqlite.table.SimpleGoodsBean;

import java.util.List;

/**
 * Created by ronda on 17-8-20/20.
 */

public class QuerySimpleGoods {

    /**
     * status : 1
     * msg : 店铺货物信息
     * data : [{"id":null,"name":"红富士","price":"0"}]
     */

    private int                   status;
    private String                msg;
    private List<SimpleGoodsBean> data;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public List<SimpleGoodsBean> getData() {
        return data;
    }

    public void setData(List<SimpleGoodsBean> data) {
        this.data = data;
    }
}
