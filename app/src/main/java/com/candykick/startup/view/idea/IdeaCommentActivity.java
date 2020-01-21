package com.candykick.startup.view.idea;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import com.candykick.startup.R;
import com.candykick.startup.engine.CdkResponseCallback;
import com.candykick.startup.engine.IdeaEngine;
import com.candykick.startup.model.IdeaCommentModel;
import com.candykick.startup.model.MyUserModel;
import com.candykick.startup.view.base.BaseActivity;
import com.candykick.startup.databinding.ActivityIdeaCommentBinding;
import com.candykick.startup.view.loginAccount.StartActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class IdeaCommentActivity extends BaseActivity<ActivityIdeaCommentBinding> {

    String postId;
    int parent = 0, seq = 0;

    IdeaCommentAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding.setActivity(this);

        binding.toolbar.tvToolBar.setText(getResources().getString(R.string.strIdeaCommentActivityTitle));
        binding.toolbar.btnToolBarLeft.setOnClickListener(v -> onBackPressed());

        progressOn();

        Intent intent = getIntent();
        postId = intent.getStringExtra("postid");

        // 댓글 리스트 셋팅
        settingCommentList();
    }

    // 댓글 등록 버튼을 눌렀을 때
    public void btnComment() {
        if(binding.etIdeaAddComment.getText().toString().replace(" ","").length() == 0) {
            makeToast("댓글을 입력해주세요.");
            binding.etIdeaAddComment.setText("");
        } else {
            IdeaCommentModel commentModel = new IdeaCommentModel();
            commentModel.setId((adapter.getItemCount()+1));
            commentModel.setComment(binding.etIdeaAddComment.getText().toString());
            commentModel.setParent(parent);
            commentModel.setSeq(seq);
            commentModel.setUsername(MyUserModel.getInstance().getUserName());
            commentModel.setProfile(MyUserModel.getInstance().getProfileImage());

            IdeaEngine.getInstance().uploadComment(postId, commentModel, new CdkResponseCallback<String>() {
                @Override
                public void onDataLoaded(String result, String item) {
                    binding.etIdeaAddComment.setText("");
                    parent = 0;
                    seq = 0;

                    settingCommentList();
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

    // 댓글 리스트 셋팅
    private void settingCommentList() {
        IdeaEngine.getInstance().getAllComments(postId, new CdkResponseCallback<ArrayList<IdeaCommentModel>>() {
            @Override
            public void onDataLoaded(String result, ArrayList<IdeaCommentModel> item) {
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(IdeaCommentActivity.this);
                binding.rvIdeaComment2.setLayoutManager(linearLayoutManager);
                adapter = new IdeaCommentAdapter(IdeaCommentActivity.this, item);
                adapter.setOnItemClickListener(new IdeaCommentAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(View v, int pos) {
                        String result = item.get(pos).getUsername();
                        parent = item.get(pos).getParent();
                        seq = item.get(pos).getSeq()+1;

                        if(parent==0) {
                            parent = item.get(pos).getId();

                            for(int i=0;i<item.size();i++) {
                                if(item.get(i).getParent() == parent) {
                                    if(item.get(i).getSeq() > seq)
                                        seq = item.get(i).getSeq()+1;
                                }
                            }
                        }

                        binding.etIdeaAddComment.setUserTag(result);
                    }
                });
                binding.rvIdeaComment2.setAdapter(adapter);
            }

            @Override
            public void onDataNotAvailable(String error) {
                makeToastLong(getResources().getString(R.string.toastCallInfoErr) + error);
                finish();
            }

            @Override
            public void onDataEmpty() {
                makeToastLong(getResources().getString(R.string.toastCallInfoErr));
                finish();
            }
        });
    }

    @Override
    protected int getLayoutId() { return R.layout.activity_idea_comment; }
}