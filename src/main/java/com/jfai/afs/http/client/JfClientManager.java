package com.jfai.afs.http.client;

import com.jfai.afs.http.exception.JfConfigException;
import com.jfai.afs.http.utils.RSA;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**{@link JfHttpClient}的管理者
 * <p>
 *     可以很方便的获取不同场景需要的{@link JfHttpClient}实例.
 * </p>
 * @author Nisus Liu
 * @version 0.0.1
 * @email liuhejun108@163.com
 * @date 2019/1/21 10:43
 */
public class JfClientManager {
    protected static final Logger log = LoggerFactory.getLogger(JfClientManager.class);

    /**
     * Jf http client的全局配置, 所有client的缺省项都会<b>复制</b>这里的参数.
     *
     */
    protected volatile Config config = new Config();

    public Config config() {
        return this.config;
    }

    public JfClientManager() {
        // 初始化一些默认配置项
        this.config.setEncryption(false)
                .setZip(false);
    }

    /**
     * 默认构建带线程池的JfHttpClient: {@link JfPoolHttpClient}
     * <p>
     *     自动继承使用{@link JfClientManager}的
     *     配置参数({@link Config}). 但随后<code>JfHttpClient</code>的定制配置会覆盖继承的全局配置.
     * </p>
     * @return
     */
    public JfHttpClient getJfHttpClient() {
        return getJfHttpClient(true);
    }

    /**
     * 获取带有<em>加密请求数据</em>配置的{@link JfHttpClient}
     * <p>
     *     等价于:
     *     <pre>
     *         JfClientManager jfClientManager = new JfClientManager();
     *         JfHttpClient c = jfClientManager.getJfHttpClient();
     *         c.config().setEncryption(true);
     *     </pre>
     * </p>
     * @return
     */
    public JfHttpClient getJfHttpClientEnc() {
        JfHttpClient c = getJfHttpClient();
        c.config().setEncryption(true);
        //validateConf(c.config());

        return c;
    }

    /**获取带有<em>加密请求数据</em>和<em>压缩请求数据</em>配置的
     * {@link JfHttpClient}.
     * <p>
     *     等价于:
     *     <pre>
     *         JfClientManager jfClientManager = new JfClientManager();
     *         JfHttpClient c = jfClientManager.getJfHttpClient();
     *         c.config().setZip(true);
     *     </pre>
     * </p>
     * @return
     */
    public JfHttpClient getJfHttpClientZip() {
        JfHttpClient c = getJfHttpClient();
        c.config().setZip(true);

        return c;
    }

    /**
     * 加密且压缩请求数据,
     *     等价于:
     *     <pre>
     *         JfClientManager jfClientManager = new JfClientManager();
     *         JfHttpClient c = jfClientManager.getJfHttpClient();
     *         c.config().setEncryption(true).setZip(true);
     *     </pre>
     * @return
     */
    public JfHttpClient getJfHttpClientEncZip() {
        JfHttpClient c = getJfHttpClient();
        c.config().setEncryption(true).setZip(true);

        return c;
    }


    /**
     * @param pooled true: 返回配有线程的客户端; false: 反之.
     * @return
     */
    public JfHttpClient getJfHttpClient(boolean pooled) {
        JfHttpClient client = pooled ? new JfPoolHttpClient() : new JfHttpClient();
        // 继承全局配置
        Config c = client.config();
        c.extend(this.config);
//        // 加密, 压缩配置项
//        c.setZip(zip);
//        c.setEncryption(enc);
//        // 配置完成后, 校验配置
//        validateConf(c);

//        log.trace("get a new JfHttpClient, config: {}", client.config());
        return client;
    }


    private void validateClientKey(String clientPubkey, String clientPrvkey) {
        RSA.validateRsaKeyPair(clientPubkey, clientPrvkey);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("JfClientManager{");
        sb.append("config=").append(config);
        sb.append('}');
        return sb.toString();
    }
}
