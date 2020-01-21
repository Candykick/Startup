package com.candykick.startup.view.userInfo;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;

import com.bumptech.glide.Glide;
import com.candykick.startup.R;
import com.candykick.startup.view.base.BaseActivity;
import com.candykick.startup.databinding.ActivityUserInfoNewBinding;
import com.candykick.startup.view.loginAccount.StartActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.kakao.auth.Session;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.LogoutResponseCallback;
import com.kakao.usermgmt.callback.MeV2ResponseCallback;
import com.kakao.usermgmt.callback.UnLinkResponseCallback;
import com.kakao.usermgmt.response.MeV2Response;

import java.util.ArrayList;

public class UserInfoActivityNew extends BaseActivity<ActivityUserInfoNewBinding> implements AppBarLayout.OnOffsetChangedListener {

    private static final float PERCENTAGE_TO_SHOW_TITLE_AT_TOOLBAR  = 0.8f;
    private static final float PERCENTAGE_TO_HIDE_TITLE_DETAILS     = 0.3f;
    private static final int ALPHA_ANIMATIONS_DURATION              = 200;
    private static final int REQUEST_CHANGE_USER_INFO              = 9794;

    private boolean mIsTheTitleVisible          = false;
    private boolean mIsTheTitleContainerVisible = true;

    String userType;

    String profile, userInfo1 = "", userInfo2 = "", userInfo3 = "";
    ArrayList<String> userJobTags = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding.setActivity(this);

        binding.appbarUserInfo.addOnOffsetChangedListener(this);
        setSupportActionBar(binding.toolbarUserInfo);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        startAlphaAnimation(binding.llRealToolbar, 0, View.INVISIBLE);

        binding.ivUserInfo2.setBackground(new ShapeDrawable(new OvalShape()));
        binding.ivUserInfo2.setClipToOutline(true);

        Intent intent = getIntent();

        if(getIntent().getBooleanExtra("ismydata",false)) { //내 데이터인 경우
            binding.btnUserInfoModify.setVisibility(View.VISIBLE);
            binding.llUserinfo.setVisibility(View.VISIBLE);
            binding.btnUIChat.setVisibility(View.GONE);
        } else {
            binding.btnUserInfoModify.setVisibility(View.GONE);
            binding.llUserinfo.setVisibility(View.GONE);
            binding.btnUIChat.setVisibility(View.VISIBLE);
        }

        binding.tvUserNameUI1.setText(intent.getStringExtra("name"));
        binding.tvUserNameUI2.setText(intent.getStringExtra("name"));
        binding.tvEmailUI.setText(intent.getStringExtra("email"));
        profile = intent.getStringExtra("profileimage");
        Glide.with(UserInfoActivityNew.this).load(profile).into(binding.ivUserInfo2);

        userType = intent.getStringExtra("userType");

