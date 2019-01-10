package com.jfai.afs.http;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.jfai.afs.http.constant.HttpConstant;
import com.jfai.afs.http.utils.HttpUtils;
import com.jfai.afs.http.utils.JfCipher;
import org.apache.http.HttpResponse;
import org.junit.Test;

import java.security.InvalidKeyException;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Nisus Liu
 * @version 0.0.1
 * @email liuhejun108@163.com
 * @date 2019/1/8 15:25
 */
public class SDKdemo {

    @Test
    public void sdkdemo() throws Exception {

        // 准备接口级别参数
        Map<String, String> data = new HashMap<>();
        data.put("name", "libai");
        data.put("say", "今朝有酒今朝醉");


        // 准备请求体
        Map<String, Object> body = new HashMap<>();
        body.put(HttpConstant.APP_KEY, "AK12345678");
        body.put(HttpConstant.DATA, data);
        body.put(HttpConstant.TIMESTAMP, System.currentTimeMillis());

        // 调用加密签名api
        // 1st: 请求数据; 2nd: 服务方公钥; 3rd: 请求方私钥; 4th: appSecret
        JfCipher.encryptAndSign(body,"peerPubKey","selfPrvKey","appSecret");

        // 发送请求
        // POST Json
        HttpResponse resp = HttpUtils.doPost("host", "path", null, null, JSON.toJSONString(body));

        // 若成功响应
        if (HttpUtils.is200(resp)) {
            JSONObject je = HttpUtils.getJsonEntity(resp);
            // 也可以指定类型获取响应体
            // HttpUtils.getJsonEntity(resp, Map.class);
            // 复杂类型, 可以:
            // List<Map<String, Object>> je = HttpUtils.getJsonEntity(resp, new TypeReference<List<Map<String, Object>>>() {});
            // 验签解密
            boolean b = JfCipher.checkSign(je, "peerPubKey", "appSecret");
            // 验签通过后, 解密数据, 使用
            if (b) {
                JfCipher.decrypt(je,"selfPrvKey");
                if (je != null && Integer.valueOf(String.valueOf(je.get(HttpConstant.CODE))) == 0) {
                    // 服务器得到请求方预期的结果, 可以正常使用了
                    Object resData = je.get(HttpConstant.DATA);
                    // ...
                }
            }
        }



    }


}
