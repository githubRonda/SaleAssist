package com.ronda.saleassist.bean;

/**
 * 商品的样式
 * <p>
 * Author: Ronda(1575558177@qq.com)
 * Date: 2017/04/23
 * Version: v1.0
 */

public class GoodsStyle {
    private int column = 6; // 列数
    private int horizontalSpace; // 水平间隔（列间距）
    private int verticalSpace; // 垂直间隔（行间距）

    private int     titleTextSize = 14; // 标题字号
    private boolean isBold = true; // 是否粗体

    private int textSize = 14; // 正文字号

    private boolean isShowNum = true; // 显示商品编码
    private boolean isShowPrice = true; // 显示商品价格

    public GoodsStyle() {
    }

    public GoodsStyle(int column, int horizontalSpace, int verticalSpace, int titleTextSize, boolean isBold, int textSize, boolean isShowNum, boolean isShowPrice) {
        this.column = column;
        this.horizontalSpace = horizontalSpace;
        this.verticalSpace = verticalSpace;
        this.titleTextSize = titleTextSize;
        this.isBold = isBold;
        this.textSize = textSize;
        this.isShowNum = isShowNum;
        this.isShowPrice = isShowPrice;
    }

    public int getColumn() {
        return column;
    }

    public void setColumn(int column) {
        this.column = column;
    }

    public int getHorizontalSpace() {
        return horizontalSpace;
    }

    public void setHorizontalSpace(int horizontalSpace) {
        this.horizontalSpace = horizontalSpace;
    }

    public int getVerticalSpace() {
        return verticalSpace;
    }

    public void setVerticalSpace(int verticalSpace) {
        this.verticalSpace = verticalSpace;
    }

    public int getTitleTextSize() {
        return titleTextSize;
    }

    public void setTitleTextSize(int titleTextSize) {
        this.titleTextSize = titleTextSize;
    }

    public boolean isBold() {
        return isBold;
    }

    public void setBold(boolean bold) {
        isBold = bold;
    }

    public int getTextSize() {
        return textSize;
    }

    public void setTextSize(int textSize) {
        this.textSize = textSize;
    }

    public boolean isShowNum() {
        return isShowNum;
    }

    public void setShowNum(boolean showNum) {
        isShowNum = showNum;
    }

    public boolean isShowPrice() {
        return isShowPrice;
    }

    public void setShowPrice(boolean showPrice) {
        isShowPrice = showPrice;
    }
}
