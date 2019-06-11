package com.ryan.camera2rtmp.video.camera;

import android.content.Context;
import android.hardware.Camera;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.ryan.camera2rtmp.utils.Logger;
import com.ryan.camera2rtmp.video.callback.CameraNV21DataListener;

public class CameraSurfaceView extends SurfaceView implements Camera.PreviewCallback, SurfaceHolder.Callback {

    // 操作相机
    private CameraUtil mCameraUtil;

    private CameraNV21DataListener nv21Listener;

    public CameraSurfaceView(Context context) {
        this(context, null);
    }

    public CameraSurfaceView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CameraSurfaceView(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public CameraSurfaceView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        mCameraUtil = new CameraUtil();
        getHolder().addCallback(this);
    }

    public void openCamera() {
        mCameraUtil.openCamera();
        this.post(new Runnable() {
            @Override
            public void run() {
                Logger.d("openCamera, startPreview+++++++++");
                mCameraUtil.startPreview(getHolder(), CameraSurfaceView.this);

                //这里可以获取真正的预览的分辨率，在这里要进行屏幕的适配，主要适配非16:9的屏幕
//                mTargetAspect = ((float) mCameraUtil.getCameraHeight()) / mCameraUtil.getCameraWidth();
            }
        });
    }


    public void closeCamera() {
        Logger.d("close Camear--------");
        mCameraUtil.releaseCamera();
    }


    @Override
    public void surfaceCreated(SurfaceHolder holder) {
//        mCameraUtil.handleCameraStartPreview(getHolder(), this);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {}

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        mCameraUtil.releaseCamera();
    }

    // 相机帧回调
    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {
        // 得到的NV21数据是 3110400，
        // 预览分辨率 1920*1080 = 2073600
        // NV21 YYYYY VUVUVUVU Y=1, U=1/4, V=1/4
        // 所以 2073600 * (1+1/4+1/4) = 3110400
        //Logger.d("onPreviewFrame data.length="+data.length); // 3110400
        camera.addCallbackBuffer(data);

        // 回调NV21帧数据
        if (nv21Listener != null) {
            nv21Listener.onCallback(data);
        }
    }

    public void setCameraNV21DataListener(CameraNV21DataListener listener) {
        this.nv21Listener = listener;
    }


    public CameraUtil getCameraUtil() {
        return mCameraUtil;
    }
}
