package com.candykick.startup.view.idea;

import android.content.Intent;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.bumptech.glide.Glide;
import com.candykick.startup.R;
import com.candykick.startup.engine.CdkResponseCallback;
import com.candykick.startup.engine.IdeaEngine;
import com.candykick.startup.model.IdeaCommentModel;
import com.candykick.startup.model.IdeaPostModel;
import com.candykick.startup.model.MyUserModel;
import com.candykick.startup.view.base.BaseActivity;
import com.candykick.startup.databinding.ActivityIdeaPostBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.kakao.auth.Session;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.MeV2ResponseCallback;
import com.kakao.usermgmt.response.MeV2Response;

import java.util.ArrayList;

public class IdeaPostActivity extends BaseActivity<ActivityIdeaPostBinding> {

    String postId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding.setActivity(this);

        binding.toolbar.tvToolBar.setText(getResources().getString(R.string.strIdeaBoardActivityTitle));
        binding.toolbar.btnToolBarLeft.setOnClickListener(v -> onBackPressed());

        Intent intent = getIntent();
        postId = intent.getStringExtra("id");

        binding.ivIdeaProfile.setBackground(new ShapeDrawable(new OvalShape()));
        binding.ivIdeaProfile.setClipToOutline(true);

        // 포스트 정보를 셋팅한다.
        progressOn();
        IdeaEngine.getInstance().getPost(postId, new CdkResponseCallback<IdeaPostModel>() {
            @Override
            public void onDataLoaded(String result, IdeaPostModel item) {
                binding.tvIdeaTitle.setText(item.getIdeaTitle());
                binding.tvIdeaContents.setText(item.getIdeaContent());
                Glide.with(IdeaPostActivity.this).load(item.getIdeaUserProfile()).into(binding.ivIdeaProfile);
                binding.tvIdeaUsername.setText(item.getIdeaUserName());

                // 댓글 목록 셋팅
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(IdeaPostActivity.this);
                binding.rvIdeaComment.setLayoutManager(linearLayoutManager);
                IdeaCommentAdapter adapter = new IdeaCommentAdapter(IdeaPostActivity.this, item.getCommentModels());
                adapter.setOnItemClickListener(new IdeaCommentAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(View v, int pos) {
                    }
                });
                binding.rvIdeaComment.setAdapter(adapter);

                if(item.getCommentModels().size() >= 5) {
                    binding.btnIdeaMoreComments.setVisibility(View.VISIBLE);
                } else {
                    binding.btnIdeaMoreComments.setVisibility(View.GONE);
                }

                // 게시글의 유저가 현재 로그인한 유저이면: 글 수정 버튼을 표시한다.
                if(MyUserModel.getInstance().getUserId().equals(item.getIdeaUser())) {
                    binding.btnIdeaBoardModify.setVisibility(View.VISIBLE);
                } else {
                    binding.btnIdeaBoardModify.setVisibility(View.GONE);
                }

                progressOff();
            }

            @Override
            public void onDataNotAvailable(String error) {
                progressOff();
                makeToast(getResources().getString(R.string.toastCallInfoErr) + error);
                finish();
            }

            @Override
            public void onDataEmpty() {
                progressOff();
                makeToast(getResources().getString(R.string.toastNoPost));
                finish();
            }
        });
    }

    // 글 수정 버튼을 눌렀을 때
    public void modifyIdea() {
        PopupMenu popupMenu = new PopupMenu(IdeaPostActivity.this, binding.btnIdeaBoardModify);
        MenuInflater inflater = popupMenu.getMenuInflater();
        Menu menu = popupMenu.getMenu();
        inflater.inflate(R.menu.postmenu, menu);
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.modifyPostMenu:
                        Intent intent = new Intent(IdeaPostActivity.this, IdeaUploadActivity.class);
                        intent.putExtra("isnew",false);
                        intent.putExtra("id",postId);
                        startActivity(intent);
                        break;
                    case R.id.deletePostMenu:
                        IdeaEngine.getInstance().deletePost(postId, new CdkResponseCallback<String>() {
                            @Override
                            public void onDataLoaded(String result, String item) {
                                makeToast(getResources().getString(R.string.toastDeletePost));
                                finish();
                            }

                            @Override
                            public void onDataNotAvailable(String error) {
                                makeToast(getResources().getString(R.string.toastDeletePostErr) + error);
                                finish();
                            }

                            @Override
                            public void onDataEmpty() {
                                makeToast(getResources().getString(R.string.toastDeletePostErr));
                                finish();
                            }
                        });
                        break;
                }

                return false;
            }
        });
        popupMenu.show();
    }

    // 댓글 더보기를 눌렀을 때
    public void btnOpenComment() {
        Intent intent = new Intent(IdeaPostActivity.this, IdeaCommentActivity.class);
        intent.putExtra("postid", postId);
        startActivity(intent);
    }

    @Override
    protected int getLayoutId() { return R.layout.activity_idea_post; }
}