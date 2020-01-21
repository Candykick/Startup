package com.candykick.startup.view.loginAccount;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextWatcher;

import com.candykick.startup.MainActivity;
import com.candykick.startup.R;
import com.candykick.startup.engine.CdkEmptyCallback;
import com.candykick.startup.engine.UserEngine;
import com.candykick.startup.view.base.BaseActivity;
import com.candykick.startup.util.webImageGet;
import com.candykick.startup.databinding.ActivityJoinOutBinding;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.kakao.auth.ApiResponseCallback;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.UserManagement;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class JoinOutActivity extends BaseActivity<ActivityJoinOutBinding> {

    String loginmethod, nickname, email, password, uid, profile, chatid;
    int profileFlag; File tempFile;

    FirebaseStorage storage;
    StorageReference storageRef;

    FirebaseAuth emailAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding.setActivity(this);

        emailAuth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();

        Intent intent = getIntent();
        loginmethod = intent.getStringExtra("loginmethod");
        nickname = intent.getStringExtra("nickname");
        email = intent.getStringExtra("email");
        password = intent.getStringExtra("password");
        uid = intent.getStringExtra("uid");
        chatid = intent.getStringExtra("chatid");
        profile = intent.getStringExtra("profile");
        profileFlag = intent.getIntExtra("profileflag",0);
        if(profileFlag == 0)
            tempFile = (File)intent.getSerializableExtra("profileImage");
        else
            tempFile = null;

        binding.toolbar.btnToolBarLeft.setOnClickListener(v -> onBackPressed());
        binding.toolbar.tvToolBar.setText("외주사업자 상세정보 입력");

        binding.etJoinOut.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                int length = s.toString().length();
                binding.tvjoEtLength.setText(length+"자/20자");
                if(length == 20) {
                    binding.tvjoEtLength.setTextColor(Color.RED);
                } else {
                    binding.tvjoEtLength.setTextColor(getResources().getColor(R.color.set2White));
                }
            }
        });
    }

    //회원가입 버튼 클릭 시
    public void fnJoin4() {
        if(!binding.rbjoOkay.isChecked() && !binding.rbjoNope.isChecked()) {
            makeToast("세금계산서 발행 여부를 선택해주세요.");
        } else if(binding.etJoinOut.getText().toString().replace(" ","").length() == 0) {
            makeToast("한 줄 소개를 입력해주세요.");
        } else {
            Map<String, Object> user = new HashMap<>();
            user.put("loginmethod", loginmethod);
            user.put("usertype", 3);
            user.put("isadviser", false);
            user.put("username", nickname);
            user.put("userDes", binding.etJoinOut.getText().toString());
            user.put("taxinvoicepossible",binding.rbjoOkay.isChecked());
            user.put("userSatis","0.0");
            user.put("userOrderNum",0);
            user.put("userAnswerTime","확인되지 않음");
            user.put("chatid",chatid);
            user.put("email", email);

            progressOn();

            //1.프로필사진 FireStore에 업로드
            //flag == 0: 갤러리나 카메라에서 얻어온 사진. 1: 인터넷에서 가져온 사진.
            if(profileFlag == 0) {
                try {
                    // Uri 스키마를 content:///에서 file:///로 변경한다.
                    Bitmap bitmap = BitmapFactory.decodeFile(tempFile.getPath());

                    StorageReference mountainImagesRef = storageRef.child("User/"+System.currentTimeMillis()+".jpg");
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 20, baos);
                    byte[] data2 = baos.toByteArray();
                    UploadTask uploadTask = mountainImagesRef.putBytes(data2);

                    uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                        @Override
                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                            if (!task.isSuccessful()) {
                                progressOff();
                                makeToastLong("프로필 사진을 등록하지 못했습니다." + task.getException().getMessage());
                                throw task.getException();
                            }

                            return mountainImagesRef.getDownloadUrl();
                        }
                    }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if (task.isSuccessful()) {
                                String profileResult = task.getResult().toString();
                                user.put("profileimage", profileResult);

                                UserEngine.getInstance().registerUser(loginmethod, user, password, new CdkEmptyCallback() {
                                    @Override
                                    public void onSuccessed() {
                                        progressOff();

                                        Intent intent = new Intent(JoinOutActivity.this, MainActivity.class);
                                        //intent.putExtra("result", strResult);
                                        //intent.putExtra("loginmethod", loginmethod);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                        finish();
                                    }

                                    @Override
                                    public void onFailed(String error) {
                                        progressOff();

                                        if(error.equals("methodErr")) {
                                            makeToastLong(resources.getString(R.string.toastRegisterUserMethodErr));
                                        } else if(error.equals("onNotSignedUp")) {
                                            makeToastLong(resources.getString(R.string.toastKakaoNotSignedUp));
                                        } else {
                                            makeToastLong(resources.getString(R.string.toastRegisterUserErr) + error);
                                        }
                                    }
                                });
                            } else {
                                progressOff();
                                makeToastLong("프로필 사진을 등록하지 못했습니다." + task.getException().getMessage());
                            }
                        }
                    });
                } catch (Exception e) {
                    progressOff();
                    e.printStackTrace();
                    makeToastLong("프로필 사진을 등록하지 못했습니다." + e.getMessage());
                }
            } else if(profileFlag == 1) {
                try {
                    Bitmap bitmap;
                    bitmap = new webImageGet().execute(new String[]{profile}).get();
                    StorageReference mountainImagesRef = storageRef.child("User/"+System.currentTimeMillis()+".jpg");
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 20, baos);
                    byte[] data2 = baos.toByteArray();
                    UploadTask uploadTask = mountainImagesRef.putBytes(data2);

                    uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                        @Override
                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                            if (!task.isSuccessful()) {
                                progressOff();
                                makeToastLong("프로필 사진을 등록하지 못했습니다." + task.getException().getMessage());
                                throw task.getException();
                            }

                            // Continue with the task to get the download URL
                            return mountainImagesRef.getDownloadUrl();
                        }
                    }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if (task.isSuccessful()) {
                                String profileResult = task.getResult().toString();
                                user.put("profileimage", profileResult);

                                UserEngine.getInstance().registerUser(loginmethod, user, password, new CdkEmptyCallback() {
                                    @Override
                                    public void onSuccessed() {
                                        progressOff();

                                        Intent intent = new Intent(JoinOutActivity.this, MainActivity.class);
                                        //intent.putExtra("result", strResult);
                                        //intent.putExtra("loginmethod", loginmethod);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                        finish();
                                    }

                                    @Override
                                    public void onFailed(String error) {
                                        progressOff();

                                        if(error.equals("methodErr")) {
                                            makeToastLong(resources.getString(R.string.toastRegisterUserMethodErr));
                                        } else if(error.equals("onNotSignedUp")) {
                                            makeToastLong(resources.getString(R.string.toastKakaoNotSignedUp));
                                        } else {
                                            makeToastLong(resources.getString(R.string.toastRegisterUserErr) + error);
                                        }
                                    }
                                });
                            } else {
                                progressOff();
                                makeToastLong("프로필 사진을 등록하지 못했습니다." + task.getException().getMessage());
                            }
                        }
                    });
                } catch (Exception e) {
                    progressOff();
                    e.printStackTrace();
                    makeToastLong("프로필 사진을 등록하지 못했습니다." + e.getMessage());
                }
            } else {
                user.put("profileimage", profile);

                UserEngine.getInstance().registerUser(loginmethod, user, password, new CdkEmptyCallback() {
                    @Override
                    public void onSuccessed() {
                        progressOff();

                        Intent intent = new Intent(JoinOutActivity.this, MainActivity.class);
                        //intent.putExtra("result", strResult);
                        //intent.putExtra("loginmethod", loginmethod);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    }

                    @Override
                    public void onFailed(String error) {
                        progressOff();

                        if(error.equals("methodErr")) {
                            makeToastLong(resources.getString(R.string.toastRegisterUserMethodErr));
                        } else if(error.equals("onNotSignedUp")) {
                            makeToastLong(resources.getString(R.string.toastKakaoNotSignedUp));
                        } else {
                            makeToastLong(resources.getString(R.string.toastRegisterUserErr) + error);
                        }
                    }
                });
            }
        }
    }

    @Override
    protected int getLayoutId() { return R.layout.activity_join_out; }
}
