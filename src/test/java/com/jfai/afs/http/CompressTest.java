package com.jfai.afs.http;

import com.jfai.afs.http.utils.GzipUtils;
import org.apache.commons.codec.binary.Base64;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.Duration;
import java.util.concurrent.TimeUnit;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * @author Nisus Liu
 * @version 0.0.1
 * @email liuhejun108@163.com
 * @date 2018/11/27 14:07
 */
public class CompressTest {

    @Test
    public void fun2() throws IOException {
        String plainTxt = "https://h5-opo-cr-saas.yilianshuyun.com/#/?appid=53fd5aa9486a455e867434b21692205a&extdata=&backUrl=&themeColor=FF0000&loginParams={\"id_card\":\"210824197002085616\",\"user_id\":\"\",\"real_name\":\"周润发\",\"account\":\"147010705473\"}\n";
        System.out.println("源文长度: "+plainTxt.getBytes().length);
        byte[] gzip = GzipUtils.gzip(plainTxt);
        System.out.println("压缩后长度:"+gzip.length);

        //解压缩
        byte[] unzip = GzipUtils.ungzipBytes(gzip);
        System.out.println(new String(unzip));
    }


    @Test
    public void fun1() throws IOException {
        String plainTxt = "https://h5-opo-cr-saas.yilianshuyun.com/#/?appid=53fd5aa9486a455e867434b21692205a&extdata=&backUrl=&themeColor=FF0000&loginParams={\"id_card\":\"210824197002085616\",\"user_id\":\"\",\"real_name\":\"周润发\",\"account\":\"147010705473\"}\n";
        System.out.println("源文长度: "+plainTxt.getBytes().length);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        GZIPOutputStream gzip = new GZIPOutputStream(out);
        gzip.write(plainTxt.getBytes());
        gzip.close();
        byte[] compressed = out.toByteArray();
        System.out.println("压缩后长度:"+compressed.length);

        String compressedBase = Base64.encodeBase64URLSafeString(compressed);
        System.out.println(compressedBase);
        System.out.println("Base64后长度: "+compressedBase.getBytes().length);
        System.out.println(Base64.encodeBase64String(compressed));

        //解压缩
        ByteArrayOutputStream deout = new ByteArrayOutputStream();
        ByteArrayInputStream in = new ByteArrayInputStream(compressed);
        GZIPInputStream ungzip = new GZIPInputStream(in);
        byte[] buff = new byte[1024];
        int n;
        while ((n = ungzip.read(buff)) >=0) {
            deout.write(buff, 0, n);
        }
        System.out.println(deout.toString());
    }

}
