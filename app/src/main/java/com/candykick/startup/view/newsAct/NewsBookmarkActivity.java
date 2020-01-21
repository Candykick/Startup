package com.candykick.startup.view.newsAct;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;

import com.candykick.startup.R;
import com.candykick.startup.view.base.BaseActivity;
import com.candykick.startup.databinding.ActivityNewsBookmarkBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class NewsBookmarkActivity extends BaseActivity<ActivityNewsBookmarkBinding> {

    private ArrayList<NewsData> arrayList = new ArrayList<>();
    String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding.setActivity(this);

        binding.toolbar.btnToolBarLeft.setOnClickListener(v -> onBackPressed());
        binding.toolbar.tvToolBar.setText("북마크한 뉴스");

        userId = getIntent().getStringExtra("id");

        FirebaseFirestore.getInstance().collection("UserCeo")
                .document(userId).collection("bookmark").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()) {
                    if (!task.getResult().isEmpty()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            NewsData newsData = new NewsData();
                            newsData.strImgUrl = document.get("imgurl").toString();
                            newsData.strContent = document.get("contents").toString();
                            newsData.strlink = document.get("link").toString();
                            newsData.strTitle = document.get("title").toString();
                            newsData.isBookmark = true;

                            if(newsData.strContent.length() > 10) {
                                newsData.strContent = newsData.strContent.substring(0, 10) + "...";
                            }

                            arrayList.add(newsData);
                        }

                        binding.lvBookmark.setVisibility(View.VISIBLE);
                        binding.llNoBookmark.setVisibility(View.GONE);

                        NewsListAdapter adapter = new NewsListAdapter(NewsBookmarkActivity.this, arrayList, userId);
                        binding.lvBookmark.setAdapter(adapter);
                        binding.lvBookmark.setOnItemClickListener((parentView, view, position, id) -> {
                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(arrayList.get(position).strlink));
                            startActivity(intent);
                        });
                    } else {
                        binding.lvBookmark.setVisibility(View.GONE);
                        binding.llNoBookmark.setVisibility(View.VISIBLE);
                    }
                }
            }
        });
    }

    @Override
    protected int getLayoutId() { return R.layout.activity_news_bookmark; }
}
