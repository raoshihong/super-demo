package com.rao.demo.nio.buff;

import java.nio.ByteBuffer;

public class ByteBufferTest {
    public static void main(String[] args) {
        ByteBuffer buffer = ByteBuffer.allocate(20);

        buffer.putInt(1);//1个整型占用4个字节
        buffer.putChar('a');//一个字符占用2个字节
        buffer.putDouble(10);//一个double占用8个字节
        buffer.put("中国".getBytes());//一个中文3个字节

        //flip,调整position和limit
        buffer.flip();

        //进行读取,读取顺序最好与写入数据顺序一致,否则可能会抛异常
        int a = buffer.getInt();
        char b = buffer.getChar();
        double c = buffer.getDouble();
        byte[] buf = new byte[6];
        buffer.get(buf,buffer.arrayOffset(),buffer.remaining());
        String str = new String(buf,0,6);
        System.out.println(str);
    }
}
