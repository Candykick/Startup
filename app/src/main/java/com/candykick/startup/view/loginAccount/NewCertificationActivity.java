package com.candykick.startup.view.loginAccount;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.candykick.startup.R;
import com.facebook.accountkit.AccessToken;
import com.facebook.accountkit.AccountKit;
import com.facebook.accountkit.AccountKitLoginResult;
import com.facebook.accountkit.ui.AccountKitActivity;
import com.facebook.accountkit.ui.AccountKitConfiguration;
import com.facebook.accountkit.ui.LoginType;

public class NewCertificationActivity extends AppCompatActivity {

    public static int APP_REQUEST_CODE = 9281; //페이스북 SMS 인증용 상수
    String loginmethod, uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_certification);

        Intent gintent = getIntent();
        loginmethod = gintent.getStringExtra("loginmethod");
        if(loginmethod.equals("kakao")) {
            uid = gintent.getStringExtra("uid");
        } else {
            uid = "";
        }

        //유효한 기존 토큰이 있는지 확인
        AccessToken accessToken = AccountKit.getCurrentAccessToken();
        if (accessToken != null) {
            //Handle Returning User
        } else {
            final Intent intent = new Intent(NewCertificationActivity.this, AccountKitActivity.class);
            AccountKitConfiguration.AccountKitConfigurationBuilder configurationBuilder =
                    new AccountKitConfiguration.AccountKitConfigurationBuilder(
                            LoginType.PHONE,
                            AccountKitActivity.ResponseType.CODE); // or .ResponseType.TOKEN
            // ... perform additional configuration ...
            intent.putExtra(
                    AccountKitActivity.ACCOUNT_KIT_ACTIVITY_CONFIGURATION,
                    configurationBuilder.build());
            startActivityForResult(intent, APP_REQUEST_CODE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == APP_REQUEST_CODE) { // confirm that this response matches your request
            super.onActivityResult(requestCode, resultCode, data);

            AccountKitLoginResult loginResult = data.getParcelableExtra(AccountKitLoginResult.RESULT_KEY);
            if (loginResult.getError() != null) {
                Toast.makeText(NewCertificationActivity.this, loginResult.getError().getUserFacingMessage(), Toast.LENGTH_LONG).show();
                finish();
            } else if (loginResult.wasCancelled()) {
                Toast.makeText(NewCertificationActivity.this, "로그인이 취소되었습니다.", Toast.LENGTH_LONG).show();
                finish();
            } else {
                // If you have an authorization code, retrieve it from
                // loginResult.getAuthorizationCode()
                // and pass it to your server and exchange it for an access token.

                // Success! Start your next activity...
                Intent intent = new Intent(NewCertificationActivity.this, ClauseActivity.class);
                intent.putExtra("loginmethod",loginmethod);
                intent.putExtra("uid",uid);
                startActivity(intent);
                finish();
            }
        }
    }
}