package com.candykick.startup.view.base;

/**
 * Created by candykick on 2019. 6. 2..
 */

public class User {

    private String userId;

    private String userName;

    private String userEmail;

    private String userLoginMethod;

    private String userImageUrl;

    private String userAddress;

    public User() { }

    public User(String userId, String userName, String userEmail, String userLoginMethod, String userImageUrl, String userAddress) {
        this.userId = userId;
        this.userName = userName;
        this.userEmail = userEmail;
        this.userLoginMethod = userLoginMethod;
        this.userImageUrl = userImageUrl;
        this.userAddress = userAddress;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserLoginMethod() {
        return userLoginMethod;
    }

    public void setUserLoginMethod(String userLoginMethod) { this.userLoginMethod = userLoginMethod; }

    public String getUserImageUrl() {
        return userImageUrl;
    }

    public void setUserImageUrl(String userImageUrl) {
        this.userImageUrl = userImageUrl;
    }

    public String getUserAddress() {
        return userAddress;
    }

    public void setUserAddress(String userAddress) {
        this.userAddress = userAddress;
    }

}
