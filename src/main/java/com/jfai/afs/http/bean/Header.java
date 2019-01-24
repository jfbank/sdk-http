package com.jfai.afs.http.bean;

import org.apache.commons.lang.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * <pre>
 *     头键, 统一小写
 * </pre>
 * @author Nisus Liu
 * @version 0.0.1
 * @email liuhejun108@163.com
 * @date 2019/1/15 2:13
 */
public class Header {

    protected Map<String, String> values = new HashMap<>();

    public Header append(String name, String value) {
        if (StringUtils.isBlank(name)) {
            return this;
        }
        values.put(name.toLowerCase(), value);
        return this;
    }

    public String get(String name) {
        if (StringUtils.isBlank(name)) {
            return null;
        }
        return values.get(name.toLowerCase());
    }

    public Set<Map.Entry<String, String>> entrySet() {
        return values.entrySet();
    }

    public Set<String> keySet() {
        return values.keySet();
    }

    public Map<String, String> values() {
        return values;
    }

}
