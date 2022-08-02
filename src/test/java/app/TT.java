package app;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

/**
 * @author
 * @date 2021/6/20 13:32
 */
@Slf4j
public class TT {
    public static void main(String[] args) throws IOException {
        int port = 8080;
        HttpServer server = HttpServer.create(new InetSocketAddress("0.0.0.0", port), 0);
        server.createContext("/", new com.sun.net.httpserver.HttpHandler() {
            @Override
            public void handle(HttpExchange httpExchange) throws IOException {
                log.info("reqURI:{}", httpExchange.getRequestURI());
//                try {
//                    String length = httpExchange.getRequestHeaders().getFirst("Content-Length");
//                    int len = length == null ? 8192 : Integer.parseInt(length);
//                    // todo len < 0
//                    byte[] body = new byte[len];
//                    InputStream in = httpExchange.getRequestBody();
//                    int readLen = 0, l = 0;
//                    while (readLen < len) {
//                        l = in.read(body, readLen, len);
//                        if (l == -1) {
//                            // todo ex len
//                            break;
//                        }
//                        readLen += l;
//                    }
//
//                    log.info("req:{}", new String(body, 0, readLen));
//                }catch (Exception e) {
//                    log.error("handle ex", e);
//                }
                String res = httpExchange.getRequestURI() + " ok.";
                byte[] resBody = res.getBytes(StandardCharsets.UTF_8);
                httpExchange.getResponseHeaders().set("Content-Type", "text/html;charset=utf-8");
                httpExchange.sendResponseHeaders(200, resBody.length);
                OutputStream out = httpExchange.getResponseBody();
                out.write(resBody);
                out.flush();
                httpExchange.close();
            }
        });

        server.start();
    }



}
