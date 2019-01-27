package com.jfai.afs.http.client;

import com.alibaba.fastjson.JSON;
import com.jfai.afs.http.bean.HttpVo;
import com.jfai.afs.http.bean.JfResBody;
import com.jfai.afs.http.bean.JfResponse;
import com.jfai.afs.http.utils.HttpUtils;
import org.apache.http.HttpResponse;
import org.junit.Test;

import java.io.IOException;
import java.util.*;

/**
 * @author Nisus Liu
 * @version 0.0.1
 * @email liuhejun108@163.com
 * @date 2019/1/14 10:32
 */
public class SdkTest {

    @Test
    public void post(){
        JfPoolHttpClient c = getJfPooledHttpClient();
        // 准备请求数据
        HashMap<String, Object> data = new HashMap<>();
        data.put("name", "libai");
        data.put("skill", "青莲剑歌");
        data.put("word", "十步杀一人");

        try {
            JfResponse res = c.doPost("http://localhost:18081/test/post", null, data);
            if (res.is2xx()) {
                HttpVo body = res.getBody();
                System.out.println(body);
                Object d = body.getData();
            }else {
                System.out.println(res.getStatusCode());
                System.out.println(res.getBody());
            }
        } catch (Exception e) {
            System.out.println("异常咯...");
            e.printStackTrace();
        }


    }



    @Test
    public void zip(){
        //JfHttpClient c = new JfHttpClient();
        JfPoolHttpClient c = getJfPooledHttpClient();
        // 准备请求数据
        HashMap<String, Object> data = new HashMap<>();
        data.put("name", "libai");
        data.put("skill", "青莲剑歌");
        data.put("word", "十步杀一人");

        try {
            JfResponse res = c.doGet("http://localhost:18081/test/encryption", null, data);
            if (res.is2xx()) {
                HttpVo body = res.getBody();
                System.out.println(body);
            }else {
                System.out.println(res.getStatusCode());
                System.out.println(res.getBody());
            }
        } catch (Exception e) {
            System.out.println("异常咯...");
            e.printStackTrace();
        }

    }


    @Test
    public void poolClient(){
        //JfHttpClient c = new JfHttpClient();
        JfPoolHttpClient c = new JfPoolHttpClient();
        c.config().setEncryption(true)
                .setAppKey("AK531894660444999680")
                .setAppSecret("b289cd954ae02d176d2b0c8f6d2f4c89")
                .setClientPubkey("-----BEGIN PUBLIC KEY-----\n" +
                        "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDGsx_3zcCH6li-8zs8CqnEtYaiLjX-VgiFx7tbDldIdJ4rW4OW9Vv6L9MxrjY-O_J8pbz2CmNsPIsw7ey8FyOLFX0-kXunaSaLU1gDTUE9W1N8PViuDFIcTonv_ui0tomg8Vg_hca6bE_1GuDxFS3k50X8DNeIrpsVRuQ5u7oz4wIDAQAB\n" +
                        "-----END PUBLIC KEY-----")
                .setClientPrvkey("-----BEGIN PRIVATE KEY-----\n" +
                        "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAMazH_fNwIfqWL7zOzwKqcS1hqIuNf5WCIXHu1sOV0h0nitbg5b1W_ov0zGuNj478nylvPYKY2w8izDt7LwXI4sVfT6Re6dpJotTWANNQT1bU3w9WK4MUhxOie_-6LS2iaDxWD-FxrpsT_Ua4PEVLeTnRfwM14iumxVG5Dm7ujPjAgMBAAECgYBfOAvDxrfS6jypFQp31WxRtePU6Gw7e6MN6Q8hrZeqQyhhArPmraHsHOsKTb_0xumHTi1lgsjuX30cb4gZAsWm0rCNsraEFO6OfBYPHrA3Z5EtW1i3KYgzHAYJiSiArA1kU5ueu29li-WQb88Z5CJoD8rp0lN0OaUowIwS5rI60QJBAO0aFclzKph4Q5YiW5isYG0lEMxMMEXkg1atoajHy-sX06cdZEYr0N0OGRzKnQwCwyzgz9P42ASvl7L4tZRjyKcCQQDWiXegdwd1BbMaV44BohE_TGSGZ5YQYcoJiiDLIdLc-CYQ3IbXWU13wZJCykuY5_74JD4YlI6-zv2F7PSR-uZlAkBo2wtdjr_8s9r373PgngL64dR-9Qa4MinfUGRY1xsYee4RTs9EtSXmTNDQSc6QPDyCgV2H2dn2oI0PCiyLVmGrAkBe3rlk314P1K5oBrHIbRe9axXFDcehhOzoHQn1agaqKp4CtNJ4JoiIXbRFDtoxSt5IcxZ2njMlk7ku4SMh7ta1AkEAoh-GjGNFVm_-I8uZbZrc60M-m5UFDVSjQrxKULtpVRyYjwAvJaNPmMHZB_b5DdaLr0UdExd_CNYRWjiLWmF8JQ\n" +
                        "-----END PRIVATE KEY-----")
                .setServerPubkey("MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCyxQxnDmbP/FqGd4yf+fGmHINts1uA57H/uYibfvaJaoP2RxA0aLGSp3jZbsu4Ri5YD1msLY+sM4za9qzOs91aEsvEY8+7ABBVcJjfPhAZ71+qXQGPXGRIxY4rkrndW9Jvyw9DBg8W5CPvLVxeid2yVUdzkIP301CCCTcyCMzQcwIDAQAB");
        try {
            JfResponse res = c.doGet("http://localhost:18081/test/encryption", null, null);
            if (res.is2xx()) {
                HttpVo body = res.getBody();
                System.out.println(body);
            }else {
                System.out.println(res.getStatusCode());
                System.out.println(res.getBody());
            }
        } catch (Exception e) {
            System.out.println("异常咯...");
            e.printStackTrace();
        }



    }


