//
// Created by hengfeng zhuo on 2019-06-11.
//

#ifndef CAMERARTMP_FRAMEENCODER_H
#define CAMERARTMP_FRAMEENCODER_H


class FrameEncoder {

public:
    FrameEncoder();
    virtual ~FrameEncoder();

    void setInWidth(int width);
    void setInHeight(int height);
    void setOutWidth(int width);
    void setOutHeight(int height);

    void setBitrate(int bitrate);

    void open();

private:
    int bitrate;
    int in_width;
    int in_height;
    int out_widht;
    int out_height;

};


#endif //CAMERARTMP_FRAMEENCODER_H
