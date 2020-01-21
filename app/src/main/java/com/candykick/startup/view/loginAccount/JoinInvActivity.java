package com.candykick.startup.view.loginAccount;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.view.ViewPager;

import com.candykick.startup.MainActivity;
import com.candykick.startup.R;
import com.candykick.startup.engine.CdkEmptyCallback;
import com.candykick.startup.engine.UserEngine;
import com.candykick.startup.view.base.BaseActivity;
import com.candykick.startup.util.webImageGet;
import com.candykick.startup.databinding.ActivityJoininvBinding;
import com.candykick.startup.view.loginAccount.loginFragment.JoinInvFragment;
import com.candykick.startup.view.loginAccount.loginFragment.JoinTypeBusinessFragment;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/*
2019.06.02
명함 미업로드시 안내 팝업 추가하기(명함 추가 없이 가입하시겠습니까?)
투자자 관련 정보가 더 필요하지 않나? 사업자번호 등등?

2019.06.06
명함 사이즈에 맞게 카메라에 가이드라인 그려주고, 해당 라인에 맞게 자동으로 명함 잘라주기
이 쪽 카메라 함수도 사진 촬영 후 뒤로가기 누르면 파일 안 지워진다.
 */

public class JoinInvActivity extends BaseActivity<ActivityJoininvBinding> {

    private File tempFile;
    FirebaseAuth emailAuth;
    String loginmethod, name, email, profile, password, uid, chatid;
    int profileFlag;

    JoinTypeBusinessFragment typeBusinessFragment; JoinInvFragment joinInvFragment;

    FirebaseStorage storage;
    StorageReference storageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding.setActivity(this);

        binding.toolbar.btnToolBarLeft.setOnClickListener(v -> onBackPressed());
        binding.toolbar.tvToolBar.setText("투자자 상세정보 입력");

        emailAuth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();

        Intent gintent = getIntent();
        loginmethod = gintent.getStringExtra("loginmethod");
        name = gintent.getStringExtra("nickname");
        email = gintent.getStringExtra("email");
        profile = gintent.getStringExtra("profile");
        profileFlag = gintent.getIntExtra("profileflag",0);
        password = gintent.getStringExtra("password");
        uid = gintent.getStringExtra("uid");
        chatid = gintent.getStringExtra("chatid");
        if(profileFlag == 0)
            tempFile = (File)gintent.getSerializableExtra("profileImage");
        else
            tempFile = null;

        typeBusinessFragment = new JoinTypeBusinessFragment(); joinInvFragment = new JoinInvFragment();
        Bundle bundle = new Bundle(); bundle.putInt("key",1); typeBusinessFragment.setArguments(bundle);
        ArrayList<Fragment> fragments = new ArrayList<>();
        fragments.add(typeBusinessFragment); fragments.add(joinInvFragment);

        JoinViewPagerAdapter adapter = new JoinViewPagerAdapter(getSupportFragmentManager(), fragments, 1);

        binding.vpji.setAdapter(adapter);
        binding.vpji.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if(position == 1) {
                    binding.btnjiJoin.setText("회원가입");
                } else {
                    binding.btnjiJoin.setText("다음으로");
                }
            }

            @Override
            public void onPageSelected(int position) {}

            @Override
            public void onPageScrollStateChanged(int state) {}
        });
    }

    //'회원가입' 버튼 클릭 시
    public void fnJoin2() {
        if(binding.btnjiJoin.getText().toString().equals("회원가입")) {
            if (typeBusinessFragment.getTypeBusiness().length() == 0) {
                makeToast("투자희망업종을 선택해주세요");
            } else {
                progressOn();

                //Firestore에 넣을 유저정보 데이터
                Map<String, Object> user = new HashMap<>();
                user.put("usertype", 1);
                user.put("loginmethod", loginmethod);
                user.put("wantinvjob", typeBusinessFragment.getTypeBusiness());
                user.put("wantinvcapital", joinInvFragment.getWantInvMoney());
                user.put("wantinvtype", joinInvFragment.getWantInvType());
                user.put("isadviser", false);
                user.put("username", name);
                user.put("chatid",chatid);
                user.put("email", email);

                File namecardFile = joinInvFragment.getNamecardFile();

                // 명함 업로드.
                // 현재 구조상 아래의 회원가입 과정과 다른 쓰레드에서 실행되며, 오류도 독립적으로 발생한다.
                if(namecardFile != null) {
                    try {
                        Bitmap namecardBitmap = BitmapFactory.decodeFile(namecardFile.getPath());

                        StorageReference nameCardRef = storageRef.child("Namecard/" + System.currentTimeMillis() + ".jpg");
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        namecardBitmap.compress(Bitmap.CompressFormat.JPEG, 20, baos);
                        byte[] data3 = baos.toByteArray();
                        UploadTask uploadTask = nameCardRef.putBytes(data3);

                        uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                            @Override
                            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                                if (!task.isSuccessful()) {
                                    makeToastLong("명함을 등록하지 못했습니다." + task.getException().getMessage());
                                    user.put("namecard", "");
                                    throw task.getException();
                                }

                                return nameCardRef.getDownloadUrl();
                            }
                        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {
                                user.put("namecard", task.getResult().toString());
                            }
                        });
                    } catch (Exception e) {
                        user.put("namecard", "");
                        makeToastLong("명함을 등록하지 못했습니다." + e.getMessage());
                    }
                } else {
                    user.put("namecard", "");
                }

                //1.프로필사진 FireStore에 업로드
                //1.프로필사진 FireStore에 업로드
                //flag == 0: 갤러리나 카메라에서 얻어온 사진. 1: 인터넷에서 가져온 사진.
                if (profileFlag == 0) {
                    try {
                        // Uri 스키마를 content:///에서 file:///로 변경한다.
                        Bitmap bitmap = BitmapFactory.decodeFile(tempFile.getPath());

                        StorageReference mountainImagesRef = storageRef.child("User/" + System.currentTimeMillis() + ".jpg");
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

                                            Intent intent = new Intent(JoinInvActivity.this, MainActivity.class);
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
                } else if (profileFlag == 1) {
                    try {
                        Bitmap bitmap;
                        bitmap = new webImageGet().execute(new String[]{profile}).get();
                        StorageReference mountainImagesRef = storageRef.child("User/" + System.currentTimeMillis() + ".jpg");
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

                                            Intent intent = new Intent(JoinInvActivity.this, MainActivity.class);
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

                            Intent intent = new Intent(JoinInvActivity.this, MainActivity.class);
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
        } else {
            binding.vpji.setCurrentItem(binding.vpji.getCurrentItem()+1);
        }
    }

    @Override
    protected int getLayoutId() { return R.layout.activity_joininv; }
}