        if(userType.equals("유형: 창업희망자")) {
            binding.tvTypeUserInfo.setText("유형: 창업희망자");
            binding.tvTitle3UserInfo.setVisibility(View.VISIBLE);
            binding.tvUserInfo3.setVisibility(View.VISIBLE);
            binding.tvTitle4UserInfo.setVisibility(View.VISIBLE);
            binding.tgUserInfo.setVisibility(View.VISIBLE);

            binding.tvTitle1UserInfo.setText("● 성별");
            userInfo1 = intent.getStringExtra("gender");
            binding.tvUserInfo1.setText(userInfo1);

            binding.tvTitle2UserInfo.setText("● 나잇대");
            userInfo2 = intent.getStringExtra("age");
            binding.tvUserInfo2.setText(userInfo2);

            binding.tvTitle3UserInfo.setText("● 역할");
            userInfo3 = intent.getStringExtra("role");
            String[] roles = userInfo3.split(", ");
            String rolesResult = "";
            for(int i=0;i<roles.length;i++) {
                rolesResult += "- ";
                rolesResult += roles[i];
                rolesResult += "\n";
            }
            rolesResult.substring(0,rolesResult.length()-2);
            binding.tvUserInfo3.setText(rolesResult);

            binding.tvTitle4UserInfo.setText("● 업종");
            userJobTags = intent.getStringArrayListExtra("jobTags");
            binding.tgUserInfo.setTags(userJobTags);
        } else if(userType.equals("유 형 :  투 자 자")) {
            binding.tvTypeUserInfo.setText("유 형 :  투 자 자");
            binding.tvTitle3UserInfo.setVisibility(View.GONE);
            binding.tvUserInfo3.setVisibility(View.GONE);
            binding.tvTitle4UserInfo.setVisibility(View.VISIBLE);
            binding.tgUserInfo.setVisibility(View.VISIBLE);

            binding.tvTitle1UserInfo.setText("● 투자희망금액" );
            userInfo1 = intent.getStringExtra("wantinvcapital");
            binding.tvUserInfo1.setText(userInfo1);

            binding.tvTitle2UserInfo.setText("● 투자희망유형" );
            userInfo2 = intent.getStringExtra("wantinvtype");
            binding.tvUserInfo2.setText(userInfo2);

            binding.tvTitle4UserInfo.setText("● 투자희망업종" );
            userJobTags = intent.getStringArrayListExtra("jobTags");
            binding.tgUserInfo.setTags(userJobTags);
        } else if(userType.equals("유형: 외주사업가")) {
            binding.tvTypeUserInfo.setText("유형: 외주사업가");
            binding.tvTitle3UserInfo.setVisibility(View.GONE);
            binding.tvUserInfo3.setVisibility(View.GONE);
            binding.tvTitle4UserInfo.setVisibility(View.GONE);
            binding.tgUserInfo.setVisibility(View.GONE);

            binding.tvTitle1UserInfo.setText("● 세금계산서 발행 여부" );
            userInfo1 = intent.getStringExtra("taxinvoicepossible");
            binding.tvUserInfo1.setText(userInfo1);
            binding.tvTitle2UserInfo.setText("● 한 줄 소개");
            userInfo2 = intent.getStringExtra("userDes");
            binding.tvUserInfo2.setText(userInfo2);
        }

