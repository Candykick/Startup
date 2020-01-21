package com.candykick.startup.view.cartAct;

/**
 * Created by candykick on 2019. 8. 12..
 */

public class CartImInvData {
    public String wantinvcapital, wantinvtype, myid;
    public String[] wantinvjob;

    public CartImInvData() {}

    public CartImInvData(String wantinvjob, String wantinvcapital, String wantinvtype, String myid) {
        this.wantinvjob = wantinvjob.split(", ");
        this.wantinvcapital = wantinvcapital;
        this.wantinvtype = wantinvtype;
        this.myid = myid;
    }
}
