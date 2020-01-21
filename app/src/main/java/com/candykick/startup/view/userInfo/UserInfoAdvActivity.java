package com.candykick.startup.view.userInfo;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;

import com.bumptech.glide.Glide;
import com.candykick.startup.R;
import com.candykick.startup.view.adviserAct.AdviserCareerAdapter;
import com.candykick.startup.view.base.BaseActivity;
import com.candykick.startup.databinding.ActivityUserInfoAdvBinding;
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
import java.util.HashMap;
import java.util.List;

public class UserInfoAdvActivity extends BaseActivity<ActivityUserInfoAdvBinding> implements AppBarLayout.OnOffsetChangedListener {

    private static final float PERCENTAGE_TO_SHOW_TITLE_AT_TOOLBAR  = 0.8f;
    private static final float PERCENTAGE_TO_HIDE_TITLE_DETAILS     = 0.3f;
    private static final int ALPHA_ANIMATIONS_DURATION              = 200;

    private static final int REQUEST_CHANGE_USER_INFO              = 9794;

    private boolean mIsTheTitleVisible          = false;
    private boolean mIsTheTitleContainerVisible = true;

    private AdviserCareerAdapter advAdapter; private LinearLayoutManager linearLayoutManager;

    String profileimage, namecard, userjob, userintro;
    ArrayList<HashMap<String, String>> career;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding.setActivity(this);

        binding.appbarUserInfoAdv.addOnOffsetChangedListener(this);
        setSupportActionBar(binding.toolbarUserInfoAdv);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        startAlphaAnimation(binding.llRealToolbarAdv, 0, View.INVISIBLE);

        binding.ivUserInfoAdv2.setBackground(new ShapeDrawable(new OvalShape()));
        binding.ivUserInfoAdv2.setClipToOutline(true);

        Intent intent = getIntent();

        if(getIntent().getBooleanExtra("ismydata",false)) { //내 데이터인 경우
            binding.btnUserInfoAdvModify.setVisibility(View.VISIBLE);
            binding.llUserInfoAdv.setVisibility(View.VISIBLE);
            //binding.btnUIChatAdv.setVisibility(View.GONE);
        } else {
            binding.btnUserInfoAdvModify.setVisibility(View.GONE);
            binding.llUserInfoAdv.setVisibility(View.GONE);
            //binding.btnUIChatAdv.setVisibility(View.VISIBLE);
        }

        binding.tvUserNameAdv1.setText(intent.getStringExtra("name"));
        binding.tvUserNameAdv2.setText(intent.getStringExtra("name"));
        binding.tvEmailAdv.setText(intent.getStringExtra("email"));
        profileimage = intent.getStringExtra("profileimage");
        Glide.with(UserInfoAdvActivity.this).load(profileimage).into(binding.ivUserInfoAdv2);

        namecard = intent.getStringExtra("namecard");
        Glide.with(UserInfoAdvActivity.this).load(namecard).into(binding.ivUserInfo3Adv);
        userjob = intent.getStringExtra("userjob");
        binding.tvUserInfo1Adv.setText(userjob);
        userintro = intent.getStringExtra("userintro");
        binding.tvUserInfo2Adv.setText(userintro);

