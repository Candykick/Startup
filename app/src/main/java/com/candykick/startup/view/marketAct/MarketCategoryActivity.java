package com.candykick.startup.view.marketAct;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.candykick.startup.R;
import com.candykick.startup.view.base.BaseActivity;
import com.candykick.startup.databinding.ActivityMarketCategoryBinding;

public class MarketCategoryActivity extends BaseActivity<ActivityMarketCategoryBinding> {

    long userType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding.setActivity(this);

        binding.toolbar.tvToolBar.setText("카테고리");
        binding.toolbar.btnToolBarLeft.setOnClickListener(v -> onBackPressed());

        SharedPreferences sf = getSharedPreferences("userType",MODE_PRIVATE);
        //text라는 key에 저장된 값이 있는지 확인. 아무값도 들어있지 않으면 ""를 반환
        userType = sf.getLong("userType",0);
    }

    //버튼 클릭 시 이벤트
    public void gotoDesign() {
        Intent intent = new Intent(MarketCategoryActivity.this, MarketBoardActivity.class);
        intent.putExtra("type","디자인");
        startActivity(intent);
    }
    public void gotoProgramming() {
        Intent intent = new Intent(MarketCategoryActivity.this, MarketBoardActivity.class);
        intent.putExtra("type","IT, 프로그래밍");
        startActivity(intent);
    }
    public void gotoContents() {
        Intent intent = new Intent(MarketCategoryActivity.this, MarketBoardActivity.class);
        intent.putExtra("type","콘텐츠 제작");
        startActivity(intent);
    }
    public void gotoMarketing() {
        Intent intent = new Intent(MarketCategoryActivity.this, MarketBoardActivity.class);
        intent.putExtra("type","마케팅");
        startActivity(intent);
    }
    public void gotoDocument() {
        Intent intent = new Intent(MarketCategoryActivity.this, MarketBoardActivity.class);
        intent.putExtra("type","문서, 서류 및 인쇄");
        startActivity(intent);
    }
    public void gotoConsulting() {
        Intent intent = new Intent(MarketCategoryActivity.this, MarketBoardActivity.class);
        intent.putExtra("type","컨설팅, 상담");
        startActivity(intent);
    }
    public void gotoMaker() {
        Intent intent = new Intent(MarketCategoryActivity.this, MarketBoardActivity.class);
        intent.putExtra("type","제작, 커스텀");
        startActivity(intent);
    }
    public void gotoTranslate() {
        Intent intent = new Intent(MarketCategoryActivity.this, MarketBoardActivity.class);
        intent.putExtra("type","번역, 통역");
        startActivity(intent);
    }
    public void gotoPlace() {
        Intent intent = new Intent(MarketCategoryActivity.this, MarketBoardActivity.class);
        intent.putExtra("type","장소, 공간");
        startActivity(intent);
    }
    public void gotoPrint() {
        Intent intent = new Intent(MarketCategoryActivity.this, MarketBoardActivity.class);
        intent.putExtra("type","인쇄");
        startActivity(intent);
    }

    public void addNewService() {
        if(userType == 3) {
            Intent intent = new Intent(MarketCategoryActivity.this, AddNewMarketActivity.class);
            startActivity(intent);
        } else {
            makeToast("새로운 서비스는 외주사업자만 등록 가능합니다.");
        }
    }

    @Override
    public int getLayoutId() { return R.layout.activity_market_category; }
}
