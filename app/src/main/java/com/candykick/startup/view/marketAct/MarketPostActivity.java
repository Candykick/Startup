package com.candykick.startup.view.marketAct;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.widget.NestedScrollView;
import android.view.View;

import com.bumptech.glide.Glide;
import com.candykick.startup.R;
import com.candykick.startup.engine.CdkEmptyCallback;
import com.candykick.startup.engine.CdkResponseCallback;
import com.candykick.startup.engine.ChatEngine;
import com.candykick.startup.engine.MarketEngine;
import com.candykick.startup.model.ChatUserModel;
import com.candykick.startup.model.MarketPostModel;
import com.candykick.startup.model.MyUserModel;
import com.candykick.startup.view.base.BaseActivity;
import com.candykick.startup.databinding.ActivityMarketPostBinding;
import com.candykick.startup.view.talkAct.ChatUserData;
import com.candykick.startup.view.talkAct.ChattingActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.kakao.auth.Session;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.MeV2ResponseCallback;
import com.kakao.usermgmt.response.MeV2Response;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MarketPostActivity extends BaseActivity<ActivityMarketPostBinding> {

    boolean isMore = false;
    PostPageAdapter viewPagerAdapter;
    ChatUserModel otherModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding.setActivity(this);

        setSupportActionBar(binding.toolbar);
        binding.btnMPChat.hide();
        binding.btnToolBarLeft.setOnClickListener(v -> onBackPressed());
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        String type = getIntent().getStringExtra("type");
        String postId = getIntent().getStringExtra("marketId");

        MyUserModel.getInstance().setUserData(new CdkResponseCallback<String>() {
            @Override
            public void onDataLoaded(String result, String item) {
                progressOn();
                setPostData(type, postId);
            }

            @Override
            public void onDataNotAvailable(String error) {
                makeToast(getResources().getString(R.string.toastCallInfoErr) + error);
                finish();
            }

            @Override
            public void onDataEmpty() {
                makeToast(getResources().getString(R.string.toastUserInfoErr));
                finish();
            }
        });
    }

    public void setPostData(String type, String postId) {
        MarketEngine.getInstance().setMarketPost(type, postId, new CdkResponseCallback<MarketPostModel>() {
            @Override
            public void onDataLoaded(String result, MarketPostModel item) {
                otherModel = item.getReceiverData();

                Glide.with(MarketPostActivity.this).load(item.getTitleImage()).into(binding.ivMarketPostTitle);

                //주요 정보 셋팅
                binding.tvMPTitle.setText(item.getTitle());
                binding.tvMPReviewNum.setText("("+item.getReviewNum()+")");
                DecimalFormat priceFormat = new DecimalFormat("###,###");
                binding.tvMPMinPrice.setText(priceFormat.format(item.getMinimumPrice())+"원");
                binding.tvMPInfo.setText(item.getDetail());
                //binding.tvMPCategory.setText("");

                //패키지 정보 셋팅
                if(item.getInfoDataLst().size() == 1) {
                    binding.tlMPInfo.setVisibility(View.GONE);
                } else {
                    for(int i=0;i<item.getInfoDataLst().size();i++)
                        binding.tlMPInfo.addTab(binding.tlMPInfo.newTab().setText(priceFormat.format(Integer.parseInt(item.getInfoDataLst().get(i).get("price")))+"원"));
                }
                viewPagerAdapter = new PostPageAdapter(getSupportFragmentManager(), item.getInfoDataLst());
                binding.vpMPInfo.setAdapter(viewPagerAdapter);
                binding.vpMPInfo.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(binding.tlMPInfo));
                binding.tlMPInfo.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                    @Override
                    public void onTabSelected(TabLayout.Tab tab) {
                        binding.vpMPInfo.setCurrentItem(tab.getPosition());
                    }

                    @Override
                    public void onTabUnselected(TabLayout.Tab tab) {}

                    @Override
                    public void onTabReselected(TabLayout.Tab tab) {}
                });

                PostExpandableAdapter postExpandableAdapter = new PostExpandableAdapter(
                        MarketPostActivity.this, item.getModifyInfo(), item.getRefundInfo(),
                        item.getReceiverAllData(), item.getCommentArray(), type, postId);
                binding.elvMP.setAdapter(postExpandableAdapter);
                binding.elvMP.setGroupIndicator(null);

                //채팅 버튼 보여주기 + 화면 스크롤에 따라서 채팅버튼 숨기기/표시하기
                binding.btnMPChat.show();
                binding.nsMP.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
                    @Override
                    public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                        if(scrollY > oldScrollY) {
                            binding.btnMPChat.hide();
                        } else {
                            binding.btnMPChat.show();
                        }
                    }
                });

                progressOff();
            }

            @Override
            public void onDataNotAvailable(String error) {
                progressOff();
                finish();
                makeToastLong(getResources().getString(R.string.toastErr) + error);
            }

            @Override
            public void onDataEmpty() {
                progressOff();
                finish();
                makeToastLong(getResources().getString(R.string.toastPostAlreadyDeletedErr));
            }
        });
    }

    //'더보기' 버튼 클릭 시
    public void fnMore() {
        if(!isMore) {
            binding.btnMPMore.setText("닫기");
            binding.tvMPInfo.setMaxLines(100);
            isMore = true;
        } else {
            binding.btnMPMore.setText("더보기");
            binding.tvMPInfo.setMaxLines(4);
            isMore = false;
        }
    }

    //채팅 버튼 클릭 시
    public void fnChat() {
        progressOn();
        ChatEngine.getInstance().enterChatRoom(otherModel, new CdkEmptyCallback() {
            @Override
            public void onSuccessed() {
                progressOff();
                Intent intent = new Intent(MarketPostActivity.this, ChattingActivity.class);
                startActivity(intent);
            }

            @Override
            public void onFailed(String error) {
                progressOff();
                makeToastLong(getResources().getString(R.string.toastErr) + error);
            }
        });
    }

    @Override
    public int getLayoutId() { return R.layout.activity_market_post; }
}