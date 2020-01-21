package com.candykick.startup.view.loginAccount;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;

import com.candykick.startup.MainActivity;
import com.candykick.startup.R;
import com.candykick.startup.engine.CdkEmptyCallback;
import com.candykick.startup.engine.UserEngine;
import com.candykick.startup.view.base.BaseActivity;
import com.candykick.startup.util.webImageGet;
import com.candykick.startup.databinding.ActivityJoinAdvBinding;
import com.candykick.startup.view.loginAccount.loginFragment.JoinAdv1Fragment;
import com.candykick.startup.view.loginAccount.loginFragment.JoinAdv2Fragment;
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
import com.google.firebase.firestore.SetOptions;
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

public class JoinAdvActivity extends BaseActivity<ActivityJoinAdvBinding> {
    
    String loginmethod, nickname, email, password, uid, profile, chatid;
    int profileFlag; File tempFile;
    FirebaseAuth emailAuth;

    JoinAdv1Fragment joinAdv1Fragment; JoinAdv2Fragment joinAdv2Fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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
        
        binding.setActivity(this);
        binding.toolbar.tvToolBar.setText("조언가 상세정보 입력");
        binding.toolbar.btnToolBarLeft.setOnClickListener(v -> onBackPressed());

        emailAuth = FirebaseAuth.getInstance();

        ArrayList<Fragment> fragments = new ArrayList<>();
        joinAdv1Fragment = new JoinAdv1Fragment(); joinAdv2Fragment = new JoinAdv2Fragment();
        fragments.add(joinAdv1Fragment); fragments.add(joinAdv2Fragment);

        JoinViewPagerAdapter adapter = new JoinViewPagerAdapter(getSupportFragmentManager(), fragments, 1);
        binding.vpja.setAdapter(adapter);
        binding.vpja.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
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

    //회원가입 버튼
    public void fnJoin3() {
        if(binding.btnjiJoin.getText().toString().equals("회원가입")) {
            if(joinAdv1Fragment.getAdvCareer().size() == 0 || joinAdv2Fragment.getAdvJob().length() == 0 || joinAdv2Fragment.getAdvIntro().length() == 0 || joinAdv2Fragment.getAdvNamecard() == null) {
            } else {
                progressOn();

                FirebaseStorage storage = FirebaseStorage.getInstance();
                StorageReference storageRef = storage.getReference();;

                Map<String, Object> user = new HashMap<>();
                user.put("loginmethod", loginmethod);
                user.put("usertype", 2);
                user.put("isadviser", true);
                user.put("username", nickname);
                user.put("career", joinAdv1Fragment.getAdvCareer());
                user.put("userintro",joinAdv2Fragment.getAdvIntro());
                user.put("userjob",joinAdv2Fragment.getAdvJob());
                user.put("chatid",chatid);
                user.put("email", email);

                //0.명함 사진 FireStore에 업로드
                try {
                    File namecardFile = joinAdv2Fragment.getAdvNamecard();
                    Bitmap nameCard = BitmapFactory.decodeFile(namecardFile.getPath());

                    StorageReference namecardImagesRef = storageRef.child("Namecard/" + System.currentTimeMillis() + ".jpg");
                    ByteArrayOutputStream baosn = new ByteArrayOutputStream();
                    nameCard.compress(Bitmap.CompressFormat.JPEG, 20, baosn);
                    byte[] data3 = baosn.toByteArray();
                    UploadTask uploadTask1 = namecardImagesRef.putBytes(data3);

                    uploadTask1.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                        @Override
                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                            if (!task.isSuccessful()) {
                                progressOff();
                                binding.toolbar.btnToolBarLeft.setOnClickListener(v -> onBackPressed());
                                makeToastLong("명함 사진을 등록하지 못했습니다." + task.getException().getMessage());
                                throw task.getException();
                            }

                            return namecardImagesRef.getDownloadUrl();
                        }
                    }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if (task.isSuccessful()) {
                                user.put("namecard", task.getResult().toString());

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
                                                    binding.toolbar.btnToolBarLeft.setOnClickListener(v -> onBackPressed());
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

                                                            Intent intent = new Intent(JoinAdvActivity.this, MainActivity.class);
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
                                                    binding.toolbar.btnToolBarLeft.setOnClickListener(v -> onBackPressed());
                                                    makeToastLong("프로필 사진을 등록하지 못했습니다." + task.getException().getMessage());
                                                }
                                            }
                                        });
                                    } catch (Exception e) {
                                        progressOff();
                                        binding.toolbar.btnToolBarLeft.setOnClickListener(v -> onBackPressed());
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
                                                    binding.toolbar.btnToolBarLeft.setOnClickListener(v -> onBackPressed());
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

                                                            Intent intent = new Intent(JoinAdvActivity.this, MainActivity.class);
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
                                                    binding.toolbar.btnToolBarLeft.setOnClickListener(v -> onBackPressed());
                                                    makeToastLong("프로필 사진을 등록하지 못했습니다." + task.getException().getMessage());
                                                }
                                            }
                                        });
                                    } catch (Exception e) {
                                        progressOff();
                                        binding.toolbar.btnToolBarLeft.setOnClickListener(v -> onBackPressed());
                                        e.printStackTrace();
                                        makeToastLong("프로필 사진을 등록하지 못했습니다." + e.getMessage());
                                    }
                                } else {
                                    user.put("profileimage", profile);

                                    UserEngine.getInstance().registerUser(loginmethod, user, password, new CdkEmptyCallback() {
                                        @Override
                                        public void onSuccessed() {
                                            progressOff();

                                            Intent intent = new Intent(JoinAdvActivity.this, MainActivity.class);
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
                            } else {
                                progressOff();
                                binding.toolbar.btnToolBarLeft.setOnClickListener(v -> onBackPressed());
                                makeToastLong("명함 사진을 등록하지 못했습니다." + task.getException().getMessage());
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressOff();
                            binding.toolbar.btnToolBarLeft.setOnClickListener(v -> onBackPressed());
                            makeToastLong("명함 사진을 등록하지 못했습니다." + e.getMessage());
                        }
                    });
                } catch (Exception e) {
                    progressOff();
                    binding.toolbar.btnToolBarLeft.setOnClickListener(v -> onBackPressed());
                    makeToastLong("회원가입을 진행할 수 없습니다." + e.getMessage());
                }
            }
        } else {
            binding.vpja.setCurrentItem(binding.vpja.getCurrentItem()+1);
        }
    }

    @Override
    protected int getLayoutId() { return R.layout.activity_join_adv; }
}
