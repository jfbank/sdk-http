package com.jfai.afs.http.demo;

import com.alibaba.fastjson.TypeReference;
import com.jfai.afs.http.bean.JfResBody;
import com.jfai.afs.http.bean.JfResponse;
import com.jfai.afs.http.client.JfClientManager;
import com.jfai.afs.http.client.JfHttpClient;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Nisus Liu
 * @version 0.0.1
 * @email liuhejun108@163.com
 * @date 2019/1/27 16:30
 */
public class DemoJfClientManager {
    // 密钥格式: 有无首尾标记均可, 中间额空白字符(\n\t 等)不影响
    private static final  String clientPubkey = "-----BEGIN PUBLIC KEY-----\n"+
            "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDGsx_3zcCH6li-8zs8CqnEtYaiLjX-VgiFx7tbDldIdJ4rW4OW9Vv6L9MxrjY-O_J8pbz2CmNsPIsw7ey8FyOLFX0-kXunaSaLU1gDTUE9W1N8PViuDFIcTonv_ui0tomg8Vg_hca6bE_1GuDxFS3k50X8DNeIrpsVRuQ5u7oz4wIDAQAB"+
            "-----END PUBLIC KEY-----";
    private static final String clientPrvkey = "-----BEGIN PRIVATE KEY-----\n" +
            "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAMazH_fNwIfqWL7zOzwKqcS1hqIuNf5WCIXHu1sOV0h0nitbg5b1W_ov0zGuNj478nylvPYKY2w8izDt7LwXI4sVfT6Re6dpJotTWANNQT1bU3w9WK4MUhxOie_-6LS2iaDxWD-FxrpsT_Ua4PEVLeTnRfwM14iumxVG5Dm7ujPjAgMBAAECgYBfOAvDxrfS6jypFQp31WxRtePU6Gw7e6MN6Q8hrZeqQyhhArPmraHsHOsKTb_0xumHTi1lgsjuX30cb4gZAsWm0rCNsraEFO6OfBYPHrA3Z5EtW1i3KYgzHAYJiSiArA1kU5ueu29li-WQb88Z5CJoD8rp0lN0OaUowIwS5rI60QJBAO0aFclzKph4Q5YiW5isYG0lEMxMMEXkg1atoajHy-sX06cdZEYr0N0OGRzKnQwCwyzgz9P42ASvl7L4tZRjyKcCQQDWiXegdwd1BbMaV44BohE_TGSGZ5YQYcoJiiDLIdLc-CYQ3IbXWU13wZJCykuY5_74JD4YlI6-zv2F7PSR-uZlAkBo2wtdjr_8s9r373PgngL64dR-9Qa4MinfUGRY1xsYee4RTs9EtSXmTNDQSc6QPDyCgV2H2dn2oI0PCiyLVmGrAkBe3rlk314P1K5oBrHIbRe9axXFDcehhOzoHQn1agaqKp4CtNJ4JoiIXbRFDtoxSt5IcxZ2njMlk7ku4SMh7ta1AkEAoh-GjGNFVm_-I8uZbZrc60M-m5UFDVSjQrxKULtpVRyYjwAvJaNPmMHZB_b5DdaLr0UdExd_CNYRWjiLWmF8JQ\n" +
            "-----END PRIVATE KEY-----";
    private static final String serverPubkey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCyxQxnDmbP/FqGd4yf+fGmHINts1uA57H/uYibfvaJaoP2RxA0aLGSp3jZbsu4Ri5YD1msLY+sM4za9qzOs91aEsvEY8+7ABBVcJjfPhAZ71+qXQGPXGRIxY4rkrndW9Jvyw9DBg8W5CPvLVxeid2yVUdzkIP301CCCTcyCMzQcwIDAQAB";


    public void 演示加密压缩请求(){

        // 构建客户端管理者实例
        JfClientManager m = buildJfClientManager();

        // 从管理者里获取新的玖富http客户端
        JfHttpClient client = m.getJfHttpClient();
        // 指定加密,压缩
        client.config().setEncryption(true).setZip(true);
        // 或, 使用快捷方式 m.getJfHttpClientEncZip(), 等价于上面的两项配置

        // 准备接口级参数
        HashMap<String, Object> data = new HashMap<>();
        data.put("name", "李白");
        data.put("type", "刺客");

        try {
            // url: 由于管理者配置了默认url, 这里使用接口的path即可(开头有无/无关紧要)
            // headers: 请求头, 默认配有Content-Type:application/json;charset=utf-8, 没有其它头, 可null
            // data: 接口级参数, 原样传入即可, 内部都会转成json串, 然后选择是否加密或者压缩.
            JfResponse res = client.doGet("test/encryption", null, data);
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


    }

    @NotNull
    public static JfClientManager buildJfClientManager() {
        // 构建玖富客户端管理者实例
        JfClientManager m = new JfClientManager();
        // 调用config()方法设置配置项, 全局配置, 只要通过这个管理者获取的客户端都会自动继承这里的配置项
        m.config()
                .setClientPubkey(clientPubkey)  // 调用者的公钥
                .setClientPrvkey(clientPrvkey)  // 调用者的私钥
                .setServerPubkey(serverPubkey)  // 玖富的公钥
                // 默认的请求URL, 有了这项配置, 以后只要是相同URL的客户端, 可以只用传入相对路径
                .setDefaultHost("http://afs.9fbank.com")
                // 事先申请的 appKey 和 appSecret
                .setAppKey("AK531894660444999680").setAppSecret("b289cd954ae02d176d2b0c8f6d2f4c89");
        return m;
    }


}
