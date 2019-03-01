package com.jfai.afs.http.client;

import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.junit.Test;

import java.io.IOException;

/**
 * @author Nisus Liu
 * @version 0.0.1
 * @email liuhejun108@163.com
 * @date 2019/2/28 18:31
 */
public class HttpClientTest {

    // java.net.SocketTimeoutException: Read timed out

    @Test
    public void fun() throws IOException {
        CloseableHttpClient httpclient = HttpClients.createDefault();

        HttpGet httpGet = new HttpGet("http://localhost:18085/manage/test/time_out");
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectTimeout(3000).setConnectionRequestTimeout(1000)
                .setSocketTimeout(3000)
                .build();
        httpGet.setConfig(requestConfig);
        CloseableHttpResponse response = null;
        try {
            response = httpclient.execute(httpGet);
        } catch (IOException e) {
            System.out.println("异常咯");
            e.printStackTrace();
        }
        System.out.println("得到的结果:" + response.getStatusLine());//得到请求结果
        HttpEntity entity = response.getEntity();//得到请求回来的数据
        String s = EntityUtils.toString(response.getEntity(), "UTF-8");
        System.out.println(s);
    }
    
    
}
