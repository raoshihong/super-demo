package com.rao.demo.nio.channel;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class FileChannelReadWriteTest {
    public static void main(String[] args) throws Exception{
        FileInputStream fis = new FileInputStream(new File("/Users/raoshihong/work/script/jps_tools.sh"));
        FileChannel readChannel = fis.getChannel();

        FileOutputStream fos = new FileOutputStream(new File("/Users/raoshihong/work/script/test2.txt"));
        FileChannel writeChannel = fos.getChannel();

        ByteBuffer buffer = ByteBuffer.allocate(10);//这里故意定义容量大小为10

        while(true) {

            //将channel中缓冲区中的数据读取出来,并写入到自定义的buffer中
            int i = readChannel.read(buffer);

            if (i==-1) {//读完
                break;
            }

            //对buffer进行flip
            buffer.flip();

            //读取buffer中的数据,并写入到目标通道的缓冲区中
            writeChannel.write(buffer);

            //重置buffer,将position和limit恢复初始化值,这里写完后，必须要重置buffer,不然position和limit的值不对,会导致读取的数据不对的问题
            buffer.clear();

        }

        fos.close();
        fis.close();

    }
}
