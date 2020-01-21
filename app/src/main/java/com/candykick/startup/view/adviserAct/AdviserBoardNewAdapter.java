package com.candykick.startup.view.adviserAct;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.candykick.startup.R;

import java.util.ArrayList;

/**
 * Created by candykick on 2019. 9. 8..
 */

public class AdviserBoardNewAdapter extends BaseAdapter {
    private Context context = null;
    private ArrayList<AdviserBoardNewData> listData = new ArrayList<>();

    public AdviserBoardNewAdapter(Context context, ArrayList<AdviserBoardNewData> listData) {
        super();
        this.context = context;
        this.listData = listData;
    }

    //아래는 BaseAdapter 상속받은 필수요소.
    @Override
    public int getCount() {
        return listData.size();
    }

    @Override
    public Object getItem(int position) {
        return listData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        View view = convertView;
        if(view == null) {
            holder = new ViewHolder();

            LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.rawadvisernew, parent, false);

            holder.ivImage = view.findViewById(R.id.ivAdvNewImage);
            holder.tvTitle = view.findViewById(R.id.tvAdvNewTitle);
            holder.tvContents = view.findViewById(R.id.tvAdvNewContents);
            holder.tvDateCategory = view.findViewById(R.id.tvAdvNewDateCategory);
            holder.tvAnswer = view.findViewById(R.id.tvAdvNewAnswer);
            holder.tvRecommend = view.findViewById(R.id.tvAdvNewRecommend);

            view.setTag(holder);
        } else {
            holder = (ViewHolder)view.getTag();
        }

        AdviserBoardNewData data = listData.get(position);

        if(!data.postImageUrl.equals("")) {
            holder.ivImage.setVisibility(View.VISIBLE);
            Glide.with(context).load(data.postImageUrl).into(holder.ivImage);
        } else {
            holder.ivImage.setVisibility(View.GONE);
        }

        holder.tvTitle.setText(data.postTitle);
        holder.tvDateCategory.setText(data.postTime + " / " + data.postCategory);
        holder.tvAnswer.setText("답변수 " + data.postAnswer);
        holder.tvRecommend.setText("조회수 "+data.postHit);

        if(data.postContents.length() < 60) {
            holder.tvContents.setText(data.postContents);
        } else {
            holder.tvContents.setText(data.postContents.substring(0,59)+"...");
        }

        return view;
    }


    public class ViewHolder {
        public ImageView ivImage;
        public TextView tvTitle;
        public TextView tvContents;
        public TextView tvDateCategory;
        public TextView tvAnswer;
        public TextView tvRecommend;
    }
}

