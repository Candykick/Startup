package com.candykick.startup.view.loginAccount;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;

/**
 * Created by candykick on 2019. 6. 16..
 */

public class JoinViewPagerAdapter extends FragmentStatePagerAdapter {

    ArrayList<Fragment> fragmentArray;
    int activityFlag = 0;

    public JoinViewPagerAdapter(FragmentManager fm, ArrayList<Fragment> fragmentArray, int activityFlag) {
        super(fm);
        this.fragmentArray = fragmentArray;
        this.activityFlag = activityFlag;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return fragmentArray.get(0);
            case 1:
                return fragmentArray.get(1);
            case 2:
                return fragmentArray.get(2);
            case 3:
                return fragmentArray.get(3);
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        if(activityFlag == 0) {
            return 4;
        } else if(activityFlag == 1) {
            return 2;
        } else {
            return 4;
        }
    }
}
