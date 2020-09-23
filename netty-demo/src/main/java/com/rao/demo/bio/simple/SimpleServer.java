package com.rao.demo.bio.simple;

import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @author honey.rao
 * @since 2020/9/23 17:54
 */
public class SimpleServer {

    public void run() {
        try {
            ServerSocket serverSocket = new ServerSocket();

            // 配置socket参数
            serverSocket.setSoTimeout(1000);

            // 指定绑定端口,并设置backlog大小
            serverSocket.bind(new InetSocketAddress(8896),100);

            // 等待客户端连接上来,当有客户端连接上,则获取客户端的socket对象
            Socket accept = serverSocket.accept();

        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
