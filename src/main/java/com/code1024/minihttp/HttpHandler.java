package com.code1024.minihttp;

import java.io.IOException;

/**
 * @author ccor2005@gmail.com
 * @date 2021/6/20 13:02
 */
@FunctionalInterface
public interface HttpHandler {
    void handle(Req req, Res res) throws IOException;
}
