package com.ronda.saleassist.bean;

import java.io.Serializable;

/**
 * Author: Ronda(1575558177@qq.com)
 * Date: 2016/11/29
 * Version: v1.0
 *
 * 买家端下的订单的列表
 */

public class BuyerOrderList implements Serializable {

    /**
     * createtime : 2016-11-28 19:37:16
     * uid : 1
     * customer : {"nick":"lcwang","mobile":"13616551322"}
     * usercheck : 1
     * status : 1
     * total_price : 7
     * no : 201611281937166955
     * shopcostprice :0.89 卖家称重后，实际的总额。 若总额值未变，则该值为0
     * costprice: 6
     *
     * newOrder:true;
     * waitSend:false
     */

    private String createtime;
    private String uid;
    private String usercheck;
    private String status;
    private String total_price;
    private String no;

    private String shopcostprice;
    private String costprice;

    //只需要获取
//    private boolean newOrder = true; //用于显示开始配货按钮和取消配货按钮
//    private boolean waitSend = false; //用于显示发送按钮

    /**
     * nick : lcwang
     * mobile : 13616551322
     */

    private CustomerBean customer;



    public String getCreatetime() {
        return createtime;
    }

    public void setCreatetime(String createtime) {
        this.createtime = createtime;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public CustomerBean getCustomer() {
        return customer;
    }

    public void setCustomer(CustomerBean customer) {
        this.customer = customer;
    }

    public String getUsercheck() {
        return usercheck;
    }

    public void setUsercheck(String usercheck) {
        this.usercheck = usercheck;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTotal_price() {
        return total_price;
    }

    public void setTotal_price(String total_price) {
        this.total_price = total_price;
    }

    public String getNo() {
        return no;
    }

    public void setNo(String no) {
        this.no = no;
    }

    public boolean isNewOrder() {
        return "1".equals(status);
    }

    public String getShopcostprice() {
        return shopcostprice;
    }

    public void setShopcostprice(String shopcostprice) {
        this.shopcostprice = shopcostprice;
    }

    public boolean isWaitSend() {
        return "4".equals(status);
    }


    public boolean isDuringPeiHuo(){
        return "2".equals(status);
    }

    public String getCostprice() {
        return costprice;
    }

    public void setCostprice(String costprice) {
        this.costprice = costprice;
    }

    public static class CustomerBean implements Serializable {
        private String nick;
        private String mobile;

        public String getNick() {
            return nick;
        }

        public void setNick(String nick) {
            this.nick = nick;
        }

        public String getMobile() {
            return mobile;
        }

        public void setMobile(String mobile) {
            this.mobile = mobile;
        }
    }
}
