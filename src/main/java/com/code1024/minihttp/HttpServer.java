package com.code1024.minihttp;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.Executor;

/**
 * @author ccor2005@gmail.com
 * @date 2021/6/20 13:34
 */
public interface HttpServer {
    HttpServer create(HttpHandler handler) throws IOException;
    HttpServer executor(Executor executor);
    void listen(String host, int port) throws IOException;
    void listen(int port) throws IOException;
    void shutdown(int delay);

    static <T extends HttpServer> HttpServer of(Class<T> x) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        Constructor<T> c = x.getDeclaredConstructor(null);
        c.setAccessible(true);
        return c.newInstance();
    }
}
