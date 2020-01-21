package com.candykick.startup.view.loginAccount;

import android.content.Intent;
import android.os.Bundle;

import com.candykick.startup.R;
import com.candykick.startup.view.base.BaseActivity;
import com.candykick.startup.databinding.ActivityCertificationBinding;
import com.facebook.accountkit.AccessToken;
import com.facebook.accountkit.AccountKit;
import com.facebook.accountkit.AccountKitLoginResult;
import com.facebook.accountkit.ui.AccountKitActivity;
import com.facebook.accountkit.ui.AccountKitConfiguration;
import com.facebook.accountkit.ui.LoginType;

public class CertificationActivity extends BaseActivity<ActivityCertificationBinding> {

    private static final int clauseIntent = 147, piagreementIntent = 247;
    public static int APP_REQUEST_CODE = 9281; //페이스북 SMS 인증용 상수

    String loginmethod, uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent gintent = getIntent();
        loginmethod = gintent.getStringExtra("loginmethod");
        if(loginmethod.equals("kakao")) {
            uid = gintent.getStringExtra("uid");
        } else {
            uid = "";
        }

        binding.setActivity(this);

        binding.toolbar.btnToolBarLeft.setOnClickListener(v -> onBackPressed());
        binding.toolbar.tvToolBar.setText("본인인증 및 동의");
    }

    //인증문자 보내기('인증받기' 버튼)
    public void fnCaPhone() {
        if(binding.etCaPhoneNumber.getText().toString().length() == 10 || binding.etCaPhoneNumber.getText().toString().length() == 11) {
            /*try {
                SmsManager smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage(binding.etCaPhoneNumber.getText().toString(),null,"Startup 회원가입: 인증번호는 [1742]입니다.",null,null);
                makeToast("입력하신 번호로 인증번호를 전송했습니다.");
            } catch (Exception e) {
                makeToastLong("인증번호 전송에 실패했습니다. 전화번호를 확인해보세요. 오류: "+e.getMessage());
            }*/

            makeToast("임시 인증번호: 1742.");
            binding.etCaNumber.setEnabled(true);
            binding.etCaNumber.setClickable(true);
            binding.btnCaNumber.setEnabled(true);
            binding.btnCaNumber.setClickable(true);
        } else {
            makeToast("올바른 휴대폰번호를 입력해주세요.");
        }
    }
    //인증번호 확인('인증확인' 버튼)
    public void fnCaNumber() {
        //유효한 기존 토큰이 있는지 확인
        AccessToken accessToken = AccountKit.getCurrentAccessToken();
        if (accessToken != null) {
            //Handle Returning User
        } else {
            final Intent intent = new Intent(CertificationActivity.this, AccountKitActivity.class);
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

        /*if(binding.etCaNumber.getText().toString().equals("1742")) {
            makeToast("인증번호가 확인되었습니다.");
            binding.btnCaCeo.setEnabled(true);
            binding.btnCaCeo.setClickable(true);
            binding.btnCaInv.setEnabled(true);
            binding.btnCaInv.setClickable(true);
            binding.btnCaAdv.setEnabled(true);
            binding.btnCaAdv.setClickable(true);
            binding.btnCaOutsourcing.setEnabled(true);
            binding.btnCaOutsourcing.setClickable(true);
        } else {
            makeToast("인증번호가 올바르지 않습니다. 다시 확인해주세요.");
        }*/
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == APP_REQUEST_CODE) { // confirm that this response matches your request
            super.onActivityResult(requestCode, resultCode, data);

            AccountKitLoginResult loginResult = data.getParcelableExtra(AccountKitLoginResult.RESULT_KEY);
            String toastMessage;
            if (loginResult.getError() != null) {
                toastMessage = loginResult.getError().getErrorType().getMessage();
                makeToastLong(loginResult.getError().getUserFacingMessage());
            } else if (loginResult.wasCancelled()) {
                toastMessage = "Login Cancelled";
            } else {
                if (loginResult.getAccessToken() != null) {
                    toastMessage = "Success:" + loginResult.getAccessToken().getAccountId();
                } else {
                    toastMessage = String.format(
                            "Success:%s...",
                            loginResult.getAuthorizationCode().substring(0,10));
                }

                // If you have an authorization code, retrieve it from
                // loginResult.getAuthorizationCode()
                // and pass it to your server and exchange it for an access token.

                // Success! Start your next activity...
                makeToast("인증번호가 확인되었습니다.");
                binding.btnCaCeo.setEnabled(true);
                binding.btnCaCeo.setClickable(true);
                binding.btnCaInv.setEnabled(true);
                binding.btnCaInv.setClickable(true);
                binding.btnCaAdv.setEnabled(true);
                binding.btnCaAdv.setClickable(true);
                binding.btnCaOutsourcing.setEnabled(true);
                binding.btnCaOutsourcing.setClickable(true);
            }

            // Surface the result to your user in an appropriate way.
            makeToastLong(toastMessage);
        } else if(requestCode == clauseIntent && resultCode == RESULT_OK) {
            binding.cbCaClause.setChecked(true);
        } else if(requestCode == piagreementIntent && resultCode == RESULT_OK) {
            binding.cbCaPiagreement.setChecked(true);
        }
    }

    //이용약관 페이지('이용약관에 동의합니다' 버튼)
    public void fnCaClause() {
        Intent intent = new Intent(CertificationActivity.this, ClauseActivity.class);
        startActivityForResult(intent, clauseIntent);
    }
    //개인정보 취급방침 페이지('개인정보 취급방침에 동의합니다' 버튼)
    public void fnCaPIAgreement() {
        Intent intent = new Intent(CertificationActivity.this, PIAgreementActivity.class);
        startActivityForResult(intent, piagreementIntent);
    }

    //창업희망자 회원가입
    public void fnCaCeo() {
        if(binding.cbCaClause.isChecked() && binding.cbCaPiagreement.isChecked()) {
            Intent intent = new Intent(CertificationActivity.this, Join1Activity.class);
            intent.putExtra("loginmethod",loginmethod);
            intent.putExtra("uid",uid);
            intent.putExtra("usertype",1);
            startActivity(intent);
        } else {
            makeToastLong("이용약관과 개인정보취급방침에 동의해주셔야 가입절차 진행이 가능합니다.");
        }
    }
    //외주사업가 회원가입
    public void fnCaOutsourcing() {
        if(binding.cbCaClause.isChecked() && binding.cbCaPiagreement.isChecked()) {
            Intent intent = new Intent(CertificationActivity.this, Join1Activity.class);
            intent.putExtra("loginmethod",loginmethod);
            intent.putExtra("uid",uid);
            intent.putExtra("usertype",4);
            startActivity(intent);
        } else {
            makeToastLong("이용약관과 개인정보취급방침에 동의해주셔야 가입절차 진행이 가능합니다.");
        }
    }
    //투자자 회원가입
    public void fnCaInv() {
        if(binding.cbCaClause.isChecked() && binding.cbCaPiagreement.isChecked()) {
            Intent intent = new Intent(CertificationActivity.this, Join1Activity.class);
            intent.putExtra("loginmethod",loginmethod);
            intent.putExtra("uid",uid);
            intent.putExtra("usertype",2);
            startActivity(intent);
        } else {
            makeToastLong("이용약관과 개인정보취급방침에 동의해주셔야 가입절차 진행이 가능합니다.");
        }
    }
    //조언가 회원가입
    public void fnCaAdv() {
        if(binding.cbCaClause.isChecked() && binding.cbCaPiagreement.isChecked()) {
            Intent intent = new Intent(CertificationActivity.this, Join1Activity.class);
            intent.putExtra("loginmethod",loginmethod);
            intent.putExtra("uid",uid);
            intent.putExtra("usertype",3);
            startActivity(intent);
        } else {
            makeToastLong("이용약관과 개인정보취급방침에 동의해주셔야 가입절차 진행이 가능합니다.");
        }
    }

    @Override
    protected int getLayoutId() { return R.layout.activity_certification; }
}
