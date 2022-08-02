package com.code1024.minihttp.impl.nio;

import java.io.IOException;
import java.io.OutputStream;

/**
 * @author ccor2005@gmail.com
 * @date 2021/6/20 18:11
 */
public class ResOutputStreamWrapper extends OutputStream {

    private int len;
    private byte[] b = new byte[1];
    @Override
    public void write(int i) throws IOException {
        b[0] = (byte) i;
        write(b, 0, 1);
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        // todo 缓存并检查缓冲区大小，满了触发发送
        // 发送需要加上head，发送方式未chunked
    }
}
