package com.jfai.afs.http.sdk;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.TypeReference;
import com.jfai.afs.http.bean.JfResBodyImpl;
import com.jfai.afs.http.bean.JfResponse;
import com.jfai.afs.http.constant.HttpConst;
import com.jfai.afs.http.exception.JfConfigException;
import com.jfai.afs.http.exception.JfSecurityException;
import com.jfai.afs.http.utils.CommonUtil;
import com.jfai.afs.http.utils.JfCipher;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.*;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 *     注意: 对于同样配置的多个请求, 可以复用一个 JfHttpClient 实例.
 *       否则, 是线程不安全的.
 * </p>
 *
 * @author 玖富AI
 */
public class JfHttpClient {
    public static final Logger log = LoggerFactory.getLogger(JfHttpClient.class);


    private static final String CONTENT_TYPE = "Content-Type";
    private static final char URL_PATH_SEP = '/';
    public static final String HTTP = "http";
    public static final String HTTPS_PROTO = "https://";


    private Config config = new Config();

    /**
     * 调出config实例, 进行参数配置.
     *
     * @return
     */
    public Config config() {
        return config;
    }


    /**
     * get
     * <p>
     * 若没有指定Content-Type, 则"Content type" 默认为null.
     * 这里将其改为默认值为: application/json; charset=UTF-8
     * </p>
     *
     * @param headers
     * @param data 接口级别参数
     * @return
     * @throws Exception
     */
    public <T> JfResponse doGet(String url,
                            Map<String, String> headers,
                            T data) throws Exception {
        HttpClient httpClient = wrapClient(url);

        // 准备请求参数
        Map<String, Object> params = prepareParams((T) data);

        HttpGet request = new HttpGet(buildUrl(url, params));
        if (headers != null) {
            for (Map.Entry<String, String> e : headers.entrySet()) {
                request.addHeader(e.getKey(), e.getValue());
            }
        }
        // 追加默认header
        // 若没有指定Content-Type, 则"Content type" 默认为 null
        // 若没有手动指定, 这里就追加设置为常用 application/json; charset=UTF-8
        if (headers == null || headers.get(CONTENT_TYPE) == null) {
            request.addHeader(CONTENT_TYPE, "application/json; charset=UTF-8");
        }

        return executeMethod(httpClient, request);
    }



    /**
     * post form
     * <p>
     *     请求体会格式化成url格式, name=libai&age=19, 且执行了urlencode.
     *     Controller方法需要使用@RequestParam接收参数, @RequestBody无效(Spring框架).
     * </p>
     * @param url
     * @param headers
     * @param data
     * @return
     * @throws Exception
     */
    public <T> JfResponse doPostForm(String url, Map<String, String> headers, T data) throws Exception {
        HttpClient httpClient = wrapClient(url);
        // 准备请求参数
        Map<String, Object> params = prepareParams((T) data);

        HttpPost request = new HttpPost(buildUrl(url, null));
        if (headers != null) {
            for (Map.Entry<String, String> e : headers.entrySet()) {
                request.addHeader(e.getKey(), e.getValue());
            }
        }

        // name1=xxx&name2=xxx 放入请求体
        if (params != null) {
            List<NameValuePair> nameValuePairList = new ArrayList<NameValuePair>();

            for (String key : params.keySet()) {
                Object v = params.get(key);
                if(v!=null) nameValuePairList.add(new BasicNameValuePair(key, String.valueOf(v)));
            }
            UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(nameValuePairList, HttpConst.UTF8);
            formEntity.setContentType("application/x-www-form-urlencoded; charset=UTF-8");
            request.setEntity(formEntity);
        }

        //return httpClient.execute(request);
        return executeMethod(httpClient, request);

    }

