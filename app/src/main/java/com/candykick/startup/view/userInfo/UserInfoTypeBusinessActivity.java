package com.candykick.startup.view.userInfo;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;

import com.candykick.startup.R;
import com.candykick.startup.view.base.BaseActivity;
import com.candykick.startup.databinding.ActivityUserInfoTypeBusinessBinding;
import com.candykick.startup.view.loginAccount.loginFragment.JoinTypeBusinessAdapter;
import com.candykick.startup.view.loginAccount.loginFragment.JoinTypeBusinessClass;
import com.candykick.startup.view.loginAccount.loginFragment.JoinTypeBusinessDialog;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;

public class UserInfoTypeBusinessActivity extends BaseActivity<ActivityUserInfoTypeBusinessBinding> {

    ArrayList<JoinTypeBusinessClass> arrayTempClass = new ArrayList<>();
    ArrayList<String> selectTags;
    JoinTypeBusinessAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding.setActivity(this);

        Intent intent = getIntent();
        selectTags = intent.getStringArrayListExtra("jobTags");
        binding.tgModifyTB.setTags(selectTags);

        //파이어베이스에서 업종 정보 읽어옴
        //한 번에 전부 다 로딩해서 저장.
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Industry")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful() && !task.getResult().isEmpty()) {
                            for(QueryDocumentSnapshot document: task.getResult()) {
                                JoinTypeBusinessClass tempData = new JoinTypeBusinessClass();
                                tempData.industryName = document.get("name").toString();
                                tempData.industrySearch = document.get("search").toString();
                                tempData.industryExp = document.get("exp").toString();
                                tempData.industryCode = document.getId();

                                /*tempData.industryExp.replace("<","&lt;");
                                tempData.industryExp.replace(">","&gt;");*/
                                tempData.industryExp = tempData.industryExp.replace("\\n",System.getProperty("line.separator"));

                                arrayTempClass.add(tempData);
                            }

                            adapter = new JoinTypeBusinessAdapter(arrayTempClass, UserInfoTypeBusinessActivity.this);
                            binding.lvModifyTB.setAdapter(adapter);
                            binding.lvModifyTB.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    Intent intent = new Intent(UserInfoTypeBusinessActivity.this, JoinTypeBusinessDialog.class);
                                    intent.putExtra("code",arrayTempClass.get(position).industryCode);
                                    intent.putExtra("name",arrayTempClass.get(position).industryName);
                                    intent.putExtra("exp",arrayTempClass.get(position).industryExp);
                                    startActivityForResult(intent,3000);
                                }
                            });
                        } else {
                            makeToastLong("업종 분류를 불러오는 도중 오류가 발생했습니다. 다시 시도해 주세요.");
                        }
                    }
                });

        binding.etModifyTB.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                String text = binding.etModifyTB.getText().toString();
                adapter.filter(text);
            }
        });
    }

    public void modifyEnd() {
        selectTags = new ArrayList<>(Arrays.asList(binding.tgModifyTB.getTags()));

        Intent intent = new Intent();
        intent.putExtra("result",selectTags);
        setResult(RESULT_OK,intent);
        finish();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Boolean isAlreadyAdded = false;
        selectTags = new ArrayList<>(Arrays.asList(binding.tgModifyTB.getTags()));

        if (resultCode == RESULT_OK) {
            String result = data.getStringExtra("result");

            for(int i=0;i<selectTags.size();i++) {
                if (result.equals(selectTags.get(i))) {
                    isAlreadyAdded = true;
                    makeToast("이미 추가한 업종입니다.");
                    break;
                }
            }

            if(!isAlreadyAdded) {
                selectTags.add(result);
                binding.tgModifyTB.setTags(selectTags);
            }
            /*String currentText = binding.tvTmpType.getText().toString();
            if(currentText.length() == 0) {
                binding.tvTmpType.setText(data.getStringExtra("result"));
            } else {
                binding.tvTmpType.setText(currentText + ", " + data.getStringExtra("result"));
            }*/
        }
    }

    @Override
    protected int getLayoutId() { return R.layout.activity_user_info_type_business; }
}
