package com.candykick.startup.view.marketAct;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;

import com.candykick.startup.R;
import com.candykick.startup.engine.CdkResponseCallback;
import com.candykick.startup.engine.MarketEngine;
import com.candykick.startup.model.MarketCommentModel;
import com.candykick.startup.model.MyUserModel;
import com.candykick.startup.view.base.BaseActivity;
import com.candykick.startup.databinding.ActivityMarketCommentBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MarketCommentActivity extends BaseActivity<ActivityMarketCommentBinding> {

    String postId; String type;
    int commentNum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding.setActivity(this);

        binding.toolbar.tvToolBar.setText(getResources().getString(R.string.strMarketCommentActivityTitle));
        binding.toolbar.btnToolBarLeft.setOnClickListener(v -> onBackPressed());

        Intent intent = getIntent();
        postId = intent.getStringExtra("postid");
        type = intent.getStringExtra("type");

        settingComment();
    }

    //댓글 등록 버튼을 눌렀을 때
    public void btnComment() {
        if(binding.etIdeaAddComment2.getText().toString().replace(" ","").length() == 0) {
            makeToast(getResources().getString(R.string.toastNoCommentErr));
            binding.etIdeaAddComment2.setText("");
        } else {
            Map<String, Object> commentData = new HashMap<>();
            commentData.put("comment", binding.etIdeaAddComment2.getText().toString());
            commentData.put("username", MyUserModel.getInstance().getUserName());
            commentData.put("profile", MyUserModel.getInstance().getProfileImage());
            commentData.put("parent", 0);
            commentData.put("seq", 0);
            commentData.put("star",Math.round(binding.ratingBar.getRating()));

            MarketEngine.getInstance().registerNewComment(type, postId, commentData, commentNum, new CdkResponseCallback<String>() {
                @Override
                public void onDataLoaded(String result, String item) {
                    binding.etIdeaAddComment2.setText("");
                    binding.ratingBar.setRating(4f);

                    settingComment();
                }

                @Override
                public void onDataNotAvailable(String error) {
                    makeToastLong(getResources().getString(R.string.toastRegCommentErr)+error);
                    finish();
                }

                @Override
                public void onDataEmpty() {
                    makeToastLong(getResources().getString(R.string.toastRegCommentErr));
                    finish();
                }
            });
        }
    }

    private void settingComment() {
        progressOn();
        MarketEngine.getInstance().getMarketComments(type, postId, new CdkResponseCallback<ArrayList<MarketCommentModel>>() {
            @Override
            public void onDataLoaded(String result, ArrayList<MarketCommentModel> item) {
                commentNum = item.size();

                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(MarketCommentActivity.this);
                binding.rvIdeaComment3.setLayoutManager(linearLayoutManager);
                MarketCommentAdapter adapter = new MarketCommentAdapter(MarketCommentActivity.this, item);
                binding.rvIdeaComment3.setAdapter(adapter);
                progressOff();
            }

            @Override
            public void onDataNotAvailable(String error) {
                progressOff();
                makeToastLong(resources.getString(R.string.toastCallInfoErr) + error);
                finish();
            }

            @Override
            public void onDataEmpty() {
                progressOff();
                makeToastLong(resources.getString(R.string.toastErr));
                finish();
            }
        });
    }

    @Override
    protected int getLayoutId() { return R.layout.activity_market_comment; }
}

/*FirebaseFirestore.getInstance().collection(type)
                .document(postId).collection("comment")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()) {
                    if (!task.getResult().isEmpty()) {
                        commentArray.clear();

                        for (QueryDocumentSnapshot document : task.getResult()) {
                            MarketCommentData data = new MarketCommentData();
                            data.id = Integer.parseInt(document.getId());
                            data.comment = document.get("comment").toString();
                            data.username = document.get("username").toString();
                            data.profile = document.get("profile").toString();
                            data.parent = ((Long)document.get("parent")).intValue();
                            data.seq = ((Long)document.get("seq")).intValue();
                            data.star = ((Long)document.get("star")).intValue();

                            commentArray.add(data);
                        }

                        //LinearLayoutManager linearLayoutManager = new LinearLayoutManager(IdeaPostActivity.this);
                        //binding.rvIdeaComment.setLayoutManager(linearLayoutManager);
                        MarketCommentAdapter adapter = new MarketCommentAdapter(MarketCommentActivity.this, commentArray);
                        binding.rvIdeaComment3.setAdapter(adapter);

                        progressOff();
                    } else {
                        progressOff();
                    }
                } else {
                    progressOff();
                    makeToastLong("불러오는 도중 오류가 발생했습니다.");
                    finish();
                }
            }
        });*/