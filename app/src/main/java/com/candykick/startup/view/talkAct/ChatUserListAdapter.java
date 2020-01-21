package com.candykick.startup.view.talkAct;

import android.content.Context;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.candykick.startup.R;
import com.candykick.startup.model.ChatUserModel;

import java.util.ArrayList;

/**
 * Created by candykick on 2019. 7. 7..
 */

public class ChatUserListAdapter extends BaseAdapter {

    private Context context = null;
    private ArrayList<ChatUserModel> listData;
    private String[] strUserType;

    public ChatUserListAdapter(Context context, ArrayList<ChatUserModel> listData) {
        super();
        this.context = context;
        this.listData = listData;

        strUserType = context.getResources().getStringArray(R.array.strUserType);
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
            view = inflater.inflate(R.layout.rawuserinfo, parent, false);

            holder.ivProfile = view.findViewById(R.id.ivUserRaw);
            holder.ivProfile.setBackground(new ShapeDrawable(new OvalShape()));
            holder.ivProfile.setClipToOutline(true);
            holder.tvUserName = view.findViewById(R.id.tvUserNameRaw);
            holder.tvUserType = view.findViewById(R.id.tvUserTypeRaw);

            view.setTag(holder);
        } else {
            holder = (ViewHolder)view.getTag();
        }

        ChatUserModel data = listData.get(position);

        if(data.getProfileImage() != null) {
            holder.ivProfile.setVisibility(View.VISIBLE);
            Glide.with(context).load(data.getProfileImage()).into(holder.ivProfile);
        } else {
            holder.ivProfile.setImageResource(R.drawable.ic_account_circle_black_24dp);
        }

        holder.tvUserName.setText(data.getUserName());
        holder.tvUserType.setText(strUserType[(int)(data.getUserType())]);

        return view;
    }


    public class ViewHolder {
        public ImageView ivProfile;
        public TextView tvUserName;
        public TextView tvUserType;
    }
}
