package com.candykick.startup.view.marketAct.addNewMarket;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

import com.candykick.startup.R;
import com.candykick.startup.view.base.BaseFragment;
import com.candykick.startup.databinding.FragmentDesBinding;

public class DesFragment extends BaseFragment<FragmentDesBinding> implements DesInterface {

    public DesFragment() {}


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    public String getMarketDes() {
        if(binding.etANMDes.getText().toString().replace(" ","").length() == 0) {
            makeToast("서비스 설명을 입력해주세요.");
            return null;
        } else {
            return binding.etANMDes.getText().toString();
        }
    }

    public String getMarketModify() {
        if(binding.etANMModify.getText().toString().replace(" ","").length() == 0) {
            makeToast("수정 및 재진행 안내를 입력해주세요.");
            return null;
        } else {
            return binding.etANMModify.getText().toString();
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_des;
    }

    @Override
    public Context getContext() {
        return getActivity();
    }
}
