package com.candykick.startup.view.loginAccount;

import android.content.Context;
import android.content.pm.ActivityInfo;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.hardware.Camera;
import android.hardware.camera2.CameraDevice;
import android.os.Bundle;
import android.view.Display;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.ViewGroup;

import com.candykick.startup.R;
import com.candykick.startup.view.base.BaseActivity;
import com.candykick.startup.databinding.ActivityNameCardBinding;

import java.util.List;

/*
  2019.08.04
  android.hardware.Camera는 곧 Deprecated된다.
  따라서 신규 라이브러리로 업데이트하는 게 필수다.
 */

public class NameCardActivity extends BaseActivity<ActivityNameCardBinding> implements SurfaceHolder.Callback {

    boolean flashOn = false;

    private CameraDevice camera;
    private SurfaceHolder cameraHolder;
    private Camera myCamera;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        binding.setActivity(this);

        myCamera = Camera.open();
        //myCamera.setDisplayOrientation(90); : 카메라 방향 세로로 돌리기 위한 것

        cameraHolder = binding.svCamera.getHolder();
        cameraHolder.addCallback(this);
        cameraHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        addContentView(new DrawNamecardLine(this), new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
    }

    //사진 촬영
    public void btnFilm() {

    }

    //라이트 켜기, 끄기
    public void btnLight() {
        if(flashOn) {
            flashOn = false;
            Camera.Parameters param = myCamera.getParameters();
            param.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
            myCamera.setParameters(param);
        } else {
            flashOn = true;
            Camera.Parameters param = myCamera.getParameters();
            param.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
            myCamera.setParameters(param);
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        try {
            if(myCamera == null) {
                myCamera.setPreviewDisplay(holder);
                myCamera.startPreview();
            }

            Paint paint = new Paint();
            paint.setColor(Color.GREEN);
            paint.setStrokeWidth(5);
        } catch (Exception e) {
            makeToastLong("카메라를 실행할 수 없습니다. 다시 시도해 주세요.");
            finish();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        //View가 존재하지 않을 때
        if(cameraHolder.getSurface() == null) {
            return;
        }

        //작업을 위해 잠시 멈춘다.
        try {
            myCamera.stopPreview();
        } catch (Exception e) {
            //에러 나도 무시함
        }

        //카메라 설정을 다시 한다.
        Camera.Parameters parameters = myCamera.getParameters();
        List<String> focusModes = parameters.getSupportedFocusModes();
        if(focusModes.contains(Camera.Parameters.FOCUS_MODE_AUTO)) {
            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
        }
        myCamera.setParameters(parameters);

        //View를 재생성한다.
        try {
            myCamera.setPreviewDisplay(cameraHolder);
            myCamera.startPreview();
        } catch (Exception e) {}
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        if(myCamera != null) {
            myCamera.stopPreview();
            myCamera.release();
            myCamera = null;
        }
    }

    @Override
    protected int getLayoutId() { return R.layout.activity_name_card; }


    //카메라에 명함 라인 그려줌
    private class DrawNamecardLine extends View {

        public DrawNamecardLine(Context context) {
            super(context);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            Paint paint = new Paint();
            paint.setColor(Color.RED);
            paint.setStrokeWidth(5);

            //드로잉 관련 변수 셋팅
            int[] drawInfo = namecardRectInfo();
            int width = drawInfo[0]; int height = drawInfo[1]; int rectY = drawInfo[2]; int lineUnit = drawInfo[3];

            //선 그리기
            canvas.drawLine(50,rectY,50+(9*lineUnit),rectY, paint);
            canvas.drawLine(50,rectY,50,rectY+(5*lineUnit),paint);
            canvas.drawLine(width-50,rectY,width-50-(9*lineUnit),rectY,paint);
            canvas.drawLine(width-50,rectY,width-50,rectY+(5*lineUnit),paint);
            canvas.drawLine(50,height-rectY,50+(9*lineUnit),height-rectY,paint);
            canvas.drawLine(50,height-rectY,50,height-rectY-(5*lineUnit),paint);
            canvas.drawLine(width-50,height-rectY,width-50-(9*lineUnit),height-rectY,paint);
            canvas.drawLine(width-50,height-rectY,width-50,height-rectY-(5*lineUnit),paint);

            //사각형 위에 텍스트 써넣기
            canvas.drawText("사각형 크기에 맞춰서 명함을 찍어주세요",width/2,20,paint);
        }
    }

    //화면 크기에서 사각형의 크기와 관련된 변수들을 구해주는 함수
    private int[] namecardRectInfo() {
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        int width = size.x;
        int height = size.y;

        double nameCardHeight = 5*(width-100) / 9;
        int rectY = (int)((height - nameCardHeight) / 2);

        int rectLineLength = width/40;

        int[] result = new int[]{width, height, rectY, rectLineLength};
        return result;
    }
}
