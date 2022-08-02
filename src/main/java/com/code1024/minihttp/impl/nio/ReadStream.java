package com.code1024.minihttp.impl.nio;

import java.io.IOException;
import java.io.InputStream;
import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import static com.code1024.minihttp.Constants.DEFAULT_BUFFER_SIZE;

/**
 * @author ccor2005@gmail.com
 * @date 2021/6/20 13:09
 */
class ReadStream extends InputStream {
    SocketChannel channel;
    ByteBuffer chanbuf;
    byte[] one;
    private boolean closed = false, eof = false;
    ByteBuffer markBuf; /* reads may be satisfied from this buffer */
    boolean marked;
    boolean reset;
    int readlimit;
    static long readTimeout;

    public ReadStream(SocketChannel chan) throws IOException {
        this.channel = chan;
        chanbuf = ByteBuffer.allocate(DEFAULT_BUFFER_SIZE);
        chanbuf.clear();
        one = new byte[1];
        closed = marked = reset = false;
    }

    public synchronized int read(byte[] b) throws IOException {
        return read(b, 0, b.length);
    }

    public synchronized int read() throws IOException {
        int result = read(one, 0, 1);
        if (result == 1) {
            return one[0] & 0xFF;
        } else {
            return -1;
        }
    }

    public synchronized int read(byte[] b, int off, int srclen) throws IOException {

        int canreturn, willreturn;

        if (closed)
            throw new IOException("Stream closed");

        if (eof) {
            return -1;
        }

        assert channel.isBlocking();

        if (off < 0 || srclen < 0 || srclen > (b.length - off)) {
            throw new IndexOutOfBoundsException();
        }

        if (reset) { /* satisfy from markBuf */
            canreturn = markBuf.remaining();
            willreturn = canreturn > srclen ? srclen : canreturn;
            markBuf.get(b, off, willreturn);
            if (canreturn == willreturn) {
                reset = false;
            }
        } else { /* satisfy from channel */
            chanbuf.clear();
            if (srclen < DEFAULT_BUFFER_SIZE) {
                chanbuf.limit(srclen);
            }
            do {
                willreturn = channel.read(chanbuf);
            } while (willreturn == 0);
            if (willreturn == -1) {
                eof = true;
                return -1;
            }
            chanbuf.flip();
            chanbuf.get(b, off, willreturn);

            if (marked) { /* copy into markBuf */
                try {
                    markBuf.put(b, off, willreturn);
                } catch (BufferOverflowException e) {
                    marked = false;
                }
            }
        }
        return willreturn;
    }

    public boolean markSupported() {
        return true;
    }

    /* Does not query the OS socket */
    public synchronized int available() throws IOException {
        if (closed)
            throw new IOException("Stream is closed");

        if (eof)
            return -1;

        if (reset)
            return markBuf.remaining();

        return chanbuf.remaining();
    }

    public void close() throws IOException {
        if (closed) {
            return;
        }
        channel.close();
        closed = true;
    }

    public synchronized void mark(int readlimit) {
        if (closed)
            return;
        this.readlimit = readlimit;
        markBuf = ByteBuffer.allocate(readlimit);
        marked = true;
        reset = false;
    }

    public synchronized void reset() throws IOException {
        if (closed)
            return;
        if (!marked)
            throw new IOException("Stream not marked");
        marked = false;
        reset = true;
        markBuf.flip();
    }
}
