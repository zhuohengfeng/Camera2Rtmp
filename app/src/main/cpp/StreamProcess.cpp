#include <jni.h>
#include <string>

#include "AndroidLog.h"

#include "libyuv.h"
#include "FrameEncoder.h"

FrameEncoder* frameEncoder;

void nv21ToYuv420p(jbyte* src_nv21_data, jint width, jint height, jbyte* des_yuv420p_data){

    // NV21中Y通道的数据大小
    jint src_y_size = width * height;
    // NV21中U,V的数据大小，都是1/4，二者是交叉存储的
    jint src_u_v_size = (width >> 1) * (height >> 1);

    jbyte* src_y_data = src_nv21_data;
    jbyte* src_u_v_data = src_nv21_data + src_y_size;

    // YUV420p的排列是先Y，再U， 再V
    jbyte* dec_y_data = des_yuv420p_data;
    jbyte* dec_u_data = des_yuv420p_data + src_y_size;
    jbyte* dec_v_data = des_yuv420p_data + src_y_size + src_u_v_size;

    libyuv::NV21ToI420((const uint8 *)src_y_data, width,
                       (const uint8 *)src_u_v_data, width,
                       (uint8 *)dec_y_data, width,
                       (uint8 *)dec_u_data, width>>1,
                       (uint8 *)dec_v_data, width>>1,
                       width, height);
}

//进行缩放操作，此时是把1080 * 1920的YUV420P的数据 ==> 480 * 640的YUV420P的数据
void scaleYuv420p(jbyte *src_i420_data, jint width, jint height, jbyte *dst_i420_data, jint dst_width,
                  jint dst_height, jint mode) {

    //Y数据大小width*height，U数据大小为1/4的width*height，V大小和U一样，一共是3/2的width*height大小
    jint src_i420_y_size = width * height;
    jint src_i420_u_size = (width >> 1) * (height >> 1);

    //由于是标准的YUV420P的数据，我们可以把三个通道全部分离出来
    jbyte *src_i420_y_data = src_i420_data;
    jbyte *src_i420_u_data = src_i420_data + src_i420_y_size;
    jbyte *src_i420_v_data = src_i420_data + src_i420_y_size + src_i420_u_size;

    //由于是标准的YUV420P的数据，我们可以把三个通道全部分离出来
    jint dst_i420_y_size = dst_width * dst_height;
    jint dst_i420_u_size = (dst_width >> 1) * (dst_height >> 1);
    jbyte *dst_i420_y_data = dst_i420_data;
    jbyte *dst_i420_u_data = dst_i420_data + dst_i420_y_size;
    jbyte *dst_i420_v_data = dst_i420_data + dst_i420_y_size + dst_i420_u_size;

    //调用libyuv库，进行缩放操作
    libyuv::I420Scale((const uint8 *) src_i420_y_data, width,
                      (const uint8 *) src_i420_u_data, width >> 1,
                      (const uint8 *) src_i420_v_data, width >> 1,
                      width, height,
                      (uint8 *) dst_i420_y_data, dst_width,
                      (uint8 *) dst_i420_u_data, dst_width >> 1,
                      (uint8 *) dst_i420_v_data, dst_width >> 1,
                      dst_width, dst_height,
                      (libyuv::FilterMode) mode);
}

void rotateI420(jbyte *src_i420_data, jint width, jint height, jbyte *dst_i420_data, jint degree) {
    jint src_i420_y_size = width * height;
    jint src_i420_u_size = (width >> 1) * (height >> 1);

    jbyte *src_i420_y_data = src_i420_data;
    jbyte *src_i420_u_data = src_i420_data + src_i420_y_size;
    jbyte *src_i420_v_data = src_i420_data + src_i420_y_size + src_i420_u_size;

    jbyte *dst_i420_y_data = dst_i420_data;
    jbyte *dst_i420_u_data = dst_i420_data + src_i420_y_size;
    jbyte *dst_i420_v_data = dst_i420_data + src_i420_y_size + src_i420_u_size;

    //要注意这里的width和height在旋转之后是相反的
    if (degree == libyuv::kRotate90 || degree == libyuv::kRotate270) {
        libyuv::I420Rotate((const uint8 *) src_i420_y_data, width,
                           (const uint8 *) src_i420_u_data, width >> 1,
                           (const uint8 *) src_i420_v_data, width >> 1,
                           (uint8 *) dst_i420_y_data, height,
                           (uint8 *) dst_i420_u_data, height >> 1,
                           (uint8 *) dst_i420_v_data, height >> 1,
                           width, height,
                           (libyuv::RotationMode) degree);
    }
}

