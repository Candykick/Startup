package com.candykick.startup.view.marketAct;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.view.View;

import com.candykick.startup.R;
import com.candykick.startup.engine.CdkResponseCallback;
import com.candykick.startup.engine.MarketEngine;
import com.candykick.startup.model.MarketListItemModel;
import com.candykick.startup.view.base.BaseActivity;
import com.candykick.startup.databinding.ActivityMarketBoardBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

/**
 가격 부분의 경우, 가격이 1개일 때에 대한 정보가 적용되어 있지 않다.
 */

public class MarketBoardActivity extends BaseActivity<ActivityMarketBoardBinding> {

    String strType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding.setActivity(this);

        strType = getIntent().getStringExtra("type");

        binding.toolbar.tvToolBar.setText(strType);
        binding.toolbar.btnToolBarLeft.setOnClickListener(v -> onBackPressed());

        binding.tlMarketBoard.setTabGravity(TabLayout.GRAVITY_CENTER);
        binding.tlMarketBoard.setTabMode(TabLayout.MODE_SCROLLABLE);

        setTab(strType);
    }

    //초기 탭 셋팅
    private void setTab(String type) {
        String[] designTab = getResources().getStringArray(R.array.strDesignTab);
        String[] programmingTab = getResources().getStringArray(R.array.strProgrammingTab);
        String[] contentsTab = getResources().getStringArray(R.array.strContentsTab);
        String[] markettingTab = getResources().getStringArray(R.array.strMarkettingTab);
        String[] documentTab = getResources().getStringArray(R.array.strDocumentTab);
        String[] consultingTab = getResources().getStringArray(R.array.strConsultingTab);
        String[] makerTab = getResources().getStringArray(R.array.strMakerTab);
        String[] translateTab = getResources().getStringArray(R.array.strTranslateTab);
        String[] placeTab = getResources().getStringArray(R.array.strPlaceTab);

        if(type.equals("디자인")) {
            for(int i=0;i<designTab.length;i++) {
                binding.tlMarketBoard.addTab(binding.tlMarketBoard.newTab().setText(designTab[i]));
            }
            setList("OSDesign");
        } else if(type.equals("IT, 프로그래밍")) {
            for(int i=0;i<programmingTab.length;i++) {
                binding.tlMarketBoard.addTab(binding.tlMarketBoard.newTab().setText(programmingTab[i]));
            }
            setList("OSProgramming");
        } else if(type.equals("콘텐츠 제작")) {
            for(int i=0;i<contentsTab.length;i++) {
                binding.tlMarketBoard.addTab(binding.tlMarketBoard.newTab().setText(contentsTab[i]));
            }
            setList("OSContents");
        } else if(type.equals("마케팅")) {
            for(int i=0;i<markettingTab.length;i++) {
                binding.tlMarketBoard.addTab(binding.tlMarketBoard.newTab().setText(markettingTab[i]));
            }
            setList("OSMarketting");
        } else if(type.equals("문서, 서류 및 인쇄")) {
            for(int i=0;i<documentTab.length;i++) {
                binding.tlMarketBoard.addTab(binding.tlMarketBoard.newTab().setText(documentTab[i]));
            }
            setList("OSDocument");
        } else if(type.equals("컨설팅, 상담")) {
            for(int i=0;i<consultingTab.length;i++) {
                binding.tlMarketBoard.addTab(binding.tlMarketBoard.newTab().setText(consultingTab[i]));
            }
            setList("OSConsulting");
        } else if(type.equals("제작, 커스텀")) {
            for(int i=0;i<makerTab.length;i++) {
                binding.tlMarketBoard.addTab(binding.tlMarketBoard.newTab().setText(makerTab[i]));
            }
            setList("OSMaker");
        } else if(type.equals("번역, 통역")) {
            for(int i=0;i<translateTab.length;i++) {
                binding.tlMarketBoard.addTab(binding.tlMarketBoard.newTab().setText(translateTab[i]));
            }
            setList("OSTranslate");
        } else if(type.equals("장소, 공간")) {
            for(int i=0;i<placeTab.length;i++) {
                binding.tlMarketBoard.addTab(binding.tlMarketBoard.newTab().setText(placeTab[i]));
            }
            setList("OSPlace");
        } else {
            makeToast(getResources().getString(R.string.toastErr));
            finish();
        }
    }

    //초기 리스트 셋팅. 데이터를 받아와서 처리하는 건 MarketEngine 담당.
    private void setList(String type) {
        progressOn();

        MarketEngine marketEngine = MarketEngine.getInstance();
        marketEngine.setMarketList(type, new CdkResponseCallback<ArrayList<MarketListItemModel>>() {
            @Override
            public void onDataLoaded(String result, ArrayList<MarketListItemModel> arrayList) {
                binding.llMarketNoList.setVisibility(View.GONE);
                binding.lvMarketBoard.setVisibility(View.VISIBLE);

                MarketBoardAdapter adapter = new MarketBoardAdapter(MarketBoardActivity.this, arrayList);
                binding.lvMarketBoard.setAdapter(adapter);

                binding.lvMarketBoard.setOnItemClickListener((parentView, view, position, id) -> {
                    Intent intent = new Intent(MarketBoardActivity.this, MarketPostActivity.class);
                    intent.putExtra("type",type);
                    intent.putExtra("marketId", arrayList.get(position).getMarketId());
                    startActivity(intent);
                });

                binding.tlMarketBoard.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                    @Override
                    public void onTabSelected(TabLayout.Tab tab) {
                        progressOn();
                        String category = tab.getText().toString();
                        //arrayList.clear();

                        if(category.equals("전체")) {
                            marketEngine.setMarketList(type, new CdkResponseCallback<ArrayList<MarketListItemModel>>() {
                                @Override
                                public void onDataLoaded(String result, ArrayList<MarketListItemModel> item) {
                                    binding.llMarketNoList.setVisibility(View.GONE);
                                    binding.lvMarketBoard.setVisibility(View.VISIBLE);

                                    MarketBoardAdapter adapter = new MarketBoardAdapter(MarketBoardActivity.this, arrayList);
                                    binding.lvMarketBoard.setAdapter(adapter);

                                    binding.lvMarketBoard.setOnItemClickListener((parentView, view, position, id) -> {
                                        Intent intent = new Intent(MarketBoardActivity.this, MarketPostActivity.class);
                                        intent.putExtra("type",type);
                                        intent.putExtra("marketId", arrayList.get(position).getMarketId());
                                        startActivity(intent);
                                    });

                                    progressOff();
                                }

                                @Override
                                public void onDataNotAvailable(String error) {
                                    progressOff();
                                    makeToastLong(getResources().getString(R.string.toastCallInfoErr)+error);
                                    finish();
                                }

                                @Override
                                public void onDataEmpty() {
                                    progressOff();
                                    binding.llMarketNoList.setVisibility(View.VISIBLE);
                                    binding.lvMarketBoard.setVisibility(View.GONE);
                                }
                            });
                        } else {
                            marketEngine.setMarketListAsQuery(type, category, new CdkResponseCallback<ArrayList<MarketListItemModel>>() {
                                @Override
                                public void onDataLoaded(String result, ArrayList<MarketListItemModel> arrayList1) {
                                    binding.llMarketNoList.setVisibility(View.GONE);
                                    binding.lvMarketBoard.setVisibility(View.VISIBLE);

                                    MarketBoardAdapter adapter = new MarketBoardAdapter(MarketBoardActivity.this, arrayList1);
                                    binding.lvMarketBoard.setAdapter(adapter);
                                    progressOff();
                                }

                                @Override
                                public void onDataNotAvailable(String error) {
                                    progressOff();
                                    makeToastLong(getResources().getString(R.string.toastCallInfoErr)+error);
                                    finish();
                                }

                                @Override
                                public void onDataEmpty() {
                                    progressOff();
                                    binding.llMarketNoList.setVisibility(View.VISIBLE);
                                    binding.lvMarketBoard.setVisibility(View.GONE);
                                }
                            });
                        }
                    }

                    @Override
                    public void onTabUnselected(TabLayout.Tab tab) {progressOff();}

                    @Override
                    public void onTabReselected(TabLayout.Tab tab) {progressOff();}
                });

                progressOff();
            }

            @Override
            public void onDataNotAvailable(String error) {
                progressOff();
                makeToastLong(getResources().getString(R.string.toastCallInfoErr)+error);
                finish();
            }

            @Override
            public void onDataEmpty() {
                progressOff();
                binding.llMarketNoList.setVisibility(View.VISIBLE);
                binding.lvMarketBoard.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public int getLayoutId() { return R.layout.activity_market_board; }
}
