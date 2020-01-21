package com.candykick.startup.engine;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.candykick.startup.model.ChatListModel;
import com.candykick.startup.model.ChatMessageModel;
import com.candykick.startup.model.ChatUserModel;
import com.candykick.startup.model.MyUserModel;
import com.candykick.startup.util.DateUtility;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class ChatEngine implements ChatEngineInterface {
    private String roomId;
    private ChatUserModel myModel;
    private ChatUserModel otherModel;
    private int myFlag; //송신자 Flag. 1이거나 2임.

    private static ChatEngine chatEngine = new ChatEngine();

    public static ChatEngine getInstance() { return chatEngine; }

    private ChatEngine() { }

    // 채팅방 리스트 데이터를 가져오는 코드.
    // 파이어베이스 버전.
    // 자체 API 버전으로 바꿀 때는 채팅 목록 중 계정을 삭제한 유저가 있다면, 그 유저와의 채팅내역은 삭제하도록 처리해야 한다.
    @Override
    public void getChatListData(CdkResponseCallback<ChatListModel> responseCallback) {
        // 세션이 열려있는지 체크하고, 열려 있다면 내 ID값을 가져온다.
        MyUserModel.getInstance().checkSessionisOpen(new CdkResponseCallback<Boolean>() {
            @Override
            public void onDataLoaded(String result, Boolean item) {
                if(item == Boolean.TRUE) {
                    // 세션이 열려 있다면 내 정보를 바탕으로 한 채팅방 리스트를 가져온다.
                    String currentUserId = MyUserModel.getInstance().getUserId();

                    /*SharedPreferences sf = getSharedPreferences("userType",MODE_PRIVATE);
                    SharedPreferences.Editor editor = sf.edit();
                    editor.putBoolean("isTalkActOpened",true);
                    editor.apply();*/

                    ArrayList<ChatListModel> chatListModels = new ArrayList<>();

                    FirebaseDatabase.getInstance().getReference().child("chat").addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                            // 채팅방 ID에 현재 로그인한 유저의 ID값이 포함되어 있으면: 해당 유저가 들어가 있는 채팅방이라는 소리!
                            String dataSnapshotKey = dataSnapshot.getKey();
                            String[] userIds = dataSnapshotKey.split(",");

                            if(dataSnapshotKey.contains(currentUserId)) {
                                String receiverId = (userIds[0].equals(currentUserId))?userIds[1]:userIds[0];
                                int receiverFlag = (userIds[0].equals(currentUserId))?2:1;

                                DocumentReference docRef = FirebaseFirestore.getInstance().collection("UserCeo").document(receiverId);
                                docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if(task.isSuccessful()) {
                                            DocumentSnapshot document = task.getResult();
                                            if(document.exists()) {
                                                //가장 최근 채팅 정보를 읽어옴
                                                int notreadMessage = 0;
                                                ChatMessageModel recentChat = new ChatMessageModel();
                                                for(DataSnapshot child : dataSnapshot.getChildren()) {
                                                    recentChat = child.getValue(ChatMessageModel.class);
                                                    if(receiverFlag==recentChat.getFlag() & recentChat.isNotread()) {
                                                        notreadMessage++;
                                                    }
                                                }
                                                //recentData를 보기 좋은 형식으로 수정함.
                                                DateUtility dateUtility = new DateUtility(recentChat.getMessageDate());

                                                //adapter에 새로운 채팅방을 추가함
                                                ChatListModel chatUser = new ChatListModel(dataSnapshotKey, receiverId, (String)document.get("username"), recentChat.getMessage(), (String)document.get("profileimage"), dateUtility.DateResultType1(), notreadMessage);
                                                responseCallback.onDataLoaded("Success", chatUser);
                                            } else {
                                                responseCallback.onDataNotAvailable(task.getException().getLocalizedMessage());
                                            }
                                        } else {
                                            responseCallback.onDataNotAvailable(task.getException().getLocalizedMessage());
                                        }
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        responseCallback.onDataNotAvailable(e.getLocalizedMessage());
                                    }
                                });
                            }
                        }

                        @Override
                        public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                        }

                        @Override
                        public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                        }

                        @Override
                        public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                } else {
                    responseCallback.onDataEmpty();
                }
            }

            @Override
            public void onDataNotAvailable(String error) {
                responseCallback.onDataNotAvailable(error);
            }

            @Override
            public void onDataEmpty() {
                responseCallback.onDataEmpty();
            }
        });
    }

    // 유저를 채팅id로 검색하는 코드.
    // 파이어베이스 버전.
    @Override
    public void searchUserByChatid(String chatid, CdkResponseCallback<ArrayList<ChatUserModel>> responseCallback) {
        MyUserModel.getInstance().checkSessionisOpen(new CdkResponseCallback<Boolean>() {
            @Override
            public void onDataLoaded(String result, Boolean item) {
                if(item == Boolean.TRUE) {
                    ArrayList<ChatUserModel> chatUserModels = new ArrayList<>();

                    //세션 열림
                    FirebaseFirestore.getInstance()
                            .collection("UserCeo").get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if(task.isSuccessful()) {
                                        for(QueryDocumentSnapshot document: task.getResult()) {
                                            ChatUserModel data = new ChatUserModel(document.getId(),
                                                    document.getData().get("username").toString(),
                                                    (long)document.getData().get("usertype"),
                                                    document.getData().get("profileimage").toString());

                                            //본인을 제외한 유저 정보이고, 채팅id가 검색한 값을 포함하고 있으면 arrayList에 추가
                                            if(!data.getUserId().equals(MyUserModel.getInstance().getUserId())
                                                    && document.getData().get("chatid").toString().contains(chatid)) {
                                                chatUserModels.add(data);
                                            }/* else if(data.userId.equals(""+result.getId())) {
                                                myData = data;
                                            }*/ //내 데이터는 이미 셋팅되어 있으므로 문제 없음.
                                        }

                                        responseCallback.onDataLoaded("Success", chatUserModels);
                                    } else {
                                        responseCallback.onDataNotAvailable(task.getException().getLocalizedMessage());
                                    }
                                }
                            });
                } else {
                    //세션 닫힘
                    responseCallback.onDataEmpty();
                }
            }

            @Override
            public void onDataNotAvailable(String error) {
                //세션 닫힘
                responseCallback.onDataNotAvailable(error);
            }

            @Override
            public void onDataEmpty() {
                //세션 닫힘
                responseCallback.onDataEmpty();
            }
        });
    }

    // ChatEngine에 채팅방 정보를 셋팅하는 코드.(채팅방에 들어가는 코드.)
    // 들어가기 전에 해당 채팅방이 존재하는지를 먼저 확인한다.
    // 파이어베이스 버전.
    @Override
    public void enterChatRoom(ChatUserModel otherModel1, CdkEmptyCallback responseCallback) {
        MyUserModel myUserModel = MyUserModel.getInstance();
        myModel = new ChatUserModel(myUserModel.getUserId(), myUserModel.getUserName(), myUserModel.getUserType(), myUserModel.getProfileImage());
        otherModel = otherModel1;

        FirebaseDatabase.getInstance().getReference().child("chat").addListenerForSingleValueEvent(new ValueEventListener() {
            String tmpRoomId;

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot: dataSnapshot.getChildren()) {
                    if(snapshot.getKey().contains(myModel.getUserId()) & snapshot.getKey().contains(otherModel.getUserId())) {
                        tmpRoomId = snapshot.getKey();
                        roomId = snapshot.getKey();
                        String[] userIds = tmpRoomId.split(",");
                        myFlag = (userIds[0].equals(myModel.getUserId()))?1:2;
                    }
                }

                if(tmpRoomId == null) {
                    roomId = myModel.getUserId()+","+otherModel.getUserId();
                    myFlag = 1;
                }

                responseCallback.onSuccessed();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                if(tmpRoomId == null) {
                    roomId = myModel.getUserId()+","+otherModel.getUserId();
                    myFlag = 1;
                }

                responseCallback.onSuccessed();
            }
        });
    }

    // 들어온 채팅방에서 채팅 프로세스를 진행하는 코드.
    // 파이어베이스 버전.
    @Override
    public void openChat(CdkResponseCallback<ChatMessageModel> responseCallback) {
        //2. 채팅방의 존재 여부를 확인한 이후, 채팅 프로세스를 진행한다.
        FirebaseDatabase.getInstance().getReference().child("chat").child(roomId).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                ChatMessageModel chat = dataSnapshot.getValue(ChatMessageModel.class);
                if(chat.getFlag() != myFlag) { //상대방이 보낸 채팅인 경우: 채팅을 읽었으므로 notread를 false 처리.
                    chat.setNotread(false);
                    FirebaseDatabase.getInstance().getReference().child("chat")
                            .child(roomId).child(dataSnapshot.getKey()).child("notread").setValue(false);
                }

                responseCallback.onDataLoaded("Success", chat);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                //removeMessage(dataSnapshot, adapter);
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    // 채팅방에서 메세지를 전송하는 코드.
    // 파이어베이스 버전.
    @Override
    public void sendMessage(ChatMessageModel chatMessage, CdkEmptyCallback responseCallback) {
        FirebaseDatabase.getInstance().getReference().child("chat").child(roomId).push().setValue(chatMessage);
        responseCallback.onSuccessed();
    }

    public String getRoomId() {
        return roomId;
    }
    public ChatUserModel getMyModel() {
        return myModel;
    }
    public ChatUserModel getOtherModel() {
        return otherModel;
    }
    public int getMyFlag() { return myFlag; }

    /*private void addMessage(DataSnapshot snapshot, ChattingAdapter adapter) {
        ChatMessageModel chat = snapshot.getValue(ChatMessageModel.class);
        binding.lvChatting.setSelection(adapter.getCount() - 1);
        if(chat.getFlag() != myFlag) { //상대방이 보낸 채팅인 경우: 채팅을 읽었으므로 notread를 false 처리.
            chat.setNotread(false);
            reference.child("chat").child(roomId).child(snapshot.getKey()).child("notread").setValue(false);
        }

        adapter.addData(chat);
    }

    private void removeMessage(DataSnapshot snapshot, ChattingAdapter adapter) {
        ChatMessageModel chat = snapshot.getValue(ChatMessageModel.class);
        adapter.removeData(chat);
    }*/
}
