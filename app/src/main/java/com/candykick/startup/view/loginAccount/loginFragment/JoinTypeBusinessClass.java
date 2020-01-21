package com.candykick.startup.view.loginAccount.loginFragment;

/**
 * Created by candykick on 2019. 8. 4..
 */

public class JoinTypeBusinessClass {
    public String industryName;
    public String industryCode;
    public String industryExp;
    public String industrySearch;

    public JoinTypeBusinessClass() {}

    public JoinTypeBusinessClass(String name, String code, String exp, String search) {
        this.industryName = name;
        this.industryCode = code;
        this.industryExp = exp;
        this.industrySearch = search;
    }
}
