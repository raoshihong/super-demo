package com.rao.demo.nio.channel;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class FileChannelWriteFileTest {
    public static void main(String[] args) throws Exception{
        //指定本地文件
        FileOutputStream fos = new FileOutputStream(new File("/Users/raoshihong/work/script/test.txt"));
        FileChannel channel = fos.getChannel();
        //把缓冲区的数据写入到通道,相当于FileChannel与文件打开了一条通道,然后将数据写入到通道中,通过通道传输到文件中
        ByteBuffer buffer = ByteBuffer.allocate(9);
        buffer.put("中国人".getBytes("UTF-8"));

        //flip()方法,一定不能忘记
        buffer.flip();

        //下面表示读取buffer中的数据,并写入到channel中的buffer中
        channel.write(buffer);
        channel.close();
        fos.close();
    }
}
