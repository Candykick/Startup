package com.candykick.startup;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by candykick on 2019. 7. 12..
 */

public class TempAdapter extends BaseAdapter {

    private ArrayList<TempClass> arrayList = new ArrayList<>();
    private ArrayList<TempClass> allList = new ArrayList<>();
    Context context;
    private static LayoutInflater inflater = null;

    public TempAdapter(ArrayList<TempClass> arrayList, Context context) {
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
            convertView = inflater.inflate(R.layout.rawtmp, parent, false);
            holder = new ViewHolder();
            holder.tvTmpCode = convertView.findViewById(R.id.tvTmpCode);
            holder.tvTmpTitle = convertView.findViewById(R.id.tvTmpTitle);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder)convertView.getTag();
        }

        holder.tvTmpCode.setText(arrayList.get(position).industryCode);
        holder.tvTmpTitle.setText(arrayList.get(position).industryName);

        return convertView;
    }

    public class ViewHolder {
        TextView tvTmpCode;
        TextView tvTmpTitle;
    }

    //검색 함수
    public void filter(String search) {
        arrayList.clear();

        if(search.length() == 0) {
            arrayList.addAll(allList);
        } else {
            for(int i=0;i<allList.size();i++) {
                TempClass tempClass = allList.get(i);
                if(tempClass.industryName.contains(search) || tempClass.industrySearch.contains(search)) {
                    arrayList.add(tempClass);
                }
            }
        }

        notifyDataSetChanged();
    }
}
