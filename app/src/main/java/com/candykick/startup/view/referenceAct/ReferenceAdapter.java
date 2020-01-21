package com.candykick.startup.view.referenceAct;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.candykick.startup.R;
import com.candykick.startup.model.ReferenceModel;

import java.util.ArrayList;

/**
 * Created by candykick on 2019. 6. 22..
 */

public class ReferenceAdapter extends BaseAdapter {

    ArrayList<ReferenceModel> arrayList;
    Context context;
    private static LayoutInflater inflater = null;

    public ReferenceAdapter(Context context, ArrayList<ReferenceModel> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
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
            convertView = inflater.inflate(R.layout.row_reference, parent, false);
            holder = new ViewHolder();
            holder.tvFileName = convertView.findViewById(R.id.tvFileName);
            holder.ivFileImage = convertView.findViewById(R.id.ivFileImage);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder)convertView.getTag();
        }

        holder.tvFileName.setText(arrayList.get(position).getFilename());
        Glide.with(context).load(arrayList.get(position).getFileimage()).into(holder.ivFileImage);

        convertView.setOnClickListener((v) -> {
            ArrayList<String> imageList = new ArrayList<>();
            imageList.addAll(arrayList.get(position).getSubimage());

            Intent intent = new Intent(context, ReferenceSelectActivity.class);
            intent.putExtra("filename",arrayList.get(position).getFilename());
            intent.putExtra("subimage",imageList);
            intent.putExtra("filepath",arrayList.get(position).getFilepath());
            context.startActivity(intent);
        });

        return convertView;
    }

    public class ViewHolder {
        TextView tvFileName;
        ImageView ivFileImage;
    }
}
