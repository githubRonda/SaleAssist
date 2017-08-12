package com.ronda.saleassist.local.sqlite.table;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

/**
 * 暂存订单
 * <p>
 * Author: Ronda(1575558177@qq.com)
 * Date: 2017/04/22
 * Version: v1.0
 */
@Entity
public class CartBeanOrder {

    @Id(autoincrement = true)
    private Long id;

    private String date;

    private String total;

    private String cartbeans;

    @Generated(hash = 161576)
    public CartBeanOrder(Long id, String date, String total, String cartbeans) {
        this.id = id;
        this.date = date;
        this.total = total;
        this.cartbeans = cartbeans;
    }

    @Generated(hash = 804005397)
    public CartBeanOrder() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDate() {
        return this.date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTotal() {
        return this.total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public String getCartbeans() {
        return this.cartbeans;
    }

    public void setCartbeans(String cartbeans) {
        this.cartbeans = cartbeans;
    }

   
}
