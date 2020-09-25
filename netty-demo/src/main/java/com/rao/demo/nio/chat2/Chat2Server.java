package com.rao.demo.nio.chat2;

import java.io.IOException;
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
 * @date 2020-07-13 22:33
 */
public class Chat2Server {

    public static List<SocketChannel> clients = new ArrayList<>();

    public static void main(String[] args) throws IOException {
        // 打开一个服务端channel
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        //设置为非阻塞的
        serverSocketChannel.configureBlocking(false);
        //设置端口号
        serverSocketChannel.bind(new InetSocketAddress(6666));

        // 创建一个Selector轮询选择器
        Selector selector = Selector.open();

        //将服务端channel注册到selector中，并指明绑定的事件,服务端关系客户端accept
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

        //轮询select
        while (true){
            //不断从selector中轮询获取
            int select = selector.select(1000);
            if (select>0) {
                //大于0表示有事件发生,则从select中取出对应发生的事件
                Set<SelectionKey> selectionKeys = selector.selectedKeys();
                Iterator<SelectionKey> iterator = selectionKeys.iterator();
                // 轮询每个事件
                while (iterator.hasNext()){
                    //  获取事件key对象
                    SelectionKey selectionKey = iterator.next();
                    //判断每个事件的类型
                    if (selectionKey.isAcceptable()) {
                        //表示客户端连接,此时的key绑定的是ServerSocketChannel
                        //从key中获取对应绑定的channel
                        ServerSocketChannel channel = (ServerSocketChannel)selectionKey.channel();
                        channel.configureBlocking(false);
                        //发生客户端连接事件，则服务端立马调用accept方法,接受客户端连接,并获取客户端channel
                        SocketChannel clientChannel = channel.accept();
                        clientChannel.configureBlocking(false);
                        // 将clientChannel注册到Selector中，并指明绑定的事件,注册channel可读事件
                        clientChannel.register(selector,SelectionKey.OP_READ);

                        //保存客户端
                        clients.add(clientChannel);
                    } else if (selectionKey.isReadable()) {
                        // 表示channel发生可读数据事件,获取目标客户端
                        SocketChannel channel = (SocketChannel)selectionKey.channel();
                        channel.configureBlocking(false);
                        // 从目标客户端channel中读取数据
                        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
                        long length = -1;
                        while ((length = channel.read(byteBuffer)) > 0) {
                            //表示读取到数据
                            //则将数据写到其他客户端channel中
                            System.out.println(new String(byteBuffer.array()).trim());
                            clients.stream().forEach(socketChannel -> {
                                if (socketChannel != channel) {
                                    try {
                                        byteBuffer.flip();
                                        socketChannel.write(byteBuffer);
                                    }catch (Exception e){
                                        e.printStackTrace();
                                    }
                                }
                            });
                        }

                        if (length < 0) {
                            //这种情况表示客户端断开
                            channel.close();
                            clients.remove(channel);
                        }
                    } else if (selectionKey.isWritable()) {
                        // 表示可以往channel中写数据

                    }

                    // 没执行完,则需要移除当前发生的事件对象
                    iterator.remove();
                }
            }
        }

    }
}
