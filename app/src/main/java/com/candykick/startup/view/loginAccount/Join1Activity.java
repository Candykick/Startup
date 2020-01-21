package com.candykick.startup.view.loginAccount;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.os.Bundle;
import android.support.v7.view.ContextThemeWrapper;
import android.text.InputType;
import android.util.Log;
import android.view.View;

import com.bumptech.glide.Glide;
import com.candykick.startup.R;
import com.candykick.startup.view.base.BaseActivity;
import com.candykick.startup.databinding.ActivityJoin1Binding;
import com.google.firebase.auth.FirebaseAuth;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.MeV2ResponseCallback;
import com.kakao.usermgmt.response.MeV2Response;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/*
5.29
실명인증 버튼 옆에 ? 버튼 놓고, 실명인증 받는 이유를 조그맣게 팝업으로 표시하는 방안이 있다.
전반적으로 UI가 너무 지저분함.
탭뷰 형식으로 구현해서, 추가 정보를 입력받는 부분을 2페이지로 놓는 방안을 고려해봤으나 스와이프하다가 버튼을 클릭할 우려가 있어 보류.
개인적으론 그냥 '다음으로' 버튼을 이용하는 쪽이 더 좋아 보인다. '굳이' 탭뷰를 쓸 이유가 없는 UI이기도 하고.
타 앱의 로그인 화면을 보면서 참고하는 방안도 좋을 것 같다.

5.31
이메일 체크하기
패스워드 길이 체크
카카오톡/구글 로그인 시 이메일 있으면 자동기입 및 패스워드 비활성화
이메일 직접입력 시 EditText 1개 길게, 아니면 뒤쪽에 이메일 도메인 표시(구현 가능할려나?)
이메일 형식에 맞게 입력했는지 체크하기

6.6
사진 파일 카메라에서 촬영 후 편집 안하고 뒤로가기 누르면 삭제가 안 되어 있다.(SDK 24에서 확인)
이름 가지고 프로필 자동으로 생성되게 할 수 있나? 구글처럼?
 */

public class Join1Activity extends BaseActivity<ActivityJoin1Binding> {

    private static final int PICK_FROM_ALBUM = 11111, PICK_FROM_CAMERA = 11112;

    int usertype; //1==창업희망자, 2==투자자, 3==조언가, 4==외주사업가
    String loginmethod, name, email, profile, uid;

    private File tempFile = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding.setActivity(this);

        binding.toolbar.btnToolBarLeft.setOnClickListener(v -> onBackPressed());
        binding.toolbar.tvToolBar.setText("기본정보 입력");

        Intent gintent = getIntent();
        usertype = gintent.getIntExtra("usertype", 1);
        loginmethod = gintent.getStringExtra("loginmethod");
        uid = gintent.getStringExtra("uid");

