package com.ronda.saleassist.local.sqlite.table;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Author: Ronda(1575558177@qq.com)
 * Date: 2017/08/19
 * Version: v1.0
 *
 * 只有货物goodsId， name, price. 作用：根据价格模糊查询
 */

@Entity
public class SimpleGoodsBean {


    @Id(autoincrement = true)
    private Long id;

    private String goodsId;

    private String name;

    private String price;

    @Generated(hash = 427095548)
    public SimpleGoodsBean(Long id, String goodsId, String name, String price) {
        this.id = id;
        this.goodsId = goodsId;
        this.name = name;
        this.price = price;
    }

    @Generated(hash = 794555017)
    public SimpleGoodsBean() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getGoodsId() {
        return this.goodsId;
    }

    public void setGoodsId(String goodsId) {
        this.goodsId = goodsId;
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

    @Override
    public String toString() {
        return "SimpleGoodsBean{" +
                "id=" + id +
                ", goodsId='" + goodsId + '\'' +
                ", name='" + name + '\'' +
                ", price='" + price + '\'' +
                '}';
    }
}
