package com.rao.demo.bio.chat;

import org.junit.Test;

import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author honey.rao
 * @since 2020/9/25 16:25
 */
public class ChatServer {

    ExecutorService executorService = Executors.newCachedThreadPool();

    List<Socket> clients = new ArrayList<>();

    @Test
    public void run(){

        try {
            ServerSocket serverSocket = new ServerSocket();

            // 配置socket等待客户端连接access超时时间参数,milliseconds,如果超过这个时间没有客户端连接上,则抛出异常
            //            serverSocket.setSoTimeout(600*1000);

            // 指定绑定端口,并设置backlog大小
            serverSocket.bind(new InetSocketAddress(8896),1000);

            // 服务端不断轮询等待客户端连接
            while (true) {

                // 阻塞等待客户端连接上来,当有客户端连接上,则获取客户端的socket对象
                Socket clientSocket = serverSocket.accept();

                clients.add(clientSocket);

                // 每连接上一个客户端则将其交给线程池处理
                executorService.submit(()->{
                    workHandler(clientSocket);
                });

            }

        }catch (Exception e){
            e.printStackTrace();
        }

    }

    // 交给handler处理read,write,业务逻辑
    public void workHandler(Socket clientSocket){
        try {
            // 有客户端连接上,通过客户端socket向客户端发送数据
            OutputStream os = clientSocket.getOutputStream();

            String clientAddress = clientSocket.getInetAddress().getHostAddress();

            String msg = "hello " + clientAddress;

            os.write(msg.getBytes());
            os.flush();
            // 没有数据要输出给客户端,则需要关闭,不要一直占用着资源
            os.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}
