package com.candykick.startup.view.userInfo;

import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;

import com.bumptech.glide.Glide;
import com.candykick.startup.R;
import com.candykick.startup.view.adviserAct.AdviserCareerAdapter;
import com.candykick.startup.view.base.BaseActivity;
import com.candykick.startup.databinding.ActivityUserInfoAdvModifyBinding;
import com.candykick.startup.view.loginAccount.JoinAdvAdapter;
import com.candykick.startup.view.loginAccount.JoinAdvData;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.kakao.auth.Session;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.MeV2ResponseCallback;
import com.kakao.usermgmt.response.MeV2Response;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class UserInfoAdvModifyActivity extends BaseActivity<ActivityUserInfoAdvModifyBinding> {

    private static final int PICK_FROM_ALBUM = 11111, PICK_FROM_CAMERA = 11112;
    private File namecardFile = null;

    private JoinAdvAdapter advAdapter;
    private AdviserCareerAdapter careerAdapter;
    private LinearLayoutManager linearLayoutManager;

    String profileimagePresent, profileimageNew, userjob, userintro, namecardPresent, namecardNew;
    ArrayList<HashMap<String, String>> career;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding.setActivity(this);

        //UserInfoAdvActivity에서 정보를 받아온다.
        Intent intent = getIntent();
        profileimagePresent = intent.getStringExtra("profileimage");
        userjob = intent.getStringExtra("userjob");
        userintro = intent.getStringExtra("userintro");
        namecardPresent = intent.getStringExtra("namecard");
        career = (ArrayList<HashMap<String, String>>)intent.getSerializableExtra("career");

        //경력 수정 RecyclerView를 선언하고, 경력 정보를 셋팅한다.
        ArrayList<JoinAdvData> careerList = new ArrayList<>();
        for(HashMap<String, String> careerInfo: career) {
            careerList.add(new JoinAdvData(careerInfo.get("careerdate"), careerInfo.get("careertitle")));
        }

        linearLayoutManager = new LinearLayoutManager(UserInfoAdvModifyActivity.this);
        binding.rvUserInfo4Adv.setLayoutManager(linearLayoutManager);
        advAdapter = new JoinAdvAdapter(UserInfoAdvModifyActivity.this, careerList);
        careerAdapter = new AdviserCareerAdapter(UserInfoAdvModifyActivity.this, career);
        binding.rvUserInfo4Adv.setAdapter(careerAdapter);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(UserInfoAdvModifyActivity.this, linearLayoutManager.getOrientation());
        binding.rvUserInfo4Adv.addItemDecoration(dividerItemDecoration);

        //EditText 및 명함 ImageView에 불러온 정보를 셋팅한다.
        binding.etModify1Adv.setText(userjob);
        binding.tvAdvModify1Length.setText(userjob.length()+"자/20자");
        binding.etModify2Adv.setText(userintro);
        binding.tvAdvModify2Length.setText(userintro.length()+"자/20자");
        Glide.with(UserInfoAdvModifyActivity.this).load(namecardPresent).into(binding.ivUserInfo3Adv);

        //현재 직함, 한줄소개 입력할 때마다 글자수 표시
        binding.etModify1Adv.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                int length = s.toString().length();
                binding.tvAdvModify1Length.setText(length+"자/20자");
                if(length >= 20) {
                    binding.tvAdvModify1Length.setTextColor(Color.RED);
                } else {
                    binding.tvAdvModify1Length.setTextColor(getResources().getColor(R.color.set2White));
                }
            }
        });

        binding.etModify2Adv.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                int length = s.toString().length();
                binding.tvAdvModify2Length.setText(length+"자/20자");
                if(length == 20) {
                    binding.tvAdvModify2Length.setTextColor(Color.RED);
                } else {
                    binding.tvAdvModify2Length.setTextColor(getResources().getColor(R.color.set2White));
                }
            }
        });
    }

    //명함 수정 버튼 클릭 시
    public void modifyNamecard() {
        AlertDialog.Builder builder = new AlertDialog.Builder(UserInfoAdvModifyActivity.this);
        builder.setTitle("이미지를 어디서 가져오시겠습니까?");
        builder.setMessage("이미지를 가져올 곳을 선택해주세요.");
        builder.setPositiveButton("카메라", (dialog, which) -> {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

            //Intent intent = new Intent(getActivity(), NameCardActivity.class);

            try {
                namecardFile = createImageFile();
            } catch (Exception e) {
                makeToastLong("이미지 처리 중 오류가 발생했습니다. 다시 시도해 주세요. 오류: "+e.toString());
                e.printStackTrace();
            }

            if(namecardFile != null) {
                if(Build.VERSION.SDK_INT >= 24) {
                    Uri photoUri = FileProvider.getUriForFile(UserInfoAdvModifyActivity.this, "com.candykick.startup.provider", namecardFile);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                    startActivityForResult(intent, PICK_FROM_CAMERA);
                } else {
                    Uri photoUri = Uri.fromFile(namecardFile);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                    startActivityForResult(intent, PICK_FROM_CAMERA);
                }
            }
        });
        builder.setNegativeButton("갤러리", (dialog,which) -> {
            CropImage.activity()
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(9,5)
                    .start(UserInfoAdvModifyActivity.this);
            /*Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
            startActivityForResult(intent, PICK_FROM_ALBUM);*/
        });
        builder.show();
    }

    public void addCareer() {
        if(binding.btnJoinAddRaw.getText().toString().equals("추가하기")) {
            advAdapter.add();
            binding.rvUserInfo4Adv.setAdapter(advAdapter);

            binding.btnJoinAddRaw.setText("적용하기");
        } else {
            if(advAdapter.getCareerByHashMap() == null) {
                makeToast("빈칸을 채워주세요.");
            } else {
                careerAdapter = new AdviserCareerAdapter(UserInfoAdvModifyActivity.this, advAdapter.getCareerByHashMap());
                binding.rvUserInfo4Adv.setAdapter(careerAdapter);

                binding.btnJoinAddRaw.setText("추가하기");
            }
        }
    }

    //'완료' 버튼 클릭 시
    public void modifyEnd() {
        progressOn();

        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("userjob",binding.etModify1Adv.getText().toString());
        userInfo.put("userintro",binding.etModify2Adv.getText().toString());
        userInfo.put("career",advAdapter.getCareerByHashMap());

        if(namecardFile != null) {
            try {
                // Uri 스키마를 content:///에서 file:///로 변경한다.
                Bitmap bitmap = BitmapFactory.decodeFile(namecardFile.getPath());

                StorageReference mountainImagesRef = FirebaseStorage.getInstance().getReference().child("User/" + System.currentTimeMillis() + ".jpg");
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 20, baos);
                byte[] data2 = baos.toByteArray();
                UploadTask uploadTask = mountainImagesRef.putBytes(data2);

                uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if (!task.isSuccessful()) {
                            progressOff();
                            makeToastLong("명함을 등록하지 못했습니다." + task.getException().getMessage());
                            throw task.getException();
                        }

                        return mountainImagesRef.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()) {
                            String profileResult = task.getResult().toString();
                            userInfo.put("namecard", profileResult);

                            changeInfo(userInfo);
                        }
                    }
                });
            } catch (Exception e) {
                progressOff();
                makeToastLong("프로필 사진을 등록하지 못했습니다." + e.getMessage());
                Intent intent = new Intent();
                setResult(2,intent);
                finish();
            }
        } else {
            changeInfo(userInfo);
        }
    }

    private void changeInfo(Map<String, Object> userInfo) {
        if(Session.getCurrentSession().isOpened()) {
            Log.d("Startup", "Kakao");
            UserManagement.getInstance().me(new MeV2ResponseCallback() {
                @Override
                public void onSessionClosed(ErrorResult errorResult) {
                    progressOff();
                    makeToast("정보를 불러오지 못했습니다. 다시 시도해 주세요: " + errorResult.toString());
                    Intent intent = new Intent();
                    setResult(RESULT_CANCELED,intent);
                    finish();
                }

                @Override
                public void onSuccess(MeV2Response result) {
                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    db.collection("UserCeo")
                            .document(""+result.getId()).set(userInfo, SetOptions.merge())
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void avoid) {
                                    progressOff();
                                    makeToast("유저 정보를 변경했습니다.");
                                    Intent intent = new Intent();
                                    setResult(RESULT_OK,intent);
                                    finish();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    progressOff();
                                    Intent intent = new Intent();
                                    setResult(2,intent);
                                    finish();
                                }
                            });
                }
            });
        } else if(FirebaseAuth.getInstance().getCurrentUser() != null) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("UserCeo")
                    .document(FirebaseAuth.getInstance().getCurrentUser().getUid()).set(userInfo,SetOptions.merge())
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void avoid) {
                            progressOff();
                            makeToast("유저 정보를 변경했습니다.");
                            Intent intent = new Intent();
                            setResult(RESULT_OK,intent);
                            finish();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressOff();
                            Intent intent = new Intent();
                            setResult(2,intent);
                            finish();
                        }
                    });
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode != RESULT_OK) {
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
                cursor = getContentResolver().query(photoUri, proj, null, null, null);

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

            Glide.with(this).load(namecardFile).into(binding.ivUserInfo3Adv);
            binding.btnAdvNamecardChange.setAlpha(0f);
        }

        else if(requestCode == PICK_FROM_CAMERA) {
            CropImage.activity(android.net.Uri.parse(namecardFile.toURI().toString()))
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(9,5)
                    .start(UserInfoAdvModifyActivity.this);
        }

        else if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if(resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                //binding.ivjiCard.setBackground(new ShapeDrawable(new OvalShape()));
                //binding.ivjiCard.setClipToOutline(true);
                Glide.with(this).load(resultUri).into(binding.ivUserInfo3Adv);
                binding.btnAdvNamecardChange.setAlpha(0f);

                namecardFile = new File(resultUri.getPath());
            } else if(resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                makeToast("오류가 발생했습니다: "+error.getMessage());
            }
        }
    }

    //프로필 사진&명함용 사진을 만들 때 새 파일을 생성하는 함수.
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
    protected int getLayoutId() { return R.layout.activity_user_info_adv_modify; }
}
