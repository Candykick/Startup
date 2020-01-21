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
import com.candykick.startup.databinding.FragmentTempBinding;
import com.fxn.pix.Pix;

import java.io.File;
import java.util.ArrayList;

public class TempFragment extends BaseFragment<FragmentTempBinding> implements TempInterface {

    ArrayList<File> fileArray = new ArrayList<>();

    public TempFragment() {}

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.setFragment(this);

        ImageViewPagerAdapter adapter = new ImageViewPagerAdapter(getActivity(), this, fileArray, 1);
        binding.vpUploadImage2.setAdapter(adapter);
    }

    public ArrayList<File> getImages() {
        return fileArray;
    }
    public String getContents() {
        return binding.etAdvAnswerContents.getText().toString();
    }

    public void fnBold() {

    }
    public void fnItalic() {

    }
    public void fnUnderline() {

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

            ImageViewPagerAdapter adapter = new ImageViewPagerAdapter(getActivity(), this, fileArray, 1);
            binding.vpUploadImage2.setAdapter(adapter);
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
        return R.layout.fragment_temp;
    }

    @Override
    public Context getContext() {
        return getActivity();
    }
}
