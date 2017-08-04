package com.ronda.saleassist.local.sqlite.table;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Author: Ronda(1575558177@qq.com)
 * Date: 2017/03/15
 * Version: v1.0
 */
@Entity
public class Cart {
    @Id(autoincrement = true)
    private Long id;

    private String name;
    private String price;
    @Generated(hash = 697536152)
    public Cart(Long id, String name, String price) {
        this.id = id;
        this.name = name;
        this.price = price;
    }
    @Generated(hash = 1029823171)
    public Cart() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getName() {
        return this.name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getPrice() {
        return this.price;
    }
    public void setPrice(String price) {
        this.price = price;
    }

}
