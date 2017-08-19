package com.ronda.saleassist.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Administrator on 2016/11/12.
 * 挂账信息具体到莫一个订单的信息
 */

public class GuaZhangOrderDetail implements Serializable {
    private String no;
    private List<Allinfo> allinfo;

    public String getNo() {
        return no;
    }

    public void setNo(String no) {
        this.no = no;
    }

    public List<Allinfo> getAllinfo() {
        return allinfo;
    }

    public void setAllinfo(List<Allinfo> allinfo) {
        this.allinfo = allinfo;
    }

    private boolean isChecked  = false;

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    @Override
    public String toString() {
        return "GuaZhangOrderDetail{" +
                "no='" + no + '\'' +
                ", allinfo=" + allinfo +
                ", isChecked=" + isChecked +
                '}';
    }
}
