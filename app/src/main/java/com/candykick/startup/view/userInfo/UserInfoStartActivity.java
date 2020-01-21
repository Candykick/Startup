package com.candykick.startup.view.userInfo;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.candykick.startup.view.base.App;
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
import com.kakao.usermgmt.callback.MeV2ResponseCallback;
import com.kakao.usermgmt.response.MeV2Response;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class UserInfoStartActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);

        App.getGlobalApplicationContext().progressOn(UserInfoStartActivity.this);

        Intent gintent = getIntent();
        boolean isMyData = gintent.getBooleanExtra("ismyData", false);
        if(isMyData) {
            if(Session.getCurrentSession().isOpened()) {
                Log.d("Startup","Kakao");
                UserManagement.getInstance().me(new MeV2ResponseCallback() {
                    @Override
                    public void onSessionClosed(ErrorResult errorResult) {
                        App.getGlobalApplicationContext().progressOff();
                        Toast.makeText(UserInfoStartActivity.this,"정보를 불러오지 못했습니다. 다시 시도해 주세요: " + errorResult.toString(),Toast.LENGTH_LONG).show();
                        UserInfoStartActivity.this.finish();
                    }

                    @Override
                    public void onSuccess(MeV2Response result) {
                        String email = result.getKakaoAccount().getEmail();

                        findUserInfo(email, ""+result.getId(), true);
                    }
                });
            } else if(FirebaseAuth.getInstance().getCurrentUser() != null) {
                Log.d("Startup","Google");
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                String email = user.getEmail();

                findUserInfo(email, user.getUid(), true);
            } else {
                //로그아웃 수행하고 로그인 화면으로 돌아간 뒤 에러 메세지 띄움.
                App.getGlobalApplicationContext().progressOff();
                Toast.makeText(UserInfoStartActivity.this,"유저가 로그인되지 않은 상태입니다. 다시 로그인해주세요.",Toast.LENGTH_LONG).show();
                Intent intent = new Intent(UserInfoStartActivity.this, StartActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                UserInfoStartActivity.this.startActivity(intent);
                UserInfoStartActivity.this.finish();
            }
        } else {
            findUserInfo("",gintent.getStringExtra("userId"), false);
        }
    }

    private void findUserInfo(String email, String userid, boolean ismydata) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("UserCeo").document(userid);

        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();

                    Log.d("Startup", document.toString());

                    if (document.exists()) {
                        if((long)document.get("usertype") == 0) {
                            Intent intent = new Intent(UserInfoStartActivity.this, UserInfoActivityNew.class);
                            intent.putExtra("name", (String)document.get("username"));
                            intent.putExtra("email",email);
                            intent.putExtra("profileimage",(String)document.get("profileimage"));
                            intent.putExtra("userType", "유형: 창업희망자");
                            intent.putExtra("ismydata",ismydata);

                            if((long)document.get("gender") == 1)
                                intent.putExtra("gender","남성");
                            else if((long)document.get("gender") == 2)
                                intent.putExtra("gender","여성");

                            intent.putExtra("age",(String)document.get("age"));

                            intent.putExtra("role",document.get("role").toString());

                            String[] jobTagsString = document.get("businesstype").toString().split(", ");
                            ArrayList<String> jobTags = new ArrayList<>();
                            for(String jobTag: jobTagsString) {
                                jobTags.add(jobTag);
                            }
                            intent.putExtra("jobTags",jobTags);

                            UserInfoStartActivity.this.startActivity(intent);
                            UserInfoStartActivity.this.finish();
                        } else if((long)document.get("usertype") == 1) {
                            Intent intent = new Intent(UserInfoStartActivity.this, UserInfoActivityNew.class);
                            intent.putExtra("name", (String)document.get("username"));
                            intent.putExtra("email",email);
                            intent.putExtra("profileimage",(String)document.get("profileimage"));
                            intent.putExtra("userType", "유 형 :  투 자 자");
                            intent.putExtra("ismydata",ismydata);

                            intent.putExtra("wantinvcapital",(String)document.get("wantinvcapital"));
                            intent.putExtra("wantinvtype",(String)document.get("wantinvtype"));

                            String[] jobTagsString = document.get("wantinvjob").toString().split(", ");
                            ArrayList<String> jobTags = new ArrayList<>();
                            for(String jobTag: jobTagsString) {
                                jobTags.add(jobTag);
                            }
                            intent.putExtra("jobTags",jobTags);

                            UserInfoStartActivity.this.startActivity(intent);
                            UserInfoStartActivity.this.finish();
                        } else if((long)document.get("usertype") == 3) {
                            Intent intent = new Intent(UserInfoStartActivity.this, UserInfoActivityNew.class);
                            intent.putExtra("name", (String)document.get("username"));
                            intent.putExtra("email",email);
                            intent.putExtra("profileimage",(String)document.get("profileimage"));
                            intent.putExtra("userType", "유형: 외주사업가");
                            intent.putExtra("ismydata",ismydata);

                            intent.putExtra("taxinvoicepossible",(boolean)document.get("taxinvoicepossible") ? "발행 가능" : "발행 불가");
                            intent.putExtra("userDes",(String)document.get("userDes"));

                            UserInfoStartActivity.this.startActivity(intent);
                            UserInfoStartActivity.this.finish();
                        } else if((long)document.get("usertype") == 2) {
                            Intent intent = new Intent(UserInfoStartActivity.this, UserInfoAdvActivity.class);
                            intent.putExtra("name", (String)document.get("username"));
                            intent.putExtra("email",email);
                            intent.putExtra("profileimage",(String)document.get("profileimage"));
                            intent.putExtra("ismydata",ismydata);

                            intent.putExtra("namecard", (String)document.get("namecard"));
                            intent.putExtra("userjob",(String)document.get("userjob"));
                            intent.putExtra("userintro", (String)document.get("userintro"));
                            ArrayList<HashMap<String, String>> careerList = new ArrayList<>();
                            careerList.addAll((List<HashMap<String, String>>)document.get("career"));
                            intent.putExtra("career", careerList);

                            UserInfoStartActivity.this.startActivity(intent);
                            UserInfoStartActivity.this.finish();
                        }
                        //myData = new ChatUserData(""+result.getId(), (String)document.get("username"), (long)document.get("usertype"), (String)document.get("profileimage"));
                    } else {
                        Toast.makeText(UserInfoStartActivity.this,"유저 정보를 불러오지 못했습니다. 해당하는 유저의 정보가 없습니다.",Toast.LENGTH_LONG).show();
                        UserInfoStartActivity.this.finish();
                    }
                } else {
                    App.getGlobalApplicationContext().progressOff();
                    Toast.makeText(UserInfoStartActivity.this,"유저 정보를 불러오는 도중 오류가 발생했습니다: "+task.getException(),Toast.LENGTH_LONG).show();
                    UserInfoStartActivity.this.finish();
                }
            }
        });
    }
}
