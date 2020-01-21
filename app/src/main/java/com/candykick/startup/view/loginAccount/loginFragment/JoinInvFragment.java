package com.candykick.startup.view.loginAccount.loginFragment;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.view.View;
import android.widget.ArrayAdapter;

import com.bumptech.glide.Glide;
import com.candykick.startup.R;
import com.candykick.startup.view.base.BaseFragment;
import com.candykick.startup.databinding.FragmentJoinInvBinding;
import com.candykick.startup.view.loginAccount.NameCardActivity;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.app.Activity.RESULT_OK;


public class JoinInvFragment extends BaseFragment<FragmentJoinInvBinding> implements JoinInvInterface {

    private static final int PICK_FROM_ALBUM = 11111, PICK_FROM_CAMERA = 11112;
    private File namecardFile = null;

    String[] strInvMoney = new String[]{"500만원~1000만원","1000만원~1500만원","1500만원~2000만원","2000만원~2500만원","2500만원~3000만원","3000만원~5000만원","5000만원 이상"};
    String strCanInv = "";

    public JoinInvFragment() {
        // Required empty public constructor
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.setFragment(this);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), R.layout.spinner_item, getResources().getStringArray(R.array.strMoney));
        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        binding.spjiMoney.setAdapter(adapter);
        ArrayAdapter<String> adapter2 = new ArrayAdapter<>(getActivity(), R.layout.spinner_item, getResources().getStringArray(R.array.strInvType));
        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        binding.spjiTypeInvest.setAdapter(adapter2);

        /*final ArrayAdapter<String> moneyAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, strInvMoney);
        binding.lvjiMoney.setAdapter(moneyAdapter);

        binding.lvjiMoney.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                view.setBackgroundColor(0);
                strCanInv = moneyAdapter.getItem(position);
            }
        });*/
    }

    @Override
    public String getWantInvMoney() {
        return binding.spjiMoney.getSelectedItem().toString();
    }
    @Override
    public String getWantInvType() {
        return binding.spjiTypeInvest.getSelectedItem().toString();
    }
    @Override
    public File getNamecardFile() {
        return namecardFile;
    }

    //명함 이미지 클릭 시
    public void fnNameCard() {
        //Intent intent = new Intent(getActivity(), NameCardActivity.class);
        //startActivity(intent);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("이미지를 어디서 가져오시겠습니까?");
        builder.setMessage("이미지를 가져올 곳을 선택해주세요.");
        builder.setPositiveButton("카메라", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                Intent intent = new Intent(getActivity(), NameCardActivity.class);

                try {
                    namecardFile = createImageFile();
                } catch (Exception e) {
                    makeToastLong("이미지 처리 중 오류가 발생했습니다. 다시 시도해 주세요. 오류: "+e.toString());
                    e.printStackTrace();
                }

                if(namecardFile != null) {
                    if(Build.VERSION.SDK_INT >= 24) {
                        Uri photoUri = FileProvider.getUriForFile(getActivity(), "com.candykick.startup.provider", namecardFile);
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                        startActivityForResult(intent, PICK_FROM_CAMERA);
                    } else {
                        Uri photoUri = Uri.fromFile(namecardFile);
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
                        .setAspectRatio(9,5)
                        .start(getContext(), JoinInvFragment.this);
                /*Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
                startActivityForResult(intent, PICK_FROM_ALBUM);*/
            }
        });
        builder.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode != Activity.RESULT_OK) {
            makeToast("선택된 사진이 없습니다.");

            if(namecardFile != null ) {
                if(namecardFile.exists()) {
                    if(namecardFile.delete()) {
                        namecardFile = null;
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

                namecardFile = new File(cursor.getString(column_index));
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }

            Glide.with(this).load(namecardFile).into(binding.ivjiCard);
        }

        else if(requestCode == PICK_FROM_CAMERA) {
            CropImage.activity(android.net.Uri.parse(namecardFile.toURI().toString()))
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(9,5)
                    .start(getContext(), JoinInvFragment.this);
        }

        else if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if(resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                binding.ivjiCard.setBackground(new ShapeDrawable(new OvalShape()));
                binding.ivjiCard.setClipToOutline(true);
                Glide.with(this).load(resultUri).into(binding.ivjiCard);

                namecardFile = new File(resultUri.getPath());
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
        return R.layout.fragment_join_inv;
    }

    @Override
    public Context getContext() {
        return getActivity();
    }
}
