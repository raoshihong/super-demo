package com.rao.demo.nio.zerocopy;

import java.io.File;
import java.io.FileInputStream;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;

/**
 * @author raoshihong
 * @date 2020-06-13 10:42
 */
public class NIOFileTransferClient {
    public static void main(String[] args) throws Exception{
        long startTime = System.currentTimeMillis();
        File file = new File("/Users/raoshihong/Downloads/apache-impala-3.0.1.tar.gz");
        FileInputStream fis = new FileInputStream(file);

        SocketChannel socketChannel = SocketChannel.open();

        socketChannel.configureBlocking(false);
        if(!socketChannel.connect(new InetSocketAddress(6666))){
            while (!socketChannel.finishConnect()){}
        }

        fis.getChannel().transferTo(0,fis.available(),socketChannel);

        fis.close();

        long endTime = System.currentTimeMillis();
        System.out.println("传输时间："+(endTime-startTime));
    }
}
