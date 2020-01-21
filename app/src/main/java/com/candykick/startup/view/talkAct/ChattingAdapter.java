package com.candykick.startup.view.talkAct;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.candykick.startup.R;
import com.candykick.startup.model.ChatMessageModel;
import com.candykick.startup.model.ChatUserModel;
import com.candykick.startup.util.DateUtility;

import java.util.ArrayList;

/**
 * Created by candykick on 2019. 7. 15..
 */

public class ChattingAdapter extends BaseAdapter {

    Context context;
    ArrayList<ChatMessageModel> arrayList = new ArrayList<>();
    ChatUserModel receiverData;
    int myFlag;

    public ChattingAdapter(Context context, ChatUserModel receiverData, int myFlag) {
        this.context = context;
        this.receiverData = receiverData;
        this.myFlag = myFlag;
    }

    //아래는 BaseAdapter 상속받은 필수요소.
    @Override
    public int getCount() {
        return arrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return arrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view; ViewHolder holder;
        ChatMessageModel data = arrayList.get(position);
        DateUtility dateUtility = new DateUtility(data.getMessageDate());

        view = convertView;

        if(view == null) {
            holder = new ViewHolder();

            LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.rawchatting, parent, false);

            holder.llChatReceiver = view.findViewById(R.id.llChatReceiver);
            holder.llChatMine = view.findViewById(R.id.llChatMine);
            holder.ivProfile = view.findViewById(R.id.ivChattingRaw);
            holder.tvName = view.findViewById(R.id.tvNameChattingRaw);
            holder.tvMessage = view.findViewById(R.id.tvChatChattingRaw);
            holder.tvChatDate = view.findViewById(R.id.tvDateChattingRaw);
            holder.tvMessageMine = view.findViewById(R.id.tvChatChatMineRaw);
            holder.tvChatDateMine = view.findViewById(R.id.tvDateChatMineRaw);

            view.setTag(holder);
        } else {
            holder = (ViewHolder)view.getTag();
        }

        if(myFlag == data.getFlag()) { //내가 작성한 메세지인 경우
            holder.llChatMine.setVisibility(View.VISIBLE);
            holder.llChatReceiver.setVisibility(View.GONE);
            holder.tvMessageMine.setText(data.getMessage());
            holder.tvChatDateMine.setText(dateUtility.DateResultType1());
        } else { //상대방이 작성한 메세지인 경우
            holder.llChatMine.setVisibility(View.GONE);
            holder.llChatReceiver.setVisibility(View.VISIBLE);
            Glide.with(context).load(receiverData.getProfileImage()).into(holder.ivProfile);
            holder.tvName.setText(receiverData.getUserName());
            holder.tvMessage.setText(data.getMessage());
            holder.tvChatDate.setText(dateUtility.DateResultType1());
        }

        return view;
    }

    public void addData(ChatMessageModel data) {
        arrayList.add(data);
        notifyDataSetChanged();
    }
    public void removeData(ChatMessageModel data) {
        arrayList.remove(data);
    }

    public class ViewHolder {
        public LinearLayout llChatReceiver;
        public LinearLayout llChatMine;
        public ImageView ivProfile;
        public TextView tvName;
        public TextView tvMessage;
        public TextView tvChatDate;
        public TextView tvMessageMine;
        public TextView tvChatDateMine;
    }
}
