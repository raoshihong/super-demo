package com.rao.demo.nio.reactor.master.slave;

import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

/**
 * @author raoshihong
 * @date 2020-08-12 18:27
 */
public class SubReactor {

    private Selector selector;

    public SubReactor(){
        try {
            selector = Selector.open();
        }catch (Exception e){

        }
    }

    /**
     * Reactor子线程注册channel
     * @param socketChannel
     * @throws Exception
     */
    public void register(SocketChannel socketChannel) throws Exception{
        socketChannel.configureBlocking(false);
        socketChannel.register(selector, SelectionKey.OP_READ);

        //Reactor子线程处理read和send操作
        // 监听selector事件发生
        while (true){
            int select = selector.select(1000);
            if (select > 0) {
                Set<SelectionKey> selectionKeys = selector.selectedKeys();
                Iterator<SelectionKey> iterator = selectionKeys.iterator();

                while (iterator.hasNext()){
                    SelectionKey selectionKey = iterator.next();
                    if (selectionKey.isReadable()) {

                        // 交给handler处理
                        new Handler().handler(selectionKey);

                        // 没执行完,则需要移除当前发生的事件对象
                        iterator.remove();
                    }
                }
            }
        }
    }


}
