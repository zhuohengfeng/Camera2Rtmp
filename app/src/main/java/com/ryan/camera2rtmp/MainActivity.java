package com.ryan.camera2rtmp;

import android.Manifest;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.TextView;

import com.ryan.camera2rtmp.stream.MediaPublisher;
import com.ryan.camera2rtmp.utils.Logger;
import com.ryan.camera2rtmp.utils.PermissionsUtils;
import com.ryan.camera2rtmp.video.VideoGatherManager;
import com.ryan.camera2rtmp.video.camera.CameraSurfaceView;

public class MainActivity extends AppCompatActivity {

    private final int REQUEST_CODE_PERMISSIONS = 0x10;

    private CameraSurfaceView mCameraSurfaceView;

    private VideoGatherManager mVideoGatherManager;
    private MediaPublisher mMediaPublisher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MainApplication.setCurrentActivity(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            final String[] PERMISSIONS = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
            PermissionsUtils.checkAndRequestMorePermissions(this, PERMISSIONS, REQUEST_CODE_PERMISSIONS,
                    new PermissionsUtils.PermissionRequestSuccessCallBack() {

                        @Override
                        public void onHasPermission() {
                            setContentView(R.layout.activity_main);
                            initView();
                        }
                    });
        }

        mMediaPublisher = new MediaPublisher();
//        mMediaPublisher.setRtmpUrl("rtmp://118.126.107.250:1935/live/room");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (PermissionsUtils.isPermissionRequestSuccess(grantResults)) {
            setContentView(R.layout.activity_main);
            initView();
        }
    }

    private void initView() {
        mCameraSurfaceView = findViewById(R.id.sf_camera);

        // 不断获取视频数据
        mVideoGatherManager = new VideoGatherManager(mCameraSurfaceView);
    }


    @Override
    protected void onResume() {
        super.onResume();
        mVideoGatherManager.onResume();

        mMediaPublisher.initVideoGather(mVideoGatherManager);
//        mMediaPublisher.initAudioGather();
//        mMediaPublisher.startGather();
        mMediaPublisher.startMediaEncoder();
    }


    @Override
    protected void onPause() {
        super.onPause();
        mVideoGatherManager.onPause();

//        mMediaPublisher.stopGather();
        mMediaPublisher.stopMediaEncoder();
//        mMediaPublisher.relaseRtmp();
    }



}
