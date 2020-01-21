package com.candykick.startup;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;

import com.candykick.startup.view.adviserAct.AdviserBoardActivity;
import com.candykick.startup.view.askInquiry.AskActivity;
import com.candykick.startup.view.base.BaseActivity;
import com.candykick.startup.view.cartAct.CartActivity;
import com.candykick.startup.databinding.ActivityMainBinding;
import com.candykick.startup.view.idea.IdeaBoardActivity;
import com.candykick.startup.view.marketAct.MarketCategoryActivity;
import com.candykick.startup.view.newsAct.NewsActivity;
import com.candykick.startup.view.referenceAct.ReferenceActivity;
import com.candykick.startup.view.talkAct.ChatMainActivity;
import com.candykick.startup.view.userInfo.UserInfoStartActivity;
import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONObject;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import cn.trinea.android.view.autoscrollviewpager.AutoScrollViewPager;

public class MainActivity extends BaseActivity<ActivityMainBinding> {

    String SERVER_KEY = "AAAAwF-RMmM:APA91bGlm5TBaD48tdUiCP0lQaccWMHprnUlbU2BvgiLUI0rhTbAWI5fkxW-ml6Ft6ty-b3bIrkYeZdBgJUd_hVoekdSFHym47uPmWJgvR-0tDbCI6YPQLb0zdD16hGFMSYd9MQf6T6v";
    //뒤로가기 버튼 입력시간
    private long pressedTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding.setActivity(this);

        SharedPreferences sf = getSharedPreferences("userType",MODE_PRIVATE);
        //text라는 key에 저장된 값이 있는지 확인. 아무값도 들어있지 않으면 ""를 반환
        long userType = sf.getLong("userType",0);
        boolean isTalkActOpened = sf.getBoolean("isTalkActOpened",false);

        if(FirebaseAuth.getInstance().getCurrentUser() != null) {
            if (!isTalkActOpened && FirebaseAuth.getInstance().getCurrentUser().getUid().equals("N6X7p34icBWUMVEO4jWGSDb6ZbA3")) {
                binding.ivChatCircle.setVisibility(View.VISIBLE);
            } else {
                binding.ivChatCircle.setVisibility(View.GONE);
            }
        } else {
            binding.ivChatCircle.setVisibility(View.GONE);
        }

