package com.jfai.afs.http.utils;

import com.jfai.afs.http.bean.HttpVo;
import com.jfai.afs.http.constant.HttpConst;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.Charset;
import java.security.InvalidKeyException;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
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
 * v1.2
 *   - 去掉接收vo的泛型
 *   - k,v 为null和空串(若是字符串)都不拼接到签名正文中
 *   - k,v 都不为空时, 才拼接到正文中去
 *
 * v1.1.1
 *   - 压缩数据时, 加入zip=true标记
 *
 * v1.1
 *   加入解压缩流程
 * </pre>
 *
 * @author 玖富AI
 * @version v1.2
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
    public static void encryptAndSign(HttpVo vo, String peerPubKey, String selfPrvKey, String appSecret, String aesKey, boolean doZip)
            throws InvalidKeySpecException, InvalidKeyException, SignatureException {
        long tt0 = System.currentTimeMillis();
        assert vo != null : "`vo` (to be encrypted data must be not null)";
        boolean noAesKey = StringUtils.isBlank(aesKey);
        //获得随机的AES密钥(sessionKey)
        long t0 = System.currentTimeMillis();
        String sessionKey = noAesKey ? AES.genAesKey() : aesKey;
        long t1 = System.currentTimeMillis();
        if (noAesKey) log.debug("Generate AES key: {}, cost {}ms", sessionKey, t1 - t0);

        //对data字段用AES密钥(sessionKey)加密
        String data = vo.getData(); // 取出来就是json字符

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
                //vo.put(HttpConst.ZIP, true);
                vo.setZip(true);
            } else {
                bitData = data.getBytes(UTF8);
            }

            // 加密
            long t2 = System.currentTimeMillis();
            String enData = AES.encrypt(bitData, sessionKey);
            long t3 = System.currentTimeMillis();
            log.debug("AES encrypt data({} chars), cost {}ms", data.length(), t3 - t2);
            //vo.put(HttpConst.DATA, enData);
            vo.setData(enData);
        } else {
            log.debug("`data` is null, no need for encrypting");
        }

        //对AES密钥(sessionKey)用对方的RSA公钥加密
        long t4 = System.currentTimeMillis();
        String enSsk = RSA.encrypt(sessionKey, peerPubKey);      //NoSuchPaddingException
        long t5 = System.currentTimeMillis();
        log.debug("RSA encrypt sessionKey({} chars), cost {}ms", sessionKey.length(), t5 - t4);
        //vo.put(HttpConst.SESSION_KEY, enSsk);
        vo.setSessionKey(enSsk);

        // 加密后, 要加上 加密标记 encryption=true
        //vo.put(HttpConst.ENCRYPTION, true);
        vo.setEncryption(true);

        //用己方的RSA私钥签名
        String signSrc = buildSignSource(vo.toMap(), appSecret);
        long t6 = System.currentTimeMillis();
        String sign = RSA.sign(signSrc, selfPrvKey);
        long t7 = System.currentTimeMillis();
        log.debug("RSA sign({} chars), cost {}ms", signSrc.length(), t7 - t6);
        //vo.put(HttpConst.SIGN, sign);
        vo.setSign(sign);
        long tt1 = System.currentTimeMillis();
        log.debug("encryptAndSign cost: {}ms", tt1 - tt0);
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
    public static void encryptAndSign(HttpVo vo, String peerPubKey, String selfPrvKey, String appSecret, boolean doZip)
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
    public static void encryptAndSign(HttpVo vo, String peerPubKey, String selfPrvKey, String appSecret)
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
    public static boolean checkSign(HttpVo vo, String peerPubKey, String appSecret) throws InvalidKeySpecException, InvalidKeyException, SignatureException {
        //用对方RSA公钥验签
        long t0 = System.currentTimeMillis();
        String content = buildSignSource(vo.toMap(), appSecret);
        boolean b = RSA.checkSign(content, vo.getSign(), peerPubKey);
        long t1 = System.currentTimeMillis();
        log.debug("RSA check sign({} chars), cost {}ms", content.length(), t1 - t0);
        return b;
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
    public static <T> void decrypt(HttpVo vo, String selfPrvKey) throws InvalidKeySpecException, InvalidKeyException, IOException {
        decrypt(vo, selfPrvKey,  false);
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
     * @throws InvalidKeySpecException
     * @throws InvalidKeyException
     */
    public static <T> void decrypt(HttpVo vo, String selfPrvKey, boolean isUngzip) throws InvalidKeySpecException, InvalidKeyException, IOException {
        assert vo != null : "`vo` mustn't be null";
        assert selfPrvKey != null && !"".equals(selfPrvKey) : "`selfPrvKey` mustn't be empty";

        String enSsk = vo.getSessionKey();
        long t0 = System.currentTimeMillis();
        String deSsk = RSA.decrypt(enSsk, selfPrvKey);
        long t1 = System.currentTimeMillis();
        log.trace("RSA decrypt encrypted sessionKey({} chars), cost {}ms", enSsk.length(), t1 - t0);
        vo.setSessionKey(deSsk);


        String data = vo.getData();
        //data 字段不为null时才需要解密
        if (data != null) {
//            // 进入解密时, data 的值必须是String
//            String data = String.valueOf(dataObj);
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

            // 将解密后的明文替换掉密文
            vo.setData(deData);
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
        // 构建TreeMap, 方便按key的字典顺序遍历 --> 改为遍历构建, 防止key有null
        // TreeMap<String, Object> signMap = new TreeMap<>(vo);
        TreeMap<String, Object> signMap = new TreeMap<>();
        for (Map.Entry<String, Object> e : vo.entrySet()) {
            String k = e.getKey();
            Object v = e.getValue();
            if (StringUtils.isNotBlank(k)) {
                signMap.put(k, v);
            }
        }


        // 追加appSecret
        if (StringUtils.isNotBlank(appSecret)) {        // 适应无需 appSecret 的场景
            signMap.put(HttpConst.APP_SECRET, appSecret);
        }

        // 遍历, 拼接
        StringBuilder sb = new StringBuilder(64);
        for (Map.Entry<String, Object> e : signMap.entrySet()) {
            String k = e.getKey();
            Object v = e.getValue();
            // 避开sign字段自身
            if (HttpConst.SIGN.equals(k)) {
                continue;
            }

            // k,v 都不为空时才拼接, k已经不为空(上文)
            // 防止value==null ==> "null"
            // 要考虑空串情况, 避免请求端和接收端行为不一致情况.
            if (v != null && !"".equals(v)) {
                // 保证尾部不留 &
                if (sb.length() > 0) {
                    sb.append("&");
                }
                // k,v都不为空时, 才参与签名
                sb.append(k).append("=").append(v);
            }
        }

        log.debug("待签名正文: {}",sb.toString());
        return sb.toString();
    }



    public static void main(String[] args) {
        System.out.println(TypeUtil.isSimple(HttpConst.Encryption.MD5));
        System.out.println(String.valueOf(HttpConst.Encryption.MD5));
    }


}
