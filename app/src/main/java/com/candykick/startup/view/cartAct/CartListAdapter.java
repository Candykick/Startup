package com.candykick.startup.view.cartAct;

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

import java.util.ArrayList;

/**
 * Created by candykick on 2019. 7. 17..
 */

public class CartListAdapter extends BaseAdapter {

    private Context context = null;
    private ArrayList<CartMainData> listData = new ArrayList<>();

    public CartListAdapter(Context context, ArrayList<CartMainData> listData) {
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
            view = inflater.inflate(R.layout.rawcart, parent, false);

            holder.ivProfile = view.findViewById(R.id.ivCartProfile);
            holder.ivProfile.setBackground(new ShapeDrawable(new OvalShape()));
            holder.ivProfile.setClipToOutline(true);
            holder.tvUserName = view.findViewById(R.id.tvCartName);
            holder.ivType = view.findViewById(R.id.tvCartAlarmType);
            holder.tvUserInfo1 = view.findViewById(R.id.tvCartUserInfo1);
            holder.tvUserInfo2 = view.findViewById(R.id.tvCartUserInfo2);
            holder.divider = view.findViewById(R.id.dividerCart);

            view.setTag(holder);
        } else {
            holder = (ViewHolder)view.getTag();
        }

        CartMainData data = listData.get(position);

        switch (data.alarmType) {
            case 6:
                holder.ivType.setImageResource(R.drawable.circle_border);
                if(position!=(listData.size()-1) && listData.get(position+1).alarmType == 5) {
                    holder.divider.setVisibility(View.VISIBLE);
                } else {
                    holder.divider.setVisibility(View.GONE);
                }
                break;
            case 5:
                holder.ivType.setImageResource(R.drawable.circle_border_orange);
                if(position!=(listData.size()-1) && listData.get(position+1).alarmType == 4) {
                    holder.divider.setVisibility(View.VISIBLE);
                } else {
                    holder.divider.setVisibility(View.GONE);
                }
                break;
            case 4:
                holder.ivType.setImageResource(R.drawable.circle_border_yellow);
                if(position!=(listData.size()-1) && listData.get(position+1).alarmType == 3) {
                    holder.divider.setVisibility(View.VISIBLE);
                } else {
                    holder.divider.setVisibility(View.GONE);
                }
                break;
            case 3:
                holder.ivType.setImageResource(R.drawable.circle_border_green);
                if(position!=(listData.size()-1) && listData.get(position+1).alarmType == 2) {
                    holder.divider.setVisibility(View.VISIBLE);
                } else {
                    holder.divider.setVisibility(View.GONE);
                }
                break;
            case 2:
                holder.ivType.setImageResource(R.drawable.circle_border_blue);
                if(position!=(listData.size()-1) && listData.get(position+1).alarmType == 1) {
                    holder.divider.setVisibility(View.VISIBLE);
                } else {
                    holder.divider.setVisibility(View.GONE);
                }
                break;
            case 1:
                holder.ivType.setImageResource(R.drawable.circle_border_navy);
                if(position!=(listData.size()-1) && listData.get(position+1).alarmType == 7) {
                    holder.divider.setVisibility(View.VISIBLE);
                } else {
                    holder.divider.setVisibility(View.GONE);
                }
                break;
            case 7:
                holder.ivType.setImageResource(R.drawable.circle_border_purple);
                holder.divider.setVisibility(View.GONE);
                break;
            default:
                holder.ivType.setImageResource(R.drawable.circle_border);
                holder.divider.setVisibility(View.GONE);
                break;
        }

        if(data.profile != null) {
            Glide.with(context).load(data.profile).into(holder.ivProfile);
        } else {
            holder.ivProfile.setImageResource(R.drawable.ic_account_circle_black_24dp);
        }

        holder.tvUserName.setText(data.userName);
        holder.tvUserInfo1.setText(data.userInfo1);
        holder.tvUserInfo2.setText(data.userInfo2);

        return view;
    }

    public void addData(CartMainData data) {
        listData.add(data);
    }
    public void removeData(CartMainData data) {
        listData.remove(data);
    }
    public ArrayList<CartMainData> getListData() { return listData; }

    public class ViewHolder {
        public ImageView ivProfile;
        public ImageView ivType;
        public TextView tvUserName;
        public TextView tvUserInfo1;
        public TextView tvUserInfo2;
        public View divider;
    }
}
