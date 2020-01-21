package com.candykick.startup.view.talkAct;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

import com.candykick.startup.R;
import com.candykick.startup.engine.CdkEmptyCallback;
import com.candykick.startup.engine.CdkResponseCallback;
import com.candykick.startup.engine.ChatEngine;
import com.candykick.startup.model.ChatMessageModel;
import com.candykick.startup.model.ChatUserModel;
import com.candykick.startup.view.base.BaseActivity;
import com.candykick.startup.databinding.ActivityChattingBinding;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/*
2019.07.11 목: 1차 채팅 기능.

made by Candykick with Firebase Realtime Database.

chat - [userId1],[userId2] - {userName1} {userProfile1}
                             {userName2} {userProfile2}
                             [chatId] - {message},
                                        {flag: int형식. userName1이 보낸 거면 1, userName2가 보낸 거면 2.},
                                        {messageDate: long형식. currentTimeMills() 그대로 전송}

 */

public class ChattingActivity extends BaseActivity<ActivityChattingBinding> {

    ChattingAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding.setActivity(this);

        //유저 정보를 ChatEngine에서 받아오고 이를 셋팅한다.
        ChatEngine chatEngine = ChatEngine.getInstance();
        binding.toolbar.tvToolBar.setText(chatEngine.getOtherModel().getUserName());
        binding.toolbar.btnToolBarLeft.setOnClickListener(v -> onBackPressed());

        //채팅방 셋팅
        chatEngine.openChat(new CdkResponseCallback<ChatMessageModel>() {
            @Override
            public void onDataLoaded(String result, ChatMessageModel item) {
                binding.llChattingNoChat.setVisibility(View.GONE);
                binding.lvChatting.setVisibility(View.VISIBLE);

                binding.lvChatting.setSelection(adapter.getCount() - 1);
                adapter.addData(item);
            }

            @Override
            public void onDataNotAvailable(String error) {

            }

            @Override
            public void onDataEmpty() {

            }
        });
    }

    //메세지 전송 버튼 클릭 시
    public void btnSend() {
        String message = binding.etChatting.getText().toString();
        String messageTest = message.replace(" ","");

        if(messageTest.length() == 0) { //띄어쓰기를 제외한 글자가 있는지 검사. 아무것도 안 쓰거나, 띄어쓰기만 겁나게 쓰는 경우 메세지 전송 X.
            makeToast("메세지를 입력해주세요.");
            binding.etChatting.setText("");
        } else {
            ChatMessageModel chat = new ChatMessageModel(message, System.currentTimeMillis(), ChatEngine.getInstance().getMyFlag(), true);
            ChatEngine.getInstance().sendMessage(chat, new CdkEmptyCallback() {
                @Override
                public void onSuccessed() {
                    binding.etChatting.setText("");
                }

                @Override
                public void onFailed(String error) {
                    makeToast(resources.getString(R.string.toastCannotSendMessage)+error);
                }
            });
        }
    }

    @Override
    public int getLayoutId() { return R.layout.activity_chatting; }
}
