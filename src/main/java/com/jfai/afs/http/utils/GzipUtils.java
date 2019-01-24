package com.jfai.afs.http.utils;

import org.apache.commons.codec.binary.Base64;

import java.io.*;
import java.nio.charset.Charset;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 *
 */
public class GzipUtils {
    private static final Charset UTF8= Charset.forName("UTF-8");


//    /**
//     * 用Base64格式编码压缩数据
//     *
//     * @param source String
//     * @return String
//     * @throws IOException
//     */
//    public static String gzipString(String source) {
//        byte[] gzip = gzip(source);
//        //return Base64.getEncoder().encodeToString(gout.toByteArray());
//        return Base64.encodeBase64URLSafeString(gzip);
//    }

    /**压缩后, 返回base64url编码字符
     * @param source
     * @return
     */
    public static String gzip2b64u(String source) {
        byte[] gzip = gzip(source);
        if (gzip != null) {
            return Base64.encodeBase64URLSafeString(gzip);
        }
        return null;
    }

    public static byte[] gzip(String source) {
        return gzipBytes(source.getBytes(UTF8));
        //return Base64.getEncoder().encodeToString(gout.toByteArray());
    }

    public static byte[] gzipBytes(byte[] source) {
        if (source == null || source.length == 0) {
            return null;
        }
        ByteArrayOutputStream gout = null;
        try {
            gout = new ByteArrayOutputStream();
            GZIPOutputStream gzip = new GZIPOutputStream(gout);
            gzip.write(source);
            gzip.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        //return Base64.getEncoder().encodeToString(gout.toByteArray());
        return gout.toByteArray();
    }

    /**
     * @param in 输入流
     * @return base64编码的字符串
     * @throws IOException
     */
    public static String gzipInputStream(InputStream in) throws IOException {
        if (in == null)
            return null;

        ByteArrayOutputStream gout = new ByteArrayOutputStream();
        GZIPOutputStream gzip = new GZIPOutputStream(gout);

        byte[] buffer = new byte[1024];
        int len = 0;
        while ((len = in.read(buffer)) != -1) {
            gzip.write(buffer, 0, len);
        }
        gzip.close();

        //return Base64.getEncoder().encodeToString(gout.toByteArray());
        return Base64.encodeBase64URLSafeString(gout.toByteArray());
    }


    /**
     * HTTP 响应正文ungzip
     *
     * @param in for HTTP InputStream
     * @return BufferedReader
     * @throws IOException
     */
    private BufferedReader ungzip(InputStream in) throws IOException {

        return new BufferedReader(new InputStreamReader(new GZIPInputStream(in)));
    }

    /**解压普通字符串
     * @param gziped
     * @return
     * @throws IOException
     */
    public static String ungzipString(String gziped) throws IOException {
        byte[] bytes = ungzipBytes(gziped.getBytes(UTF8));

        return new String(bytes,UTF8);
    }

    /**解压 baset64 OR base64Url 编码的字符串
     * @param b64
     * @return
     */
    public static String ungzipb64(String b64) throws IOException {
        if (b64 == null) {
            return null;
        }
        byte[] bytes = Base64.decodeBase64(b64);
        byte[] unziped = ungzipBytes(bytes);
        return new String(unziped, UTF8);
    }

    public static byte[] ungzipBytes(byte[] gziped) throws IOException {
        ByteArrayInputStream in = new ByteArrayInputStream(gziped);
        ByteArrayOutputStream deout = new ByteArrayOutputStream();
        GZIPInputStream ungzip = null;
            ungzip = new GZIPInputStream(in);

        byte[] buff = new byte[1024];
        int n;
        while ((n = ungzip.read(buff)) >=0) {
            deout.write(buff, 0, n);
        }

        return deout.toByteArray();
    }


//    /**
//     * 将base64编码的数据进行解码
//     *
//     * @param base64
//     * @return
//     */
//    public static InputStream base64Decoder(String base64) {
//
//        if (base64 == null) return null;
//        Base64.Decoder decoder = Base64.getDecoder();
//        try {
//            byte[] b = decoder.decode(base64);
//            return new ByteArrayInputStream(b);
//        } catch (Exception e) {
//            e.printStackTrace();
//            return null;
//        }
//
//    }

}