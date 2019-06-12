package com.ryan.camera2rtmp.stream;

import com.ryan.camera2rtmp.Contacts;
import com.ryan.camera2rtmp.utils.FileManager;
import com.ryan.camera2rtmp.video.VideoData;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.concurrent.LinkedBlockingQueue;

public class MediaEncoder {


    private static final String VLC_HOST = "10.88.1.102";
    private static final int VLC_PORT = 5001;

    private static final boolean SAVE_FILE_FOR_TEST = false;


    private Thread videoEncoderThread, audioEncoderThread;
    private boolean videoEncoderLoop, audioEncoderLoop;

    private InetAddress address;
    private DatagramSocket socket;

    //视频流队列
    private LinkedBlockingQueue<VideoData> videoQueue;

    private FileManager videoFileManager;

    public MediaEncoder() {
        if (SAVE_FILE_FOR_TEST) {
            videoFileManager = new FileManager(FileManager.TEST_H264_FILE);
//            audioFileManager = new FileManager(FileManager.TEST_AAC_FILE);
        }
        videoQueue = new LinkedBlockingQueue<>();
//        audioQueue = new LinkedBlockingQueue<>();
        //这里我们初始化音频数据，为什么要初始化音频数据呢？音频数据里面我们做了什么事情？
//        audioEncodeBuffer = StreamProcessManager.encoderAudioInit(Contacts.SAMPLE_RATE,
//                Contacts.CHANNELS, Contacts.BIT_RATE);

        try {
            address = InetAddress.getByName(VLC_HOST);
            socket = new DatagramSocket();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void start() {
//        startAudioEncode();
        startVideoEncode();
    }

    public void stop() {
//        stopAudioEncode();
        stopVideoEncode();
        saveFileForTest();
    }

    //摄像头的YUV420P数据，put到队列中，生产者模型
    public void putVideoData(VideoData videoData) {
        try {
            videoQueue.put(videoData);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private int fps = 0;
    public void startVideoEncode() {
        if (videoEncoderLoop) {
            throw new RuntimeException("必须先停止");
        }

        /**
         * ffplay -f h264 test.h264
         */
        videoEncoderThread = new Thread() {
            @Override
            public void run() {
                //视频消费者模型，不断从队列中取出视频流来进行h264编码
                while (videoEncoderLoop && !Thread.interrupted()) {
                    try {
                        //队列中取视频数据VideoData，这个是我们封装的一个类
                        VideoData videoData = videoQueue.take();
                        fps++;
                        byte[] outbuffer = new byte[videoData.width * videoData.height];
                        int[] buffLength = new int[10];

                        //对YUV420P进行h264编码，返回一个数据大小，里面是编码出来的h264数据
                        int numNals = StreamProcessManager.encoderVideoEncode(videoData.videoData, videoData.videoData.length, fps, outbuffer, buffLength);
                        //Log.e("RiemannLee", "data.length " +  videoData.videoData.length + " h264 encode length " + buffLength[0]);
                        if (numNals > 0) {
                            int[] segment = new int[numNals];
                            System.arraycopy(buffLength, 0, segment, 0, numNals);
                            int totalLength = 0;
                            for (int i = 0; i < segment.length; i++) {
                                totalLength += segment[i];
                            }
                            //Log.i("RiemannLee", "###############totalLength " + totalLength);
                            //编码后的h264数据
                            byte[] encodeData = new byte[totalLength];
                            System.arraycopy(outbuffer, 0, encodeData, 0, encodeData.length);

//                            if (sMediaEncoderCallback != null) {
//                                sMediaEncoderCallback.receiveEncoderVideoData(encodeData, encodeData.length, segment);
//                            }
                            //我们可以把数据在java层保存到文件中，看看我们编码的h264数据是否能播放，h264裸数据可以在VLC播放器中播放
                            if (SAVE_FILE_FOR_TEST) {
                                videoFileManager.saveFileData(encodeData);
                            }


                            try {
                                // 把数据通过UDP发送出去
                                DatagramPacket packet = new DatagramPacket(encodeData, 0, encodeData.length, address, VLC_PORT);
                                socket.send(packet);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        break;
                    }
                }

            }
        };
        videoEncoderLoop = true;
        videoEncoderThread.start();
    }

    public void stopVideoEncode() {
        videoEncoderLoop = false;
        videoEncoderThread.interrupt();
    }

    private void saveFileForTest() {
        if (SAVE_FILE_FOR_TEST) {
            videoFileManager.closeFile();
//            audioFileManager.closeFile();
        }
    }


}
