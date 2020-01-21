package com.candykick.startup.view.talkAct;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import com.candykick.startup.R;
import com.candykick.startup.engine.CdkResponseCallback;
import com.candykick.startup.engine.ChatEngine;
import com.candykick.startup.model.ChatListModel;
import com.candykick.startup.util.DateUtility;
import com.candykick.startup.view.base.BaseActivity;
import com.candykick.startup.databinding.ActivityChatMainBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.kakao.auth.Session;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.MeV2ResponseCallback;
import com.kakao.usermgmt.response.MeV2Response;

import java.util.ArrayList;

/*
2019.07.11 목: 1차 채팅 기능.

made by Candykick with Firebase Realtime Database.

chat - [userId1],[userId2] - [chatId] - {message},
                                        {flag: int형식. userName1이 보낸 거면 1, userName2가 보낸 거면 2.},
                                        {messageDate: long형식. currentTimeMills() 그대로 전송}

* 가장 최근 메세지는 가장 최근의 [chatId]를 읽어오는 방식으로. 근데 이거, 가능한건가...?
* 사실 더 좋은 방법이 있을 거라고 보는데, 어차피 임시방편이니 더 머리 쓰는 게 귀찮음.
* Realtime Database 말고 Cloud Firestore를 쓸 수 있을 거 같음. 방법은 모름 ㅎ
* Cloud Firestore로 작업할 경우, 색인 기능을 활용해서 훨씬 효율적인 작업이 가능할 것으로 보임.

 */

public class ChatMainActivity extends BaseActivity<ActivityChatMainBinding> {

    // 현재 파이어베이스 버전 채팅방은 adapter에 차례차례로 데이터를 더하는 방식으로 되어 있어서,
    // 기존 방식으로 작업하는 데에 어려움이 있다.
    // 아래의 roomIds는 나중에 ArrayList<ChstListModel>로 바꿀 것.
    ArrayList<String> roomIds = new ArrayList<>();
    ArrayList<String> receivers = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding.setActivity(this);

        binding.toolbar.tvToolBar.setText(getResources().getString(R.string.strChatMainActivityTitle));
        binding.toolbar.btnToolBarRight.setImageResource(R.drawable.ic_chat_bubble_outline_black_24dp);
        binding.toolbar.btnToolBarRight.setOnClickListener(v -> {
            Intent intent = new Intent(ChatMainActivity.this, UserSearchActivity.class);
            startActivity(intent);
        });
        binding.toolbar.btnToolBarLeft.setOnClickListener(v -> onBackPressed());
    }

    @Override
    protected void onResume() {
        super.onResume();

        //채팅방 보여주는 리스트 셋팅
        setChatMainList();
    }

    //채팅방 보여주는 리스트 셋팅
    private void setChatMainList() {
        ArrayList<ChatListModel> arrayList = new ArrayList<>();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ChatMainActivity.this);
        binding.rvChatMain.setLayoutManager(linearLayoutManager);
        final ChatMainListAdapter adapter = new ChatMainListAdapter(ChatMainActivity.this, arrayList);
        binding.rvChatMain.setAdapter(adapter);

        progressOn();

        ChatEngine.getInstance().getChatListData(new CdkResponseCallback<ChatListModel>() {
            @Override
            public void onDataLoaded(String result, ChatListModel item) {
                binding.rvChatMain.setVisibility(View.VISIBLE);
                binding.llChatMainNoChat.setVisibility(View.GONE);
                binding.llChatMainAddChat.setVisibility(View.GONE);

                roomIds.add(item.getRoomId());
                receivers.add(item.getUserId());
                adapter.addData(item);
                adapter.notifyDataSetChanged();

                binding.rvChatMain.setOnItemClickListener((parentView, view, position, id) -> {
                    Intent intent = new Intent(ChatMainActivity.this, ChattingActivity.class);
                    intent.putExtra("user",receivers.get(position));
                    intent.putExtra("roomId",roomIds.get(position));
                    startActivity(intent);
                });

                progressOff();
            }

            @Override
            public void onDataNotAvailable(String error) {
                progressOff();
                makeToast(getResources().getString(R.string.toastUserInfoErr) + error);
                finish();
            }

            @Override
            public void onDataEmpty() {
                progressOff();
                makeToast(getResources().getString(R.string.toastCallInfoErr));
                finish();
            }
        });
    }

    @Override
    public int getLayoutId() { return R.layout.activity_chat_main; }
}
