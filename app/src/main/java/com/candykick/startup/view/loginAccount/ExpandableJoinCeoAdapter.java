package com.candykick.startup.view.loginAccount;

/**
 * Created by candykick on 2019. 6. 16..
 */

public class ExpandableJoinCeoAdapter {}

/*public class ExpandableJoinCeoAdapter extends BaseExpandableListAdapter {

    private Context mContext;
    private ChildListViewHolder mChildListViewHolder;

    // CustomExpandableListViewAdapter 생성자
    public ExpandableJoinCeoAdapter(Context context){
        this.mContext = context;
    }

    //ParentListView에 대한 method
    @Override
    public String getGroup(int groupPosition) { // ParentList의 position을 받아 해당 TextView에 반영될 String을 반환
        //return mParentList.get(groupPosition);
        return "이용자 신상정보";
    }

    @Override
    public int getGroupCount() { // ParentList의 원소 개수를 반환
        //return mParentList.size();
        return 1;
    }

    @Override
    public long getGroupId(int groupPosition) { // ParentList의 position을 받아 long값으로 반환
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) { // ParentList의 View
        if(convertView == null){
            LayoutInflater groupInfla = (LayoutInflater) this.mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            // ParentList의 layout 연결. root로 argument 중 parent를 받으며 root로 고정하지는 않음
            convertView = groupInfla.inflate(R.layout., parent, false);
        }

        // ParentList의 Layout 연결 후, 해당 layout 내 TextView를 연결
        TextView parentText = (TextView)convertView.findViewById(R.id.parenttext);
        parentText.setText(getGroup(groupPosition));
        return convertView;
    }

    // 여기서부터 ChildListView에 대한 method
    @Override
    public ChildListData getChild(int groupPosition, int childPosition) { // groupPostion과 childPosition을 통해 childList의 원소를 얻어옴
        return this.mChildHashMap.get(this.mParentList.get(groupPosition)).get(childPosition);

    }

    @Override
    public int getChildrenCount(int groupPosition) { // ChildList의 크기를 int 형으로 반환
        return this.mChildHashMap.get(this.mParentList.get(groupPosition)).size();

    }

    @Override
    public long getChildId(int groupPosition, int childPosition) { // ChildList의 ID로 long 형 값을 반환
        return childPosition;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        // ChildList의 View. 위 ParentList의 View를 얻을 때와 비슷하게 Layout 연결 후, layout 내 TextView, ImageView를 연결
        LayoutInflater childInfla = (LayoutInflater) this.mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        switch (groupPosition) {
            case 0:
                //이용자 신상정보. 성별, 나이, 역할
                convertView = childInfla.inflate(R.layout.row_joinceo_personal, null);

        }

        mChildListViewHolder.mChildListViewIcon = (ImageView)convertView.findViewById(R.id.child_item_icon);
        mChildListViewHolder.mChildListViewText = (TextView)convertView.findViewById(R.id.childtext);

        mChildListViewHolder.mChildListViewText.setText(getChild(groupPosition, childPosition).mChildText);
        mChildListViewHolder.mChildListViewIcon.setImageDrawable(getChild(groupPosition, childPosition).mChildItem);
        return convertView;

    }

    @Override
    public boolean hasStableIds() { return true; } // stable ID인지 boolean 값으로 반환

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) { return true; } // 선택여부를 boolean 값으로 반환
}*/