package com.code1024.minihttp;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

/**
 * @author ccor2005@gmail.com
 * @date 2021/6/20 12:51
 */
public interface Res {
    Res status(int val);

    Res headers(String name, String val);

    Res headers(String name, List<String> list);

    Map<String, List<String>> headers();

    OutputStream bodyOutStream();

    void send() throws IOException;

    void send(byte[] b) throws IOException;

    void send(String s) throws IOException;
}
