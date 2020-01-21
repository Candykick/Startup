package com.candykick.startup.view.userInfo;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.support.v7.view.ContextThemeWrapper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;

import com.bumptech.glide.Glide;
import com.candykick.startup.R;
import com.candykick.startup.view.base.BaseActivity;
import com.candykick.startup.databinding.ActivityUserInfoModifyBinding;
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

public class UserInfoModifyActivity extends BaseActivity<ActivityUserInfoModifyBinding> {

    private static final int PICK_FROM_ALBUM = 11111, PICK_FROM_CAMERA = 11112;
    private static final int REQUEST_CHANGE_USER_INFO              = 9794;
    private File tempFile = null;

    String userType;
    String profileOld;
    ArrayList<String> userJobTags = new ArrayList<>();
    boolean isInfo1First;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding.setActivity(this);

        viewSetting();

        binding.btnUserModify11.setOnClickListener(BooleanButtonListener);
        binding.btnUserModify12.setOnClickListener(BooleanButtonListener);

        //한줄소개 입력할 때마다 글자수 표시
        binding.etUserModify4.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                int length = s.toString().length();
                binding.tvUserModify4Length.setText(length+"자/20자");
                if(length >= 20) {
                    binding.tvUserModify4Length.setTextColor(Color.RED);
                } else {
                    binding.tvUserModify4Length.setTextColor(getResources().getColor(R.color.set2White));
                }
            }
        });
        
        //역할에서 '기타 역할' 체크 시 기타 역할 적는 EditText 나오게 하는 리스너
        binding.cbciOther2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    binding.etUserModify5.setVisibility(View.VISIBLE);
                } else {
                    binding.etUserModify5.setVisibility(View.GONE);
                }
            }
        });
    }

    public void modifyProfile() {
        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(UserInfoModifyActivity.this, R.style.AlertDialogCustom));
        builder.setTitle("프로필사진 바꾸기");
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
                        Uri photoUri = FileProvider.getUriForFile(UserInfoModifyActivity.this, "com.candykick.startup.provider", tempFile);
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
                CropImage.activity().setGuidelines(CropImageView.Guidelines.ON)
                        .setCropShape(CropImageView.CropShape.OVAL)
                        .setAspectRatio(1,1)
                        .start(UserInfoModifyActivity.this);
                /*Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
                startActivityForResult(intent, PICK_FROM_ALBUM);*/
            }
        });
        builder.show();
    }
    
    public void modifyJobTags() {
        binding.btnJobTagsChange.setAlpha(0f);
        Intent intent = new Intent(UserInfoModifyActivity.this, UserInfoTypeBusinessActivity.class);
        intent.putExtra("jobTags",userJobTags);
        startActivityForResult(intent,REQUEST_CHANGE_USER_INFO);
    }

    public void modifyEnd() {
        progressOn();

        Map<String, Object> userInfo = new HashMap<>();
        if(userType.equals("유형: 창업희망자")) {
            if(isInfo1First) {
                userInfo.put("gender", 1);
            } else {
                userInfo.put("gender", 2);
            }

            userInfo.put("age", binding.spUserModify2.getSelectedItem().toString());
            userInfo.put("role", getCeoRole());
            userInfo.put("businesstype", getTypeBusiness());
            //userInfo.put("istypeopen", typeBusinessFragment.isTypeOpen());
        } else if(userType.equals("유 형 :  투 자 자")) {
            userInfo.put("wantinvjob", getTypeBusiness());
            userInfo.put("wantinvcapital", binding.spUserModify2.getSelectedItem().toString());
            userInfo.put("wantinvtype", binding.spUserModify3.getSelectedItem().toString());
        } else if(userType.equals("유형: 외주사업가")) {
            userInfo.put("userDes", binding.etUserModify4.getText().toString());
            userInfo.put("taxinvoicepossible",isInfo1First);
        }

        if(tempFile != null) {
            try {
                // Uri 스키마를 content:///에서 file:///로 변경한다.
                Bitmap bitmap = BitmapFactory.decodeFile(tempFile.getPath());

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
                            makeToastLong("프로필 사진을 등록하지 못했습니다." + task.getException().getMessage());
                            throw task.getException();
                        }

                        return mountainImagesRef.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()) {
                            String profileResult = task.getResult().toString();
                            userInfo.put("profileimage", profileResult);

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


    private void viewSetting() {
        Intent intent = getIntent();

        userType = intent.getStringExtra("usertype");

        binding.ivTitle0Modify.setBackground(new ShapeDrawable(new OvalShape()));
        binding.ivTitle0Modify.setClipToOutline(true);
        profileOld = intent.getStringExtra("profile");
        Glide.with(UserInfoModifyActivity.this).load(profileOld).into(binding.ivTitle0Modify);

        if(userType.equals("유형: 창업희망자")) {
            binding.tvTitle1Modify.setVisibility(View.VISIBLE);
            binding.llUserModify1.setVisibility(View.VISIBLE);
            binding.tvTitle1Modify.setText("● 성별");
            binding.btnUserModify11.setText("남성");
            binding.btnUserModify12.setText("여성");
            if(intent.getStringExtra("userInfo1").equals("남성")) {
                binding.btnUserModify11.setBackgroundResource(R.drawable.rectgrid1);
                binding.btnUserModify12.setBackgroundResource(R.drawable.rectgrid2);
                isInfo1First = true;
            } else {
                binding.btnUserModify11.setBackgroundResource(R.drawable.rectgrid2);
                binding.btnUserModify12.setBackgroundResource(R.drawable.rectgrid1);
                isInfo1First = false;
            }

            binding.tvTitle2Modify.setVisibility(View.VISIBLE);
            binding.spUserModify2.setVisibility(View.VISIBLE);
            binding.tvTitle2Modify.setText("● 나잇대");
            String[] strsYear = getResources().getStringArray(R.array.strArrayYear);
            ArrayAdapter<String> adapter = new ArrayAdapter<>(UserInfoModifyActivity.this, R.layout.spinner_item, strsYear);
            adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
            binding.spUserModify2.setAdapter(adapter);
            for(int i=0;i<strsYear.length;i++) {
                if(intent.getStringExtra("userInfo2").equals(strsYear[i])) {
                    binding.spUserModify2.setSelection(i);
                    break;
                }
            }

            binding.tvTitle3Modify.setVisibility(View.GONE);
            binding.spUserModify3.setVisibility(View.GONE);

            binding.tvTitle4Modify.setVisibility(View.GONE);
            binding.etUserModify4.setVisibility(View.GONE);
            binding.tvUserModify4Length.setVisibility(View.GONE);

            binding.tvTitle5Modify.setVisibility(View.VISIBLE);
            binding.cbciCeo2.setVisibility(View.VISIBLE);
            binding.cbciProvider2.setVisibility(View.VISIBLE);
            binding.cbciDeveloper2.setVisibility(View.VISIBLE);
            binding.cbciDesigner2.setVisibility(View.VISIBLE);
            binding.cbciMarketer2.setVisibility(View.VISIBLE);
            binding.cbciPM2.setVisibility(View.VISIBLE);
            binding.cbciOther2.setVisibility(View.VISIBLE);
            binding.etUserModify5.setVisibility(View.GONE);
            String[] userInfo3 = intent.getStringExtra("userInfo3").split(", ");
            for(String userInfo: userInfo3) {
                switch (userInfo) {
                    case "대표":
                        binding.cbciCeo2.setChecked(true);
                        break;
                    case "기획자":
                        binding.cbciProvider2.setChecked(true);
                        break;
                    case "개발자":
                        binding.cbciDeveloper2.setChecked(true);
                        break;
                    case "디자이너":
                        binding.cbciDesigner2.setChecked(true);
                        break;
                    case "마케터":
                        binding.cbciMarketer2.setChecked(true);
                        break;
                    case "프로젝트 매니저":
                        binding.cbciPM2.setChecked(true);
                        break;
                    case "기타 역할":
                        binding.cbciOther2.setChecked(true);
                        binding.etUserModify5.setVisibility(View.VISIBLE);
                        break;
                }
            }


            binding.tvTitle6Modify.setVisibility(View.VISIBLE);
            binding.tgUserModify.setVisibility(View.VISIBLE);
            binding.btnJobTagsChange.setVisibility(View.VISIBLE);
            binding.tvTitle6Modify.setText("● 업종");
            binding.btnJobTagsChange.setText("업종 변경하기");
            userJobTags = intent.getStringArrayListExtra("jobTags");
            binding.tgUserModify.setTags(userJobTags);
        } else if(userType.equals("유 형 :  투 자 자")) {
            binding.tvTitle1Modify.setVisibility(View.GONE);
            binding.llUserModify1.setVisibility(View.GONE);

            binding.tvTitle2Modify.setVisibility(View.VISIBLE);
            binding.spUserModify2.setVisibility(View.VISIBLE);
            binding.tvTitle2Modify.setText("● 투자희망금액");
            String[] strsMoney = getResources().getStringArray(R.array.strMoney);
            ArrayAdapter<String> adapter = new ArrayAdapter<>(UserInfoModifyActivity.this, R.layout.spinner_item, strsMoney);
            adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
            binding.spUserModify2.setAdapter(adapter);
            for(int i=0;i<strsMoney.length;i++) {
                if(intent.getStringExtra("userInfo1").equals(strsMoney[i])) {
                    binding.spUserModify2.setSelection(i);
                    break;
                }
            }

            binding.tvTitle3Modify.setVisibility(View.VISIBLE);
            binding.spUserModify3.setVisibility(View.VISIBLE);
            String[] strsInvType = getResources().getStringArray(R.array.strInvType);
            ArrayAdapter<String> adapter2 = new ArrayAdapter<>(UserInfoModifyActivity.this, R.layout.spinner_item, strsInvType);
            adapter2.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
            binding.spUserModify3.setAdapter(adapter2);
            for(int i=0;i<strsInvType.length;i++) {
                if(intent.getStringExtra("userInfo2").equals(strsInvType[i])) {
                    binding.spUserModify3.setSelection(i);
                    break;
                }
            }

            binding.tvTitle4Modify.setVisibility(View.GONE);
            binding.etUserModify4.setVisibility(View.GONE);
            binding.tvUserModify4Length.setVisibility(View.GONE);

            binding.tvTitle5Modify.setVisibility(View.GONE);
            binding.cbciCeo2.setVisibility(View.GONE);
            binding.cbciProvider2.setVisibility(View.GONE);
            binding.cbciDeveloper2.setVisibility(View.GONE);
            binding.cbciDesigner2.setVisibility(View.GONE);
            binding.cbciMarketer2.setVisibility(View.GONE);
            binding.cbciPM2.setVisibility(View.GONE);
            binding.cbciOther2.setVisibility(View.GONE);
            binding.etUserModify5.setVisibility(View.GONE);

            binding.tvTitle6Modify.setVisibility(View.VISIBLE);
            binding.tgUserModify.setVisibility(View.VISIBLE);
            binding.btnJobTagsChange.setVisibility(View.VISIBLE);
            binding.tvTitle6Modify.setText("● 투자희망업종");
            binding.btnJobTagsChange.setText("투자희망업종 변경하기");
            userJobTags = intent.getStringArrayListExtra("jobTags");
            binding.tgUserModify.setTags(userJobTags);
        } else if(userType.equals("유형: 외주사업가")) {
            binding.tvTitle1Modify.setVisibility(View.VISIBLE);
            binding.llUserModify1.setVisibility(View.VISIBLE);
            binding.tvTitle1Modify.setText("● 세금계산서 여부");
            binding.btnUserModify11.setText("발행 가능");
            binding.btnUserModify12.setText("발행 불가");
            if(intent.getStringExtra("userInfo1").equals("발행 가능")) {
                binding.btnUserModify11.setBackgroundResource(R.drawable.rectgrid1);
                binding.btnUserModify12.setBackgroundResource(R.drawable.rectgrid2);
                isInfo1First = true;
            } else {
                binding.btnUserModify11.setBackgroundResource(R.drawable.rectgrid2);
                binding.btnUserModify12.setBackgroundResource(R.drawable.rectgrid1);
                isInfo1First = false;
            }

            binding.tvTitle2Modify.setVisibility(View.GONE);
            binding.spUserModify2.setVisibility(View.GONE);

            binding.tvTitle3Modify.setVisibility(View.GONE);
            binding.spUserModify3.setVisibility(View.GONE);

            binding.tvTitle4Modify.setVisibility(View.VISIBLE);
            binding.etUserModify4.setVisibility(View.VISIBLE);
            binding.tvUserModify4Length.setVisibility(View.VISIBLE);
            binding.etUserModify4.setText(intent.getStringExtra("userInfo2"));
            binding.tvUserModify4Length.setText(intent.getStringExtra("userInfo2").length()+"자/20자");

            binding.tvTitle5Modify.setVisibility(View.GONE);
            binding.cbciCeo2.setVisibility(View.GONE);
            binding.cbciProvider2.setVisibility(View.GONE);
            binding.cbciDeveloper2.setVisibility(View.GONE);
            binding.cbciDesigner2.setVisibility(View.GONE);
            binding.cbciMarketer2.setVisibility(View.GONE);
            binding.cbciPM2.setVisibility(View.GONE);
            binding.cbciOther2.setVisibility(View.GONE);
            binding.etUserModify5.setVisibility(View.GONE);

            binding.tvTitle6Modify.setVisibility(View.GONE);
            binding.tgUserModify.setVisibility(View.GONE);
            binding.btnJobTagsChange.setVisibility(View.GONE);
        }
    }

    private Button.OnClickListener BooleanButtonListener = new Button.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch(v.getId()) {
                case R.id.btnUserModify11:
                    binding.btnUserModify11.setBackgroundResource(R.drawable.rectgrid1);
                    binding.btnUserModify12.setBackgroundResource(R.drawable.rectgrid2);
                    isInfo1First = true;
                    break;
                case R.id.btnUserModify12:
                    binding.btnUserModify11.setBackgroundResource(R.drawable.rectgrid2);
                    binding.btnUserModify12.setBackgroundResource(R.drawable.rectgrid1);
                    isInfo1First = false;
                    break;
            }
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode != REQUEST_CHANGE_USER_INFO && resultCode != Activity.RESULT_OK) {
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

        else if(requestCode == REQUEST_CHANGE_USER_INFO && resultCode == Activity.RESULT_OK) {
            userJobTags = data.getStringArrayListExtra("result");
            binding.tgUserModify.setTags(userJobTags);
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

                tempFile = new File(cursor.getString(column_index));
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }

            binding.ivTitle0Modify.setBackground(new ShapeDrawable(new OvalShape()));
            binding.ivTitle0Modify.setClipToOutline(true);
            Glide.with(this).load(tempFile).into(binding.ivTitle0Modify);
        }

        else if(requestCode == PICK_FROM_CAMERA) {
            CropImage.activity(android.net.Uri.parse(tempFile.toURI().toString()))
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setCropShape(CropImageView.CropShape.OVAL)
                    .setAspectRatio(1,1)
                    .start(UserInfoModifyActivity.this);
        }

        else if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if(resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                binding.ivTitle0Modify.setBackground(new ShapeDrawable(new OvalShape()));
                binding.ivTitle0Modify.setClipToOutline(true);
                Glide.with(this).load(resultUri).into(binding.ivTitle0Modify);

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

    //수정값 처리하는 함수들
    private String getCeoRole() {
        String result = "";
        if(binding.cbciCeo2.isChecked()) {
            result+="대표, ";
        } if(binding.cbciProvider2.isChecked()) {
            result+="기획자, ";
        } if(binding.cbciDeveloper2.isChecked()) {
            result+="개발자, ";
        } if(binding.cbciDesigner2.isChecked()) {
            result+="디자이너, ";
        } if(binding.cbciMarketer2.isChecked()) {
            result+="마케터, ";
        } if(binding.cbciPM2.isChecked()) {
            result+="프로젝트 매니저, ";
        } if(binding.cbciOther2.isChecked()) {
            result+="기타 역할(";
            result+=binding.etUserModify5.getText().toString();
            result+=")";
        }

        if(result.charAt(result.length()-2) == ',') {
            result = result.substring(0,result.length()-2);
        }
        return result;
    }

    public String getTypeBusiness() {
        String[] typeBusiness = binding.tgUserModify.getTags();
        String result = typeBusiness[0];

        if (typeBusiness.length > 1) {
            for (int i = 1; i < typeBusiness.length; i++) {
                result = result + ", " + typeBusiness[i];
            }
        }

        return result;
    }

    @Override
    protected int getLayoutId() { return R.layout.activity_user_info_modify; }
}
