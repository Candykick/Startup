package com.candykick.startup.view.loginAccount.loginFragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ArrayAdapter;

import com.candykick.startup.R;
import com.candykick.startup.view.base.BaseFragment;
import com.candykick.startup.databinding.FragmentJoinPersonalBinding;

public class JoinPersonalFragment extends BaseFragment<FragmentJoinPersonalBinding> implements JoinPersonalInterface {

    public JoinPersonalFragment() { }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), R.layout.spinner_item, getResources().getStringArray(R.array.strArrayYear));
        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        binding.spciYear.setAdapter(adapter);
    }

    @Override
    public long getGender() {
        if(binding.rbciMan.isChecked()) {
            return 1;
        } else if(binding.rbciWoman.isChecked()) {
            return 2;
        } else {
            return 0;
        }
    }

    @Override
    public String getCeoAge() {
        return binding.spciYear.getSelectedItem().toString();
    }

    @Override
    public String getCeoRole() {
        String result = "";
        if(binding.cbciCeo.isChecked()) {
            result+="대표, ";
        } if(binding.cbciProvider.isChecked()) {
            result+="기획자, ";
        } if(binding.cbciDeveloper.isChecked()) {
            result+="개발자, ";
        } if(binding.cbciDesigner.isChecked()) {
            result+="디자이너, ";
        } if(binding.cbciMarketer.isChecked()) {
            result+="마케터, ";
        } if(binding.cbciPM.isChecked()) {
            result+="프로젝트 매니저, ";
        } if(binding.cbciOther.isChecked()) {
            result+="기타 역할";
        }

        if(result.charAt(result.length()-2) == ',') {
            result = result.substring(0,result.length()-2);
        }
        return result;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_join_personal;
    }

    @Override
    public Context getContext() {
        return getActivity();
    }
}
