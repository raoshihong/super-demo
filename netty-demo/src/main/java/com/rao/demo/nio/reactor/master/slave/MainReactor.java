package com.rao.demo.nio.reactor.master.slave;

import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Reactor主线程
 * @author raoshihong
 * @date 2020-08-12 18:26
 */
public class MainReactor {

    static ExecutorService executorService = new ThreadPoolExecutor(10,20,10, TimeUnit.MINUTES,new LinkedBlockingDeque<>());

    public static void main(String[] args) throws Exception{
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.configureBlocking(false);
        serverSocketChannel.bind(new InetSocketAddress(9989));

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
                        SocketChannel socketChannel = new Acceptor().acceptor(selectionKey);

                        // 将socketChannel注册到SubReactor
                        // 开启子线程

                        executorService.execute(()->{
                            try {
                                SubReactor reactor = new SubReactor();
                                reactor.register(socketChannel);
                            }catch (Exception e){

                            }
                        });
                        // 没执行完,则需要移除当前发生的事件对象
                        iterator.remove();
                    }


                }
            }
        }
    }
}
