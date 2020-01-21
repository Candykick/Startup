package com.candykick.startup.view.loginAccount;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.candykick.startup.R;

/*
5.29
1. 로그인으로 넘길 때 StartActivity를 finish해야 하는가?
현재로써는 안전을 위해 Finish로 처리했지만 분명 사용자가 불편을 느낄 요소이기도 하다. 체크해볼 것.
2. 로그인 버튼 3개를 남기고, 카카오/구글은 회원가입 여부 체크 후 회원가입 안내하고, 이메일로 로그인은 이메일 로그인 밑에 회원가입 링크를 만들어두었다.
 (기존에는 회원가입 버튼이 따로 있었으나, 이렇게 할 경우 카카오/구글 연동이 까다로워져서 포기. 버튼이 5개나 되서 생기는 지저분함도 문제다.)
3. TedPermission은 일단 여기에 넣어두고, 나중에 필요한 부분으로 옮길 것.

투자자 사업가 조언가

조언가: 명함 필수. 이력 및 경력 선택 기입. 기관 소속의 조언가. 실명기재 필수(닉네임=실명)
 */

public class StartActivity extends AppCompatActivity {

    FragmentManager fm;
    FragmentTransaction ftran;

    //뒤로가기 버튼 입력시간
    private long pressedTime = 0;
    //각 Fragment에서 뒤로 가기 동작을 처리하기 위한 리스너, 리스너 객체, 리스너 설정 메소드.
    public interface OnBackPressedListener {
        void onBack();
    }
    private OnBackPressedListener mBackListener;
    public void setOnBackPressedListener(OnBackPressedListener listener) {
        mBackListener = listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        fm = getSupportFragmentManager();
        ftran = fm.beginTransaction();
        ftran.add(R.id.llLogin, new Start1Fragment());
        ftran.commit();
    }

    //뒤로 가기 버튼을 눌렀을 때 Fragment에 따라서 다르게 처리한다.
    //Start2Fragment -> Start1Fragment -> (두번 클릭 시)앱 종료
    @Override
    public void onBackPressed() {
        if(mBackListener != null) { //다른 Fragment에서 리스너를 설정했을 때
            mBackListener.onBack();
        } else { //리스너가 설정되지 않은 상태(Start1Fragment)
            if(pressedTime == 0) {
                Toast.makeText(StartActivity.this, "한 번 더 누르면 앱이 종료됩니다.",Toast.LENGTH_SHORT).show();
                pressedTime = System.currentTimeMillis();
            } else {
                int seconds = (int) (System.currentTimeMillis() - pressedTime);
                if(seconds > 2000) {
                    Toast.makeText(StartActivity.this, "한 번 더 누르면 앱이 종료됩니다.",Toast.LENGTH_SHORT).show();
                    pressedTime = System.currentTimeMillis();
                } else {
                    super.onBackPressed();
                    //finish();
                    //android.os.Process.killProcess(android.os.Process.myPid());
                }
            }
        }
    }

    // 카카오 로그인의 onActivityResult가 Fragment가 아닌 Activity로 넘어오기 때문에, 해당 부분을 처리함.
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.llLogin);
        if(fragment != null) {
            fragment.onActivityResult(requestCode, resultCode, data);
        }
    }
}