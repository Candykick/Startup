package com.candykick.startup.view.loginAccount;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;

import com.candykick.startup.R;
import com.candykick.startup.view.base.BaseActivity;
import com.candykick.startup.databinding.ActivityPermissionBinding;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.util.ArrayList;

public class PermissionActivity extends BaseActivity<ActivityPermissionBinding> {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding.setActivity(this);
    }

    //'확인' 버튼 클릭 시
    public void fnPermissionOk() {
        //TedPermission.
        PermissionListener permissionListener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                Intent intent = new Intent(PermissionActivity.this, StartActivity.class);
                startActivity(intent);
                finish();
            }

            @Override
            public void onPermissionDenied(ArrayList<String> deniedPermissions) {

            }
        };

        TedPermission.with(this)
                .setPermissionListener(permissionListener)
                .setRationaleMessage("사진을 촬영하고, 갤러리에서 사진을 가져오기 위해 권한 설정이 필요합니다.")
                .setDeniedMessage("[설정] > [권한]에서 권한을 허용할 수 있습니다.")
                .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA)
                .check();
    }

    @Override
    protected int getLayoutId() { return R.layout.activity_permission; }
}
