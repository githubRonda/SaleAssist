package com.ronda.saleassist.bean;

/**
 * Created by lrd on 0020,2016/9/20.
 */
public class PankuBean {

    //checkbox是否选中
    private boolean isChecked = false;

    private String gid;
    private String name;
    private String isBar;
    private int check; //当日盘库标志，0代表此货物当日未盘库，1代表已盘库
    private String checkstatus; //盘库编码，11位数字（撤销操作时需上传）
    private String percent;
    private String stock;
    private String intime;

    public PankuBean(String gid, String name, String isBar, int check, String checkstatus, String percent, String stock, String intime) {
        this.gid = gid;
        this.name = name;
        this.isBar = isBar;
        this.check = check;
        this.checkstatus = checkstatus;
        this.percent = percent;
        this.stock = stock;
        this.intime = intime;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public String getGid() {
        return gid;
    }

    public void setGid(String gid) {
        this.gid = gid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIsBar() {
        return isBar;
    }

    public void setIsBar(String isBar) {
        this.isBar = isBar;
    }

    public int getCheck() {
        return check;
    }

    public void setCheck(int check) {
        this.check = check;
    }

    public String getCheckstatus() {
        return checkstatus;
    }

    public void setCheckstatus(String checkstatus) {
        this.checkstatus = checkstatus;
    }

    public String getPercent() {
        return percent;
    }

    public void setPercent(String percent) {
        this.percent = percent;
    }

    public String getStock() {
        return stock;
    }

    public void setStock(String stock) {
        this.stock = stock;
    }

    public String getIntime() {
        return intime;
    }

    public void setIntime(String intime) {
        this.intime = intime;
    }
}
