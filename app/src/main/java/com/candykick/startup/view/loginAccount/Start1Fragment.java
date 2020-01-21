package com.candykick.startup.view.loginAccount;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.candykick.startup.MainActivity;
import com.candykick.startup.engine.CdkResponseCallback;
import com.candykick.startup.model.MyUserModel;
import com.candykick.startup.view.base.App;
import com.candykick.startup.R;
import com.candykick.startup.view.base.User;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.kakao.auth.AuthType;
import com.kakao.auth.ISessionCallback;
import com.kakao.auth.Session;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.LogoutResponseCallback;
import com.kakao.usermgmt.callback.MeV2ResponseCallback;
import com.kakao.usermgmt.response.MeV2Response;
import com.kakao.util.exception.KakaoException;

import static android.content.Context.MODE_PRIVATE;

/*
2019.06.03
1. 카카오 로그인: 현재 닉네임, 이메일, 프로필정보만 수신함. 나머지 부분도 수신해서 반영할 수 있도록 할 것.
   (다만 권한 허용/거부 시 코드가 각각 필요해서 노가다가 된다. 해당 부분은 MVVM 적용 후 처리할 것.)
   추가로, 이메일이 안 넘어오는 경우도 있으니 그거 고려할 것.
2. 카카오 로그인 라이브러리 업데이트함. 업데이트 이후 사용하던 함수들 전부 deprecated됬으니 소스코드 업데이트.
 */

public class Start1Fragment extends Fragment {

    //프래그먼트 관련 객체
    FragmentManager fm;
    FragmentTransaction ftran;

    //로그인 관련 객체 및 콜백
    private SessionCallback KakaoCallback;
    private FirebaseAuth GoogleModule;
    private GoogleSignInClient GoogleClient;

