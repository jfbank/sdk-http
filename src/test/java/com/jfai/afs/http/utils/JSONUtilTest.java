package com.jfai.afs.http.utils;

import com.alibaba.fastjson.JSON;
import com.jfai.afs.http.bean.JfReqParam;
import org.junit.Test;

import java.util.HashMap;

import static org.junit.Assert.*;

public class JSONUtilTest {

    @Test
    public void fun1(){
        HashMap<String, Object> mm = new HashMap<>();
        mm.put("name", "libao,i");
        HashMap<String, Object> m = new HashMap<>();
        m.put("type", "son, \"who's\" son");
        m.put("age", 19);
        //mm.put("son", m);
        mm.put("son", JSON.toJSONString(m));

        String ss = JSON.toJSONString(mm);

//        for (char c : ss.toCharArray()) {
//            System.out.println(c);
//        }

        System.out.println(ss);

        System.out.println(JSONUtil.formatJson(ss));

        //
        System.out.println(JSONUtil.formatJson("{\"type\":\"son, \\\\\"who's\\\\\" son\",\"age\":19}"));


    }

    @Test
    public void formatJson() {
        JfReqParam p = new JfReqParam();
        HashMap m = new HashMap();
        m.put(new String[]{"1", "2"}, "字符数组");
        m.put(545, new Integer[]{3, 46, 78,});
        p.setData(m);

        String s = JSON.toJSONString(p);
        System.out.println(s);

        System.out.println(JSONUtil.formatJson(s));
        System.out.println("-----");
        System.out.println(JSONUtil.formatJson(p.getData()));

        System.out.println(p);
        System.out.println(p.toString(true));
    }

    @Test
    public void toJsonAdapt() {
    }
}