        progressOff();
    }

    //로그아웃 버튼 클릭 시
    public void logoutUser() {
        if (Session.getCurrentSession().isOpened()) {
            UserManagement.getInstance().requestLogout(new LogoutResponseCallback() {
                @Override
                public void onCompleteLogout() {}
            });
            SharedPreferences sf = getSharedPreferences("userType",MODE_PRIVATE);
            SharedPreferences.Editor editor = sf.edit();
            editor.putBoolean("isTalkActOpened",false);
            editor.apply();

            makeToast("로그아웃했습니다. 이용해 주셔서 감사합니다.");
            Intent intent = new Intent(getApplicationContext(), StartActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
            ActivityCompat.finishAffinity(UserInfoActivityNew.this);
            startActivity(intent);
        } else if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            FirebaseAuth.getInstance().signOut();

            SharedPreferences sf = getSharedPreferences("userType",MODE_PRIVATE);
            SharedPreferences.Editor editor = sf.edit();
            editor.putBoolean("isTalkActOpened",false);
            editor.apply();

            makeToast("로그아웃했습니다. 이용해 주셔서 감사합니다.");
            Intent intent = new Intent(getApplicationContext(), StartActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
            ActivityCompat.finishAffinity(this);
            startActivity(intent);
        }
    }
    //회원탈퇴 버튼 클릭 시
    public void signoutUser() {
        //DB 자료 지워지는지 테스트 필요.
        if (!Session.getCurrentSession().isClosed()) {
            UserManagement.getInstance().me(new MeV2ResponseCallback() {
                @Override
                public void onSessionClosed(ErrorResult errorResult) {
                    progressOff();
                    makeToast("정보를 불러오지 못했습니다. 다시 시도해 주세요: " + errorResult.toString());
                    finish();
                }

                @Override
                public void onSuccess(MeV2Response result) {
                    String userId = ""+result.getId();

                    UserManagement.getInstance().requestUnlink(new UnLinkResponseCallback() {
                        @Override
                        public void onSessionClosed(ErrorResult errorResult) {
                            makeToast("탈퇴 처리 도중 오류가 발생했습니다.");
                            Intent intent = new Intent(getApplicationContext(), StartActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                            ActivityCompat.finishAffinity(UserInfoActivityNew.this);
                            startActivity(intent);
                        }

                        @Override
                        public void onNotSignedUp() {
                            makeToast("탈퇴 처리 도중 오류가 발생했습니다.");
                            Intent intent = new Intent(getApplicationContext(), StartActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                            ActivityCompat.finishAffinity(UserInfoActivityNew.this);
                            startActivity(intent);
                        }

                        @Override
                        public void onSuccess(Long result) {
                            FirebaseFirestore db = FirebaseFirestore.getInstance();
                            //Task task =
                            db.collection("UserCeo").document(userId).delete();
                            makeToast("탈퇴처리했습니다. 이용해 주셔서 감사합니다.");
                            Intent intent = new Intent(getApplicationContext(), StartActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                            ActivityCompat.finishAffinity(UserInfoActivityNew.this);
                            startActivity(intent);
                        }
                    });
                }
            });
        } else if(FirebaseAuth.getInstance().getCurrentUser() != null) {
            makeToast("탈퇴처리했습니다. 이용해 주셔서 감사합니다.");
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            //Task task =
                    db.collection("UserCeo").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).delete();
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(getApplicationContext(), StartActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            ActivityCompat.finishAffinity(UserInfoActivityNew.this);
            startActivity(intent);
        }
    }
    public void modifyUserInfo() {
        Intent intent = new Intent(UserInfoActivityNew.this, UserInfoModifyActivity.class);
        intent.putExtra("usertype",userType);
        intent.putExtra("profile",profile);
        intent.putExtra("userInfo1",userInfo1);
        intent.putExtra("userInfo2",userInfo2);
        intent.putExtra("userInfo3",userInfo3);
        intent.putExtra("jobTags",userJobTags);
        startActivityForResult(intent,REQUEST_CHANGE_USER_INFO);
    }

    public void fnChat() {}

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == 2) {
            makeToast("유저 정보를 업로드하는 도중 오류가 발생했습니다.");
            return;
        } else if(resultCode == RESULT_OK) {
            progressOn();

            if(Session.getCurrentSession().isOpened()) {
                UserManagement.getInstance().me(new MeV2ResponseCallback() {
                    @Override
                    public void onSessionClosed(ErrorResult errorResult) {
                        progressOff();
                        makeToastLong("정보를 불러오지 못했습니다. 다시 시도해 주세요: " + errorResult.toString());
                        finish();
                    }

                    @Override
                    public void onSuccess(MeV2Response result) {
                        FirebaseFirestore db = FirebaseFirestore.getInstance();
                        DocumentReference docRef = db.collection("UserCeo").document(""+result.getId());
                        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    DocumentSnapshot document = task.getResult();

                                    if (document.exists()) {
                                        Glide.with(UserInfoActivityNew.this).load(document.get("profileimage").toString()).into(binding.ivUserInfo2);

                                        if((long)document.get("usertype") == 0) {
                                            binding.tvTypeUserInfo.setText("유형: 창업희망자");
                                            binding.tvTitle1UserInfo.setText("● 성별");
                                            if((long)document.get("gender") == 1)
                                                userInfo1 = "남성";
                                            else if((long)document.get("gender") == 2)
                                                userInfo1 = "여성";
                                            binding.tvUserInfo1.setText(userInfo1);

                                            binding.tvTitle2UserInfo.setText("● 나잇대");
                                            userInfo2 = (String)document.get("age");
                                            binding.tvUserInfo2.setText(userInfo2);

                                            binding.tvTitle3UserInfo.setText("● 역할");
                                            userInfo3 = (String)document.get("role");
                                            String[] roles = userInfo3.split(", ");
                                            String rolesResult = "";
                                            for(int i=0;i<roles.length;i++) {
                                                rolesResult += "- ";
                                                rolesResult += roles[i];
                                                rolesResult += "\n";
                                            }
                                            rolesResult.substring(0,rolesResult.length()-2);
                                            binding.tvUserInfo3.setText(rolesResult);

                                            binding.tvTitle4UserInfo.setText("● 업종");
                                            String[] jobTagsString = document.get("businesstype").toString().split(", ");
                                            userJobTags = new ArrayList<>();
                                            for(String jobTag: jobTagsString) {
                                                userJobTags.add(jobTag);
                                            }
                                            binding.tgUserInfo.setTags(userJobTags);
                                        } else if((long)document.get("usertype") == 1) {
                                            binding.tvTypeUserInfo.setText("유 형 :  투 자 자");

                                            binding.tvTitle1UserInfo.setText("● 투자희망금액" );
                                            userInfo1 = (String)document.get("wantinvcapital");
                                            binding.tvUserInfo1.setText(userInfo1);

                                            binding.tvTitle2UserInfo.setText("● 투자희망유형" );
                                            userInfo2 = (String)document.get("wantinvtype");
                                            binding.tvUserInfo2.setText(userInfo2);

                                            binding.tvTitle4UserInfo.setText("● 투자희망업종" );
                                            String[] jobTagsString = document.get("wantinvjob").toString().split(", ");
                                            userJobTags = new ArrayList<>();
                                            for(String jobTag: jobTagsString) {
                                                userJobTags.add(jobTag);
                                            }
                                            binding.tgUserInfo.setTags(userJobTags);
                                        } else if((long)document.get("usertype") == 3) {
                                            binding.tvTypeUserInfo.setText("유형: 외주사업가");

                                            binding.tvTitle1UserInfo.setText("● 세금계산서 발행 여부" );
                                            userInfo1 = (boolean)document.get("taxinvoicepossible") ? "발행 가능" : "발행 불가";
                                            binding.tvUserInfo1.setText(userInfo1);
                                            binding.tvTitle2UserInfo.setText("● 한 줄 소개");
                                            userInfo2 = (String)document.get("userDes");
                                            binding.tvUserInfo2.setText(userInfo2);
                                        }
                                        progressOff();
                                    } else {
                                        progressOff();
                                        makeToastLong("유저 정보를 불러오지 못했습니다. 해당하는 유저의 정보가 없습니다.");
                                        finish();
                                    }
                                } else {
                                    progressOff();
                                    makeToastLong("유저 정보를 불러오는 도중 오류가 발생했습니다: "+task.getException());
                                    finish();
                                }
                            }
                        });
                    }
                });
            } else if(FirebaseAuth.getInstance().getCurrentUser() != null) {
                Log.d("Startup","Google");
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                FirebaseFirestore db = FirebaseFirestore.getInstance();
                DocumentReference docRef = db.collection("UserCeo").document(user.getUid());
                docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();

                            Glide.with(UserInfoActivityNew.this).load(document.get("profileimage").toString()).into(binding.ivUserInfo2);


                            if (document.exists()) {
                                if((long)document.get("usertype") == 0) {
                                    binding.tvTypeUserInfo.setText("유형: 창업희망자");
                                    binding.tvTitle1UserInfo.setText("● 성별");
                                    if((long)document.get("gender") == 1)
                                        userInfo1 = "남성";
                                    else if((long)document.get("gender") == 2)
                                        userInfo1 = "여성";
                                    binding.tvUserInfo1.setText(userInfo1);

                                    binding.tvTitle2UserInfo.setText("● 나잇대");
                                    userInfo2 = (String)document.get("age");
                                    binding.tvUserInfo2.setText(userInfo2);

                                    binding.tvTitle3UserInfo.setText("● 역할");
                                    userInfo3 = (String)document.get("role");
                                    String[] roles = userInfo3.split(", ");
                                    String rolesResult = "";
                                    for(int i=0;i<roles.length;i++) {
                                        rolesResult += "- ";
                                        rolesResult += roles[i];
                                        rolesResult += "\n";
                                    }
                                    rolesResult.substring(0,rolesResult.length()-2);
                                    binding.tvUserInfo3.setText(rolesResult);

                                    binding.tvTitle4UserInfo.setText("● 업종");
                                    String[] jobTagsString = document.get("businesstype").toString().split(", ");
                                    userJobTags = new ArrayList<>();
                                    for(String jobTag: jobTagsString) {
                                        userJobTags.add(jobTag);
                                    }
                                    binding.tgUserInfo.setTags(userJobTags);
                                } else if((long)document.get("usertype") == 1) {
                                    binding.tvTypeUserInfo.setText("유 형 :  투 자 자");

                                    binding.tvTitle1UserInfo.setText("● 투자희망금액" );
                                    userInfo1 = (String)document.get("wantinvcapital");
                                    binding.tvUserInfo1.setText(userInfo1);

                                    binding.tvTitle2UserInfo.setText("● 투자희망유형" );
                                    userInfo2 = (String)document.get("wantinvtype");
                                    binding.tvUserInfo2.setText(userInfo2);

                                    binding.tvTitle4UserInfo.setText("● 투자희망업종" );
                                    String[] jobTagsString = document.get("wantinvjob").toString().split(", ");
                                    userJobTags = new ArrayList<>();
                                    for(String jobTag: jobTagsString) {
                                        userJobTags.add(jobTag);
                                    }
                                    binding.tgUserInfo.setTags(userJobTags);
                                } else if((long)document.get("usertype") == 3) {
                                    binding.tvTypeUserInfo.setText("유형: 외주사업가");

                                    binding.tvTitle1UserInfo.setText("● 세금계산서 발행 여부" );
                                    userInfo1 = (boolean)document.get("taxinvoicepossible") ? "발행 가능" : "발행 불가";
                                    binding.tvUserInfo1.setText(userInfo1);
                                    binding.tvTitle2UserInfo.setText("● 한 줄 소개");
                                    userInfo2 = (String)document.get("userDes");
                                    binding.tvUserInfo2.setText(userInfo2);
                                }
                                progressOff();
                            } else {
                                progressOff();
                                makeToastLong("유저 정보를 불러오지 못했습니다. 해당하는 유저의 정보가 없습니다.");
                                finish();
                            }
                        } else {
                            progressOff();
                            makeToastLong("유저 정보를 불러오는 도중 오류가 발생했습니다: "+task.getException());
                            finish();
                        }
                    }
                });
            }

            return;
        }
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int offset) {
        int maxScroll = appBarLayout.getTotalScrollRange();
        float percentage = (float) Math.abs(offset) / (float) maxScroll;

        handleAlphaOnTitle(percentage);
        handleToolbarTitleVisibility(percentage);
    }

    private void handleToolbarTitleVisibility(float percentage) {
        if(percentage >= PERCENTAGE_TO_SHOW_TITLE_AT_TOOLBAR) {
            if(!mIsTheTitleVisible) {
                startAlphaAnimation(binding.llRealToolbar, ALPHA_ANIMATIONS_DURATION, View.VISIBLE);
                mIsTheTitleVisible = true;
            }
        } else {
            if(mIsTheTitleVisible) {
                startAlphaAnimation(binding.llRealToolbar, ALPHA_ANIMATIONS_DURATION, View.INVISIBLE);
                mIsTheTitleVisible = false;
            }
        }
    }

    private void handleAlphaOnTitle(float percentage) {
        if(percentage >= PERCENTAGE_TO_HIDE_TITLE_DETAILS) {
            if(mIsTheTitleContainerVisible) {
                startAlphaAnimation(binding.llToolbarUserInfo, ALPHA_ANIMATIONS_DURATION, View.INVISIBLE);
                mIsTheTitleContainerVisible = false;
            }
        } else {
            if(!mIsTheTitleContainerVisible) {
                startAlphaAnimation(binding.llToolbarUserInfo, ALPHA_ANIMATIONS_DURATION, View.VISIBLE);
                mIsTheTitleContainerVisible = true;
            }
        }
    }

    public static void startAlphaAnimation(View v, long duration, int visibility) {
        AlphaAnimation alphaAnimation = (visibility == View.VISIBLE)
                ? new AlphaAnimation(0f, 1f)
                : new AlphaAnimation(1f, 0f);
        alphaAnimation.setDuration(duration);
        alphaAnimation.setFillAfter(true);
        v.startAnimation(alphaAnimation);
    }

    @Override
    protected int getLayoutId() { return R.layout.activity_user_info_new;  }
}
