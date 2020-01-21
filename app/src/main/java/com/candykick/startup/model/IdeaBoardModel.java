package com.candykick.startup.model;

public class IdeaBoardModel {
    private boolean isNotice;
    private String ideaId;
    private String ideaTitle;
    private String ideaContent;
    private String  ideaUser;

    public IdeaBoardModel() {}

    public IdeaBoardModel(String ideaId, boolean isNotice, String ideaTitle, String ideaContent, String ideaUser) {
        this.ideaId = ideaId;
        this.isNotice = isNotice;
        this.ideaTitle = ideaTitle;
        this.ideaContent = ideaContent;
        this.ideaUser = ideaUser;
    }

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
}
