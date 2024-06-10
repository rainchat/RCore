package com.rainchat.rlib.utils.language;

public class LangMsg {

    private String msgDefault;
    private String message;
    private String path;

    public LangMsg(String path, String msgDefault) {
        this.path = path;
        this.msgDefault = msgDefault;
    }


    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMsgDefault() {
        return msgDefault;
    }

    public void setMsgDefault(String msgDefault) {
        this.msgDefault = msgDefault;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

}
