package com.candykick.startup.view.adviserAct;

/**
 * Created by candykick on 2019. 8. 10..
 */

public class AdviserBoardData {
    public String adviserId, adviserName, adviserNameCard, adviserIntro, adviserJob, profile;

    public AdviserBoardData() {}

    public AdviserBoardData(String adviserId, String adviserName, String adviserNameCard, String adviserIntro, String adviserJob, String profile) {
        this.adviserId = adviserId;
        this.adviserName = adviserName;
        this.adviserNameCard = adviserNameCard;
        this.adviserIntro = adviserIntro;
        this.adviserJob = adviserJob;
        this.profile = profile;
    }
}