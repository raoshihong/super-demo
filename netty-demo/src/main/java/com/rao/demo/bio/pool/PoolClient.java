package com.rao.demo.bio.pool;

import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * @author honey.rao
 * @since 2020/9/24 17:04
 */
public class PoolClient {

    @Test
    public void run(){
        Socket socket = new Socket();

        // 客户端连接服务端
        try {
            // milliseconds
            socket.connect(new InetSocketAddress(8896), 1000*60);
        }catch (Exception e){
            e.printStackTrace();
        }

        if (socket.isConnected()) {
            // 等待服务端的回复,读取服务端的信息
            try {
                InputStream is = socket.getInputStream();

                byte[] bytes = new byte[10];
                // 采用字节缓存输出流缓存读取到的字节数组数据,避免半包问题
                ByteArrayOutputStream baos = new ByteArrayOutputStream();

                // read方法,如果建立的通道不关闭,则会一直等待,所以服务端如果不关闭,则客户端会一直等待服务端输出数据
                while (is.read(bytes)!=-1){
                    baos.write(bytes);
                }

                System.out.println(baos.toString());

                baos.close();
                is.close();

            }catch (Exception e){
                e.printStackTrace();
            }

        }else {
            System.out.println("连接失败");
        }
    }

}
