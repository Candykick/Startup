package com.candykick.startup.view.adviserAct;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;

import com.candykick.startup.R;
import com.candykick.startup.view.base.BaseActivity;
import com.candykick.startup.databinding.ActivityAdviserUploadBinding;
import com.candykick.startup.view.loginAccount.JoinViewPagerAdapter;
import com.candykick.startup.view.loginAccount.StartActivity;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class AdviserUploadActivity extends BaseActivity<ActivityAdviserUploadBinding> {

    String postid = "";
    AdviserUpload1Fragment upload1Fragment; AdviserUpload2Fragment upload2Fragment;
    boolean imageerr = false;
    String errStr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding.setActivity(this);

        binding.toolbar.tvToolBar.setText("새 질문 올리기");
        binding.toolbar.btnToolBarLeft.setOnClickListener(v -> onBackPressed());

        upload1Fragment = new AdviserUpload1Fragment(); upload2Fragment = new AdviserUpload2Fragment();
        ArrayList<Fragment> fragments = new ArrayList<>();
        fragments.add(upload1Fragment); fragments.add(upload2Fragment);
        JoinViewPagerAdapter adapter = new JoinViewPagerAdapter(getSupportFragmentManager(), fragments, 1);
        binding.vpAdvUpload.setAdapter(adapter);
        binding.vpAdvUpload.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if(position == 1) {
                    binding.btnAdvUpload.setText("글 올리기");
                } else {
                    binding.btnAdvUpload.setText("다음으로");
                }
            }

            @Override
            public void onPageSelected(int position) {}

            @Override
            public void onPageScrollStateChanged(int state) {}
        });

        Intent intent = getIntent();
        if(!intent.getBooleanExtra("isnew",true)) {
            postid = intent.getStringExtra("id");
            upload1Fragment.setTitle(intent.getStringExtra("title"));
            upload2Fragment.setContents(intent.getStringExtra("contents"));
            upload1Fragment.setCategory(intent.getStringExtra("category"));

            if(intent.getStringExtra("writer").equals("비공개")) {
                upload1Fragment.setIsWriterOpen(true);
            } else {
                upload1Fragment.setIsWriterOpen(false);
            }
        }
    }

    //'등록하기' 버튼 클릭 시
    public void btnUpload() {
        if(binding.btnAdvUpload.getText().toString().equals("다음으로")) {
            binding.vpAdvUpload.setCurrentItem(1);
        } else {
            if(upload1Fragment.getTitle().replace(" ","").length()==0) {
                upload1Fragment.setTitle("");
                makeToastLong("제목을 입력해주세요.");
            } else if(upload2Fragment.getContents().replace(" ","").length()==0) {
                upload2Fragment.setContents("");
                makeToastLong("내용을 입력해주세요.");
            } else {
                progressOn();

                // 질문 이름: adv+연도 및 시간
                if(postid.equals("")) {
                    String timeStamp = new SimpleDateFormat("yyMMddHHmmss").format(new Date());
                    postid = "adv" + timeStamp;
                }

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
                            Map<String, Object> advData = new HashMap<>();
                            advData.put("answernum",0);
                            advData.put("category",upload1Fragment.getCategory());
                            advData.put("contents", upload2Fragment.getContents());
                            advData.put("hit",0);
                            advData.put("image","");
                            advData.put("time",new SimpleDateFormat("yyMMddHHmmss").format(new Date()).toString());
                            advData.put("title", upload1Fragment.getTitle());
                            advData.put("userid", ""+result.getId());
                            postid = postid+result.getId();

                            if(upload1Fragment.getIsWriterOpen()) {
                                advData.put("quser","비공개");
                                upload(advData);
                            } else {
                                findUserName(""+result.getId(), advData);
                            }
                        }
                    });
                } else if(FirebaseAuth.getInstance().getCurrentUser() != null) {
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                    Map<String, Object> advData = new HashMap<>();
                    advData.put("answernum",0);
                    advData.put("category",upload1Fragment.getCategory());
                    advData.put("contents", upload2Fragment.getContents());
                    advData.put("hit",0);
                    advData.put("image","");
                    advData.put("time",new SimpleDateFormat("yyMMddHHmmss").format(new Date()).toString());
                    advData.put("title", upload1Fragment.getTitle());
                    advData.put("userid", user.getUid());
                    postid = postid+user.getUid();

                    if(upload1Fragment.getIsWriterOpen()) {
                        advData.put("quser","비공개");
                        upload(advData);
                    } else {
                        findUserName(user.getUid(), advData);
                    }
                } else {
                    //로그아웃 수행하고 로그인 화면으로 돌아간 뒤 에러 메세지 띄움.
                    progressOff();
                    makeToastLong("유저가 로그인되지 않은 상태입니다. 다시 로그인해주세요.");

                    Intent intent = new Intent(AdviserUploadActivity.this, StartActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                }
            }
        }
    }

    private void findUserName(String userid, Map<String, Object> data) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("UserCeo").document(userid);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();

                    if (document.exists()) {
                        data.put("quser",document.get("username").toString());

                        upload(data);
                    }
                }
            }
        });
    }

    private void upload(Map<String, Object> data) {
        FirebaseFirestore.getInstance().collection("Adviser")
                .document(postid).set(data, SetOptions.merge())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void avoid) {
                        if(upload1Fragment.getImageSrc().size() == 0) {
                            progressOff();
                            Intent intent = new Intent(AdviserUploadActivity.this, AdviserPostActivity.class);
                            intent.putExtra("id",postid);
                            //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            finish();
                        } else {
                            for(int i=0;i<upload1Fragment.getImageSrc().size();i++) {
                                final int index = i;
                                File image = upload1Fragment.getImageSrc().get(index);

                                final StorageReference imageRef = FirebaseStorage.getInstance().getReference().child("Adv/"+System.currentTimeMillis()+".jpg");
                                Bitmap bitmap = BitmapFactory.decodeFile(image.getPath());
                                ByteArrayOutputStream baos = new ByteArrayOutputStream();
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
                                                    .document(postid).collection("image").document(""+index).set(map)
                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            if(index == upload1Fragment.getImageSrc().size()-1) {
                                                                progressOff();
                                                                if(imageerr) {
                                                                    makeToast(errStr);
                                                                } else {
                                                                    makeToast("글을 성공적으로 등록했습니다.");
                                                                }
                                                                Intent intent = new Intent(AdviserUploadActivity.this, AdviserPostActivity.class);
                                                                intent.putExtra("id",postid);
                                                                //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                                                startActivity(intent);
                                                                finish();
                                                            }
                                                        }
                                                    }).addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            if(index == upload1Fragment.getImageSrc().size()-1) {
                                                                progressOff();
                                                                makeToastLong("사진 등록에 실패했습니다. 아래 왼쪽의 버튼을 눌러서 사진을 다시 추가할 수 있습니다.");
                                                                Intent intent = new Intent(AdviserUploadActivity.this, AdviserPostActivity.class);
                                                                intent.putExtra("id", postid);
                                                                //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                                                startActivity(intent);
                                                                finish();
                                                            }
                                                        }
                                                    });
                                        } else {
                                            if(index == upload1Fragment.getImageSrc().size()-1) {
                                                progressOff();
                                                makeToastLong("사진 등록에 실패했습니다. 아래 왼쪽의 버튼을 눌러서 사진을 다시 추가할 수 있습니다.");
                                                Intent intent = new Intent(AdviserUploadActivity.this, AdviserPostActivity.class);
                                                intent.putExtra("id", postid);
                                                //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                                startActivity(intent);
                                                finish();
                                            }
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
                    }
                });
    }

    @Override
    protected int getLayoutId() { return R.layout.activity_adviser_upload; }
}
