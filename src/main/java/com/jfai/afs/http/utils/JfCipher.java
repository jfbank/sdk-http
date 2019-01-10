package com.jfai.afs.http.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jfai.afs.http.constant.HttpConstant;
import jdk.nashorn.internal.ir.IfNode;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.Charset;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * 定制的加密, 解密, 签名, 验签工具.
 *
 * <pre>
 * 关于压缩:
 *     使用gzip压缩方式.
 *     默认不压缩数据,
 *     若指定压缩, 仅对data字段压缩, 先压缩, 后加密, 再签名.
 *
 *
 * v1.1.1
 *   - 压缩数据时, 加入zip=true标记
 *
 * v1.1
 *   加入解压缩流程
 * </pre>
 *
 * @author 玖富AI
 * @version v1.1.1
 * @email liuhejun108@163.com
 * @date 2018/10/28 10:24
 */
public class JfCipher {
    public static final Logger log = LoggerFactory.getLogger(JfCipher.class);
    public static final String BLANK = "";
    private static final Charset UTF8 = Charset.forName("UTF-8");


    /**
     * 用于对发送数据报的加密和签名
     * <pre>
     *     获得随机的AES密钥(sessionKey)c -->
     *     对data字段用AES密钥(sessionKey)加密 -->
     *     对AES密钥(sessionKey)用对方的RSA公钥加密 -->
     *     用己方的RSA私钥签名 --> Done!
     * </pre>
     *
     * @param vo
     * @param peerPubKey  对方RSA公钥
     * @param selfPrvKey 己方RSA私钥
     * @param appSecret
     * @param aesKey     AES密钥
     * @throws InvalidKeySpecException
     * @throws InvalidKeyException
     * @throws SignatureException
     */
    public static void encryptAndSign(Map<String, Object> vo, String peerPubKey, String selfPrvKey, String appSecret, String aesKey, boolean doZip)
            throws InvalidKeySpecException, InvalidKeyException, SignatureException {
        long tt0 = System.currentTimeMillis();
        assert vo != null : "`vo` (to be encrypted data must be not null)";
        boolean noAesKey = StringUtils.isBlank(aesKey);
        //获得随机的AES密钥(sessionKey)
        long t0 = System.currentTimeMillis();
        String sessionKey = noAesKey ? AES.genAesKey() : aesKey;
        long t1 = System.currentTimeMillis();
        if (noAesKey) log.debug("Generate AES key:{}, cost {}ms", sessionKey, t1 - t0);

        //对data字段用AES密钥(sessionKey)加密
        String data = getStringData(vo.get(HttpConstant.DATA));
        byte[] bitData;

        if (data != null) {
            // 是否压缩
            if (doZip) {
                long gz0 = System.currentTimeMillis();
                bitData = GzipUtils.gzip(data);
                long gz1 = System.currentTimeMillis();
                log.debug("gzip cost: {}ms, source size: {}KB, after gzip: {}KB, compression ratio: {}",
                        gz1 - gz0, data.getBytes().length / 1024.0, bitData.length / 1024.0, data.getBytes().length / (bitData.length + 0.0));
                // 设置压缩标记
                vo.put(HttpConstant.ZIP, true);
            } else {
                bitData = data.getBytes(UTF8);
            }

            // 加密
            long t2 = System.currentTimeMillis();
            String enData = AES.encrypt(bitData, sessionKey);
            long t3 = System.currentTimeMillis();
            log.debug("AES encrypt data({} chars), cost {}ms", data.length(), t3 - t2);
            vo.put(HttpConstant.DATA, enData);
        } else {
            log.debug("`data` is null, no need for encrypting");
        }

        //对AES密钥(sessionKey)用对方的RSA公钥加密
        long t4 = System.currentTimeMillis();
        String enSsk = RSA.encrypt(sessionKey, peerPubKey);      //NoSuchPaddingException
        long t5 = System.currentTimeMillis();
        log.debug("RSA encrypt sessionKey({} chars), cost {}ms", sessionKey.length(), t5 - t4);
        vo.put(HttpConstant.SESSION_KEY, enSsk);

        // 加密后, 要加上 加密标记 encryption=true
        vo.put(HttpConstant.ENCRYPTION, true);

        //用己方的RSA私钥签名
        String signSrc = buildSignSource(vo, appSecret);
        long t6 = System.currentTimeMillis();
        String sign = RSA.sign(signSrc, selfPrvKey);
        long t7 = System.currentTimeMillis();
        log.debug("RSA sign({} chars), cost {}ms", signSrc.length(), t7 - t6);
        vo.put(HttpConstant.SIGN, sign);
        long tt1 = System.currentTimeMillis();
        log.debug("encryptAndSign cost:{}ms", tt1 - tt0);
    }

