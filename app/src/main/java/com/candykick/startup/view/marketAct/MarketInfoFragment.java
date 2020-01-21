package com.candykick.startup.view.marketAct;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

import com.candykick.startup.R;
import com.candykick.startup.view.base.BaseFragment;
import com.candykick.startup.databinding.FragmentMarketInfoBinding;

import java.util.HashMap;

public class MarketInfoFragment extends BaseFragment<FragmentMarketInfoBinding> {

    HashMap<String, String> data;

    public MarketInfoFragment() {}

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        data = (HashMap<String, String>)getArguments().get("data");

        binding.tvFMITitle.setText(data.get("infotitle"));
        binding.tvFMIExp.setText(data.get("infoexp").replace("\\n",System.getProperty("line.separator")));
        binding.tvFMIModify.setText(data.get("infomodnum")+"회");
        binding.tvFMIDate.setText(data.get("infoworkdate")+"일");
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_market_info;
    }

    @Override
    public Context getContext() {
        return getActivity();
    }
}