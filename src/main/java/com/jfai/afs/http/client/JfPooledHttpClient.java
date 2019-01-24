package com.jfai.afs.http.client;

import org.apache.http.*;
import org.apache.http.client.HttpClient;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.protocol.HttpContext;

import javax.net.ssl.SSLException;
import javax.net.ssl.SSLHandshakeException;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.UnknownHostException;

/**
 * Http请求工具类
 * 
 * @author 大漠知秋
 */
public class JfPooledHttpClient extends JfHttpClient{

    /** 全局连接池对象 */
    protected static final PoolingHttpClientConnectionManager connManager = new PoolingHttpClientConnectionManager();
    /**
     * 创建Http请求配置参数
     */
    private static RequestConfig requestConfig;
    /**
     * 测出超时重试机制为了防止超时不生效而设置
     *  如果直接放回false,不重试
     *  这里会根据情况进行判断是否重试
     */
    private static HttpRequestRetryHandler httpRequestRetryHandler;

    /*
     * 静态代码块配置连接池信息
     */
    static {
        
        // 设置最大连接数
        connManager.setMaxTotal(200);
        // 设置每个路由的最大连接数 / 受cpu限制, 过多无益
        connManager.setDefaultMaxPerRoute(10);

        // 创建Http请求配置参数
        requestConfig = RequestConfig.custom()
                // 默认是0, 即未配置timeout, 这样会一直等待下去
                // 获取连接超时时间 / ms / 每个响应处理结束后要close, 避免连接没有及时释放, 出现Timeout waiting for connection from pool异常
                .setConnectionRequestTimeout(1800000)   // 30min=1800000
                // 请求超时时间
                .setConnectTimeout(1800000)
                // 响应超时时间
                .setSocketTimeout(1800000)
                .build();


        httpRequestRetryHandler = new HttpRequestRetryHandler() {
            @Override
            public boolean retryRequest(IOException exception, int executionCount, HttpContext context) {
                if (executionCount >= 3) {// 如果已经重试了3次，就放弃
                    return false;
                }
                if (exception instanceof NoHttpResponseException) {// 如果服务器丢掉了连接，那么就重试
                    return true;
                }
                if (exception instanceof SSLHandshakeException) {// 不要重试SSL握手异常
                    return false;
                }
                if (exception instanceof InterruptedIOException) {// 超时
                    return true;
                }
                if (exception instanceof UnknownHostException) {// 目标服务器不可达
                    return false;
                }
                if (exception instanceof ConnectTimeoutException) {// 连接被拒绝
                    return false;
                }
                if (exception instanceof SSLException) {// ssl握手异常
                    return false;
                }
                HttpClientContext clientContext = HttpClientContext.adapt(context);
                HttpRequest request = clientContext.getRequest();
                // 如果请求是幂等的，就再次尝试
                if (!(request instanceof HttpEntityEnclosingRequest)) {
                    return true;
                }
                return false;
            }
        };
    }

    /**
     * 获取配有连接池的Http客户端
     * 
     * @return Http客户端连接对象
     */
    @Override
    public HttpClient getHttpClient() {
        // 创建httpClient
        return HttpClients.custom()
                // 把请求相关的超时信息设置到连接客户端
                .setDefaultRequestConfig(requestConfig)
                // 把请求重试设置到连接客户端
                .setRetryHandler(httpRequestRetryHandler)
                // 配置连接池管理对象
                .setConnectionManager(connManager)
                .build();
    }

    
}