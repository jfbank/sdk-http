package com.jfai.afs.http.client;

import com.jfai.afs.http.utils.TypeUtil;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 玖富http客户端需要的配置项.
 * @author Nisus Liu
 */
public class Config {

    /**
     * 参数容器, 线程安全
     */
    private Map<String, String> attributes = new ConcurrentHashMap<>();

    /**
     * 作为身份验证和计费的依据, 必选.
     */
    private static final String APP_KEY = "appKey";
    /**
     * 服务端公钥, 即玖富的公钥, 用于加密请求数据.
     */
    private static final String SERVER_PUBKEY = "serverPubkey";
    /**
     * 客户端公钥, 即接口接入方公钥, 用于验签响应数据.
     */
    private static final String CLIENT_PUBKEY = "clientPubkey";
    /**
     * 客户端私钥, 即接入方的私钥, 用于加密请求数据.
     */
    private static final String CLIENT_PRVKEY = "clientPrvkey";
    /**
     * 和appKey时一对, 用于签名时加盐. !相当于密码, 不可外泄!.
     */
    private static final String APP_SECRET = "appSecret";
    /**
     * 默认host, 如: http://afs.9fbank.com
     */
    private static final String DEFAULT_HOST = "defaultHost";
    /**
     * 是否对请求数据加密.
     */
    private static final String ENCRYPTION = "encryption";
    /**
     * 是否压缩请求数据
     */
    private static String ZIP = "zip";

    public String getAppKey() {
        return attributes.get(APP_KEY);
    }

    public Config setAppKey(String appKey) {
        attributes.put(APP_KEY, appKey);
        return this;
    }

    public String getServerPubkey() {
        return attributes.get(SERVER_PUBKEY);
    }

    public Config setServerPubkey(String serverPubkey) {
        attributes.put(SERVER_PUBKEY, serverPubkey);
        return this;
    }

    public String getClientPubkey() {
        return attributes.get(CLIENT_PUBKEY);
    }

    public Config setClientPubkey(String clientPubkey) {
        attributes.put(CLIENT_PUBKEY, clientPubkey);
        return this;
    }

    public String getClientPrvkey() {
        return attributes.get(CLIENT_PRVKEY);
    }

    public Config setClientPrvkey(String clientPrvkey) {
        attributes.put(CLIENT_PRVKEY, clientPrvkey);
        return this;
    }

    public String getAppSecret() {
        return attributes.get(APP_SECRET);
    }

    public Config setAppSecret(String appSecret) {
        attributes.put(APP_SECRET, appSecret);
        return this;
    }

    public String getDefaultHost() {
        return attributes.get(DEFAULT_HOST);
    }

    /**默认host, 如: http://afs.9fbank.com, http://localhost:8080
     * <p>
     *     配有此项时, 在发起请求传URL参数时, 只需要<em>方法路径</em>即可.
     *     会自动拼接 host和path, 也会自动处理好衔接的"/".
     * </p>
     * @param defaultHost
     * @return
     */
    public Config setDefaultHost(String defaultHost) {
        attributes.put(DEFAULT_HOST, defaultHost);
        return this;
    }

    public boolean getEncryption() {
        return Boolean.parseBoolean(attributes.get(ENCRYPTION));
    }

    /**是否加密请求数据
     * @param encryption
     * @return
     */
    public Config setEncryption(boolean encryption) {
        attributes.put(ENCRYPTION, String.valueOf(encryption));
        return this;
    }

    public boolean getZip() {
        return Boolean.parseBoolean(attributes.get(ZIP));
    }

    /**是否压缩请求数据
     * @param zip
     * @return
     */
    public Config setZip(boolean zip) {
        attributes.put(ZIP, String.valueOf(zip));
        return this;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Config").append(attributes);
        return sb.toString();
    }

//    public Map<String, String> getAttributes() {
//        return attributes;
//    }

    /**扩充配置: 自身没有的配置项, 会用别人的配置项填充进来.
     * @param other
     * @return
     */
    public Config extend(Config other) {
        if (other != null && other.attributes!=null) {
            for (String othKey : other.attributes.keySet()) {
                // 自己若没有, 就合并进来
                if (!this.attributes.containsKey(othKey)) {
                    this.attributes.put(othKey, other.attributes.get(othKey));
                }
            }
        }

        return this;
    }
}
