package com.ronda.saleassist.bean;

/**
 * Created by ronda on 17-8-20/20.
 */

public class QueryOneGoods {

    /**
     * status : 1
     * msg : 货物信息
     * data : {"gid":"12121","orderid":"0","originid":"10522","idintro":"官方库同步","isbar":"0","stock":"0","no":"2016083016","category":"100138","name":"花菜","photo":"100135","cost":"1","price":"12","intro":"null","shopid":"101029","status":"0","discount1":"1","discount2":null,"discount3":null,"checkstatus":"20161102473","intime":null,"noticenum":"0","method":"0","unit":"元/kg"}
     */

    private int         status;
    private String      msg;
    private SubCategory data;

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

    public SubCategory getData() {
        return data;
    }

    public void setData(SubCategory data) {
        this.data = data;
    }

/*    public static class SubCategory {
        *//**
         * gid : 12121
         * orderid : 0
         * originid : 10522
         * idintro : 官方库同步
         * isbar : 0
         * stock : 0
         * no : 2016083016
         * category : 100138
         * name : 花菜
         * photo : 100135
         * cost : 1
         * price : 12
         * intro : null
         * shopid : 101029
         * status : 0
         * discount1 : 1
         * discount2 : null
         * discount3 : null
         * checkstatus : 20161102473
         * intime : null
         * noticenum : 0
         * method : 0
         * unit : 元/kg
         *//*

        private String gid;
        private String orderid;
        private String originid;
        private String idintro;
        private String isbar;
        private String stock;
        private String no;
        private String category;
        private String name;
        private String photo;
        private String cost;
        private String price;
        private String intro;
        private String shopid;
        private String status;
        private String discount1;
        private Object discount2;
        private Object discount3;
        private String checkstatus;
        private Object intime;
        private String noticenum;
        private String method;
        private String unit;

        public String getGid() {
            return gid;
        }

        public void setGid(String gid) {
            this.gid = gid;
        }

        public String getOrderid() {
            return orderid;
        }

        public void setOrderid(String orderid) {
            this.orderid = orderid;
        }

        public String getOriginid() {
            return originid;
        }

        public void setOriginid(String originid) {
            this.originid = originid;
        }

        public String getIdintro() {
            return idintro;
        }

        public void setIdintro(String idintro) {
            this.idintro = idintro;
        }

        public String getIsbar() {
            return isbar;
        }

        public void setIsbar(String isbar) {
            this.isbar = isbar;
        }

        public String getStock() {
            return stock;
        }

        public void setStock(String stock) {
            this.stock = stock;
        }

        public String getNo() {
            return no;
        }

        public void setNo(String no) {
            this.no = no;
        }

        public String getCategory() {
            return category;
        }

        public void setCategory(String category) {
            this.category = category;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getPhoto() {
            return photo;
        }

        public void setPhoto(String photo) {
            this.photo = photo;
        }

        public String getCost() {
            return cost;
        }

        public void setCost(String cost) {
            this.cost = cost;
        }

        public String getPrice() {
            return price;
        }

        public void setPrice(String price) {
            this.price = price;
        }

        public String getIntro() {
            return intro;
        }

        public void setIntro(String intro) {
            this.intro = intro;
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

        public String getDiscount1() {
            return discount1;
        }

        public void setDiscount1(String discount1) {
            this.discount1 = discount1;
        }

        public Object getDiscount2() {
            return discount2;
        }

        public void setDiscount2(Object discount2) {
            this.discount2 = discount2;
        }

        public Object getDiscount3() {
            return discount3;
        }

        public void setDiscount3(Object discount3) {
            this.discount3 = discount3;
        }

        public String getCheckstatus() {
            return checkstatus;
        }

        public void setCheckstatus(String checkstatus) {
            this.checkstatus = checkstatus;
        }

        public Object getIntime() {
            return intime;
        }

        public void setIntime(Object intime) {
            this.intime = intime;
        }

        public String getNoticenum() {
            return noticenum;
        }

        public void setNoticenum(String noticenum) {
            this.noticenum = noticenum;
        }

        public String getMethod() {
            return method;
        }

        public void setMethod(String method) {
            this.method = method;
        }

        public String getUnit() {
            return unit;
        }

        public void setUnit(String unit) {
            this.unit = unit;
        }
    }*/
}