    /**
     * Post json
     *
     * <p>
     *     虽然, post请求可以同时带上url参数和请求体, 但是为了简单.
     *     本API在post请求时, 将请求数据统一放入请求体. 不提供同时支持url参数和请求体的API.
     * </p>
     *
     *
     * @param url
     * @param headers
     * @return
     * @throws Exception
     */
    public <T> JfResponse doPost(String url, Map<String, String> headers, T data) throws Exception {
        HttpClient httpClient = wrapClient(url);
        Map<String, Object> params = prepareParams(data);


        HttpPost request = new HttpPost(buildUrl(url, null));

        if (headers != null) {
            for (Map.Entry<String, String> e : headers.entrySet()) {
                request.addHeader(e.getKey(), e.getValue());
            }
        }

        // 追加默认header
        // 若没有指定Content-Type, 则"Content type" 默认为'text/plain;charset=UTF-8'
        // 若没有手动指定, 这里就追加设置为常用 application/json; charset=UTF-8
        if (headers == null || headers.get(CONTENT_TYPE) == null) {
            request.addHeader(CONTENT_TYPE, "application/json; charset=UTF-8");
        }

        String jsonBody = JSON.toJSONString(params);
        if (StringUtils.isNotBlank(jsonBody)) {
            request.setEntity(new StringEntity(jsonBody, HttpConst.UTF8));

            //默认就是Content type 'application/octet-stream'
            //request.setEntity(new InputStreamEntity(new ByteArrayInputStream(body.getBytes("utf-8"))));
        }

        //return httpClient.execute(request);
        return executeMethod(httpClient, request);
    }

//    /**
//     * Post stream
//     *
//     * @param host
//     * @param path
//     * @param headers
//     * @param querys
//     * @param body
//     * @return
//     * @throws Exception
//     */
//    public HttpResponse doPost(String host, String path,
//                               Map<String, String> headers,
//                               Map<String, String> querys,
//                               byte[] body)
//            throws Exception {
//        HttpClient httpClient = wrapClient(host);
//
//        HttpPost request = new HttpPost(buildUrl(host, path, querys));
//        for (Map.Entry<String, String> e : headers.entrySet()) {
//            request.addHeader(e.getKey(), e.getValue());
//        }
//
//        if (body != null) {
//            request.setEntity(new ByteArrayEntity(body));
//        }
//
//        return httpClient.execute(request);
//    }
//
//    /**
//     * Put json
//     * <p>
//     * 类似doPost(post json)
//     * </p>
//     *
//     * @param host
//     * @param path
//     * @param headers
//     * @param querys
//     * @param body
//     * @return
//     * @throws Exception
//     */
//    public HttpResponse doPut(String host, String path,
//                              Map<String, String> headers,
//                              Map<String, String> querys,
//                              String body)
//            throws Exception {
//        HttpClient httpClient = wrapClient(host);
//
//        HttpPut request = new HttpPut(buildUrl(host, path, querys));
//        if (headers != null) {
//            for (Map.Entry<String, String> e : headers.entrySet()) {
//                request.addHeader(e.getKey(), e.getValue());
//            }
//        }
//
//        //追加默认header
//        //若没有指定Content-Type, 则"Content type" 默认为'text/plain;charset=UTF-8'
//        //若没有手动指定, 这里就追加设置为常用application/json
//        if (headers == null || headers.get(CONTENT_TYPE) == null) {
//            request.addHeader(CONTENT_TYPE, "application/json; charset=UTF-8");
//        }
//
//        if (StringUtils.isNotBlank(body)) {
//            request.setEntity(new StringEntity(body, "utf-8"));
//        }
//
//        return httpClient.execute(request);
//    }
//
//    /**
//     * Put stream
//     *
//     * @param host
//     * @param path
//     * @param method
//     * @param headers
//     * @param querys
//     * @param body
//     * @return
//     * @throws Exception
//     */
//    public HttpResponse doPut(String host, String path, String method,
//                              Map<String, String> headers,
//                              Map<String, String> querys,
//                              byte[] body)
//            throws Exception {
//        HttpClient httpClient = wrapClient(host);
//
//        HttpPut request = new HttpPut(buildUrl(host, path, querys));
//        for (Map.Entry<String, String> e : headers.entrySet()) {
//            request.addHeader(e.getKey(), e.getValue());
//        }
//
//        if (body != null) {
//            request.setEntity(new ByteArrayEntity(body));
//        }
//
//        return httpClient.execute(request);
//    }
//
//    /**
//     * Delete
//     *
//     * @param host
//     * @param path
//     * @param method
//     * @param headers
//     * @param querys
//     * @return
//     * @throws Exception
//     */
//    public HttpResponse doDelete(String host, String path, String method,
//                                 Map<String, String> headers,
//                                 Map<String, String> querys)
//            throws Exception {
//        HttpClient httpClient = wrapClient(host);
//
//        HttpDelete request = new HttpDelete(buildUrl(host, path, querys));
//        for (Map.Entry<String, String> e : headers.entrySet()) {
//            request.addHeader(e.getKey(), e.getValue());
//        }
//
//        return httpClient.execute(request);
//    }

//
//    public static void main(String[] args) {
//        String url = "127.0.0.1:8080";
//        try {
//            JSONObject jsonObjectSend = new JSONObject();
//            JSONObject bodyJsonObject = new JSONObject();
//            JSONArray jsonArray = new JSONArray();
//            HttpClient httpClient = new DefaultHttpClient();
//            HttpPost method = new HttpPost(url);
//            String[] devices = {"123", "234"};
//            bodyJsonObject.put("message", "test");
//            bodyJsonObject.put("devices", devices);
//            jsonArray.put(0, bodyJsonObject);
//            jsonObjectSend.put("body", jsonArray);
//            String sendstr = jsonObjectSend.toString();
//            method.addHeader("Content-type", "application/json; charset=utf-8");
//            method.setEntity(new StringEntity(sendstr, Charset.forName("UTF-8")));
//            httpClient.execute(method);
//            return;
//        } catch (Exception e) {
//            System.out.println("error....");
//            System.out.println(e.toString());
//            System.out.println("--------------------");
//            System.out.println(e.getMessage());
//            System.out.println("--------------------");
//            e.printStackTrace();
//            return;
//        }
//    }


