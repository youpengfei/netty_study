package com.youyou.nio.action;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.CountDownLatch;

/**
 * Created with IntelliJ IDEA.
 * User: youpengfei
 * Date: 14/7/23
 * Time: 下午6:17
 * To change this template use File | Settings | File Templates.
 */
public class PlainNio2EchoServer {

    public void server(int port) throws IOException {
        System.out.println("服务启动端口为：" + port);
        final AsynchronousServerSocketChannel serverSocketChannel = AsynchronousServerSocketChannel.open();
        InetSocketAddress address = new InetSocketAddress(port);
        serverSocketChannel.bind(address);
        final CountDownLatch latch = new CountDownLatch(1);
        serverSocketChannel.accept(null, new CompletionHandler<AsynchronousSocketChannel, Object>() {
            @Override
            public void completed(AsynchronousSocketChannel channel, Object attachment) {
                serverSocketChannel.accept(null, this);
                ByteBuffer buffer = ByteBuffer.allocate(100);
                channel.read(buffer, buffer, new EchoCompletionHandler(channel));
            }

            @Override
            public void failed(Throwable exc, Object attachment) {
                try {
                    serverSocketChannel.close();
                } catch (IOException ignored) {
                    //ignore
                } finally {
                    latch.countDown();
                }
            }
        });
        try {
            latch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public static void main(String[] args) throws IOException {
        new PlainNio2EchoServer().server(8080);
    }

    private final class EchoCompletionHandler implements CompletionHandler<Integer, ByteBuffer> {
        AsynchronousSocketChannel channel;
        public EchoCompletionHandler(AsynchronousSocketChannel channel) {
            this.channel = channel;
        }
        @Override
        public void completed(Integer result, ByteBuffer buffer) {
            buffer.flip();
            System.out.println(new String(buffer.array()));
            channel.write(buffer, buffer, new CompletionHandler<Integer, ByteBuffer>() {
                @Override
                public void completed(Integer result, ByteBuffer buffer) {
                    if (buffer.hasRemaining()) {
                        channel.write(buffer, buffer, this);
                    } else {
                        buffer.compact();
                        channel.read(buffer, buffer, EchoCompletionHandler.this);
                    }

                }

                @Override
                public void failed(Throwable exc, ByteBuffer attachment) {

                    try {
                        channel.close();
                    } catch (IOException e) {
                        //ignore
                    }
                }
            });
            buffer.compact();
        }

        @Override
        public void failed(Throwable exc, ByteBuffer attachment) {
            try {
                channel.close();
            } catch (IOException ignored) {

            }
        }
    }
}


