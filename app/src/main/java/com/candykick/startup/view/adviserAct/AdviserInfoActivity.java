package com.candykick.startup.view.adviserAct;

import android.content.Intent;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import com.bumptech.glide.Glide;
import com.candykick.startup.R;
import com.candykick.startup.view.base.BaseActivity;
import com.candykick.startup.databinding.ActivityAdviserInfoBinding;
import com.candykick.startup.view.loginAccount.StartActivity;
import com.candykick.startup.view.talkAct.ChatUserData;
import com.candykick.startup.view.talkAct.ChattingActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.kakao.auth.Session;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.LogoutResponseCallback;
import com.kakao.usermgmt.callback.MeV2ResponseCallback;
import com.kakao.usermgmt.callback.UnLinkResponseCallback;
import com.kakao.usermgmt.response.MeV2Response;

import java.util.HashMap;
import java.util.List;

public class AdviserInfoActivity extends BaseActivity<ActivityAdviserInfoBinding> {

    String adviserId, roomId = null;
    ChatUserData myData = new ChatUserData();
    ChatUserData adviserData = null;

    private AdviserCareerAdapter advAdapter; private LinearLayoutManager linearLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding.setActivity(this);

        binding.toolbar.btnToolBarLeft.setOnClickListener(v -> onBackPressed());
        binding.toolbar.tvToolBar.setText("조언가 정보");

        binding.ivAdviserProfile.setBackground(new ShapeDrawable(new OvalShape()));
        binding.ivAdviserProfile.setClipToOutline(true);

        Intent gIntent = getIntent();
        if(!gIntent.getBooleanExtra("ismyData", false)) {
            adviserId = gIntent.getStringExtra("id");
            binding.btnLogoutAdv.setVisibility(View.GONE);
            binding.btnSignoutAdv.setVisibility(View.GONE);
            binding.btnAdviserChat.setVisibility(View.VISIBLE);
        } else {
            if(FirebaseAuth.getInstance().getCurrentUser() != null) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                adviserId = user.getUid();
            } else if(Session.getCurrentSession().isOpened()) {
                UserManagement.getInstance().me(new MeV2ResponseCallback() {
                    @Override
                    public void onSessionClosed(ErrorResult errorResult) {
                        progressOff();
                        makeToast("정보를 불러오지 못했습니다. 다시 시도해 주세요: "+errorResult.toString());
                        finish();
                    }

                    @Override
                    public void onSuccess(MeV2Response result) {
                        adviserId = ""+result.getId();
                    }
                });
            } else {
                makeToast("유저가 로그인되지 않은 상태입니다. 다시 로그인해주세요.");
                Intent intent = new Intent(AdviserInfoActivity.this, StartActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }

            binding.btnLogoutAdv.setVisibility(View.VISIBLE);
            binding.btnSignoutAdv.setVisibility(View.VISIBLE);
            binding.btnAdviserChat.setVisibility(View.GONE);
        }

