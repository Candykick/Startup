package com.candykick.startup.view.talkAct;

import android.content.Context;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.candykick.startup.R;
import com.candykick.startup.model.ChatAlarmModel;
import com.candykick.startup.model.ChatListModel;

import java.util.ArrayList;

/**
 * Created by candykick on 2019. 7. 11..
 */

public class ChatMainListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int ALARMTYPE = 0;
    private static final int CHATTYPE = 1;

    private ArrayList<ChatListModel> listData;
    private ArrayList<ChatAlarmModel> alarmData;
    private Context context;

    public interface OnItemClickListener {
        void onItemClick(View v, int pos);
    }

    private OnItemClickListener mListener = null;

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mListener = listener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView ivProfile;
        public TextView tvUserName;
        public TextView tvUserChat;
        public TextView tvChatDate;
        public TextView tvNotRead;

        public ViewHolder(View view) {
            super(view);
            this.ivProfile = view.findViewById(R.id.ivChatMainRaw);
            this.tvUserName = view.findViewById(R.id.tvChatMainNameRaw);
            this.tvUserChat = view.findViewById(R.id.tvChatMainChatRaw);
            this.tvChatDate = view.findViewById(R.id.tvChatMainDate);
            this.tvNotRead = view.findViewById(R.id.tvChatMainNotRead);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if(position != RecyclerView.NO_POSITION) {
                        mListener.onItemClick(v, position);
                    }
                }
            });
        }
    }
    public class AlarmHolder extends RecyclerView.ViewHolder {
        protected TextView tvAlarmName, tvAlarmContent;

        public AlarmHolder(View view) {
            super(view);
            this.tvAlarmName = view.findViewById(R.id.tvChatMain2Name);
            this.tvAlarmContent = view.findViewById(R.id.tvChatMain2Alarm);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if(position != RecyclerView.NO_POSITION) {
                        mListener.onItemClick(v, position);
                    }
                }
            });
        }
    }

    public ChatMainListAdapter(Context context, ArrayList<ChatListModel> listData, ArrayList<ChatAlarmModel> alarmData) {
        this.context = context;
        this.listData = listData;
        this.alarmData = alarmData;
    }

    // RecyclerView에 새로운 데이터를 보여주기 위해 필요한 ViewHolder를 생성해야 할 때 호출됩니다.
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view;
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if(viewType == ALARMTYPE) {
            view = inflater.inflate(R.layout.rawchatmain2, viewGroup, false);
            AlarmHolder alarmHolder = new AlarmHolder(view);

            return alarmHolder;
        } else {
            view = inflater.inflate(R.layout.rawchatmain, viewGroup, false);
            ViewHolder viewHolder = new ViewHolder(view);
            viewHolder.ivProfile.setBackground(new ShapeDrawable(new OvalShape()));
            viewHolder.ivProfile.setClipToOutline(true);

            return viewHolder;
        }
    }

    // Adapter의 특정 위치(position)에 있는 데이터를 보여줘야 할때 호출됩니다.
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder vh, int position) {
        if(vh instanceof ViewHolder) {
            ViewHolder viewHolder = (ViewHolder)vh;

            ChatListModel data = listData.get(position);

            if(data.getProfileImage() != null) {
                viewHolder.ivProfile.setVisibility(View.VISIBLE);
                Glide.with(context).load(data.getProfileImage()).into(viewHolder.ivProfile);
            } else {
                viewHolder.ivProfile.setImageResource(R.drawable.ic_account_circle_black_24dp);
            }

            viewHolder.tvUserName.setText(data.getUserName());
            viewHolder.tvUserChat.setText(data.getUserRecentMessage());
            viewHolder.tvChatDate.setText(data.getMessageDate());
            viewHolder.tvNotRead.setText("신규 "+data.getNotreadMessage()+"개");
        } else {
            AlarmHolder alarmHolder = (AlarmHolder)vh;

            ChatAlarmModel data = alarmData.get(position);

            alarmHolder.tvAlarmName.setText(Integer.toString(data.getAlarmType()));
            alarmHolder.tvAlarmContent.setText(data.getMessage());
        }
    }

    @Override
    public int getItemCount() {
        if (listData == null & alarmData == null) {
            return 0;
        } else if(listData == null) {
            return alarmData.size();
        } else if(alarmData == null) {
            return listData.size();
        } else {
            return alarmData.size() + listData.size();
        }
    }

    @Override
    public int getItemViewType(int position) {
        if(position < alarmData.size()) {
            return ALARMTYPE;
        } else {
            return CHATTYPE;
        }
    }



    public void addData(ChatListModel data) {
        listData.add(data);
    }
    public void removeData(ChatListModel data) {
        listData.remove(data);
    }
}
