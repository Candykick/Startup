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
import com.candykick.startup.model.MyUserModel;
import com.candykick.startup.view.base.BaseActivity;
import com.candykick.startup.util.webImageGet;
import com.candykick.startup.databinding.ActivityJoinceoBinding;
import com.candykick.startup.view.loginAccount.loginFragment.JoinLicenseeFragment;
import com.candykick.startup.view.loginAccount.loginFragment.JoinPersonalFragment;
import com.candykick.startup.view.loginAccount.loginFragment.JoinTypeBusinessFragment;
import com.candykick.startup.view.loginAccount.loginFragment.JoinWantInvFragment;
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
1. 신청서마냥 단 나눠놓은 게 더 안 좋은듯. 핸드폰 화면은 작으니까.
태블릿은 저런 식으로 가되, 일반 스마트폰에선 한 화면 안에 표현하도록 하기.

2.ExpandableListView 활용하는 것도 고려해보기. 단, 뭔가 쓸 게 많아 보이는 효과가 있을 수도 있어서 고민.

3.사업장등록 여부의 O,X 표시는 서술형으로 쓰기: 사업자등록 함, 올해 중 등록 예정, 아직 계획 없음 등등등
그리고 사업자번호 입력하는 란 넣기 -> 입력란 필요없음. 실명인증할 때 같이 할 수 있다.

4.역할의 '기타' 클릭 시 어떤 일 하는지 적을 수 있게 하기

5.중요한 부분!
현 상태의 레아아웃 배치는 절.대.로. 사용할 수 없다. 정사각형에 가까운 화면일 경우 무조건 아래가 짤리기 때문. 또한 작은 화면에서 누르기 불편하다.
일단 기능 먼저 구현하고, 개선사항을 만들어낼 것.

6.여기도 명함 넣을 수 있으면 좋을 듯.

7.사업장등록 여부->사업장유무
 */

public class JoinCeoActivity extends BaseActivity<ActivityJoinceoBinding> {

    String loginmethod, nickname, email, password, uid, profile, chatid;
    int profileFlag; File tempFile;
    JoinPersonalFragment personalFragment; JoinLicenseeFragment licenseeFragment;
    JoinTypeBusinessFragment typeBusinessFragment; JoinWantInvFragment wantInvFragment;

    FirebaseStorage storage;
    StorageReference storageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding.setActivity(this);

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
        binding.toolbar.tvToolBar.setText("창업희망자 상세정보 입력");

        personalFragment = new JoinPersonalFragment(); licenseeFragment = new JoinLicenseeFragment();
        typeBusinessFragment = new JoinTypeBusinessFragment(); wantInvFragment = new JoinWantInvFragment();
        Bundle bundle = new Bundle(); bundle.putInt("key",0); typeBusinessFragment.setArguments(bundle);
        ArrayList<Fragment> fragments = new ArrayList<>();
        fragments.add(personalFragment);fragments.add(licenseeFragment);
        fragments.add(typeBusinessFragment);fragments.add(wantInvFragment);

        JoinViewPagerAdapter adapter = new JoinViewPagerAdapter(getSupportFragmentManager(), fragments, 0);

        binding.vpci.setAdapter(adapter);
        binding.vpci.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if(position == 3) {
                    binding.btnJcNext1.setText("회원가입");
                } else {
                    binding.btnJcNext1.setText("다음으로");
                }
            }

            @Override
            public void onPageSelected(int position) {}

            @Override
            public void onPageScrollStateChanged(int state) {}
        });
    }

    public void btnJcNext() {
        if(binding.btnJcNext1.getText().toString().equals("회원가입")) {
            if(personalFragment.getGender() == 0) {
                makeToast("성별을 선택해주세요.");
            } else if(licenseeFragment.getOfficeInfo() == 0) {
                makeToast("사업장 여부를 선택해주세요.");
            } else if(typeBusinessFragment.getTypeBusiness().length() == 0) {
                makeToast("업종을 선택해주세요.");
            }else {
                boolean isoffice = (personalFragment.getGender() == 1) ? true : false;
                Map<String, Object> user = new HashMap<>();
                user.put("loginmethod", loginmethod);
                user.put("usertype", 0);
                user.put("gender", personalFragment.getGender());
                user.put("age", personalFragment.getCeoAge());
                user.put("role", personalFragment.getCeoRole());
                user.put("isoffice", isoffice);
                user.put("email", email);
                user.put("businesstype", typeBusinessFragment.getTypeBusiness());
                user.put("istypeopen", typeBusinessFragment.isTypeOpen());
                user.put("mycapital", wantInvFragment.getMyCapital());
                user.put("wantcapital", wantInvFragment.getWantCapital());
                user.put("iswantcapitalopen", wantInvFragment.isWantCapitalOpen());
                user.put("investtype", wantInvFragment.getInvestType());
                user.put("isinvesttypeopen", wantInvFragment.isInvestTypeOpen());
                user.put("isadviser", false);
                user.put("username", nickname);
                user.put("chatid",chatid);

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

                                            Intent intent = new Intent(JoinCeoActivity.this, MainActivity.class);
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

                                            Intent intent = new Intent(JoinCeoActivity.this, MainActivity.class);
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

                            Intent intent = new Intent(JoinCeoActivity.this, MainActivity.class);
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
            binding.vpci.setCurrentItem(binding.vpci.getCurrentItem()+1);
        }
    }

    @Override
    protected int getLayoutId() { return R.layout.activity_joinceo; }
}

