package com.jfai.afs.http.demo;

import com.alibaba.fastjson.JSON;
import com.jfai.afs.http.utils.JfCipher;
import com.jfai.afs.http.utils.RSA;
import com.jfai.afs.http.demo.vo.*;

import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.util.HashMap;
import java.util.UUID;

/**
 * 加密通信演示
 *
 * @author Nisus Liu
 * @version 0.0.1
 * @email liuhejun108@163.com
 * @date 2018/10/28 13:28
 */
public class DemoJfCipher {
    public static final String DELIMITER = "------------------------------------------------";

    public static void main(String[] args) throws InvalidKeyException, InvalidKeySpecException, NoSuchPaddingException, SignatureException, IOException {
        /*
        * 服务端: 甄姬
        * 客户端: 兰陵王
        * 客户端事先申请 appKey和appSecret对.*/
        String appKey = "LLW-897557";
        String appSecret = appKey + System.currentTimeMillis();
        System.out.println("兰陵王申请的 appKey:" + appKey + ", appSecret:" + appSecret);

        System.out.println(DELIMITER);

        //---- 1. 兰陵王 和 甄姬 各自自行生成RSA密钥对 ----//
        RSA.RSAInfo llwRsa = RSA.generateKeyPair();
        RSA.RSAInfo zjRsa = RSA.generateKeyPair();
        //兰陵王  甄姬  交换RSA公钥钥
        //兰陵王持有甄姬的公钥和自己的私钥, 甄姬持有兰陵王的公钥和自己的私钥
        System.out.println("--兰陵王RSA:--");
        System.out.println(llwRsa);
        System.out.println("--甄姬RSA:--");
        System.out.println(zjRsa);

        System.out.println(DELIMITER);


        //---- 2. 兰陵王 --> 甄姬 ----//
        DemoRequestVo reqVo = new DemoRequestVo();
        reqVo.setAppKey(appKey);
        reqVo.setData(new String[]{"1)集合", "2)团战", "3)别浪"});
        reqVo.setTimestamp(System.currentTimeMillis());
        reqVo.setPath("/v1/timi/paiwei");
        System.out.println("兰陵王 加密前的数据包: " + reqVo);
        //兰陵王 加密并签名数据包
        long t0 = System.currentTimeMillis();
        JfCipher.encryptAndSign(reqVo, zjRsa.PUBLIC_KEY, llwRsa.PRIVATE_KEY, appSecret);
        long t1 = System.currentTimeMillis();
        System.out.println("兰陵王 加密后的数据包: " + reqVo+"\n加密耗时: "+(t1-t0));
        System.out.println("兰陵王 成功发送数据包!");
        System.out.println(DELIMITER);

        //---- 3. 甄姬 接收并解密数据 ----//
        System.out.println("甄姬 成功接收数据包");
        //验签
        long t2 = System.currentTimeMillis();
        boolean isSignPass = JfCipher.checkSign(reqVo, llwRsa.PUBLIC_KEY, appSecret);
        long t3 = System.currentTimeMillis();
        System.out.println("甄姬 验签数据包? " + isSignPass);
        System.out.println("验签耗时: "+(t3-t2));
        //解密数据包
        long t4 = System.currentTimeMillis();
        JfCipher.decrypt(reqVo, zjRsa.PRIVATE_KEY);
        long t5 = System.currentTimeMillis();
        System.out.println("甄姬 解密数据包得到: " + reqVo);
        System.out.println("解密耗时: "+(t5-t4));

        System.out.println(DELIMITER);

        //---- 4. 甄姬 处理并响应数据 ----//
        HashMap<String, Object> data = new HashMap<>();
        data.put("kill", 13);
        data.put("die", 3);
        data.put("assist", 7);
        DemoResponseVo respVo = new DemoResponseVo();
        respVo.setRequestId(UUID.randomUUID().
                toString()).
                setCode("0").
                setMessage("SUCCESS").
                setData(JSON.toJSONString(data));
        System.out.println("甄姬 准备响应的明文数据包: "+respVo);

        //加密数据
        JfCipher.encryptAndSign(respVo,llwRsa.PUBLIC_KEY,zjRsa.PRIVATE_KEY,appSecret);
        System.out.println("甄姬 加密后的密文响应数据包: "+respVo);
        System.out.println("甄姬 响应数据包成功");
        System.out.println(DELIMITER);

        //---- 5. 兰陵王 接收响应数据----//
        //验签, 解密
        boolean zjCheckSign = JfCipher.checkSign(respVo, zjRsa.PUBLIC_KEY, appSecret);
        System.out.println("兰陵王 验签通过? "+zjCheckSign);
        JfCipher.decrypt(respVo,llwRsa.PRIVATE_KEY);
        System.out.println("兰陵王 解密响应数据包: "+respVo);


    }

}