        if(loginmethod.equals("kakao")) {
            Log.d("Startup", "kakao");
            /*List<String> keys = new ArrayList<>();
            keys.add("properties.nickname");
            keys.add("properties.profile_image");
            keys.add("kakao_account.email");*/
            UserManagement.getInstance().me(new MeV2ResponseCallback() {
                @Override
                public void onSessionClosed(ErrorResult errorResult) {

                }

                @Override
                public void onSuccess(MeV2Response result) {
                    name = result.getNickname();
                    profile = result.getProfileImagePath();
                    if(profile == null) {
                        profile = "";
                    }
                    if(result.getKakaoAccount().hasEmail().getBoolean()) {
                        email = result.getKakaoAccount().getEmail();
                    } else {
                        email = "";
                    }

                    binding.etJoinNick.setText(name);
                    binding.etJoinPW.setText("카카오/구글로 가입하는 계정입니다.");
                    binding.etJoinPW.setTextSize(15);
                    binding.etJoinPW.setInputType(InputType.TYPE_TEXT_VARIATION_NORMAL);
                    binding.etJoinPW.setBackgroundColor(getResources().getColor(R.color.colorNotSelectable));
                    binding.etJoinPW.setClickable(false);
                    binding.etJoinPW.setFocusable(false);
                    binding.etJoinPW2.setText("카카오/구글로 가입하는 계정입니다.");
                    binding.etJoinPW2.setVisibility(View.GONE);

                    if(email.length() != 0) {
                        binding.etJoinEmail.setText(email);
                        //etEmail.setClickable(false);
                        //etEmail.setFocusable(false);
                    } if(profile.length() != 0) {
                        Glide.with(Join1Activity.this).load(profile).into(binding.ivJoinImage);
                    }
                }
            });
        } else if(loginmethod.equals("google")) {
            name = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
            email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
            profile = FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl().toString();

            binding.etJoinNick.setText(name);
            binding.etJoinPW.setText("카카오/구글로 가입하는 계정입니다.");
            binding.etJoinPW.setTextSize(15);
            binding.etJoinPW.setInputType(InputType.TYPE_TEXT_VARIATION_NORMAL);
            binding.etJoinPW.setBackgroundColor(getResources().getColor(R.color.colorNotSelectable));
            binding.etJoinPW.setClickable(false);
            binding.etJoinPW.setFocusable(false);
            binding.etJoinPW2.setText("카카오/구글로 가입하는 계정입니다.");
            binding.etJoinPW2.setVisibility(View.GONE);

            if(email.length() != 0) {
                binding.etJoinEmail.setText(email);
                //etEmail.setClickable(false);
                //etEmail.setFocusable(false);
            } if(profile.length() != 0) {
                Glide.with(this).load(profile).into(binding.ivJoinImage);
            }
        } else {
            name = ""; email = ""; profile = "";
        }
    }

    //'다음' 버튼 클릭 시
    public void next3() {
        if(binding.etJoinEmail.getText().toString().length() == 0) {
            makeToast("이메일을 입력해주세요.");
        } else if(binding.etJoinPW.getText().toString().length() == 0) {
            makeToast("패스워드를 입력해주세요.");
        } else if(binding.etJoinPW2.getText().toString().length() == 0) {
            makeToast("패스워드 확인을 입력해주세요.");
        } else if(binding.etJoinNick.getText().toString().length() == 0) {
            makeToast("닉네임을 입력해주세요.");
        } else if(!binding.etJoinPW.getText().toString().equals(binding.etJoinPW2.getText().toString())) {
            makeToast("패스워드가 일치하지 않습니다.");
            binding.etJoinPW.setText("");
            binding.etJoinPW2.setText("");
        } else if(binding.etJoinChatID.getText().toString().replace(" ","").length() == 0) {
            makeToast("채팅 ID를 입력해주세요.");
        } else {
            Intent intent;

            switch(usertype) {
                case 1: //창업희망자
                    intent = new Intent(Join1Activity.this, JoinCeoActivity.class);
                    intent.putExtra("loginmethod",loginmethod);
                    intent.putExtra("nickname",binding.etJoinNick.getText().toString());
                    intent.putExtra("email",binding.etJoinEmail.getText().toString());
                    intent.putExtra("password",binding.etJoinPW.getText().toString());
                    intent.putExtra("uid",uid);
                    intent.putExtra("chatid",binding.etJoinChatID.getText().toString());

                    if (profile.equals("") && tempFile == null) {
                        intent.putExtra("profileflag",2);
                        intent.putExtra("profile", "https://firebasestorage.googleapis.com/v0/b/startup-dbe79.appspot.com/o/1562769833457.png?alt=media&token=9008329f-3bb4-46eb-9444-0226990b44f2");
                    } else if(tempFile == null) {
                        intent.putExtra("profileflag",1);
                        intent.putExtra("profile",profile);
                    } else {
                        intent.putExtra("profileflag",0);
                        intent.putExtra("profile",Uri.fromFile(tempFile).toString());
                        intent.putExtra("profileImage",tempFile);
                    }

                    startActivity(intent);
                    break;
                case 2: //투자자
                    intent = new Intent(Join1Activity.this, JoinInvActivity.class);
                    intent.putExtra("loginmethod",loginmethod);
                    intent.putExtra("nickname",binding.etJoinNick.getText().toString());
                    intent.putExtra("email",binding.etJoinEmail.getText().toString());
                    intent.putExtra("password",binding.etJoinPW.getText().toString());
                    intent.putExtra("uid",uid);
                    intent.putExtra("chatid",binding.etJoinChatID.getText().toString());

                    if (profile.equals("") && tempFile == null) {
                        intent.putExtra("profileflag",2);
                        intent.putExtra("profile", "https://firebasestorage.googleapis.com/v0/b/startup-dbe79.appspot.com/o/1562769833457.png?alt=media&token=9008329f-3bb4-46eb-9444-0226990b44f2");
                    } else if(tempFile == null) {
                        intent.putExtra("profileflag",1);
                        intent.putExtra("profile",profile);
                    } else {
                        intent.putExtra("profileflag",0);
                        intent.putExtra("profile",Uri.fromFile(tempFile).toString());
                        intent.putExtra("profileImage",tempFile);
                    }

                    startActivity(intent);
                    break;
                case 3: //조언가
                    intent = new Intent(Join1Activity.this, JoinAdvActivity.class);
                    intent.putExtra("loginmethod",loginmethod);
                    intent.putExtra("nickname",binding.etJoinNick.getText().toString());
                    intent.putExtra("email",binding.etJoinEmail.getText().toString());
                    intent.putExtra("password",binding.etJoinPW.getText().toString());
                    intent.putExtra("uid",uid);
                    intent.putExtra("chatid",binding.etJoinChatID.getText().toString());

                    if (profile.equals("") && tempFile == null) {
                        intent.putExtra("profileflag",2);
                        intent.putExtra("profile", "https://firebasestorage.googleapis.com/v0/b/startup-dbe79.appspot.com/o/1562769833457.png?alt=media&token=9008329f-3bb4-46eb-9444-0226990b44f2");
                    } else if(tempFile == null) {
                        intent.putExtra("profileflag",1);
                        intent.putExtra("profile",profile);
                    } else {
                        intent.putExtra("profileflag",0);
                        intent.putExtra("profile",Uri.fromFile(tempFile).toString());
                        intent.putExtra("profileImage",tempFile);
                    }

                    startActivity(intent);
                    break;
                case 4: //외주사업가
                    intent = new Intent(Join1Activity.this, JoinOutActivity.class);
                    intent.putExtra("loginmethod",loginmethod);
                    intent.putExtra("nickname",binding.etJoinNick.getText().toString());
                    intent.putExtra("email",binding.etJoinEmail.getText().toString());
                    intent.putExtra("password",binding.etJoinPW.getText().toString());
                    intent.putExtra("uid",uid);
                    intent.putExtra("chatid",binding.etJoinChatID.getText().toString());

                    if (profile.equals("") && tempFile == null) {
                        intent.putExtra("profileflag",2);
                        intent.putExtra("profile", "https://firebasestorage.googleapis.com/v0/b/startup-dbe79.appspot.com/o/1562769833457.png?alt=media&token=9008329f-3bb4-46eb-9444-0226990b44f2");
                    } else if(tempFile == null) {
                        intent.putExtra("profileflag",1);
                        intent.putExtra("profile",profile);
                    } else {
                        intent.putExtra("profileflag",0);
                        intent.putExtra("profile",Uri.fromFile(tempFile).toString());
                        intent.putExtra("profileImage",tempFile);
                    }

                    startActivity(intent);
                    break;
                default:
                    makeToast("오류가 발생했습니다. 다시 시도해 주세요.");
                    intent = new Intent(Join1Activity.this, StartActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                    break;
            }
        }
    }

    //'실명인증 받기' 버튼 클릭 시
    /*public void fnJoinNice() {
        makeToast("구현중입니다.");
    }*/

    //프로필사진 선택 기능
    public void fnJoinImage() {
        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(Join1Activity.this, R.style.AlertDialogCustom));
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
                        Uri photoUri = FileProvider.getUriForFile(Join1Activity.this, "com.candykick.startup.provider", tempFile);
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
                        .start(Join1Activity.this);
                /*Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
                startActivityForResult(intent, PICK_FROM_ALBUM);*/
            }
        });
        builder.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode != Activity.RESULT_OK) {
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

            binding.ivJoinImage.setBackground(new ShapeDrawable(new OvalShape()));
            binding.ivJoinImage.setClipToOutline(true);
            Glide.with(this).load(tempFile).into(binding.ivJoinImage);
        }

        else if(requestCode == PICK_FROM_CAMERA) {
            CropImage.activity(android.net.Uri.parse(tempFile.toURI().toString()))
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setCropShape(CropImageView.CropShape.OVAL)
                    .setAspectRatio(1,1)
                    .start(Join1Activity.this);
        }

        else if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if(resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                binding.ivJoinImage.setBackground(new ShapeDrawable(new OvalShape()));
                binding.ivJoinImage.setClipToOutline(true);
                Glide.with(this).load(resultUri).into(binding.ivJoinImage);

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
    protected int getLayoutId() { return R.layout.activity_join1; }
}
