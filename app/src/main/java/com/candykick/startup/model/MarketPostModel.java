package com.candykick.startup.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MarketPostModel {
    private String titleImage;
    private String category;
    private String title;
    private String grade;
    private long minimumPrice;
    private List<HashMap<String, String>> infoDataLst;
    private long reviewNum;
    private String detail;
    private String modifyInfo;
    private String refundInfo;
    private String receiverId;
    private ChatUserModel receiverData;
    private UserOutModel receiverAllData;
    private ArrayList<MarketCommentModel> commentArray = new ArrayList<>();

    public MarketPostModel() {}

    public List<HashMap<String, String>> getInfoDataLst() { return infoDataLst; }
    public long getReviewNum() {
        return reviewNum;
    }
    public String getTitleImage() {
        return titleImage;
    }
    public String getCategory() {
        return category;
    }
    public String getTitle() {
        return title;
    }
    public String getGrade() {
        return grade;
    }
    public long getMinimumPrice() {
        return minimumPrice;
    }
    public String getDetail() {
        return detail;
    }
    public String getReceiverId() {
        return receiverId;
    }
    public ChatUserModel getReceiverData() {
        return receiverData;
    }
    public UserOutModel getReceiverAllData() {
        return receiverAllData;
    }
    public String getModifyInfo() {
        return modifyInfo;
    }
    public String getRefundInfo() {
        return refundInfo;
    }
    public ArrayList<MarketCommentModel> getCommentArray() {
        return commentArray;
    }

    public void setInfoDataLst(List<HashMap<String, String>> infoDataLst) { this.infoDataLst = infoDataLst; }
    public void setReviewNum(long reviewNum) {
        this.reviewNum = reviewNum;
    }
    public void setTitleImage(String titleImage) {
        this.titleImage = titleImage;
    }
    public void setCategory(String category) {
        this.category = category;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public void setGrade(String grade) {
        this.grade = grade;
    }
    public void setMinimumPrice(long minimumPrice) {
        this.minimumPrice = minimumPrice;
    }
    public void setDetail(String detail) {
        this.detail = detail;
    }
    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }
    public void setReceiverData(ChatUserModel receiverData) {
        this.receiverData = receiverData;
    }
    public void setReceiverAllData(UserOutModel allData) {
        this.receiverAllData = allData;
    }
    public void setModifyInfo(String modifyInfo) {
        this.modifyInfo = modifyInfo;
    }
    public void setRefundInfo(String refundInfo) {
        this.refundInfo = refundInfo;
    }
    public void setCommentArray(ArrayList<MarketCommentModel> commentArray) { this.commentArray = commentArray; }
}