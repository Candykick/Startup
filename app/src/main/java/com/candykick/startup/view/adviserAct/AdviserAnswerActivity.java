package com.candykick.startup.view.adviserAct;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;

import com.candykick.startup.R;
import com.candykick.startup.view.base.BaseActivity;
import com.candykick.startup.databinding.ActivityAdviserAnswerBinding;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.kakao.auth.Session;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.MeV2ResponseCallback;
import com.kakao.usermgmt.response.MeV2Response;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class AdviserAnswerActivity extends BaseActivity<ActivityAdviserAnswerBinding> {

    private static final int RESULT_ERROR = 2;
    private static final int RESULT_LOGINERR = 3;

    String postId;
    int answerNum;

    TempFragment fragment;
    boolean imageerr = false;
    String errStr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding.setActivity(this);

        binding.toolbar.tvToolBar.setText("답변 등록하기");
        binding.toolbar.btnToolBarLeft.setOnClickListener(v -> onBackPressed());

        postId = getIntent().getStringExtra("id");
        answerNum = getIntent().getIntExtra("answernum",0) + 1;

        fragment = new TempFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.containerAnswer, fragment).commit();
    }

    public void fnAnswer() {
        if(fragment.getContents().replace(" ","").length()==0) {
            makeToastLong("내용을 입력해주세요.");
        } else {
            progressOn();

            // 이미지 파일 이름: startup_{시간}
            if(Session.getCurrentSession().isOpened()) {
                UserManagement.getInstance().me(new MeV2ResponseCallback() {
                    @Override
                    public void onSessionClosed(ErrorResult errorResult) {
                        progressOff();
                        makeToastLong("유저정보를 불러오지 못했습니다. 다시 시도해 주세요: " + errorResult.toString());
                        finish();
                    }

                    @Override
                    public void onSuccess(MeV2Response result) {
                        uploadAnswer(""+result.getId());
                    }
                });
            } else if(FirebaseAuth.getInstance().getCurrentUser() != null) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                uploadAnswer(user.getUid());
            } else {
                //로그아웃 수행하고 로그인 화면으로 돌아간 뒤 에러 메세지 띄움.
                progressOff();
                makeToastLong("유저가 로그인되지 않은 상태입니다. 다시 로그인해주세요.");

                Intent intent = new Intent();
                intent.putExtra("id",postId);
                setResult(RESULT_LOGINERR,intent);
                finish();
            }
        }
    }

    private void uploadAnswer(String userId) {
        Map<String, Object> answerNumData = new HashMap<>();
        answerNumData.put("answernum",answerNum);

        Map<String, Object> answerData = new HashMap<>();
        answerData.put("contents",fragment.getContents());
        answerData.put("isbest",false);
        answerData.put("recommendnum",0);
        answerData.put("userid",userId);

        FirebaseFirestore.getInstance().collection("Adviser")
                .document(postId).set(answerNumData, SetOptions.merge())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        FirebaseFirestore.getInstance().collection("Adviser")
                                .document(postId).collection("answer").document(""+answerNum).set(answerData, SetOptions.merge())
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void avoid) {
                                        if(fragment.getImages().size() == 0) {
                                            progressOff();
                                            Intent intent = new Intent();
                                            intent.putExtra("id", postId);
                                            setResult(RESULT_OK, intent);
                                            finish();
                                        } else {
                                            for(int i=0;i<fragment.getImages().size();i++) {
                                                final int index = i;
                                                File image = fragment.getImages().get(index);

                                                final StorageReference imageRef = FirebaseStorage.getInstance().getReference().child("Adv/"+System.currentTimeMillis()+postId+".jpg");
                                                Bitmap bitmap = BitmapFactory.decodeFile(image.getPath());
                                                ByteArrayOutputStream baos = new ByteArrayOutputStream();

                                                Log.d("Startup",""+index);
                                                Log.d("Startup",image.getPath());

                                                bitmap.compress(Bitmap.CompressFormat.JPEG, 20, baos);
                                                byte[] data2 = baos.toByteArray();

                                                imageRef.putBytes(data2).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                                                    @Override
                                                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                                                        if (!task.isSuccessful()) {
                                                            imageerr = true;
                                                            errStr = "프로필 사진을 등록하지 못했습니다." + task.getException().getMessage();
                                                            throw task.getException();
                                                        }

                                                        return imageRef.getDownloadUrl();
                                                    }
                                                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Uri> task) {
                                                        if (task.isSuccessful()) {
                                                            String imageSrc = task.getResult().toString();

                                                            Map<String, Object> map = new HashMap<>();
                                                            map.put("image", imageSrc);

                                                            FirebaseFirestore.getInstance().collection("Adviser")
                                                                    .document(postId).collection("answer").document(""+answerNum)
                                                                    .collection("image").document(index+"").set(map)
                                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                        @Override
                                                                        public void onSuccess(Void aVoid) {
                                                                            if(index == fragment.getImages().size()-1) {
                                                                                progressOff();
                                                                                if(imageerr) {
                                                                                    makeToast(errStr);
                                                                                } else {
                                                                                    makeToast("글을 성공적으로 등록했습니다.");
                                                                                }
                                                                                progressOff();
                                                                                Intent intent = new Intent();
                                                                                intent.putExtra("id", postId);
                                                                                setResult(RESULT_OK, intent);
                                                                                finish();
                                                                            }
                                                                        }
                                                                    }).addOnFailureListener(new OnFailureListener() {
                                                                @Override
                                                                public void onFailure(@NonNull Exception e) {
                                                                    if(index == fragment.getImages().size()-1) {
                                                                        progressOff();
                                                                        makeToastLong("사진 등록에 실패했습니다. 아래 왼쪽의 버튼을 눌러서 사진을 다시 추가할 수 있습니다.");
                                                                        progressOff();
                                                                        Intent intent = new Intent();
                                                                        intent.putExtra("id", postId);
                                                                        setResult(RESULT_OK, intent);
                                                                        finish();
                                                                    }
                                                                }
                                                            });
                                                        }
                                                    }
                                                });
                                            }
                                        }
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        progressOff();
                                        makeToastLong("글을 등록하는 도중 오류가 발생했습니다. 오류: " + e.toString());
                                        Intent intent = new Intent();
                                        intent.putExtra("id",postId);
                                        setResult(RESULT_ERROR,intent);
                                        finish();
                                    }
                                });
                    }
                });
    }

    @Override
    protected int getLayoutId() { return R.layout.activity_adviser_answer; }
}
