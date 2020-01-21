package com.candykick.startup.view.adviserAct;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.candykick.startup.R;
import com.candykick.startup.view.base.HtmlTextView;
import com.candykick.startup.view.userInfo.UserInfoStartActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by candykick on 2019. 9. 9..
 */

public class AdviserAnswerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int HEADER_VIEW = 1;
    private static final int FOOTER_VIEW = 2;

    boolean ischosen = false, ismypost = false, isimadviser = false;
    private String postid, postTitle, postUser, postDate, postCategory, postQuestion;
    private int postHit, answerNum;

    private ArrayList<AdviserAnswerData> arrayList = new ArrayList<>();
    private ArrayList<String> questionImage = new ArrayList<>();
    private Context context;

    public class Header extends RecyclerView.ViewHolder {
        protected TextView tvAdvPostTitle, tvAdvPostQName, tvAdvPostDateCategory, tvAdvPostHits;
        protected HtmlTextView tvAdvPostContents;
        protected ViewPager vpRawAdvHeaderImage;

        public Header(View view) {
            super(view);
            this.tvAdvPostTitle = view.findViewById(R.id.tvAdvPostTitle);
            this.tvAdvPostQName = view.findViewById(R.id.tvAdvPostQName);
            this.tvAdvPostDateCategory = view.findViewById(R.id.tvAdvPostDateCategory);
            this.tvAdvPostContents = view.findViewById(R.id.tvAdvPostContents);
            this.tvAdvPostHits = view.findViewById(R.id.tvAdvPostHits);
            this.vpRawAdvHeaderImage = view.findViewById(R.id.vpRawAdvHeaderImage);
        }
    }
    public class AnswerViewHolder extends RecyclerView.ViewHolder {
        protected View lineAdvPost;
        protected TextView tvAdvPostBestAnswer, tvAdvPostAnswerUser, tvAdvPostUserRanking,
                tvAdvPostUserInfo, tvAdvPostRecommend;
        protected HtmlTextView tvAdvPostAnswer;
        protected ImageView ivAdvPostProfile, ivAdvPostMedal;
        protected ViewPager vpRawAdvImage;
        protected Button btnAdvPostRecommend, btnAdvPostSeeInfo;

        public AnswerViewHolder(View view) {
            super(view);
            this.lineAdvPost = view.findViewById(R.id.lineAdvPost);
            this.tvAdvPostBestAnswer = view.findViewById(R.id.tvAdvPostBestAnswer);
            this.tvAdvPostAnswerUser = view.findViewById(R.id.tvAdvPostAnswerUser);
            this.tvAdvPostUserRanking = view.findViewById(R.id.tvAdvPostUserRanking);
            this.tvAdvPostUserInfo = view.findViewById(R.id.tvAdvPostUserInfo);
            this.tvAdvPostAnswer = view.findViewById(R.id.tvAdvPostAnswer);
            this.tvAdvPostRecommend = view.findViewById(R.id.tvAdvPostRecommend);
            this.ivAdvPostProfile = view.findViewById(R.id.ivAdvPostProfile);
            this.ivAdvPostMedal = view.findViewById(R.id.ivAdvPostMedal);
            this.btnAdvPostRecommend = view.findViewById(R.id.btnAdvPostRecommend);
            this.btnAdvPostSeeInfo = view.findViewById(R.id.btnAdvPostSeeInfo);
            this.vpRawAdvImage = view.findViewById(R.id.vpRawAdvImage);

            this.ivAdvPostProfile.setBackground(new ShapeDrawable(new OvalShape()));
            this.ivAdvPostProfile.setClipToOutline(true);
        }
    }
    public class Footer extends RecyclerView.ViewHolder {
        protected Button btnAdvPostAddAnswer;

        public Footer(View view) {
            super(view);
            this.btnAdvPostAddAnswer = view.findViewById(R.id.btnAdvPostAddAnswer);
            this.btnAdvPostAddAnswer.setOnClickListener(v -> {
                Intent intent = new Intent(context, AdviserAnswerActivity.class);
                intent.putExtra("id",postid);
                intent.putExtra("answernum",answerNum);
                ((AdviserPostActivity)context).startActivityForResult(intent, 0);
            });
        }
    }

    public AdviserAnswerAdapter(boolean ismypost, boolean isimadviser, String postid, String postTitle, String postUser, String postDate, String postCategory, String postQuestion, int postHit, ArrayList<String> questionImage, Context context, ArrayList<AdviserAnswerData> arrayList, int answerNum) {
        this.ismypost = ismypost;
        this.isimadviser = isimadviser;
        this.postid = postid;
        this.postTitle = postTitle;
        this.postUser = postUser;
        this.postDate = postDate;
        this.postCategory = postCategory;
        this.postQuestion = postQuestion;
        this.postHit = postHit;
        this.questionImage = questionImage;
        this.context = context;
        this.arrayList = arrayList;
        this.answerNum = answerNum;
    }

    // RecyclerView에 새로운 데이터를 보여주기 위해 필요한 ViewHolder를 생성해야 할 때 호출됩니다.
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        View view;

        if(viewType == HEADER_VIEW) {
            view = LayoutInflater.from(context).inflate(R.layout.rawadviserheader,viewGroup,false);
            Header header = new Header(view);
            return header;
        } else if(viewType == FOOTER_VIEW) {
            view = LayoutInflater.from(context).inflate(R.layout.rawadviserfooter,viewGroup,false);
            Footer footer = new Footer(view);
            return footer;
        } else {
            view = LayoutInflater.from(context).inflate(R.layout.rawadviseranswer, viewGroup, false);
            AnswerViewHolder viewHolder = new AnswerViewHolder(view);
            return viewHolder;
        }
    }

    // Adapter의 특정 위치(position)에 있는 데이터를 보여줘야 할때 호출됩니다.
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder vh, int position) {
        try {
            if(vh instanceof AnswerViewHolder) {
                AnswerViewHolder viewHolder = (AnswerViewHolder)vh;

                if(arrayList.get(position-1).isBest) {
                    viewHolder.lineAdvPost.setBackgroundColor(context.getResources().getColor(R.color.newsetGreenBlue));
                    viewHolder.tvAdvPostBestAnswer.setVisibility(View.VISIBLE);
                    viewHolder.ivAdvPostMedal.setVisibility(View.VISIBLE);
                    ischosen = true;
                } else {
                    viewHolder.lineAdvPost.setBackgroundColor(context.getResources().getColor(R.color.newsetGrayWhite));
                    viewHolder.tvAdvPostBestAnswer.setVisibility(View.GONE);
                    viewHolder.ivAdvPostMedal.setVisibility(View.GONE);
                }

                viewHolder.tvAdvPostAnswerUser.setText(arrayList.get(position-1).answerUserName);
                viewHolder.tvAdvPostUserRanking.setText("랭킹 "+arrayList.get(position-1).answerRanking+"등 | 베스트답변 "+arrayList.get(position-1).answerBestNum+"개");
                viewHolder.tvAdvPostUserInfo.setText(arrayList.get(position-1).answerUserInfo);
                viewHolder.tvAdvPostAnswer.setHtmlText(arrayList.get(position-1).answerText);
                viewHolder.tvAdvPostRecommend.setText("추천수 "+arrayList.get(position-1).answerRecommendNum);

                Glide.with(context).load(arrayList.get(position-1).answerUserProfileUrl).into(viewHolder.ivAdvPostProfile);

                if(arrayList.get(position-1).imageList.size() == 0) {
                    viewHolder.vpRawAdvImage.setVisibility(View.GONE);
                } else {
                    viewHolder.vpRawAdvImage.setVisibility(View.VISIBLE);
                    ImageViewSimpleAdapter adapter = new ImageViewSimpleAdapter(context, arrayList.get(position-1).imageList);
                    viewHolder.vpRawAdvImage.setAdapter(adapter);
                }

                if(ismypost & !ischosen) {
                    viewHolder.btnAdvPostRecommend.setText("이 답변 채택하기");
                    viewHolder.btnAdvPostRecommend.setOnClickListener(v -> {
                        ((AdviserPostActivity)context).progressOn();

                        Map<String, Object> rcmData = new HashMap<>();
                        rcmData.put("isbest",true);

                        FirebaseFirestore.getInstance().collection("Adviser")
                                .document(postid).collection("answer").document(position+"").set(rcmData, SetOptions.merge())
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void avoid) {
                                        ((AdviserPostActivity)context).progressOff();
                                        ((AdviserPostActivity)context).makeToast("답변을 채택했습니다!");
                                        arrayList.get(position-1).isBest = true;
                                        viewHolder.lineAdvPost.setBackgroundColor(context.getResources().getColor(R.color.newsetGreenBlue));
                                        viewHolder.tvAdvPostBestAnswer.setVisibility(View.VISIBLE);
                                        viewHolder.ivAdvPostMedal.setVisibility(View.VISIBLE);

                                        viewHolder.btnAdvPostRecommend.setText("이 답변 추천하기");
                                        viewHolder.btnAdvPostRecommend.setOnClickListener(v -> {
                                            ((AdviserPostActivity)context).progressOn();

                                            Map<String, Object> rcmData = new HashMap<>();
                                            arrayList.get(position-1).answerRecommendNum++;
                                            rcmData.put("recommendnum",arrayList.get(position-1).answerRecommendNum);

                                            FirebaseFirestore.getInstance().collection("Adviser")
                                                    .document(postid).collection("answer").document(position+"").set(rcmData, SetOptions.merge())
                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void avoid) {
                                                            ((AdviserPostActivity)context).progressOff();
                                                            ((AdviserPostActivity)context).makeToast("추천했습니다!");
                                                            viewHolder.tvAdvPostRecommend.setText("추천수 "+arrayList.get(position-1).answerRecommendNum);

                                                            viewHolder.btnAdvPostRecommend.setEnabled(false);
                                                            viewHolder.btnAdvPostRecommend.setClickable(false);
                                                            viewHolder.btnAdvPostRecommend.setBackgroundColor(context.getResources().getColor(R.color.newsetWhite));
                                                            viewHolder.btnAdvPostRecommend.setTextColor(context.getResources().getColor(R.color.newsetGrayWhite));
                                                        }
                                                    })
                                                    .addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            ((AdviserPostActivity)context).progressOff();
                                                            ((AdviserPostActivity)context).makeToast("추천에 실패했습니다. 오류: " + e.toString());
                                                        }
                                                    });
                                        });
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        ((AdviserPostActivity)context).progressOff();
                                        ((AdviserPostActivity)context).makeToast("채택에 실패했습니다. 오류: " + e.toString());
                                    }
                                });
                    });
                } else {
                    viewHolder.btnAdvPostRecommend.setText("이 답변 추천하기");
                    viewHolder.btnAdvPostRecommend.setOnClickListener(v -> {
                        ((AdviserPostActivity)context).progressOn();

                        Map<String, Object> rcmData = new HashMap<>();
                        arrayList.get(position-1).answerRecommendNum++;
                        rcmData.put("recommendnum",arrayList.get(position-1).answerRecommendNum);

                        FirebaseFirestore.getInstance().collection("Adviser")
                                .document(postid).collection("answer").document(position+"").set(rcmData, SetOptions.merge())
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void avoid) {
                                        ((AdviserPostActivity)context).progressOff();
                                        ((AdviserPostActivity)context).makeToast("추천했습니다!");
                                        viewHolder.tvAdvPostRecommend.setText("추천수 "+arrayList.get(position-1).answerRecommendNum);

                                        viewHolder.btnAdvPostRecommend.setEnabled(false);
                                        viewHolder.btnAdvPostRecommend.setClickable(false);
                                        viewHolder.btnAdvPostRecommend.setBackgroundColor(context.getResources().getColor(R.color.newsetWhite));
                                        viewHolder.btnAdvPostRecommend.setTextColor(context.getResources().getColor(R.color.newsetGrayWhite));
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        ((AdviserPostActivity)context).progressOff();
                                        ((AdviserPostActivity)context).makeToast("추천에 실패했습니다. 오류: " + e.toString());
                                    }
                                });
                    });
                }

                viewHolder.btnAdvPostSeeInfo.setOnClickListener(v -> {
                    Intent intent = new Intent(context, UserInfoStartActivity.class);
                    intent.putExtra("ismyData",false);
                    intent.putExtra("userId",arrayList.get(position-1).answerUserId);
                    context.startActivity(intent);
                });
            } else if(vh instanceof Header) {
                Header viewHolder = (Header)vh;

                viewHolder.tvAdvPostTitle.setText(postTitle);
                viewHolder.tvAdvPostQName.setText(postUser);
                viewHolder.tvAdvPostDateCategory.setText(postDate+"/"+postCategory);
                viewHolder.tvAdvPostContents.setHtmlText(postQuestion);
                viewHolder.tvAdvPostHits.setText("조회수 "+postHit);

                if(questionImage.size() == 0) {
                    viewHolder.vpRawAdvHeaderImage.setVisibility(View.GONE);
                } else {
                    viewHolder.vpRawAdvHeaderImage.setVisibility(View.VISIBLE);
                    ImageViewSimpleAdapter adapter = new ImageViewSimpleAdapter(context, questionImage);
                    viewHolder.vpRawAdvHeaderImage.setAdapter(adapter);
                }
            } else if(vh instanceof Footer) {
                Footer footer = (Footer)vh;

                if(!isimadviser) {
                    footer.btnAdvPostAddAnswer.setVisibility(View.GONE);
                }
                else if(ischosen || ismypost) {
                    footer.btnAdvPostAddAnswer.setVisibility(View.GONE);
                }
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

        if (arrayList.size() == 0) {
            return 2;
        }

        // Add extra view to show the footer view
        return arrayList.size()+2;
    }

    @Override
    public int getItemViewType(int position) {
        if(position == 0) {
            return HEADER_VIEW;
        } else if(position == arrayList.size()+1) {
            return FOOTER_VIEW;
        } else {
            return super.getItemViewType(position);
        }
    }
}