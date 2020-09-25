package com.rao.demo.nio.reactor.master.slave;

import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

/**
 * @author raoshihong
 * @date 2020-08-12 18:01
 */
public class Acceptor {

    public SocketChannel acceptor(SelectionKey selectionKey) throws Exception{
        ServerSocketChannel serverSocketChannel = (ServerSocketChannel)selectionKey.channel();
        // 通过acceptor与客户端建立连接
        SocketChannel socketChannel = serverSocketChannel.accept();

        return socketChannel;
    }

}
