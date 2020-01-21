package com.candykick.startup.view.adviserAct;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.candykick.startup.R;
import com.fxn.pix.Pix;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by candykick on 2019. 9. 15..
 */

public class ImageViewPagerAdapter extends PagerAdapter {

    private ArrayList<File> imageList = new ArrayList<>();
    private LayoutInflater inflater;
    private Context context;
    AdviserUpload1Fragment fragment;
    TempFragment fragment2;
    int tmp = 0;

    public ImageViewPagerAdapter(Context context, AdviserUpload1Fragment fragment, ArrayList<File> imageList) {
        this.context = context;
        this.fragment = fragment;
        this.imageList = imageList;
    }
    public ImageViewPagerAdapter(Context context, TempFragment fragment2, ArrayList<File> imageList, int tmp) {
        this.context = context;
        this.fragment2 = fragment2;
        this.imageList = imageList;
        this.tmp = tmp;
    }

    @Override
    public int getCount() {
        return imageList.size()+1;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((ViewGroup) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        inflater = (LayoutInflater)context.getSystemService
                (Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.raw_viewpager_image, container, false);
        ImageView imageView = v.findViewById(R.id.ivRawVPImage);
        ImageButton xButton = v.findViewById(R.id.btnRawVPImageX);
        Button addButton = v.findViewById(R.id.btnRawVPImageAdd);

        if(position == imageList.size()) {
            imageView.setVisibility(View.GONE);
            xButton.setVisibility(View.GONE);
            addButton.setVisibility(View.VISIBLE);

            if(tmp==0) {
                addButton.setOnClickListener(view -> {
                    Pix.start(fragment,                    //Activity or Fragment Instance
                            1009,                //Request code for activity results
                            9);
                });
            } else {
                addButton.setOnClickListener(view -> {
                    Pix.start(fragment2,                    //Activity or Fragment Instance
                            1009,                //Request code for activity results
                            9);
                });
            }
        } else {
            imageView.setVisibility(View.VISIBLE);
            //xButton.setVisibility(View.VISIBLE);
            addButton.setVisibility(View.GONE);
            Glide.with(context).load(imageList.get(position)).into(imageView);

            xButton.setOnClickListener(view -> {
                imageList.get(position).delete();
                notifyDataSetChanged();
            });
        }

        /*if(isSrc) {
            if(imagesrcList.get(position).equals("")) {
                imageView.setVisibility(View.GONE);
                xButton.setVisibility(View.GONE);
                addButton.setVisibility(View.VISIBLE);

                addButton.setOnClickListener(view -> {
                    Pix.start(context,                    //Activity or Fragment Instance
                            1009,                //Request code for activity results
                            9);
                });
            } else {
                imageView.setVisibility(View.VISIBLE);
                xButton.setVisibility(View.VISIBLE);
                addButton.setVisibility(View.GONE);
                Glide.with(context).load(imagesrcList.get(position)).into(imageView);
            }
        } else {
            Glide.with(context).load(imageList.get(position)).into(imageView);
        }*/

        container.addView(v);

        return v;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ((ViewPager)container).removeView((ConstraintLayout) object);
        //container.invalidate();
    }

    @Override
    public int getItemPosition(Object object){
        return PagerAdapter.POSITION_NONE;
    }
}