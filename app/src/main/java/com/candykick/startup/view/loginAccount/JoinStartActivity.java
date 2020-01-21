package com.candykick.startup.view.loginAccount;

import android.content.Intent;
import android.os.Bundle;

import com.candykick.startup.R;
import com.candykick.startup.view.base.BaseActivity;
import com.candykick.startup.databinding.ActivityJoinStartBinding;

public class JoinStartActivity extends BaseActivity<ActivityJoinStartBinding> {

    String loginmethod, uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding.setActivity(this);

        Intent gintent = getIntent();
        loginmethod = gintent.getStringExtra("loginmethod");
        if(loginmethod.equals("kakao")) {
            uid = gintent.getStringExtra("uid");
        } else {
            uid = "";
        }
    }

    public void joinCeo() {
        Intent intent = new Intent(JoinStartActivity.this, Join1Activity.class);
        intent.putExtra("loginmethod",loginmethod);
        intent.putExtra("uid",uid);
        intent.putExtra("usertype",1);
        startActivity(intent);
    }
    public void joinInv() {
        Intent intent = new Intent(JoinStartActivity.this, Join1Activity.class);
        intent.putExtra("loginmethod",loginmethod);
        intent.putExtra("uid",uid);
        intent.putExtra("usertype",2);
        startActivity(intent);
    }
    public void joinOut() {
        Intent intent = new Intent(JoinStartActivity.this, Join1Activity.class);
        intent.putExtra("loginmethod",loginmethod);
        intent.putExtra("uid",uid);
        intent.putExtra("usertype",4);
        startActivity(intent);
    }
    public void joinAdv() {
        Intent intent = new Intent(JoinStartActivity.this, Join1Activity.class);
        intent.putExtra("loginmethod",loginmethod);
        intent.putExtra("uid",uid);
        intent.putExtra("usertype",3);
        startActivity(intent);
    }

    @Override
    protected int getLayoutId() { return R.layout.activity_join_start; }
}
