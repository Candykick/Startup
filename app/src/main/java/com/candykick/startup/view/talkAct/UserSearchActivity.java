package com.candykick.startup.view.talkAct;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;

import com.candykick.startup.R;
import com.candykick.startup.engine.CdkResponseCallback;
import com.candykick.startup.engine.ChatEngine;
import com.candykick.startup.model.ChatUserModel;
import com.candykick.startup.view.base.BaseActivity;
import com.candykick.startup.databinding.ActivityUserSearchBinding;
import com.candykick.startup.view.loginAccount.StartActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.kakao.auth.Session;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.MeV2ResponseCallback;
import com.kakao.usermgmt.response.MeV2Response;

import java.util.ArrayList;

public class UserSearchActivity extends BaseActivity<ActivityUserSearchBinding> {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding.setActivity(this);
        binding.toolbar.btnToolBarLeft3.setOnClickListener(v -> onBackPressed());
        binding.toolbar.tvToolBar3.setText(resources.getString(R.string.strUserSearchActivityTitle));
        binding.toolbar.btnToolBarRight3.setOnClickListener(v -> searchUser());
    }

    // 유저 검색 버튼을 눌렀을 때
    private void searchUser() {
        if(binding.etUserSearch.getText().toString().replace(" ","").length() == 0) {
            binding.etUserSearch.setText("");
            makeToastLong(resources.getString(R.string.toastPleaseInputChatid));
        } else if(binding.etUserSearch.getText().toString().replace(" ","").length() < 3) {
            makeToastLong(resources.getString(R.string.toastChatidLengthErr));
        } else {
            ChatEngine.getInstance().searchUserByChatid(binding.etUserSearch.getText().toString(), new CdkResponseCallback<ArrayList<ChatUserModel>>() {
                @Override
                public void onDataLoaded(String result, ArrayList<ChatUserModel> item) {
                    if(item.size() == 0) {
                        binding.lvUserSearch.setVisibility(View.GONE);
                        binding.llUserSearch.setVisibility(View.VISIBLE);
                        binding.tvUserSearchToast.setText(resources.getString(R.string.toastNoUserByChatid));
                    } else {
                        binding.lvUserSearch.setVisibility(View.VISIBLE);
                        binding.llUserSearch.setVisibility(View.GONE);

                        ChatUserListAdapter adapter = new ChatUserListAdapter(UserSearchActivity.this, item);
                        binding.lvUserSearch.setAdapter(adapter);

                        binding.lvUserSearch.setOnItemClickListener((parentView, view, position, id) -> {
                            progressOn();

                            ChatEngine.getInstance().enterChatRoom(item.get(position), new CdkResponseCallback<String>() {
                                @Override
                                public void onDataLoaded(String result, String item) {
                                    progressOff();
                                    Intent intent = new Intent(UserSearchActivity.this, ChattingActivity.class);
                                    startActivity(intent);
                                }

                                @Override
                                public void onDataNotAvailable(String error) {
                                    progressOff();
                                    makeToastLong(getResources().getString(R.string.toastErr) + error);
                                }

                                @Override
                                public void onDataEmpty() {
                                    progressOff();
                                    makeToastLong(getResources().getString(R.string.toastErr));
                                }
                            });
                            /*FirebaseDatabase database = FirebaseDatabase.getInstance();
                            DatabaseReference reference = database.getReference();
                            reference.child("chat").addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                        if (snapshot.getKey().contains(myData.userId) & snapshot.getKey().contains(arrayList.get(position).userId)) {
                                            roomId = snapshot.getKey();
                                        }
                                    }

                                    if (roomId == null) {
                                        roomId = myData.userId + "," + arrayList.get(position).userId;
                                        Log.d("Startup 2", roomId);
                                    }

                                    progressOff();
                                    Intent intent = new Intent(UserSearchActivity.this, ChattingActivity.class);
                                    intent.putExtra("myData", myData); //ChatUserData
                                    intent.putExtra("user", arrayList.get(position)); //ChatUserData
                                    intent.putExtra("roomId", roomId);
                                    startActivity(intent);
                                    finish();
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                    if (roomId == null) {
                                        roomId = myData.userId + "," + arrayList.get(position).userId;
                                        Log.d("Startup 2", roomId);
                                    }

                                    progressOff();
                                    Intent intent = new Intent(UserSearchActivity.this, ChattingActivity.class);
                                    intent.putExtra("myData", myData); //ChatUserData
                                    intent.putExtra("user", arrayList.get(position)); //ChatUserData
                                    intent.putExtra("roomId", roomId);
                                    startActivity(intent);
                                    finish();
                                }
                            });*/
                        });
                    }
                }

                @Override
                public void onDataNotAvailable(String error) {
                    makeToastLong(resources.getString(R.string.toastUserInfoErr)+error);
                }

                @Override
                public void onDataEmpty() { //Empty도 세션 닫힌 걸로 간주하고 처리.
                    makeToastLong(resources.getString(R.string.toastUserInfoErr));
                }
            });
        }
    }

    @Override
    public int getLayoutId() { return R.layout.activity_user_search; }
}
