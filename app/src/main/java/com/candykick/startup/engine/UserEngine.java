package com.candykick.startup.engine;

//싱글톤 객체로 생성

import android.net.Uri;
import android.support.annotation.NonNull;

import com.candykick.startup.model.MyUserModel;
import com.candykick.startup.model.UserAdvModel;
import com.candykick.startup.model.UserCeoModel;
import com.candykick.startup.model.UserInvModel;
import com.candykick.startup.model.UserOutModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.kakao.auth.ApiResponseCallback;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.MeV2ResponseCallback;
import com.kakao.usermgmt.response.MeV2Response;

import java.util.HashMap;
import java.util.Map;

public class UserEngine implements UserEngineInterface {
    private static UserEngine userEngine = new UserEngine();

    public static UserEngine getInstance() { return userEngine; }
    private UserEngine() { }

    @Override
    public void registerUser(String loginMethod, Map<String, Object> userInfo, String password, CdkEmptyCallback responseCallback) {
        if(loginMethod.equals("email")) {
            //2. Firebase에 이메일로 회원가입
            FirebaseAuth.getInstance().createUserWithEmailAndPassword(userInfo.get("email").toString(), password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                final FirebaseUser tmpuser = FirebaseAuth.getInstance().getCurrentUser();

                                //3. Firebase에 가입한 유저의 정보 새로 업데이트
                                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                        .setDisplayName(userInfo.get("username").toString())
                                        .setPhotoUri(Uri.parse(userInfo.get("profileimage").toString()))
                                        .build();

                                tmpuser.updateProfile(profileUpdates)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    uploadUserInfo(tmpuser.getUid(), userInfo, new CdkEmptyCallback() {
                                                        @Override
                                                        public void onSuccessed() {
                                                            responseCallback.onSuccessed();
                                                        }

                                                        @Override
                                                        public void onFailed(String error) {
                                                            responseCallback.onFailed(error);
                                                        }
                                                    });
                                                } else {
                                                    responseCallback.onFailed(task.getException().getLocalizedMessage());
                                                }
                                            }
                                        });
                            }
                        }
                    });
        } else if(loginMethod.equals("google")) {
            final FirebaseUser tmpuser = FirebaseAuth.getInstance().getCurrentUser();

            //2. Firebase에 가입한 유저의 정보 새로 업데이트
            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                    .setDisplayName(userInfo.get("username").toString())
                    .setPhotoUri(Uri.parse(userInfo.get("profileimage").toString()))
                    .build();

            tmpuser.updateProfile(profileUpdates)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                uploadUserInfo(tmpuser.getUid(), userInfo, new CdkEmptyCallback() {
                                    @Override
                                    public void onSuccessed() {
                                        responseCallback.onSuccessed();
                                    }

                                    @Override
                                    public void onFailed(String error) {
                                        responseCallback.onFailed(error);
                                    }
                                });
                            } else {
                                responseCallback.onFailed(task.getException().getLocalizedMessage());
                            }
                        }
                    });
        } else if(loginMethod.equals("kakao")) {
            //카카오 등록 순서: 프로필 사진 업로드 -> 업로드 성공 시 카카오API의 유저 정보 수정 -> FireStore에 유저 정보 업로드
            //2. 업로드 성공 시 카카오API의 유저 정보 수정
            final Map<String, String> properties = new HashMap<>();
            properties.put("nickname", userInfo.get("username").toString());
            properties.put("profile_image", userInfo.get("profileimage").toString());
            properties.put("email", userInfo.get("email").toString());

            UserManagement.getInstance().me(new MeV2ResponseCallback() {
                @Override
                public void onSessionClosed(ErrorResult errorResult) {
                    responseCallback.onFailed(errorResult.getErrorMessage());
                }

                @Override
                public void onSuccess(MeV2Response meV2Response) {
                    UserManagement.getInstance().requestUpdateProfile(new ApiResponseCallback<Long>() {
                        @Override
                        public void onSessionClosed(ErrorResult errorResult) {
                            responseCallback.onFailed(errorResult.getErrorMessage());
                        }

                        @Override
                        public void onNotSignedUp() {
                            responseCallback.onFailed("onNotSignedUp");
                        }

                        @Override
                        public void onSuccess(Long result) {
                            //3. FireStore에 유저 정보 업로드
                            uploadUserInfo(""+meV2Response.getId(), userInfo, new CdkEmptyCallback() {
                                @Override
                                public void onSuccessed() {
                                    responseCallback.onSuccessed();
                                }

                                @Override
                                public void onFailed(String error) {
                                    responseCallback.onFailed(error);
                                }
                            });
                        }
                    }, properties);
                }
            });
        } else {
            responseCallback.onFailed("methodErr");
        }
    }

    private void uploadUserInfo(String userId, Map<String, Object> userInfo, CdkEmptyCallback responseCallback) {
        FirebaseFirestore.getInstance().collection("UserCeo")
                .document(userId).set(userInfo)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void avoid) {
                        MyUserModel.getInstance().setUserData(new CdkResponseCallback<String>() {
                            @Override
                            public void onDataLoaded(String result, String item) {
                                responseCallback.onSuccessed();
                            }

                            @Override
                            public void onDataNotAvailable(String error) {
                                responseCallback.onFailed(error);
                            }

                            @Override
                            public void onDataEmpty() {
                                responseCallback.onFailed("");
                            }
                        });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        responseCallback.onFailed(e.getLocalizedMessage());
                    }
                });
    }


}

