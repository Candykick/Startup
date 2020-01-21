package com.candykick.startup.view.adviserAct;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.candykick.startup.R;
import com.candykick.startup.view.base.BaseActivity;
import com.candykick.startup.databinding.ActivityAdviserPostBinding;
import com.candykick.startup.view.loginAccount.StartActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.kakao.auth.Session;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.MeV2ResponseCallback;
import com.kakao.usermgmt.response.MeV2Response;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AdviserPostActivity extends BaseActivity<ActivityAdviserPostBinding> {

    private static final int RESULT_LOGINERR = 3;

    String postId;
    ArrayList<AdviserAnswerData> arrayList = new ArrayList<>();
    ArrayList<String> questionImage = new ArrayList<>();
    String postTitle, postUser, postDate, postCategory, postQuestion;
    int postHit, answerNum;
    boolean ismypost = false, isimadviser = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding.setActivity(this);

        binding.toolbar.tvToolBar.setText("조언 내용");
        binding.toolbar.btnToolBarLeft.setOnClickListener(v -> onBackPressed());
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(Session.getCurrentSession().isOpened()) {
            UserManagement.getInstance().me(new MeV2ResponseCallback() {
                @Override
                public void onSessionClosed(ErrorResult errorResult) {
                    progressOff();
                    makeToast("정보를 불러오지 못했습니다. 다시 시도해 주세요: " + errorResult.toString());
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
                                    if ((long) document.get("usertype") == 2) {
                                        isimadviser = true;
                                    }

                                    settingPost(result.getId() + "");
                                }
                            }
                        }
                    });
                }
            });
        } else if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            DocumentReference docRef = db.collection("UserCeo").document(FirebaseAuth.getInstance().getCurrentUser().getUid());
            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();

                        if (document.exists()) {
                            if ((long) document.get("usertype") == 2) {
                                isimadviser = true;
                            }

                            settingPost(FirebaseAuth.getInstance().getCurrentUser().getUid());
                        }
                    }
                }
            });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_OK) {
            if(Session.getCurrentSession().isOpened()) {
                UserManagement.getInstance().me(new MeV2ResponseCallback() {
                    @Override
                    public void onSessionClosed(ErrorResult errorResult) {
                        progressOff();
                        makeToast("정보를 불러오지 못했습니다. 다시 시도해 주세요: " + errorResult.toString());
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
                                    arrayList.clear();
                                    questionImage.clear();

                                    DocumentSnapshot document = task.getResult();

                                    if (document.exists()) {
                                        if ((long) document.get("usertype") == 2) {
                                            isimadviser = true;
                                        }

                                        settingPost(result.getId() + "");
                                    }
                                }
                            }
                        });
                    }
                });
            } else if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                DocumentReference docRef = db.collection("UserCeo").document(FirebaseAuth.getInstance().getCurrentUser().getUid());
                docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            arrayList.clear();
                            questionImage.clear();

                            DocumentSnapshot document = task.getResult();

                            if (document.exists()) {
                                if ((long) document.get("usertype") == 2) {
                                    isimadviser = true;
                                }

                                settingPost(FirebaseAuth.getInstance().getCurrentUser().getUid());
                            }
                        }
                    }
                });
            }
        } else if(resultCode == RESULT_LOGINERR) {
            Intent intent = new Intent(AdviserPostActivity.this, StartActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }
    }

    private void settingPost(String userid) {
        postId = getIntent().getStringExtra("id");

        Log.d("Startup", "start");

        progressOn();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference advReference = db.collection("Adviser").document(postId);
        advReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    Log.d("Startup", "1");

                    DocumentSnapshot document = task.getResult();

                    postTitle = document.get("title").toString();
                    postUser = document.get("quser").toString();
                    postDate = document.get("time").toString();
                    postCategory = document.get("category").toString();
                    postQuestion = document.get("contents").toString();
                    postHit = ((Long)document.get("hit")).intValue();

                    if(userid.equals(document.get("userid").toString())) {
                        ismypost = true;
                    }

                    advReference.collection("image").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if(task.isSuccessful()) {
                                Log.d("Startup", "2");

                                if (!task.getResult().isEmpty()) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        questionImage.add(document.get("image").toString());
                                    }
                                }

                                advReference.collection("answer").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if(task.isSuccessful()) {
                                            Log.d("Startup", "3");

                                            if(!task.getResult().isEmpty()) {
                                                for (QueryDocumentSnapshot document : task.getResult()) {
                                                    AdviserAnswerData data = new AdviserAnswerData();

                                                    data.isBest = (boolean)document.get("isbest");
                                                    data.answerRecommendNum = ((Long)document.get("recommendnum")).intValue();
                                                    data.answerText = document.get("contents").toString();
                                                    data.answerUserId = document.get("userid").toString();

                                                    advReference.collection("answer").document(document.getId()).collection("image")
                                                            .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                            if(task.isSuccessful()) {
                                                                Log.d("Startup", "4");

                                                                if(!task.getResult().isEmpty()) {
                                                                    for(QueryDocumentSnapshot document : task.getResult()) {
                                                                        data.imageList.add(document.get("image").toString());
                                                                    }
                                                                }

                                                                if(arrayList.size() != 0) {
                                                                    Log.d("Startup", "5");

                                                                    FirebaseFirestore.getInstance().collection("UserCeo").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                                            int arrayIndex=0;

                                                                            if (task.isSuccessful()) {
                                                                                Log.d("Startup", "6");

                                                                                if(!task.getResult().isEmpty()) {
                                                                                    for(QueryDocumentSnapshot document : task.getResult()) {
                                                                                        if(arrayIndex != arrayList.size()) {
                                                                                            if (arrayList.get(arrayIndex).answerUserId.equals(document.getId())) {
                                                                                                arrayList.get(arrayIndex).answerBestNum = ((Long) document.get("userbestnum")).intValue();
                                                                                                arrayList.get(arrayIndex).answerUserInfo = document.get("userintro").toString();
                                                                                                arrayList.get(arrayIndex).answerUserName = document.get("username").toString();
                                                                                                arrayList.get(arrayIndex).answerUserProfileUrl = document.get("profileimage").toString();
                                                                                                arrayList.get(arrayIndex).answerRanking = ((Long) document.get("userranking")).intValue();

                                                                                                arrayIndex++;
                                                                                            }
                                                                                        }
                                                                                    }
                                                                                }

                                                                                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(AdviserPostActivity.this);
                                                                                binding.rvAdviserPost.setLayoutManager(linearLayoutManager);
                                                                                AdviserAnswerAdapter advAdapter = new AdviserAnswerAdapter(ismypost, isimadviser, postId, postTitle, postUser, postDate, postCategory, postQuestion, postHit+1, questionImage, AdviserPostActivity.this, arrayList, answerNum);
                                                                                binding.rvAdviserPost.setAdapter(advAdapter);

                                                                                // 조회수 수정하기
                                                                                Map<String, Object> hitData = new HashMap<>();
                                                                                hitData.put("hit",postHit+1);
                                                                                advReference.set(hitData, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                    @Override
                                                                                    public void onSuccess(Void aVoid) {
                                                                                        Log.d("Startup", "7");
                                                                                        progressOff();
                                                                                    }
                                                                                }).addOnFailureListener(new OnFailureListener() {
                                                                                    @Override
                                                                                    public void onFailure(@NonNull Exception e) {
                                                                                        progressOff();
                                                                                    }
                                                                                });
                                                                            }
                                                                        }
                                                                    });
                                                                } else {
                                                                    Log.d("Startup", "5");

                                                                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(AdviserPostActivity.this);
                                                                    binding.rvAdviserPost.setLayoutManager(linearLayoutManager);
                                                                    AdviserAnswerAdapter advAdapter = new AdviserAnswerAdapter(ismypost, isimadviser, postId, postTitle, postUser, postDate, postCategory, postQuestion, postHit+1, questionImage,AdviserPostActivity.this, arrayList, answerNum);
                                                                    binding.rvAdviserPost.setAdapter(advAdapter);

                                                                    // 조회수 수정하기
                                                                    Map<String, Object> hitData = new HashMap<>();
                                                                    hitData.put("hit",postHit+1);
                                                                    advReference.set(hitData, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                        @Override
                                                                        public void onSuccess(Void aVoid) {
                                                                            Log.d("Startup", "6");
                                                                            progressOff();
                                                                        }
                                                                    }).addOnFailureListener(new OnFailureListener() {
                                                                        @Override
                                                                        public void onFailure(@NonNull Exception e) {
                                                                            progressOff();
                                                                        }
                                                                    });
                                                                }
                                                            }
                                                        }
                                                    });

                                                    arrayList.add(data);
                                                }

                                                answerNum = task.getResult().size();
                                            } else {
                                                answerNum = 0;

                                                Log.d("Startup", "4");

                                                progressOff();
                                                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(AdviserPostActivity.this);
                                                binding.rvAdviserPost.setLayoutManager(linearLayoutManager);
                                                AdviserAnswerAdapter advAdapter = new AdviserAnswerAdapter(ismypost, isimadviser, postId, postTitle, postUser, postDate, postCategory, postQuestion, postHit+1, questionImage,AdviserPostActivity.this, arrayList, answerNum);
                                                binding.rvAdviserPost.setAdapter(advAdapter);
                                            }
                                        }
                                    }
                                });
                            }
                        }
                    });
                } else {
                    progressOff();
                    makeToastLong("해당 게시글이 삭제되었습니다.");
                    finish();
                }
            }
        });
    }

    public void modifyAdv() {
        PopupMenu popupMenu = new PopupMenu(AdviserPostActivity.this, binding.btnAdvBoardModify);
        MenuInflater inflater = popupMenu.getMenuInflater();
        Menu menu = popupMenu.getMenu();
        inflater.inflate(R.menu.postmenu, menu);
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.modifyPostMenu:
                        Intent intent = new Intent(AdviserPostActivity.this, AdviserUploadActivity.class);
                        intent.putExtra("isnew",false);
                        intent.putExtra("id",postId);
                        intent.putExtra("title",postTitle);
                        intent.putExtra("category",postCategory);
                        intent.putExtra("writer",postUser);
                        intent.putExtra("contents",postQuestion);
                        startActivity(intent);
                        break;
                    case R.id.deletePostMenu:
                        FirebaseFirestore db = FirebaseFirestore.getInstance();
                        //Task task =
                        db.collection("Adviser").document(postId).delete();
                        makeToast("게시글을 삭제했습니다.");
                        finish();
                        break;
                }

                return false;
            }
        });

        popupMenu.show();
    }

    @Override
    protected int getLayoutId() { return R.layout.activity_adviser_post; }
}