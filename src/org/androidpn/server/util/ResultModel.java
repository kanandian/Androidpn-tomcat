package org.androidpn.server.util;

public class ResultModel {

    private int errcode;
    private String errMessage;

    public ResultModel() {
        this.errcode = 1;
    }

    public int getErrcode() {
        return errcode;
    }

    public void setErrcode(int errcode) {
        this.errcode = errcode;
    }

    public String getErrMessage() {
        return errMessage;
    }

    public void setErrMessage(String errMessage) {
        this.errMessage = errMessage;
    }
}
