package com.ronda.saleassist.bean;

import com.ronda.saleassist.utils.MathCompute;

/**
 * 用于类别统计中的底部概览信息的显示
 *
 * Author: Ronda(1575558177@qq.com)
 * Date: 2016/12/14
 * Version: v1.0
 */

public class OrderSurvey {

    /**
     * count : 0
     * laterpay_count : 0
     * laterpay_turnover : 0
     * web_avgturnover : 0
     * cash_turnover : 0
     * cash_count : 0
     * laterpay_avgturnover : 0
     * laterpay_complete_avgturnover : 0
     * web_turnover : 0
     * vippay_turnover : 0
     * cash_avgturnover : 0
     * turnover : 0
     * vippay_avgturnover : 0
     * laterpay_complete_count : 0
     * vippay_count : 0
     * laterpay_complete_turnover : 0
     * web_count : 0
     * avgturnover : 0
     */

    private String count;
    private String laterpay_count;
    private String laterpay_turnover;
    private String web_avgturnover;
    private String cash_turnover;
    private String cash_count;
    private String laterpay_avgturnover;
    private String laterpay_complete_avgturnover;
    private String web_turnover;
    private String vippay_turnover;
    private String cash_avgturnover;
    private String turnover;
    private String vippay_avgturnover;
    private String laterpay_complete_count;
    private String vippay_count;
    private String laterpay_complete_turnover;
    private String web_count;
    private String avgturnover;

    public OrderSurvey(String count, String laterpay_count, String laterpay_turnover, String web_avgturnover, String cash_turnover, String cash_count, String laterpay_avgturnover, String laterpay_complete_avgturnover, String web_turnover, String vippay_turnover, String cash_avgturnover, String turnover, String vippay_avgturnover, String laterpay_complete_count, String vippay_count, String laterpay_complete_turnover, String web_count, String avgturnover) {
        this.count = count;
        this.laterpay_count = laterpay_count;
        this.laterpay_turnover = laterpay_turnover;
        this.web_avgturnover = web_avgturnover;
        this.cash_turnover = cash_turnover;
        this.cash_count = cash_count;
        this.laterpay_avgturnover = laterpay_avgturnover;
        this.laterpay_complete_avgturnover = laterpay_complete_avgturnover;
        this.web_turnover = web_turnover;
        this.vippay_turnover = vippay_turnover;
        this.cash_avgturnover = cash_avgturnover;
        this.turnover = turnover;
        this.vippay_avgturnover = vippay_avgturnover;
        this.laterpay_complete_count = laterpay_complete_count;
        this.vippay_count = vippay_count;
        this.laterpay_complete_turnover = laterpay_complete_turnover;
        this.web_count = web_count;
        this.avgturnover = avgturnover;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public String getLaterpay_count() {
        return laterpay_count;
    }

    public void setLaterpay_count(String laterpay_count) {
        this.laterpay_count = laterpay_count;
    }

    public String getLaterpay_turnover() {
        if (null == laterpay_turnover){
            return "";
        }
        return MathCompute.roundHalfUp_scale2(laterpay_turnover);
    }

    public void setLaterpay_turnover(String laterpay_turnover) {
        this.laterpay_turnover = laterpay_turnover;
    }

    public String getWeb_avgturnover() {
        if (null == web_avgturnover){
            return "";
        }
        return MathCompute.roundHalfUp_scale2(web_avgturnover);
    }

    public void setWeb_avgturnover(String web_avgturnover) {
        this.web_avgturnover = web_avgturnover;
    }

    public String getCash_turnover() {
        if (null == cash_turnover){
            return "";
        }
        return MathCompute.roundHalfUp_scale2(cash_turnover);
    }

    public void setCash_turnover(String cash_turnover) {
        this.cash_turnover = cash_turnover;
    }

    public String getCash_count() {
        return cash_count;
    }

    public void setCash_count(String cash_count) {
        this.cash_count = cash_count;
    }

    public String getLaterpay_avgturnover() {
        if (null == laterpay_avgturnover){
            return "";
        }
        return MathCompute.roundHalfUp_scale2(laterpay_avgturnover);
    }

    public void setLaterpay_avgturnover(String laterpay_avgturnover) {
        this.laterpay_avgturnover = laterpay_avgturnover;
    }

    public String getLaterpay_complete_avgturnover() {
        if (null == laterpay_complete_avgturnover){
            return "";
        }
        return MathCompute.roundHalfUp_scale2(laterpay_complete_avgturnover);
    }

    public void setLaterpay_complete_avgturnover(String laterpay_complete_avgturnover) {
        this.laterpay_complete_avgturnover = laterpay_complete_avgturnover;
    }

    public String getWeb_turnover() {
        if (null == web_turnover){
            return "";
        }
        return MathCompute.roundHalfUp_scale2(web_turnover);
    }

    public void setWeb_turnover(String web_turnover) {
        this.web_turnover = web_turnover;
    }

    public String getVippay_turnover() {
        if (null == vippay_turnover){
            return "";
        }
        return MathCompute.roundHalfUp_scale2(vippay_turnover);
    }

    public void setVippay_turnover(String vippay_turnover) {
        this.vippay_turnover = vippay_turnover;
    }

    public String getCash_avgturnover() {
        if (null == cash_avgturnover){
            return "";
        }
        return MathCompute.roundHalfUp_scale2(cash_avgturnover);
    }

    public void setCash_avgturnover(String cash_avgturnover) {
        this.cash_avgturnover = cash_avgturnover;
    }

    public String getTurnover() {
        if (null == turnover){
            return "";
        }
        return MathCompute.roundHalfUp_scale2(turnover);
    }

    public void setTurnover(String turnover) {
        this.turnover = turnover;
    }

    public String getVippay_avgturnover() {
        if (null == vippay_avgturnover){
            return "";
        }
        return MathCompute.roundHalfUp_scale2(vippay_avgturnover);
    }

    public void setVippay_avgturnover(String vippay_avgturnover) {
        this.vippay_avgturnover = vippay_avgturnover;
    }

    public String getLaterpay_complete_count() {
        return laterpay_complete_count;
    }

    public void setLaterpay_complete_count(String laterpay_complete_count) {
        this.laterpay_complete_count = laterpay_complete_count;
    }

    public String getVippay_count() {
        return vippay_count;
    }

    public void setVippay_count(String vippay_count) {
        this.vippay_count = vippay_count;
    }

    public String getLaterpay_complete_turnover() {
        if (null == laterpay_complete_turnover){
            return "";
        }
        return MathCompute.roundHalfUp_scale2(laterpay_complete_turnover);
    }

    public void setLaterpay_complete_turnover(String laterpay_complete_turnover) {
        this.laterpay_complete_turnover = laterpay_complete_turnover;
    }

    public String getWeb_count() {
        return web_count;
    }

    public void setWeb_count(String web_count) {
        this.web_count = web_count;
    }

    public String getAvgturnover() {
        if (null == avgturnover){
            return "";
        }
        return MathCompute.roundHalfUp_scale2(avgturnover);
    }

    public void setAvgturnover(String avgturnover) {
        this.avgturnover = avgturnover;
    }
}
