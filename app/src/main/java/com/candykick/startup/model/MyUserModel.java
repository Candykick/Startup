package com.candykick.startup.model;

// 내 유저 데이터는 싱글톤으로 생성함.

import android.support.annotation.NonNull;

import com.candykick.startup.engine.CdkResponseCallback;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.kakao.auth.Session;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.MeV2ResponseCallback;
import com.kakao.usermgmt.response.MeV2Response;

public class MyUserModel {
    private String userId;
    private String userName;
    private String userEmail;
    private String chatid;
    private String profileImage;
    private long userType;

    private static MyUserModel myUserModel = new MyUserModel();

    public static MyUserModel getInstance() { return myUserModel; }

    private MyUserModel() { }

    // 세션이 열려있는지 체크하는 코드.
    public void checkSessionisOpen(CdkResponseCallback<Boolean> responseCallback) {
        if(Session.getCurrentSession().isOpened()) {
            responseCallback.onDataLoaded("Success", Boolean.TRUE);
        } else if(FirebaseAuth.getInstance().getCurrentUser() != null) {
            responseCallback.onDataLoaded("Success", Boolean.TRUE);
        } else {
            responseCallback.onDataLoaded("Success", Boolean.FALSE);
        }
    }

    // 유저 정보를 셋팅하는 코드.
    public void setUserData(CdkResponseCallback<String> responseCallback) {
        if(Session.getCurrentSession().isOpened()) {
            UserManagement.getInstance().me(new MeV2ResponseCallback() {
                @Override
                public void onSessionClosed(ErrorResult errorResult) {
                    responseCallback.onDataNotAvailable(errorResult.getErrorMessage());
                }

                @Override
                public void onSuccess(MeV2Response result) {
                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    db.collection("UserCeo").document("" + result.getId()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if (document.exists()) {
                                    userId = document.getId();
                                    userName = document.get("username").toString();
                                    userEmail = "";
                                    chatid = document.get("chatid").toString();
                                    profileImage = document.get("profileimage").toString();
                                    userType = (long)document.get("usertype");
                                    responseCallback.onDataLoaded("", "");
                                } else {
                                    responseCallback.onDataEmpty();
                                }
                            }
                        }
                    });
                }
            });
        } else if(FirebaseAuth.getInstance().getCurrentUser() != null) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("UserCeo").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            userId = document.getId();
                            userName = document.get("username").toString();
                            userEmail = "";
                            chatid = document.get("chatid").toString();
                            profileImage = document.get("profileimage").toString();
                            userType = (long)document.get("usertype");
                            responseCallback.onDataLoaded("", "");
                        } else {
                            responseCallback.onDataEmpty();
                        }
                    } else {
                        responseCallback.onDataNotAvailable(task.getException().getLocalizedMessage());
                    }
                }
            });
        } else {
            responseCallback.onDataEmpty();
        }
    }

    public String getUserId() {
        return userId;
    }
    public String getUserName() {
        return userName;
    }
    public String getUserEmail() {
        return userEmail;
    }
    public String getChatid() {
        return chatid;
    }
    public String getProfileImage() {
        return profileImage;
    }
    public long getUserType() {
        return userType;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
    public void setUserName(String userName) {
        this.userName = userName;
    }
    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }
    public void setChatid(String chatid) {
        this.chatid = chatid;
    }
    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }
    public void setUserType(long userType) {
        this.userType = userType;
    }
}
