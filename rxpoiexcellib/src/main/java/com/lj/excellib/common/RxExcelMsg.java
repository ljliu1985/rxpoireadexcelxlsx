package com.lj.excellib.common;

public class RxExcelMsg {

    public static final int TYPE_SEND_LOADING = 2;
    public static final int TYPE_SEND_MSG = 3;
    public static final int TYPE_SUCCESS = 4;
    public int type;
    public String msg;
    public Object object;

    public RxExcelMsg(int type, String msg) {
        this.type = type;
        this.msg = msg;
    }

    public RxExcelMsg(int type, Object object) {
        this.type = type;
        this.object = object;
    }
}
