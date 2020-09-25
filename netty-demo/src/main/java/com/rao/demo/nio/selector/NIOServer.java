package com.rao.demo.nio.selector;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class NIOServer {

    public static void main(String[] args) throws Exception{
        //1.创建一个Selector
        Selector selector = Selector.open();

        //2.创建一个Channel
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        //设置非阻塞的
        serverSocketChannel.configureBlocking(false);
        InetSocketAddress inetSocketAddress = new InetSocketAddress(8087);
        serverSocketChannel.bind(inetSocketAddress);

        //3.注册channel到selector中,ServerSocketChannel关心的是客户端accept连接,所以需要注册OP_ACCEPT事件,事件需要不断注册不断删除
        SelectionKey key = serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        System.out.println("selectionKey="+key.hashCode());
        //4.循环监听事件
        while (true){
            //5.判断是否有事件发生
            // 注意如果这边等待事件过长,客户端那边可能事件已发生了,结果这边还没监听到,客户端已关闭,所以需要客户端进行等待
            if(selector.select(1000)==0) {
                System.out.println("没有事件发生");
            }else{//大于0表示有事件发生

                //6.获取发生事件的key
                Set<SelectionKey> selectionKeys = selector.selectedKeys();
                Iterator<SelectionKey> iterator = selectionKeys.iterator();
                while (iterator.hasNext()){//当异常关闭客户端时,这里会发生问题,客户端关闭也会触发OP_READ事件,所以需要在OP_READ中判断并关闭客户端channel
                    try {
                        SelectionKey selectionKey = iterator.next();
                        System.out.println("selectionKey=" + selectionKey.hashCode());
                        if (selectionKey.isAcceptable()) {//表示有Accept事件发生
                            //7.accept接收客户端的连接,accept虽然是阻塞的,但因为客户端已经发生accept事件，所以这里可以立马连接
                            SocketChannel socketChannel = serverSocketChannel.accept();

                            System.out.println("客户端:" + socketChannel.getRemoteAddress() + "连接上线");
                            socketChannel.configureBlocking(false);
                            //8.再将SocketChannel注册到Selector中,让Selector监听SocketChannel的相关事件
                            socketChannel.register(selector, SelectionKey.OP_READ);
                        }
                        if (selectionKey.isReadable()) {//有客户端发送数据事件发生(注意:如果客户端关闭,也会发生OP_READ)
                            //通过select获取对应事件的channel
                            SocketChannel channel = (SocketChannel) selectionKey.channel();

                            //读取channel缓冲区中的数据
                            ByteBuffer buffer = ByteBuffer.allocate(1024);

                            int length = -1;
                            while ((length = channel.read(buffer)) >0){
                                buffer.flip();
                                byte[] bt = new byte[buffer.remaining()];
                                buffer.get(bt);
                                System.out.println(new String(bt).trim());
                            }

                            if (length < 0) {//通过判断读取数据小于0来判断客户端已经关闭了
                                channel.close();
                            }

                        }
//                        if (selectionKey.isValid()) {
//                            System.out.println("无效");
//                        }
//                        if (selectionKey.isConnectable()) {
//                            System.out.println("可连接");
//                        }
                    }finally {
                        //9. 最后一定要移除事件,防止事件重复操作
                        iterator.remove();
                    }

                }

            }

        }

    }

}
