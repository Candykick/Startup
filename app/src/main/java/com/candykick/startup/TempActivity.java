package com.candykick.startup;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.candykick.startup.view.base.BaseActivity;
import com.candykick.startup.databinding.ActivityTempBinding;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

public class TempActivity extends BaseActivity<ActivityTempBinding> {

    String temp = "dfdsfsafasdfsfsdvcxzvsfa";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding.setActivity(this);

        int startIndex = temp.indexOf("src=\"");
        Log.d("Startup 1",startIndex+"");
        /*String temp2 = temp.substring(startIndex);
        Log.d("Startup 2",temp2);
        int endIndex = temp2.indexOf("\" alt=");
        Log.d("Startup 3",endIndex+"");
        String temp3 = temp2.substring(0,endIndex);
        Log.d("Startup 4",temp3);
        /*Parser parser = new Parser();
        parser.onFinish(new OnTaskCompleted() {

            //what to do when the parsing is done
            @Override
            public void onTaskCompleted(List<Article> list) {
                String result = "";
                for(Article article : list) {
                    result += article.getTitle();
                    result += ",";
                }

                binding.tvTemp.setText(result);
            }

            //what to do in case of error
            @Override
            public void onError(Exception e) {
                // Handle the exception
            }
        });
        parser.execute("http://feeds.feedburner.com/venturesquare");

        /*try {
            binding.tvTemp.setText(new tempClass().execute().get());
        } catch (Exception e) {
            binding.tvTemp.setText(e.toString());
        }*/
    }

    @Override
    public int getLayoutId() { return R.layout.activity_temp; }

    private class tempClass extends AsyncTask<String,Void,String> {
        @Override
        protected String doInBackground(String... params) {
            URL url = null;
            try {
                url = new URL("http://feeds.feedburner.com/venturesquare");
            } catch(MalformedURLException e1) {
                e1.printStackTrace();
            }

            InputStream in = null;
            try {
                in = url.openStream();
                byte[] buffer = new byte[128];
                int readCount = 0;
                StringBuilder result = new StringBuilder();

                while((readCount = in.read(buffer)) != -1) {
                    String part = new String(buffer, 0, readCount);
                    result.append(part);
                }

                return result.toString();
            }
            catch (IOException e) {
                return e.toString();
            }
        }
    }
}
