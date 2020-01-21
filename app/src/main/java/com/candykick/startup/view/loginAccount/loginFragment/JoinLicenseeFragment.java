package com.candykick.startup.view.loginAccount.loginFragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

import com.candykick.startup.R;
import com.candykick.startup.view.base.BaseFragment;
import com.candykick.startup.databinding.FragmentJoinLicenseeBinding;

public class JoinLicenseeFragment extends BaseFragment<FragmentJoinLicenseeBinding> implements JoinLicenseeInterface {


    public JoinLicenseeFragment() {
        // Required empty public constructor
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public int getOfficeInfo() {
        if(binding.rbciOffice.isChecked()) {
            return 1;
        } else if(binding.rbciNoOffice.isChecked()) {
            return 2;
        } else {
            return 0;
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_join_licensee;
    }

    @Override
    public Context getContext() {
        return getActivity();
    }

}
