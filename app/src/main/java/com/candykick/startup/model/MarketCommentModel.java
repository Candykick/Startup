package com.candykick.startup.model;

import java.io.Serializable;

public class MarketCommentModel implements Serializable {
    private String comment;
    private String username;
    private String profile;
    private int id;
    private int parent;
    private int seq;
    private int star;

    public MarketCommentModel() {}

    public MarketCommentModel(int id, String comment, String username, String profile, int parent, int seq, int star) {
        this.id = id;
        this.comment = comment;
        this.username = username;
        this.profile = profile;
        this.parent = parent;
        this.seq = seq;
        this.star = star;
    }

    public String getComment() {
        return comment;
    }
    public String getUsername() {
        return username;
    }
    public String getProfile() {
        return profile;
    }
    public int getId() {
        return id;
    }
    public int getParent() {
        return parent;
    }
    public int getSeq() {
        return seq;
    }
    public int getStar() {
        return star;
    }
    public void setComment(String comment) {
        this.comment = comment;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public void setProfile(String profile) {
        this.profile = profile;
    }
    public void setId(int id) {
        this.id = id;
    }
    public void setParent(int parent) {
        this.parent = parent;
    }
    public void setSeq(int seq) {
        this.seq = seq;
    }
    public void setStar(int star) {
        this.star = star;
    }
}