    private static String getStringData(Object dataObj) {
        if (dataObj == null) {
            return null;
        }
        String data;
        if (isPrimitive(dataObj)) {
            data = String.valueOf(dataObj);
        } else if (dataObj instanceof Byte[] || dataObj instanceof byte[]) {
            throw new RuntimeException("Don't support byte[]");
        } else {
            data = JSON.toJSONString(dataObj);
        }
        return data;
    }

    /**
     * 重载, 方便复用sessionKey
     *
     * @param vo
     * @param peerPubKey
     * @param selfPrvKey
     * @param appSecret
     * @throws InvalidKeyException
     * @throws InvalidKeySpecException
     * @throws SignatureException
     */
    public static void encryptAndSign(Map<String, Object> vo, String peerPubKey, String selfPrvKey, String appSecret, boolean doZip)
            throws InvalidKeyException, InvalidKeySpecException, SignatureException {
        encryptAndSign(vo, peerPubKey, selfPrvKey, appSecret, null, doZip);
    }

    /**
     * 默认不压缩
     *
     * @param vo
     * @param peerPubKey 对方公钥
     * @param selfPrvKey 自己的私钥
     * @param appSecret
     * @throws InvalidKeyException
     * @throws InvalidKeySpecException
     * @throws SignatureException
     */
    public static void encryptAndSign(Map<String, Object> vo, String peerPubKey, String selfPrvKey, String appSecret)
            throws InvalidKeyException, InvalidKeySpecException, SignatureException {
        encryptAndSign(vo, peerPubKey, selfPrvKey, appSecret, null, false);
    }

    /**
     * 用于对即接收的数据包验签
     * <pre>
     *     采用Sha1withRsa算法验签.
     *     简单理解:
     *       1)将数据包转换成URL的key-value形式的字符串(name=xx&age=yy), 排除sign字段自身;
     *       2)用对方的RSA公钥对sign的值解密, 得到SHA值;
     *       3)用SHA1算法对1)中的字符串取SHA值;
     *       4)比对2)和3)的SHA值, 若一致表示验签通过, 否则验签失败.
     *
     *     验签通过后, 方可进行数据报解密操作. 否则, 必然无法解密.
     * </pre>
     *
     * @param vo
     * @param peerPubKey
     * @param appSecret
     * @return
     * @throws InvalidKeySpecException
     * @throws InvalidKeyException
     * @throws SignatureException
     */
    public static boolean checkSign(Map<String, Object> vo, String peerPubKey, String appSecret) throws InvalidKeySpecException, InvalidKeyException, SignatureException {
        //用对方RSA公钥验签
        long t0 = System.currentTimeMillis();
        String content = buildSignSource(vo, appSecret);
        boolean b = RSA.checkSign(content, String.valueOf(vo.get(HttpConstant.SIGN)), peerPubKey);
        long t1 = System.currentTimeMillis();
        log.debug("RSA check sign({} chars), cost {}ms", content.length(), t1 - t0);
        return b;
    }

    public static <T> void decrypt(Map<String, Object> vo, String selfPrvKey, boolean needUnzip) throws InvalidKeySpecException, InvalidKeyException, IOException {
        decrypt(vo, selfPrvKey, null, needUnzip);
    }

    /**
     * 默认不执行解压流程
     *
     * @param vo
     * @param selfPrvKey
     * @param <T>
     * @throws InvalidKeySpecException
     * @throws InvalidKeyException
     */
    public static <T> void decrypt(Map<String, Object> vo, String selfPrvKey) throws InvalidKeySpecException, InvalidKeyException, IOException {
        decrypt(vo, selfPrvKey, null, false);
    }

    public static <T> void decrypt(Map<String, Object> vo, String selfPrvKey, Class<T> dataType) throws InvalidKeySpecException, InvalidKeyException, IOException {
        decrypt(vo, selfPrvKey, dataType, false);
    }

