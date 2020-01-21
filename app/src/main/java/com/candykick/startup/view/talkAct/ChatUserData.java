package com.candykick.startup.view.talkAct;

import java.io.Serializable;

/**
 * Created by candykick on 2019. 7. 7..
 */

public class ChatUserData implements Serializable {
    public String userId;
    public String userName;
    public String userType;
    public String profileImage;

    public ChatUserData() {}

    public ChatUserData(String userId, String userName, long userType, String profileImage) {
        int userType2 = (int)userType;
        this.userId = userId;
        this.userName = userName;
        this.profileImage = profileImage;

        switch(userType2) {
            case 0:
                this.userType = "창업희망자";
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
