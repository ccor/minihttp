package com.code1024.minihttp.impl.nio;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

import static com.code1024.minihttp.Constants.DEFAULT_BUFFER_SIZE;

/**
 * @author ccor2005@gmail.com
 * @date 2021/6/20 13:09
 */
class WriteStream extends java.io.OutputStream {
    SocketChannel channel;
    ByteBuffer buf;
    SelectionKey key;
    boolean closed;
    byte[] one;

    public WriteStream(SocketChannel channel) throws IOException {
        this.channel = channel;
        assert channel.isBlocking();
        closed = false;
        one = new byte[1];
        buf = ByteBuffer.allocate(DEFAULT_BUFFER_SIZE);
    }

    public synchronized void write(int b) throws IOException {
        one[0] = (byte) b;
        write(one, 0, 1);
    }

    public synchronized void write(byte[] b) throws IOException {
        write(b, 0, b.length);
    }

    public synchronized void write(byte[] b, int off, int len) throws IOException {
        int l = len;
        if (closed)
            throw new IOException("stream is closed");

        int cap = buf.capacity();
        if (cap < len) {
            int diff = len - cap;
            buf = ByteBuffer.allocate(2 * (cap + diff));
        }
        buf.clear();
        buf.put(b, off, len);
        buf.flip();
        int n;
        while ((n = channel.write(buf)) < l) {
            l -= n;
            if (l == 0)
                return;
        }
    }

    public void close() throws IOException {
        if (closed)
            return;
        //server.logStackTrace ("Request.OS.close: isOpen="+channel.isOpen());
        channel.close();
        closed = true;
    }
}
