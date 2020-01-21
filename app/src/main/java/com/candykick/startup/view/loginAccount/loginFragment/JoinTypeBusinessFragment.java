package com.candykick.startup.view.loginAccount.loginFragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CompoundButton;

import com.candykick.startup.R;
import com.candykick.startup.view.base.BaseFragment;
import com.candykick.startup.databinding.FragmentJoinTypeBusinessBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static android.app.Activity.RESULT_OK;

public class JoinTypeBusinessFragment extends BaseFragment<FragmentJoinTypeBusinessBinding> implements JoinTypeBusinessInterface {

    ArrayList<JoinTypeBusinessClass> arrayTempClass = new ArrayList<>();
    List<String> selectTags;
    JoinTypeBusinessAdapter adapter;

    Boolean isAll = false;

    public JoinTypeBusinessFragment() {
        // Required empty public constructor
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if(getArguments().getInt("key") == 0) {
            binding.tvciTitle4.setText("업종 선택");
            binding.cbciOpenToInv1.setVisibility(View.VISIBLE);
            binding.cbciNoType.setText("업종 없음");
        }
        else if(getArguments().getInt("key") == 1) {
            binding.tvciTitle4.setText("투자희망업종 선택");
            binding.cbciOpenToInv1.setVisibility(View.GONE);
            binding.cbciNoType.setText("업종 관계없이 투자 가능");
        }

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

                            adapter = new JoinTypeBusinessAdapter(arrayTempClass, getActivity());
                            binding.lvciType.setAdapter(adapter);
                            binding.lvciType.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    Intent intent = new Intent(getActivity(), JoinTypeBusinessDialog.class);
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

        binding.etciBusiness.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                String text = binding.etciBusiness.getText().toString();
                adapter.filter(text);
            }
        });

        binding.cbciNoType.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    binding.lvciType.setActivated(false);
                    binding.tgciType.setActivated(false);
                    binding.tgciType.setTags();
                    isAll = true;
                } else {
                    binding.lvciType.setActivated(true);
                    binding.tgciType.setActivated(true);
                    isAll = false;
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Boolean isAlreadyAdded = false;
        selectTags = Arrays.asList(binding.tgciType.getTags());
        selectTags = new ArrayList<>(selectTags);

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
                binding.tgciType.setTags(selectTags);
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
    public String getTypeBusiness() {
        if(isAll)
            return "all";
        else {
            String[] typeBusiness = binding.tgciType.getTags();
            String result = typeBusiness[0];

            if (typeBusiness.length > 1) {
                for (int i = 1; i < typeBusiness.length; i++) {
                    result = result + ", " + typeBusiness[i];
                }
            }

            return result;
        }
    }

    @Override
    public boolean isTypeOpen() {
        return binding.cbciOpenToInv1.isChecked();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_join_type_business;
    }

    @Override
    public Context getContext() {
        return getActivity();
    }
}