    @Test
    public void JfHttpClientEnc(){
        JfHttpClient c = new JfHttpClient();
        c.config().setEncryption(true)
                .setAppKey("AK531894660444999680")
                .setAppSecret("b289cd954ae02d176d2b0c8f6d2f4c89")
                .setClientPubkey("-----BEGIN PUBLIC KEY-----\n" +
                        "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDGsx_3zcCH6li-8zs8CqnEtYaiLjX-VgiFx7tbDldIdJ4rW4OW9Vv6L9MxrjY-O_J8pbz2CmNsPIsw7ey8FyOLFX0-kXunaSaLU1gDTUE9W1N8PViuDFIcTonv_ui0tomg8Vg_hca6bE_1GuDxFS3k50X8DNeIrpsVRuQ5u7oz4wIDAQAB\n" +
                        "-----END PUBLIC KEY-----")
                .setClientPrvkey("-----BEGIN PRIVATE KEY-----\n" +
                        "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAMazH_fNwIfqWL7zOzwKqcS1hqIuNf5WCIXHu1sOV0h0nitbg5b1W_ov0zGuNj478nylvPYKY2w8izDt7LwXI4sVfT6Re6dpJotTWANNQT1bU3w9WK4MUhxOie_-6LS2iaDxWD-FxrpsT_Ua4PEVLeTnRfwM14iumxVG5Dm7ujPjAgMBAAECgYBfOAvDxrfS6jypFQp31WxRtePU6Gw7e6MN6Q8hrZeqQyhhArPmraHsHOsKTb_0xumHTi1lgsjuX30cb4gZAsWm0rCNsraEFO6OfBYPHrA3Z5EtW1i3KYgzHAYJiSiArA1kU5ueu29li-WQb88Z5CJoD8rp0lN0OaUowIwS5rI60QJBAO0aFclzKph4Q5YiW5isYG0lEMxMMEXkg1atoajHy-sX06cdZEYr0N0OGRzKnQwCwyzgz9P42ASvl7L4tZRjyKcCQQDWiXegdwd1BbMaV44BohE_TGSGZ5YQYcoJiiDLIdLc-CYQ3IbXWU13wZJCykuY5_74JD4YlI6-zv2F7PSR-uZlAkBo2wtdjr_8s9r373PgngL64dR-9Qa4MinfUGRY1xsYee4RTs9EtSXmTNDQSc6QPDyCgV2H2dn2oI0PCiyLVmGrAkBe3rlk314P1K5oBrHIbRe9axXFDcehhOzoHQn1agaqKp4CtNJ4JoiIXbRFDtoxSt5IcxZ2njMlk7ku4SMh7ta1AkEAoh-GjGNFVm_-I8uZbZrc60M-m5UFDVSjQrxKULtpVRyYjwAvJaNPmMHZB_b5DdaLr0UdExd_CNYRWjiLWmF8JQ\n" +
                        "-----END PRIVATE KEY-----")
                .setServerPubkey("MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCyxQxnDmbP/FqGd4yf+fGmHINts1uA57H/uYibfvaJaoP2RxA0aLGSp3jZbsu4Ri5YD1msLY+sM4za9qzOs91aEsvEY8+7ABBVcJjfPhAZ71+qXQGPXGRIxY4rkrndW9Jvyw9DBg8W5CPvLVxeid2yVUdzkIP301CCCTcyCMzQcwIDAQAB");
        try {
            JfResponse res = c.doGet("http://localhost:18081/test/encryption", null, null);
            if (res.is2xx()) {
                HttpVo body = res.getBody();
                System.out.println(body);
            }else {
                System.out.println(res.getStatusCode());
                System.out.println(res.getBody());
            }
        } catch (Exception e) {
            System.out.println("异常咯...");
            e.printStackTrace();
        }

    }

    /**
     * <pre>
     *
     *     host错误:
     *       拒绝访问, 进入异常.
     *     path错误:
     *       404
     *
     *
     * </pre>
     */
    @Test
    public void JfHttpClient(){

        JfHttpClient c = new JfHttpClient();
        c.config().setDefaultHost("http://localhost:18081/")
                .setEncryption(false);
        try {
            JfResponse res = c.doGet("test", null, null);
            if (res.is2xx()) {
                HttpVo body = res.getBody();
                System.out.println(body);
                System.out.println(body.getData());
            }else {
                System.out.println(res.getStatusCode());
                System.out.println(res.getBody());
            }
        } catch (Exception e) {
            System.out.println("异常咯...");
            e.printStackTrace();
        }


    }


