package com.candykick.startup.engine;

import android.support.annotation.NonNull;

import com.candykick.startup.model.ReferenceModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

/*
 파이어베이스 버전은 자료실의 모든 데이터를 통으로 로딩한 뒤, 자료 하나를 보여줄 때는 Intent로 필요한 값들을 전달하는 방식을 쓰고 있다.
 */

public class ReferenceEngine implements ReferenceEngineInterface {
    private static ReferenceEngine referenceEngine = new ReferenceEngine();

    public static ReferenceEngine getInstance() { return referenceEngine; }
    private ReferenceEngine() { }

    // 자료실의 모든 자료 목록 받아오기
    @Override
    public void getReferenceList(CdkResponseCallback<ArrayList<ReferenceModel>> responseCallback) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("File")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()) {
                            if(!task.getResult().isEmpty()){
                                ArrayList<ReferenceModel> referenceModels = new ArrayList<>();

                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    ReferenceModel model = new ReferenceModel((long) document.get("doctype"),
                                            (String) document.get("filename"),
                                            (String) document.get("fileimage"),
                                            (String) document.get("filepath"),
                                            (List<String>) document.get("subimage"));

                                    referenceModels.add(model);
                                }

                                responseCallback.onDataLoaded("Success", referenceModels);
                            } else {
                                responseCallback.onDataEmpty();
                            }
                        } else {
                            responseCallback.onDataNotAvailable(task.getException().getLocalizedMessage());
                        }
                    }
                });
    }


}
