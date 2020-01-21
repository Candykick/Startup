package com.candykick.startup.view.marketAct;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.HashMap;
import java.util.List;

/**
 * Created by candykick on 2019. 7. 28..
 */

public class PostPageAdapter extends FragmentStatePagerAdapter {

    List<HashMap<String, String>> infoDataLst;
    private int pageCount;

    public PostPageAdapter(FragmentManager fm, List<HashMap<String, String>> infoDataLst) {
        super(fm);
        this.infoDataLst = infoDataLst;
        pageCount = infoDataLst.size();
    }

    @Override
    public Fragment getItem(int position) {
        MarketInfoFragment fragment = new MarketInfoFragment();
        Bundle bundle = new Bundle(1);
        bundle.putSerializable("data",infoDataLst.get(position));
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public int getCount() {
        return pageCount;
    }
}
