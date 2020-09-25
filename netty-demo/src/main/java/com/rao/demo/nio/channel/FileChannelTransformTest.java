package com.rao.demo.nio.channel;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;

public class FileChannelTransformTest {
    public static void main(String[] args) throws Exception{
        //实现文件拷贝
        FileInputStream fis = new FileInputStream(new File("/Users/raoshihong/Desktop/WechatIMG811.jpeg"));
        FileChannel fisChannel = fis.getChannel();

        FileOutputStream fos = new FileOutputStream(new File("/Users/raoshihong/Desktop/WechatIMG8112.jpeg"));
        FileChannel fosChannel = fos.getChannel();

        fisChannel.transferTo(0,fis.available(),fosChannel);

        fis.close();
        fos.close();
    }
}
