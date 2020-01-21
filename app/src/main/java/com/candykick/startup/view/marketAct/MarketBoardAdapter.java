package com.candykick.startup.view.marketAct;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.candykick.startup.R;
import com.candykick.startup.model.MarketListItemModel;

import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 가격 부분의 경우, 가격이 1개일 때에 대한 정보가 적용되어 있지 않다.
 */

public class MarketBoardAdapter extends BaseAdapter {

    DecimalFormat priceFormat = new DecimalFormat("###,###");
    private Context context = null;
    private ArrayList<MarketListItemModel> listData = new ArrayList<>();

    public MarketBoardAdapter(Context context, ArrayList<MarketListItemModel> listData) {
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
            view = inflater.inflate(R.layout.rawmarketmain, parent, false);

            holder.ivImage = view.findViewById(R.id.ivMarketMain);
            holder.tvTitle = view.findViewById(R.id.tvMarketMainName);
            holder.tvInfo = view.findViewById(R.id.tvMarketMainInfo);
            holder.tvPrice = view.findViewById(R.id.tvMarketMainPrice);

            view.setTag(holder);
        } else {
            holder = (ViewHolder)view.getTag();
        }

        MarketListItemModel data = listData.get(position);

        if(data.getMarketImage() != null) {
            holder.ivImage.setVisibility(View.VISIBLE);
            Glide.with(context).load(data.getMarketImage()).into(holder.ivImage);
        } else {
            holder.ivImage.setVisibility(View.GONE);
        }

        if(data.getMarketTitle().length() > 23) {
            holder.tvTitle.setText(data.getMarketTitle().substring(0, 23)+"...");
        } else {
            holder.tvTitle.setText(data.getMarketTitle());
        }


        holder.tvInfo.setText(data.getMarketGrade()+" | "+data.getMarketReviewNum()+"명의 평가");

        holder.tvPrice.setText(priceFormat.format(data.getMarketMinPrice())+"원부터~");

        return view;
    }


    public class ViewHolder {
        public ImageView ivImage;
        public TextView tvTitle;
        public TextView tvInfo;
        public TextView tvPrice;
    }
}
