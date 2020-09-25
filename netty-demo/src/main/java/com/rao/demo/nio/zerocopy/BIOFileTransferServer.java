package com.rao.demo.nio.zerocopy;

import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class BIOFileTransferServer {
    public static void main(String[] args) throws Exception{
        ServerSocket serverSocket = new ServerSocket();
        //绑定端口
        serverSocket.bind(new InetSocketAddress(6666));
        while (true) {
            //等待客户端连接
            final Socket socket = serverSocket.accept();
            System.out.println("接收到客户端连接");
            //连接后读取客户端数据
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        InputStream is = socket.getInputStream();
                        while (true){
                            int length = is.read();
                            System.out.println("读取到客户端数据");
                            if (length < 0) {
                                break;
                            }
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }).start();

        }

    }
}