        career = (ArrayList<HashMap<String, String>>)intent.getSerializableExtra("career");
        linearLayoutManager = new LinearLayoutManager(UserInfoAdvActivity.this);
        binding.rvUserInfo4Adv.setLayoutManager(linearLayoutManager);
        advAdapter = new AdviserCareerAdapter(UserInfoAdvActivity.this, career);
        binding.rvUserInfo4Adv.setAdapter(advAdapter);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(UserInfoAdvActivity.this, linearLayoutManager.getOrientation());
        binding.rvUserInfo4Adv.addItemDecoration(dividerItemDecoration);

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
            ActivityCompat.finishAffinity(UserInfoAdvActivity.this);
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
                            ActivityCompat.finishAffinity(UserInfoAdvActivity.this);
                            startActivity(intent);
                        }

                        @Override
                        public void onNotSignedUp() {
                            makeToast("탈퇴 처리 도중 오류가 발생했습니다.");
                            Intent intent = new Intent(getApplicationContext(), StartActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                            ActivityCompat.finishAffinity(UserInfoAdvActivity.this);
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
                            ActivityCompat.finishAffinity(UserInfoAdvActivity.this);
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
            ActivityCompat.finishAffinity(UserInfoAdvActivity.this);
            startActivity(intent);
        }
    }
    public void modifyUserInfo() {
        Intent intent = new Intent(UserInfoAdvActivity.this, UserInfoAdvModifyActivity.class);
        intent.putExtra("profile",profileimage);
        intent.putExtra("userjob",userjob);
        intent.putExtra("userintro",userintro);
        intent.putExtra("namecard",namecard);
        intent.putExtra("career",career);
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
                                        profileimage = document.get("profileimage").toString();
                                        Glide.with(UserInfoAdvActivity.this).load(profileimage).into(binding.ivUserInfoAdv2);

                                        namecard = (String)document.get("namecard");
                                        userjob = (String)document.get("userjob");
                                        userintro = (String)document.get("userintro");
                                        career.clear();
                                        career.addAll((List<HashMap<String, String>>)document.get("career"));

                                        binding.tvUserNameAdv1.setText((String)document.get("username"));
                                        binding.tvUserNameAdv2.setText((String)document.get("username"));
                                        binding.tvUserInfo1Adv.setText(userjob);
                                        binding.tvUserInfo2Adv.setText(userintro);
                                        Glide.with(UserInfoAdvActivity.this).load(namecard).into(binding.ivUserInfo3Adv);
                                        advAdapter = new AdviserCareerAdapter(UserInfoAdvActivity.this, career);
                                        binding.rvUserInfo4Adv.setAdapter(advAdapter);
                                        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(UserInfoAdvActivity.this, linearLayoutManager.getOrientation());
                                        binding.rvUserInfo4Adv.addItemDecoration(dividerItemDecoration);


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

                            if(document.exists()) {
                                profileimage = document.get("profileimage").toString();
                                Glide.with(UserInfoAdvActivity.this).load(profileimage).into(binding.ivUserInfoAdv2);

                                namecard = (String) document.get("namecard");
                                userjob = (String) document.get("userjob");
                                userintro = (String) document.get("userintro");
                                career.clear();
                                career.addAll((List<HashMap<String, String>>) document.get("career"));

                                binding.tvUserNameAdv1.setText((String) document.get("username"));
                                binding.tvUserNameAdv2.setText((String) document.get("username"));
                                binding.tvUserInfo1Adv.setText(userjob);
                                binding.tvUserInfo2Adv.setText(userintro);
                                Glide.with(UserInfoAdvActivity.this).load(namecard).into(binding.ivUserInfo3Adv);
                                advAdapter = new AdviserCareerAdapter(UserInfoAdvActivity.this, career);
                                binding.rvUserInfo4Adv.setAdapter(advAdapter);
                                DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(UserInfoAdvActivity.this, linearLayoutManager.getOrientation());
                                binding.rvUserInfo4Adv.addItemDecoration(dividerItemDecoration);


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
                startAlphaAnimation(binding.llRealToolbarAdv, ALPHA_ANIMATIONS_DURATION, View.VISIBLE);
                mIsTheTitleVisible = true;
            }
        } else {
            if(mIsTheTitleVisible) {
                startAlphaAnimation(binding.llRealToolbarAdv, ALPHA_ANIMATIONS_DURATION, View.INVISIBLE);
                mIsTheTitleVisible = false;
            }
        }
    }

    private void handleAlphaOnTitle(float percentage) {
        if(percentage >= PERCENTAGE_TO_HIDE_TITLE_DETAILS) {
            if(mIsTheTitleContainerVisible) {
                startAlphaAnimation(binding.llToolbarUserInfoAdv, ALPHA_ANIMATIONS_DURATION, View.INVISIBLE);
                mIsTheTitleContainerVisible = false;
            }
        } else {
            if(!mIsTheTitleContainerVisible) {
                startAlphaAnimation(binding.llToolbarUserInfoAdv, ALPHA_ANIMATIONS_DURATION, View.VISIBLE);
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
    protected int getLayoutId() { return R.layout.activity_user_info_adv; }
}