        progressOn();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("UserCeo").document(adviserId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    adviserData = new ChatUserData(adviserId, document.get("username").toString(), 2, document.get("profileimage").toString());

                    Glide.with(AdviserInfoActivity.this).load(document.get("namecard").toString()).into(binding.ivAdviserNamecard);
                    Glide.with(AdviserInfoActivity.this).load(adviserData.profileImage).into(binding.ivAdviserProfile);
                    binding.tvAdviserIName.setText(adviserData.userName);
                    binding.tvAdviserIJob.setText(document.get("userjob").toString());
                    binding.tvAdviserIIntro.setText(document.get("userintro").toString());

                    List<HashMap<String, String>> infoDataLst = (List<HashMap<String, String>>)document.get("career");
                    linearLayoutManager = new LinearLayoutManager(AdviserInfoActivity.this);
                    binding.rvAdviserCareer.setLayoutManager(linearLayoutManager);
                    advAdapter = new AdviserCareerAdapter(AdviserInfoActivity.this, infoDataLst);
                    binding.rvAdviserCareer.setAdapter(advAdapter);
                    DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(AdviserInfoActivity.this, linearLayoutManager.getOrientation());
                    binding.rvAdviserCareer.addItemDecoration(dividerItemDecoration);

                    requestMyData();
                } else {
                    makeToastLong("정보를 불러오는 도중 오류가 발생했습니다: "+task.getException());
                    progressOff();
                    finish();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                makeToastLong("정보를 불러오는 도중 오류가 발생했습니다: "+e.getMessage());
                progressOff();
                finish();
            }
        });
    }
    
    public void fnAdvChat() {
        if(myData != null & adviserData != null) {
            progressOn();
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference reference = database.getReference();
            reference.child("chat").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for(DataSnapshot snapshot: dataSnapshot.getChildren()) {
                        if(snapshot.getKey().contains(myData.userId) & snapshot.getKey().contains(adviserData.userId)) {
                            roomId = snapshot.getKey();
                        }
                    }

                    if(roomId == null) {
                        roomId = myData.userId+","+adviserData.userId;
                    }

                    progressOff();
                    Intent intent = new Intent(AdviserInfoActivity.this, ChattingActivity.class);
                    intent.putExtra("myData", myData); //ChatUserData
                    intent.putExtra("user", adviserData); //ChatUserData
                    intent.putExtra("roomId", roomId);
                    startActivity(intent);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    if(roomId == null) {
                        roomId = myData.userId+","+adviserData.userId;
                    }

                    progressOff();
                    Intent intent = new Intent(AdviserInfoActivity.this, ChattingActivity.class);
                    intent.putExtra("myData", myData); //ChatUserData
                    intent.putExtra("user", adviserData); //ChatUserData
                    intent.putExtra("roomId", roomId);
                    startActivity(intent);
                }
            });
        } else {
            makeToast("정보를 불러올 수 없습니다. 다시 시도해 주세요.");
            finish();
        }
    }

    public void logoutAdv() {
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            FirebaseAuth.getInstance().signOut();
            makeToast("로그아웃했습니다. 이용해 주셔서 감사합니다.");
            Intent intent = new Intent(getApplicationContext(), StartActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
            ActivityCompat.finishAffinity(AdviserInfoActivity.this);
            startActivity(intent);
        } else if (!Session.getCurrentSession().isClosed()) {
            UserManagement.getInstance().requestLogout(new LogoutResponseCallback() {
                @Override
                public void onCompleteLogout() {
                    makeToast("로그아웃했습니다. 이용해 주셔서 감사합니다.");
                    Intent intent = new Intent(getApplicationContext(), StartActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    ActivityCompat.finishAffinity(AdviserInfoActivity.this);
                    startActivity(intent);
                }
            });
        }
    }


    public void signoutAdv() {
        if (!Session.getCurrentSession().isClosed()) {
            UserManagement.getInstance().requestUnlink(new UnLinkResponseCallback() {
                @Override
                public void onSessionClosed(ErrorResult errorResult) {
                    makeToast("탈퇴 처리 도중 오류가 발생했습니다.");
                    Intent intent = new Intent(getApplicationContext(), StartActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    ActivityCompat.finishAffinity(AdviserInfoActivity.this);
                    startActivity(intent);
                }

                @Override
                public void onNotSignedUp() {
                    makeToast("탈퇴 처리 도중 오류가 발생했습니다.");
                    Intent intent = new Intent(getApplicationContext(), StartActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    ActivityCompat.finishAffinity(AdviserInfoActivity.this);
                    startActivity(intent);
                }

                @Override
                public void onSuccess(Long result) {
                    makeToast("탈퇴처리했습니다. 이용해 주셔서 감사합니다.");
                    Intent intent = new Intent(getApplicationContext(), StartActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    ActivityCompat.finishAffinity(AdviserInfoActivity.this);
                    startActivity(intent);
                }
            });
        }
    }

    //내 정보를 요구하는 경우
    public void requestMyData() {
        if(FirebaseAuth.getInstance().getCurrentUser() != null) {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

            FirebaseFirestore db = FirebaseFirestore.getInstance();
            DocumentReference docRef = db.collection("UserCeo").document(user.getUid());
            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();

                        if (document.exists()) {
                            ChatUserData tmp = new ChatUserData(document.getId(), (String)document.get("username"), (long)document.get("usertype"), (String)document.get("profileimage"));
                            myData = tmp;
                            progressOff();
                        } else {
                            progressOff();
                            makeToast("유저 정보를 불러오지 못했습니다. 해당하는 유저의 정보가 없습니다.");
                            Intent intent = new Intent(AdviserInfoActivity.this, StartActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            finish();
                        }
                    } else {
                        progressOff();
                        makeToastLong("유저 정보를 불러오는 도중 오류가 발생했습니다: "+task.getException());
                        Intent intent = new Intent(AdviserInfoActivity.this, StartActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    }
                }
            });
        } else if(Session.getCurrentSession().isOpened()) {
            UserManagement.getInstance().me(new MeV2ResponseCallback() {
                @Override
                public void onSessionClosed(ErrorResult errorResult) {
                    progressOff();
                    makeToast("정보를 불러오지 못했습니다. 다시 시도해 주세요: "+errorResult.toString());
                    Intent intent = new Intent(AdviserInfoActivity.this, StartActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                }

                @Override
                public void onSuccess(MeV2Response result) {
                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    DocumentReference docRef = db.collection("UserCeo").document(""+result.getId());
                    docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if (document.exists()) {
                                    ChatUserData tmp = new ChatUserData(document.getId(), (String)document.get("username"), (long)document.get("usertype"), (String)document.get("profileimage"));
                                    myData = tmp;
                                    progressOff();
                                } else {
                                    progressOff();
                                    makeToast("유저 정보를 불러오지 못했습니다. 해당하는 유저의 정보가 없습니다.");
                                    Intent intent = new Intent(AdviserInfoActivity.this, StartActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                    finish();
                                }
                            } else {
                                progressOff();
                                makeToastLong("유저 정보를 불러오는 도중 오류가 발생했습니다: "+task.getException());
                                Intent intent = new Intent(AdviserInfoActivity.this, StartActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                finish();
                            }
                        }
                    });
                }
            });
        } else {
            progressOff();
            //로그아웃 수행하고 로그인 화면으로 돌아간 뒤 에러 메세지 띄움.
            makeToast("유저가 로그인되지 않은 상태입니다. 다시 로그인해주세요.");
            Intent intent = new Intent(AdviserInfoActivity.this, StartActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected int getLayoutId() { return R.layout.activity_adviser_info; }
}
