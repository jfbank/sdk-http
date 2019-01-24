package com.jfai.afs.http.client;

import com.jfai.afs.http.bean.HttpVo;
import com.jfai.afs.http.bean.JfResBody;
import com.jfai.afs.http.bean.JfResponse;
import com.jfai.afs.http.exception.JfConfigException;
import com.jfai.afs.http.utils.RSA;
import org.jetbrains.annotations.NotNull;
import org.junit.Test;

import java.security.InvalidKeyException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;

public class JfClientManagerTest {
    private static final  String clientPubkey = "-----BEGIN PUBLIC KEY-----\n"+
            "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDGsx_3zcCH6li-8zs8CqnEtYaiLjX-VgiFx7tbDldIdJ4rW4OW9Vv6L9MxrjY-O_J8pbz2CmNsPIsw7ey8FyOLFX0-kXunaSaLU1gDTUE9W1N8PViuDFIcTonv_ui0tomg8Vg_hca6bE_1GuDxFS3k50X8DNeIrpsVRuQ5u7oz4wIDAQAB\n"+
            "-----END PUBLIC KEY-----";
    private static final String clientPrvkey = "-----BEGIN PRIVATE KEY-----\n" +
            "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAMazH_fNwIfqWL7zOzwKqcS1hqIuNf5WCIXHu1sOV0h0nitbg5b1W_ov0zGuNj478nylvPYKY2w8izDt7LwXI4sVfT6Re6dpJotTWANNQT1bU3w9WK4MUhxOie_-6LS2iaDxWD-FxrpsT_Ua4PEVLeTnRfwM14iumxVG5Dm7ujPjAgMBAAECgYBfOAvDxrfS6jypFQp31WxRtePU6Gw7e6MN6Q8hrZeqQyhhArPmraHsHOsKTb_0xumHTi1lgsjuX30cb4gZAsWm0rCNsraEFO6OfBYPHrA3Z5EtW1i3KYgzHAYJiSiArA1kU5ueu29li-WQb88Z5CJoD8rp0lN0OaUowIwS5rI60QJBAO0aFclzKph4Q5YiW5isYG0lEMxMMEXkg1atoajHy-sX06cdZEYr0N0OGRzKnQwCwyzgz9P42ASvl7L4tZRjyKcCQQDWiXegdwd1BbMaV44BohE_TGSGZ5YQYcoJiiDLIdLc-CYQ3IbXWU13wZJCykuY5_74JD4YlI6-zv2F7PSR-uZlAkBo2wtdjr_8s9r373PgngL64dR-9Qa4MinfUGRY1xsYee4RTs9EtSXmTNDQSc6QPDyCgV2H2dn2oI0PCiyLVmGrAkBe3rlk314P1K5oBrHIbRe9axXFDcehhOzoHQn1agaqKp4CtNJ4JoiIXbRFDtoxSt5IcxZ2njMlk7ku4SMh7ta1AkEAoh-GjGNFVm_-I8uZbZrc60M-m5UFDVSjQrxKULtpVRyYjwAvJaNPmMHZB_b5DdaLr0UdExd_CNYRWjiLWmF8JQ\n" +
            "-----END PRIVATE KEY-----";
    private static final String serverPubkey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCyxQxnDmbP/FqGd4yf+fGmHINts1uA57H/uYibfvaJaoP2RxA0aLGSp3jZbsu4Ri5YD1msLY+sM4za9qzOs91aEsvEY8+7ABBVcJjfPhAZ71+qXQGPXGRIxY4rkrndW9Jvyw9DBg8W5CPvLVxeid2yVUdzkIP301CCCTcyCMzQcwIDAQAB";

    @Test
    public void fun2(){
        RSA.validateRsaKeyPair(clientPubkey, clientPrvkey);
    }

