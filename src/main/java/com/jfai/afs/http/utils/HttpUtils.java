package com.jfai.afs.http.utils;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.TypeReference;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author 玖富AI
 */
public class HttpUtils {
    private static final String CONTENT_TYPE = "Content-Type";
    private static final char URL_PATH_SEP = '/';

    /**
     * get
     * <p>
     * 若没有指定Content-Type, 则"Content type" 默认为null.
     * 这里将其改为默认值为: application/json; charset=UTF-8
     * </p>
     *
     * @param host
     * @param path
     * @param headers
     * @param querys
     * @return
     * @throws Exception
     */
    public static HttpResponse doGet(String host, String path,
                                     Map<String, String> headers,
                                     Map<String, String> querys)
            throws Exception {
        HttpClient httpClient = wrapClient(host);

        HttpGet request = new HttpGet(buildUrl(host, path, querys));
        if (headers != null) {
            for (Map.Entry<String, String> e : headers.entrySet()) {
                request.addHeader(e.getKey(), e.getValue());
            }
        }
        //追加默认header
        //若没有指定Content-Type, 则"Content type" 默认为 null
        //若没有手动指定, 这里就追加设置为常用 application/json; charset=UTF-8
        if (headers == null || headers.get(CONTENT_TYPE) == null) {
            request.addHeader(CONTENT_TYPE, "application/json; charset=UTF-8");
        }

        return httpClient.execute(request);
    }

    /**
     * post form
     * <p>
     * 请求体会格式化成url格式, name=libai&age=19, 且执行了urlencode.
     * Controller方法需要使用@RequestParam接收参数, @RequestBody无效(Spring框架).
     * </p>
     *
     * @param host
     * @param path
     * @param headers
     * @param querys
     * @param bodys
     * @return
     * @throws Exception
     */
    public static HttpResponse doPost(String host, String path,
                                      Map<String, String> headers,
                                      Map<String, String> querys,
                                      Map<String, String> bodys)
            throws Exception {
        HttpClient httpClient = wrapClient(host);

        HttpPost request = new HttpPost(buildUrl(host, path, querys));
        if (headers != null) {
            for (Map.Entry<String, String> e : headers.entrySet()) {
                request.addHeader(e.getKey(), e.getValue());
            }
        }

        if (bodys != null) {
            List<NameValuePair> nameValuePairList = new ArrayList<NameValuePair>();

            for (String key : bodys.keySet()) {
                nameValuePairList.add(new BasicNameValuePair(key, bodys.get(key)));
            }
            UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(nameValuePairList, "utf-8");
            formEntity.setContentType("application/x-www-form-urlencoded; charset=UTF-8");
            request.setEntity(formEntity);
        }

        return httpClient.execute(request);
    }

    /**
     * Post json
     * <p>
     * 建议此方法请求体设置为json字符串. 接收方Controller对应@RequestBody接收参数(Spring框架).
     * </p>
     *
     * @param host
     * @param path
     * @param headers
     * @param querys
     * @param body    建议传入json格式字符串. 若要传入普通文本, 务必设置content type为'text/plain;charset=UTF-8'
     * @return
     * @throws Exception
     */
    public static HttpResponse doPost(String host, String path,
                                      Map<String, String> headers,
                                      Map<String, String> querys,
                                      String body)
            throws Exception {
        HttpClient httpClient = wrapClient(host);

        HttpPost request = new HttpPost(buildUrl(host, path, querys));

        if (headers != null) {
            for (Map.Entry<String, String> e : headers.entrySet()) {
                request.addHeader(e.getKey(), e.getValue());
            }
        }

        //追加默认header
        //若没有指定Content-Type, 则"Content type" 默认为'text/plain;charset=UTF-8'
        //若没有手动指定, 这里就追加设置为常用 application/json; charset=UTF-8
        if (headers == null || headers.get(CONTENT_TYPE) == null) {
            request.addHeader(CONTENT_TYPE, "application/json; charset=UTF-8");
        }

        if (StringUtils.isNotBlank(body)) {
            request.setEntity(new StringEntity(body, "utf-8"));

            //默认就是Content type 'application/octet-stream'
            //request.setEntity(new InputStreamEntity(new ByteArrayInputStream(body.getBytes("utf-8"))));
        }

        return httpClient.execute(request);
    }