/*
if (loginmethod.equals("email")) {
                                        //2. Firebase에 이메일로 회원가입
                                        emailAuth.createUserWithEmailAndPassword(email, password)
                                                .addOnCompleteListener(JoinCeoActivity.this, new OnCompleteListener<AuthResult>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                                        if (task.isSuccessful()) {
                                                            final FirebaseUser tmpuser = emailAuth.getCurrentUser();

                                                            //3. Firebase에 가입한 유저의 정보 새로 업데이트
                                                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                                                    .setDisplayName(nickname)
                                                                    .setPhotoUri(Uri.parse(profileResult))
                                                                    .build();

                                                            tmpuser.updateProfile(profileUpdates)
                                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                            if (task.isSuccessful()) {
                                                                                FirebaseFirestore db = FirebaseFirestore.getInstance();

                                                                                db.collection("UserCeo")
                                                                                        .document(tmpuser.getUid()).set(user)
                                                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                            @Override
                                                                                            public void onSuccess(Void avoid) {
                                                                                                progressOff();
                                                                                                SharedPreferences sf = getSharedPreferences("userType",MODE_PRIVATE);
                                                                                                SharedPreferences.Editor editor = sf.edit();
                                                                                                editor.putLong("userType",0);
                                                                                                editor.commit();

                                                                                                Intent intent = new Intent(JoinCeoActivity.this, MainActivity.class);
                                                                                                //intent.putExtra("result", strResult);
                                                                                                //intent.putExtra("loginmethod", loginmethod);
                                                                                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                                                                                startActivity(intent);
                                                                                                finish();
                                                                                            }
                                                                                        })
                                                                                        .addOnFailureListener(new OnFailureListener() {
                                                                                            @Override
                                                                                            public void onFailure(@NonNull Exception e) {
                                                                                                progressOff();
                                                                                                makeToastLong("유저 정보를 등록하는 도중 오류가 발생했습니다. 오류: " + e.toString());
                                                                                            }
                                                                                        });
                                                                            } else {
                                                                                progressOff();
                                                                                makeToastLong("회원가입에 실패했습니다. 다시 시도해주세요: " + task.getException().getMessage());
                                                                            }
                                                                        }
                                                                    });
                                                        } else {
                                                            progressOff();
                                                            makeToastLong("회원가입에 실패했습니다. 다시 시도해주세요: " + task.getException().getMessage());
                                                        }
                                                    }
                                                });
                                    } else if (loginmethod.equals("google")) {
                                        final FirebaseUser tmpuser = emailAuth.getCurrentUser();

                                        //2. Firebase에 가입한 유저의 정보 새로 업데이트
                                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                                .setDisplayName(nickname)
                                                .setPhotoUri(Uri.parse(profileResult))
                                                .build();

                                        tmpuser.updateProfile(profileUpdates)
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            FirebaseFirestore db = FirebaseFirestore.getInstance();

                                                            db.collection("UserCeo")
                                                                    .document(tmpuser.getUid()).set(user)
                                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                        @Override
                                                                        public void onSuccess(Void aVoid) {
                                                                            //String userInfo = "유저이름: "+tmpuser.getDisplayName()+"\n유저 이메일: "+tmpuser.getEmail()+"\n유저 프로필: "+tmpuser.getPhotoUrl().toString()+"\n";
                                                                            //strResult = userInfo+strResult;

                                                                            progressOff();
                                                                            SharedPreferences sf = getSharedPreferences("userType",MODE_PRIVATE);
                                                                            SharedPreferences.Editor editor = sf.edit();
                                                                            editor.putLong("userType",0);
                                                                            editor.commit();

                                                                            Intent intent = new Intent(JoinCeoActivity.this, MainActivity.class);
                                                                            //intent.putExtra("result", strResult);
                                                                            //intent.putExtra("loginmethod", loginmethod);
                                                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                                                            startActivity(intent);
                                                                            finish();
                                                                        }
                                                                    })
                                                                    .addOnFailureListener(new OnFailureListener() {
                                                                        @Override
                                                                        public void onFailure(@NonNull Exception e) {
                                                                            progressOff();
                                                                            makeToastLong("유저 정보를 등록하는 도중 오류가 발생했습니다. 오류: " + e.toString());
                                                                        }
                                                                    });
                                                        } else {
                                                            progressOff();
                                                            makeToastLong("회원가입에 실패했습니다. 다시 시도해주세요: " + task.getException().getMessage());
                                                        }
                                                    }
                                                });
                                    } else {
                                        //카카오 등록 순서: 프로필 사진 업로드 -> 업로드 성공 시 카카오API의 유저 정보 수정 -> FireStore에 유저 정보 업로드
                                        //2. 업로드 성공 시 카카오API의 유저 정보 수정
                                        final Map<String, String> properties = new HashMap<>();
                                        properties.put("nickname", nickname);
                                        properties.put("profile_image", profileResult);
                                        properties.put("email", email);

                                        UserManagement.getInstance().requestUpdateProfile(new ApiResponseCallback<Long>() {
                                            @Override
                                            public void onSessionClosed(ErrorResult errorResult) {
                                                progressOff();
                                                makeToast("에러가 발생했습니다. 다시 시도해 주세요: " + errorResult.toString());
                                            }

                                            @Override
                                            public void onNotSignedUp() {
                                                progressOff();
                                                makeToast("에러가 발생했습니다. 다시 시도해 주세요: onNotSignedUp()");
                                            }

                                            @Override
                                            public void onSuccess(Long result) {
                                                //3. FireStore에 유저 정보 업로드
                                                FirebaseFirestore db = FirebaseFirestore.getInstance();

                                                db.collection("UserCeo")
                                                        .document(uid).set(user)
                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void aVoid) {
                                                                //String userInfo = "유저이름: "+tmpuser.getDisplayName()+"\n유저 이메일: "+tmpuser.getEmail()+"\n유저 프로필: "+tmpuser.getPhotoUrl().toString()+"\n";
                                                                //strResult = userInfo+strResult;

                                                                progressOff();
                                                                SharedPreferences sf = getSharedPreferences("userType",MODE_PRIVATE);
                                                                SharedPreferences.Editor editor = sf.edit();
                                                                editor.putLong("userType",0);
                                                                editor.commit();

                                                                Intent intent = new Intent(JoinCeoActivity.this, MainActivity.class);
                                                                //intent.putExtra("result", strResult);
                                                                //intent.putExtra("loginmethod", loginmethod);
                                                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                                                startActivity(intent);
                                                                finish();
                                                            }
                                                        })
                                                        .addOnFailureListener(new OnFailureListener() {
                                                            @Override
                                                            public void onFailure(@NonNull Exception e) {
                                                                progressOff();
                                                                makeToastLong("유저 정보를 등록하는 도중 오류가 발생했습니다. 오류: " + e.toString());
                                                            }
                                                        });
                                            }
                                        }, properties);
                                    }
 */