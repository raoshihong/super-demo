package com.rao.demo.nio.channel;

import java.io.File;
import java.io.FileInputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class FileChannelReadFileTest {
    public static void main(String[] args) throws Exception {
        FileInputStream fis = new FileInputStream(new File("/Users/raoshihong/work/script/jps_tools.sh"));

        ByteBuffer targetBuffer = ByteBuffer.allocate(fis.available());

        FileChannel channel = fis.getChannel();
        //将channel与文件建立通道,并读取channel中缓冲区中的数据,写入到我们的buffer中
        channel.read(targetBuffer);

        //一定要调用flip,因为上面是对我们的targetBuffer进行写操作,
        // 如果下面想对targetBuffer进行读取数据，则一定要调用flip重置position和limit
        targetBuffer.flip();

        //从targetBuffer中读取数据
        String result = new String(targetBuffer.array());
        System.out.println(result);

        fis.close();
    }
}
