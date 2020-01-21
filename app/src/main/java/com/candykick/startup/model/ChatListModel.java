package com.candykick.startup.model;

public class ChatListModel {
    private String roomId;
    private String userId;
    private String userName;
    private String userRecentMessage;
    private String profileImage;
    private String messageDate;
    private int notreadMessage;

    public ChatListModel() {}

    public ChatListModel(String roomId, String userId, String userName, String userRecentMessage, String profileImage, String messageDate, int notreadMessage) {
        this.roomId = roomId;
        this.userId = userId;
        this.userName = userName;
        this.userRecentMessage = userRecentMessage;
        this.profileImage = profileImage;
        this.messageDate = messageDate;
        this.notreadMessage = notreadMessage;
    }

    public String getRoomId() {
        return roomId;
    }
    public String getProfileImage() {
        return profileImage;
    }
    public String getUserName() {
        return userName;
    }
    public String getUserId() {
        return userId;
    }
    public int getNotreadMessage() {
        return notreadMessage;
    }
    public String getMessageDate() {
        return messageDate;
    }
    public String getUserRecentMessage() {
        return userRecentMessage;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }
    public void setMessageDate(String messageDate) {
        this.messageDate = messageDate;
    }
    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }
    public void setUserName(String userName) {
        this.userName = userName;
    }
    public void setUserId(String userId) {
        this.userId = userId;
    }
    public void setNotreadMessage(int notreadMessage) {
        this.notreadMessage = notreadMessage;
    }
    public void setUserRecentMessage(String userRecentMessage) {
        this.userRecentMessage = userRecentMessage;
    }
}
