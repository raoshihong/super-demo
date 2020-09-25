package com.rao.demo.nio.buff;

import java.nio.ByteBuffer;

public class ReadOnlyBufferTest {
    public static void main(String[] args) {
        ByteBuffer buffer = ByteBuffer.allocate(10);
        buffer.put("aaa".getBytes());
        buffer.put("bbb".getBytes());

        //将普通buffer转化为只读buffer,真是类型为HeapByteBufferR
        ByteBuffer readOnlyBuffer = buffer.asReadOnlyBuffer();

        readOnlyBuffer.flip();

        //只读buffer可以读取数据
        while (readOnlyBuffer.hasRemaining()){
            System.out.println(readOnlyBuffer.get());
        }

        readOnlyBuffer.flip();
        //只读buffer不能写入数据,否则抛出异常java.nio.ReadOnlyBufferException
        readOnlyBuffer.put("bb".getBytes());
    }
}
