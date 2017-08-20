package com.ronda.saleassist.bean;

/**
 * Created by lrd on 0016,2016/9/16.
 */
public class CategoryStatisticBean {
    private String categoryId; //类id
    private String categoryName; //类名
    private String goodnum; //总重量或总件数
    private String income; //总收入

    private String businessnum;//交易笔数

    public CategoryStatisticBean(String categoryId, String categoryName, String goodnum, String income, String businessnum) {
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.goodnum = goodnum;
        this.income = income;
        this.businessnum = businessnum;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getGoodnum() {
        return goodnum;
    }

    public void setGoodnum(String goodnum) {
        this.goodnum = goodnum;
    }

    public String getIncome() {
        return income;
    }

    public void setIncome(String income) {
        this.income = income;
    }

    public String getBusinessnum() {
        return businessnum;
    }

    public void setBusinessnum(String businessnum) {
        this.businessnum = businessnum;
    }
}
