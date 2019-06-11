package com.ryan.camera2rtmp.stream;

import com.ryan.camera2rtmp.video.VideoData;
import com.ryan.camera2rtmp.video.VideoGatherManager;
import com.ryan.camera2rtmp.video.callback.CameraYUVDataListener;

public class MediaPublisher {

    private VideoGatherManager videoGatherManager;
    private MediaEncoder mediaEncoder;

    public MediaPublisher() {
        mediaEncoder = new MediaEncoder();
    }

    public void initVideoGather(VideoGatherManager manager) {
        videoGatherManager = manager;
        videoGatherManager.setYuvDataListener(new CameraYUVDataListener() {
            @Override
            public void onYUVDataReceiver(byte[] data, int width, int height) {
                // 得到了YUV420P数据，然后要进行编码，生成H264
                VideoData videoData = new VideoData(data, width, height);
                mediaEncoder.putVideoData(videoData);
            }
        });
    }


    public void startMediaEncoder() {
        mediaEncoder.start();
    }

    public void stopMediaEncoder() {
        mediaEncoder.stop();
    }




}
