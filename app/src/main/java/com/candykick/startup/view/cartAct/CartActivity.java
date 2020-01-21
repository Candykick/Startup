package com.candykick.startup.view.cartAct;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.CompoundButton;

import com.candykick.startup.R;
import com.candykick.startup.view.base.BaseActivity;
import com.candykick.startup.databinding.ActivityCartBinding;
import com.candykick.startup.view.loginAccount.StartActivity;
import com.candykick.startup.view.userInfo.UserInfoStartActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.kakao.auth.Session;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.MeV2ResponseCallback;
import com.kakao.usermgmt.response.MeV2Response;

import java.util.ArrayList;

public class CartActivity extends BaseActivity<ActivityCartBinding> {

    FirebaseFirestore db;
    ArrayList<CartMainData> arrayRed = new ArrayList<>();
    ArrayList<CartMainData> arrayOrange = new ArrayList<>();
    ArrayList<CartMainData> arrayYellow = new ArrayList<>();
    ArrayList<CartMainData> arrayGreen = new ArrayList<>();
    ArrayList<CartMainData> arrayBlue = new ArrayList<>();
    ArrayList<CartMainData> arrayNavy = new ArrayList<>();
    ArrayList<CartMainData> arrayInv = new ArrayList<>();
    ArrayList<CartMainData> newList = new ArrayList<>();
    CartImCeoData myCeoData; CartImInvData myInvData; int myUserType;
    int ceoSelector = 8; boolean isInvAdded = true;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding.setActivity(this);

        db = FirebaseFirestore.getInstance();

        binding.toolbar.tvToolBar.setText("내 카트");
        binding.toolbar.btnToolBarLeft.setOnClickListener(v -> onBackPressed());

        requestAllData();

        binding.cbCart.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    ceoSelector = 8;
                    isInvAdded = true;
                    binding.btnCartRed.setAlpha(1.0f);
                    binding.btnCartOrange.setAlpha(1.0f);
                    binding.btnCartYellow.setAlpha(1.0f);
                    binding.btnCartGreen.setAlpha(1.0f);
                    binding.btnCartBlue.setAlpha(1.0f);
                    binding.btnCartNavy.setAlpha(1.0f);
                    binding.btnCartPurple.setAlpha(1.0f);

                    newList = new ArrayList<>(arrayRed); newList.addAll(arrayOrange);
                    newList.addAll(arrayYellow); newList.addAll(arrayGreen); newList.addAll(arrayBlue);
                    newList.addAll(arrayNavy); newList.addAll(arrayInv);
                    CartListAdapter adapter = new CartListAdapter(CartActivity.this, newList);
                    binding.lvCart.setAdapter(adapter);

