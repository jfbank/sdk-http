package com.jfai.afs.http.utils;

import org.apache.commons.codec.binary.Base64;

/**
 * @author Nisus Liu
 * @version 0.0.1
 * @email liuhejun108@163.com
 * @date 2019/1/2 11:36
 */
public class Base64Util {


    /**Base64Url 字符 -> Base64字符
     * @param b64u
     * @return
     */
    public static String b64utob64(String b64u) {
        if (b64u == null) {
            return null;
        }
        if (b64u.length() % 4==2) {
            b64u += "==";
        }else{
            if (b64u.length() % 4 == 3) {
                b64u += "=";
            }
        }
        b64u = b64u.replaceAll("-", "+");
        b64u = b64u.replaceAll("_", "/");
        return b64u;
    }

    /**Base64 -> Base64Url
     * @param b64
     * @return
     */
    public static String b64tob64u(String b64) {
        if (b64 == null) {
            return null;
        }
        return b64.replaceAll("=", "")
                .replaceAll("\\+", "-")
                .replaceAll("/", "_");
    }

    public static void main(String[] args) {
        String s = Base64.encodeBase64URLSafeString("好呀哟65".getBytes());
        System.out.println(s);
        System.out.println(b64utob64(s));
    }

}
