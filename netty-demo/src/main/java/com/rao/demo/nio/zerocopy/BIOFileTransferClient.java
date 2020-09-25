package com.rao.demo.nio.zerocopy;

import java.io.File;
import java.io.FileInputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

public class BIOFileTransferClient {

    public static void main(String[] args) throws Exception{
        long startTime = System.currentTimeMillis();
        File file = new File("/Users/raoshihong/Downloads/apache-impala-3.0.1.tar.gz");
        FileInputStream fis = new FileInputStream(file);
        byte[] bytes = new byte[(int) file.length()];
        //1.先从磁盘读取,这块涉及到将系统硬盘中的文件通过DMA copy拷贝到内核buffer, 此时切换到kernel context
        //2.再通过CPU copy 将内核buffer中到数据拷贝到用户空间的buffer中  此时切换到user context
        //3.最后通过应用程序read读取 user buffer中的数据
        fis.read(bytes);

        //再通过socket进行传输
        Socket socket = new Socket();
        socket.connect(new InetSocketAddress(6666));

        // 4.先将应用程序中的数据写入到user buffer中
        // 5.再通过cpu copy将user buffer中的数据写入到 kernel buffer   此时切换到kernel context
        // 6.最后通过DMA copy将内核kernel buffer的数据写入到socket buffer中
        socket.getOutputStream().write(bytes);

        socket.close();

        long endTime = System.currentTimeMillis();
        System.out.println("传输时间："+(endTime-startTime));

        //传输时间：20232
    }

}
