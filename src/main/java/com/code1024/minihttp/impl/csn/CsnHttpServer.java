package com.code1024.minihttp.impl.csn;

import com.code1024.minihttp.HttpHandler;
import com.code1024.minihttp.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.Executor;

/**
 * @author ccor2005@gmail.com
 * @date 2021/6/20 15:25
 */
public class CsnHttpServer implements HttpServer {

    private com.sun.net.httpserver.HttpServer server;
    private Executor executor;
    private HttpHandler handler = (req, res) -> {
        res.status(404).send();
    };

    @Override
    public HttpServer create(HttpHandler handler) throws IOException {
        this.handler = handler;
        return this;
    }

    @Override
    public HttpServer executor(Executor executor) {
        this.executor = executor;
        return this;
    }

    @Override
    public void listen(String host, int port) throws IOException {
        server = com.sun.net.httpserver.HttpServer.create(new InetSocketAddress(host, port), 0);
        if(executor != null) {
            server.setExecutor(executor);
        }
        server.createContext("/", httpExchange -> {
            CsnReqImpl req = new CsnReqImpl(httpExchange);
            CsnResImpl res = new CsnResImpl(httpExchange);
            handler.handle(req, res);
            // todo 清理 req 和 res
        });
        server.start();
        executor = server.getExecutor();
    }

    @Override
    public void listen(int port) throws IOException {
        listen("0.0.0.0", port);
    }

    @Override
    public void shutdown(int delay) {
        server.stop(delay);
    }
}
