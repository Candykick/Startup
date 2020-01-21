package com.candykick.startup.model;

public class ChatAlarmModel {
    private int alarmType;
    private int roomId;
    private String message;
    private int messageDateFrom;

    public ChatAlarmModel() {}

    public ChatAlarmModel(int alarmType, int roomId, String message, int messageDateFrom) {
        this.alarmType = alarmType;
        this.roomId = roomId;
        this.message = message;
        this.messageDateFrom = messageDateFrom;
    }

    public String getMessage() {
        return message;
    }
    public int getAlarmType() {
        return alarmType;
    }
    public int getMessageDateFrom() {
        return messageDateFrom;
    }
    public int getRoomId() {
        return roomId;
    }

    public void setRoomId(int roomId) {
        this.roomId = roomId;
    }
    public void setMessage(String message) {
        this.message = message;
    }
    public void setAlarmType(int alarmType) {
        this.alarmType = alarmType;
    }
    public void setMessageDateFrom(int messageDateFrom) {
        this.messageDateFrom = messageDateFrom;
    }
}
