package com.candykick.startup.view.referenceAct;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.candykick.startup.R;
import com.candykick.startup.view.base.BaseActivity;
import com.candykick.startup.databinding.ActivityReferenceSelectBinding;

import java.util.ArrayList;

public class ReferenceSelectActivity extends BaseActivity<ActivityReferenceSelectBinding> {

    String filepath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding.setActivity(this);

        Intent gIntent = getIntent();
        String filename = gIntent.getStringExtra("filename");
        ArrayList<String> imageList = gIntent.getStringArrayListExtra("subimage");
        filepath = gIntent.getStringExtra("filepath");

        binding.toolbar.tvToolBar.setText("문서 다운로드");
        binding.toolbar.btnToolBarLeft.setOnClickListener(v -> super.onBackPressed());

        binding.tvRSDocTitle.setText(filename);
        ReferenceSubimageAdapter adapter = new ReferenceSubimageAdapter(imageList, ReferenceSelectActivity.this);
        binding.vpRSDocument.setAdapter(adapter);
    }

    // '다운로드' 버튼 클릭 시
    public void btnRSDownload() {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(filepath));
        startActivity(intent);
    }

    @Override
    public int getLayoutId() {return R.layout.activity_reference_select;}
}
