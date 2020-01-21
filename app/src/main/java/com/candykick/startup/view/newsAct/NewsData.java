package com.candykick.startup.view.newsAct;

/**
 * Created by candykick on 2019. 6. 9..
 */

public class NewsData {
    public boolean isBookmark;
    public String strImgUrl;
    public String strTitle;
    public String strContent;
    public String strlink;

    public NewsData() {}

    public NewsData(boolean isBookmark, String strImgUrl, String strTitle, String strContent, String strlink) {
        this.isBookmark = isBookmark;
        this.strImgUrl = strImgUrl;
        this.strTitle = strTitle;
        this.strContent = strContent;
        this.strlink = strlink;
    }
}
