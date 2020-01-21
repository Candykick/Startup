package com.candykick.startup;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

/**
 * Created by candykick on 2019. 6. 6..
 */

public class AutoScrollAdapter extends PagerAdapter {

    Context context;
    ArrayList<Integer> data;

    public AutoScrollAdapter(Context context, ArrayList<Integer> data) {
        this.context = context;
        this.data = data;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        //position 값을 받아 주어진 위치에 페이지를 생성
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.auto_viewpager, null);
        ImageView image_container = v.findViewById(R.id.image_container);
        Glide.with(context).load(data.get(position)).into(image_container);
        container.addView(v);
        return v;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        //position 값을 받아 주어진 위치에 있는 페이지 삭제
        container.removeView((View)object);
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }
}
