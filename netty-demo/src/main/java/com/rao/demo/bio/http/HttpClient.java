package com.rao.demo.bio.http;

import com.sun.org.apache.xpath.internal.operations.String;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * 通过socket模拟http请求
 *
 * @author honey.rao
 * @since 2020/9/24 19:10
 */
public class HttpClient {

    /**
     * GET / HTTP/1.1
     * Host: localhost:8896
     * Connection: keep-alive
     * Cache-Control: max-age=0
     * Upgrade-Insecure-Requests: 1
     * User-Agent: Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/71.0.3578.80 Safari/537.36
     * Accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng
     *
     */
    @Test
    public void run() {
        // 模拟http请求
        Socket socket = new Socket();

        // 连接服务器
        try {
            socket.connect(new InetSocketAddress(8896));
        }catch (Exception e){
            e.printStackTrace();
        }

        if (socket.isConnected()) {
            // 向服务端发送http请求
            try {
                OutputStream os = socket.getOutputStream();

                os.write("GET / HTTP/1.1 \r\n".getBytes());
                os.write("Host: localhost:8896 \r\n".getBytes());
                os.write("Content-Type: text/html \r\n".getBytes());
                os.write("Accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng \r\n".getBytes());
                os.write("\r\n".getBytes());

                os.write("{}".getBytes());

                InputStream is = socket.getInputStream();
                byte[] bytes = new byte[1024];
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                int length = -1;
                while ((length=is.read(bytes))!=-1){
                    baos.write(bytes,0,length);
                }

                System.out.println(baos.toString());

                os.close();
                is.close();
                socket.close();

            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

}
