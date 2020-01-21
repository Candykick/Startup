package com.candykick.startup.view.idea;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.candykick.startup.R;
import com.candykick.startup.model.IdeaBoardModel;

import java.util.ArrayList;

/**
 * Created by candykick on 2019. 9. 3..
 */

public class IdeaBoardAdapter extends BaseAdapter {
    private Context context = null;
    private ArrayList<IdeaBoardModel> listData = new ArrayList<>();

    public IdeaBoardAdapter(Context context, ArrayList<IdeaBoardModel> listData) {
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
            view = inflater.inflate(R.layout.rawidea_main, parent, false);

            holder.tvTitle = view.findViewById(R.id.tvIdeaMainTitle);
            holder.tvContents = view.findViewById(R.id.tvIdeaMainContent);

            view.setTag(holder);
        } else {
            holder = (ViewHolder)view.getTag();
        }

        IdeaBoardModel data = listData.get(position);

        if(data.isNotice()) {
            LinearLayout llRawIdeaMain = view.findViewById(R.id.llRawIdeaMain);
            llRawIdeaMain.setBackgroundColor(context.getResources().getColor(R.color.newsetIdeaNotice));
        }

        if(data.getIdeaTitle().length() > 17) {
            holder.tvTitle.setText(data.getIdeaTitle().substring(0, 17)+"...");
        } else {
            holder.tvTitle.setText(data.getIdeaTitle());
        }

        String contents = data.getIdeaContent().replace("\n", " ");
        if(contents.length() > 23) {
            holder.tvContents.setText(contents.substring(0, 23)+"...");
        } else {
            holder.tvContents.setText(contents);
        }

        return view;
    }


    public class ViewHolder {
        public TextView tvTitle;
        public TextView tvContents;
    }
}
