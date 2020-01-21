package com.candykick.startup.engine;

import com.candykick.startup.model.ChatListModel;
import com.candykick.startup.model.ChatMessageModel;
import com.candykick.startup.model.ChatUserModel;

import java.util.ArrayList;

public interface ChatEngineInterface {
    // 채팅방 리스트 데이터를 가져오는 코드.
    // 파이어베이스 버전.
    // 자체 API 버전으로 바꿀 때는 채팅 목록 중 계정을 삭제한 유저가 있다면, 그 유저와의 채팅내역은 삭제하도록 처리해야 한다.
    // Input: (userId), Output: ChatListModel (ArrayList<ChatListModel>)
    void getChatListData(CdkResponseCallback<ChatListModel> responseCallback);

    // 유저를 채팅id로 검색하는 코드.
    // 파이어베이스 버전.
    // Input: chatid, Output: ArrayList<ChatUserModel>
    void searchUserByChatid(String chatid, CdkResponseCallback<ArrayList<ChatUserModel>> responseCallback);

    // ChatEngine에 채팅방 정보를 셋팅하는 코드.(채팅방에 들어가는 코드.)
    // 들어가기 전에 해당 채팅방이 존재하는지를 먼저 확인한다.
    // 파이어베이스 버전.
    // Input: ChatUserModel(상대 정보+내 정보), Output: X
    void enterChatRoom(ChatUserModel otherModel1, CdkEmptyCallback responseCallback);

    // ChatEngine에 있는 정보를 바탕으로 채팅방의 정보를 가져오는 코드. (채팅방 셋팅)
    // 파이어베이스 버전.
    // Input: X, Output: ChatMessageModel (ArrayList<ChatMessageModel>)
    void openChat(CdkResponseCallback<ChatMessageModel> emptyCallback);

    // 채팅방에서 메세지를 전송하는 코드.
    // 파이어베이스 버전.
    // Input: ChatMessageModel, Output: X
    void sendMessage(ChatMessageModel chatMessage, CdkEmptyCallback responseCallback);

    // 채팅방에서 사진을 전송하는 코드.
    // 파이어베이스 버전.
    // void sendPhotos(ChatMessageModel chatMessage, CdkResponseCallback<String> responseCallback);

    // 채팅방에서 파일을 전송하는 코드.
    // 파이어베이스 버전.
    // void sendFiles(ChatMessageModel chatMessage, CdkResponseCallback<String> responseCallback);
}
