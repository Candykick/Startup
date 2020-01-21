package com.candykick.startup.model;

import java.io.Serializable;

public class ChatUserModel implements Serializable {
    private String userId;
    private String userName;
    private long userType;
    private String profileImage;

    public ChatUserModel() { }

    public ChatUserModel(String userId, String userName, long userType, String profileImage) {
        this.userType = userType;
        this.userId = userId;
        this.userName = userName;
        this.profileImage = profileImage;
    }

    public String getUserId() {
        return userId;
    }
    public String getUserName() {
        return userName;
    }
    public String getProfileImage() {
        return profileImage;
    }
    public long getUserType() {
        return userType;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
    public void setUserName(String userName) {
        this.userName = userName;
    }
    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }
    public void setUserType(long userType) {
        this.userType = userType;
    }
}