        binding.toolbar.btnToolBarMRight.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, UserInfoStartActivity.class);
            intent.putExtra("ismyData", true);
            startActivity(intent);
            /*if(userType == 2) {
                Intent uintent = new Intent(MainActivity.this, AdviserInfoActivity.class);
                uintent.putExtra("ismyData", true);
                startActivity(uintent);
            } else {
                Intent uintent = new Intent(MainActivity.this, UserInfoActivity.class);
                uintent.putExtra("ismyData", true);
                startActivity(uintent);
            }*/
        });
        binding.toolbar.btnToolBarMLeft.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, CartActivity.class);
            startActivity(intent);
        });

        //임시 액티비티
        binding.toolbar.btnToolBarMCenter.setOnClickListener(v -> {
                    /*for (int i = 0; i < typeId.length; i++) {
                        Map<String, String> info = new HashMap<>();
                        info.put("name", typeName[i]);
                        info.put("exp", typeDes[i]);
                        info.put("search", "");
                        info.put("searchexcept", "");

                        FirebaseFirestore.getInstance().collection("Industry")
                                .document("" + typeId[i]).set(info)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void avoid) {
                                        re++;
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(MainActivity.this, "정보를 등록하는 도중 오류가 발생했습니다. 오류: " + e.toString(), Toast.LENGTH_LONG).show();
                                    }
                                });
                    }

                    if (re == typeId.length) {
                        Toast.makeText(MainActivity.this, "성공", Toast.LENGTH_LONG).show();
                    }
                });*/


                    new sendToTopic().execute("");
            /*if(Session.getCurrentSession().isOpened()) {
                UserManagement.getInstance().me(new MeV2ResponseCallback() {
                    @Override
                    public void onSessionClosed(ErrorResult errorResult) {}

                    @Override
                    public void onSuccess(MeV2Response result) {
                        FirebaseFirestore db = FirebaseFirestore.getInstance();
                        DocumentReference docRef = db.collection("UserCeo").document(""+result.getId());
                        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    DocumentSnapshot document = task.getResult();

                                    if (document.exists()) {
                                        String name = (String)document.get("username");
                                        new sendToTopic().execute(name);
                                    }
                                }
                            }
                        });
                    }
                });
            } else if(FirebaseAuth.getInstance().getCurrentUser() != null) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                FirebaseFirestore db = FirebaseFirestore.getInstance();
                DocumentReference docRef = db.collection("UserCeo").document(user.getUid());
                docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();

                            if (document.exists()) {
                                String name = (String)document.get("username");
                                new sendToTopic().execute(name);
                            }
                        }
                    }
                });
            }*/
        });

        /*Intent intent = getIntent();
        String loginmethod = intent.getStringExtra("loginmethod");
        binding.textView.setText(intent.getStringExtra("result"));
        if(loginmethod.equals("google") || loginmethod.equals("email")) {
            Glide.with(this).load(FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl()).into(binding.ivMProfile);
        }*/

        ArrayList<Integer> data = new ArrayList<>();
        ArrayList<Integer> data2 = new ArrayList<>();
        data.add(R.drawable.popup1);data2.add(R.drawable.popup2);
        data.add(R.drawable.popup3);data2.add(R.drawable.popup4);
        data.add(R.drawable.popup5);data2.add(R.drawable.popup6);
        data.add(R.drawable.popup7);

        AutoScrollViewPager autoPagerView = findViewById(R.id.autoPagerView);
        AutoScrollAdapter scrollAdapter = new AutoScrollAdapter(this, data);
        autoPagerView.setAdapter(scrollAdapter);
        autoPagerView.setInterval(3000); //페이지 넘어갈 시간 간격 설정
        autoPagerView.startAutoScroll(); //자동스크롤 시작

        AutoScrollViewPager autoPagerView2 = findViewById(R.id.autoPagerView2);
        AutoScrollAdapter scrollAdapter2 = new AutoScrollAdapter(this, data2);
        autoPagerView2.setAdapter(scrollAdapter2);
        autoPagerView2.setInterval(3000);
        autoPagerView2.startAutoScroll();

        binding.btnGovVenture.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.mss.go.kr/"));
            startActivity(intent);
        });
        binding.btnKosme.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.kosmes.or.kr/"));
            startActivity(intent);
        });
        binding.btnkipo.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.kipo.go.kr/"));
            startActivity(intent);
        });
        binding.btnKBBank.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.kbstar.com/"));
            startActivity(intent);
        });
        binding.btnKibo.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.kibo.or.kr:444"));
            startActivity(intent);
        });
        binding.btnKodit.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.kodit.co.kr/"));
            startActivity(intent);
        });
        binding.btnKaist.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.kaist.ac.kr/"));
            startActivity(intent);
        });
        binding.btnKiup.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.ibk.co.kr/"));
            startActivity(intent);
        });
        binding.btnPosco.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.poscocapital.com/kr/"));
            startActivity(intent);
        });
        binding.btnSanupBank.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.kdb.co.kr/"));
            startActivity(intent);
        });
    }

    //'문의하기' 버튼 클릭 시
    public void fnMainAsk() {
        Intent intent2 = new Intent(MainActivity.this, AskActivity.class);
        startActivity(intent2);
    }

    //'아이디어 공간' 버튼 클릭 시
    public void fnMainIdea() {
        Intent intent3 = new Intent(MainActivity.this, IdeaBoardActivity.class);
        startActivity(intent3);
    }

    //'자료실' 버튼 클릭 시
    public void fnMainReference() {
        Intent intent4 = new Intent(MainActivity.this, ReferenceActivity.class);
        startActivity(intent4);
    }

    //'외주마켓' 버튼 클릭 시
    public void fnMainMarket() {
        Intent intent5 = new Intent(MainActivity.this, MarketCategoryActivity.class);
        startActivity(intent5);
    }

    //'창업뉴스' 버튼 클릭 시
    public void fnMainNews() {
        Intent intent3 = new Intent(MainActivity.this, NewsActivity.class);
        startActivity(intent3);
    }

    //'대화하기' 버튼 클릭 시
    public void fnMainTalk() {
        binding.ivChatCircle.setVisibility(View.GONE);
        Intent intent4 = new Intent(MainActivity.this, ChatMainActivity.class);
        startActivity(intent4);
    }

    //'조언가그룹' 버튼 클릭 시
    public void fnMainAdviser() {
        Intent intent5 = new Intent(MainActivity.this, AdviserBoardActivity.class);
        startActivity(intent5);
    }

    @Override
    public void onBackPressed() {
        if(pressedTime == 0) {
            makeToast("한 번 더 누르면 앱이 종료됩니다.");
            pressedTime = System.currentTimeMillis();
        } else {
            int seconds = (int) (System.currentTimeMillis() - pressedTime);
            if(seconds > 2000) {
                makeToast("한 번 더 누르면 앱이 종료됩니다.");
                pressedTime = System.currentTimeMillis();
            } else {
                super.onBackPressed();
                //finish();
                //android.os.Process.killProcess(android.os.Process.myPid());
            }
        }
    }

    @Override
    protected int getLayoutId() { return R.layout.activity_main; }

    public class sendToTopic extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            try {
                Thread.sleep(5000);

                // FMC 메시지 생성 start
                JSONObject root = new JSONObject();
                JSONObject notification = new JSONObject();
                notification.put("body", "카트를 확인해주세요!");
                notification.put("title", params[0]+"님, 새로운 동료가 기다리고 있습니다!");
                root.put("notification", notification);
                //root.put("to","duQWsOQ45xg:APA91bFGPLfWuF0PtiGOUiPW9eHFwG4No0KiS1hw6GKF8r1gwfIhENaDNAUOU409taKyrMh45b9kjPXI02GFEYPxf0UiyUbSBvnvB4EW2hgFXX9k-Comv9vZeVCl6jrWoiPS-46uuwN9");
                root.put("to", "fRt9HFh45E4:APA91bH_XabtjV4zsgvDOOTDOQLjLljoIxviVf9Br_qoOhrCEXgXN-VuLnh9iDKp2tBwMgkW2j6tT3UBVf7s03KjR3TXpXOe2YIhZj3loVX6mHliOC3-3q7hONlO6NzsMrdX3pCJGLss");
                // FMC 메시지 생성 end

                URL Url = new URL("https://fcm.googleapis.com/fcm/send");
                HttpURLConnection conn = (HttpURLConnection) Url.openConnection();
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);
                conn.setDoInput(true);
                conn.addRequestProperty("Authorization", "key=" + SERVER_KEY);
                conn.setRequestProperty("Accept", "application/json");
                conn.setRequestProperty("Content-type", "application/json");
                OutputStream os = conn.getOutputStream();
                os.write(root.toString().getBytes("utf-8"));
                os.flush();
                conn.getResponseCode();
            } catch (Exception e) {
                makeToast(e.toString());
            }

            return null;
        }
    }
}