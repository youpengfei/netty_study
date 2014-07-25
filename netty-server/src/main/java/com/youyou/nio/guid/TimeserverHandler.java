package com.youyou.nio.guid;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Date;

import org.apache.commons.io.IOUtils;

/**
 * Created with IntelliJ IDEA.
 * User: youpengfei
 * Date: 14/7/18
 * Time: 下午5:01
 * To change this template use File | Settings | File Templates.
 */
public class TimeserverHandler implements Runnable {


    private Socket socket;

    public TimeserverHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {


    }
}
