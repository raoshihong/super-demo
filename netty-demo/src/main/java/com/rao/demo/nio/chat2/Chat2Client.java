package com.rao.demo.nio.chat2;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Scanner;
import java.util.Set;

/**
 * @author raoshihong
 * @date 2020-07-13 23:07
 */
public class Chat2Client {
    public static void main(String[] args) throws IOException {
        // 创建一个客户端socket
        SocketChannel socketChannel = SocketChannel.open();
        // 设置非阻塞
        socketChannel.configureBlocking(false);

        // 创建一个Selector
        Selector selector = Selector.open();

        //连接服务器,不断尝试连接服务器
        while (!socketChannel.connect(new InetSocketAddress(6666))){
            // 连接成功,则跳出循环
            if (socketChannel.finishConnect()) {
                break;
            }
        }

        // 开启一个子线程去监听服务器端返回的数据
        new Thread(new Runnable() {
            @Override
            public void run() {
                // 注册socketChannel到Selector上，并绑定可读事件监听
                try {
                    socketChannel.register(selector, SelectionKey.OP_READ);

                    // 不断轮询监听
                    while (true){
                        // 从selector中获取发生事件
                        int select = selector.select(1000);

                        // 表示有事件发生
                        if (select > 0) {

                            Set<SelectionKey> selectionKeys = selector.selectedKeys();
                            Iterator<SelectionKey> iterator = selectionKeys.iterator();
                            // 遍历每个事件
                            while (iterator.hasNext()){
                                SelectionKey selectionKey = iterator.next();

                                // 判断发生的事件
                                if(selectionKey.isReadable()){
                                    // 表示channel中有数据可读
                                    SocketChannel channel = (SocketChannel)selectionKey.channel();
                                    channel.configureBlocking(false);

                                    //从channel中读取数据
                                    ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
                                    int length = -1;

                                    while ((length = channel.read(byteBuffer)) > 0){
                                        System.out.println(new String(byteBuffer.array()).trim());
                                    }

                                }

                                // 需要移除事件
                                iterator.remove();
                            }
                        }
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }

            }
        }).start();

        // 读取控制台数据
        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNext()){
            String msg = scanner.nextLine();

            // 将数据输出到服务器端
            ByteBuffer byteBuffer = ByteBuffer.wrap(msg.getBytes());
            socketChannel.write(byteBuffer);
        }
    }
}
