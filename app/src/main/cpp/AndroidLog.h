//
// Created by hengfeng zhuo on 2019/3/25.
//

#ifndef ANDROID_LOG_H
#define ANDROID_LOG_H

#include "android/log.h"

#define LOGD(FORMAT,...) __android_log_print(ANDROID_LOG_DEBUG,"zhf_camera_native",FORMAT,##__VA_ARGS__);
#define LOGE(FORMAT,...) __android_log_print(ANDROID_LOG_ERROR,"zhf_camera_native",FORMAT,##__VA_ARGS__);


#endif //ANDROID_LOG_H