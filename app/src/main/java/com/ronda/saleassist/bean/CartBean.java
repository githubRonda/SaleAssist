package com.ronda.saleassist.bean;

import com.ronda.saleassist.base.AppConst;
import com.ronda.saleassist.local.preference.SPUtils;
import com.ronda.saleassist.utils.MathCompute;

/**
 * Created by Dragon on 2016-4-7.
 * <p/>
 * 用于货篮中显示的列表数据类型
 * <p/>
 * discountCost方便于本次订单的总价，以及上传订单时的data数据（即本次订单中每种货物的花费）
 */
public class CartBean {

    private long date; // 当秤端的发起的额外项，即使两次重量和单价一样，也要认为不相等

    private String id;
    private String name;
    private String price; //double类型的字符串 单位kg/元
    private String weight;//double 类型的字符串， 单位kg
    private String imgPath;

    private String discount;//折扣  小数
//    private String discountCost;

    private String category;

    private String barcode; //是0就表示称重类，条码值就表示条码类货物

    private String method; //值为0时，表示称重类；值为1时，表示条码类和计件类
    private String unit; //货物的单位

    public CartBean(){}

    public CartBean(String id, String name, String price, String weight, String imgPath, String discount, String category, String barcode, String method, String unit) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.weight = weight;
        this.imgPath = imgPath;
        this.discount = discount;
        this.category = category;
        this.barcode = barcode;
        this.method = method;
        this.unit = unit;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getImgPath() {
        return imgPath;
    }

    public void setImgPath(String imgPath) {
        this.imgPath = imgPath;
    }

    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
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

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public String getDiscountPrice() {//单位kg/元

        String discount_t;
        if (discount == null || discount.isEmpty())
            discount_t = "1";
        else
            discount_t = discount;
//        return MathCompute.roundHalfUp_scale2(MathCompute.mul(price, discount_t));
        return MathCompute.roundFloor_scale2(MathCompute.mul(price, discount_t));
    }

    /**
     * 获取这个菜的总花费（折扣后的）,单位 元
     *
     * @return
     */
    public String getDiscountCost() {
        int type = SPUtils.getInt(AppConst.CALCULATE_TYPE, AppConst.TYPE_TOTAL_5);// 默认总额逢5进
        if (type == AppConst.TYPE_PER_1){ //每笔逢1进
            return MathCompute.roundUp_scale1(MathCompute.mul(weight, getDiscountPrice()));
        }
        else if (type == AppConst.TYPE_PER_5){ // 每笔逢5进
            return MathCompute.roundHalfUp_scale1(MathCompute.mul(weight, getDiscountPrice()));
        }
        else{ //总额进位， 不在这里处理
            return MathCompute.roundFloor_scale2(MathCompute.mul(weight, getDiscountPrice()));
        }
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public boolean isBargoods(){
        if (barcode == null || barcode.isEmpty() || barcode.equals("0"))
            return false;
        return true;
    }


    @Override
    public int hashCode() {
        return  id.hashCode() + name.hashCode() + price.hashCode()  + discount.hashCode() + barcode.hashCode() + new Long(this.date).hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (obj == this)
            return true;
        if(!(obj instanceof CartBean)) return false;

        CartBean cartBean = (CartBean) obj;

        return this.id.equals(cartBean.id) && this.name.equals(cartBean.name) && this.price.equals(cartBean.price)  && this.discount.equals(cartBean.discount) && this.date == cartBean.date;
    }

    @Override
    public String toString() {
        return "CartBean{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", price='" + price + '\'' +
                ", weight='" + weight + '\'' +
                ", imgPath='" + imgPath + '\'' +
                ", discount='" + discount + '\'' +
                ", category='" + category + '\'' +
                ", barcode='" + barcode + '\'' +
                '}';
    }
}
