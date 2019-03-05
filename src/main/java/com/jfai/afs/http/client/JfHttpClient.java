package com.jfai.afs.http.client;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.TypeReference;
import com.jfai.afs.http.bean.JfReqParam;
import com.jfai.afs.http.bean.JfResBody;
import com.jfai.afs.http.bean.JfResponse;
import com.jfai.afs.http.constant.HttpConst;
import com.jfai.afs.http.exception.JfConfigException;
import com.jfai.afs.http.exception.JfSecurityException;
import com.jfai.afs.http.utils.GzipUtils;
import com.jfai.afs.http.utils.JfCipher;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
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
import org.jetbrains.annotations.NotNull;
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

import org.jetbrains.annotations.Contract;

/**
 * <p>
 * 注意: 对于同样配置的多个请求, 可以复用一个 JfHttpClient 实例.
 * 否则, 是线程不安全的.
 * </p>
 *
 * @author 玖富AI
 */
public class JfHttpClient {
    public static final Logger log = LoggerFactory.getLogger(JfHttpClient.class);


    public static final String CONTENT_TYPE = "Content-Type";
    public static final char URL_PATH_SEP = '/';
    public static final String HTTP = "http";
    public static final String HTTPS_PROTO = "https://";


    protected volatile Config config = new Config();

    /**
     * {@link RequestConfig} 用于配置{@link HttpClient}
     * <p>
     *     定义的缺省设置:
     *     <li>ConnectionRequestTimeout: 1000ms, 设置从connect Manager获取Connection 超时时间，单位毫秒(针对有连接池的客户端)
     *     <li>ConnectTimeout: 3000ms, 设置连接超时时间, 单位ms.
     *     <li>SocketTimeout: 3000ms, 请求获取数据的超时时间, 单位ms, 如果访问一个接口，多少时间内无法返回数据，就直接放弃此次调用
     * </p>
     * <p>
     *     使用{@link JfHttpClient#setRequestConfig(org.apache.http.client.config.RequestConfig)}可自定义配置.
     * </p>
     * <p>
     *     使用HttpClient，一般都需要设置连接超时时间和获取数据超时时间。
     *     这两个参数很重要，目的是为了防止访问其他http时，由于超时导致自己的应用受影响。
     * </p>
     */
    protected volatile RequestConfig requestConfig = RequestConfig.custom()
            // 设置从connect Manager获取Connection 超时时间，单位毫秒(针对有连接池的客户端)
            .setConnectionRequestTimeout(1000)
            // 设置连接超时时间, 单位ms
            .setConnectTimeout(3000)
            // 请求获取数据的超时时间, 单位ms, 如果访问一个接口，多少时间内无法返回数据，就直接放弃此次调用。
            .setSocketTimeout(3000)
            .build();

    /**
     * 调出config实例, 进行参数配置.
     *
     * @return
     */
    public Config config() {
        return config;
    }

    public RequestConfig getRequestConfig() {
        return requestConfig;
    }

    /**自定义{@link RequestConfig}
     * <p>
     *     RequestConfig构建示例:
     *     <pre>
     *         RequestConfig.custom()
     *             // 设置从connect Manager获取Connection 超时时间，单位毫秒(针对有连接池的客户端)
     *             .setConnectionRequestTimeout(1000)
     *             // 设置连接超时时间, 单位ms
     *             .setConnectTimeout(3000)
     *             // 请求获取数据的超时时间, 单位ms, 如果访问一个接口，多少时间内无法返回数据，就直接放弃此次调用。
     *             .setSocketTimeout(3000)
     *             .build();
     *     </pre>
     * </p>
     * @param requestConfig
     */
    public void setRequestConfig(RequestConfig requestConfig) {
        this.requestConfig = requestConfig;
    }


    public JfHttpClient() {
    }

    /**
     * get
     * <p>
     * 若没有指定Content-Type, 则"Content type" 默认为null.
     * 这里将其改为默认值为: <code>application/json; charset=UTF-8</code>
     * </p>
     *
     * @param headers 可null
     * @param data    接口级别参数, 可null
     * @return
     * @throws Exception
     */
    @Contract("_->!null")
    public <T> JfResponse doGet(String url, Map<String, String> headers, T data) throws Exception {
        HttpClient httpClient = wrapClient(url);

        //
        RequestMeta requestMeta = new RequestMeta();

        // 准备请求参数
        Map<String, Object> params = prepareParams((T) data, requestMeta).toMap();

        HttpGet request = new HttpGet(buildUrl(url, params));
        // 设置RequestConfig
        request.setConfig(requestConfig);

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

        return executeMethod(httpClient, request, requestMeta);
    }


