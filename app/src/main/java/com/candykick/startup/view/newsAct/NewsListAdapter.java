package com.candykick.startup.view.newsAct;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.candykick.startup.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by candykick on 2019. 6. 9..
 */

public class NewsListAdapter extends BaseAdapter {

    private Context context = null;
    private ArrayList<NewsData> listData = new ArrayList<>();
    private String userId;

    public NewsListAdapter(Context context, ArrayList<NewsData> listData, String userId) {
        super();
        this.context = context;
        this.listData = listData;
        this.userId = userId;
    }

    public NewsListAdapter(Context context, ArrayList<NewsData> listData, String userId, ArrayList<String> bookmarkLink) {
        super();
        this.context = context;
        this.listData = listData;
        this.userId = userId;

        for(String link: bookmarkLink) {
            for(NewsData data: listData) {
                if(data.strlink.equals(link)) {
                    data.isBookmark = true;
                }
            }
        }
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
            view = inflater.inflate(R.layout.newsraw, parent, false);

            holder.ivNewsImage = view.findViewById(R.id.ivNewsRaw);
            holder.tvNewsTitle = view.findViewById(R.id.tvNewsTitle);
            holder.tvNewsContent = view.findViewById(R.id.tvNewsContent);
            holder.btnNewsBookmark = view.findViewById(R.id.btnNewsBookmark);

            view.setTag(holder);
        } else {
            holder = (ViewHolder)view.getTag();
        }

        NewsData data = listData.get(position);

        if(!data.strImgUrl.equals("")) {
            holder.ivNewsImage.setVisibility(View.VISIBLE);
            Glide.with(context).load(data.strImgUrl).into(holder.ivNewsImage);
        } else {
            holder.ivNewsImage.setVisibility(View.GONE);
        }

        holder.tvNewsTitle.setText(data.strTitle);
        holder.tvNewsContent.setText(data.strContent);

        if(data.isBookmark) {
            holder.btnNewsBookmark.setImageResource(R.drawable.bookmarkon);
        } else {
            holder.btnNewsBookmark.setImageResource(R.drawable.bookmarkoff);
        }
        holder.btnNewsBookmark.setOnClickListener(v -> {
            //progressOn();
            String documentId = data.strlink.substring(30, 36);

            if(!data.isBookmark) {
                Map<String, Object> bookmark = new HashMap<>();
                bookmark.put("title", data.strTitle);
                bookmark.put("contents", data.strContent);
                bookmark.put("imgurl", data.strImgUrl);
                bookmark.put("link", data.strlink);

                FirebaseFirestore.getInstance().collection("UserCeo")
                        .document(userId).collection("bookmark").document(documentId).set(bookmark)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void avoid) {
                                //progressOff();
                                Toast.makeText(context, "북마크를 추가했습니다.", Toast.LENGTH_SHORT).show();
                                data.isBookmark = true;
                                holder.btnNewsBookmark.setImageResource(R.drawable.bookmarkon);
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                //progressOff();
                                Toast.makeText(context, "북마크를 추가하는 도중 오류가 발생했습니다. 오류: " + e.toString(), Toast.LENGTH_LONG).show();
                            }
                        });
            } else {
                FirebaseFirestore.getInstance().collection("UserCeo")
                        .document(userId).collection("bookmark").document(documentId)
                        .delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(context, "북마크를 제거했습니다.", Toast.LENGTH_SHORT).show();
                        data.isBookmark = false;
                        holder.btnNewsBookmark.setImageResource(R.drawable.bookmarkoff);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context, "북마크를 삭제하는 도중 오류가 발생했습니다.", Toast.LENGTH_LONG).show();
                    }
                });
            }
        });

        return view;
    }


    public class ViewHolder {
        public ImageView ivNewsImage;
        public TextView tvNewsTitle;
        public TextView tvNewsContent;
        public ImageButton btnNewsBookmark;
    }
}
