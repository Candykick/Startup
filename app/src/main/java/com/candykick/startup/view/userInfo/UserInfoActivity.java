package com.candykick.startup.view.userInfo;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;

import com.bumptech.glide.Glide;
import com.candykick.startup.R;
import com.candykick.startup.view.base.BaseActivity;
import com.candykick.startup.databinding.ActivityUserInfoBinding;
import com.candykick.startup.view.loginAccount.StartActivity;
import com.candykick.startup.view.talkAct.ChatUserData;
import com.candykick.startup.view.talkAct.ChattingActivity;
import com.google.android.gms.tasks.OnCompleteListener;
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

public class UserInfoActivity extends BaseActivity<ActivityUserInfoBinding> {

    ChatUserData myData = new ChatUserData();
    ChatUserData otherData = null;

    String roomId = null;
    int btnFlag = 0; //1: 회원탈퇴, 2: 채팅

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding.setActivity(this);

        binding.toolbar.tvToolBar.setText("유저 정보");
        binding.toolbar.btnToolBarLeft.setOnClickListener(v -> super.onBackPressed());

        binding.ivUIProfile.setBackground(new ShapeDrawable(new OvalShape()));
        binding.ivUIProfile.setClipToOutline(true);

        Intent intent = getIntent();
        if(intent.getBooleanExtra("ismyData",true)) {
            btnFlag = 1;
            requestMyData();
        } else {
            btnFlag = 2;
            String userId = intent.getStringExtra("userId");
            requestOtherData(userId);
        }
    }

    //내 정보를 요구하는 경우
    public void requestMyData() {
        progressOn();

        binding.btnLogout.setVisibility(View.VISIBLE);
        binding.tvUIEmail.setVisibility(View.VISIBLE);
        binding.btnSignout.setText("회원탈퇴");

        if(Session.getCurrentSession().isOpened()) {
            Log.d("Startup","Kakao");
            UserManagement.getInstance().me(new MeV2ResponseCallback() {
                @Override
                public void onSessionClosed(ErrorResult errorResult) {
                    progressOff();
                    makeToast("정보를 불러오지 못했습니다. 다시 시도해 주세요: "+errorResult.toString());
                    finish();
                }

                @Override
                public void onSuccess(MeV2Response result) {
                    binding.tvUIName.setText(result.getNickname());
                    binding.tvUIEmail.setText(result.getKakaoAccount().getEmail());
                    Glide.with(UserInfoActivity.this).load(result.getProfileImagePath()).into(binding.ivUIProfile);

                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    DocumentReference docRef = db.collection("UserCeo").document(""+result.getId());
                    docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();

                                if (document.exists()) {
                                    if((long)document.get("usertype") == 0) {
                                        binding.tvUIUserType.setText("사업가");
                                        binding.llUI6.setVisibility(View.VISIBLE);
                                        binding.tvUI1.setText("성별  " );
                                        if((long)document.get("gender") == 1)
                                            binding.tvUIUserInfo1.setText("남성");
                                        else if((long)document.get("gender") == 2)
                                            binding.tvUIUserInfo1.setText("여성");
                                        binding.tvUI2.setText("나잇대   ");
                                        binding.tvUIUserInfo2.setText((String)document.get("age"));
                                        binding.tvUI3.setText("역할   ");
                                        binding.tvUIUserInfo3.setText((String)document.get("role"));
                                        binding.tvUI4.setText("업종   ");
                                        binding.tvUIUserInfo4.setText((String)document.get("businesstype"));
                                    } else if((long)document.get("usertype") == 1) {
                                        binding.tvUIUserType.setText("투자자");
                                        binding.llUI6.setVisibility(View.GONE);
                                        binding.tvUI1.setText("투자희망업종  " );
                                        binding.tvUIUserInfo1.setText((String)document.get("wantinvjob"));
                                        binding.tvUI2.setText("투자희망금액  " );
                                        binding.tvUIUserInfo2.setText((String)document.get("wantinvcapital"));
                                        binding.tvUI3.setText("투자유형  " );
                                        binding.tvUIUserInfo3.setText((String)document.get("wantinvtype"));
                                    } else if((long)document.get("usertype") == 3) {
                                        binding.tvUIUserType.setText("외주사업가");
                                        binding.llUI5.setVisibility(View.GONE);
                                        binding.llUI6.setVisibility(View.GONE);
                                        binding.tvUI1.setText("세금계산서 발행 " );
                                        binding.tvUIUserInfo1.setText((boolean)document.get("taxinvoicepossible") ? "가능" : "불가");
                                        binding.tvUI2.setText("한 줄 소개 : ");
                                        binding.tvUIUserInfo2.setText((String)document.get("userDes"));
                                    }
                                    myData = new ChatUserData(""+result.getId(), (String)document.get("username"), (long)document.get("usertype"), (String)document.get("profileimage"));
                                } else {
                                    makeToast("유저 정보를 불러오지 못했습니다. 해당하는 유저의 정보가 없습니다.");
                                }

                                progressOff();
                            } else {
                                progressOff();
                                makeToastLong("유저 정보를 불러오는 도중 오류가 발생했습니다: "+task.getException());
                            }
                        }
                    });
                }
            });
        } else if(FirebaseAuth.getInstance().getCurrentUser() != null) {
            Log.d("Startup","Google");
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            binding.tvUIName.setText(user.getDisplayName());
            binding.tvUIEmail.setText(user.getEmail());
            Glide.with(UserInfoActivity.this).load(user.getPhotoUrl()).into(binding.ivUIProfile);

            FirebaseFirestore db = FirebaseFirestore.getInstance();
            DocumentReference docRef = db.collection("UserCeo").document(user.getUid());
            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();

                        if (document.exists()) {
                            if((long)document.get("usertype") == 0) {
                                binding.tvUIUserType.setText("창업희망자");
                                binding.llUI6.setVisibility(View.VISIBLE);
                                binding.tvUI1.setText("성별  " );
                                if((long)document.get("gender") == 1)
                                    binding.tvUIUserInfo1.setText("남성");
                                else if((long)document.get("gender") == 2)
                                    binding.tvUIUserInfo1.setText("여성");
                                binding.tvUI2.setText("나잇대   ");
                                binding.tvUIUserInfo2.setText((String)document.get("age"));
                                binding.tvUI3.setText("역할   ");
                                binding.tvUIUserInfo3.setText((String)document.get("role"));
                                binding.tvUI4.setText("업종   ");
                                binding.tvUIUserInfo4.setText((String)document.get("businesstype"));
                            } else if((long)document.get("usertype") == 1) {
                                binding.tvUIUserType.setText("투자자");
                                binding.llUI6.setVisibility(View.GONE);
                                binding.tvUI1.setText("투자희망업종  " );
                                binding.tvUI2.setText("투자희망금액  " );
                                binding.tvUI3.setText("투자유형  " );
                                binding.tvUIUserInfo1.setText((String)document.get("wantinvjob"));
                                binding.tvUIUserInfo2.setText((String)document.get("wantinvcapital"));
                                binding.tvUIUserInfo3.setText((String)document.get("wantinvtype"));
                            } else if((long)document.get("usertype") == 3) {
                                binding.tvUIUserType.setText("외주사업가");
                                binding.llUI5.setVisibility(View.GONE);
                                binding.llUI6.setVisibility(View.GONE);
                                binding.tvUI1.setText("세금계산서 발행 " );
                                binding.tvUIUserInfo1.setText((boolean)document.get("taxinvoicepossible") ? "가능" : "불가");
                                binding.tvUI2.setText("한 줄 소개 : ");
                                binding.tvUIUserInfo2.setText((String)document.get("userDes"));
                            }

                            myData = new ChatUserData(user.getUid(), (String)document.get("username"), (long)document.get("usertype"), (String)document.get("profileimage"));
                        } else {
                            makeToast("유저 정보를 불러오지 못했습니다. 해당하는 유저의 정보가 없습니다.");
                        }
                        progressOff();
                    } else {
                        progressOff();
                        makeToastLong("유저 정보를 불러오는 도중 오류가 발생했습니다: "+task.getException());
                    }
                }
            });
        } else {
            //로그아웃 수행하고 로그인 화면으로 돌아간 뒤 에러 메세지 띄움.
            progressOff();
            makeToast("유저가 로그인되지 않은 상태입니다. 다시 로그인해주세요.");
            Intent intent = new Intent(UserInfoActivity.this, StartActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }
    }

    //다른 유저의 정보를 요구하는 경우
    public void requestOtherData(String id) {
        progressOn();

        binding.btnLogout.setVisibility(View.GONE);
        binding.tvUIEmail.setVisibility(View.GONE);
        binding.tvUIEmailTitle.setVisibility(View.GONE);
        binding.btnSignout.setText("채팅하기");

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("UserCeo").document(id);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    binding.tvUIName.setText((String)document.get("username"));
                    Glide.with(UserInfoActivity.this).load((String)document.get("profileimage")).into(binding.ivUIProfile);

                    if (document.exists()) {
                        if((long)document.get("usertype") == 0) {
                            binding.tvUIUserType.setText("사업가");
                            binding.llUI6.setVisibility(View.VISIBLE);
                            binding.tvUI1.setText("성별  " );
                            if((long)document.get("gender") == 1)
                                binding.tvUIUserInfo1.setText("남성");
                            else if((long)document.get("gender") == 2)
                                binding.tvUIUserInfo1.setText("여성");
                            binding.tvUI2.setText("나잇대   ");
                            binding.tvUIUserInfo2.setText((String)document.get("age"));
                            binding.tvUI3.setText("역할   ");
                            binding.tvUIUserInfo3.setText((String)document.get("role"));
                            binding.tvUI4.setText("업종   ");
                            binding.tvUIUserInfo4.setText((String)document.get("businesstype"));
                        } else if((long)document.get("usertype") == 1) {
                            binding.tvUIUserType.setText("투자자");
                            binding.llUI6.setVisibility(View.GONE);
                            binding.tvUI1.setText("투자희망업종  " );
                            binding.tvUIUserInfo1.setText((String)document.get("wantinvjob"));
                            binding.tvUI2.setText("투자희망금액  " );
                            binding.tvUIUserInfo2.setText((String)document.get("wantinvcapital"));
                            binding.tvUI3.setText("투자유형  " );
                            binding.tvUIUserInfo3.setText((String)document.get("wantinvtype"));
                        } else if((long)document.get("usertype") == 3) {
                            binding.tvUIUserType.setText("외주사업가");
                            binding.llUI5.setVisibility(View.GONE);
                            binding.llUI6.setVisibility(View.GONE);
                            binding.tvUI1.setText("세금계산서 발행 " );
                            binding.tvUIUserInfo1.setText((boolean)document.get("taxinvoicepossible") ? "가능" : "불가");
                            binding.tvUI2.setText("한 줄 소개 : ");
                            binding.tvUIUserInfo2.setText((String)document.get("userDes"));
                        }

                        otherData = new ChatUserData(id, (String)document.get("username"), (long)document.get("usertype"), (String)document.get("profileimage"));


                        if(Session.getCurrentSession().isOpened()) {
                            UserManagement.getInstance().me(new MeV2ResponseCallback() {
                                @Override
                                public void onSessionClosed(ErrorResult errorResult) {
                                    progressOff();
                                    makeToast("정보를 불러오지 못했습니다. 다시 시도해 주세요: "+errorResult.toString());
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
                                                    myData = new ChatUserData(""+result.getId(), (String)document.get("username"), (long)document.get("usertype"), (String)document.get("profileimage"));
                                                } else {
                                                    makeToast("유저 정보를 불러오지 못했습니다. 해당하는 유저의 정보가 없습니다.");
                                                }

                                                progressOff();
                                            } else {
                                                progressOff();
                                                makeToastLong("유저 정보를 불러오는 도중 오류가 발생했습니다: "+task.getException());
                                                finish();
                                            }
                                        }
                                    });
                                }
                            });
                        } else if(FirebaseAuth.getInstance().getCurrentUser() != null) {
                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                            FirebaseFirestore db = FirebaseFirestore.getInstance();
                            DocumentReference docRef = db.collection("UserCeo").document(user.getUid());
                            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()) {
                                        DocumentSnapshot document = task.getResult();

                                        if (document.exists()) {
                                            myData = new ChatUserData(user.getUid(), (String)document.get("username"), (long)document.get("usertype"), (String)document.get("profileimage"));
                                        } else {
                                            makeToast("유저 정보를 불러오지 못했습니다. 해당하는 유저의 정보가 없습니다.");
                                        }
                                        progressOff();
                                    } else {
                                        progressOff();
                                        makeToastLong("유저 정보를 불러오는 도중 오류가 발생했습니다: "+task.getException());
                                        finish();
                                    }
                                }
                            });
                        } else {
                            //로그아웃 수행하고 로그인 화면으로 돌아간 뒤 에러 메세지 띄움.
                            progressOff();
                            makeToast("유저가 로그인되지 않은 상태입니다. 다시 로그인해주세요.");
                            Intent intent = new Intent(UserInfoActivity.this, StartActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            finish();
                        }
                    } else {
                        progressOff();
                        makeToast("유저 정보를 불러오지 못했습니다. 해당하는 유저의 정보가 없습니다.");
                        finish();
                    }
                    progressOff();
                } else {
                    progressOff();
                    makeToastLong("유저 정보를 불러오는 도중 오류가 발생했습니다: "+task.getException());
                }
            }
        });
    }

    //회원탈퇴,채팅하기 버튼 클릭 시
    public void signout() {
        if(btnFlag == 1) {
            //회원탈퇴로 작동
            //DB 자료 지우는 건 미구현상태.
            if (!Session.getCurrentSession().isClosed()) {
                UserManagement.getInstance().requestUnlink(new UnLinkResponseCallback() {
                    @Override
                    public void onSessionClosed(ErrorResult errorResult) {
                        makeToast("탈퇴 처리 도중 오류가 발생했습니다.");
                        Intent intent = new Intent(getApplicationContext(), StartActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        ActivityCompat.finishAffinity(UserInfoActivity.this);
                        startActivity(intent);
                    }

                    @Override
                    public void onNotSignedUp() {
                        makeToast("탈퇴 처리 도중 오류가 발생했습니다.");
                        Intent intent = new Intent(getApplicationContext(), StartActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        ActivityCompat.finishAffinity(UserInfoActivity.this);
                        startActivity(intent);
                    }

                    @Override
                    public void onSuccess(Long result) {
                        makeToast("탈퇴처리했습니다. 이용해 주셔서 감사합니다.");
                        Intent intent = new Intent(getApplicationContext(), StartActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        ActivityCompat.finishAffinity(UserInfoActivity.this);
                        startActivity(intent);
                    }
                });
            } else if(FirebaseAuth.getInstance().getCurrentUser() != null) {
                makeToast("탈퇴처리했습니다. 이용해 주셔서 감사합니다.");
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getApplicationContext(), StartActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                ActivityCompat.finishAffinity(UserInfoActivity.this);
                startActivity(intent);
            }
        } else if(btnFlag == 2) {
            if (myData != null & otherData != null) {
                progressOn();

                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference reference = database.getReference();
                reference.child("chat").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            if(snapshot.getKey().contains(myData.userId) & snapshot.getKey().contains(otherData.userId)) {
                                roomId = snapshot.getKey();
                            }
                        }

                        if(roomId == null) {
                            roomId = myData.userId+","+otherData.userId;
                        }

                        progressOff();
                        Intent intent = new Intent(UserInfoActivity.this, ChattingActivity.class);
                        intent.putExtra("myData", myData); //ChatUserData
                        intent.putExtra("user", otherData); //ChatUserData
                        intent.putExtra("roomId", roomId);
                        startActivity(intent);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        if(roomId == null) {
                            roomId = myData.userId+","+otherData.userId;
                        }

                        progressOff();
                        Intent intent = new Intent(UserInfoActivity.this, ChattingActivity.class);
                        intent.putExtra("myData", myData); //ChatUserData
                        intent.putExtra("user", otherData); //ChatUserData
                        intent.putExtra("roomId", roomId);
                        startActivity(intent);
                    }
                });
            } else {
                makeToast("정보를 불러올 수 없습니다. 다시 시도해 주세요.");
                finish();
            }
        }
    }

    //로그아웃 버튼 클릭 시
    public void logout() {
        if (Session.getCurrentSession().isOpened()) {
            UserManagement.getInstance().requestLogout(new LogoutResponseCallback() {
                @Override
                public void onCompleteLogout() {

                }
            });
            SharedPreferences sf = getSharedPreferences("userType",MODE_PRIVATE);
            SharedPreferences.Editor editor = sf.edit();
            editor.putBoolean("isTalkActOpened",false);
            editor.apply();

            makeToast("로그아웃했습니다. 이용해 주셔서 감사합니다.");
            Intent intent = new Intent(getApplicationContext(), StartActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
            ActivityCompat.finishAffinity(UserInfoActivity.this);
            startActivity(intent);
        } else if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            FirebaseAuth.getInstance().signOut();

            SharedPreferences sf = getSharedPreferences("userType",MODE_PRIVATE);
            SharedPreferences.Editor editor = sf.edit();
            editor.putBoolean("isTalkActOpened",false);
            editor.apply();

            makeToast("로그아웃했습니다. 이용해 주셔서 감사합니다.");
            Intent intent = new Intent(getApplicationContext(), StartActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
            ActivityCompat.finishAffinity(this);
            startActivity(intent);
        }
    }

    @Override
    protected int getLayoutId() { return R.layout.activity_user_info; }
}
