package com.candykick.startup.view.cartAct;

/**
 * Created by candykick on 2019. 8. 12..
 */

public class CartImCeoData {
    public String wantcapital, age, investtype, mycapital;
    public long gender;
    public boolean isoffice;
    public String[] role, businesstype;
    public String myid;

    public CartImCeoData() {}

    public CartImCeoData(String businesstype, String role, String wantcapital, String age, long gender, boolean isoffice, String investtype, String mycapital, String myid) {
        this.businesstype = businesstype.split(", ");
        this.role = role.split(", ");
        this.wantcapital = wantcapital;
        this.age = age;
        this.gender = gender;
        this.isoffice = isoffice;
        this.investtype = investtype;
        this.mycapital = mycapital;
        this.myid = myid;
    }
}