void mirrorI420(jbyte *src_i420_data, jint width, jint height, jbyte *dst_i420_data) {
    jint src_i420_y_size = width * height;
    jint src_i420_u_size = (width >> 1) * (height >> 1);

    jbyte *src_i420_y_data = src_i420_data;
    jbyte *src_i420_u_data = src_i420_data + src_i420_y_size;
    jbyte *src_i420_v_data = src_i420_data + src_i420_y_size + src_i420_u_size;

    jbyte *dst_i420_y_data = dst_i420_data;
    jbyte *dst_i420_u_data = dst_i420_data + src_i420_y_size;
    jbyte *dst_i420_v_data = dst_i420_data + src_i420_y_size + src_i420_u_size;

    libyuv::I420Mirror((const uint8 *) src_i420_y_data, width,
                       (const uint8 *) src_i420_u_data, width >> 1,
                       (const uint8 *) src_i420_v_data, width >> 1,
                       (uint8 *) dst_i420_y_data, width,
                       (uint8 *) dst_i420_u_data, width >> 1,
                       (uint8 *) dst_i420_v_data, width >> 1,
                       width, height);
}



/** -------------------------------------- */
/**
 * 对原始的NV21数据进行压缩，并转化成YUV420P的数据格式
 */
extern "C"
JNIEXPORT jint JNICALL
Java_com_ryan_camera2rtmp_stream_StreamProcessManager_compressYUV(JNIEnv *env, jclass type,
                                                                  jbyteArray src_, jint width,
                                                                  jint height, jbyteArray dst_,
                                                                  jint dst_width, jint dst_height) {
    jbyte *src = env->GetByteArrayElements(src_, NULL);
    jbyte *dst = env->GetByteArrayElements(dst_, NULL);

    jbyte *temp_i420_data_scale = (jbyte *) malloc(sizeof(jbyte) * width * height * 3 / 2);
    jbyte *temp_i420_data_rotate = (jbyte *) malloc(sizeof(jbyte) * width * height * 3 / 2);

    //LOGD("compressYUV width=%d, height=%d, dst_width=%d, dst_height=%d", width, height, dst_width, dst_height);
    // 调用接口，转化成相同大小的YUV420P格式数据
    nv21ToYuv420p(src, width, height, temp_i420_data_scale);

    // 进行缩放操作，对YUV420P数据进行压缩
    scaleYuv420p(temp_i420_data_scale, width, height, temp_i420_data_rotate, dst_width, dst_height, 0);

    rotateI420(temp_i420_data_rotate, dst_width, dst_height, dst, 90); // 这里要对后置摄像头进行一下翻转

    free(temp_i420_data_scale);
    free(temp_i420_data_rotate);

    env->ReleaseByteArrayElements(src_, src, 0);
    env->ReleaseByteArrayElements(dst_, dst, 0);

    return 0;
}



extern "C"
JNIEXPORT jint JNICALL
Java_com_ryan_camera2rtmp_stream_StreamProcessManager_encoderVideoinit(JNIEnv *env, jclass type,
                                                                jint jwidth, jint jheight,
                                                                jint joutwidth, jint joutheight) {
    frameEncoder = new FrameEncoder();
    frameEncoder->setInWidth(jwidth);
    frameEncoder->setInHeight(jheight);

    frameEncoder->setOutWidth(joutwidth);
    frameEncoder->setOutHeight(joutheight);

    frameEncoder->setBitrate(128);
    frameEncoder->open();
    return 0;
}

extern "C"
JNIEXPORT jint JNICALL
Java_com_ryan_camera2rtmp_stream_StreamProcessManager_encoderVideoEncode (JNIEnv *env, jclass type,
                                                                        jbyteArray jsrcFrame, jint jframeSize,
                                                                        jint counter,
                                                                        jbyteArray jdstFrame, jintArray jdstFrameSize) {

    jbyte *Src_data = env->GetByteArrayElements(jsrcFrame, NULL);
    jbyte *Dst_data = env->GetByteArrayElements(jdstFrame, NULL);
    jint *dstFrameSize = env->GetIntArrayElements(jdstFrameSize, NULL);

    int numNals = frameEncoder->encodeFrame((char*)Src_data, jframeSize, counter, (char*)Dst_data, dstFrameSize);

    env->ReleaseByteArrayElements(jdstFrame, Dst_data, 0);
    env->ReleaseByteArrayElements(jsrcFrame, Src_data, 0);
    env->ReleaseIntArrayElements(jdstFrameSize, dstFrameSize, 0);

    return numNals;
}