package com.candykick.startup.view.adviserAct;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.candykick.startup.R;

import java.util.HashMap;
import java.util.List;

public class AdviserCareerAdapter extends RecyclerView.Adapter<AdviserCareerAdapter.ViewHolder> {

    List<HashMap<String, String>> infoDataLst;
    private Context context;

    public class ViewHolder extends RecyclerView.ViewHolder {
        protected TextView tvAdvCareer1, tvAdvCareer2;

        public ViewHolder(View view) {
            super(view);
            this.tvAdvCareer1 = view.findViewById(R.id.tvAdvCareer1);
            this.tvAdvCareer2 = view.findViewById(R.id.tvAdvCareer2);
        }
    }

    public AdviserCareerAdapter(Context context, List<HashMap<String, String>> infoDataLst) {
        this.context = context;
        this.infoDataLst = infoDataLst;
    }

    // RecyclerView에 새로운 데이터를 보여주기 위해 필요한 ViewHolder를 생성해야 할 때 호출됩니다.
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view;

        view = LayoutInflater.from(context).inflate(R.layout.rawadviser,viewGroup,false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    // Adapter의 특정 위치(position)에 있는 데이터를 보여줘야 할때 호출됩니다.
    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        try {
            viewHolder.tvAdvCareer1.setText(infoDataLst.get(position).get("careerdate"));
            viewHolder.tvAdvCareer2.setText(infoDataLst.get(position).get("careertitle"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return infoDataLst.size();
    }
}
