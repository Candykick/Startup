package com.candykick.startup.view.referenceAct;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.candykick.startup.R;

import java.util.ArrayList;

/**
 * Created by candykick on 2019. 8. 27..
 */

public class ReferenceSubimageAdapter extends PagerAdapter {

    ArrayList<String> imageList;
    Context context;
    LayoutInflater inflater;

    public ReferenceSubimageAdapter(ArrayList<String> imageList, Context context) {
        this.imageList = imageList;
        this.context = context;
    }

    @Override
    public int getCount() {
        return imageList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((FrameLayout)object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.rawreferenceimage, container, false);
        ImageView imageView = v.findViewById(R.id.ivReferenceRaw);

        Glide.with(context).load(imageList.get(position)).into(imageView);

        container.addView(v);
        return v;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.invalidate();
    }
}