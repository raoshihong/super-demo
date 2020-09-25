package com.rao.demo.nio.selector;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class NIOClient {
    public static void main(String[] args) throws Exception{
        SocketChannel socketChannel = SocketChannel.open();
        //设置非阻塞
        socketChannel.configureBlocking(false);
        InetSocketAddress inetSocketAddress = new InetSocketAddress(8087);
        //连接服务端,服务端Selector触发OP_ACCEPT事件
        if(!socketChannel.connect(inetSocketAddress)){
            while (!socketChannel.finishConnect()){
                System.out.println("连接需要时间,请耐心等待");
            }
        }

        ByteBuffer byteBuffer = ByteBuffer.wrap("abcd".getBytes());
        //向服务端写数据,服务端Selector触发OP_READ事件,表示客户端有数据可读
        socketChannel.write(byteBuffer);

        Thread.sleep(10000);

        // 这里关闭会触发服务端的SelectionKey.OP_READ
        socketChannel.close();
    }
}
