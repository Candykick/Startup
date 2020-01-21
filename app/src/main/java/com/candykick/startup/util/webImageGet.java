package com.candykick.startup.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import com.candykick.startup.util.ImageUtil;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;

/**
 * Created by candykick on 2019. 7. 31..
 */

public class webImageGet extends AsyncTask<String, Void, Bitmap> {
    @Override
    protected Bitmap doInBackground(String... params) {
        try {
            java.net.URL url = new java.net.URL(params[0]);
            HttpURLConnection connection = (HttpURLConnection) url
                    .openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            Bitmap result = new ImageUtil().getResizedBitmap(myBitmap,1);

            return result;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /*
    public Bitmap getResizedBitmap(Bitmap bm, int newHeight, int newWidth) {
    int width = bm.getWidth();
    int height = bm.getHeight();
    float scaleWidth = ((float) newWidth) / width;
    float scaleHeight = ((float) newHeight) / height;
    // CREATE A MATRIX FOR THE MANIPULATION
    Matrix matrix = new Matrix();
    // RESIZE THE BIT MAP
    matrix.postScale(scaleWidth, scaleHeight);

    // "RECREATE" THE NEW BITMAP
    Bitmap resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width, height,
            matrix, false);

    return resizedBitmap;
    }
     */
}
