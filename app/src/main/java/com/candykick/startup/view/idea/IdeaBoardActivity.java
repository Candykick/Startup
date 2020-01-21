package com.candykick.startup.view.idea;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.candykick.startup.R;
import com.candykick.startup.engine.CdkResponseCallback;
import com.candykick.startup.engine.IdeaEngine;
import com.candykick.startup.model.IdeaBoardModel;
import com.candykick.startup.model.MyUserModel;
import com.candykick.startup.view.base.BaseActivity;
import com.candykick.startup.databinding.ActivityIdeaBoardBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class IdeaBoardActivity extends BaseActivity<ActivityIdeaBoardBinding> {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding.setActivity(this);

        binding.toolbar.tvToolBar.setText(getResources().getString(R.string.strIdeaBoardActivityTitle));
        binding.toolbar.btnToolBarLeft.setOnClickListener(v -> onBackPressed());

        MyUserModel.getInstance().setUserData(new CdkResponseCallback<String>() {
            @Override
            public void onDataLoaded(String result, String item) { }
            @Override
            public void onDataNotAvailable(String error) { }
            @Override
            public void onDataEmpty() { }});

        progressOn();

        IdeaEngine.getInstance().getIdeaBoard(new CdkResponseCallback<ArrayList<IdeaBoardModel>>() {
            @Override
            public void onDataLoaded(String result, ArrayList<IdeaBoardModel> item) {
                progressOff();

                IdeaBoardAdapter adapter = new IdeaBoardAdapter(IdeaBoardActivity.this, item);
                binding.lvIdeaBoard.setAdapter(adapter);

                binding.lvIdeaBoard.setOnItemClickListener((parentView, view, position, id) -> {
                    Intent intent = new Intent(IdeaBoardActivity.this, IdeaPostActivity.class);
                    intent.putExtra("id",item.get(position).getIdeaId());
                    intent.putExtra("title",item.get(position).getIdeaTitle());
                    intent.putExtra("contents",item.get(position).getIdeaContent());
                    intent.putExtra("user",item.get(position).getIdeaUser());
                    startActivity(intent);
                });
            }

            @Override
            public void onDataNotAvailable(String error) {
                progressOff();
                makeToastLong(getResources().getString(R.string.toastCallInfoErr)+error);
                finish();
            }

            @Override
            public void onDataEmpty() {
                progressOff();
                makeToastLong(getResources().getString(R.string.toastNoPost));
            }
        });
    }

    @Override
    protected int getLayoutId() { return R.layout.activity_idea_board; }

    public void addIdea() {
        Intent intent = new Intent(IdeaBoardActivity.this, IdeaUploadActivity.class);
        intent.putExtra("isnew",true);
        startActivity(intent);
    }
}
