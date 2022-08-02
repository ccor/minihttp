package com.code1024.minihttp.impl.nio;

import lombok.extern.slf4j.Slf4j;

import java.util.*;

/**
 * @author ccor2005@gmail.com
 * @date 2021/6/20 13:11
 */
@Slf4j
class HttpConnectionManager {
    //todo 连接管理还需要完善，对req或res阶段的超时处理
    Set<HttpConnection> allConnections;
    Set<HttpConnection> idleConnections;
    private long maxIdleInterval = 30000L;
    private Timer timer;


    HttpConnectionManager() {
        allConnections = new HashSet<>();
        idleConnections = new HashSet<>();
        timer = new Timer("minihttp-con-manager-timer", true);
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                LinkedList<HttpConnection> toClose = new LinkedList<>();
                long time = System.currentTimeMillis();
                synchronized (idleConnections) {
                    for (HttpConnection connection : idleConnections) {
                        if (connection.time <= time) {
                            toClose.add(connection);
                        }
                    }
                    for (HttpConnection connection : toClose) {
                        close(connection);
                    }
                }
            }
        }, 10000L, 10000L);
    }

    void newRequest(HttpConnection connection) {
        allConnections.add(connection);
        connection.time = System.currentTimeMillis() + maxIdleInterval;
        idleConnections.add(connection);
    }

    void requestStarted(HttpConnection connection) {
        idleConnections.remove(connection);
    }

    void requestCompleted(HttpConnection connection) {
        connection.time = System.currentTimeMillis() + maxIdleInterval;
        idleConnections.add(connection);
    }

    void close(HttpConnection connection) {
        idleConnections.remove(connection);
        allConnections.remove(connection);
        connection.close();
        log.info("close idle connection:{}", connection);
    }
}
