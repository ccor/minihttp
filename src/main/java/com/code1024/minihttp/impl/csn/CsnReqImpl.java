package com.code1024.minihttp.impl.csn;

import com.code1024.minihttp.Req;
import com.sun.net.httpserver.HttpExchange;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

/**
 * @author ccor2005@gmail.com
 * @date 2021/6/20 15:40
 */
class CsnReqImpl implements Req {
    private HttpExchange httpExchange;

    CsnReqImpl(HttpExchange httpExchange) {
        this.httpExchange = httpExchange;
    }

    @Override
    public String method() {
        return httpExchange.getRequestMethod();
    }

    @Override
    public String uri() {
        return httpExchange.getRequestURI().toString();
    }

    @Override
    public String version() {
        return httpExchange.getProtocol();
    }

    @Override
    public Map<String, List<String>> headers() {
        return httpExchange.getRequestHeaders();
    }

    @Override
    public InputStream bodyInputStream() {
        return httpExchange.getRequestBody();
    }
}
