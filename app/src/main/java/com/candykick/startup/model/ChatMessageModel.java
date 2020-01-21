package com.candykick.startup.model;

public class ChatMessageModel {
    private String message;
    private long messageDate;
    private int flag;
    private boolean notread;

    public ChatMessageModel() { }

    public ChatMessageModel(String message, long messageDate, int flag, boolean notread) {
        this.message = message;
        this.messageDate = messageDate;
        this.flag = flag;
        this.notread = notread;
    }

    public int getFlag() {
        return flag;
    }
    public long getMessageDate() {
        return messageDate;
    }
    public String getMessage() {
        return message;
    }
    public boolean isNotread() {
        return notread;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }
    public void setMessage(String message) {
        this.message = message;
    }
    public void setMessageDate(long messageDate) {
        this.messageDate = messageDate;
    }
    public void setNotread(boolean notread) {
        this.notread = notread;
    }
}
