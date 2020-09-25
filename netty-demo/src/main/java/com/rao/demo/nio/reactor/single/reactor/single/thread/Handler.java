package com.rao.demo.nio.reactor.single.reactor.single.thread;

import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

/**
 * @author raoshihong
 * @date 2020-08-12 18:03
 */
public class Handler {

    public void handler(SelectionKey selectionKey) throws Exception{
        if (selectionKey.isReadable()) {
            read(selectionKey);
        }
    }

    public void read(SelectionKey selectionKey)throws Exception{
        SocketChannel channel = (SocketChannel)selectionKey.channel();
        channel.configureBlocking(false);
        // 从目标客户端channel中读取数据
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        long length = -1;
        while ((length = channel.read(byteBuffer)) > 0) {
            //表示读取到数据
            //则将数据写到其他客户端channel中
            System.out.println(new String(byteBuffer.array()).trim());
        }

        if (length < 0) {
            //这种情况表示客户端断开
            channel.close();
        }

        //进行业务处理
        business();
    }

    public void send(){

    }

    public void business(){

    }

}
