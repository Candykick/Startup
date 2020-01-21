package com.candykick.startup.view.marketAct;

import android.content.Context;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.candykick.startup.R;
import com.candykick.startup.model.MarketCommentModel;

import java.util.ArrayList;

/**
 * Created by candykick on 2019. 9. 18..
 */

public class MarketCommentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<MarketCommentModel> arrayList = new ArrayList<>();
    private Context context;

    public class ViewHolder extends RecyclerView.ViewHolder {
        protected ImageView ivRawICProfile, star1, star2, star3, star4, star5;
        protected TextView tvRawICName, tvRawICComment;

        public ViewHolder(View view) {
            super(view);
            this.ivRawICProfile = view.findViewById(R.id.ivRawICProfile2);
            this.star1 = view.findViewById(R.id.ivRawCMStar1);
            this.star2 = view.findViewById(R.id.ivRawCMStar2);
            this.star3 = view.findViewById(R.id.ivRawCMStar3);
            this.star4 = view.findViewById(R.id.ivRawCMStar4);
            this.star5 = view.findViewById(R.id.ivRawCMStar5);
            this.tvRawICName = view.findViewById(R.id.tvRawICName2);
            this.tvRawICComment = view.findViewById(R.id.tvRawICComment2);
        }
    }

    public MarketCommentAdapter(Context context, ArrayList<MarketCommentModel> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }

    // RecyclerView에 새로운 데이터를 보여주기 위해 필요한 ViewHolder를 생성해야 할 때 호출됩니다.
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view;

        view = LayoutInflater.from(context).inflate(R.layout.raw_comment_market,viewGroup,false);
        ViewHolder viewHolder = new ViewHolder(view);
        viewHolder.ivRawICProfile.setBackground(new ShapeDrawable(new OvalShape()));
        viewHolder.ivRawICProfile.setClipToOutline(true);

        return viewHolder;
    }

    // Adapter의 특정 위치(position)에 있는 데이터를 보여줘야 할때 호출됩니다.
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder vh, int position) {
        try {
            ViewHolder viewHolder = (ViewHolder)vh;

            Glide.with(context).load(arrayList.get(position).getProfile()).into(viewHolder.ivRawICProfile);
            viewHolder.tvRawICName.setText(arrayList.get(position).getUsername());

            viewHolder.tvRawICComment.setText(arrayList.get(position).getComment());

            if(arrayList.get(position).getStar() == 5) {
                viewHolder.star1.setImageResource(R.drawable.star);
                viewHolder.star2.setImageResource(R.drawable.star);
                viewHolder.star3.setImageResource(R.drawable.star);
                viewHolder.star4.setImageResource(R.drawable.star);
                viewHolder.star5.setImageResource(R.drawable.star);
            } else if(arrayList.get(position).getStar() == 4) {
                viewHolder.star1.setImageResource(R.drawable.star);
                viewHolder.star2.setImageResource(R.drawable.star);
                viewHolder.star3.setImageResource(R.drawable.star);
                viewHolder.star4.setImageResource(R.drawable.star);
                viewHolder.star5.setImageResource(R.drawable.stargray);
            } else if(arrayList.get(position).getStar() == 3) {
                viewHolder.star1.setImageResource(R.drawable.star);
                viewHolder.star2.setImageResource(R.drawable.star);
                viewHolder.star3.setImageResource(R.drawable.star);
                viewHolder.star4.setImageResource(R.drawable.stargray);
                viewHolder.star5.setImageResource(R.drawable.stargray);
            } else if(arrayList.get(position).getStar() == 2) {
                viewHolder.star1.setImageResource(R.drawable.star);
                viewHolder.star2.setImageResource(R.drawable.star);
                viewHolder.star3.setImageResource(R.drawable.stargray);
                viewHolder.star4.setImageResource(R.drawable.stargray);
                viewHolder.star5.setImageResource(R.drawable.stargray);
            } else if(arrayList.get(position).getStar() == 1) {
                viewHolder.star1.setImageResource(R.drawable.star);
                viewHolder.star2.setImageResource(R.drawable.stargray);
                viewHolder.star3.setImageResource(R.drawable.stargray);
                viewHolder.star4.setImageResource(R.drawable.stargray);
                viewHolder.star5.setImageResource(R.drawable.stargray);
            } else {
                viewHolder.star1.setImageResource(R.drawable.stargray);
                viewHolder.star2.setImageResource(R.drawable.stargray);
                viewHolder.star3.setImageResource(R.drawable.stargray);
                viewHolder.star4.setImageResource(R.drawable.stargray);
                viewHolder.star5.setImageResource(R.drawable.stargray);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        if (arrayList == null) {
            return 0;
        }

        return arrayList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }
}