    /**
     * Note: 仅适用于响应结果为合法的Json时
     *
     * @param response
     * @return
     * @throws IOException
     * @throws JSONException 只要在响应结果为Json时才可以调用此方法, 否则会报Json解析异常
     */
    public com.alibaba.fastjson.JSONObject getJsonEntity(HttpResponse response)
            throws IOException, JSONException {
        HttpEntity entity = response.getEntity();
        if (entity == null) {
            return null;
        }
        return JSON.parseObject(EntityUtils.toString(entity));
    }

    /**
     * 指定类型, 获取响应实体
     *
     * @param response
     * @param type     指定转换的目标类型
     * @param <T>
     * @return
     * @throws IOException
     * @throws JSONException
     */
    public <T> T getJsonEntity(HttpResponse response, TypeReference<T> type)
            throws IOException {
        HttpEntity entity = response.getEntity();
        if (entity == null) {
            return null;
        }
        return JSON.parseObject(EntityUtils.toString(entity), type);
    }

    public <T> T getJsonEntity(HttpResponse response, Class<T> type)
            throws IOException {
        HttpEntity entity = response.getEntity();
        if (entity == null) {
            return null;
        }
        return JSON.parseObject(EntityUtils.toString(entity), type);
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


    /**
     * <pre>
     *     1. 可以传入完整的url, 如'http://localhost:8080/test', 也可以在配置了'defaultHost'下, 仅传入相对URL, 如:
     *       '/test'('test'也可, 会自动处理好衔接的/).
     *     2. 自动拼接上URL参数.
     * </pre>
     *
     * @param url
     * @param querys
     * @return
     * @throws UnsupportedEncodingException
     */
    private String buildUrl(String url, Map<String, Object> querys) {
        //assert StringUtils.isNotBlank(url) : "url mustn't be empty";

        StringBuilder sbUrl = new StringBuilder();

        // url 若是相对路径, 则自动在前面补上host
        sbUrl.append(prefixHostAdaptedly(url));


        if (null != querys) {
            StringBuilder sbQuery = new StringBuilder();
            for (Map.Entry<String, Object> query : querys.entrySet()) {
                if (0 < sbQuery.length()) {
                    sbQuery.append("&");
                }
               //region key为空时, 参数对就不要了 --Nisus Liu 2019/1/15 1:37
               /*
                *  if (StringUtils.isBlank(query.getKey()) && StringUtils.isNotBlank(query.getValue())) {
                     sbQuery.append(query.getValue());
                 }
                */
               //endregion
                if (query.getKey() != null) {
                    try {
                        // 对key也url编码
                        sbQuery.append(URLEncoder.encode(query.getKey(), HttpConst.UTF8.name()));
                    } catch (UnsupportedEncodingException e) {
                        throw new RuntimeException(e);
                    }
                    if (query.getValue() != null) {
                        sbQuery.append("=");
                        try {
                            sbQuery.append(URLEncoder.encode(String.valueOf(query.getValue()), HttpConst.UTF8.name()));
                        } catch (UnsupportedEncodingException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            }
            if (0 < sbQuery.length()) {
                sbUrl.append("?").append(sbQuery);
            }
        }

        return sbUrl.toString();
    }

    private String prefixHostAdaptedly(String url) {
        if (!isAbsoluteUrl(url)) {
            // 相对路径时, 需要补上host前缀

            if (StringUtils.isBlank(config.defaultHost)) {
                throw new JfConfigException("使用相对URL时, 需要'defaultHost'配置");
            }
            StringBuilder sbUrl = new StringBuilder(32);
            sbUrl.append(config.defaultHost);

            if (!StringUtils.isBlank(url)) {
                char lh = sbUrl.charAt(sbUrl.length() - 1);
                char lp = url.charAt(0);
                if (lh == URL_PATH_SEP && lp == URL_PATH_SEP) {
                    //减一个
                    sbUrl.deleteCharAt(sbUrl.length() - 1);
                }
                if (lh != URL_PATH_SEP && lp != URL_PATH_SEP) {
                    //补一个
                    sbUrl.append(URL_PATH_SEP);
                }
                sbUrl.append(url);
            }// else 传入的url为空 --> 使用host作为url

            return sbUrl.toString();
        }

        return url;
    }

    /**
     * 以 'http://' 或 'https://' 开头的为绝对路径
     * @param url
     * @return
     */
    private boolean isAbsoluteUrl(String url) {
        if (StringUtils.isBlank(url)) {
            // 为空, 定以为不是绝对路径, 效果是直接用host访问
            return false;
        }

        return url.startsWith(HTTP);
    }

    private HttpClient wrapClient(String url) {
        HttpClient httpClient = getHttpClient();
        if (isAbsoluteUrl(url)) {
            if (url.startsWith(HTTPS_PROTO)) {
                sslClient(httpClient);
            }
        }else{
            if (StringUtils.isBlank(config.defaultHost)) {
                throw new JfConfigException("使用相对URL时, 需要'defaultHost'配置");
            }
            if (config.defaultHost.startsWith(HTTPS_PROTO)) {
                sslClient(httpClient);
            }
        }

        return httpClient;
    }


    private JfResponse executeMethod(HttpClient httpClient, HttpRequestBase request) throws IOException, JfSecurityException {
        HttpResponse resp = null;
        JfResponse jfResponse;
        try {
            resp = httpClient.execute(request);
        } catch (IOException e) {
            log.error("Execute request ex: {}", e.getMessage());
            throw e;

        } finally {
            // 处理完响应后, 才可以关闭
            jfResponse = handleJsonResponse(resp);

            if (resp != null && resp instanceof CloseableHttpResponse) {
                try {
                    ((CloseableHttpResponse) resp).close();
                } catch (IOException e) {
                    log.error("Close response ex: ", e);
                }
            }
        }


        return jfResponse;

    }

    /**针对json的响应体的处理
     * <p>
     *     本系统暂时仅支持返回json响应体. 对于响应结果的处理, 也是仅提供json格式的处理.
     * </p>
     * @param resp
     * @return
     * @throws IOException
     * @throws JfSecurityException
     */
    private JfResponse handleJsonResponse(HttpResponse resp) throws IOException, JfSecurityException {
        if (resp == null) {
            // 返回空响应
            return new JfResponse();
        }

        Integer sc = null;
        if (resp.getStatusLine() != null) {
            sc = resp.getStatusLine().getStatusCode();
        }

        // 默认支持json响应体
        // 服务端必须响应json
        JfResBodyImpl body = getJsonEntity(resp, JfResBodyImpl.class);

        // 若响应体是加密的, 则执行解密操作
        // 解密流程包括: 验签 -> 解密
        if (body!=null && body.getEncryption()) {
            try {
                JfCipher.checkSign(body.values(), config.serverPubkey, config.appSecret);
            } catch (Exception e) {
                throw new JfSecurityException("check sign exception", e);
            }
            try {
                JfCipher.decrypt(body.values(), config.clientPrvkey, body.getZip());
            } catch (Exception e) {
                throw new JfSecurityException("decrypt exception", e);
            }

        }


        return new JfResponse(sc,body);
    }


    /**
     * 默认使用基本的httpClient
     *
     * @return
     */
    public HttpClient getHttpClient() {
        return new DefaultHttpClient();
    }

    private void sslClient(HttpClient httpClient) {
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


    /**自动增加玖富接口需要系统级参数
     * <p>
     *     url参数包装. 开发者提供的接口级参数data, 会转换成json字符串
     * </p>
     * @param data
     */
    private <T> Map<String, Object> wrapReqParams(T data) {
        Map<String, Object> p = new HashMap<>();
        // 接口参数转换, 统一为字符串
        String ds = CommonUtil.getJsonString(data);
        if(ds!=null) p.put(HttpConst.DATA, ds);
        // 补充系统参数
        if(config.getAppKey()!=null) p.put(HttpConst.APP_KEY, config.getAppKey());
        p.put(HttpConst.TIMESTAMP, System.currentTimeMillis());
        return p;
    }


    /**准备请求参数
     * <p>
     *     <ul>
     *         <li>补充请求需要的系统级别参数. 开发者只需要关心如何准备目标接口所需接口级参数.
     *         而无需关心, 如: 'appKey', 'sessionKey', 'sign', ...</li>
     *         <li>加密和压缩请求参数(由配置参数决定是否加密和压缩)</li>
     *     </ul>
     * </p>
     * @param data
     * @param <T>
     * @return
     * @throws JfSecurityException
     */
    private <T> Map<String, Object> prepareParams(T data) throws JfSecurityException {
        // 补充系统级参数
        Map<String, Object> params = wrapReqParams(data);

        // 加密
        if (config.encryption && params != null) {
            try {
                JfCipher.encryptAndSign(params, config.serverPubkey, config.clientPrvkey, config.appSecret, config.zip);
            } catch (Exception e) {
                throw new JfSecurityException("encrypt and sign exception", e);
            }
        }
        return params;
    }



    /**
     * 玖富http客户端需要的配置项.
     */
    public static class Config {
        /**
         * 作为身份验证和计费的依据, 必选.
         */
        private String appKey;
        /**
         * 服务端公钥, 即玖富的公钥, 用于加密请求数据.
         */
        private String serverPubkey;
        /**
         * 客户端公钥, 即接口接入方公钥, 用于验签响应数据.
         */
        private String clientPubkey;
        /**
         * 客户端私钥, 即接入方的私钥, 用于加密请求数据.
         */
        private String clientPrvkey;
        /**
         * 和appKey时一对, 用于签名时加盐. !相当于密码, 不可外泄!.
         */
        private String appSecret;
        /**
         * 默认host, 如: http://afs.9fbank.com
         */
        private String defaultHost;
        /**
         * 是否对请求数据加密.
         */
        private boolean encryption = false;
        /**
         * 是否压缩请求数据
         */
        private boolean zip = false;

        public String getAppKey() {
            return appKey;
        }

        public Config setAppKey(String appKey) {
            this.appKey = appKey;
            return this;
        }

        public String getServerPubkey() {
            return serverPubkey;
        }

        public Config setServerPubkey(String serverPubkey) {
            this.serverPubkey = serverPubkey;
            return this;
        }

        public String getClientPubkey() {
            return clientPubkey;
        }

        public Config setClientPubkey(String clientPubkey) {
            this.clientPubkey = clientPubkey;
            return this;
        }

        public String getClientPrvkey() {
            return clientPrvkey;
        }

        public Config setClientPrvkey(String clientPrvkey) {
            this.clientPrvkey = clientPrvkey;
            return this;
        }

        public String getAppSecret() {
            return appSecret;
        }

        public Config setAppSecret(String appSecret) {
            this.appSecret = appSecret;
            return this;
        }

        public String getDefaultHost() {
            return defaultHost;
        }

        public Config setDefaultHost(String defaultHost) {
            this.defaultHost = defaultHost;
            return this;
        }

        public boolean getEncryption() {
            return encryption;
        }

        public Config setEncryption(boolean encryption) {
            this.encryption = encryption;
            return this;
        }

        public boolean getZip() {
            return zip;
        }

        public Config setZip(boolean zip) {
            this.zip = zip;
            return this;
        }
    }



}