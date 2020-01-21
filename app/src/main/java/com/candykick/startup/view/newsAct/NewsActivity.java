package com.candykick.startup.view.newsAct;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;

import com.candykick.startup.R;
import com.candykick.startup.view.base.BaseActivity;
import com.candykick.startup.databinding.ActivityNewsBinding;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.kakao.auth.Session;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.MeV2ResponseCallback;
import com.kakao.usermgmt.response.MeV2Response;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import java.net.URL;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class NewsActivity extends BaseActivity<ActivityNewsBinding> {

    ReadNews readNews;
    ArrayList<String> bookmarkLink = new ArrayList<>();
    String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding.setActivity(this);

        binding.toolbar.btnToolBarLeft.setOnClickListener(v -> onBackPressed());
        binding.toolbar.tvToolBar.setText("최신 창업뉴스");
        binding.toolbar.btnToolBarRight.setImageResource(R.drawable.bookmarkon);
        binding.toolbar.btnToolBarRight.setPadding(13,13,13,13);
        binding.toolbar.btnToolBarRight.setOnClickListener(v -> {
            Intent intent = new Intent(NewsActivity.this, NewsBookmarkActivity.class);
            intent.putExtra("id",userId);
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        bookmarkLink.clear();

        if(Session.getCurrentSession().isOpened()) {
            progressOn();

            UserManagement.getInstance().me(new MeV2ResponseCallback() {
                @Override
                public void onSessionClosed(ErrorResult errorResult) {
                    progressOff();
                    makeToast("정보를 불러오지 못했습니다. 다시 시도해 주세요: " + errorResult.toString());
                    finish();
                }

                @Override
                public void onSuccess(MeV2Response result) {
                    userId = ""+result.getId();

                    FirebaseFirestore.getInstance().collection("UserCeo")
                            .document(userId).collection("bookmark").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if(task.isSuccessful()) {
                                if (!task.getResult().isEmpty()) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        bookmarkLink.add(document.get("link").toString());
                                    }
                                }
                            }
                        }
                    }).continueWithTask(new Continuation<QuerySnapshot, Task<Void>>() {
                        @Override
                        public Task<Void> then(@NonNull Task<QuerySnapshot> task) throws Exception {
                            try {
                                readNews = new ReadNews();
                                //readNews.execute("https://www.besuccess.com/feed/?lang=ko");
                                //readNews.execute("http://feeds.feedburner.com/venturesquare");
                                readNews.execute("https://www.k-startup.go.kr/common/rssFeed.do?mid=30004&cid=0&bid=701&kid=0&sid=0&boardUrl=/common/announcement/announcementList.do");
                            } catch (Exception e) {
                                makeToastLong("오류가 발생했습니다: "+e.toString());
                            }

                            return null;
                        }
                    });
                }
            });
        } else if(FirebaseAuth.getInstance().getCurrentUser() != null) {
            progressOn();

            userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

            FirebaseFirestore.getInstance().collection("UserCeo")
                    .document(userId)
                    .collection("bookmark").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if(task.isSuccessful()) {
                        if (!task.getResult().isEmpty()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                bookmarkLink.add(document.get("link").toString());
                            }
                        }
                    }
                }
            }).continueWithTask(new Continuation<QuerySnapshot, Task<Void>>() {
                @Override
                public Task<Void> then(@NonNull Task<QuerySnapshot> task) throws Exception {
                    try {
                        readNews = new ReadNews();
                        //readNews.execute("https://www.besuccess.com/feed/?lang=ko");
                        readNews.execute("http://feeds.feedburner.com/venturesquare");
                        //readNews.execute("https://www.k-startup.go.kr/common/rssFeed.do?mid=30004&cid=0&bid=701&kid=0&sid=0&boardUrl=/common/announcement/announcementList.do");
                    } catch (Exception e) {
                        makeToastLong("오류가 발생했습니다: "+e.toString());
                    }

                    return null;
                }
            });
        }
    }

    @Override
    protected void onDestroy() {
        readNews = null;
        super.onDestroy();
    }

    @Override
    protected int getLayoutId() { return R.layout.activity_news; }

    class ReadNews extends AsyncTask<String, Void, Document> {
        @Override
        protected void onPreExecute() {
            //progressOn();
        }

        @Override
        protected Document doInBackground(String... params) {
            URL url;
            Document doc = null;
            try {
                url = new URL(params[0]);
                DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                DocumentBuilder db = dbf.newDocumentBuilder();
                doc = db.parse(new InputSource(url.openStream()));
                doc.getDocumentElement().normalize();

                return doc;
            } catch (Exception e) {
                progressOff();
                Log.e("Startup",e.toString());
                return null;
            }
        }

        @Override
        protected void onPostExecute(Document document) {
            if(document == null) {
                makeToastLong("뉴스를 읽어오던 도중 오류가 발생했습니다.");
            } else {
                ArrayList<NewsData> arrayList = new ArrayList<>();

                NodeList item = document.getElementsByTagName("item");

                for(int i=0;i<item.getLength();i++) {
                    Node node = item.item(i);
                    Element element = (Element) node;

                    NodeList titleNodeList = element.getElementsByTagName("title");
                    NodeList linkNodeList = element.getElementsByTagName("link");
                    NodeList descriptionNodeList = element.getElementsByTagName("description");
                    NodeList imageNodeList = element.getElementsByTagName("content:encoded");
                    String title = titleNodeList.item(0).getChildNodes().item(0).getNodeValue();
                    String link = linkNodeList.item(0).getChildNodes().item(0).getNodeValue();
                    String description = descriptionNodeList.item(0).getChildNodes().item(0).getNodeValue().replace("<p>","");
                    String temp1 = imageNodeList.item(0).getChildNodes().item(0).getNodeValue();

                    Log.d("Startup 1",title);
                    Log.d("Startup 2",description);

                    if(description.length() > 10) {
                        description = description.substring(0, 10) + "...";
                    }

                    if(temp1.toString().contains("src=\"")) {
                        int startIndex = temp1.indexOf("src=\"") + 5;
                        if (startIndex != -1) {
                            Log.d("Startup 3",temp1);
                            String temp2 = temp1.substring(startIndex);
                            //int endIndex = temp2.indexOf("\" alt=");
                            int endIndex = temp2.indexOf("\"");
                            String image = temp2.substring(0, endIndex);

                            arrayList.add(new NewsData(false, image, title, description, link));
                        } else {
                            arrayList.add(new NewsData(false, "", title, description, link));
                        }
                    } else {
                        arrayList.add(new NewsData(false, "", title, description, link));
                    }
                }

                NewsListAdapter adapter = new NewsListAdapter(NewsActivity.this, arrayList, userId, bookmarkLink);
                binding.lvNews.setAdapter(adapter);

                binding.lvNews.setOnItemClickListener((parentView, view, position, id) -> {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(arrayList.get(position).strlink));
                    startActivity(intent);
                });
            }
            progressOff();
        }
    }

    /*class ReadNews extends AsyncTask<String, Void, ArrayList<NewsData>> {

        @Override
        protected void onPreExecute() {
            progressOn();
        }

        @Override
        protected ArrayList<NewsData> doInBackground(String... params) {
            try {
                URL url = new URL(params[0]); // URL of the XML
                SAXParserFactory saxPF = SAXParserFactory.newInstance();
                SAXParser saxP = saxPF.newSAXParser();
                XMLReader xmlR = saxP.getXMLReader();
                GetNewsInfo getNewsInfo = new GetNewsInfo();
                xmlR.setContentHandler(getNewsInfo);

                InputSource is = new InputSource(url.openStream());
                is.setEncoding("UTF-8");
                xmlR.parse(is);
                //------ Main parse section end ------//
                // load parsing data & View

                return getNewsInfo.getData();
            } catch (Exception e) {
                progressOff();
                Log.e("Startup",e.toString());
                return null;
            }
        }

        @Override
        protected void onPostExecute(ArrayList<NewsData> arrayList) {
            if(arrayList == null) {
                makeToastLong("뉴스를 읽어오던 도중 오류가 발생했습니다.");
            } else {
                NewsListAdapter adapter = new NewsListAdapter(NewsActivity.this, arrayList);
                binding.lvNews.setAdapter(adapter);

                binding.lvNews.setOnItemClickListener((parentView, view, position, id) -> {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(arrayList.get(position).strlink));
                    startActivity(intent);
                });
            }
            progressOff();
        }
    }*/
}
