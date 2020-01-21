package com.candykick.startup.model;

public class UserOutModel {
    public String userId;
    public String userName;
    public String userType;
    public String profileImage;
    public boolean taxinvoicepossible;
    public String userDes;
    public String userSatis;
    public long userOrderNum;
    public String userAnswerTime;

    public UserOutModel() {}

    public UserOutModel(String userId, String userName, long userType, String profileImage, boolean taxinvoicepossible,
                       String userDes, String userSatis, long userOrderNum, String userAnswerTime) {
        int userType2 = (int)userType;
        this.userId = userId;
        this.userName = userName;
        this.profileImage = profileImage;
        this.taxinvoicepossible = taxinvoicepossible;
        this.userDes = userDes;
        this.userSatis = userSatis;
        this.userOrderNum = userOrderNum;
        this.userAnswerTime = userAnswerTime;

        switch(userType2) {
            case 0:
                this.userType = "사업가";
                break;
            case 1:
                this.userType = "투자자";
                break;
            default:
                this.userType = "사업가";
                break;
        }
    }
}