    /**
     * 用于对接收的数据报解密
     * <pre>
     * 1)用己方RSA私钥对 sessionKey 解密, 得到AES密钥;
     * 2)用AES密钥对 data 字段解密
     * 3)OK
     * </pre>
     *
     * @param vo
     * @param selfPrvKey
     * @param dataType   确保类型正确
     * @throws InvalidKeySpecException
     * @throws InvalidKeyException
     */
    public static <T> void decrypt(Map<String, Object> vo, String selfPrvKey, Class<T> dataType, boolean isUngzip) throws InvalidKeySpecException, InvalidKeyException, IOException {
        assert vo != null : "`vo` mustn't be null";
        assert selfPrvKey != null && !"".equals(selfPrvKey) : "`rsaPrivkey` mustn't be empty";

        String enSsk = String.valueOf(vo.get(HttpConstant.SESSION_KEY));
        long t0 = System.currentTimeMillis();
        String deSsk = RSA.decrypt(enSsk, selfPrvKey);
        long t1 = System.currentTimeMillis();
        log.trace("RSA decrypt encrypted sessionKey({} chars), cost {}ms", enSsk.length(), t1 - t0);
        vo.put(HttpConstant.SESSION_KEY, deSsk);

        //进入解密的环节, data 字段必然也必须是 String
        Object dataObj = vo.get(HttpConstant.DATA);
        //data 字段不为null时才需要解密
        if (dataObj != null) {
            String data = String.valueOf(dataObj);
            long t2 = System.currentTimeMillis();
            String deData = null;
            byte[] ungzipDeData = null;
            if (!isUngzip) {
                deData = AES.decrypt(data, deSsk);
                long t3 = System.currentTimeMillis();
                log.debug("AES decrypt data({} chars), cost {}ms", data.length(), t3 - t2);
                //log.trace("decrypted `data`:{}", deData);
            } else {
                ungzipDeData = AES.decryptToBytes(data, deSsk);
                long tt3 = System.currentTimeMillis();
                log.debug("AES decrypt data({} KB), cost {}ms", ungzipDeData.length / 1024.0, tt3 - t2);
            }


            //解密完了后, 是否要解压缩
            if (isUngzip) {
                long g0 = System.currentTimeMillis();
                deData = new String(GzipUtils.ungzipBytes(ungzipDeData), UTF8);
                long g1 = System.currentTimeMillis();
                log.debug("ungzip cost:{}ms, {} KB", g1 - g0, deData.getBytes().length / 1024.0);
            }


            //将解密后的 data Json串还原成指定的类型对象
            if (dataType != null) {
                log.debug("transfer decrypted `data` to specified type {}", dataType);
                Object trDataObj;
                //数组 OR Collection todo
                if (Collection.class.isAssignableFrom(dataType) || dataType.isArray()) {
                    trDataObj = JSON.parseArray(deData, Object.class);//Note: 无法指定元素类型 / List<Object>
                } else {
                    trDataObj = JSON.parseObject(deData, dataType);
                }

                log.trace("transfered `data`:{}", trDataObj);
                vo.put(HttpConstant.DATA, trDataObj);

            } else {
                log.trace("no specified data type, will set decrypted string");
                vo.put(HttpConstant.DATA, deData);
            }


        } else {
            log.info("`data` is null, need not to decrypt");
        }
    }


//    private static Object toObjectData(String deData) {
//        Object dataObj=deData;
//        try {
//            dataObj = JSON.parseObject(deData);
//        } catch (Exception e) {
//        }
//
//        return dataObj;
//    }


    /**
     * <pre>
     *     key != null, value == null => key&...
     * </pre>
     * @param vo
     * @param appSecret 可NULL
     * @return
     */
    private static String buildSignSource(Map<String, Object> vo, String appSecret) {
        //构建TreeMap, 方便按key的字典顺序遍历 --> 改为遍历构建, 防止key有null
        //TreeMap<String, Object> signMap = new TreeMap<>(vo);
        TreeMap<String, Object> signMap = new TreeMap<>();
        for (Map.Entry<String, Object> e : vo.entrySet()) {
            String k = e.getKey();
            Object v = e.getValue();
            if (k != null) {
                signMap.put(k, v);
            }
        }


        //追加appSecret
        if (appSecret != null) {        // 适应无需 appSecret的场景
            signMap.put(HttpConstant.APP_SECRET, appSecret);
        }

        //遍历, 拼接
        StringBuilder sb = new StringBuilder(64);
        for (Map.Entry<String, Object> e : signMap.entrySet()) {
            String k = e.getKey();
            Object v = e.getValue();
            //避开sign字段自身
            if (HttpConstant.SIGN.equals(k)) {
                continue;
            }

            if (sb.length() > 0) {
                sb.append("&");
            }
            sb.append(k);
            if (v != null) {        //防止value==null ==> "null" / if v==null key&...
                sb.append("=").append(v);
            }
        }

        log.debug("待签名正文: {}",sb.toString());
        return sb.toString();
    }


    /**
     * 判断是否是"基本类型"
     * <pre>
     *     Note: 这里的基本类型特指简单的值, "xxx",99,'u',true,...
     *     这些类型不适宜转换成Json string, 虽然可以转换, 但是再反过来parseObject会报错.
     *     toJsonString("xxx") ==> ""xxx""
     * </pre>
     *
     * @param o
     * @return
     */
    private static boolean isPrimitive(Object o) {
        if (o instanceof String || o instanceof Number || o instanceof Boolean
                || o instanceof Character || o instanceof Enum) {
            return true;
        }
        return false;
    }

    public static void main(String[] args) {

    }


}