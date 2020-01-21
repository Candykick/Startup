package com.candykick.startup.util;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;

/**
 * Created by candykick on 2019. 8. 1..
 */

public class ImageUtil {

    String result;

    //프로필사진 업로드 함수
    public String uploadProfileImage(String url, int flag, Context context, StorageReference storageRef) {

        //flag == 0: 갤러리나 카메라에서 얻어온 사진. 1: 인터넷에서 가져온 사진.
        if(flag == 0) {
            Bitmap bitmap;
            Uri photoUri = Uri.parse(url);
            Cursor cursor = null;

            try {
                // Uri 스키마를 content:///에서 file:///로 변경한다.
                String[] proj = {MediaStore.Images.Media.DATA};
                assert photoUri != null; //photoUri가 null이면 강제종료
                cursor = context.getContentResolver().query(photoUri, proj, null, null, null);

                assert cursor != null; //cursor가 null이면 강제종료
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);

                cursor.moveToFirst();

                File tempFile = new File(cursor.getString(column_index));
                bitmap = BitmapFactory.decodeFile(tempFile.getPath());

                StorageReference mountainImagesRef = storageRef.child("User/"+System.currentTimeMillis()+".jpg");
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 20, baos);
                byte[] data2 = baos.toByteArray();
                UploadTask uploadTask = mountainImagesRef.putBytes(data2);

                uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if (!task.isSuccessful()) {
                            result = "Error::"+task.getException().getMessage();
                            throw task.getException();
                        }

                        return mountainImagesRef.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()) {
                            result = task.getResult().toString();
                        } else {
                            result = "Error::"+task.getException().getMessage();
                        }
                    }
                });

                return result;
            } catch (Exception e) {
                e.printStackTrace();
                return "Error::"+e.toString();
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
        } else {
            try {
                Bitmap bitmap;
                bitmap = new webImageGet().execute(new String[]{url}).get();
                StorageReference mountainImagesRef = storageRef.child("User/"+System.currentTimeMillis()+".jpg");
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 20, baos);
                byte[] data2 = baos.toByteArray();
                UploadTask uploadTask = mountainImagesRef.putBytes(data2);

                uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if (!task.isSuccessful()) {
                            result = "Error::"+task.getException().getMessage();
                            throw task.getException();
                        }

                        // Continue with the task to get the download URL
                        return mountainImagesRef.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()) {
                            result = task.getResult().toString();
                        } else {
                            result = "Error::"+task.getException().getMessage();
                        }
                    }
                });

                return result;
            } catch (Exception e) {
                e.printStackTrace();
                return "Error::"+e.toString();
            }
        }
    }

    //이미지 리사이징 함수.
    public Bitmap getResizedBitmap(Bitmap bm, int flag) {
        //flag == 1 : 프로필 사진용 리사이징(큰 쪽 기준, 가로 640이나 세로 640 고정)

        int width = bm.getWidth();
        int height = bm.getHeight();

        if(flag == 1) {
            float scale = 1.0f;
            if(width>height)
                scale = 640f / width;
            else
                scale = 640f / height;

            Matrix matrix = new Matrix();
            matrix.postScale(scale, scale);
            Bitmap resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width, height,
                    matrix, false);
            return resizedBitmap;
        } else {
            return null;
        }
    }
}
