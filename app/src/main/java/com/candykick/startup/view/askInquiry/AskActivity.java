package com.candykick.startup.view.askInquiry;

import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ArrayAdapter;

import com.candykick.startup.R;
import com.candykick.startup.view.base.BaseActivity;
import com.candykick.startup.databinding.ActivityAskBinding;

import javax.mail.MessagingException;
import javax.mail.SendFailedException;

public class AskActivity extends BaseActivity<ActivityAskBinding> {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding.setActivity(this);

        binding.toolbar.btnToolBarLeft.setOnClickListener(v -> onBackPressed());
        binding.toolbar.tvToolBar.setText("문의하기");

        ArrayAdapter<String> adapter = new ArrayAdapter<>(AskActivity.this, R.layout.spinner_item, getResources().getStringArray(R.array.strArrayAsk));
        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        binding.spAsk.setAdapter(adapter);

        //이메일 전송 위한 준비
        /*StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
        .permitDiskReads()
        .permitDiskWrites()
        .permitNetwork().build());*/
    }

    public void sendForAsk() {
        String sendMessage = "유저이름: 아직없음"+"\n";
        sendMessage += "문의 종류: "+binding.spAsk.getSelectedItem().toString()+"\n";
        sendMessage += "문의 내용: "+binding.etAsk.getText().toString();

        MailSend mailSend = new MailSend();
        try {
            String result = mailSend.execute(new String[]{sendMessage}).get();
            makeToast(result);
        } catch (Exception e) {
            makeToast("오류가 발생했습니다: "+e.getMessage());
        }
    }

    @Override
    protected int getLayoutId() { return R.layout.activity_ask; }


    public class MailSend extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            progressOn();
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                GmailSender gmailSender = new GmailSender("sider.named.outer@gmail.com", "devilryeang");
                gmailSender.sendMail("타입)유저이름님의 몇월몇일몇시몇분 문의입니다", params[0], "sider.named.outer@gmail.com", "mbpsolamsider@gmail.com");
                progressOff();
                return "문의를 성공적으로 접수했습니다. 최대한 빨리 답변해드리겠습니다!";
            } catch (SendFailedException e) {
                progressOff();
                return "문의 이메일 형식이 잘못되었습니다.";
            } catch (MessagingException e) {
                progressOff();
                return "인터넷 연결을 확인해주십시오.";
            } catch (Exception e) {
                progressOff();
                e.printStackTrace();
                return "오류가 발생했습니다: "+e.getMessage();
            }
        }
    }
}