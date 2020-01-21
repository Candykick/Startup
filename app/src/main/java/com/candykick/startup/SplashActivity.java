package com.candykick.startup;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.candykick.startup.engine.CdkResponseCallback;
import com.candykick.startup.model.MyUserModel;
import com.candykick.startup.view.loginAccount.PermissionActivity;
import com.candykick.startup.view.loginAccount.StartActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.kakao.auth.Session;

public class SplashActivity extends AppCompatActivity {

    //View
    ImageView ivSplash;
    ProgressBar pbSplash;

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        ivSplash = findViewById(R.id.ivSplash);
        pbSplash = findViewById(R.id.pbSplash);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR); // 상태바 글씨 검정
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        fadein(ivSplash, pbSplash);

        //앱 설치 후 첫 실행인지를 체크한다. 첫 실행이면 퍼미션 안내 Activity로 넘어간다.
        SharedPreferences pref = getSharedPreferences("firstexe", MODE_PRIVATE);
        if (pref.getBoolean("firstexe", true)) {
            SharedPreferences.Editor editor = pref.edit();
            editor.putBoolean("firstexe",false);
            editor.commit();

            new Handler().postDelayed(() -> {
                Intent intent = new Intent(SplashActivity.this, PermissionActivity.class);
                startActivity(intent);
                SplashActivity.this.finish();
            }, 500);
        } else {
            MyUserModel.getInstance().checkSessionisOpen(new CdkResponseCallback<Boolean>() {
                @Override
                public void onDataLoaded(String result, Boolean item) {
                    if(item == Boolean.TRUE) { //세션 열림
                        MyUserModel.getInstance().setUserData(new CdkResponseCallback<String>() {
                            @Override
                            public void onDataLoaded(String result, String item) {
                                new Handler().postDelayed(() -> {
                                    Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                                    /*intent.putExtra("result", "유저 이름:" + user.getDisplayName() + "\n유저 이메일" + user.getEmail() + "\n유저 프로필 사진" + user.getPhotoUrl());
                                    intent.putExtra("loginmethod", "email");*/
                                    startActivity(intent);
                                    SplashActivity.this.finish();
                                }, 500);
                            }

                            @Override
                            public void onDataNotAvailable(String error) {
                                Toast.makeText(getApplicationContext(), getResources().getString(R.string.toastLoginErr)+error, Toast.LENGTH_LONG).show();
                                finish();
                            }

                            @Override
                            public void onDataEmpty() {
                                new Handler().postDelayed(() -> {
                                    Intent intent = new Intent(SplashActivity.this, StartActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                    startActivity(intent);
                                    SplashActivity.this.finish();
                                }, 500);
                            }
                        });
                    } else { //세션 닫힘
                        new Handler().postDelayed(() -> {
                            Intent intent = new Intent(SplashActivity.this, StartActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                            startActivity(intent);
                            SplashActivity.this.finish();
                        }, 500);
                    }
                }

                @Override
                public void onDataNotAvailable(String error) {
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.toastLoginErr)+error, Toast.LENGTH_LONG).show();
                    finish();
                }

                @Override
                public void onDataEmpty() {
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.toastLoginErr), Toast.LENGTH_LONG).show();
                    finish();
                }
            });
        }
    }

    //Fadein 애니메이션
    private void fadein(final View tv, final View tv2) {
        final Animation animationFade;
        tv.setAlpha(0f);
        tv2.setAlpha(0f);
        animationFade = AnimationUtils.loadAnimation(this,R.anim.fadein);
        Handler mhandler = new Handler();
        mhandler.postDelayed(() -> {
                tv.setAlpha(1f);
                tv2.setAlpha(1f);
                tv.startAnimation(animationFade);
                tv2.startAnimation(animationFade);
        }, 0);
    }
}