    public Start1Fragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //로그인 초기화
        KakaoCallback = new SessionCallback();
        Session.getCurrentSession().addCallback(KakaoCallback);
        App.setCurrentActivity(getActivity());
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .requestProfile()
                .build();
        GoogleClient = GoogleSignIn.getClient(getActivity(), gso);
        GoogleModule = FirebaseAuth.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_start1, container, false);
        Button btnLgKakao = view.findViewById(R.id.btnLgKakao);
        Button btnLgGoogle = view.findViewById(R.id.btnLgGoogle);
        Button btnLgEmail = view.findViewById(R.id.btnLgEmail);

        btnLgKakao.setOnClickListener((v) -> {
            App.getGlobalApplicationContext().progressOn(getActivity());
            Session.getCurrentSession().open(AuthType.KAKAO_LOGIN_ALL, getActivity());
        });
        btnLgGoogle.setOnClickListener((v) -> {
                App.getGlobalApplicationContext().progressOn(getActivity());
                Intent intent = GoogleClient.getSignInIntent();
                startActivityForResult(intent, 9001);
        });
        btnLgEmail.setOnClickListener((v) -> {
                fm = getActivity().getSupportFragmentManager();
                ftran = fm.beginTransaction();
                ftran.replace(R.id.llLogin, new Start2Fragment());
                ftran.commit();
        });

        return view;
    }

    //각 로그인의 onActivityResult
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 9001) { //구글
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (Exception e) {
                /*if(e.toString().contains("ApiException")) {
                    Toast.makeText(getActivity(),"구글 플레이 버전이 너무 낮습니다. 구글 플레이를 업데이트해주세요.",Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.google.android.gms&hl=ko"));
                    startActivity(intent);
                } else {*/
                    Toast.makeText(getActivity(),getResources().getString(R.string.toastLoginErr) + e.toString(),Toast.LENGTH_SHORT).show();
                //}
            }
        } else if (Session.getCurrentSession().handleActivityResult(requestCode, resultCode, data)) { //카카오
            super.onActivityResult(requestCode, resultCode, data);
            return;
        }
    }

    //카카오 로그인 구현
    private class SessionCallback implements ISessionCallback {
        @Override
        public void onSessionOpened() {
            UserManagement.getInstance().me(new MeV2ResponseCallback() {
                @Override
                public void onSessionClosed(ErrorResult errorResult) {
                    App.getGlobalApplicationContext().progressOff();
                    Toast.makeText(getActivity(), getResources().getString(R.string.toastLoginErr)+errorResult.toString(),Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onSuccess(MeV2Response result) {
                    //1.Firestore에 유저 정보가 있는지 검색
                    MyUserModel.getInstance().setUserData(new CdkResponseCallback<String>() {
                        @Override
                        public void onDataLoaded(String result, String item) {
                            App.getGlobalApplicationContext().progressOff();
                            Intent intent = new Intent(getActivity(), MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
                            startActivity(intent);
                            getActivity().finish();
                        }

                        @Override
                        public void onDataNotAvailable(String error) {
                            Toast.makeText(getActivity(),getResources().getString(R.string.toastLoginErr)+error, Toast.LENGTH_LONG).show();

                            UserManagement.getInstance().requestLogout(new LogoutResponseCallback() {
                                @Override
                                public void onCompleteLogout() {
                                    App.getGlobalApplicationContext().progressOff();
                                        /*Intent intent = new Intent(getActivity(), StartActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                        ActivityCompat.finishAffinity(UserInfoActivity.this);
                                        startActivity(intent);*/
                                }
                            });
                        }

                        @Override
                        public void onDataEmpty() {
                            App.getGlobalApplicationContext().progressOff();
                            Intent intent = new Intent(getActivity(), NewCertificationActivity.class);
                            intent.putExtra("loginmethod","kakao");
                            intent.putExtra("uid",""+result.getId());
                            startActivity(intent);
                        }
                    });
                }
            });

            /*UserManagement.getInstance().requestMe(new MeResponseCallback() {
                @Override
                public void onFailure(ErrorResult errorResult) {
                    ErrorCode result = ErrorCode.valueOf(errorResult.getErrorCode());
                    if (result == ErrorCode.CLIENT_ERROR_CODE) { //인터넷 연결이 끊어진 경우.
                        Toast.makeText(getActivity(), "인터넷 연결이 끊어졌습니다.", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getActivity(), "로그인 중 에러가 발생했습니다. 에러코드: " + errorResult.toString(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onSessionClosed(ErrorResult errorResult) {}

                @Override
                public void onNotSignedUp() {}

                @Override
                public void onSuccess(UserProfile result) {
                    user = new User("",result.getNickname(),result.getEmail(), "kakao", result.getProfileImagePath(), "");

                    Intent intent = new Intent(getActivity(), NewCertificationActivity.class);
                    intent.putExtra("loginmethod","kakao");
                    intent.putExtra("uid",result.get`)
                    startActivity(intent);
                }
            });*/
        }

        @Override
        public void onSessionOpenFailed(KakaoException e) {
            if (e != null) {
                App.getGlobalApplicationContext().progressOff();
                Toast.makeText(getActivity(), getResources().getString(R.string.toastKakaoNotSignedUp) + e.toString(), Toast.LENGTH_LONG).show();
                /*Intent intent = getIntent();
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
                LoginActivity.this.finish();
                startActivity(intent);*/
            }
        }
    }

    //구글 로그인 구현
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        //Log.d("Individual Car", "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        GoogleModule.signInWithCredential(credential)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            MyUserModel.getInstance().setUserData(new CdkResponseCallback<String>() {
                                @Override
                                public void onDataLoaded(String result, String item) {
                                    App.getGlobalApplicationContext().progressOff();
                                    Intent intent = new Intent(getActivity(), MainActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                    startActivity(intent);
                                    getActivity().finish();
                                }

                                @Override
                                public void onDataNotAvailable(String error) {
                                    App.getGlobalApplicationContext().progressOff();
                                    Toast.makeText(getActivity(),getResources().getString(R.string.toastLoginErr)+task.getException(), Toast.LENGTH_LONG).show();
                                    FirebaseAuth.getInstance().signOut();
                                }

                                @Override
                                public void onDataEmpty() {
                                    App.getGlobalApplicationContext().progressOff();
                                    Intent intent = new Intent(getActivity(), NewCertificationActivity.class);
                                    intent.putExtra("loginmethod","google");
                                    startActivity(intent);
                                }
                            });
                        } else {
                            App.getGlobalApplicationContext().progressOff();
                            Toast.makeText(getActivity(),getResources().getString(R.string.toastLoginErr) + task.getException().toString(),Toast.LENGTH_SHORT).show();
                            FirebaseAuth.getInstance().signOut();
                        }
                    }
                });
    }

    @Override
    public void onDestroy() {
        App.getGlobalApplicationContext().progressOff();
        super.onDestroy();
        Session.getCurrentSession().removeCallback(KakaoCallback);
    }

    /*@Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }*/
}