package com.jfai.afs.http.utils;

import com.alibaba.fastjson.JSON;
import org.apache.commons.codec.binary.Base64;
import org.junit.Test;

import java.io.UnsupportedEncodingException;

/**
 * <pre>
 *     - js 加密 java 解密 OK
 *
 *
 * </pre>
 */
public class AES2Test {

    public static final String KEY = "x4hnmkJR4Q9ZJxbf";

    @Test
    public void fun3(){
        String enc = AES.encrypt("哈喽", KEY);
        System.out.println(enc);
        String dec = AES.decrypt("bM0azjjsmXf4TSFi6y0W0gbM0azjjsmXf4TSFi6y0W0g", KEY);
        System.out.println(dec);
    }

    @Test
    public void fun2(){
        String dec = AES.decrypt("zcHM23_gzBwYtiS3m7mI3MsYwK8TudLRGL6xXfZXG-XuMhGd1NAH8huJbxSm-KlQ6iCwcxVy7NPQ1a_2NO5fCSulhes6hf2oN5tsjURAPr7X7PzedcUzzc3vSavGMaFhEbVUv1Fg7o9_syORgANH3rLGOoLCPyxONMa28v2Xzh_WJHduHUaKy6NV-n75FezWlzqfjdgywER6zgJw2teDqQ-tByY2PnTMO8vUEj2CVUXfBaVlwCuf9jZmJlvs6i-n98y4B3_izqQRt_kbc4KvLWLJz8tl-d3BqNzW4KUV2pTi78FpDBOUL3FQ2PE6B4Y-bSIjWAg6BxRPIdo0IrH3o45rUrfouyMxbKKCKpw_Gok6GZIKd4j3duj9L_JlxQ-d",
                "yg4CvHxmr5RGdhTU");
        System.out.println(dec);

        System.out.println(JSON.parseObject(dec));
    }

    @Test
    public void fun(){
//        RSA.RSAInfo rsaInfo = RSA.generateKeyPair();
//        System.out.println(JSON.toJSONString(rsaInfo));

        RSA.RSAInfo rsaInfo = JSON.parseObject("{\"MODULUS\":\"AK40aFzHqlo3witfmxP41GdaVmDVegOZvJ6Tu11ayyEAtg9rmIzCACFT9bbdmPjMySlHBIvEu1ZJP5zbDIvZbgt4R0BO-lcwO3Et_EFB5pgwDqzqllRL4NikMRC_iAEEQq2y6BNU9ck5snIGsRdSykPogLpiN22D0c8XwjjspknV\",\"PRIVATE_KEY\":\"MIICdQIBADANBgkqhkiG9w0BAQEFAASCAl8wggJbAgEAAoGBAK40aFzHqlo3witfmxP41GdaVmDVegOZvJ6Tu11ayyEAtg9rmIzCACFT9bbdmPjMySlHBIvEu1ZJP5zbDIvZbgt4R0BO-lcwO3Et_EFB5pgwDqzqllRL4NikMRC_iAEEQq2y6BNU9ck5snIGsRdSykPogLpiN22D0c8XwjjspknVAgMBAAECgYBk4tD1umQ7r0Lx34JuBJLt7_FKTxdEcoo7xA6kN-qfiEvnjkZgbhGpO3h3mMn4fJCbKRfG4f4kMHN4oZsYeqkxYReDcpRIaBMVq8QXF7Az8wnVVFhCdGPQUXeXoWUm3ef8drcocnlXzuNuSuS3PzOzvWR1sXFtLPuW_qD-ZfuVWQJBAOzRcIrxeG2WHH3qXAySHVHJFHZmL7nF_QrQZgY-vSNqy10i58RUsTw71CuVxeViYYipa-yINTnGSDVfKHmVrMsCQQC8UKSY2tzQu5rDPaaydGLjW-tEkjJFrqLZ-1zrklovAx37r7oJFUEbKPXLi8GjSsDDbEhqtaMPWEWQb2oHsq_fAkAxI9iFjEuf-bkZy6qkFSGuWdo8I0Id-15hgxLYkuYuVjeysM6E58oLJKMHFEIHMh3o_LbfUvgwb9uF76P3br53AkBjroBiVUAb0aLLhJ_vyMHrveVcp9KqphWYM4FtRUwtpIXDi7J7Sl_rQ5RQiYsXp-M_ztrKeZl17vDr0r4akxCbAkBuzdeqypdESccOTrZJI5qJ-E0ZEyLzUeog9tqh3Y0dSyEmBYT0Jbh09_-_Ipzc5qcjVl0l1msUg50CXiDG7_rb\",\"PUBLIC_KEY\":\"MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCuNGhcx6paN8IrX5sT-NRnWlZg1XoDmbyek7tdWsshALYPa5iMwgAhU_W23Zj4zMkpRwSLxLtWST-c2wyL2W4LeEdATvpXMDtxLfxBQeaYMA6s6pZUS-DYpDEQv4gBBEKtsugTVPXJObJyBrEXUspD6IC6Yjdtg9HPF8I47KZJ1QIDAQAB\"}", RSA.RSAInfo.class);
        System.out.println(rsaInfo);
    }

    @Test
    public void javaDecJs(){
        String enc = "135hHAi00gmfJabIQcQxDQAaY4KbU8EpN5/NL6/x7Co=";
        System.out.println(AES.decrypt(enc, "fmS9IMAA4XawecZl"));


    }

    @Test
    public void jsDecJava(){
        String src = "ujhael;䖛个攻克809972()&地级市了竟然花了几ledjhldj;rhjerol;yhjerl;yhjer";
//        String src = "haohaiyo嗨个攻克809972()&地级市了竟然花了几ledjhldj;rhjerol;yhjerl;yhjer";
        System.out.println(AES.encrypt(src, KEY));


    }

    @Test
    public void fun1() throws UnsupportedEncodingException {
        String s = Base64.encodeBase64URLSafeString(null);
        System.out.println(s);
    }

}