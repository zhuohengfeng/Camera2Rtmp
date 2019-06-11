package com.ryan.camera2rtmp.video;

public class VideoData {
    public byte[] videoData;
    public int width;
    public int height;

    public VideoData(byte[] videoData, int width, int height) {
        this.videoData = videoData;
        this.width = width;
        this.height = height;
    }
}
