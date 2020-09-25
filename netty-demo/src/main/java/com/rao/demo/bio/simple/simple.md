1. 服务端在accept方法阻塞,等待客户端的连接,如果设置了SoTimeout,则如果超过这个时间还没有客户端连接上来
 则会抛出SocketTimeOutException异常
 
2. 客户端在read方法阻塞,如果一直没有读取到数据,且服务端没有关闭客户端连接,则客户端会一直阻塞在read这个方法

3. InputStream.read读取二进制时,可能会存在读取半包的问题,所以最好是使用ByteArrayOutputStream进行字节缓存

4. 目前这个案例只是单线程的socket,服务端每次都是阻塞在accept方法,且每次只能处理一个客户端的操作

优点：
代码简单，容易理解

缺点：
 > 多个连接阻塞在同一个accept上 
 
 > 单线程,不支持高并发