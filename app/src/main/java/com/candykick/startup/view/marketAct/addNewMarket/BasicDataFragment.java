package com.candykick.startup.view.marketAct.addNewMarket;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import com.candykick.startup.R;
import com.candykick.startup.view.base.BaseFragment;
import com.candykick.startup.databinding.FragmentAddnewmarketBasicDataBinding;

/**
 * Created by candykick on 2019. 8. 5..
 */

public class BasicDataFragment extends BaseFragment<FragmentAddnewmarketBasicDataBinding> implements BasicDataInterface {

    String[] categoryTab = {"디자인","IT, 프로그래밍","콘텐츠 제작","마케팅","문서, 서류 및 인쇄","컨설팅, 상담","제작, 커스텀","번역, 통역","장소, 공간"};
    String[] designTab = {"선택해주세요.","웹/모바일 디자인","PPT,인포그래픽","명함,인쇄물","상세페이지,배너","3D모델링,도면","로고디자인",
            "SNS,커뮤니티","패키지,북커버","웹툰,캐릭터","캘리그라피","일러스트,삽화","캐리커쳐,인물","간판디자인","공간,인테리어","현수막,POP","기타"};
    String[] programmingTab = {"선택해주세요.","쇼핑몰,커머스","웹페이지","모바일 앱","프로그램 개발","게임","QA,테스트","챗봇","서버 구축","데이터베이스","빅데이터/데이터분석","기술지원","기타"};
    String[] contentsTab = {"선택해주세요.","영상","사진","더빙,녹음","음악,사운드","엔터테이너","기타"};
    String[] markettingTab = {"선택해주세요.","종합광고대행","블로그,카페","SNS","유튜브","인스타그램","블로그 체험단","쇼핑몰,스토어",
            "앱 마케팅","키워드,배너광고","검색최적화,SEO","지도등록,리뷰","포털질문,답변","언론홍보","마케팅 노하우","기타"};
    String[] documentTab = {"선택해주세요.","맞춤양식","카피라이팅","타이핑","대량인쇄","기타"};
    String[] consultingTab = {"선택해주세요.","법률,법무","세무회계","창업,경영진단","사업계획,제안","리서치,서베이","해외사업","HR,인사","업무지원,CS","자산관리,재테크","기타"};
    String[] makerTab = {"선택해주세요.","대량생산","공방 제작","3D프린터","맞춤제작","기타"};
    String[] translateTab = {"선택해주세요.","번역","통역","영상번역","기타"};
    String[] placeTab = {"선택해주세요.","공공사무실","장소 대여","기타"};

    public BasicDataFragment() {}

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.spANMFirstCategory.setAdapter(new ArrayAdapter<>(getActivity(), R.layout.support_simple_spinner_dropdown_item, categoryTab));
        binding.spANMFirstCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        binding.spANMSecondCategory.setAdapter(new ArrayAdapter<>(getActivity(), R.layout.support_simple_spinner_dropdown_item, designTab));
                        break;
                    case 1:
                        binding.spANMSecondCategory.setAdapter(new ArrayAdapter<>(getActivity(), R.layout.support_simple_spinner_dropdown_item, programmingTab));
                        break;
                    case 2:
                        binding.spANMSecondCategory.setAdapter(new ArrayAdapter<>(getActivity(), R.layout.support_simple_spinner_dropdown_item, contentsTab));
                        break;
                    case 3:
                        binding.spANMSecondCategory.setAdapter(new ArrayAdapter<>(getActivity(), R.layout.support_simple_spinner_dropdown_item, markettingTab));
                        break;
                    case 4:
                        binding.spANMSecondCategory.setAdapter(new ArrayAdapter<>(getActivity(), R.layout.support_simple_spinner_dropdown_item, documentTab));
                        break;
                    case 5:
                        binding.spANMSecondCategory.setAdapter(new ArrayAdapter<>(getActivity(), R.layout.support_simple_spinner_dropdown_item, consultingTab));
                        break;
                    case 6:
                        binding.spANMSecondCategory.setAdapter(new ArrayAdapter<>(getActivity(), R.layout.support_simple_spinner_dropdown_item, makerTab));
                        break;
                    case 7:
                        binding.spANMSecondCategory.setAdapter(new ArrayAdapter<>(getActivity(), R.layout.support_simple_spinner_dropdown_item, translateTab));
                        break;
                    case 8:
                        binding.spANMSecondCategory.setAdapter(new ArrayAdapter<>(getActivity(), R.layout.support_simple_spinner_dropdown_item, placeTab));
                        break;

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    public String getMarketTitle() {
        return binding.etANMTitle.getText().toString();
    }
    public String getMarketFirstCategory() {
        if(binding.spANMFirstCategory.getSelectedItem() == null) {
            return null;
        } else {
            return binding.spANMFirstCategory.getSelectedItem().toString();
        }
    }
    public String getMarketSecondCategory() {
        if(binding.spANMSecondCategory.getSelectedItem() == null || binding.spANMSecondCategory.getSelectedItemPosition() == 0) {
            makeToast("하위 카테고리를 선택해주세요.");
            return null;
        } else {
            return binding.spANMSecondCategory.getSelectedItem().toString();
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_addnewmarket_basic_data;
    }

    @Override
    public Context getContext() {
        return getActivity();
    }
}