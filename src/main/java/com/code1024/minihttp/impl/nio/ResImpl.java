package com.code1024.minihttp.impl.nio;

import com.code1024.minihttp.Constants;
import com.code1024.minihttp.HttpStatusCode;
import com.code1024.minihttp.Res;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

/**
 * @author ccor2005@gmail.com
 * @date 2021/6/20 13:26
 */
class ResImpl implements Res {
    int status;
    Headers headers;
    HttpConnection connection;
    OutputStream out;

    ResImpl(HttpConnection connection) throws IOException {
        this.connection = connection;
        this.out = new WriteStream(connection.channel);
        this.status = HttpStatusCode.HTTP_OK;
    }

    @Override
    public Res status(int val) {
        this.status = val;
        return this;
    }

    @Override
    public Res headers(String name, String val) {
        lazyInitHeaders().add(name, val);
        return this;
    }

    @Override
    public Map<String, List<String>> headers() {
        return lazyInitHeaders();
    }

    @Override
    public Res headers(String name, List<String> list) {
        lazyInitHeaders().put(name, list);
        return this;
    }

    @Override
    public OutputStream bodyOutStream() {
        // todo 这里外部写入需要触发发送头
        return out;
    }

    @Override
    public void send() throws IOException {
        this.send((byte[]) null);
    }

    @Override
    public void send(byte[] b) throws IOException {
        StringBuilder sb = new StringBuilder(512);
        sb.append("HTTP/1.1 ")
                .append(status)
                .append(HttpStatusCode.msg(status))
                .append(Constants.CRLF);

        if (b != null && b.length > 0) {
            sb.append("Content-Length: ").append(b.length).append(Constants.CRLF);
            sb.append("Connection: ").append(connection.keepAlive ? "close" : "keep-alive").append(Constants.CRLF);
            if (headers != null && !headers.isEmpty()) {
                for (Map.Entry<String, List<String>> en : headers.entrySet()) {
                    String k = en.getKey();
                    List<String> vals = en.getValue();
                    if(vals != null) {
                        for(String val : vals) {
                            appendHead(sb, k, val);
                        }
                    }else{
                        appendHead(sb, k, null);
                    }
                }
            }
        } else {
            sb.append("Content-Length: 0\r\n");
        }
        sb.append(Constants.CRLF);
        out.write(sb.toString().getBytes(StandardCharsets.UTF_8));
        if (b != null && b.length > 0) {
            out.write(b);
        }
        out.flush();
    }

    @Override
    public void send(String s) throws IOException {
        this.send(s.getBytes(StandardCharsets.UTF_8));
    }

    private void appendHead(StringBuilder sb, String k, String v) {
        sb.append(k).append(Constants.KEY_VALUE_SEPARATOR).append(" ");
        if (v != null) {
            sb.append(v);
        }
        sb.append(Constants.CRLF);
    }

    private Headers lazyInitHeaders() {
        if(headers == null) {
            headers = new Headers();
        }
        return headers;
    }
}
