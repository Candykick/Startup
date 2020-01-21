package com.candykick.startup.view.loginAccount.loginFragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import com.candykick.startup.R;
import com.candykick.startup.view.base.BaseFragment;
import com.candykick.startup.databinding.FragmentJoinAdv1Binding;
import com.candykick.startup.view.loginAccount.JoinAdvAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class JoinAdv1Fragment extends BaseFragment<FragmentJoinAdv1Binding> implements JoinAdv1Interface {

    private JoinAdvAdapter advAdapter; private LinearLayoutManager linearLayoutManager;

    public JoinAdv1Fragment() {
        // Required empty public constructor
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.setFragment(this);

        linearLayoutManager = new LinearLayoutManager(getActivity());
        binding.rvJoinAdv.setLayoutManager(linearLayoutManager);
        advAdapter = new JoinAdvAdapter(getActivity());
        binding.rvJoinAdv.setAdapter(advAdapter);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getActivity(), linearLayoutManager.getOrientation());
        binding.rvJoinAdv.addItemDecoration(dividerItemDecoration);
    }

    public void addCareer() {
        advAdapter.add();
    }


    //경력 리스트 반환
    public ArrayList<Map<String, String>> getAdvCareer() {
        ArrayList<Map<String, String>> careerList = new ArrayList<>();
        if(advAdapter.getCareer().size() == 0) {
            makeToast("이력을 1개 이상 입력해주세요.");
        } else {
            for (int i = 0; i < advAdapter.getCareer().size(); i++) {
                Map<String, String> mapData = new HashMap<>();
                mapData.put("careerdate", advAdapter.getCareer().get(i).careerDate);
                mapData.put("careertitle", advAdapter.getCareer().get(i).careerName);

                careerList.add(mapData);
            }
        }
        return careerList;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_join_adv1;
    }

    @Override
    public Context getContext() {
        return getActivity();
    }
}