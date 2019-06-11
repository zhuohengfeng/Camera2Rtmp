package com.ryan.camera2rtmp.video.callback;

/**
 * 转换成YUV420P数据后的回调
 */
public interface CameraYUVDataListener {
    void onYUVDataReceiver(byte[] data, int width, int height);
}
