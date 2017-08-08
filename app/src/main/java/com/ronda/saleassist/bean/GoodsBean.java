package com.ronda.saleassist.bean;

import java.util.List;

/**
 * Author: Ronda(1575558177@qq.com)
 * Date: 2017/08/08
 * Version: v1.0
 */

public class GoodsBean {

    /**
     * status : 1
     * msg : 店铺货物获取成功
     * data : [{"goods":"10828","orderid":"0","no":"2016092020","name":"青菜","intro":"","price":"2","category":{"id":"100006","name":"分类1","notes":"分类notes","shopid":"100001"},"picurl":"/market/Public/upload/file/20160920/1474373846754101.png","stock":"0","discount1":"1","discount2":"0.98","discount3":"0.98","bargoods":"0","intime":null,"checkstatus":"20161224318","method":"1","unit":"元/个"}]
     * count : 1
     * page : 1
     * allcount : 11
     */

    private int status;
    private String msg;
    private String count;
    private String page;
    private String allcount;
    /**
     * goods : 10828
     * orderid : 0
     * no : 2016092020
     * name : 青菜
     * intro :
     * price : 2
     * category : {"id":"100006","name":"分类1","notes":"分类notes","shopid":"100001"}
     * picurl : /market/Public/upload/file/20160920/1474373846754101.png
     * stock : 0
     * discount1 : 1
     * discount2 : 0.98
     * discount3 : 0.98
     * bargoods : 0
     * intime : null
     * checkstatus : 20161224318
     * method : 1
     * unit : 元/个
     */

    private List<SubCategory> data;

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

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public String getPage() {
        return page;
    }

    public void setPage(String page) {
        this.page = page;
    }

    public String getAllcount() {
        return allcount;
    }

    public void setAllcount(String allcount) {
        this.allcount = allcount;
    }

    public List<SubCategory> getData() {
        return data;
    }

    public void setData(List<SubCategory> data) {
        this.data = data;
    }

}
