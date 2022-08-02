package app;

import com.code1024.minihttp.HttpServer;
import com.code1024.minihttp.impl.csn.CsnHttpServer;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

/**
 * @author ccor2005@gmail.com
 * @date 2021/4/26 17:29
 */
@Slf4j
@Data
public class T {
    private String username;
    public static void main(String[] args) throws IOException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        HttpServer.of(CsnHttpServer.class).create((req, res) -> {
            res.send("ok, uir:"+req.uri());
            log.info("req:{}", req.uri());
        }).listen(3000);

//        Timer t = new Timer();
//        t.schedule(new TimerTask() {
//            @Override
//            public void run() {
//                System.out.println("----task1");
//            }
//        }, 100, 100);
//        t.schedule(new TimerTask() {
//            @Override
//            public void run() {
//                System.out.println("----task2");
//            }
//        }, 200, 200);
    }

}