                    binding.lvCart.setOnItemClickListener((parentView, view, position, id) -> {
                        Intent intent = new Intent(CartActivity.this, UserInfoStartActivity.class);
                        intent.putExtra("ismyData",false);
                        intent.putExtra("userId",newList.get(position).userId);
                        startActivity(intent);
                    });
                } else {
                    ceoSelector = 0;
                    isInvAdded = false;
                    binding.btnCartRed.setAlpha(0.3f);
                    binding.btnCartOrange.setAlpha(0.3f);
                    binding.btnCartYellow.setAlpha(0.3f);
                    binding.btnCartGreen.setAlpha(0.3f);
                    binding.btnCartBlue.setAlpha(0.3f);
                    binding.btnCartNavy.setAlpha(0.3f);
                    binding.btnCartPurple.setAlpha(0.3f);

                    newList = new ArrayList<>();
                    CartListAdapter adapter = new CartListAdapter(CartActivity.this, newList);
                    binding.lvCart.setAdapter(adapter);
                    binding.lvCart.setOnItemClickListener((parentView, view, position, id) -> {});
                }
            }
        });
    }

    public void btnRed() {
        binding.btnCartRed.setAlpha(1.0f);
        binding.btnCartOrange.setAlpha(0.3f);
        binding.btnCartYellow.setAlpha(0.3f);
        binding.btnCartGreen.setAlpha(0.3f);
        binding.btnCartBlue.setAlpha(0.3f);
        binding.btnCartNavy.setAlpha(0.3f);

        binding.cbCart.setChecked(false);

        makeList(6);
    }
    public void btnOrange() {
        binding.btnCartRed.setAlpha(0.3f);
        binding.btnCartOrange.setAlpha(1.0f);
        binding.btnCartYellow.setAlpha(0.3f);
        binding.btnCartGreen.setAlpha(0.3f);
        binding.btnCartBlue.setAlpha(0.3f);
        binding.btnCartNavy.setAlpha(0.3f);

        binding.cbCart.setChecked(false);

        makeList(5);
    }
    public void btnYellow() {
        binding.btnCartRed.setAlpha(0.3f);
        binding.btnCartOrange.setAlpha(0.3f);
        binding.btnCartYellow.setAlpha(1.0f);
        binding.btnCartGreen.setAlpha(0.3f);
        binding.btnCartBlue.setAlpha(0.3f);
        binding.btnCartNavy.setAlpha(0.3f);

        binding.cbCart.setChecked(false);

        makeList(4);
    }
    public void btnGreen() {
        binding.btnCartRed.setAlpha(0.3f);
        binding.btnCartOrange.setAlpha(0.3f);
        binding.btnCartYellow.setAlpha(0.3f);
        binding.btnCartGreen.setAlpha(1.0f);
        binding.btnCartBlue.setAlpha(0.3f);
        binding.btnCartNavy.setAlpha(0.3f);

        binding.cbCart.setChecked(false);

        makeList(3);
    }
    public void btnBlue() {
        binding.btnCartRed.setAlpha(0.3f);
        binding.btnCartOrange.setAlpha(0.3f);
        binding.btnCartYellow.setAlpha(0.3f);
        binding.btnCartGreen.setAlpha(0.3f);
        binding.btnCartBlue.setAlpha(1.0f);
        binding.btnCartNavy.setAlpha(0.3f);

        binding.cbCart.setChecked(false);

        makeList(2);
    }
    public void btnNavy() {
        binding.btnCartRed.setAlpha(0.3f);
        binding.btnCartOrange.setAlpha(0.3f);
        binding.btnCartYellow.setAlpha(0.3f);
        binding.btnCartGreen.setAlpha(0.3f);
        binding.btnCartBlue.setAlpha(0.3f);
        binding.btnCartNavy.setAlpha(1.0f);

        binding.cbCart.setChecked(false);

        makeList(1);
    }
    public void btnPurple() {
        if(myUserType == 0) {
            if (!binding.cbCart.isChecked()) {
                if (binding.btnCartPurple.getAlpha() == 1.0f) {
                    binding.btnCartPurple.setAlpha(0.3f);
                    addInvList(false);
                } else {
                    binding.btnCartPurple.setAlpha(1.0f);
                    addInvList(true);
                }
            } else {
                btnRed();
            }
        }
    }

    //전체 정보 다운로드. 일단 내 정보 받고 -> 색깔별 정보 받고.
    public void requestAllData() {
        progressOn();
            if (Session.getCurrentSession().isOpened()) {
                UserManagement.getInstance().me(new MeV2ResponseCallback() {
                    @Override
                    public void onSessionClosed(ErrorResult errorResult) {
                        progressOff();
                        makeToast("정보를 불러오지 못했습니다. 다시 시도해 주세요: " + errorResult.toString());
                        Intent intent = new Intent(CartActivity.this, StartActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    }

                    @Override
                    public void onSuccess(MeV2Response result) {
                        FirebaseFirestore db = FirebaseFirestore.getInstance();
                        DocumentReference docRef = db.collection("UserCeo").document("" + result.getId());
                        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    DocumentSnapshot document = task.getResult();
                                    if (document.exists()) {
                                        if ((long) document.get("usertype") == 0) {
                                            myCeoData = new CartImCeoData((String) document.get("businesstype"), (String) document.get("role"), (String) document.get("wantcapital"), (String) document.get("age"), (long) document.get("gender"), (boolean) document.get("isoffice"), (String) document.get("investtype"), (String) document.get("mycapital"), document.getId());
                                            myInvData = null;
                                            myUserType = 0;
                                            requestOtherData();
                                        } else if ((long) document.get("usertype") == 1) {
                                            myCeoData = null;
                                            myInvData = new CartImInvData((String) document.get("wantinvjob"), (String) document.get("wantinvcapital"), (String) document.get("wantinvtype"),document.getId());
                                            myUserType = 1;
                                            requestOtherData();
                                        } else {
                                            progressOff();
                                            makeToast("해당 유형의 계정은 카트 서비스가 제공되지 않습니다.");
                                            finish();
                                        }
                                    } else {
                                        progressOff();
                                        makeToast("유저 정보를 불러오지 못했습니다. 해당하는 유저의 정보가 없습니다.");
                                        Intent intent = new Intent(CartActivity.this, StartActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                        finish();
                                    }
                                } else {
                                    progressOff();
                                    makeToastLong("유저 정보를 불러오는 도중 오류가 발생했습니다: " + task.getException());
                                    Intent intent = new Intent(CartActivity.this, StartActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                    finish();
                                }
                            }
                        });
                    }
                });
            } else if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                FirebaseFirestore db = FirebaseFirestore.getInstance();
                DocumentReference docRef = db.collection("UserCeo").document(user.getUid());
                docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();

                            if (document.exists()) {
                                if ((long) document.get("usertype") == 0) {
                                    myCeoData = new CartImCeoData((String) document.get("businesstype"), (String) document.get("role"), (String) document.get("wantcapital"), (String) document.get("age"), (long) document.get("gender"), (boolean) document.get("isoffice"), (String) document.get("investtype"), (String) document.get("mycapital"), document.getId());
                                    myInvData = null;
                                    myUserType = 0;
                                    requestOtherData();
                                } else if ((long) document.get("usertype") == 1) {
                                    myCeoData = null;
                                    myInvData = new CartImInvData((String) document.get("wantinvjob"), (String) document.get("wantinvcapital"), (String) document.get("wantinvtype"),document.getId());
                                    myUserType = 1;
                                    requestOtherData();
                                } else {
                                    progressOff();
                                    makeToast("해당 유형의 계정은 카트 서비스가 제공되지 않습니다.");
                                    finish();
                                }
                            } else {
                                progressOff();
                                makeToast("유저 정보를 불러오지 못했습니다. 해당하는 유저의 정보가 없습니다.");
                                Intent intent = new Intent(CartActivity.this, StartActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                finish();
                            }
                        } else {
                            progressOff();
                            makeToastLong("유저 정보를 불러오는 도중 오류가 발생했습니다: " + task.getException());
                            Intent intent = new Intent(CartActivity.this, StartActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            finish();
                        }
                    }
                });
            } else {
                progressOff();
                //로그아웃 수행하고 로그인 화면으로 돌아간 뒤 에러 메세지 띄움.
                makeToast("유저가 로그인되지 않은 상태입니다. 다시 로그인해주세요.");
                Intent intent = new Intent(CartActivity.this, StartActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
    }

    //색깔별 정보 받음
    public void requestOtherData() {
        db.collection("UserCeo").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    if(myUserType == 0) {
                        binding.cbCart.setVisibility(View.VISIBLE);
                        binding.btnCartNavy.setVisibility(View.VISIBLE);
                        binding.btnCartBlue.setVisibility(View.VISIBLE);
                        binding.btnCartGreen.setVisibility(View.VISIBLE);
                        binding.btnCartYellow.setVisibility(View.VISIBLE);
                        binding.btnCartOrange.setVisibility(View.VISIBLE);
                        binding.btnCartRed.setVisibility(View.VISIBLE);

                        for (QueryDocumentSnapshot document : task.getResult()) {
                            if (!myCeoData.myid.equals(document.getId())) {
                                if ((long) (document.getData().get("usertype")) == 0) {
                                    checkCeoMatch(document);
                                } else if ((long) (document.getData().get("usertype")) == 1) {
                                    checkCeotoInvMatch(document);
                                }
                            }
                        }
                    } else if(myUserType == 1) {
                        binding.cbCart.setVisibility(View.GONE);
                        binding.btnCartNavy.setVisibility(View.GONE);
                        binding.btnCartBlue.setVisibility(View.GONE);
                        binding.btnCartGreen.setVisibility(View.GONE);
                        binding.btnCartYellow.setVisibility(View.GONE);
                        binding.btnCartOrange.setVisibility(View.GONE);
                        binding.btnCartRed.setVisibility(View.GONE);

                        for (QueryDocumentSnapshot document : task.getResult()) {
                            if (!myInvData.myid.equals(document.getId())) {
                                if ((long) (document.getData().get("usertype")) == 0) {
                                    checkInvtoCeoMatch(document);
                                }
                            }
                        }
                    }
                }

                newList = new ArrayList<>(arrayRed); newList.addAll(arrayOrange);
                newList.addAll(arrayYellow); newList.addAll(arrayGreen); newList.addAll(arrayBlue);
                newList.addAll(arrayNavy); newList.addAll(arrayInv);
                CartListAdapter adapter = new CartListAdapter(CartActivity.this, newList);
                binding.lvCart.setAdapter(adapter);

                binding.lvCart.setOnItemClickListener((parentView, view, position, id) -> {
                    Intent intent = new Intent(CartActivity.this, UserInfoStartActivity.class);
                    intent.putExtra("ismyData",false);
                    intent.putExtra("userId",newList.get(position).userId);
                    startActivity(intent);
                });

                if(myUserType == 0) {
                    binding.btnCartRed.setText(Integer.toString(arrayRed.size()));
                    binding.btnCartOrange.setText(Integer.toString(arrayOrange.size()));
                    binding.btnCartYellow.setText(Integer.toString(arrayYellow.size()));
                    binding.btnCartGreen.setText(Integer.toString(arrayGreen.size()));
                    binding.btnCartBlue.setText(Integer.toString(arrayBlue.size()));
                    binding.btnCartNavy.setText(Integer.toString(arrayNavy.size()));
                    binding.btnCartPurple.setText(Integer.toString(arrayInv.size()));
                } else if(myUserType == 1) {
                    binding.btnCartPurple.setText(Integer.toString(arrayNavy.size()+arrayBlue.size()+arrayGreen.size()));
                }

                progressOff();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressOff();
                //로그아웃 수행하고 로그인 화면으로 돌아간 뒤 에러 메세지 띄움.
                makeToast("정보를 불러올 수 없습니다. 다시 시도해 주세요: "+e.getMessage());
                finish();
            }
        });
    }

    //창업희망자 매칭
    public void makeList(int mustmatch) {
        newList = new ArrayList<>();

        switch (mustmatch) {
            case 1:
                ceoSelector = 1;
                newList.addAll(arrayNavy);
                break;
            case 2:
                ceoSelector = 2;
                newList.addAll(arrayBlue);
                break;
            case 3:
                ceoSelector = 3;
                newList.addAll(arrayGreen);
                break;
            case 4:
                ceoSelector = 4;
                newList.addAll(arrayYellow);
                break;
            case 5:
                ceoSelector = 5;
                newList.addAll(arrayOrange);
                break;
            case 6:
                ceoSelector = 6;
                newList.addAll(arrayRed);
                break;
        }

        if(isInvAdded) {
            newList.addAll(arrayInv);
        }

        CartListAdapter adapter = new CartListAdapter(CartActivity.this, newList);
        binding.lvCart.setAdapter(adapter);

        binding.lvCart.setOnItemClickListener((parentView, view, position, id) -> {
            Intent intent = new Intent(CartActivity.this, UserInfoStartActivity.class);
            intent.putExtra("ismyData",false);
            intent.putExtra("userId",newList.get(position).userId);
            startActivity(intent);
        });
    }

    //창업희망자 -> 투자자 매칭
    private void addInvList(boolean isadd) {
        isInvAdded = isadd;
        if(isadd) {
            CartListAdapter adapter;
            newList = new ArrayList<>();
            switch (ceoSelector) {
                case 1:
                    newList.addAll(arrayNavy); newList.addAll(arrayInv);
                    adapter = new CartListAdapter(CartActivity.this, newList);
                    binding.lvCart.setAdapter(adapter);

                    binding.lvCart.setOnItemClickListener((parentView, view, position, id) -> {
                        Intent intent = new Intent(CartActivity.this, UserInfoStartActivity.class);
                        intent.putExtra("ismyData",false);
                        intent.putExtra("userId",newList.get(position).userId);
                        startActivity(intent);
                    });
                    break;
                case 2:
                    newList.addAll(arrayBlue); newList.addAll(arrayInv);
                    adapter = new CartListAdapter(CartActivity.this, newList);
                    binding.lvCart.setAdapter(adapter);

                    binding.lvCart.setOnItemClickListener((parentView, view, position, id) -> {
                        Intent intent = new Intent(CartActivity.this, UserInfoStartActivity.class);
                        intent.putExtra("ismyData",false);
                        intent.putExtra("userId",newList.get(position).userId);
                        startActivity(intent);
                    });
                    break;
                case 3:
                    newList.addAll(arrayGreen); newList.addAll(arrayInv);
                    adapter = new CartListAdapter(CartActivity.this, newList);
                    binding.lvCart.setAdapter(adapter);

                    binding.lvCart.setOnItemClickListener((parentView, view, position, id) -> {
                        Intent intent = new Intent(CartActivity.this, UserInfoStartActivity.class);
                        intent.putExtra("ismyData",false);
                        intent.putExtra("userId",newList.get(position).userId);
                        startActivity(intent);
                    });
                    break;
                case 4:
                    newList.addAll(arrayYellow); newList.addAll(arrayInv);
                    adapter = new CartListAdapter(CartActivity.this, newList);
                    binding.lvCart.setAdapter(adapter);

                    binding.lvCart.setOnItemClickListener((parentView, view, position, id) -> {
                        Intent intent = new Intent(CartActivity.this, UserInfoStartActivity.class);
                        intent.putExtra("ismyData",false);
                        intent.putExtra("userId",newList.get(position).userId);
                        startActivity(intent);
                    });
                    break;
                case 5:
                    newList.addAll(arrayOrange); newList.addAll(arrayInv);
                    adapter = new CartListAdapter(CartActivity.this, newList);
                    binding.lvCart.setAdapter(adapter);

                    binding.lvCart.setOnItemClickListener((parentView, view, position, id) -> {
                        Intent intent = new Intent(CartActivity.this, UserInfoStartActivity.class);
                        intent.putExtra("ismyData",false);
                        intent.putExtra("userId",newList.get(position).userId);
                        startActivity(intent);
                    });
                    break;
                case 6:
                    newList.addAll(arrayRed); newList.addAll(arrayInv);
                    adapter = new CartListAdapter(CartActivity.this, newList);
                    binding.lvCart.setAdapter(adapter);

                    binding.lvCart.setOnItemClickListener((parentView, view, position, id) -> {
                        Intent intent = new Intent(CartActivity.this, UserInfoStartActivity.class);
                        intent.putExtra("ismyData",false);
                        intent.putExtra("userId",newList.get(position).userId);
                        startActivity(intent);
                    });
                    break;
            }
        } else {
            CartListAdapter adapter;
            switch (ceoSelector) {
                case 1:
                    adapter = new CartListAdapter(CartActivity.this, arrayNavy);
                    binding.lvCart.setAdapter(adapter);

                    binding.lvCart.setOnItemClickListener((parentView, view, position, id) -> {
                        Intent intent = new Intent(CartActivity.this, UserInfoStartActivity.class);
                        intent.putExtra("ismyData",false);
                        intent.putExtra("userId",arrayNavy.get(position).userId);
                        startActivity(intent);
                    });
                    break;
                case 2:
                    adapter = new CartListAdapter(CartActivity.this, arrayBlue);
                    binding.lvCart.setAdapter(adapter);

                    binding.lvCart.setOnItemClickListener((parentView, view, position, id) -> {
                        Intent intent = new Intent(CartActivity.this, UserInfoStartActivity.class);
                        intent.putExtra("ismyData",false);
                        intent.putExtra("userId",arrayBlue.get(position).userId);
                        startActivity(intent);
                    });
                    break;
                case 3:
                    adapter = new CartListAdapter(CartActivity.this, arrayGreen);
                    binding.lvCart.setAdapter(adapter);

                    binding.lvCart.setOnItemClickListener((parentView, view, position, id) -> {
                        Intent intent = new Intent(CartActivity.this, UserInfoStartActivity.class);
                        intent.putExtra("ismyData",false);
                        intent.putExtra("userId",arrayGreen.get(position).userId);
                        startActivity(intent);
                    });
                    break;
                case 4:
                    adapter = new CartListAdapter(CartActivity.this, arrayYellow);
                    binding.lvCart.setAdapter(adapter);

                    binding.lvCart.setOnItemClickListener((parentView, view, position, id) -> {
                        Intent intent = new Intent(CartActivity.this, UserInfoStartActivity.class);
                        intent.putExtra("ismyData",false);
                        intent.putExtra("userId",arrayYellow.get(position).userId);
                        startActivity(intent);
                    });
                    break;
                case 5:
                    adapter = new CartListAdapter(CartActivity.this, arrayOrange);
                    binding.lvCart.setAdapter(adapter);

                    binding.lvCart.setOnItemClickListener((parentView, view, position, id) -> {
                        Intent intent = new Intent(CartActivity.this, UserInfoStartActivity.class);
                        intent.putExtra("ismyData",false);
                        intent.putExtra("userId",arrayOrange.get(position).userId);
                        startActivity(intent);
                    });
                    break;
                case 6:
                    adapter = new CartListAdapter(CartActivity.this, arrayRed);
                    binding.lvCart.setAdapter(adapter);

                    binding.lvCart.setOnItemClickListener((parentView, view, position, id) -> {
                        Intent intent = new Intent(CartActivity.this, UserInfoStartActivity.class);
                        intent.putExtra("ismyData",false);
                        intent.putExtra("userId",arrayRed.get(position).userId);
                        startActivity(intent);
                    });
                    break;
            }
        }
    }

    //창업희망자 간 정보 일치 여부 확인(return 문 활용 위한 것)
    //몇 개가 매치되느냐를 파악해서 각 ArrayList에 넣는다.
    public void checkCeoMatch(QueryDocumentSnapshot document) {
        //성별, 사업장, 연령대, 자본, 역할, 업종 순으로 검사.
        int datamatch = 0;

        if((long)document.getData().get("gender") == myCeoData.gender) { //성별
            datamatch += 1;
        }
        if((boolean)document.getData().get("isoffice") == myCeoData.isoffice) { //사업장
            datamatch += 1;
        }
        if(document.getData().get("age").toString().equals(myCeoData.age)) { //연령대
            datamatch += 1;
        }

        if(document.getData().get("mycapital").toString().equals(myCeoData.mycapital)) { //자본
            datamatch += 1;
        }

        //역할, 업종
        String[] role = document.getData().get("role").toString().split(", ");
        String[] businesstype = document.getData().get("businesstype").toString().split(", ");
        for(String roletmp : role) {
            for(String myrole : myCeoData.role) {
                if (roletmp.equals(myrole)) {
                    datamatch += 1;
                }
            }
        }
        for(String typetmp : businesstype) {
            for(String mytype : myCeoData.businesstype) {
                if(typetmp.equalsIgnoreCase(mytype)) {
                    datamatch += 1;
                }
            }
        }

        switch(datamatch) {
            case 1:
                addData(document, 1);
                break;
            case 2:
                addData(document, 2);
                break;
            case 3:
                addData(document, 3);
                break;
            case 4:
                addData(document, 4);
                break;
            case 5:
                addData(document, 5);
                break;
            case 6:
                addData(document, 6);
                break;
        }
    }

    //투자자 -> 창업희망자 간 매칭
    private void checkInvtoCeoMatch(QueryDocumentSnapshot document) {
        //희망투자금액, 희망투자유형, 업종 순으로 검색
        int datamatch = 0;

        if(document.getData().get("wantcapital").toString() == myInvData.wantinvcapital) { //희망투자금액
            datamatch += 1;
        }
        if(document.getData().get("investtype").toString() == myInvData.wantinvtype) { //희망투자유형
            datamatch += 1;
        }

        //업종
        String[] role = document.getData().get("businesstype").toString().split(", ");
        for(String roletmp : role) {
            for(String myrole : myInvData.wantinvjob) {
                if (roletmp.equals(myrole)) {
                    datamatch += 1;
                }
            }
        }

        switch(datamatch) {
            case 1:
                addData(document, 1);
                break;
            case 2:
                addData(document, 2);
                break;
            case 3:
                addData(document, 3);
                break;
        }
    }

    //창업희망자 -> 투자자 간 정보 일치 여부 확인
    //업종, 희망투자금액, 희망투자유형 중 2개 이상 일치 시 추가
    private void checkCeotoInvMatch(QueryDocumentSnapshot document) {
        int matchNumber = 0;

        if(document.getData().get("wantinvcapital").toString().equals(myCeoData.wantcapital)) { //희망투자금액
            matchNumber += 1;
        }

        if(document.getData().get("wantinvtype").toString().equals(myCeoData.investtype)) { //희망투자유형
            matchNumber += 1;
            if(matchNumber == 2) {
                addData(document, 7);
                return;
            }
        }

        //업종
        String[] role = document.getData().get("wantinvjob").toString().split(", ");
        for(String roletmp : role) {
            for(String myrole : myCeoData.role) {
                if (roletmp.equals(myrole)) {
                    matchNumber += 1;
                    if(matchNumber >= 2) {
                        addData(document, 7);
                        return;
                    }
                }
            }
        }
    }

    //메인 리스트에 정보 추가
    private void addData(QueryDocumentSnapshot document, int matchNum) {
        CartMainData data = new CartMainData();
        data.alarmType = matchNum;
        data.userId = document.getId();
        data.profile = document.getData().get("profileimage").toString();
        data.userName = document.getData().get("username").toString();
        if((long)document.getData().get("usertype") == 0) {
            String gender = ((long)document.getData().get("gender")==1)?"남성":"여성";
            data.userInfo1 = gender+", "+document.getData().get("age").toString();
            data.userInfo2 = document.getData().get("role").toString();
        } else if((long)document.getData().get("usertype") == 1) {
            data.userInfo1 = document.getData().get("wantinvcapital").toString()
                    +", "+document.getData().get("wantinvtype").toString();
            data.userInfo2 = "투자자";
        }

        switch (matchNum) {
            case 1:
                arrayNavy.add(data);
                break;
            case 2:
                arrayBlue.add(data);
                break;
            case 3:
                arrayGreen.add(data);
                break;
            case 4:
                arrayYellow.add(data);
                break;
            case 5:
                arrayOrange.add(data);
                break;
            case 6:
                arrayRed.add(data);
                break;
            case 7:
                arrayInv.add(data);
                break;
        }
    }

    @Override
    public int getLayoutId() {return R.layout.activity_cart;}
}
