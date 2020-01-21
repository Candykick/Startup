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
 * Created by candykick on 2019. 8. 10..
 */

public class AdviserBoardAdapter extends BaseAdapter {
    private Context context = null;
    private ArrayList<AdviserBoardData> listData = new ArrayList<>();

    public AdviserBoardAdapter(Context context, ArrayList<AdviserBoardData> listData) {
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
            view = inflater.inflate(R.layout.rawadviserboard, parent, false);

            holder.ivImage = view.findViewById(R.id.ivAdviserRaw);
            holder.tvTitle = view.findViewById(R.id.tvAdviserName);
            holder.tvJob = view.findViewById(R.id.tvAdviserJob);
            holder.tvIntro = view.findViewById(R.id.tvAdviserIntro);

            view.setTag(holder);
        } else {
            holder = (ViewHolder)view.getTag();
        }

        AdviserBoardData data = listData.get(position);

        if(data.adviserNameCard != null) {
            holder.ivImage.setVisibility(View.VISIBLE);
            Glide.with(context).load(data.adviserNameCard).into(holder.ivImage);
        } else {
            holder.ivImage.setVisibility(View.GONE);
        }

        holder.tvTitle.setText(data.adviserName);
        holder.tvJob.setText(data.adviserJob);
        holder.tvIntro.setText(data.adviserIntro);

        return view;
    }


    public class ViewHolder {
        public ImageView ivImage;
        public TextView tvTitle;
        public TextView tvJob;
        public TextView tvIntro;
    }
}
