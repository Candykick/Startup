package com.candykick.startup.view.marketAct.addNewMarket;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.View;

import com.candykick.startup.R;
import com.candykick.startup.view.base.BaseFragment;
import com.candykick.startup.databinding.FragmentAddnewmarketRequestBinding;

/**
 * A simple {@link Fragment} subclass.
 */
public class RequestforCustomerFragment extends BaseFragment<FragmentAddnewmarketRequestBinding> implements RequestforCustomerInterface {


    public RequestforCustomerFragment() {
        // Required empty public constructor
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    public String getRequest() {
        return binding.etANMRequest.getText().toString();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_addnewmarket_request;
    }

    @Override
    public Context getContext() {
        return getActivity();
    }

}