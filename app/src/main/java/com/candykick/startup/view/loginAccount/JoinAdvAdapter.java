package com.candykick.startup.view.loginAccount;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.candykick.startup.R;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by candykick on 2019. 8. 8..
 */

public class JoinAdvAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<JoinAdvData> arrayList = new ArrayList<>();
    private Context context;

    public class ViewHolder extends RecyclerView.ViewHolder {
        protected EditText etJoinAddRaw1, etJoinAddRaw2;
        //protected Button btnJoinDeleteRaw1;

        public ViewHolder(View view) {
            super(view);
            this.etJoinAddRaw1 = view.findViewById(R.id.etJoinAddRaw1);
            this.etJoinAddRaw2 = view.findViewById(R.id.etJoinAddRaw2);
            //this.btnJoinDeleteRaw1 = view.findViewById(R.id.btnJoinDeleteRaw1);
        }
    }

    public JoinAdvAdapter(Context context) {
        this.context = context;
        arrayList.add(new JoinAdvData("",""));
    }

    public JoinAdvAdapter(Context context, ArrayList<JoinAdvData> arrayListInput) {
        this.context = context;
        for(int i=0;i<arrayListInput.size();i++) {
            arrayList.add(arrayListInput.get(i));
        }
    }

    public void add() {
        arrayList.add(new JoinAdvData("",""));

        notifyDataSetChanged();
    }

    // RecyclerView에 새로운 데이터를 보여주기 위해 필요한 ViewHolder를 생성해야 할 때 호출됩니다.
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view;

        view = LayoutInflater.from(context).inflate(R.layout.rawjoinadv,viewGroup,false);
        ViewHolder viewHolder = new ViewHolder(view);
        viewHolder.etJoinAddRaw2.addTextChangedListener(new TextWatcher() {
            String previousString = "";
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                previousString = s.toString();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if(viewHolder.etJoinAddRaw2.getLineCount() > 3) {
                    viewHolder.etJoinAddRaw2.setText(previousString);
                    viewHolder.etJoinAddRaw2.setSelection(viewHolder.etJoinAddRaw2.length());
                    Toast.makeText(context,"최대 3줄까지 입력 가능합니다.",Toast.LENGTH_SHORT).show();
                }
            }
        });

        return viewHolder;
    }

    // Adapter의 특정 위치(position)에 있는 데이터를 보여줘야 할때 호출됩니다.
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder vh, int position) {
        try {
            if(vh instanceof ViewHolder) {
                ViewHolder viewHolder = (ViewHolder)vh;

                if(arrayList.get(position).careerName != null) {
                    viewHolder.etJoinAddRaw1.setText(arrayList.get(position).careerDate);
                    viewHolder.etJoinAddRaw2.setText(arrayList.get(position).careerName);
                }

                /*viewHolder.btnJoinDeleteRaw1.setOnClickListener(v -> {
                    arrayList.remove(position);
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, arrayList.size());
                });*/

                viewHolder.etJoinAddRaw1.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

                    @Override
                    public void afterTextChanged(Editable editable) {
                        arrayList.get(position).careerDate = editable.toString();
                    }
                });
                viewHolder.etJoinAddRaw2.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

                    @Override
                    public void afterTextChanged(Editable editable) {
                        arrayList.get(position).careerName = editable.toString();
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        if (arrayList == null) {
            return 0;
        }

        return arrayList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    //이력 ArrayList 반환
    public ArrayList<JoinAdvData> getCareer() {
        return arrayList;
    }

    //이력 ArrayList 반환2
    public ArrayList<HashMap<String, String>> getCareerByHashMap() {
        ArrayList<HashMap<String, String>> templist = new ArrayList<>();

        for(JoinAdvData data : arrayList) {
            if(data.careerName.equals("") || data.careerDate.equals("")) {
                return null;
            } else {
                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put("careerdate", data.careerDate);
                hashMap.put("careertitle", data.careerName);

                templist.add(hashMap);
            }
        }

        return templist;
    }
}
