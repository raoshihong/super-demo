package com.rao.demo.nio.reactor.single.reactor.single.thread;

import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.util.Iterator;
import java.util.Set;

/**
 * 单个Reactor单个线程demo
 * @author raoshihong
 * @date 2020-08-12 17:54
 */
public class SingleReactorSingleThread {

    public static void main(String[] args) throws Exception{
        // 单个Reactor单个线程,所有的操作都是在一个线程中进行
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.configureBlocking(false);
        serverSocketChannel.bind(new InetSocketAddress(9898));

        // 创建一个Selector
        Selector selector = Selector.open();
        //注册Acceptor监听事件
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

        // 监听selector事件发生
        while (true){
            int select = selector.select(1000);
            if (select > 0) {
                Set<SelectionKey> selectionKeys = selector.selectedKeys();
                Iterator<SelectionKey> iterator = selectionKeys.iterator();

                while (iterator.hasNext()){
                    SelectionKey selectionKey = iterator.next();
                    if (selectionKey.isAcceptable()) {
                        //交给Acceptor处理
                        new Acceptor().acceptor(selectionKey);
                    } else{
                        // 交给handler处理
                        new Handler().handler(selectionKey);
                    }

                    // 没执行完,则需要移除当前发生的事件对象
                    iterator.remove();
                }
            }
        }
    }

}
