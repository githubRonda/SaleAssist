package com.ronda.saleassist.bean;

import java.util.List;

/**
 * Author: Ronda(1575558177@qq.com)
 * Date: 2017/08/03
 * Version: v1.0
 */

public class LoginBean {

    /**
     * status : 1
     * msg : 登录成功
     * data : {"token":"fe67f20c600b557d46d7f20442e0c010","mobile":"15858188264","nickname":"刘荣达","userId":"8","shopinfo":[{"shopid":"100001","shopname":"官方测试店铺","alipay_check":1,"wechatpay_check":1,"calculatetype":"0"}]}
     */

    private int status;
    private String msg;
    /**
     * token : fe67f20c600b557d46d7f20442e0c010
     * mobile : 15858188264
     * nickname : lrd
     * userId : 8
     * shopinfo : [{"shopid":"100001","shopname":"官方测试店铺","alipay_check":1,"wechatpay_check":1,"calculatetype":"0"}]
     */

    private DataBean data;

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

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {
        private String token;
        private String mobile;
        private String nickname;
        private String userId;
        /**
         * shopid : 100001
         * shopname : 官方测试店铺
         * alipay_check : 1
         * wechatpay_check : 1
         * calculatetype : 0
         */

        private List<ShopinfoBean> shopinfo;

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }

        public String getMobile() {
            return mobile;
        }

        public void setMobile(String mobile) {
            this.mobile = mobile;
        }

        public String getNickname() {
            return nickname;
        }

        public void setNickname(String nickname) {
            this.nickname = nickname;
        }

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public List<ShopinfoBean> getShopinfo() {
            return shopinfo;
        }

        public void setShopinfo(List<ShopinfoBean> shopinfo) {
            this.shopinfo = shopinfo;
        }

        public static class ShopinfoBean {
            private String shopid;
            private String shopname;
            private int alipay_check;
            private int wechatpay_check;
            private String calculatetype;

            public String getShopid() {
                return shopid;
            }

            public void setShopid(String shopid) {
                this.shopid = shopid;
            }

            public String getShopname() {
                return shopname;
            }

            public void setShopname(String shopname) {
                this.shopname = shopname;
            }

            public int getAlipay_check() {
                return alipay_check;
            }

            public void setAlipay_check(int alipay_check) {
                this.alipay_check = alipay_check;
            }

            public int getWechatpay_check() {
                return wechatpay_check;
            }

            public void setWechatpay_check(int wechatpay_check) {
                this.wechatpay_check = wechatpay_check;
            }

            public String getCalculatetype() {
                return calculatetype;
            }

            public void setCalculatetype(String calculatetype) {
                this.calculatetype = calculatetype;
            }
        }
    }
}
