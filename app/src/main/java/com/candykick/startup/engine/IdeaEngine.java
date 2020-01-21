package com.candykick.startup.engine;

import android.support.annotation.NonNull;

import com.candykick.startup.model.IdeaBoardModel;
import com.candykick.startup.model.IdeaCommentModel;
import com.candykick.startup.model.IdeaPostModel;
import com.candykick.startup.model.MyUserModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class IdeaEngine implements IdeaEngineInterface {
    private static IdeaEngine ideaEngine = new IdeaEngine();

    public static IdeaEngine getInstance() { return ideaEngine; }
    private IdeaEngine() { }

    // 아이디어 게시판 전체 게시판 정보를 얻어오는 코드.
    // 파이어베이스 버전으로 작성.
    @Override
    public void getIdeaBoard(CdkResponseCallback<ArrayList<IdeaBoardModel>> responseCallback) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Idea").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    if (!task.getResult().isEmpty()) {
                        ArrayList<IdeaBoardModel> boardData = new ArrayList<>();

                        for (QueryDocumentSnapshot document : task.getResult()) {
                            IdeaBoardModel model = new IdeaBoardModel(document.getId(),
                                    (boolean)document.getData().get("isnotice"),
                                    document.getData().get("title").toString(),
                                    document.getData().get("contents").toString(),
                                    document.getData().get("user").toString());

                            boardData.add(model);
                        }

                        responseCallback.onDataLoaded("Success", boardData);
                    } else {
                        responseCallback.onDataEmpty();
                    }
                } else {
                    responseCallback.onDataNotAvailable(task.getException().getLocalizedMessage());
                }
            }
        });
    }

    // 아이디어 게시판 특정 글의 정보를 얻어오는 코드.
    // 파이어베이스 버전으로 작성.
    @Override
    public void getPost(String postId, CdkResponseCallback<IdeaPostModel> responseCallback) {
        FirebaseFirestore.getInstance().collection("Idea")
                .document(postId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if(document.exists()) {
                        IdeaPostModel model = new IdeaPostModel();
                        model.setIdeaId(document.getId());
                        model.setNotice((boolean)document.getData().get("isnotice"));
                        model.setIdeaTitle(document.getData().get("title").toString());
                        model.setIdeaContent(document.getData().get("contents").toString());
                        model.setIdeaUser(document.getData().get("user").toString());

                        getUserNameProfile(model.getIdeaUser(), new CdkResponseCallback<String[]>() {
                            @Override
                            public void onDataLoaded(String result, String[] item) {
                                model.setIdeaUserName(item[0]);
                                model.setIdeaUserProfile(item[1]);

                                getFiveComments(postId, new CdkResponseCallback<ArrayList<IdeaCommentModel>>() {
                                    @Override
                                    public void onDataLoaded(String result, ArrayList<IdeaCommentModel> item) {
                                        model.setCommentModels(item);
                                        responseCallback.onDataLoaded("Success", model);
                                    }

                                    @Override
                                    public void onDataNotAvailable(String error) {
                                        responseCallback.onDataNotAvailable(error);
                                    }

                                    @Override
                                    public void onDataEmpty() {
                                        model.setCommentModels(new ArrayList<>());
                                        responseCallback.onDataLoaded("Success", model);
                                    }
                                });
                            }

                            @Override
                            public void onDataNotAvailable(String error) {
                                responseCallback.onDataNotAvailable(error);
                            }

                            @Override
                            public void onDataEmpty() {
                                responseCallback.onDataNotAvailable("No User Info");
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

    // 아이디어 게시판 특정 글의 댓글 상위 5개를 얻어오는 코드.
    // 파이어베이스 버전으로 작성.
    @Override
    public void getFiveComments(String postId, CdkResponseCallback<ArrayList<IdeaCommentModel>> responseCallback) {
        FirebaseFirestore.getInstance().collection("Idea")
                .document(postId).collection("comment")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()) {
                    if (!task.getResult().isEmpty()) {
                        ArrayList<IdeaCommentModel> commentArray = new ArrayList<>();
                        ArrayList<IdeaCommentModel> commentParent = new ArrayList<>();
                        ArrayList<IdeaCommentModel> commentChild = new ArrayList<>();

                        for (QueryDocumentSnapshot document : task.getResult()) {
                            IdeaCommentModel data = new IdeaCommentModel();
                            data.setId(Integer.parseInt(document.getId()));
                            data.setUsername(document.get("user").toString());
                            data.setSeq(((Long)document.get("seq")).intValue());
                            data.setProfile(document.get("profile").toString());
                            data.setParent(((Long)document.get("parent")).intValue());
                            data.setComment(document.get("comment").toString());

                            if(data.getParent() == 0) {
                                commentParent.add(data);
                            } else {
                                commentChild.add(data);
                            }
                        }

                        for(IdeaCommentModel parent : commentParent) {
                            commentArray.add(parent);

                            for(IdeaCommentModel child : commentChild) {
                                if(parent.getId() == child.getParent()) {
                                    commentArray.add(child);
                                }
                            }
                        }

                        ArrayList<IdeaCommentModel> arrayListfinal = new ArrayList<>();
                        arrayListfinal.addAll(commentArray.subList(0,5));

                        responseCallback.onDataLoaded("Success", arrayListfinal);
                    } else {
                        responseCallback.onDataEmpty();
                    }
                } else {
                    responseCallback.onDataNotAvailable(task.getException().getLocalizedMessage());
                }
            }
        });
    }

    // 아이디어 게시판 특정 글의 댓글을 얻어오는 코드.
    // 파이어베이스 버전으로 작성.
    @Override
    public void getAllComments(String postId, CdkResponseCallback<ArrayList<IdeaCommentModel>> responseCallback) {
        FirebaseFirestore.getInstance().collection("Idea")
                .document(postId).collection("comment")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()) {
                    if (!task.getResult().isEmpty()) {
                        ArrayList<IdeaCommentModel> commentArray = new ArrayList<>();
                        ArrayList<IdeaCommentModel> commentParent = new ArrayList<>();
                        ArrayList<IdeaCommentModel> commentChild = new ArrayList<>();

                        for (QueryDocumentSnapshot document : task.getResult()) {
                            IdeaCommentModel data = new IdeaCommentModel();
                            data.setId(Integer.parseInt(document.getId()));
                            data.setUsername(document.get("user").toString());
                            data.setSeq(((Long)document.get("seq")).intValue());
                            data.setProfile(document.get("profile").toString());
                            data.setParent(((Long)document.get("parent")).intValue());
                            data.setComment(document.get("comment").toString());

                            if(data.getParent() == 0) {
                                commentParent.add(data);
                            } else {
                                commentChild.add(data);
                            }
                        }

                        for(IdeaCommentModel parent : commentParent) {
                            commentArray.add(parent);

                            for(IdeaCommentModel child : commentChild) {
                                if(parent.getId() == child.getParent()) {
                                    commentArray.add(child);
                                }
                            }
                        }

                        responseCallback.onDataLoaded("Success", commentArray);
                    } else {
                        responseCallback.onDataEmpty();
                    }
                } else {
                    responseCallback.onDataNotAvailable(task.getException().getLocalizedMessage());
                }
            }
        });
    }

    // 아이디어 게시판 특정 글에 댓글을 등록하는 코드.
    // 파이어베이스 버전으로 작성.
    @Override
    public void uploadComment(String postId, IdeaCommentModel commentModel, CdkResponseCallback<String> responseCallback) {
        Map<String, Object> commentData = new HashMap<>();
        commentData.put("comment", commentModel.getComment());
        commentData.put("parent", commentModel.getParent());
        commentData.put("profile", commentModel.getProfile());
        commentData.put("seq", commentModel.getSeq());
        commentData.put("user", commentModel.getUsername());

        FirebaseFirestore.getInstance().collection("Idea")
                .document(postId).collection("comment").document(""+commentModel.getId()).set(commentData, SetOptions.merge())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        responseCallback.onDataLoaded("Success","");
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                responseCallback.onDataNotAvailable(e.getLocalizedMessage());
            }
        });
    }

    // 아이디어 게시판 특정 글을 삭제하는 코드.
    // 파이어베이스 버전으로 작성.
    @Override
    public void deletePost(String postId, CdkResponseCallback<String> responseCallback) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Idea").document(postId).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()) {
                    responseCallback.onDataLoaded("Success", "");
                } else {
                    responseCallback.onDataNotAvailable(task.getException().getLocalizedMessage());
                }
            }
        });
    }

    // 아이디어 게시판 글을 업로드/수정하는 코드.
    // 파이어베이스 버전으로 작성.
    @Override
    public void uploadPost(String postid, String title, String contents, CdkResponseCallback<String> responseCallback) {
        // 게시글 이름: idea_yyMMdd_HHmmss / team_yyMMdd_HHmmss
        String postidReal;

        if(postid.equals("")) {
            String timeStamp = new SimpleDateFormat("yyMMdd_HHmmss").format(new Date());
            postidReal = "idea_" + timeStamp;
        } else {
            postidReal = postid;
        }

        MyUserModel.getInstance().checkSessionisOpen(new CdkResponseCallback<Boolean>() {
            @Override
            public void onDataLoaded(String result, Boolean item) {
                if(item == Boolean.FALSE) {
                    responseCallback.onDataNotAvailable("");
                } else {
                    FirebaseFirestore db = FirebaseFirestore.getInstance();

                    Map<String, Object> ideaData = new HashMap<>();
                    ideaData.put("title", title);
                    ideaData.put("contents", contents);
                    ideaData.put("user", MyUserModel.getInstance().getUserId());
                    ideaData.put("isnotice", false);

                    db.collection("Idea")
                            .document(postidReal).set(ideaData, SetOptions.merge())
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void avoid) {
                                    responseCallback.onDataLoaded("Success", postidReal);
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    responseCallback.onDataNotAvailable(e.getLocalizedMessage());
                                }
                            });
                }
            }

            @Override
            public void onDataNotAvailable(String error) {
                responseCallback.onDataNotAvailable(error);
            }

            @Override
            public void onDataEmpty() {
                responseCallback.onDataNotAvailable("");
            }
        });
    }

    // 내부함수.
    // 아이디어 게시판 용: 유저의 프로필사진과 이름을 가져오는 코드.
    private void getUserNameProfile(String userid, CdkResponseCallback<String[]> responseCallback) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("UserCeo").document(userid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if(document.exists()) {
                        String[] results = new String[2];
                        results[0] = document.get("username").toString();
                        results[1] = document.get("profileimage").toString();

                        responseCallback.onDataLoaded("Success",results);
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
