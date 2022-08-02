package com.code1024.minihttp.impl.csn;

import com.code1024.minihttp.Res;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

/**
 * @author ccor2005@gmail.com
 * @date 2021/6/20 15:41
 */
class CsnResImpl implements Res {
    int status;
    HttpExchange httpExchange;

    CsnResImpl(HttpExchange httpExchange) {
        this.httpExchange = httpExchange;
    }

    @Override
    public Res status(int val) {
        this.status = val;
        return this;
    }

    @Override
    public Res headers(String name, String val) {
        httpExchange.getResponseHeaders().set(name, val);
        return this;
    }

    @Override
    public Res headers(String name, List<String> list) {
        httpExchange.getResponseHeaders().put(name, list);
        return null;
    }

    @Override
    public Map<String, List<String>> headers() {
        return httpExchange.getResponseHeaders();
    }

    @Override
    public OutputStream bodyOutStream() {
        httpExchange.getResponseBody();
        return null;
    }

    @Override
    public void send() throws IOException {
        send((byte[]) null);
    }

    @Override
    public void send(byte[] b) throws IOException {
        httpExchange.sendResponseHeaders(200, b.length);
        httpExchange.getResponseBody().write(b);
    }

    @Override
    public void send(String s) throws IOException {
        send(s.getBytes(StandardCharsets.UTF_8));
    }
}
