package com.candykick.startup.view.referenceAct;

import android.os.Bundle;
import android.support.annotation.NonNull;

import com.candykick.startup.R;
import com.candykick.startup.engine.CdkResponseCallback;
import com.candykick.startup.engine.ReferenceEngine;
import com.candykick.startup.model.ReferenceModel;
import com.candykick.startup.view.base.BaseActivity;
import com.candykick.startup.databinding.ActivityReferenceBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class ReferenceActivity extends BaseActivity<ActivityReferenceBinding> {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding.setActivity(this);

        binding.toolbar.tvToolBar.setText(getResources().getString(R.string.strReferenceActivityTitle));
        binding.toolbar.btnToolBarLeft.setOnClickListener(v -> super.onBackPressed());

        progressOn();
        ReferenceEngine.getInstance().getReferenceList(new CdkResponseCallback<ArrayList<ReferenceModel>>() {
            @Override
            public void onDataLoaded(String result, ArrayList<ReferenceModel> item) {
                binding.gvReference.setAdapter(new ReferenceAdapter(ReferenceActivity.this, item));
                progressOff();
            }

            @Override
            public void onDataNotAvailable(String error) {
                progressOff();
                makeToastLong(getResources().getString(R.string.toastReferenceListErr)+error);
                finish();
            }

            @Override
            public void onDataEmpty() {
                progressOff();
                makeToastLong(getResources().getString(R.string.toastReferenceListErr));
                finish();
            }
        });
    }

    /*
    // '전체' 버튼 클릭 시
    public void rfButton1() {
        binding.gvReference.setAdapter(new ReferenceAdapter(ReferenceActivity.this, arrayList));
    }
    // '이력서' 버튼 클릭 시
    public void rfButton2() {
        ArrayList<ReferenceModel> arrayList2 = new ArrayList<>();
        for(int i=0;i<arrayList.size();i++) {
            if(arrayList.get(i).doctype == 1) {
                arrayList2.add(arrayList.get(i));
            }
        }
        binding.gvReference.setAdapter(new ReferenceAdapter(ReferenceActivity.this, arrayList2));
    }
    // '보고서' 버튼 클릭 시
    public void rfButton3() {
        ArrayList<ReferenceModel> arrayList3 = new ArrayList<>();
        for(int i=0;i<arrayList.size();i++) {
            if(arrayList.get(i).doctype == 2) {
                arrayList3.add(arrayList.get(i));
            }
        }
        binding.gvReference.setAdapter(new ReferenceAdapter(ReferenceActivity.this, arrayList3));
    }
    // '계약관련' 버튼 클릭 시
    public void rfButton4() {
        ArrayList<ReferenceModel> arrayList4 = new ArrayList<>();
        for(int i=0;i<arrayList.size();i++) {
            if(arrayList.get(i).doctype == 3) {
                arrayList4.add(arrayList.get(i));
            }
        }
        binding.gvReference.setAdapter(new ReferenceAdapter(ReferenceActivity.this, arrayList4));
    }
    // '회사관련' 버튼 클릭 시
    public void rfButton5() {
        ArrayList<ReferenceModel> arrayList5 = new ArrayList<>();
        for(int i=0;i<arrayList.size();i++) {
            if(arrayList.get(i).doctype == 4) {
                arrayList5.add(arrayList.get(i));
            }
        }
        binding.gvReference.setAdapter(new ReferenceAdapter(ReferenceActivity.this, arrayList5));
    }
    // '기타' 버튼 클릭 시
    public void rfButton6() {
        ArrayList<ReferenceModel> arrayList6 = new ArrayList<>();
        for(int i=0;i<arrayList.size();i++) {
            if(arrayList.get(i).doctype == 5) {
                arrayList6.add(arrayList.get(i));
            }
        }
        binding.gvReference.setAdapter(new ReferenceAdapter(ReferenceActivity.this, arrayList6));
    }*/

    @Override
    public int getLayoutId() {return R.layout.activity_reference;}
}
