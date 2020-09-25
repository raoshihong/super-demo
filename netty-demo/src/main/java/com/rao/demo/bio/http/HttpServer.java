package com.rao.demo.bio.http;

import org.junit.Test;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 模拟解析http的服务器
 * @author honey.rao
 * @since 2020/9/24 17:26
 */
public class HttpServer {

    ExecutorService executorService = Executors.newCachedThreadPool();

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
            // 有客户端连接上,读取客户端的数据
            InputStream is = clientSocket.getInputStream();
            int count =0;
            // 一直循环,直到有数据则跳出循环,进行数据的读取
            while (count ==0) {
                count = is.available();
            }

            byte[] b =new byte[count];
            int len = is.read(b);
            String result =new String(b,0, len);

            String uri =parseHttpRequest(result);

            buildHttpResponse(clientSocket,uri);

            //close tcp link
            clientSocket.close();

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     *
     * GET / HTTP/1.1
     * Host: localhost:8896
     * Connection: keep-alive
     * Cache-Control: max-age=0
     * Upgrade-Insecure-Requests: 1
     * User-Agent: Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/71.0.3578.80 Safari/537.36
     * Accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng
     *
     */
    public String parseHttpRequest(String msg){
        System.out.println(msg);
        String uri = "/";
        if (msg.contains("text/html")) {
            // 解析第一行
            String[] split = msg.split("\r\n");
            if (split.length>0) {
                uri = split[0].split(" ")[1];
                String contentType = "text/html";
            }
        }

        return uri;
    }

    /**
     *
     * HTTP/1.1 200
     * Date: Thu, 24 Sep 2020 10:18:16 GMT
     * Content-Type: text/html;charset=UTF-8
     * Transfer-Encoding: chunked
     * Vary: Accept-Encoding
     * Vary: Origin
     * Access-Control-Allow-Credentials: true
     * Content-Encoding: gzip
     * Connection: keep-alive
     *
     *
     * <html>asdfaf</html>
     */
    public void buildHttpResponse(Socket clientSocket,String uri) throws Exception{
        //create http response
        OutputStream socketOut=clientSocket.getOutputStream();
        //send http response first line
        socketOut.write("HTTP/1.1 200 OK\r\n".getBytes());
        //send http response heade
        socketOut.write("Content-Type:text/html\r\n".getBytes());
        socketOut.write("\r\n".getBytes());
        socketOut.write("".getBytes());

        String baseDir = "/bio/http";
        String path = "";
        if ("/".equals(uri) || "/index".equals(uri)) {
            path = baseDir+"/index.html";
        }else{
            path = baseDir+uri;
        }

        InputStream inputStream = this.getClass().getResourceAsStream(path);

        if (Objects.isNull(inputStream)) {
            inputStream = this.getClass().getResourceAsStream(baseDir+"/404.html");
        }

        byte[] buffer=new byte[1024];
        int len=-1;
        while ((len = inputStream.read(buffer)) != -1) {
            socketOut.write(buffer,0,len);
        }
        inputStream.close();

        socketOut.flush();
    }

}
