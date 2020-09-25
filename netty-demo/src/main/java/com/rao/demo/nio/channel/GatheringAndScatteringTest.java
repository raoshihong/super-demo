package com.rao.demo.nio.channel;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Arrays;

public class GatheringAndScatteringTest {
    public static void main(String[] args) throws Exception{

        ByteBuffer[] byteBuffers = new ByteBuffer[2];
        byteBuffers[0] = ByteBuffer.allocate(3);
        byteBuffers[1] = ByteBuffer.allocate(7);

        //abcde
        FileInputStream fis = new FileInputStream(new File("/Users/raoshihong/work/script/test2.txt"));

        FileChannel channel = fis.getChannel();

        //读入方式以字节的方式读入,a占用一个字节,依次添加到byteBuffer中
        channel.read(byteBuffers);

        Arrays.asList(byteBuffers).stream().forEach(byteBuffer->{
            byteBuffer.flip();
        });

        FileOutputStream fos = new FileOutputStream(new File("/Users/raoshihong/work/script/test3.txt"));

        FileChannel writeChannel = fos.getChannel();
        //在这里会依次从数组中的byteBuffer中读取数据,并写入到Channel中的缓冲区
        writeChannel.write(byteBuffers);

        fis.close();
        fos.close();
    }
}
