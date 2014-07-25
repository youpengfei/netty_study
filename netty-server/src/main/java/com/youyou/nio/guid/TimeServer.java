package com.youyou.nio.guid;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;

import org.apache.commons.io.IOUtils;

import sun.java2d.loops.TransformHelper;

/**
 * User: youpengfei
 * Date: 14/7/18
 * Time: 下午4:57
 */
public class TimeServer {

    public static final String QUERY_TIME_ORDER = "Query time order";
    public static final String BAD_ORDER = "bad order\n";

    public static void main(String[] args) throws IOException {
        int port = 8080;
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(port);
            while (true) {
                final Socket socket = serverSocket.accept();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        BufferedReader in = null;
                        PrintWriter out = null;
                        try {
                            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                            out = new PrintWriter(socket.getOutputStream(), true);
                            String currentTime = null;
                            String body = null;
                            while (true) {
                                body = in.readLine();
                                currentTime = QUERY_TIME_ORDER.equalsIgnoreCase(body) ? new Date().toString() + "\n"
                                                                                      : BAD_ORDER;
                                out.print(currentTime);
                                out.flush();
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        } finally {
                            IOUtils.closeQuietly(out);
                            IOUtils.closeQuietly(in);
                            IOUtils.closeQuietly(socket);
                        }
                    }
                }).start();
            }

        } finally {
            IOUtils.closeQuietly(serverSocket);
        }

    }

}
