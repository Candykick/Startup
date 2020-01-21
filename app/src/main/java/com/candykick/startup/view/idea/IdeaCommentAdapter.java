package com.candykick.startup.view.idea;

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
import com.candykick.startup.model.IdeaCommentModel;

import java.util.ArrayList;

/**
 * Created by candykick on 2019. 9. 11..
 */

public class IdeaCommentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<IdeaCommentModel> arrayList = new ArrayList<>();
    private Context context;

    public interface OnItemClickListener {
        void onItemClick(View v, int pos);
    }

    private OnItemClickListener mListener = null;

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mListener = listener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        protected View viewBlankComment;
        protected ImageView ivRawICProfile;
        protected TextView tvRawICName, tvRawICComment;

        public ViewHolder(View view) {
            super(view);
            this.viewBlankComment = view.findViewById(R.id.viewBlankComment);
            this.ivRawICProfile = view.findViewById(R.id.ivRawICProfile);
            this.tvRawICName = view.findViewById(R.id.tvRawICName);
            this.tvRawICComment = view.findViewById(R.id.tvRawICComment);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if(position != RecyclerView.NO_POSITION) {
                        mListener.onItemClick(v, position);
                    }
                }
            });
        }
    }

    public IdeaCommentAdapter(Context context, ArrayList<IdeaCommentModel> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }

    // RecyclerView에 새로운 데이터를 보여주기 위해 필요한 ViewHolder를 생성해야 할 때 호출됩니다.
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view;

        view = LayoutInflater.from(context).inflate(R.layout.raw_comment_idea,viewGroup,false);
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

            if(arrayList.get(position).getParent() == 0) {
                viewHolder.viewBlankComment.setVisibility(View.GONE);
            } else {
                viewHolder.viewBlankComment.setVisibility(View.VISIBLE);
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
