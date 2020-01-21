package com.candykick.startup.view.idea;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.candykick.startup.R;
import com.candykick.startup.engine.CdkResponseCallback;
import com.candykick.startup.engine.IdeaEngine;
import com.candykick.startup.model.IdeaPostModel;
import com.candykick.startup.view.base.BaseActivity;
import com.candykick.startup.databinding.ActivityIdeaUploadBinding;
import com.candykick.startup.view.loginAccount.StartActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.kakao.auth.Session;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.MeV2ResponseCallback;
import com.kakao.usermgmt.response.MeV2Response;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class IdeaUploadActivity extends BaseActivity<ActivityIdeaUploadBinding> {

    String postid="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding.setActivity(this);

        binding.toolbar.tvToolBar.setText("새 아이디어 올리기");
        binding.toolbar.btnToolBarLeft.setOnClickListener(v -> onBackPressed());

        Intent intent = getIntent();
        if(!intent.getBooleanExtra("isnew",true)) {
            postid = intent.getStringExtra("id");
            IdeaEngine.getInstance().getPost(postid, new CdkResponseCallback<IdeaPostModel>() {
                @Override
                public void onDataLoaded(String result, IdeaPostModel item) {
                    binding.etIdeaUploadTitle.setText(item.getIdeaTitle());
                    binding.etIdeaUploadContents.setText(item.getIdeaContent());
                }

                @Override
                public void onDataNotAvailable(String error) {
                    makeToast(getResources().getString(R.string.toastCallInfoErr) + error);
                    finish();
                }

                @Override
                public void onDataEmpty() {
                    makeToast(getResources().getString(R.string.toastCallInfoErr));
                    finish();
                }
            });
        }
    }

    //'등록하기' 버튼 클릭 시
    public void fnUpload() {
        if(binding.etIdeaUploadTitle.getText().toString().replace(" ","").length()==0) {
            binding.etIdeaUploadTitle.setText("");
            makeToastLong("제목을 입력해주세요.");
        } else if(binding.etIdeaUploadContents.getText().toString().replace(" ","").length()==0) {
            binding.etIdeaUploadContents.setText("");
            makeToastLong("내용을 입력해주세요.");
        } else {
            progressOn();
            IdeaEngine.getInstance().uploadPost(postid, binding.etIdeaUploadTitle.getText().toString(),
                    binding.etIdeaUploadContents.getText().toString(), new CdkResponseCallback<String>() {
                        @Override
                        public void onDataLoaded(String result, String postId) {
                            progressOff();
                            Intent intent = new Intent(IdeaUploadActivity.this, IdeaPostActivity.class);
                            intent.putExtra("id", postId);
                            //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            finish();
                        }

                        @Override
                        public void onDataNotAvailable(String error) {
                            progressOff();
                            if(error.equals("")) {
                                makeToastLong(getResources().getString(R.string.toastNotLogined));

                                Intent intent = new Intent(IdeaUploadActivity.this, StartActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                finish();
                            } else {
                                makeToastLong(getResources().getString(R.string.toastRegPostErr) + error);
                                finish();
                            }
                        }

                        @Override
                        public void onDataEmpty() {
                            progressOff();
                            makeToastLong(getResources().getString(R.string.toastNotLogined));

                            Intent intent = new Intent(IdeaUploadActivity.this, StartActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            finish();
                        }
                    });
        }
    }

    @Override
    protected int getLayoutId() { return R.layout.activity_idea_upload; }
}
