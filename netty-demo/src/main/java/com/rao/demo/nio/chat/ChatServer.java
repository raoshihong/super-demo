package com.rao.demo.nio.chat;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * @author raoshihong
 * @date 2020-06-13 11:07
 */
public class ChatServer extends Thread{

    private List<SocketChannel> clients;
    private ServerSocketChannel server;
    private Selector selector;
    private int port;

    public ChatServer(int port){
        try {
            this.port = port;
            clients = new ArrayList<>();
            // 服务端创建一个Selector选择器
            selector = Selector.open();
            // 服务端创建ServerSocketChannel
            server = ServerSocketChannel.open();
            server.configureBlocking(false);
            server.bind(new InetSocketAddress(port));

            // 将ServerSocketChannel注册到Selector,并绑定关心的事件
            // 服务端关心的是客户端的连接,所以是accept
            server.register(selector, SelectionKey.OP_ACCEPT);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            // 死循环等待相应的事件发生
            while (true){
                // 通过selector不断监听是否有事件发生
                int select = selector.select(1000);
                // 大于0 表示有事件发生
                if (select > 0) {
                    Set<SelectionKey> selectionKeys = selector.selectedKeys();
                    Iterator<SelectionKey> iterator = selectionKeys.iterator();
                    //遍历循环每个发生的事件,从而针对不同的事件做不同的处理
                    while (iterator.hasNext()) {
                        SelectionKey selectionKey = iterator.next();

                        //客户端连接事件
                        if (selectionKey.isAcceptable()) {
                            handleAccept();
                        }

                        //表示客户端可读事件
                        if (selectionKey.isReadable()) {
                            SocketChannel socketChannel = (SocketChannel)selectionKey.channel();
                            handleRead(socketChannel);
                        }

                        // 移除事件,避免重复
                        iterator.remove();
                    }
                }

            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void handleAccept() throws Exception{
        //则接受客户端的连接
        SocketChannel socketChannel = server.accept();
        System.out.println("客户端连接:"+socketChannel.getRemoteAddress());
        socketChannel.configureBlocking(false);
        // 注册客户端监听OP_READ
        socketChannel.register(selector,SelectionKey.OP_READ);
        //保存客户端连接
        clients.add(socketChannel);
    }

    private void handleRead(SocketChannel socketChannel) throws Exception{
        //读取客户端的数据
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        int length = -1;
        while((length = socketChannel.read(buffer))>0){
            //将消息发送给其他客户端
            InetSocketAddress remoteAddress =
                (InetSocketAddress)socketChannel.getRemoteAddress();
            String msg = "客户端"+remoteAddress.getPort()+"说:"+new String(buffer.array(),0,buffer.remaining()).trim();

            System.out.println(msg);
            clients.stream().forEach(socketChannel1 -> {
                if (!socketChannel.equals(socketChannel1)) {
                    try{
                        ByteBuffer buffer1 = ByteBuffer.wrap(msg.getBytes());
                        socketChannel1.write(buffer1);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            });
        }

        if (length<0) {
            // 处理客户端关闭连接问题
            socketChannel.close();
            clients.remove(socketChannel);
        }
    }

    public static void main(String[] args){
        ChatServer chatServer = new ChatServer(6666);
        chatServer.start();
    }
}