    private JfPoolHttpClient getJfPooledHttpClient() {
        JfPoolHttpClient c = new JfPoolHttpClient();
        c.config().setEncryption(true)
                .setAppKey("AK531894660444999680")
                .setAppSecret("b289cd954ae02d176d2b0c8f6d2f4c89")
                .setClientPubkey("-----BEGIN PUBLIC KEY-----\n" +
                        "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDGsx_3zcCH6li-8zs8CqnEtYaiLjX-VgiFx7tbDldIdJ4rW4OW9Vv6L9MxrjY-O_J8pbz2CmNsPIsw7ey8FyOLFX0-kXunaSaLU1gDTUE9W1N8PViuDFIcTonv_ui0tomg8Vg_hca6bE_1GuDxFS3k50X8DNeIrpsVRuQ5u7oz4wIDAQAB\n" +
                        "-----END PUBLIC KEY-----")
                .setClientPrvkey("-----BEGIN PRIVATE KEY-----\n" +
                        "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAMazH_fNwIfqWL7zOzwKqcS1hqIuNf5WCIXHu1sOV0h0nitbg5b1W_ov0zGuNj478nylvPYKY2w8izDt7LwXI4sVfT6Re6dpJotTWANNQT1bU3w9WK4MUhxOie_-6LS2iaDxWD-FxrpsT_Ua4PEVLeTnRfwM14iumxVG5Dm7ujPjAgMBAAECgYBfOAvDxrfS6jypFQp31WxRtePU6Gw7e6MN6Q8hrZeqQyhhArPmraHsHOsKTb_0xumHTi1lgsjuX30cb4gZAsWm0rCNsraEFO6OfBYPHrA3Z5EtW1i3KYgzHAYJiSiArA1kU5ueu29li-WQb88Z5CJoD8rp0lN0OaUowIwS5rI60QJBAO0aFclzKph4Q5YiW5isYG0lEMxMMEXkg1atoajHy-sX06cdZEYr0N0OGRzKnQwCwyzgz9P42ASvl7L4tZRjyKcCQQDWiXegdwd1BbMaV44BohE_TGSGZ5YQYcoJiiDLIdLc-CYQ3IbXWU13wZJCykuY5_74JD4YlI6-zv2F7PSR-uZlAkBo2wtdjr_8s9r373PgngL64dR-9Qa4MinfUGRY1xsYee4RTs9EtSXmTNDQSc6QPDyCgV2H2dn2oI0PCiyLVmGrAkBe3rlk314P1K5oBrHIbRe9axXFDcehhOzoHQn1agaqKp4CtNJ4JoiIXbRFDtoxSt5IcxZ2njMlk7ku4SMh7ta1AkEAoh-GjGNFVm_-I8uZbZrc60M-m5UFDVSjQrxKULtpVRyYjwAvJaNPmMHZB_b5DdaLr0UdExd_CNYRWjiLWmF8JQ\n" +
                        "-----END PRIVATE KEY-----")
                .setServerPubkey("MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCyxQxnDmbP/FqGd4yf+fGmHINts1uA57H/uYibfvaJaoP2RxA0aLGSp3jZbsu4Ri5YD1msLY+sM4za9qzOs91aEsvEY8+7ABBVcJjfPhAZ71+qXQGPXGRIxY4rkrndW9Jvyw9DBg8W5CPvLVxeid2yVUdzkIP301CCCTcyCMzQcwIDAQAB")
                .setZip(true);
        return c;
    }



    @Test
    public void fun2(){

        JfResBody resp = new JfResBody();
        resp.setCode(89);
        resp.setMessage("xxxxx");
        resp.setCause("原因");
        HashMap<Object, Object> data = new HashMap<>();
        data.put("a", "aaaa");
        data.put("b", "bbbb");
        resp.setData(data);
        resp.setRequestId("1234556789");
        resp.setSpendTime(System.currentTimeMillis());

        String x = JSON.toJSONString(resp);
        System.out.println(x);

        HttpVo jvo = JSON.parseObject(x, HttpVo.class);
        System.out.println(jvo);

//        TreeMap<String, Object> tr = new TreeMap<>();
//        tr.put("xx", null);
//        tr.put("aa", "aa");
//        System.out.println(tr);



    }


    @Test
    public void fun1(){
        HttpResponse resp = null;
        try {
            resp = HttpUtils.doGet("http://localhost:18083/test",null , null);
        } catch (Exception e) {     // java.net.ConnectException: Connection refused: connect, resp==null
            e.printStackTrace();
            System.out.println("11111");
        }
        try {
            System.out.println(HttpUtils.getEntityString(resp));
        } catch (IOException e) {
            e.printStackTrace();
        }


    }


}
