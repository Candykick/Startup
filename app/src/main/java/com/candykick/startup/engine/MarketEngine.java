package com.candykick.startup.engine;

//싱글톤 객체로 생성

import android.support.annotation.NonNull;

import com.candykick.startup.model.ChatUserModel;
import com.candykick.startup.model.MarketCommentModel;
import com.candykick.startup.model.MarketListItemModel;
import com.candykick.startup.model.MarketPostModel;
import com.candykick.startup.model.UserOutModel;
import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MarketEngine implements MarketEngineInterface {
    private static MarketEngine marketEngine = new MarketEngine();

    public static MarketEngine getInstance() { return marketEngine; }
    private MarketEngine() { }

    // 외주마켓 타입별 전체 외주목록을 얻어오는 코드.
    // 파이어베이스 버전으로 작성.
    @Override
    public void setMarketList(String type, CdkResponseCallback<ArrayList<MarketListItemModel>> responseCallback) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(type).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    if(task.getResult().isEmpty()) {
                        responseCallback.onDataEmpty();
                    } else {
                        ArrayList<MarketListItemModel> marketListItemModels = new ArrayList<>();

                        for (QueryDocumentSnapshot document : task.getResult()) {
                            MarketListItemModel data = new MarketListItemModel();
                            data.setMarketId(document.getId());
                            data.setMarketTitle(document.getData().get("title").toString());
                            data.setMarketImage(document.getData().get("image").toString());
                            data.setMarketGrade(document.getData().get("grade").toString());
                            data.setMarketReviewNum((long)document.getData().get("reviewnum"));
                            data.setMarketMinPrice((long)document.getData().get("minprice"));

                            marketListItemModels.add(data);
                        }

                        responseCallback.onDataLoaded("Success",marketListItemModels);
                    }
                } else {
                    responseCallback.onDataNotAvailable(task.getException().getLocalizedMessage());
                }
            }
        });
    }

    // 외주마켓 타입 중 특정 카테고리의 외주목록을 쿼리해서 얻어오는 코드.
    // 파이어베이스 버전으로 작성.
    @Override
    public void setMarketListAsQuery(String type, String category, CdkResponseCallback<ArrayList<MarketListItemModel>> responseCallback) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Query query = db.collection(type).whereEqualTo("category", category);
        Task<QuerySnapshot> task = query.get();
        task.addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()) {
                    if (task.getResult().isEmpty()) {
                        responseCallback.onDataEmpty();
                    } else {
                        ArrayList<MarketListItemModel> marketListItemModels = new ArrayList<>();

                        for (DocumentSnapshot document : task.getResult()) {
                            MarketListItemModel data = new MarketListItemModel();
                            data.setMarketId(document.getId());
                            data.setMarketTitle(document.getData().get("title").toString());
                            data.setMarketImage(document.getData().get("image").toString());
                            data.setMarketGrade(document.getData().get("grade").toString());
                            data.setMarketReviewNum((long) document.getData().get("reviewnum"));
                            data.setMarketMinPrice((long) document.getData().get("minprice"));

                            marketListItemModels.add(data);
                        }

                        responseCallback.onDataLoaded("Success", marketListItemModels);
                    }
                } else {
                    responseCallback.onDataNotAvailable(task.getException().getLocalizedMessage());
                }
            }
        });
    }

    // 외주마켓 글 전체 내용을 얻어오는 코드.
    // 파이어베이스 버전으로 작성.
    @Override
    public void setMarketPost(String type, String postId, CdkResponseCallback<MarketPostModel> responseCallback) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(type).document(postId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {

                        MarketPostModel marketPostModel = new MarketPostModel();
                        marketPostModel.setTitleImage((String)document.get("image"));
                        marketPostModel.setTitle((String)document.get("title"));
                        marketPostModel.setReviewNum((long)document.get("reviewnum"));
                        marketPostModel.setMinimumPrice((long)document.get("minprice"));
                        marketPostModel.setDetail(((String)document.get("detail")).replace("\\n",System.getProperty("line.separator")));
                        marketPostModel.setInfoDataLst((List<HashMap<String, String>>)document.get("info"));
                        marketPostModel.setModifyInfo(((String)document.get("modify")).replace("\\n",System.getProperty("line.separator")));
                        marketPostModel.setRefundInfo(((String)document.get("refund")).replace("\\n",System.getProperty("line.separator")));
                        marketPostModel.setReceiverId((String)document.get("user"));

                        //업체의 유저 정보를 가져옴
                        getUserOutInfo(marketPostModel.getReceiverId(), new CdkResponseCallback<UserOutModel>() {
                            @Override
                            public void onDataLoaded(String result, UserOutModel item) {
                                marketPostModel.setReceiverAllData(item);
                                marketPostModel.setReceiverData(new ChatUserModel(item.userId, item.userName, 3, item.profileImage));

                                getMarketCommentsForMain(type, postId, new CdkResponseCallback<ArrayList<MarketCommentModel>>() {
                                    @Override
                                    public void onDataLoaded(String result, ArrayList<MarketCommentModel> item) {
                                        marketPostModel.setCommentArray(item);

                                        responseCallback.onDataLoaded("Success", marketPostModel);
                                    }

                                    @Override
                                    public void onDataNotAvailable(String error) {
                                        responseCallback.onDataNotAvailable(task.getException().getLocalizedMessage());
                                    }

                                    @Override
                                    public void onDataEmpty() {
                                        responseCallback.onDataEmpty();
                                    }
                                });
                            }

                            @Override
                            public void onDataNotAvailable(String error) {
                                responseCallback.onDataNotAvailable(task.getException().getLocalizedMessage());
                            }

                            @Override
                            public void onDataEmpty() {
                                responseCallback.onDataEmpty();
                            }
                        });
                    } else {
                        responseCallback.onDataEmpty();
                    }
                } else {
                    responseCallback.onDataNotAvailable(task.getException().getLocalizedMessage());
                }
            }
        });
    }

    // 외주마켓 글 댓글 내용을 얻어오는 코드.
    // 파이어베이스 버전으로 작성.
    @Override
    public void getMarketComments(String type, String postId, CdkResponseCallback<ArrayList<MarketCommentModel>> responseCallback) {
        FirebaseFirestore.getInstance().collection(type).document(postId).collection("comment").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                ArrayList<MarketCommentModel> commentArray = new ArrayList<>();
                if(!queryDocumentSnapshots.isEmpty()) {
                    for(DocumentSnapshot document : queryDocumentSnapshots) {
                        MarketCommentModel data = new MarketCommentModel(Integer.parseInt(document.getId()),
                                document.get("comment").toString(), document.get("username").toString(), document.get("profile").toString(),
                                ((Long)document.get("parent")).intValue(), ((Long)document.get("seq")).intValue(), ((Long)document.get("star")).intValue());
                        commentArray.add(data);
                    }
                }

                responseCallback.onDataLoaded("Success", commentArray);

                //commentArray.clear();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                responseCallback.onDataNotAvailable(e.getLocalizedMessage());
            }
        }).addOnCanceledListener(new OnCanceledListener() {
            @Override
            public void onCanceled() {
                responseCallback.onDataEmpty();
            }
        });
    }

    @Override
    public void getMarketCommentsForMain(String type, String postId, CdkResponseCallback<ArrayList<MarketCommentModel>> responseCallback) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(type).document(postId).collection("comment").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                ArrayList<MarketCommentModel> commentArray = new ArrayList<>();

                if(!queryDocumentSnapshots.isEmpty()) {
                    for(DocumentSnapshot document : queryDocumentSnapshots) {
                        MarketCommentModel data = new MarketCommentModel(Integer.parseInt(document.getId()),
                                document.get("comment").toString(), document.get("username").toString(), document.get("profile").toString(),
                                ((Long)document.get("parent")).intValue(), ((Long)document.get("seq")).intValue(), ((Long)document.get("star")).intValue());
                        commentArray.add(data);

                        if(commentArray.size() == 5) {
                            break;
                        }
                    }
                }

                responseCallback.onDataLoaded("Success", commentArray);
            }
        });
    }

    // 외주마켓 글에 새 댓글을 등록하는 코드.
    // 파이어베이스 버전으로 작성.
    @Override
    public void registerNewComment(String type, String postId, Map<String, Object> commentData, int listNum, CdkResponseCallback<String> responseCallback) {
        FirebaseFirestore.getInstance().collection(type)
                .document(postId).collection("comment").document(""+(listNum+1)).set(commentData, SetOptions.merge())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        responseCallback.onDataLoaded("", "");
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                responseCallback.onDataNotAvailable(e.getLocalizedMessage());
            }
        });
    }

    // 내부함수
    // 외주마켓용: 외주업체 정보를 가져오는 코드.
    private void getUserOutInfo(String userid, CdkResponseCallback<UserOutModel> responseCallback) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("UserCeo").document(userid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        UserOutModel allData = new UserOutModel(userid, (String)document.get("username"), (long)document.get("usertype"), (String)document.get("profileimage"),
                                (boolean)document.get("taxinvoicepossible"),(String)document.get("userDes"),(String)document.get("userSatis"),
                                (long)document.get("userOrderNum"), (String)document.get("userAnswerTime"));

                        responseCallback.onDataLoaded("Success", allData);
                    } else {
                        responseCallback.onDataEmpty();
                    }
                } else {
                    responseCallback.onDataNotAvailable(task.getException().getLocalizedMessage());
                }
            }
        });
    }

    // 외주마켓에 새 글을 등록하는 코드.
    // 파이어베이스 버전으로 작성.
    @Override
    public void uploadMarketPost(String type, MarketPostModel postModel, CdkResponseCallback<String> responseCallback) {

    }
}
