package com.candykick.startup.view.marketAct.addNewMarket;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.support.v7.view.ContextThemeWrapper;
import android.view.View;

import com.bumptech.glide.Glide;
import com.candykick.startup.R;
import com.candykick.startup.view.base.BaseFragment;
import com.candykick.startup.databinding.FragmentAddnewmarketImageBinding;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 */
public class AddMarketImageFragment extends BaseFragment<FragmentAddnewmarketImageBinding> implements AddMarketImageInterface {

    private static final int PICK_FROM_ALBUM = 11111, PICK_FROM_CAMERA = 11112;
    private File tempFile = null;

    public AddMarketImageFragment() {
        // Required empty public constructor
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.ivANMMainImage.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(getActivity(), R.style.AlertDialogCustom));
            builder.setTitle("이미지를 어디서 가져오시겠습니까?");
            builder.setMessage("이미지를 가져올 곳을 선택해주세요.");
            builder.setPositiveButton("카메라", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                    try {
                        tempFile = createImageFile();
                    } catch (Exception e) {
                        makeToastLong("이미지 처리 중 오류가 발생했습니다. 다시 시도해 주세요. 오류코드: "+e.toString());
                        e.printStackTrace();
                    }

                    if(tempFile != null) {
                        if(Build.VERSION.SDK_INT >= 24) {
                            Uri photoUri = FileProvider.getUriForFile(getActivity(), "com.candykick.startup.provider", tempFile);
                            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                            startActivityForResult(intent, PICK_FROM_CAMERA);
                        } else {
                            Uri photoUri = Uri.fromFile(tempFile);
                            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                            startActivityForResult(intent, PICK_FROM_CAMERA);
                        }
                    }
                }
            });
            builder.setNegativeButton("갤러리", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    CropImage.activity()
                            .setGuidelines(CropImageView.Guidelines.ON)
                            .start(getContext(), AddMarketImageFragment.this);
                    /*Intent intent = new Intent(Intent.ACTION_PICK);
                    intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
                    startActivityForResult(intent, PICK_FROM_ALBUM);*/
                }
            });
            builder.show();
        });
    }


    public File getMainImage() {
        if(tempFile == null) {
            makeToast("메인 이미지를 선택해주세요.");
        }
        return tempFile;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode != RESULT_OK) {
            makeToast("선택된 사진이 없습니다.");

            if(tempFile != null ) {
                if(tempFile.exists()) {
                    if(tempFile.delete()) {
                        tempFile = null;
                    }
                }
            }
            return;
        }

        else if(requestCode == PICK_FROM_ALBUM) {
            Uri photoUri = data.getData();
            Cursor cursor = null;

            try {
                // Uri 스키마를 content:///에서 file:///로 변경한다.
                String[] proj = {MediaStore.Images.Media.DATA};
                assert photoUri != null; //photoUri가 null이면 강제종료
                cursor = getActivity().getContentResolver().query(photoUri, proj, null, null, null);

                assert cursor != null; //cursor가 null이면 강제종료
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);

                cursor.moveToFirst();

                tempFile = new File(cursor.getString(column_index));
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }

            Glide.with(this).load(tempFile).into(binding.ivANMMainImage);
        }

        else if(requestCode == PICK_FROM_CAMERA) {
            CropImage.activity(android.net.Uri.parse(tempFile.toURI().toString()))
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .start(getContext(), AddMarketImageFragment.this);
            //Glide.with(this).load(tempFile).into(binding.ivANMMainImage);
        }

        else if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if(resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                Glide.with(this).load(resultUri).into(binding.ivANMMainImage);

                tempFile = new File(resultUri.getPath());
            } else if(resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                makeToast("오류가 발생했습니다: "+error.getMessage());
            }
        }
    }

    private File createImageFile() throws IOException {
        // 이미지 파일 이름: startup_{시간}
        String timeStamp = new SimpleDateFormat("HHmmss").format(new Date());
        String imageFileName = "startup_"+timeStamp;
        // 이미지가 저장될 폴더 이름: startup
        File storageDir = new File(Environment.getExternalStorageDirectory()+"/startup/");
        if(!storageDir.exists())
            storageDir.mkdirs();
        // 빈 파일 생성
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);

        return image;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_addnewmarket_image;
    }

    @Override
    public Context getContext() {
        return getActivity();
    }
}
