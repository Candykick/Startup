package com.candykick.startup.model;

public class MarketListItemModel {
    private String marketId;
    private String marketImage;
    private String marketTitle;
    private String marketGrade;
    private long marketReviewNum;
    private long marketMinPrice;

    public MarketListItemModel() {}

    public String getMarketId() {
        return marketId;
    }
    public String getMarketImage() {
        return marketImage;
    }
    public String getMarketTitle() {
        return marketTitle;
    }
    public String getMarketGrade() {
        return marketGrade;
    }
    public long getMarketReviewNum() {
        return marketReviewNum;
    }
    public long getMarketMinPrice() {
        return marketMinPrice;
    }

    public void setMarketId(String marketId) {
        this.marketId = marketId;
    }
    public void setMarketImage(String marketImage) {
        this.marketImage = marketImage;
    }
    public void setMarketTitle(String marketTitle) {
        this.marketTitle = marketTitle;
    }
    public void setMarketGrade(String marketGrade) {
        this.marketGrade = marketGrade;
    }
    public void setMarketReviewNum(long marketReviewNum) {
        this.marketReviewNum = marketReviewNum;
    }
    public void setMarketMinPrice(long marketMinPrice) {
        this.marketMinPrice = marketMinPrice;
    }
}
