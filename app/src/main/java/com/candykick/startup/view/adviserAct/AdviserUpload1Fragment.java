package com.candykick.startup.view.adviserAct;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

import com.candykick.startup.R;
import com.candykick.startup.view.base.BaseFragment;
import com.candykick.startup.databinding.FragmentAdviserUpload1Binding;
import com.fxn.pix.Pix;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by candykick on 2019. 9. 15..
 */

public class AdviserUpload1Fragment extends BaseFragment<FragmentAdviserUpload1Binding> implements AdviserUpload1Interface {

    ArrayList<File> fileArray = new ArrayList<>();
    public AdviserUpload1Fragment() {}

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.setFragment(this);

        ImageViewPagerAdapter adapter = new ImageViewPagerAdapter(getActivity(), this, fileArray);
        binding.vpUploadImage.setAdapter(adapter);
    }

    public String getTitle() {
        return binding.etAdvUploadTitle.getText().toString();
    }
    public String getCategory() {
        return binding.spAdvUploadCategory.getSelectedItem().toString();
    }
    public boolean getIsWriterOpen() {
        return binding.cbAdvUploadUserOpen.isChecked();
    }
    public ArrayList<File> getImageSrc() {
        return fileArray;
    }

    public void setTitle(String title) {
        binding.etAdvUploadTitle.setText(title);
    }
    public void setCategory(String category) {
        String[] categorys = getResources().getStringArray(R.array.strAdvType);
        for(int i=0;i<categorys.length;i++) {
            if(category.equals(categorys[i])) {
                binding.spAdvUploadCategory.setSelection(i);
            }
        }
    }
    public void setIsWriterOpen(boolean isWriterOpen) {
        binding.cbAdvUploadUserOpen.setChecked(isWriterOpen);
    }
    public void setImageSrc(ArrayList<String> imageSrc) {
        //셋팅만 되어 있음.
        //ImageViewPagerAdapter adapter = new ImageViewPagerAdapter(getActivity(), imageSrc, true);
        //binding.vpUploadImage.setAdapter(adapter);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == 1009) {
            ArrayList<String> returnValue = data.getStringArrayListExtra(Pix.IMAGE_RESULTS);

            for(String url: returnValue) {
                File tempFile = new File(Uri.parse(url).getPath());
                fileArray.add(tempFile);
            }

            ImageViewPagerAdapter adapter = new ImageViewPagerAdapter(getActivity(), this, fileArray);
            binding.vpUploadImage.setAdapter(adapter);
        }

        /*if(resultCode != RESULT_OK) {
            makeToast("선택된 사진이 없습니다.");
            return;
        }

        else if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if(resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();


            } else if(resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                makeToast("오류가 발생했습니다: "+error.getMessage());
            }
        }*/
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_adviser_upload1;
    }

    @Override
    public Context getContext() {
        return getActivity();
    }
}
