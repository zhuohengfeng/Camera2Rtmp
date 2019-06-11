package com.ryan.camera2rtmp.video;

import com.ryan.camera2rtmp.stream.StreamProcessManager;
import com.ryan.camera2rtmp.utils.FileManager;
import com.ryan.camera2rtmp.utils.Logger;
import com.ryan.camera2rtmp.video.callback.CameraNV21DataListener;
import com.ryan.camera2rtmp.video.callback.CameraYUVDataListener;
import com.ryan.camera2rtmp.video.camera.CameraSurfaceView;
import com.ryan.camera2rtmp.video.camera.CameraUtil;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * 获取camera帧数据，进行H264编码
 */
public class VideoGatherManager implements CameraNV21DataListener {

    private static final boolean SAVE_FILE_FOR_TEST = true;
    private FileManager fileManager;

    private CameraSurfaceView mCameraSurfaceView;
    private CameraUtil mCameraUtil;

    private LinkedBlockingQueue<byte[]> mNV21Queue = new LinkedBlockingQueue<>();
    private Thread yuvConvertThread;
    private boolean isExit;

    private CameraYUVDataListener yuvDataListener;

    // 进行数据压缩后再发送
    private int scaleWidth = 480;
    private int scaleHeight = 640;

    public VideoGatherManager(CameraSurfaceView cameraSurfaceView) {
        this.mCameraSurfaceView = cameraSurfaceView;
        this.mCameraSurfaceView.setCameraNV21DataListener(this);

        this.mCameraUtil = mCameraSurfaceView.getCameraUtil();

        StreamProcessManager.encoderVideoinit(scaleWidth, scaleHeight, scaleWidth, scaleHeight);

        if (SAVE_FILE_FOR_TEST) {
            fileManager = new FileManager(FileManager.TEST_YUV_FILE);
        }
    }

    public void onResume() {
        if (this.mCameraSurfaceView != null) {
            this.mCameraSurfaceView.openCamera();
        }
        isExit = false;
        initWorkThread();
        if (yuvConvertThread != null) {
            yuvConvertThread.start();
        }
    }

    public void onPause() {
        if (this.mCameraSurfaceView != null) {
            this.mCameraSurfaceView.closeCamera();
        }
        isExit = true;
        if (yuvConvertThread != null) {
            yuvConvertThread.interrupt();
            yuvConvertThread = null;
        }
    }

    /**
     * 从队列中取出NV21数据，转换成YUV420P数据
     * YYYYY VUVUVUVUV ===>>>  YYYYYYYY UUUUUUU VVVVVV
     *
     * ffplay -f rawvideo -video_size 640x480 test.yuv
     */
    private void initWorkThread() {
        yuvConvertThread = new Thread("yuvConvertThread") {
            @Override
            public void run() {
                super.run();
                Logger.d("启动NV21->YUV420P线程");
                while (!isExit && !Thread.interrupted()) {
                    try {
                        byte[] srcData = mNV21Queue.take();
                        //Logger.d("得到NV21大小 "+srcData.length);

                        //生成I420(YUV标准格式数据及YUV420P)目标数据，生成后的数据长度width * height * 3 / 2
                        byte[] dstData = new byte[scaleWidth * scaleHeight * 3 / 2];
                        StreamProcessManager.compressYUV(srcData,
                                mCameraUtil.getCameraWidth(),
                                mCameraUtil.getCameraHeight(),
                                dstData, scaleHeight, scaleWidth);

                        if (yuvDataListener != null) {
                            yuvDataListener.onYUVDataReceiver(dstData, scaleWidth, scaleHeight);
                        }

                        if (SAVE_FILE_FOR_TEST) {
                            fileManager.saveFileData(dstData);
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                Logger.d("退出NV21->YUV420P线程");
            }
        };
    }

    public void setYuvDataListener(CameraYUVDataListener listener) {
        this.yuvDataListener = listener;
    }


    /**
     * 回调得到NV21原始数据
     * @param data
     */
    @Override
    public void onCallback(byte[] data) {
        if (data != null && data.length > 0) {
            try {
                mNV21Queue.put(data);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
