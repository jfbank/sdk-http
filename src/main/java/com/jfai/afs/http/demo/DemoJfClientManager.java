package com.jfai.afs.http.demo;

import com.alibaba.fastjson.TypeReference;
import com.jfai.afs.http.bean.JfResBody;
import com.jfai.afs.http.bean.JfResponse;
import com.jfai.afs.http.client.JfClientManager;
import com.jfai.afs.http.client.JfHttpClient;
import org.apache.http.client.config.RequestConfig;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

/**
 * 借助{@link JfClientManager}快速调用调用接口的代码参考
 * @author Nisus Liu
 * @version 0.0.1
 * @email liuhejun108@163.com
 * @date 2019/1/27 16:30
 */
public class DemoJfClientManager {
    private static final String serverPubkey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCyxQxnDmbP/FqGd4yf+fGmHINts1uA57H/uYibfvaJaoP2RxA0aLGSp3jZbsu4Ri5YD1msLY+sM4za9qzOs91aEsvEY8+7ABBVcJjfPhAZ71+qXQGPXGRIxY4rkrndW9Jvyw9DBg8W5CPvLVxeid2yVUdzkIP301CCCTcyCMzQcwIDAQAB";

    public static void main(String[] args) {
        // 构建客户端管理者实例
        JfClientManager m = buildJfClientManager(); // 下文封装的方法

        // 从管理者里获取新的玖富http客户端
        JfHttpClient client = m.getJfHttpClient();
        // 指定加密,压缩
        client.config().setEncryption(true).setZip(true);
        // 或, 使用快捷方式 m.getJfHttpClientEncZip(), 等价于上面的两项配置

        // 设置RequestConfig, 如使用缺省值, 请跳过, 详细说明见下文
        // client.setRequestConfig(RequestConfig实例);

        // 准备接口级参数
        HashMap<String, Object> data = new HashMap<>();
        data.put("name", "李白");
        data.put("type", "刺客");

        try {
            // url: 由于管理者配置了默认url, 这里使用接口的path即可(开头有无/无关紧要)
            // headers: 请求头, 默认配有Content-Type:application/json;charset=utf-8, 没有其它头, 可null
            // data: 接口级参数, 原样传入即可, 内部都会转成json串, 然后选择是否加密或者压缩.
            //JfResponse res = client.doGet("api/test/encryption", null, data);
            JfResponse res = client.doGet("http://localhost:18081/api/test/encryption", null, data);
            // 请求是否成功响应
            if (res.is2xx()) {
                // 拿出响应体
                JfResBody body = res.getBody();
                if (body.getCode()==0) {
                    System.out.println("请求成功");
                    // 处理响应的业务数据
                    // 方式1: 获取data字段的原始内容, json串或普通字符串, 随具体的接口决定
                    String data0 = body.getData();
                    // 方式2: 可指定类型, 自动将data的json串转成java object
                    Map data1 = body.getData(Map.class);
                    // 方式3: 同上, 但适用于更复杂的对象
                    Map<String, Object> data2 = body.getData(new TypeReference<Map<String, Object>>() {
                    });
                }else{
                    System.out.println("请求失败");
                }
                // 以格式化的方式展示响应体
                System.out.println(body.toString(true));
            }else{
                System.out.println(res);
            }

        } catch (Exception e) {
            // 如连接失败会进入异常
            System.out.println("异常咯...");
            e.printStackTrace();
        }


        // 关于 RequestConfig 设置说明
        // 默认设置了两个主要参数: 连接超时时间3秒, 获取数据超时时间3秒
        // 开发者可根据需要自定义RequestConfig
        // 示例:
        client.setRequestConfig(RequestConfig.custom()
                // 设置从connect Manager获取Connection 超时时间，单位毫秒(针对有连接池的客户端)
                .setConnectionRequestTimeout(1000)
                // 设置连接超时时间, 单位ms
                .setConnectTimeout(3000)
                // 请求获取数据的超时时间, 单位ms, 如果访问一个接口，多少时间内无法返回数据，就直接放弃此次调用。
                .setSocketTimeout(3000)
                // ...
                .build()
        );

    }

    @NotNull
    public static JfClientManager buildJfClientManager() {
        // 构建玖富客户端管理者实例
        JfClientManager m = new JfClientManager();
        // 调用config()方法设置配置项, 全局配置, 只要通过这个管理者获取的客户端都会自动继承这里的配置项
        m.config()
                .setServerPubkey(serverPubkey)  // 玖富的公钥
                // 默认的请求URL, 有了这项配置, 以后只要是相同URL的客户端, 可以只用传入相对路径
                .setDefaultHost("http://afs.9fbank.com")
                // 事先申请的 appKey 和 appSecret
                .setAppKey("AK531894660444999680")
                .setAppSecret("b289cd954ae02d176d2b0c8f6d2f4c89");
        return m;
    }


}