    /**
     * Post stream
     *
     * @param host
     * @param path
     * @param headers
     * @param querys
     * @param body
     * @return
     * @throws Exception
     */
    public static HttpResponse doPost(String host, String path,
                                      Map<String, String> headers,
                                      Map<String, String> querys,
                                      byte[] body)
            throws Exception {
        HttpClient httpClient = wrapClient(host);

        HttpPost request = new HttpPost(buildUrl(host, path, querys));
        for (Map.Entry<String, String> e : headers.entrySet()) {
            request.addHeader(e.getKey(), e.getValue());
        }

        if (body != null) {
            request.setEntity(new ByteArrayEntity(body));
        }

        return httpClient.execute(request);
    }

    /**
     * Put json
     * <p>
     * 类似doPost(post json)
     * </p>
     *
     * @param host
     * @param path
     * @param headers
     * @param querys
     * @param body
     * @return
     * @throws Exception
     */
    public static HttpResponse doPut(String host, String path,
                                     Map<String, String> headers,
                                     Map<String, String> querys,
                                     String body)
            throws Exception {
        HttpClient httpClient = wrapClient(host);

        HttpPut request = new HttpPut(buildUrl(host, path, querys));
        if (headers != null) {
            for (Map.Entry<String, String> e : headers.entrySet()) {
                request.addHeader(e.getKey(), e.getValue());
            }
        }

        //追加默认header
        //若没有指定Content-Type, 则"Content type" 默认为'text/plain;charset=UTF-8'
        //若没有手动指定, 这里就追加设置为常用application/json
        if (headers == null || headers.get(CONTENT_TYPE) == null) {
            request.addHeader(CONTENT_TYPE, "application/json; charset=UTF-8");
        }

        if (StringUtils.isNotBlank(body)) {
            request.setEntity(new StringEntity(body, "utf-8"));
        }

        return httpClient.execute(request);
    }

    /**
     * Put stream
     *
     * @param host
     * @param path
     * @param method
     * @param headers
     * @param querys
     * @param body
     * @return
     * @throws Exception
     */
    public static HttpResponse doPut(String host, String path, String method,
                                     Map<String, String> headers,
                                     Map<String, String> querys,
                                     byte[] body)
            throws Exception {
        HttpClient httpClient = wrapClient(host);

        HttpPut request = new HttpPut(buildUrl(host, path, querys));
        for (Map.Entry<String, String> e : headers.entrySet()) {
            request.addHeader(e.getKey(), e.getValue());
        }

        if (body != null) {
            request.setEntity(new ByteArrayEntity(body));
        }

        return httpClient.execute(request);
    }

    /**
     * Delete
     *
     * @param host
     * @param path
     * @param method
     * @param headers
     * @param querys
     * @return
     * @throws Exception
     */
    public static HttpResponse doDelete(String host, String path, String method,
                                        Map<String, String> headers,
                                        Map<String, String> querys)
            throws Exception {
        HttpClient httpClient = wrapClient(host);

        HttpDelete request = new HttpDelete(buildUrl(host, path, querys));
        for (Map.Entry<String, String> e : headers.entrySet()) {
            request.addHeader(e.getKey(), e.getValue());
        }

        return httpClient.execute(request);
    }


    public static void main(String[] args) {
        String url = "127.0.0.1:8080";
        try {
            JSONObject jsonObjectSend = new JSONObject();
            JSONObject bodyJsonObject = new JSONObject();
            JSONArray jsonArray = new JSONArray();
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost method = new HttpPost(url);
            String[] devices = {"123", "234"};
            bodyJsonObject.put("message", "test");
            bodyJsonObject.put("devices", devices);
            jsonArray.put(0, bodyJsonObject);
            jsonObjectSend.put("body", jsonArray);
            String sendstr = jsonObjectSend.toString();
            method.addHeader("Content-type", "application/json; charset=utf-8");
            method.setEntity(new StringEntity(sendstr, Charset.forName("UTF-8")));
            httpClient.execute(method);
            return;
        } catch (Exception e) {
            System.out.println("error....");
            System.out.println(e.toString());
            System.out.println("--------------------");
            System.out.println(e.getMessage());
            System.out.println("--------------------");
            e.printStackTrace();
            return;
        }
    }


