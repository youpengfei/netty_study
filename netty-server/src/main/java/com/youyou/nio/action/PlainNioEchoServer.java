package com.youyou.nio.action;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: youpengfei
 * Date: 14/7/23
 * Time: 下午3:09
 * To change this template use File | Settings | File Templates.
 */
public class PlainNioEchoServer {

    public static void serve(int port) throws IOException {
        System.out.println("监听端口为：" + port);
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        ServerSocket serverSocket = serverSocketChannel.socket();
        InetSocketAddress inetSocketAddress = new InetSocketAddress(port);
        serverSocket.bind(inetSocketAddress);
        serverSocketChannel.configureBlocking(false);
        Selector selector = Selector.open();
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        while (true) {
            try {
                selector.select();
            } catch (Exception e) {

            }
            Set<SelectionKey> selectionKeys = selector.selectedKeys();
            Iterator<SelectionKey> iterator = selectionKeys.iterator();
            while (iterator.hasNext()) {
                SelectionKey selectionKey = iterator.next();
                iterator.remove();
                try {
                    if (selectionKey.isAcceptable()) {
                        ServerSocketChannel server = (ServerSocketChannel) selectionKey.channel();
                        SocketChannel client = server.accept();
                        System.out.println("获取到连接，客户端为:" + client);
                        client.configureBlocking(false);
                        client.register(selector, SelectionKey.OP_WRITE | SelectionKey.OP_READ,
                                        ByteBuffer.allocate(8));
                    }
                    if (selectionKey.isReadable()) {
                        SocketChannel client = (SocketChannel) selectionKey.channel();
                        ByteBuffer out = (ByteBuffer) selectionKey.attachment();
                        client.read(out);
                        if (selectionKey.isWritable()) {
                            SocketChannel client1 = (SocketChannel) selectionKey.channel();
                            ByteBuffer out1 = (ByteBuffer) selectionKey.attachment();

                            System.out.println("内容为："+new String(out1.array()));
                            if (out.array().length>0){
                                out1.flip();
                                client.write(ByteBuffer.wrap("来自服务端:".getBytes("utf8")));
                                client.write(out1);
                                client.write(ByteBuffer.wrap("\n".getBytes("utf8")));
                                out.compact();
                            }
                        }
                    }



                } catch (Exception e) {
                    selectionKey.cancel();
                    try {
                        selectionKey.channel().close();
                    } catch (Exception e1) {

                    }
                }
            }
        }

    }

    public static void main(String[] args) throws IOException {
        serve(8080);
    }
}