    @Test
    public void fun4(){
        // post form
        JfClientManager m = getJfClientManager();
        JfHttpClient c = m.getJfHttpClientEnc();
        try {
            JfResponse res = c.doPostForm("test/form",null, "下载的数据");
            if (res.is2xx()) {
                System.out.println(res.getBody());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void fun3(){
        // 只压缩, 不加密
        JfClientManager m = getJfClientManager();

        JfHttpClient c = m.getJfHttpClientZip();
        m.validateConf();

        try {
            JfResponse res = c.doGet("test/encryption", null, "请求data");
            if (res.is2xx()) {
                JfResBody body = (JfResBody) res.getBody();
                if (body.getCode()==0) {
                    System.out.println("请求成功");
                }else{
                    System.out.println("请求失败");
                }
                System.out.println(body.toString(true));
            }else{
                System.out.println(res);
            }

        } catch (Exception e) {
            System.out.println("异常咯...");
            e.printStackTrace();
        }

    }

    @Test
    public void fun1(){

        JfClientManager m = getJfClientManager();

        JfHttpClient client = m.getJfHttpClient();
        client.config().setEncryption(true).setZip(true);
        try {
            JfResponse res = client.doGet("test/encryption", null, "请求data");
            if (res.is2xx()) {
                JfResBody body = (JfResBody) res.getBody();
                if (body.getCode()==0) {
                    System.out.println("请求成功");
                }else{
                    System.out.println("请求失败");
                }
                System.out.println(body.toString(true));
            }else{
                System.out.println(res);
            }

        } catch (Exception e) {
            System.out.println("异常咯...");
            e.printStackTrace();
        }


    }

    @NotNull
    private JfClientManager getJfClientManager() {
        JfClientManager m = new JfClientManager();
        m.config().setClientPubkey(clientPubkey).setClientPrvkey(clientPrvkey).setServerPubkey(serverPubkey)
                .setDefaultHost("http://localhost:18081")
                .setAppKey("AK531894660444999680").setAppSecret("b289cd954ae02d176d2b0c8f6d2f4c89");
        return m;
    }


    @Test
    public void config() {
        final Config c = new Config();
        ArrayList<Thread> threads = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            final int finalI = i;
            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    c.setAppKey(finalI + "");
                }
            });
            threads.add(t);
        }

        for (Thread t : threads) {
            t.start();
            try {
                t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        System.out.println(c);
        System.out.println("done...");

    }

    @Test
    public void getJfHttpClient() {
        JfClientManager jfClientManager = new JfClientManager();
        jfClientManager.config().setAppKey("AK007")
                .setAppSecret("AS")
                .setDefaultHost("http://localhost")
                .setServerPubkey("server pubkey")
                .setClientPubkey("client pubkey")
                .setClientPrvkey("client privkey");
        JfHttpClient c = jfClientManager.getJfHttpClient();

        System.out.println(c);

        JfHttpClient enc = jfClientManager.getJfHttpClientEnc();
        System.out.println(enc);

        JfHttpClient enczip = jfClientManager.getJfHttpClientEncZip();
        System.out.println(enczip);

    }

    @Test
    public void getJfHttpClientEnc() {
        String src = "Hi~ 我是测试客户端RSA密钥对是否有效的原文^~^";
        System.out.println("test source: "+src);
        String enc = null;
        try {
            enc = RSA.encrypt(src, clientPubkey);
        } catch (InvalidKeySpecException | InvalidKeyException e) {
            e.printStackTrace();
        }

        System.out.println("test rsa encrypt: "+enc);
        String dec = null;
        try {
            dec = RSA.decrypt(enc, clientPrvkey);
        } catch (InvalidKeySpecException | InvalidKeyException e) {
            e.printStackTrace();
        }
        System.out.println("test rsa decrypt: "+ dec);

        System.out.println("------");
        String sign = null;
        try {
            sign = RSA.sign(src, clientPrvkey);
        } catch (Exception e) {
            throw new JfConfigException("'client prvkey' 签名异常", e);
        }
        System.out.println(sign);
        boolean b = false;
        try {
            b = RSA.checkSign(src, sign, clientPubkey);
        } catch (Exception e) {
            throw new JfConfigException("'client pubkey' 验签异常", e);
        }
        if (!b) {
            throw new JfConfigException("'client pubkey' 验签失败");
        }

    }

    @Test
    public void getJfHttpClientZip() {
    }

    @Test
    public void getJfHttpClientEncZip() {
    }

    @Test
    public void getJfHttpClient1() {
    }

    @Test
    public void getJfHttpClient2() {
    }
}