package com.candykick.startup.view.adviserAct;

/**
 * Created by candykick on 2019. 9. 8..
 */

public class AdviserBoardNewData {
    public String postId, postTitle, postContents, postImageUrl, postCategory, postTime, userId;
    public int postAnswer, postHit;

    public AdviserBoardNewData() {}

    public AdviserBoardNewData(String postId, String postTitle, String postContents, String postImageUrl, String postCategory,
                               String postTime, String userId, int postAnswer, int postHit) {
        this.postId = postId;
        this.postTitle = postTitle;
        this.postContents = postContents;
        this.postImageUrl = postImageUrl;
        this.postCategory = postCategory;
        this.postTime = postTime;
        this.userId = userId;
        this.postAnswer = postAnswer;
        this.postHit = postHit;
    }
}
