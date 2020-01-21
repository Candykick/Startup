package com.candykick.startup;

import android.content.Intent;
import android.os.Bundle;

import com.candykick.startup.view.base.BaseActivity;
import com.candykick.startup.databinding.DialogTempBinding;

public class TempDialog extends BaseActivity<DialogTempBinding> {

    String code, name="", exp="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding.setActivity(this);

        Intent intent = getIntent();
        code = intent.getStringExtra("code");
        name = intent.getStringExtra("name");
        exp = intent.getStringExtra("exp");

        binding.tvDiaTmp11.setText(code);
        binding.tvDiaTmp21.setText(name);
        binding.tvDiaTmp31.setText(exp);
    }

    public void btnCancel() {
        Intent intent = new Intent();
        setResult(RESULT_CANCELED,intent);
        finish();
    }

    public void btnSelect() {
        Intent intent = new Intent();
        intent.putExtra("result",name);
        setResult(RESULT_OK,intent);
        finish();
    }

    @Override
    public int getLayoutId() { return R.layout.dialog_temp; }
}
