package com.rao.demo.nio.buff;

import java.nio.IntBuffer;

public class BaseBufferTest {
    public static void main(String[] args) {
        IntBuffer buf = IntBuffer.allocate(6);
        System.out.println("position="+buf.position()+",limit="+buf.limit()+",capacity="+buf.capacity());
        //写入数据
        buf.put(1);
        buf.put(2);
        buf.put(3);
        buf.put(4);
        System.out.println("position="+buf.position()+",limit="+buf.limit()+",capacity="+buf.capacity());

        //需要先调用flip()方法,调整position和limit
        buf.flip();

        System.out.println("position="+buf.position()+",limit="+buf.limit()+",capacity="+buf.capacity());

        //读取数据
        while (buf.hasRemaining()){
            System.out.println(buf.get());
        }

        System.out.println("position="+buf.position()+",limit="+buf.limit()+",capacity="+buf.capacity());

    }
}
