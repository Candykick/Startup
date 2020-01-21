package com.candykick.startup.view.adviserAct;

import java.util.ArrayList;

/**
 * Created by candykick on 2019. 9. 9..
 */

public class AdviserAnswerData {
    public boolean isBest;
    public String answerUserId, answerUserName, answerUserInfo, answerUserProfileUrl, answerText;
    public int answerRanking, answerBestNum, answerRecommendNum;
    public ArrayList<String> imageList = new ArrayList<>();

    public AdviserAnswerData() {}
}
