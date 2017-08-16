package com.ronda.saleassist.bean;

/**
 * Author: Ronda(1575558177@qq.com)
 * Date: 2017/08/17
 * Version: v1.0
 */
public class CodeEvent {
    private String code;

    public CodeEvent() {
    }

    public CodeEvent(String barcode) {
        code = barcode;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
