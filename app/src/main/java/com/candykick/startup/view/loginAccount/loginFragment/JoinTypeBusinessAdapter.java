package com.candykick.startup.view.loginAccount.loginFragment;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.candykick.startup.R;

import java.util.ArrayList;

/**
 * Created by candykick on 2019. 8. 4..
 */

public class JoinTypeBusinessAdapter extends BaseAdapter {

    private ArrayList<JoinTypeBusinessClass> arrayList = new ArrayList<>();
    private ArrayList<JoinTypeBusinessClass> allList = new ArrayList<>();
    Context context;
    private static LayoutInflater inflater = null;

    public JoinTypeBusinessAdapter(ArrayList<JoinTypeBusinessClass> arrayList, Context context) {
        this.context = context;
        this.arrayList = arrayList;
        this.allList.addAll(arrayList);
        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return arrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if(convertView == null) {
            convertView = inflater.inflate(R.layout.raw_type_business, parent, false);
            holder = new ViewHolder();
            holder.tvTypeBusinessCode = convertView.findViewById(R.id.tvTypeBusinessCode);
            holder.tvTypeBusinessTitle = convertView.findViewById(R.id.tvTypeBusinessTitle);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder)convertView.getTag();
        }

        holder.tvTypeBusinessCode.setText(arrayList.get(position).industryCode);
        holder.tvTypeBusinessTitle.setText(arrayList.get(position).industryName);

        return convertView;
    }

    public class ViewHolder {
        TextView tvTypeBusinessCode;
        TextView tvTypeBusinessTitle;
    }

    //검색 함수
    public void filter(String search) {
        arrayList.clear();

        if(search.length() == 0) {
            arrayList.addAll(allList);
        } else {
            for(int i=0;i<allList.size();i++) {
                JoinTypeBusinessClass typeBusinessClass = allList.get(i);
                if(typeBusinessClass.industryName.contains(search) || typeBusinessClass.industrySearch.contains(search)) {
                    arrayList.add(typeBusinessClass);
                }
            }
        }

        notifyDataSetChanged();
    }
}