/*public class JoinCeoActivity extends BaseActivity<ActivityJoinceoBinding> {

    String loginmethod, name, email, profile, password;

    FirebaseAuth emailAuth;
    String strResult = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding.setActivity(this);

        binding.toolbar.btnToolBarLeft.setOnClickListener(v -> onBackPressed());
        binding.toolbar.tvToolBar.setText("사업자 상세정보 입력");

        emailAuth = FirebaseAuth.getInstance();

        Intent gintent = getIntent();
        loginmethod = gintent.getStringExtra("loginmethod");
        name = gintent.getStringExtra("nickname");
        email = gintent.getStringExtra("email");
        profile = gintent.getStringExtra("profile");
        password = gintent.getStringExtra("password");
    }

    //'회원가입' 버튼 클릭 시
    public void fnJoin() {
        if(genderChecker().length() == 0) {
            makeToast("성별을 선택해주세요");
        } else if(licenseChecker().length() == 0) {
            makeToast("사업자등록 여부를 선택해주세요");
        } else if(cbChecker().equals("역할: \n")) {
            makeToast("역할을 선택해주세요");
        } else if(binding.etciBusiness.length() == 0) {
            makeToast("업종을 기입해주세요");
        } else {
            strResult += genderChecker();
            strResult += "나이: "+binding.spciYear.getSelectedItem().toString()+"\n";
            strResult += licenseChecker();
            strResult += cbChecker();
            strResult += "보유자본: "+binding.spciMoney.getSelectedItem().toString()+"\n";
            strResult += "투자희망금액: "+binding.spciInvest.getSelectedItem().toString()+"\n";
            strResult += "업종: "+binding.etciBusiness.getText().toString();

            if(loginmethod.equals("email")) {
                emailAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(JoinCeoActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful()) {
                                    final FirebaseUser user = emailAuth.getCurrentUser();

                                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                            .setDisplayName(name)
                                            .setPhotoUri(Uri.parse(profile))
                                            .build();

                                    user.updateProfile(profileUpdates)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if(task.isSuccessful()) {
                                                        String userInfo = "유저이름: "+user.getDisplayName()+"\n유저 이메일: "+user.getEmail()+"\n";
                                                        strResult = userInfo+strResult;

                                                        Intent intent = new Intent(JoinCeoActivity.this, MainActivity.class);
                                                        intent.putExtra("result", strResult);
                                                        intent.putExtra("loginmethod", loginmethod);
                                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                                                        startActivity(intent);
                                                        finish();
                                                    } else {
                                                        makeToastLong("회원가입에 실패했습니다. 다시 시도해주세요. 오류: "+task.getException().getMessage());
                                                    }
                                                }
                                            });
                                } else {
                                    makeToastLong("회원가입에 실패했습니다. 다시 시도해주세요: "+task.getException().getMessage());
                                }
                            }
                        });
            } else if(loginmethod.equals("google")) {
                final FirebaseUser user = emailAuth.getCurrentUser();

                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                        .setDisplayName(name)
                        .setPhotoUri(Uri.parse(profile))
                        .build();

                user.updateProfile(profileUpdates)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    String userInfo = "유저이름: "+user.getDisplayName()+"\n유저 이메일: "+user.getEmail()+"\n유저 프로필: "+user.getPhotoUrl().toString()+"\n";
                                    strResult = userInfo+strResult;

                                    Intent intent = new Intent(JoinCeoActivity.this, MainActivity.class);
                                    intent.putExtra("result", strResult);
                                    intent.putExtra("loginmethod", loginmethod);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    makeToastLong("회원가입에 실패했습니다. 다시 시도해주세요: "+task.getException().getMessage());
                                }
                            }
                        });
            } else {
                String userInfo = "유저이름: "+name+"\n유저 이메일: "+email+"\n유저 프로필"+profile+"\n";
                strResult = userInfo+strResult;

                Intent intent = new Intent(JoinCeoActivity.this, MainActivity.class);
                intent.putExtra("result", strResult);
                intent.putExtra("loginmethod", loginmethod);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        }
    }

    //성별 체크 후 결과값 돌려줌
    private String genderChecker() {
        String result = "";

        if(binding.rbciMan.isChecked()) {
            result = "성별: 남성\n";
        } else if(binding.rbciWoman.isChecked()) {
            result = "성별: 여성\n";
        }

        return result;
    }

    //사업자 가입 여부 체크 후 결과값 돌려줌
    private String licenseChecker() {
        String result = "";

        if(binding.rbciLicensed.isChecked()) {
            result = "사업자등록여부: 가입함\n";
        } else if(binding.rbciwillLicense.isChecked()) {
            result = "사업자등록여부: 등록 예정\n";
        } else if(binding.rbcinotLicense.isChecked()) {
            result = "사업자등록여부: 등록 안함\n";
        }

        return result;
    }

    //어떤 체크박스가 체크되었는지 확인 후 결과값 돌려줌
    private String cbChecker() {
        String result = "역할: ";

        if(binding.cbciCeo.isChecked()) {
            result += "Ceo,";
        } if(binding.cbciDesigner.isChecked()) {
            result += "디자이너,";
        } if(binding.cbciDeveloper.isChecked()) {
            result += "개발자,";
        } if(binding.cbciMarketer.isChecked()) {
            result += "마케터,";
        } if(binding.cbciPM.isChecked()) {
            result += "프로젝트 매니저,";
        } if(binding.cbciProvider.isChecked()) {
            result += "기획자,";
        } if(binding.cbciOther.isChecked()) {
            result += "기타 역할";
        }

        result += "\n";
        return result;
    }

    @Override
    protected int getLayoutId() { return R.layout.activity_joinceo; }
}*/
