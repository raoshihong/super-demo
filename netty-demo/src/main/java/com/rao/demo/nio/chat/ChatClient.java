package com.rao.demo.nio.chat;

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
 * @date 2020-06-13 11:07
 */
public class ChatClient extends Thread{
    private Selector selector;
    private SocketChannel socketChannel;

    public ChatClient(int port){
        try {
            selector = Selector.open();

            socketChannel = SocketChannel.open(new InetSocketAddress(port));
            socketChannel.configureBlocking(false);

            // 客户端监听服务端写数据到该客户端,所以是监听该channel的OP_READ事件
            socketChannel.register(selector, SelectionKey.OP_READ);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        handlerRead();
        handlerWrite();
    }

    private void handlerRead(){
        new Thread(()-> {
            while (true){
                try{
                    int select = selector.select(1000);
                    if (select>0) {
                        Set<SelectionKey> selectionKeys = selector.selectedKeys();
                        Iterator<SelectionKey> iterator = selectionKeys.iterator();

                        while (iterator.hasNext()){
                            SelectionKey selectionKey = iterator.next();
                            if (selectionKey.isReadable()) {
                                ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
                                int length = -1;
                                while ((length = socketChannel.read(byteBuffer))>0) {
                                    System.out.println(new String(byteBuffer.array(),0,length));
                                }
                            }

                            // 这里一定要移除,否则无法监听下次事件
                            iterator.remove();
                        }
                    }

                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void handlerWrite(){
        try{
            Scanner scanner = new Scanner(System.in);
            while (scanner.hasNext()){
                String content = scanner.nextLine();

                //将内容发送到服务器
                socketChannel.write(ByteBuffer.wrap(content.getBytes()));
            }
        }catch (Exception e){

        }
    }

    public static void main(String[] args) throws Exception{
        ChatClient chatClient = new ChatClient(6666);
        chatClient.start();
    }
}
