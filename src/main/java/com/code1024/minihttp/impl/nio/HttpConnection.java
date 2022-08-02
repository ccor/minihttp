package com.code1024.minihttp.impl.nio;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.channels.SocketChannel;

/**
 * @author ccor2005@gmail.com
 * @date 2021/6/20 13:07
 */
@Slf4j
class HttpConnection {
    SocketChannel channel;
    boolean closed = false;
    boolean keepAlive = false;
    long time;

    synchronized void close() {
        if (!this.closed) {
            this.closed = true;
            if (!this.channel.isOpen()) {
                log.debug("Channel already closed.");
            } else {
//                    try {
//                        if (this.in != null) {
//                            this.in.close();
//                        }
//                    } catch (IOException e) {
//                        log.debug("close inputStream ex.", e);
//                    }
//                    try {
//                        if (this.out != null) {
//                            this.out.close();
//                        }
//                    } catch (IOException e) {
//                        log.debug("close outputStream ex.", e);
//                    }
                try {
                    this.channel.close();
                } catch (IOException e) {
                    log.debug("close SocketChannel ex.", e);
                }
            }
        }
    }

}
