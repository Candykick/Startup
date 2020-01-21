package com.candykick.startup.view.adviserAct;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.candykick.startup.R;
import com.candykick.startup.view.base.BaseActivity;
import com.candykick.startup.databinding.ActivityAdviserBoardBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.kakao.auth.Session;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.MeV2ResponseCallback;
import com.kakao.usermgmt.response.MeV2Response;

import java.util.ArrayList;

public class AdviserBoardActivity extends BaseActivity<ActivityAdviserBoardBinding> {

    String myId;
    ArrayList<AdviserBoardData> arrayList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding.setActivity(this);

        binding.toolbar.btnToolBarLeft.setOnClickListener(v -> onBackPressed());
        binding.toolbar.tvToolBar.setText("조언가 목록");

        progressOn();

        if(FirebaseAuth.getInstance().getCurrentUser() != null) {
            myId = FirebaseAuth.getInstance().getCurrentUser().getUid();

            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("UserCeo").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        for(QueryDocumentSnapshot document : task.getResult()) { //조언가 타입의 유저 정보만 담는다.
                            if((long)(document.getData().get("usertype")) == 2) {
                                if(myId != document.getId()) {
                                    AdviserBoardData data = new AdviserBoardData();
                                    data.adviserId = document.getId();
                                    data.adviserName = document.getData().get("username").toString();
                                    data.adviserNameCard = document.getData().get("namecard").toString();
                                    data.adviserIntro = document.getData().get("userintro").toString();
                                    data.adviserJob = document.getData().get("userjob").toString();
                                    data.profile = document.getData().get("profileimage").toString();

                                    arrayList.add(data);
                                }
                            }
                        }
                        AdviserBoardAdapter adapter = new AdviserBoardAdapter(AdviserBoardActivity.this, arrayList);
                        binding.lvAdviserBoard.setAdapter(adapter);

                        binding.lvAdviserBoard.setOnItemClickListener((parentView, view, position, id) -> {
                            Intent intent = new Intent(AdviserBoardActivity.this, AdviserBoardNewActivity.class);
                            //intent.putExtra("ismyData", false);
                            //intent.putExtra("id",arrayList.get(position).adviserId);
                            startActivity(intent);
                        });
                        progressOff();
                    } else {
                        progressOff();
                        makeToastLong("정보를 불러오는 도중 오류가 발생했습니다: "+task.getException());
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressOff();
                    makeToastLong("정보를 불러오는 도중 오류가 발생했습니다: "+e.getMessage());
                }
            });
        } else if(Session.getCurrentSession().isOpened()) {
            UserManagement.getInstance().me(new MeV2ResponseCallback() {
                @Override
                public void onSessionClosed(ErrorResult errorResult) {
                    progressOff();
                    makeToast("정보를 불러오지 못했습니다. 다시 시도해 주세요: " + errorResult.toString());
                    finish();
                }

                @Override
                public void onSuccess(MeV2Response result) {
                    myId = result.getId()+"";

                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    db.collection("UserCeo").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for(QueryDocumentSnapshot document : task.getResult()) { //조언가 타입의 유저 정보만 담는다.
                                    if((long)(document.getData().get("usertype")) == 2) {
                                        if(myId != document.getId()) {
                                            AdviserBoardData data = new AdviserBoardData();
                                            data.adviserId = document.getId();
                                            data.adviserName = document.getData().get("username").toString();
                                            data.adviserNameCard = document.getData().get("namecard").toString();
                                            data.adviserIntro = document.getData().get("userintro").toString();
                                            data.adviserJob = document.getData().get("userjob").toString();
                                            data.profile = document.getData().get("profileimage").toString();

                                            arrayList.add(data);
                                        }
                                    }
                                }
                                AdviserBoardAdapter adapter = new AdviserBoardAdapter(AdviserBoardActivity.this, arrayList);
                                binding.lvAdviserBoard.setAdapter(adapter);

                                binding.lvAdviserBoard.setOnItemClickListener((parentView, view, position, id) -> {
                                    Intent intent = new Intent(AdviserBoardActivity.this, AdviserInfoActivity.class);
                                    intent.putExtra("ismyData", false);
                                    intent.putExtra("id",arrayList.get(position).adviserId);
                                    startActivity(intent);
                                });
                                progressOff();
                            } else {
                                progressOff();
                                makeToastLong("정보를 불러오는 도중 오류가 발생했습니다: "+task.getException());
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressOff();
                            makeToastLong("정보를 불러오는 도중 오류가 발생했습니다: "+e.getMessage());
                        }
                    });
                }
            });
        }
    }

    @Override
    protected int getLayoutId() {return R.layout.activity_adviser_board; }
}
