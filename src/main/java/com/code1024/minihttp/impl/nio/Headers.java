package com.code1024.minihttp.impl.nio;

import java.util.*;

/**
 * @see com.sun.net.httpserver.Headers
 */
public class Headers implements Map<String, List<String>> {
    HashMap<String, List<String>> map = new HashMap<>(32);

    public Headers() {
    }

    private String normalize(String key) {
        if (key == null) {
            return null;
        } else {
            int len = key.length();
            if (len == 0) {
                return key;
            } else {
                char[] b = key.toCharArray();
                if (b[0] >= 'a' && b[0] <= 'z') {
                    b[0] = (char)(b[0] - 32);
                } else if (b[0] == '\r' || b[0] == '\n') {
                    throw new IllegalArgumentException("illegal character in key");
                }

                for(int i = 1; i < len; ++i) {
                    if (b[i] >= 'A' && b[i] <= 'Z') {
                        b[i] = (char)(b[i] + 32);
                    } else if (b[i] == '\r' || b[i] == '\n') {
                        throw new IllegalArgumentException("illegal character in key");
                    }
                }

                return new String(b);
            }
        }
    }

    public int size() {
        return this.map.size();
    }

    public boolean isEmpty() {
        return this.map.isEmpty();
    }

    public boolean containsKey(Object key) {
        if (key == null) {
            return false;
        } else {
            return key instanceof String && this.map.containsKey(this.normalize((String) key));
        }
    }

    public boolean containsValue(Object value) {
        return this.map.containsValue(value);
    }

    public List<String> get(Object key) {
        return this.map.get(this.normalize((String)key));
    }

    public String getFirst(String key) {
        List<String> l = this.map.get(this.normalize(key));
        return l == null ? null : l.get(0);
    }

    public List<String> put(String key, List<String> value) {

        for (String v : value) {
            checkValue(v);
        }

        return this.map.put(this.normalize(key), value);
    }

    public void add(String key, String value) {
        checkValue(value);
        String k = this.normalize(key);
        List<String> l = this.map.computeIfAbsent(k, k1 -> new LinkedList<>());
        l.add(value);
    }

    private static void checkValue(String value) {
        int len = value.length();

        for(int i = 0; i < len; ++i) {
            char c = value.charAt(i);
            if (c == '\r') {
                if (i >= len - 2) {
                    throw new IllegalArgumentException("Illegal CR found in header");
                }

                char c1 = value.charAt(i + 1);
                char c2 = value.charAt(i + 2);
                if (c1 != '\n') {
                    throw new IllegalArgumentException("Illegal char found after CR in header");
                }

                if (c2 != ' ' && c2 != '\t') {
                    throw new IllegalArgumentException("No whitespace found after CRLF in header");
                }

                i += 2;
            } else if (c == '\n') {
                throw new IllegalArgumentException("Illegal LF found in header");
            }
        }
    }

    public void set(String key, String value) {
        LinkedList<String> l = new LinkedList<>();
        l.add(value);
        this.put(key, l);
    }

    public List<String> remove(Object key) {
        return (List<String>)this.map.remove(this.normalize((String)key));
    }

    public void putAll(Map<? extends String, ? extends List<String>> t) {
        this.map.putAll(t);
    }

    public void clear() {
        this.map.clear();
    }

    public Set<String> keySet() {
        return this.map.keySet();
    }

    public Collection<List<String>> values() {
        return this.map.values();
    }

    public Set<Entry<String, List<String>>> entrySet() {
        return this.map.entrySet();
    }

    public boolean equals(Object o) {
        return this.map.equals(o);
    }

    public int hashCode() {
        return this.map.hashCode();
    }
}