    /**
     * post form
     * <p>
     * 请求体会格式化成url格式, name=libai&age=19, 且执行了urlencode.
     * Controller方法需要使用@RequestParam接收参数, @RequestBody无效(Spring框架).
     * </p>
     *
     * @param url
     * @param headers
     * @param data
     * @return
     * @throws Exception
     */
    @Contract("_->!null")
    public <T> JfResponse doPostForm(String url, Map<String, String> headers, T data) throws Exception {
        HttpClient httpClient = wrapClient(url);

        RequestMeta requestMeta = new RequestMeta();

        // 准备请求参数
        Map<String, Object> params = prepareParams((T) data, requestMeta).toMap();

        HttpPost request = new HttpPost(buildUrl(url, null));
        // 设置RequestConfig
        request.setConfig(requestConfig);

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
                if (v != null) nameValuePairList.add(new BasicNameValuePair(key, String.valueOf(v)));
            }
            UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(nameValuePairList, HttpConst.UTF8);
            formEntity.setContentType("application/x-www-form-urlencoded; charset=UTF-8");
            request.setEntity(formEntity);
        }

        //return httpClient.execute(request);
        return executeMethod(httpClient, request, requestMeta);

    }

    /**
     * Post json
     *
     * <p>
     * 虽然, post请求可以同时带上url参数和请求体, 但是为了简单.
     * 本API在post请求时, 将请求数据统一放入请求体. 不提供同时支持url参数和请求体的API.
     * </p>
     *
     * @param url
     * @param headers
     * @return
     * @throws Exception
     */
    @Contract("_->!null")
    public <T> JfResponse doPost(String url, Map<String, String> headers, T data) throws Exception {
        HttpClient httpClient = wrapClient(url);
        RequestMeta requestMeta = new RequestMeta();

        Map<String, Object> params = prepareParams(data, requestMeta).toMap();


        HttpPost request = new HttpPost(buildUrl(url, null));
        // 设置RequestConfig
        request.setConfig(requestConfig);


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
        return executeMethod(httpClient, request, requestMeta);
    }




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
    protected String buildUrl(String url, Map<String, Object> querys) {
        //assert StringUtils.isNotBlank(url) : "url mustn't be empty";

        StringBuilder sbUrl = new StringBuilder();

        // url 若是相对路径, 则自动在前面补上host
        sbUrl.append(prefixHostAdaptedly(url));


        if (null != querys) {
            StringBuilder sbQuery = new StringBuilder();
            for (Map.Entry<String, Object> query : querys.entrySet()) {
                //region key为空时, 参数对就不要了 --Nisus Liu 2019/1/15 1:37
               /*
                *  if (StringUtils.isBlank(query.getKey()) && StringUtils.isNotBlank(query.getValue())) {
                     sbQuery.append(query.getValue());
                 }
                */
                //endregion
                // k,v 同时有效, 才拼接
                if (query.getKey() != null && query.getValue() != null) {
                    if (0 < sbQuery.length()) {
                        sbQuery.append("&");
                    }
                    try {
                        // 对key也url编码
                        sbQuery.append(URLEncoder.encode(query.getKey(), HttpConst.UTF8.name()));
                    } catch (UnsupportedEncodingException e) {
                        throw new RuntimeException(e);
                    }
                    sbQuery.append("=");
                    try {
                        sbQuery.append(URLEncoder.encode(String.valueOf(query.getValue()), HttpConst.UTF8.name()));
                    } catch (UnsupportedEncodingException e) {
                        throw new RuntimeException(e);
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

            if (StringUtils.isBlank(config.getDefaultHost())) {
                throw new JfConfigException("使用相对URL时, 需要'defaultHost'配置");
            }
            StringBuilder sbUrl = new StringBuilder(32);
            sbUrl.append(config.getDefaultHost());

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
     *
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

    protected HttpClient wrapClient(String url) {
        HttpClient httpClient = getHttpClient();
        if (isAbsoluteUrl(url)) {
            if (url.startsWith(HTTPS_PROTO)) {
                sslClient(httpClient);
            }
        } else {
            if (StringUtils.isBlank(config.getDefaultHost())) {
                throw new JfConfigException("使用相对URL时, 需要'defaultHost'配置");
            }
            if (config.getDefaultHost().startsWith(HTTPS_PROTO)) {
                sslClient(httpClient);
            }
        }

        return httpClient;
    }


    @Contract("_,_,_->!null")
    protected JfResponse executeMethod(HttpClient httpClient, HttpRequestBase request, RequestMeta requestMeta) throws IOException {
        HttpResponse resp = null;
        JfResponse jfResponse;
        try {
            resp = httpClient.execute(request);
        } catch (IOException e) {
            log.error("Execute request ex: {}", e.getMessage());
            throw e;

        } finally {
            // 处理完响应后, 才可以关闭
            jfResponse = handleJsonResponse(resp, requestMeta);

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

    /**
     * 针对json的响应体的处理
     * <p>
     * 本系统暂时仅支持返回json响应体. 对于响应结果的处理, 也是仅提供json格式的处理.
     * </p>
     *
     * @param resp
     * @param requestMeta
     * @return
     * @throws IOException
     */
    @Contract("_, _ -> !null")
    protected JfResponse handleJsonResponse(HttpResponse resp, RequestMeta requestMeta) throws IOException {
        if (resp == null) {
            // 返回无body
            return new JfResponse();
        }

        Integer sc = null;
        if (resp.getStatusLine() != null) {
            sc = resp.getStatusLine().getStatusCode();
        }

        // 默认支持json响应体
        // 服务端必须响应json
        JfResBody body = getJsonEntity(resp, JfResBody.class);

        // 若响应体是加密的, 则执行解密操作
        // 解密流程包括: 验签 -> 解密
        if (body != null) {
            // 验签
            if (body.getSign() != null) {
                log.debug("发现'sign', 进入验签: ");
                boolean b = JfCipher.checkSign(body, config.getAppSecret());
                if (!b) {
                    throw new JfSecurityException("响应数据验签失败: 服务端签名设置有误, 或数据被篡改");
                }
            }

            // 加密响应
            if (body.getEncryption() != null && body.getEncryption()) {
                try {
                    JfCipher.decrypt2(body, requestMeta.getSessionKey(), body.getZip() != null && body.getZip());
                    log.debug("解密后的JfResBody: {}", body);
                } catch (Exception e) {
                    throw new JfSecurityException("decrypt exception", e);
                }
            } else {
                // 明文, 但压缩的响应
                if (body.getZip() != null && body.getZip()) {
                    String s = GzipUtils.ungzipb64(body.getData());
                    body.setData(s);
                    log.debug("解压后的JfResBody: {}", body);
                }
            }
        }


        return new JfResponse(sc, body);
    }


    /**
     * 默认使用基本的httpClient
     *
     * @return
     */
    public HttpClient getHttpClient() {
        return new DefaultHttpClient();
//        return HttpClients.createDefault();
    }

    protected void sslClient(HttpClient httpClient) {
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
     * 自动增加玖富接口需要系统级参数
     * <p>
     * url参数包装. 开发者提供的接口级参数data, 会转换成json字符串
     * </p>
     *
     * @param data
     */
    @NotNull
    protected <T> JfReqParam wrapReqParams(T data) {
        //Map<String, Object> p = new HashMap<>();
        JfReqParam reqParam = new JfReqParam();
        reqParam.setData(data);


        // 补充系统参数
        if (config.getAppKey() != null){
            reqParam.setAppKey(config.getAppKey());
        }else{
            throw new JfConfigException("缺少 'appKey' 参数配置");
        }
        reqParam.setTimestamp(System.currentTimeMillis());


        return reqParam;
    }

    @NotNull
    private String buildSignSrc(JfReqParam reqParam) {
        if (config.getAppSecret() == null) {
            throw new JfConfigException("缺少 'appSecret' 参数配置");
        }
        HashMap<String, Object> signMap = new HashMap<>();
        signMap.put(HttpConst.APP_KEY, reqParam.getAppKey());
        signMap.put(HttpConst.TIMESTAMP, reqParam.getTimestamp());
        return JfCipher.buildSignSource(signMap, config.getAppSecret());
    }


    /**
     * 准备请求参数
     * <p>
     * <ul>
     * <li>补充请求需要的系统级别参数. 开发者只需要关心如何准备目标接口所需接口级参数.
     * 而无需关心, 如: 'appKey', 'sessionKey', 'sign', ...</li>
     * <li>加密和压缩请求参数(由配置参数决定是否加密和压缩)</li>
     * <li>'data' 字段的不论是基本类型还是pojo, 都转成json字符串</li>
     * </ul>
     * </p>
     *
     * @param <T>
     * @param data
     * @param requestMeta
     * @return
     */
    protected <T> JfReqParam prepareParams(T data, RequestMeta requestMeta) throws JfSecurityException {
        // 补充系统级参数
        JfReqParam params = wrapReqParams(data);

        // 加密
        if (config.getEncryption()) {
            try {
                //JfCipher.encryptAndSign(params, config.getServerPubkey(), config.getClientPrvkey(), config.getAppSecret(), config.getZip());

                String serverPubkey = config.getServerPubkey();
                if (serverPubkey == null) {
                    throw new JfConfigException("缺少服务端公钥");
                }


                String sessionKey = JfCipher.encrypt(params, config.getServerPubkey(), config.getZip());
                // 暂存sessionKey
                requestMeta.setSessionKey(sessionKey);

                log.debug("加密后的JfReqParam: {}", params);
            } catch (Exception e) {
                throw new JfSecurityException("encrypt and sign exception", e);
            }
        } else {
            // 不加密, 但压缩
            if (config.getZip()) {
                String s = GzipUtils.gzip2b64u(params.getData());
                params.setData(s);
                params.setZip(true);
                log.debug("压缩后的JfReqParam: {}", params);
            }

        }

        // 最后, 制作签名
        if (config.getAppSecret() == null) {
            throw new JfConfigException("缺少 'appSecret' 参数配置");
        }

        JfCipher.sign(params, config.getAppSecret());


        return params;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder(this.getClass().getSimpleName()).append("{");
        sb.append(config);
        sb.append('}');
        return sb.toString();
    }


    /**
     * 用来保存每次请求中的数据, 这些数据在收到响应后需要用到.
     * <p>
     *     每个请求独占一个实例.
     * </p>
     */
    class RequestMeta{
        String sessionKey;

        public String getSessionKey() {
            return sessionKey;
        }

        public void setSessionKey(String sessionKey) {
            this.sessionKey = sessionKey;
        }
    }
}