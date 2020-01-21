package com.candykick.startup.util;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Created by candykick on 2019. 8. 12..
 */

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {
    private static final String TAG = "MyFirebaseIIDService";

    @Override
    public void onTokenRefresh() {
        String token = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG,"Refreshed token : "+token);

        //SharedPreferences prefLogin = getSharedPreferences("prefLogin", Context.MODE_PRIVATE);
        //String id = prefLogin.getString("id", "");

        //if(!id.equals("")) {
            //sendRegistrationToServer(id, token);
        //}
    }
}