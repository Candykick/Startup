package com.candykick.startup.view.marketAct;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.candykick.startup.R;
import com.candykick.startup.model.MarketCommentModel;
import com.candykick.startup.model.UserOutModel;

import java.util.ArrayList;

public class PostExpandableAdapter extends BaseExpandableListAdapter {

    private ArrayList<MarketCommentModel> commentArray = new ArrayList<>();
    private String type, postId;

    private Context context;
    private String strModify, strRefund;
    private UserOutModel userData;
    private String parentList[] = {"수정 및 재진행 안내", "취소 및 환불규정", "전문가 정보", "서비스 평가"};
    //private String parentList[] = {"수정 및 재진행 안내", "취소 및 환불규정", "전문가 정보"};

    public PostExpandableAdapter(Context context, String strModify, String strRefund, UserOutModel userData, ArrayList<MarketCommentModel> commentArray, String type, String postId) {
        this.context = context;
        this.strModify = strModify;
        this.strRefund = strRefund;
        this.userData = userData;
        this.commentArray = commentArray;
        this.type = type;
        this.postId = postId;
    }

    @Override
    public int getChildType(int groupPosition, int childPosition)
    {
        int result = 0;
        if (groupPosition==2)
            result = 1;
        else if(groupPosition==3)
            result = 2;

        return result; //0: 텍스트뷰만 있는 ChildView, 1: 회사 정보 보여주는 ChildView, 2: 댓글 및 평점 보여주는 ChildView
    }

    @Override
    public int getChildTypeCount() {
        return 3;
    }

    @Override
    public String getGroup(int groupPosition) {
        return parentList[groupPosition];
    }

    @Override
    public int getGroupCount() {
        return parentList.length;
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        ParentItemHolder holder;
        View row = convertView;

        if(row == null) {
            LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.rawmarket_expandable, null);

            holder = new ParentItemHolder();
            holder.tvParent = row.findViewById(R.id.tvEPParent);

            row.setTag(holder);
        } else {
            holder = (ParentItemHolder)row.getTag();
        }

        holder.tvParent.setText(parentList[groupPosition]);

