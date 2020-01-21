package com.candykick.startup.view.loginAccount.loginFragment;

import android.content.Intent;
import android.os.Bundle;

import com.candykick.startup.R;
import com.candykick.startup.view.base.BaseActivity;
import com.candykick.startup.databinding.ActivityJoinTypeBusinessDialogBinding;

public class JoinTypeBusinessDialog extends BaseActivity<ActivityJoinTypeBusinessDialogBinding> {

    String code, name="", exp="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding.setActivity(this);

        Intent intent = getIntent();
        code = intent.getStringExtra("code");
        name = intent.getStringExtra("name");
        exp = intent.getStringExtra("exp");

        binding.tvciDiaTmp11.setText(code);
        binding.tvciDiaTmp21.setText(name);
        binding.tvciDiaTmp31.setText(exp);
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
    public int getLayoutId() { return R.layout.activity_join_type_business_dialog; }
}
