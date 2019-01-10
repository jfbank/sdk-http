package com.jfai.afs.http;

import org.apache.commons.codec.binary.Base64;
import org.junit.Test;

import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;

/**
 * @author Nisus Liu
 * @version 0.0.1
 * @email liuhejun108@163.com
 * @date 2018/11/27 10:15
 */
public class Base64Test {

    @Test
    public void fun5(){
        String s = "本地人发红包";
        System.out.println(s.getBytes().length);

        String s1 = Base64.encodeBase64URLSafeString(s.getBytes());


        System.out.println(s1+", "+s1.getBytes().length);

        String s2 = new String((Base64.decodeBase64(s1)));
        System.out.println(s2 + ", " + s2.getBytes().length);
    }

    @Test
    public void fun4(){
        String s = "本地人发红包";
        System.out.println(s.getBytes().length);

        String s1 = URLEncoder.encode(Base64.encodeBase64String(s.getBytes()));

        System.out.println(s1+", "+s1.getBytes().length);

        String s2 = new String((Base64.decodeBase64(URLDecoder.decode(s1))));
        System.out.println(s2 + ", " + s2.getBytes().length);
    }

    @Test
    public void fun3(){
        String s = "本地人发红包";
        System.out.println(s.getBytes().length);

        String s1 = new String(Base64.encodeBase64(Base64.encodeBase64(s.getBytes())));
        //System.out.println("encryptedString" + encryptedString);
        System.out.println(s1+", "+s1.getBytes().length);

        String s2 = new String(Base64.decodeBase64(Base64.decodeBase64(s1.getBytes())));
        System.out.println(s2 + ", " + s2.getBytes().length);
    }

    @Test
    public void fun2(){
        String s = "本地人发红包";
        System.out.println(s.getBytes().length);

        String s1 = new String(Base64.encodeBase64(s.getBytes()));
        //System.out.println("encryptedString" + encryptedString);
        System.out.println(s1+", "+s1.getBytes().length);

        Base64 encoder = new Base64();
        byte[] encodedBytes = encoder.encode(s1.getBytes());
        String s11 = new String(encodedBytes);
        System.out.println(s11+", "+s11.getBytes().length);
    }

    @Test
    public void fun1(){
        String s = "本地人发红包";
        System.out.println(s.getBytes().length);
        String s1 = new String(Base64.encodeBase64(s.getBytes()));
        System.out.println(s1+", "+s1.getBytes().length);
        String s11 = new String(Base64.encodeBase64(s1.getBytes()));
        System.out.println(s11+", "+s11.getBytes().length);
    }
}
