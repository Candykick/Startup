package com.candykick.startup.view.loginAccount;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.candykick.startup.MainActivity;
import com.candykick.startup.R;
import com.candykick.startup.engine.CdkResponseCallback;
import com.candykick.startup.model.MyUserModel;
import com.candykick.startup.view.base.App;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import static android.content.Context.MODE_PRIVATE;


public class Start2Fragment extends Fragment implements StartActivity.OnBackPressedListener {

    /*
     이메일이나 패스워드 틀렸을 때 안내 띄우기
     */

    // 이메일 로그인 관련 객체 및 변수
    private FirebaseAuth emailAuth;

    public Start2Fragment() { }

    @Override
    public void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);

        emailAuth = FirebaseAuth.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_start2, container, false);
        Button btnFSLogin = view.findViewById(R.id.btnFSLogin);
        Button btnFSJoin = view.findViewById(R.id.btnFSJoin);
        final EditText etFSEmail = view.findViewById(R.id.etFSEmail);
        final EditText etFSPassword = view.findViewById(R.id.etFSPassword);

        btnFSLogin.setOnClickListener((v) -> {
                App.getGlobalApplicationContext().progressOn(getActivity());

                emailAuth.signInWithEmailAndPassword(etFSEmail.getText().toString(), etFSPassword.getText().toString())
                        .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful()) {
                                    MyUserModel.getInstance().setUserData(new CdkResponseCallback<String>() {
                                        @Override
                                        public void onDataLoaded(String result, String item) {
                                            App.getGlobalApplicationContext().progressOff();
                                            Intent intent = new Intent(getActivity(), MainActivity.class);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                            startActivity(intent);
                                            getActivity().finish();
                                        }

                                        @Override
                                        public void onDataNotAvailable(String error) {
                                            App.getGlobalApplicationContext().progressOff();
                                            Toast.makeText(getActivity(), getResources().getString(R.string.toastLoginErr) + task.getException(), Toast.LENGTH_LONG).show();
                                        }

                                        @Override
                                        public void onDataEmpty() {
                                            App.getGlobalApplicationContext().progressOff();
                                            Toast.makeText(getActivity(), getResources().getString(R.string.toastLoginErr) + task.getException().toString(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            }
                        });
        });

        btnFSJoin.setOnClickListener((v) -> {
                Intent intent = new Intent(getActivity(), NewCertificationActivity.class);
                intent.putExtra("loginmethod","email");
                startActivity(intent);
        });

        return view;
    }

    @Override
    public void onBack() {
        //리스너 설정을 위해 Activity를 받아옵니다.
        StartActivity activity = (StartActivity)getActivity();
        //한 번 뒤로가기 버튼을 눌렀다면 리스너를 해제합니다.
        activity.setOnBackPressedListener(null);
        //Start1Fragment로 교체
        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.llLogin, new Start1Fragment()).commit();
    }

    //Fragment 호출 시 호출되는 오버라이드 메소드.
    //Context로 안 되면 Activity로 해볼것.
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        ((StartActivity)context).setOnBackPressedListener(this);
    }
}
