package com.jfai.afs.http.client;

import com.jfai.afs.http.utils.TypeUtil;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 玖富http客户端需要的配置项.
 *
 * @author Nisus Liu
 */
public class Config {

    /**
     * 作为身份验证和计费的依据, 必选                                                                                  .
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
     * 默认host, 如: http://afs.9fbank.com, http://localhost:8080
     * <p>
     * 配有此项时, 在发起请求传URL参数时, 只需要<em>方法路径</em>即可.
     * 会自动拼接 host和path, 也会自动处理好衔接的"/".
     * </p>
     */
    private String defaultHost;
    /**
     * 是否对请求数据加密.
     */
    private boolean encryption;
    /**
     * 是否压缩请求数据
     */
    private boolean zip;

    public Config() {
    }

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

    /**
     * 扩充配置: 自身没有的配置项, 会用别人的配置项填充进来.
     *
     * @param other
     * @return
     */
    public Config extend(Config other) {
        if (other != null) {
            if (appKey == null) appKey = other.getAppKey();
            if (serverPubkey == null) serverPubkey = other.getServerPubkey();
            if (clientPubkey == null) clientPubkey = other.getClientPubkey();
            if (clientPrvkey == null) clientPrvkey = other.getClientPrvkey();
            if (appSecret == null) appSecret = other.getAppSecret();
            if (defaultHost == null) defaultHost = other.getDefaultHost();
            if (encryption == false) encryption = other.getEncryption();
            if (zip == false) zip = other.getZip();
        }

        return this;
    }
}
