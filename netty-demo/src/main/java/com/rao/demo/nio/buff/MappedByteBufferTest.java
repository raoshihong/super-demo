package com.rao.demo.nio.buff;


import java.io.File;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

public class MappedByteBufferTest {
    public static void main(String[] args) throws Exception{

        //创建一个可以随机读写文件的对象
        //文件内容：asdfasffffsss
        RandomAccessFile randomAccessFile = new RandomAccessFile(new File("/Users/raoshihong/work/script/test.txt"),"rw");

        //获取通道
        FileChannel channel = randomAccessFile.getChannel();

        //通过通道获取MappedByteBuffer,map方法实现将指定position开始位置，sizte大小的数据加载到内存buffer中
        /**
         * map方法
         * 参数1表示读写模式,READ_WRITE表示可读可写
         * 参数2,position表示读取开始位置
         * 参数3,size表示读取多少个数据
         * 返回值：MappedByteBuffer
         * 下面position=3,site=4 表示读取目标文件中从索引位置为3开始读取，并读取4个字节,保存到系统内存中,并返回一个MappedByteBuffer
         * 实际类型为DirectByteBuffer，这个buffer中position=0,limit=4,capacity=4,hb[]=
         */
        MappedByteBuffer byteBuffer = channel.map(FileChannel.MapMode.READ_WRITE,3,4);

        //将读取到的数据输出
        byte[] bytes = new byte[4];
        byteBuffer.get(bytes,0,4);
        System.out.println(new String(bytes,0,4));

        byteBuffer.flip();

        //修改返回的MappedByteBuffer的
        byteBuffer.put(2,(byte)'x');//表示修改byteBuffer第2个位置第数据
        byteBuffer.put(3,(byte)'y');

        byteBuffer.get(bytes,0,4);
        System.out.println(new String(bytes,0,4));


        //上面修改MappedByteBuffer中第数据后,关闭randomAccessFile会自动将内存中第数据刷出写入到对应文件中
        randomAccessFile.close();
    }
}
