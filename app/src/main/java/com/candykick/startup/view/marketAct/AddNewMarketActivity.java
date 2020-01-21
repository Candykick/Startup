package com.candykick.startup.view.marketAct;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;

import com.candykick.startup.R;
import com.candykick.startup.view.base.BaseActivity;
import com.candykick.startup.databinding.ActivityAddNewMarketBinding;
import com.candykick.startup.view.loginAccount.StartActivity;
import com.candykick.startup.view.marketAct.addNewMarket.AddMarketImageFragment;
import com.candykick.startup.view.marketAct.addNewMarket.AddNewMarketData;
import com.candykick.startup.view.marketAct.addNewMarket.BasicDataFragment;
import com.candykick.startup.view.marketAct.addNewMarket.DesFragment;
import com.candykick.startup.view.marketAct.addNewMarket.PriceDataFragment;
import com.candykick.startup.view.marketAct.addNewMarket.RequestforCustomerFragment;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.kakao.auth.Session;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.MeV2ResponseCallback;
import com.kakao.usermgmt.response.MeV2Response;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AddNewMarketActivity extends BaseActivity<ActivityAddNewMarketBinding> {

    String engCategory = ""; String userId = ""; String marketId;

    AddNewMarketData data;
    BasicDataFragment basicDataFragment; PriceDataFragment priceDataFragment; DesFragment desFragment;
    AddMarketImageFragment addMarketImageFragment; RequestforCustomerFragment requestforCustomerFragment;
    int fragmentFlag = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding.setActivity(this);

        String[] addNewMarketTab = getResources().getStringArray(R.array.strAddNewMarket);

        binding.toolbar.tvToolBar.setText(getResources().getString(R.string.strAddNewMarketActivityTitle));
        binding.toolbar.btnToolBarLeft.setOnClickListener(v -> onBackPressed());
        marketId = ""+System.currentTimeMillis();

        binding.tlAddNewMarket.setTabGravity(TabLayout.GRAVITY_FILL);
        binding.tlAddNewMarket.setTabMode(TabLayout.MODE_FIXED);
        for(int i=0;i<addNewMarketTab.length;i++) {
            binding.tlAddNewMarket.addTab(binding.tlAddNewMarket.newTab().setText(addNewMarketTab[i]));
        }
        binding.tlAddNewMarket.getTabAt(0).select();
        LinearLayout tabStrip = ((LinearLayout)binding.tlAddNewMarket.getChildAt(0));
        for(int i = 0; i < tabStrip.getChildCount(); i++) {
            tabStrip.getChildAt(i).setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    return true;
                }
            });
        }

        basicDataFragment = new BasicDataFragment();
        getSupportFragmentManager().beginTransaction().add(R.id.ctAddNewMarket, basicDataFragment).commit();

        data = new AddNewMarketData();
    }

    public void newmarketNext() {
        switch (fragmentFlag) {
            case 0:
                if(basicDataFragment.getMarketTitle().replace(" ","").length() == 0) {
                    makeToast("서비스 이름을 입력해주세요.");
                } else if(basicDataFragment.getMarketFirstCategory() == null) {
                    makeToast("상위 카테고리를 선택해주세요.");
                } else if(basicDataFragment.getMarketSecondCategory() == null) {
                    makeToast("하위 카테고리를 선택해주세요.");
                } else {
                    data.marketTitle = basicDataFragment.getMarketTitle();
                    data.marketFirstCategory = basicDataFragment.getMarketFirstCategory();
                    data.marketSecondCategory = basicDataFragment.getMarketSecondCategory();

                    priceDataFragment = new PriceDataFragment();
                    getSupportFragmentManager().beginTransaction().replace(R.id.ctAddNewMarket, priceDataFragment).commit();
                    fragmentFlag++;
                    binding.tlAddNewMarket.getTabAt(fragmentFlag).select();
                }
                break;
            case 1:
                if(priceDataFragment.getPackageInfo() != null) {
                    data.packageData = priceDataFragment.getPackageInfo();

                    desFragment = new DesFragment();
                    getSupportFragmentManager().beginTransaction().replace(R.id.ctAddNewMarket, desFragment).commit();
                    fragmentFlag++;
                    binding.tlAddNewMarket.getTabAt(fragmentFlag).select();
                }
                break;
            case 2:
                if(desFragment.getMarketDes().replace(" ","").length() != 0
                        && desFragment.getMarketModify().replace(" ","").length() != 0) {
                    data.marketDes = desFragment.getMarketDes();
                    data.marketModify = desFragment.getMarketModify();

                    addMarketImageFragment = new AddMarketImageFragment();
                    getSupportFragmentManager().beginTransaction().replace(R.id.ctAddNewMarket, addMarketImageFragment).commit();
                    fragmentFlag++;
                    binding.tlAddNewMarket.getTabAt(fragmentFlag).select();
                }
                break;
            case 3:
                if(addMarketImageFragment.getMainImage() != null) {
                    data.marketMainImage = addMarketImageFragment.getMainImage();

                    requestforCustomerFragment = new RequestforCustomerFragment();
                    getSupportFragmentManager().beginTransaction().replace(R.id.ctAddNewMarket, requestforCustomerFragment).commit();
                    fragmentFlag++;
                    binding.tlAddNewMarket.getTabAt(fragmentFlag).select();

                    binding.btnAddNewMarket.setText("등록하기");
                }
                break;
            case 4:
                data.marketRequest = requestforCustomerFragment.getRequest();
                uploadNewMarket();
                break;
        }
    }

    //DB에 마켓 정보 업로드
    public void uploadNewMarket() {
        progressOn();

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();

        if(data.marketFirstCategory.equals("디자인"))
            engCategory = "OSDesign";
        else if(data.marketFirstCategory.equals("IT, 프로그래밍"))
            engCategory = "OSProgramming";
        else if(data.marketFirstCategory.equals("콘텐츠 제작"))
            engCategory = "OSContents";
        else if(data.marketFirstCategory.equals("마케팅"))
            engCategory = "OSMarketting";
        else if(data.marketFirstCategory.equals("문서, 서류 및 인쇄"))
            engCategory = "OSDocument";
        else if(data.marketFirstCategory.equals("컨설팅, 상담"))
            engCategory = "OSConsulting";
        else if(data.marketFirstCategory.equals("제작, 커스텀"))
            engCategory = "OSMaker";
        else if(data.marketFirstCategory.equals("번역, 통역"))
            engCategory = "OSTranslate";
        else if(data.marketFirstCategory.equals("장소, 공간"))
            engCategory = "OSPlace";

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
                    userId = ""+result.getId();
                }
            });
        } else if(FirebaseAuth.getInstance().getCurrentUser() != null) {
            userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        } else {
            //로그아웃 수행하고 로그인 화면으로 돌아간 뒤 에러 메세지 띄움.
            progressOff();
            makeToast("유저가 로그인되지 않은 상태입니다. 다시 로그인해주세요.");
            Intent intent = new Intent(AddNewMarketActivity.this, StartActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }

        try {
            // Uri 스키마를 content:///에서 file:///로 변경한다.
            Bitmap bitmap = BitmapFactory.decodeFile(data.marketMainImage.getPath());

            StorageReference mountainImagesRef = storageRef.child(engCategory + "/" + System.currentTimeMillis() + ".jpg");
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 20, baos);
            byte[] data2 = baos.toByteArray();
            UploadTask uploadTask = mountainImagesRef.putBytes(data2);

            uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        progressOff();
                        makeToastLong("대표이미지를 등록하지 못했습니다." + task.getException().getMessage());
                        throw task.getException();
                    }

                    return mountainImagesRef.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        FirebaseFirestore db = FirebaseFirestore.getInstance();

                        Map<String, Object> marketData = new HashMap<>();
                        marketData.put("category", data.marketSecondCategory);
                        marketData.put("detail", data.marketDes);
                        marketData.put("grade", "0.0");
                        marketData.put("image", task.getResult().toString());
                        marketData.put("minprice",Long.parseLong(data.packageData.get(0).price));
                        marketData.put("modify", data.marketModify);
                        marketData.put("refund", "작성중입니다.");
                        marketData.put("reviewnum",0);
                        marketData.put("title", data.marketTitle);
                        marketData.put("request", data.marketRequest);
                        marketData.put("user",userId);

                        ArrayList<Map<String,String>> packageList = new ArrayList<>();
                        for(int i=0;i<data.packageData.size();i++) {
                            Map<String,String> mapData = new HashMap<>();
                            mapData.put("infotitle", data.packageData.get(i).infotitle);
                            mapData.put("infoexp", data.packageData.get(i).infoexp);
                            mapData.put("price", data.packageData.get(i).price);
                            mapData.put("infomodnum", data.packageData.get(i).infomodnum);
                            mapData.put("infoworkdate", data.packageData.get(i).infoworkdate);

                            packageList.add(mapData);
                        }
                        marketData.put("info",packageList);

                        db.collection(engCategory)
                                .document(marketId).set(marketData,SetOptions.merge())
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void avoid) {
                                        progressOff();
                                        Intent intent = new Intent(AddNewMarketActivity.this, MarketBoardActivity.class);
                                        intent.putExtra("type", data.marketFirstCategory);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                        finish();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        progressOff();
                                        makeToastLong("새로운 서비스를 등록하는 도중 오류가 발생했습니다. 오류: " + e.toString());

                                        db.collection(engCategory).document(marketId).delete();
                                    }
                                });

                        /*db.collection(engCategory)
                                .document(marketId).update("info", FieldValue.arrayUnion(Arrays.asList(list1)))
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {

                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        makeToastLong("새로운 서비스를 등록하는 도중 오류가 발생했습니다. 오류: " + e.toString());
                                    }
                                });*/
                    }
                }
            });
        } catch (Exception e) {
            progressOff();
            makeToastLong("새로운 서비스를 등록하는 도중 오류가 발생했습니다. 오류: " + e.toString());
        }
    }

    @Override
    protected int getLayoutId() { return R.layout.activity_add_new_market; }
}
