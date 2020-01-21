package com.candykick.startup.view.loginAccount;

import android.content.Intent;
import android.os.Bundle;

import com.candykick.startup.R;
import com.candykick.startup.view.base.BaseActivity;
import com.candykick.startup.databinding.ActivityPiagreementBinding;

/*
5.29
얘도 너무 길어서 짤린다.
개인정보처리방침을 좀 더 깔끔하게 보여주는 방법...?
글씨 크기 바꾸고, 가운데 정렬 하고.
 */

public class PIAgreementActivity extends BaseActivity<ActivityPiagreementBinding> {

    String loginmethod, uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding.setActivity(this);

        binding.toolbar.btnToolBarLeft.setOnClickListener(v -> onBackPressed());
        binding.toolbar.tvToolBar.setText("개인정보처리방침");

        Intent gintent = getIntent();
        loginmethod = gintent.getStringExtra("loginmethod");
        if(loginmethod.equals("kakao")) {
            uid = gintent.getStringExtra("uid");
        } else {
            uid = "";
        }
    }

    public void btnPiAgreement() {
        /*Intent intent = new Intent();
        setResult(RESULT_OK, intent);
        finish();*/
        Intent intent = new Intent(PIAgreementActivity.this, JoinStartActivity.class);
        intent.putExtra("loginmethod",loginmethod);
        intent.putExtra("uid",uid);
        startActivity(intent);
        finish();
    }

    @Override
    protected int getLayoutId() { return R.layout.activity_piagreement; }
}