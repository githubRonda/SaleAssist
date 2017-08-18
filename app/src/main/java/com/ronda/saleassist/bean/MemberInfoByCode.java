package com.ronda.saleassist.bean;

import java.util.List;

/**
 * Author: Ronda(1575558177@qq.com)
 * Date: 2016/11/28
 * Version: v1.0
 */

public class MemberInfoByCode {

    /**
     * userid : 7
     * money : 0
     * username : 15858188264
     * nick : liu
     * extcode : bw8ZxxHT7h
     */

    private String userid;
    private double money;
    private String username;
    private String nick;
    private String extcode;

    private List<PreferenceBean> costs; //此会员的优惠项

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public double getMoney() {
        return money;
    }

    public void setMoney(int money) {
        this.money = money;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public String getExtcode() {
        return extcode;
    }

    public void setExtcode(String extcode) {
        this.extcode = extcode;
    }

    public List<PreferenceBean> getCosts() {
        return costs;
    }

    public void setCosts(List<PreferenceBean> costs) {
        this.costs = costs;
    }
}
