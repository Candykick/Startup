package com.candykick.startup.view.adviserAct;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.candykick.startup.R;
import com.candykick.startup.view.base.BaseActivity;
import com.candykick.startup.databinding.ActivityAdviserBoardNewBinding;
import com.google.android.gms.tasks.OnCompleteListener;
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

public class AdviserBoardNewActivity extends BaseActivity<ActivityAdviserBoardNewBinding> {

    ArrayList<AdviserBoardNewData> boardData = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding.setActivity(this);

        binding.toolbar.tvToolBar.setText("조언 모음");
        binding.toolbar.btnToolBarLeft.setOnClickListener(v -> onBackPressed());
        /*binding.toolbar.btnToolBarRight.setImageResource(R.drawable.ic_account_circle_black_24dp);
        binding.toolbar.btnToolBarRight.setOnClickListener(v -> {
            Intent intent = new Intent(AdviserBoardNewActivity.this, MyQuestionActivity.class);
            startActivity(intent);
        });*/
    }

    @Override
    protected void onResume() {
        super.onResume();

        //TabLayout.Tab tab = binding.tlAdvBoard.getTabAt(0);
        //tab.select();
        setAdvBoardList();
    }

    private void setAdvBoardList() {
        progressOn();

        boardData.clear();

        FirebaseFirestore.getInstance().collection("Adviser").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    if (!task.getResult().isEmpty()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            AdviserBoardNewData data = new AdviserBoardNewData();
                            data.postId = document.getId();
                            data.postTitle = document.getData().get("title").toString();
                            data.postContents = document.getData().get("contents").toString();
                            data.postImageUrl = document.getData().get("image").toString();
                            data.postCategory = document.getData().get("category").toString();
                            data.postTime = document.getData().get("time").toString();
                            data.userId = document.getData().get("userid").toString();
                            data.postAnswer = ((Long)document.getData().get("answernum")).intValue();;
                            data.postHit = ((Long)document.getData().get("hit")).intValue();

                            boardData.add(data);
                        }

                        AdviserBoardNewAdapter adapter = new AdviserBoardNewAdapter(AdviserBoardNewActivity.this, boardData);
                        binding.lvABN.setAdapter(adapter);

                        binding.lvABN.setOnItemClickListener((parentView, view, position, id) -> {
                            Intent intent = new Intent(AdviserBoardNewActivity.this, AdviserPostActivity.class);
                            intent.putExtra("id",boardData.get(position).postId);
                            startActivity(intent);
                        });

                        /*binding.tlAdvBoard.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                            @Override
                            public void onTabSelected(TabLayout.Tab tab) {
                                switch (tab.getPosition()) {
                                    case 0: //전체
                                        setAdvBoardList();
                                        break;
                                    case 1: //내 글
                                        setMyBoardList();
                                        break;
                                }
                            }

                            @Override
                            public void onTabUnselected(TabLayout.Tab tab) {}

                            @Override
                            public void onTabReselected(TabLayout.Tab tab) {}
                        });*/

                        progressOff();
                    } else {
                        progressOff();
                        makeToastLong("게시글이 없습니다.");
                    }
                } else {
                    progressOff();
                    makeToastLong("정보를 불러오는 도중 오류가 발생했습니다: "+task.getException());
                    finish();
                }
            }
        });
    }

    private void setMyBoardList() {
        progressOn();

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
                    String myId = result.getId()+"";

                    FirebaseFirestore.getInstance().collection("Adviser").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                boardData.clear();

                                if (!task.getResult().isEmpty()) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        if(myId.equals(document.getData().get("userid").toString())) {
                                            AdviserBoardNewData data = new AdviserBoardNewData();
                                            data.postId = document.getId();
                                            data.postTitle = document.getData().get("title").toString();
                                            data.postContents = document.getData().get("contents").toString();
                                            data.postImageUrl = document.getData().get("image").toString();
                                            data.postCategory = document.getData().get("category").toString();
                                            data.postTime = document.getData().get("time").toString();
                                            data.userId = document.getData().get("userid").toString();
                                            data.postAnswer = ((Long)document.getData().get("answernum")).intValue();;
                                            data.postHit = ((Long)document.getData().get("hit")).intValue();

                                            boardData.add(data);
                                        }
                                    }

                                    if(boardData.size() == 0) {
                                        AdviserBoardNewAdapter adapter = new AdviserBoardNewAdapter(AdviserBoardNewActivity.this, new ArrayList<AdviserBoardNewData>());
                                        binding.lvABN.setAdapter(adapter);
                                        makeToast("내가 올린 질문이 없습니다.");
                                    } else {
                                        AdviserBoardNewAdapter adapter = new AdviserBoardNewAdapter(AdviserBoardNewActivity.this, boardData);
                                        binding.lvABN.setAdapter(adapter);

                                        binding.lvABN.setOnItemClickListener((parentView, view, position, id) -> {
                                            Intent intent = new Intent(AdviserBoardNewActivity.this, AdviserPostActivity.class);
                                            intent.putExtra("id",boardData.get(position).postId);
                                            startActivity(intent);
                                        });
                                    }

                                    /*binding.tlAdvBoard.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                                        @Override
                                        public void onTabSelected(TabLayout.Tab tab) {
                                            switch (tab.getPosition()) {
                                                case 0: //전체
                                                    setAdvBoardList();
                                                    break;
                                                case 1: //내 글
                                                    setMyBoardList();
                                                    break;
                                            }
                                        }

                                        @Override
                                        public void onTabUnselected(TabLayout.Tab tab) {}

                                        @Override
                                        public void onTabReselected(TabLayout.Tab tab) {}
                                    });*/

                                    progressOff();
                                } else {
                                    progressOff();
                                    makeToastLong("게시글이 없습니다.");
                                }
                            } else {
                                progressOff();
                                makeToastLong("정보를 불러오는 도중 오류가 발생했습니다: "+task.getException());
                                finish();
                            }
                        }
                    });
                }
            });
        } else if(FirebaseAuth.getInstance().getCurrentUser() != null) {
            String myId = FirebaseAuth.getInstance().getCurrentUser().getUid();

            FirebaseFirestore.getInstance().collection("Adviser").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        boardData.clear();

                        if (!task.getResult().isEmpty()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                if(myId.equals(document.getData().get("userid").toString())) {
                                    AdviserBoardNewData data = new AdviserBoardNewData();
                                    data.postId = document.getId();
                                    data.postTitle = document.getData().get("title").toString();
                                    data.postContents = document.getData().get("contents").toString();
                                    data.postImageUrl = document.getData().get("image").toString();
                                    data.postCategory = document.getData().get("category").toString();
                                    data.postTime = document.getData().get("time").toString();
                                    data.userId = document.getData().get("userid").toString();
                                    data.postAnswer = ((Long)document.getData().get("answernum")).intValue();;
                                    data.postHit = ((Long)document.getData().get("hit")).intValue();

                                    boardData.add(data);
                                }
                            }

                            if(boardData.size() == 0) {
                                makeToast("내가 올린 질문이 없습니다.");
                            } else {
                                AdviserBoardNewAdapter adapter = new AdviserBoardNewAdapter(AdviserBoardNewActivity.this, boardData);
                                binding.lvABN.setAdapter(adapter);

                                binding.lvABN.setOnItemClickListener((parentView, view, position, id) -> {
                                    Intent intent = new Intent(AdviserBoardNewActivity.this, AdviserPostActivity.class);
                                    intent.putExtra("id",boardData.get(position).postId);
                                    startActivity(intent);
                                });
                            }

                            /*binding.tlAdvBoard.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                                @Override
                                public void onTabSelected(TabLayout.Tab tab) {
                                    switch (tab.getPosition()) {
                                        case 0: //전체
                                            setAdvBoardList();
                                            break;
                                        case 1: //내 글
                                            setMyBoardList();
                                            break;
                                    }
                                }

                                @Override
                                public void onTabUnselected(TabLayout.Tab tab) {}

                                @Override
                                public void onTabReselected(TabLayout.Tab tab) {}
                            });*/

                            progressOff();
                        } else {
                            progressOff();
                            makeToastLong("게시글이 없습니다.");
                        }
                    } else {
                        progressOff();
                        makeToastLong("정보를 불러오는 도중 오류가 발생했습니다: "+task.getException());
                        finish();
                    }
                }
            });
        }
    }

    public void addQuestion() {
        Intent intent = new Intent(AdviserBoardNewActivity.this, AdviserUploadActivity.class);
        startActivity(intent);
    }

    @Override
    protected int getLayoutId() {return R.layout.activity_adviser_board_new;}
}
