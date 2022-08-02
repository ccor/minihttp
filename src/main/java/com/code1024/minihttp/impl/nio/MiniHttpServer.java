package com.code1024.minihttp.impl.nio;

import com.code1024.minihttp.HttpHandler;
import com.code1024.minihttp.HttpServer;
import com.code1024.minihttp.HttpStatusCode;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@Slf4j
/**
 * @author ccor2005@gmail.com
 * @date 2021/6/20 13:07
 */
public class MiniHttpServer implements HttpServer {
    private boolean started = false;
    private boolean running = true;
    private ServerSocketChannel ssChannel;
    private Selector selector;
    private SelectionKey listenerKey;
    private Executor executor;
    private HttpHandler handler = (req, res) -> {
        res.status(404).send();
    };
    private HttpConnectionManager connectionManager;


    private MiniHttpServer() throws IOException {
        ssChannel = ServerSocketChannel.open();
        selector = Selector.open();
        ssChannel.configureBlocking(false);
        listenerKey = ssChannel.register(selector, SelectionKey.OP_ACCEPT);
        connectionManager = new HttpConnectionManager();
    }

    @Override
    public HttpServer create(HttpHandler handler) throws IOException {
        MiniHttpServer httpServer = new MiniHttpServer();
        httpServer.handler = handler;
        return httpServer;
    }

    @Override
    public HttpServer executor(Executor executor) {
        if (this.started) {
            throw new IllegalStateException("server already started");
        }
        this.executor = executor;
        return this;
    }

    @Override
    public void listen(String host, int port) throws IOException {
        if (started) {
            throw new IllegalStateException("server in wrong state");
        }
        if(executor == null) {
            executor = Executors.newSingleThreadExecutor();
        }
        ServerSocket socket = ssChannel.socket();
        socket.bind(new InetSocketAddress(host, port));
        Thread t = new Thread(() -> {
            while (running) {
                try {
                    selector.select(1000);
                    Set<SelectionKey> selected = selector.selectedKeys();
                    Iterator<SelectionKey> iter = selected.iterator();
                    HttpConnection connection;
                    while (iter.hasNext()) {
                        SelectionKey key = iter.next();
                        iter.remove();
                        if (key.equals(listenerKey)) {
                            SocketChannel channel = ssChannel.accept();
                            channel.configureBlocking(false);
                            SelectionKey selectionKey = channel.register(selector, SelectionKey.OP_READ);

                            connection = new HttpConnection();
                            connection.channel = channel;
                            selectionKey.attach(connection);
                            connectionManager.newRequest(connection);

                        } else if (key.isReadable()) {
                            SocketChannel channel = (SocketChannel) key.channel();
                            log.info("isReadable:{}@{}", channel, channel.hashCode());
                            connection = (HttpConnection) key.attachment();
                            key.cancel();
                            channel.configureBlocking(true);
                            handle(connection);
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    // todo handle ex
                }
            }
        });

        started = true;
        t.start();
    }

    // todo 交易处里
    void handle(HttpConnection connection) throws IOException {
        connectionManager.requestStarted(connection);

        // 请求来创建一个任务扔到执行器执行
        this.executor.execute(() -> {
            try {
                ReqImpl req = new ReqImpl(connection);
                ResImpl res = new ResImpl(connection);

                // 检查请求是否合法
                if (req.startLine == null) {
                    res.status(HttpStatusCode.HTTP_BAD_REQUEST).send();
                    return;
                }
                // 处理keep-alive
//                if (req.version.equalsIgnoreCase("http/1.0")) {
//                    res.headers("Connection", "close");
//                } else
                if (req.headers.getFirst("Connection").equalsIgnoreCase("keep-alive")) {
                    res.headers("Connection", "keep-alive");
                    res.headers("Keep-Alive", "timeout=30000, max=3");
                    connection.keepAlive = true;
                }

                handler.handle(req, res);
                // todo 需要 检查req 流是否正常读完？ 检查res的流程是否flush？


                if (connection.keepAlive) {
                    connection.channel.configureBlocking(false);
                    SelectionKey key = connection.channel.register(selector, SelectionKey.OP_READ);
                    key.attach(connection);
                }
                connectionManager.requestCompleted(connection);
            } catch (IOException e) {
                e.printStackTrace();
                connectionManager.close(connection);
            }

        });
    }

    @Override
    public void listen(int port) throws IOException {
        this.listen("0.0.0.0", port);
        System.out.println("server start.");
    }

    @Override
    public void shutdown(int delay) {
        // todo close
    }

}
