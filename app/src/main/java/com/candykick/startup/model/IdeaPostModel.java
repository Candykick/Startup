package com.candykick.startup.model;

import java.util.ArrayList;

public class IdeaPostModel {
    private boolean isNotice;
    private String ideaId;
    private String ideaTitle;
    private String ideaContent;
    private String ideaUser;
    private String ideaUserName;
    private String ideaUserProfile;
    private ArrayList<IdeaCommentModel> commentModels;

    public IdeaPostModel() {}

    public boolean isNotice() {
        return isNotice;
    }
    public String getIdeaId() {
        return ideaId;
    }
    public String getIdeaTitle() {
        return ideaTitle;
    }
    public String getIdeaContent() {
        return ideaContent;
    }
    public String getIdeaUser() {
        return ideaUser;
    }
    public ArrayList<IdeaCommentModel> getCommentModels() {
        return commentModels;
    }
    public String getIdeaUserName() {
        return ideaUserName;
    }
    public String getIdeaUserProfile() {
        return ideaUserProfile;
    }

    public void setNotice(boolean notice) {
        isNotice = notice;
    }
    public void setIdeaId(String ideaId) {
        this.ideaId = ideaId;
    }
    public void setIdeaTitle(String ideaTitle) {
        this.ideaTitle = ideaTitle;
    }
    public void setIdeaContent(String ideaContent) {
        this.ideaContent = ideaContent;
    }
    public void setIdeaUser(String ideaUser) {
        this.ideaUser = ideaUser;
    }
    public void setCommentModels(ArrayList<IdeaCommentModel> commentModels) {
        this.commentModels = commentModels;
    }
    public void setIdeaUserName(String ideaUserName) {
        this.ideaUserName = ideaUserName;
    }
    public void setIdeaUserProfile(String ideaUserProfile) {
        this.ideaUserProfile = ideaUserProfile;
    }
}