        return row;
    }

    @Override //getChild로 얻어올 뭔가가 없어서 일단 그대로 둠.
    public Object getChild(int groupPosition, int childPosition) {
        if(groupPosition == 0)
            return strModify;
        else if(groupPosition == 1)
            return strRefund;
        else
            return null;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return 1;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return groupPosition*10+childPosition;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        if(convertView == null) {
            LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            int childType = getChildType(groupPosition, childPosition);

            switch (childType) {
                case 0:
                    convertView = inflater.inflate(R.layout.rawmarket_expandable_child1, null);
                    TextView tvChild = convertView.findViewById(R.id.tvEPChild1);
                    tvChild.setText(groupPosition==0?strModify:strRefund);
                    break;
                case 1:
                    convertView = inflater.inflate(R.layout.rawmarket_expandable_child2, null);
                    ImageView ivEPChild2 = convertView.findViewById(R.id.ivEPChild2);
                    ivEPChild2.setBackground(new ShapeDrawable(new OvalShape()));
                    ivEPChild2.setClipToOutline(true);
                    ImageView ivEPStatus = convertView.findViewById(R.id.ivEPStatus);
                    TextView tvEPTax = convertView.findViewById(R.id.tvEPTax);
                    TextView tvEPName = convertView.findViewById(R.id.tvEPName);
                    TextView tvEPDes = convertView.findViewById(R.id.tvEPDes);
                    TextView tvEPSatis = convertView.findViewById(R.id.tvEPSatis);
                    TextView tvEPOrderNum = convertView.findViewById(R.id.tvEPOrderNum);
                    TextView tvEPAnswerTime = convertView.findViewById(R.id.tvEPAnswerTime);

                    Glide.with(context).load(userData.profileImage).into(ivEPChild2);
                    if(userData.taxinvoicepossible) {
                        tvEPTax.setText("세금계산서 발행 가능");
                    } else {
                        tvEPTax.setText("세금계산서 발행 불가");
                    }
                    tvEPName.setText(userData.userName);
                    tvEPDes.setText(userData.userDes);
                    tvEPSatis.setText(userData.userSatis+"/5.0");
                    tvEPOrderNum.setText(userData.userOrderNum+"건");
                    tvEPAnswerTime.setText(userData.userAnswerTime+"이내");
                    break;
                case 2:
                    convertView = inflater.inflate(R.layout.rawmarket_expandable_child3, null);

                    RecyclerView rvIdeaComment2 = convertView.findViewById(R.id.rvIdeaComment2);
                    LinearLayout llIdeaAddComment2 = convertView.findViewById(R.id.llIdeaAddComment2);
                    llIdeaAddComment2.setOnClickListener(v -> {
                        Intent intent = new Intent(context, MarketCommentActivity.class);
                        intent.putExtra("postid",postId);
                        intent.putExtra("type",type);
                        context.startActivity(intent);
                    });
                    Button btnIdeaMoreComments2 = convertView.findViewById(R.id.btnIdeaMoreComments2);
                    btnIdeaMoreComments2.setOnClickListener(v -> {
                        Intent intent = new Intent(context, MarketCommentActivity.class);
                        context.startActivity(intent);
                    });

                    if(commentArray.size() == 0) {
                        btnIdeaMoreComments2.setVisibility(View.GONE);
                        rvIdeaComment2.setVisibility(View.GONE);
                    } else if(commentArray.size() < 6) {
                        btnIdeaMoreComments2.setVisibility(View.GONE);
                        rvIdeaComment2.setVisibility(View.VISIBLE);

                        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
                        rvIdeaComment2.setLayoutManager(linearLayoutManager);
                        MarketCommentAdapter adapter = new MarketCommentAdapter(context, commentArray);
                        rvIdeaComment2.setAdapter(adapter);
                    } else {
                        btnIdeaMoreComments2.setVisibility(View.VISIBLE);
                        rvIdeaComment2.setVisibility(View.VISIBLE);

                        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
                        rvIdeaComment2.setLayoutManager(linearLayoutManager);
                        MarketCommentAdapter adapter = new MarketCommentAdapter(context, commentArray);
                        rvIdeaComment2.setAdapter(adapter);
                    }
                    break;
            }
        }

        return convertView;

        /*if(groupPosition == 2) {
            LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.rawmarket_expandable_child2, null);

            ImageView ivEPChild2 = row.findViewById(R.id.ivEPChild2);
            ImageView ivEPStatus = row.findViewById(R.id.ivEPStatus);
            TextView tvEPTax = row.findViewById(R.id.tvEPTax);
            TextView tvEPName = row.findViewById(R.id.tvEPName);
            TextView tvEPDes = row.findViewById(R.id.tvEPDes);
            TextView tvEPSatis = row.findViewById(R.id.tvEPSatis);
            TextView tvEPOrderNum = row.findViewById(R.id.tvEPOrderNum);
            TextView tvEPAnswerTime = row.findViewById(R.id.tvEPAnswerTime);

            Glide.with(context).load(userData.profileImage).into(ivEPChild2);
            if(userData.taxinvoicepossible) {
                tvEPTax.setText("세금계산서 발행 가능");
            } else {
                tvEPTax.setText("세금계산서 발행 불가");
            }
            tvEPName.setText(userData.userName);
            tvEPDes.setText(userData.userDes);
            tvEPSatis.setText(userData.userSatis+"/5.0");
            tvEPOrderNum.setText(userData.userOrderNum+"건");
            tvEPAnswerTime.setText(userData.userAnswerTime+"이내");
        } else {
            if(row == null) {
                LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                row = inflater.inflate(R.layout.rawmarket_expandable_child1, null);

                holder = new ChildItemHolder1();
                holder.tvChild = row.findViewById(R.id.tvEPChild1);

                row.setTag(holder);

                holder.tvChild.setText(groupPosition==0?strModify:strRefund);
            } else {
                holder = (ChildItemHolder1)row.getTag();
                holder.tvChild.setText(groupPosition==0?strModify:strRefund);
            }
        }

        return row;*/
    }

    @Override
    public boolean hasStableIds() { return true; }
    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) { return true; }


    class ParentItemHolder {
        TextView tvParent;
    }
}