    /**
     * Note: 仅适用于响应结果为合法的Json时
     *
     * @param response
     * @return
     * @throws IOException
     * @throws JSONException 只要在响应结果为Json时才可以调用此方法, 否则会报Json解析异常
     */
    public static com.alibaba.fastjson.JSONObject getJsonEntity(HttpResponse response)
            throws IOException, JSONException {
        HttpEntity entity = response.getEntity();
        if (entity==null) {
            return null;
        }
        return JSON.parseObject(EntityUtils.toString(entity));
    }

    /**指定类型, 获取响应实体
     * @param response
     * @param type 指定转换的目标类型
     * @param <T>
     * @return
     * @throws IOException
     * @throws JSONException
     */
    public static <T> T getJsonEntity(HttpResponse response, TypeReference<T> type)
            throws IOException, JSONException {
        HttpEntity entity = response.getEntity();
        if (entity==null) {
            return null;
        }
        return JSON.parseObject(EntityUtils.toString(entity),type);
    }

    public static <T> T getJsonEntity(HttpResponse response, Type type)
            throws IOException, JSONException {
        HttpEntity entity = response.getEntity();
        if (entity==null) {
            return null;
        }
        return JSON.parseObject(EntityUtils.toString(entity),type);
    }


    /**
     * 将响应体转成字符串
     *
     * @param response
     * @return
     * @throws IOException
     */
    public static String getEntityString(HttpResponse response) throws IOException {
        return EntityUtils.toString(response.getEntity());
    }


    private static String buildUrl(String host, String path, Map<String, String> querys) throws UnsupportedEncodingException {
        StringBuilder sbUrl = new StringBuilder();

        sbUrl.append(host);

        if (!StringUtils.isBlank(path)) {
            char lh = sbUrl.charAt(sbUrl.length() - 1);
            char lp = path.charAt(0);
            if (lh == URL_PATH_SEP && lp == URL_PATH_SEP) {
                //减一个
                sbUrl.deleteCharAt(sbUrl.length() - 1);
            }
            if (lh != URL_PATH_SEP && lp != URL_PATH_SEP) {
                //补一个
                sbUrl.append(URL_PATH_SEP);
            }
            sbUrl.append(path);
        }
        if (null != querys) {
            StringBuilder sbQuery = new StringBuilder();
            for (Map.Entry<String, String> query : querys.entrySet()) {
                if (0 < sbQuery.length()) {
                    sbQuery.append("&");
                }
                if (StringUtils.isBlank(query.getKey()) && StringUtils.isNotBlank(query.getValue())) {
                    sbQuery.append(query.getValue());
                }
                if (query.getKey() != null) {
                    sbQuery.append(query.getKey());
                    if (query.getValue() != null) {
                        sbQuery.append("=");
                        sbQuery.append(URLEncoder.encode(String.valueOf(query.getValue()), "utf-8"));
                    }
                }
            }
            if (0 < sbQuery.length()) {
                sbUrl.append("?").append(sbQuery);
            }
        }

        return sbUrl.toString();
    }

    private static HttpClient wrapClient(String host) {
        HttpClient httpClient = new DefaultHttpClient();
        if (host.startsWith("https://")) {
            sslClient(httpClient);
        }

        return httpClient;
    }

    private static void sslClient(HttpClient httpClient) {
        try {
            SSLContext ctx = SSLContext.getInstance("TLS");
            X509TrustManager tm = new X509TrustManager() {
                public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }

                public void checkClientTrusted(X509Certificate[] xcs, String str) {

                }

                public void checkServerTrusted(X509Certificate[] xcs, String str) {

                }
            };
            ctx.init(null, new TrustManager[]{tm}, null);
            SSLSocketFactory ssf = new SSLSocketFactory(ctx);
            ssf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
            ClientConnectionManager ccm = httpClient.getConnectionManager();
            SchemeRegistry registry = ccm.getSchemeRegistry();
            registry.register(new Scheme("https", 443, ssf));
        } catch (KeyManagementException ex) {
            throw new RuntimeException(ex);
        } catch (NoSuchAlgorithmException ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * 简化200判断
     */
    public static boolean is200(HttpResponse response) {
        if (response != null && response.getStatusLine() != null && response.getStatusLine().getStatusCode() == 200) {
            return true;
        }
        return false;
    }

    /**
     * 获取http状态码
     * @return 获取失败, 返回 null
     */
    public static Integer getHttpStatusCode(HttpResponse response) {
        if (response!=null && response.getStatusLine()!=null) {
            return response.getStatusLine().getStatusCode();
        }
        return null;
    }

}