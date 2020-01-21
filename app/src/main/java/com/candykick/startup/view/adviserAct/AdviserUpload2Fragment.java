package com.candykick.startup.view.adviserAct;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.View;

import com.candykick.startup.R;
import com.candykick.startup.view.base.BaseFragment;
import com.candykick.startup.databinding.FragmentAdviserUpload2Binding;

/**
 * A simple {@link Fragment} subclass.
 */
public class AdviserUpload2Fragment extends BaseFragment<FragmentAdviserUpload2Binding> implements AdviserUpload2Interface {


    public AdviserUpload2Fragment() { }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.setFragment(this);
    }

    public String getContents() {
        return binding.etAdvUploadContents.getText().toString();
    }

    public void setContents(String contents) {
        binding.etAdvUploadContents.setText(Html.fromHtml(contents));
    };

    public void fnBold() {
        int selectStart = binding.etAdvUploadContents.getSelectionStart();
        int selectEnd = binding.etAdvUploadContents.getSelectionEnd();

        String startStr = binding.etAdvUploadContents.getText().toString().substring(0,selectStart);
        String selectStr = binding.etAdvUploadContents.getText().toString().substring(selectStart,selectEnd);
        String endStr = binding.etAdvUploadContents.getText().toString().substring(selectEnd,binding.etAdvUploadContents.getText().toString().length());

        String result = startStr+"<b>"+selectStr+"</b>"+endStr;
        binding.etAdvUploadContents.setText(Html.fromHtml(result));
    }
    public void fnItalic() {
        int selectStart = binding.etAdvUploadContents.getSelectionStart();
        int selectEnd = binding.etAdvUploadContents.getSelectionEnd();

        String startStr = binding.etAdvUploadContents.getText().toString().substring(0,selectStart);
        String selectStr = binding.etAdvUploadContents.getText().toString().substring(selectStart,selectEnd);
        String endStr = binding.etAdvUploadContents.getText().toString().substring(selectEnd,binding.etAdvUploadContents.getText().toString().length());

        String result = startStr+"<i>"+selectStr+"</i>"+endStr;
        binding.etAdvUploadContents.setText(Html.fromHtml(result));
    }
    public void fnUnderline() {
        int selectStart = binding.etAdvUploadContents.getSelectionStart();
        int selectEnd = binding.etAdvUploadContents.getSelectionEnd();

        String startStr = binding.etAdvUploadContents.getText().toString().substring(0,selectStart);
        String selectStr = binding.etAdvUploadContents.getText().toString().substring(selectStart,selectEnd);
        String endStr = binding.etAdvUploadContents.getText().toString().substring(selectEnd,binding.etAdvUploadContents.getText().toString().length());

        String result = startStr+"<u>"+selectStr+"</u>"+endStr;
        binding.etAdvUploadContents.setText(Html.fromHtml(result));
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_adviser_upload2;
    }

    @Override
    public Context getContext() {
        return getActivity();
    }

}
