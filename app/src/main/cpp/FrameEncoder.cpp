//
// Created by hengfeng zhuo on 2019-06-11.
//

#include "FrameEncoder.h"

FrameEncoder::FrameEncoder() {

}

FrameEncoder::~FrameEncoder() {

}

void FrameEncoder::open() {

}

void FrameEncoder::setInWidth(int width) {
    this->in_width = width;
}

void FrameEncoder::setInHeight(int height) {
    this->in_height = height;
}

void FrameEncoder::setOutWidth(int width) {
    this->out_widht = width;
}

void FrameEncoder::setOutHeight(int height) {
    this->out_height = height;
}

void FrameEncoder::setBitrate(int bitrate) {
    this->bitrate = bitrate;
}


