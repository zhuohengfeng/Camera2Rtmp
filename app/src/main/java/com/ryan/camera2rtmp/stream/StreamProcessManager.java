package com.ryan.camera2rtmp.stream;

public class StreamProcessManager {

    static {
        System.loadLibrary("stream");
    }

//    public static native int init(int width, int height, int outWidth, int outHeight);
//
//    public static native int release();
//
    /**
     * NV21转化为YUV420P数据
     * @param src         原始数据
     * @param width       原始数据宽度
     * @param height      原始数据高度
     * @param dst         生成数据
     * @param dst_width   生成数据宽度
     * @param dst_height  生成数据高度
     * @return
     */
    public static native int compressYUV(byte[] src, int width, int height,
                                         byte[] dst, int dst_width, int dst_height);


    /**
     * 编码视频数据准备工作
     * @param in_width
     * @param in_height
     * @param out_width
     * @param out_height
     * @return
     */
    public static native int encoderVideoinit(int in_width, int in_height, int out_width, int out_height);


    /**
     * 编码视频数据接口
     * @param srcFrame      原始数据(YUV420P数据)
     * @param frameSize     帧大小
     * @param fps           fps
     * @param dstFrame      编码后的数据存储
     * @param outFramewSize 编码后的数据大小
     * @return
     */
    public static native int encoderVideoEncode(byte[] srcFrame, int frameSize, int fps, byte[] dstFrame, int[] outFramewSize);

}
