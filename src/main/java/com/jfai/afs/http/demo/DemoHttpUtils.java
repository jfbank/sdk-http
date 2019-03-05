package com.jfai.afs.http.demo;

import com.alibaba.fastjson.JSON;
import com.jfai.afs.http.bean.JfReqParam;
import com.jfai.afs.http.client.JfHttpClient;
import com.jfai.afs.http.constant.HttpConst;
import com.jfai.afs.http.utils.AES;
import com.jfai.afs.http.utils.HttpUtils;
import com.jfai.afs.http.utils.JfCipher;
import com.jfai.afs.http.utils.RSA;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.http.HttpResponse;

import java.security.InvalidKeyException;
import java.security.spec.InvalidKeySpecException;
import java.util.HashMap;
import java.util.TreeMap;

/**
 * 不使用 {@link JfHttpClient} 调用接口的代码参考
 *
 * @author Nisus Liu
 * @version 0.0.1
 * @email liuhejun108@163.com
 * @date 2019/3/4 21:51
 */
public class DemoHttpUtils {
    private static final String serverPubkey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCyxQxnDmbP/FqGd4yf+fGmHINts1uA57H/uYibfvaJaoP2RxA0aLGSp3jZbsu4Ri5YD1msLY+sM4za9qzOs91aEsvEY8+7ABBVcJjfPhAZ71+qXQGPXGRIxY4rkrndW9Jvyw9DBg8W5CPvLVxeid2yVUdzkIP301CCCTcyCMzQcwIDAQAB";


    public static void main(String[] args) throws Exception {

        // 准备请求参数
        HashMap<String, String> param = new HashMap<>();
        param.put(HttpConst.APP_KEY,"AK531894660444999680");
        param.put(HttpConst.TIMESTAMP, String.valueOf(System.currentTimeMillis()));
        // 接口参数
        HashMap<String, Object> data = new HashMap<>();
        data.put("name", "李白");
        data.put("type", "刺客");
        param.put(HttpConst.DATA, JSON.toJSONString(data));

        // 加密数据

        // 1) 生成AES密钥, 对data加密
        String sessionKey = AES.genAesKey();
        String encData = AES.encrypt(param.get(HttpConst.DATA), sessionKey);
        System.out.println("加密后数据: " + encData);
        // 替换掉明文数据
        param.put(HttpConst.DATA,encData);

        // 2) RSA加密服务端公钥
        String encSessionKey = RSA.encrypt(sessionKey, serverPubkey);
        param.put(HttpConst.SESSION_KEY,encSessionKey);

        // 3) 设置加密标识字段
        param.put(HttpConst.ENCRYPTION, String.valueOf(true));

        System.out.println("请求参数: " + param);

        // 签名: 用于身份校验
        HashMap<String, Object> signMap = new HashMap<String, Object>(param);
        String s = JfCipher.buildSignSource(signMap, "b289cd954ae02d176d2b0c8f6d2f4c89");
        String sign = DigestUtils.sha1Hex(s);
        param.put(HttpConst.SIGN, sign);


        HttpResponse resp = HttpUtils.doGet("http://localhost:18081/api/test/encryption", null, param);

        if (HttpUtils.is200(resp)) {
            System.out.println(HttpUtils.getJsonEntity(resp));
        }


    }


}
