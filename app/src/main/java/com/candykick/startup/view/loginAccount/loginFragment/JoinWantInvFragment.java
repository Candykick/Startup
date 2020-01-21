package com.candykick.startup.view.loginAccount.loginFragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ArrayAdapter;

import com.candykick.startup.R;
import com.candykick.startup.view.base.BaseFragment;
import com.candykick.startup.databinding.FragmentJoinWantInvBinding;

public class JoinWantInvFragment extends BaseFragment<FragmentJoinWantInvBinding> implements JoinWantInvInterface {
    public JoinWantInvFragment() {
        // Required empty public constructor
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), R.layout.spinner_item, getResources().getStringArray(R.array.strMoney));
        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        binding.spciMoney.setAdapter(adapter);
        binding.spciInvest.setAdapter(adapter);

        ArrayAdapter<String> adapter2 = new ArrayAdapter<>(getActivity(), R.layout.spinner_item, getResources().getStringArray(R.array.strInvType));
        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        binding.spciTypeInvest.setAdapter(adapter2);
    }

    @Override
    public String getMyCapital() {
        return binding.spciMoney.getSelectedItem().toString();
    }
    @Override
    public String getWantCapital() {
        return binding.spciInvest.getSelectedItem().toString();
    }
    @Override
    public String getInvestType() {
        return binding.spciTypeInvest.getSelectedItem().toString();
    }
    @Override
    public boolean isWantCapitalOpen() {
        return binding.cbciOpenToInv2.isChecked();
    }
    @Override
    public boolean isInvestTypeOpen() {
        return binding.cbciOpenToInv3.isChecked();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_join_want_inv;
    }

    @Override
    public Context getContext() {
        return getActivity();
    }
}
