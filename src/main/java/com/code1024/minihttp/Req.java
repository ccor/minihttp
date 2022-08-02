package com.code1024.minihttp;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

/**
 * @author ccor2005@gmail.com
 * @date 2021/6/20 12:51
 */
public interface Req {
    String method();

    String uri();

    String version();

    Map<String, List<String>> headers();

    InputStream bodyInputStream();
}
