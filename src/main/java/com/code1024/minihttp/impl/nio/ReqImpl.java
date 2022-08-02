package com.code1024.minihttp.impl.nio;

import com.code1024.minihttp.Constants;
import com.code1024.minihttp.Req;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

/**
 * @author ccor2005@gmail.com
 * @date 2021/6/20 13:26
 */
class ReqImpl implements Req {
    final static int BUF_LEN = 2048;
    final static byte CR = 13;
    final static byte LF = 10;
    String startLine;
    String method;
    String uri;
    String version;
    Headers headers;
    HttpConnection connection;
    InputStream in;
    StringBuilder sb;

    ReqImpl(HttpConnection connection) throws IOException {
        this.connection = connection;
        this.headers = new Headers();
        this.in = new ReadStream(connection.channel);
        this.sb = new StringBuilder(BUF_LEN);
        startLine = readLine();
        if (startLine == null) {
            return;
        }
        parseRequestLine();
        parseRequestHead();
    }

    void parseRequestLine() {
        String[] parts = startLine.split(" ");
        // 请求头的第一行必须由三部分构成，分别为 METHOD PATH VERSION
        // 比如：GET /index.html HTTP/1.1
        if (parts.length < 3) {
            return;
        }
        this.method = parts[0];
        this.uri = parts[1];
        this.version = parts[2];
    }

    void parseRequestHead() throws IOException {
        // 解析请求头属于部分
        String line;
        int pos;
        while ((line = readLine()) != null && line.length() > 0) {
            pos = line.indexOf(Constants.KEY_VALUE_SEPARATOR);
            if (pos == -1) {
                continue;
            }
            String key = line.substring(0, pos);
            if (pos + 1 >= line.length()) {
                headers.add(key, "");
                continue;
            }
            String val = line.substring(pos + 1).trim();
            headers.add(key, val);
        }
    }

    String readLine() throws IOException {
        boolean gotCR = false, gotLF = false;
        while (!gotLF) {
            int c = in.read();
            if (c == -1) {
                return null;
            }
            if (gotCR) {
                if (c == LF) {
                    gotLF = true;
                } else {
                    gotCR = false;
                    sb.append((char) CR).append((char) c);
                }
            } else {
                if (c == CR) {
                    gotCR = true;
                } else {
                    sb.append((char) c);
                }
            }
        }
        String line = sb.toString();
        sb.setLength(0);
        return line;
    }


    @Override
    public String method() {
        return method;
    }

    @Override
    public String uri() {
        return uri;
    }

    @Override
    public String version() {
        return version;
    }

    @Override
    public Map<String, List<String>> headers() {
        return headers;
    }

    @Override
    public InputStream bodyInputStream() {
        // todo 需要包装inputStream
        return null;
    }
}
