package com.candykick.startup.util;

/*
 Type 1. 카카오톡 방식(적용)
  ~ 24시간: '오전/오후 00:00'
 1일, 2일: '어제, 그제'
 3일 ~ 1년: '0월 00일'
 작년 ~ : '0000.00.00'

 Type 2. (미적용)
 0초 ~ 2분: '방금 전'
 2분 ~ 60분: '00분 전'
 1시간 ~ 24시간: '00시간 전'
 1일 ~ 30일: '00일 전'
 1달 ~ 12달: '0달 전'
 12달 ~ : '0년 전'
 */

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtility {

    //채팅 작성 시간
    long datentime;

    //글 작성 시점과 현재 시점이 동일년도인지 체크하는 용도
    SimpleDateFormat yearcheck = new SimpleDateFormat("yyyy");

    //Type 1. 카카오톡 방식(기본 적용)
    SimpleDateFormat type11 = new SimpleDateFormat("a hh:mm"); // ~ 24시간: '오전/오후 00:00'
                                                                        //1일, 2일: '어제, 그제'
    SimpleDateFormat type13 = new SimpleDateFormat("MM월 dd일"); //3일 ~ 동일 년도: '0월 00일'
    SimpleDateFormat type14 = new SimpleDateFormat("yyyy.MM.dd"); //작년 ~ : '0000.00.00'

    //Type 2. 현재 시간 기준 방식(미적용)
                                                                      //0초 ~ 2분: '방금 전'
    SimpleDateFormat type22 = new SimpleDateFormat("mm분 전"); //2분 ~ 60분: '00분 전'
    SimpleDateFormat type23 = new SimpleDateFormat("HH시간 전"); //1시간 ~ 24시간: '00시간 전'
    SimpleDateFormat type24 = new SimpleDateFormat("dd일 전"); //1일 ~ 30일: '00일 전'
    SimpleDateFormat type25 = new SimpleDateFormat("MM달 전"); //1달 ~ 동일 년도: '0달 전'
    SimpleDateFormat type26 = new SimpleDateFormat("yy년 전"); //작년 ~ : '0년 전'

    public DateUtility(long datentime) {
        this.datentime = datentime;
    }

    public String DateResultType1() {
        String result;

        long timeFlag = (System.currentTimeMillis() - datentime)/1000/60/60; //시간 단위로 나눔
        if(timeFlag < 24) {
            result = type11.format(new Date(datentime));
        } else if(timeFlag < 48) {
            result = "어제";
        } else if(timeFlag < 72) {
            result = "그제";
        } else if(yearcheck.format(new Date(System.currentTimeMillis())).equals(yearcheck.format(new Date(datentime)))) {
            result = type13.format(new Date(datentime));
        } else {
            result = type14.format(new Date(datentime));
        }

        return result;
    }

    public String DateResultType2() {
        String result;

        long timeFlagMills = System.currentTimeMillis() - datentime;
        long timeFlag = timeFlagMills/1000/60; //분 단위로 나눔

        if(timeFlag <= 2) { //0초 ~ 2분: '방금 전'
            result = "방금 전";
        } else if(timeFlag < 60) {//2분 ~ 60분: '00분 전'
            result = type22.format(new Date(timeFlagMills));
        } else if(timeFlag < 1440) {//1시간 ~ 24시간: '00시간 전'
            result = type23.format(new Date(timeFlagMills));
        } else if(timeFlag < 44640) {//1일 ~ 31일: '00일 전'
            result = type24.format(new Date(timeFlagMills));
        } else if(yearcheck.format(new Date(System.currentTimeMillis())).equals(yearcheck.format(new Date(datentime)))) {
            //1달 ~ 동일 년도: '0달 전'
            result = type25.format(new Date(timeFlagMills));
        } else {//작년 ~ : '0년 전'
            result = type26.format(new Date(timeFlagMills));
        }

        return result;
    }
}
