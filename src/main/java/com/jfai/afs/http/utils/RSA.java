package com.jfai.afs.http.utils;


import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import java.io.ByteArrayOutputStream;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.security.*;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

/**
 * <p>
 * --------------------------------------------**********--------------------------------------------<br/>
 * <p>
 * 该算法于1977年由美国麻省理工学院MIT(Massachusetts Institute of Technology)的Ronal Rivest，Adi Shamir和Len Adleman三位年轻教授提出，并以三人的姓氏Rivest，Shamir和Adlernan命名为RSA算法，是一个支持变长密钥的公共密钥算法，需要加密的文件快的长度也是可变的!
 * <p>
 * 所谓RSA加密算法，是世界上第一个非对称加密算法，也是数论的第一个实际应用。它的算法如下：
 * <p>
 * 1.找两个非常大的质数p和q（通常p和q都有155十进制位或都有512十进制位）并计算n=pq，k=(p-1)(q-1)。
 * <p>
 * 2.将明文编码成整数M，保证M不小于0但是小于n。
 * <p>
 * 3.任取一个整数e，保证e和k互质，而且e不小于0但是小于k。加密钥匙（称作公钥）是(e, n)。
 * <p>
 * 4.找到一个整数d，使得ed除以k的余数是1（只要e和n满足上面条件，d肯定存在）。解密钥匙（称作密钥）是(d, n)。
 * <p>
 * 加密过程： 加密后的编码C等于M的e次方除以n所得的余数。
 * <p>
 * 解密过程： 解密后的编码N等于C的d次方除以n所得的余数。
 * <p>
 * 只要e、d和n满足上面给定的条件。M等于N。<br/><br/>
 *
 * <p>
 *     注意传入的参数都是 Base64(Url) 编码字符
 * </p>
 *
 * <p>
 * -------------------------------------------- Version Log --------------------------------------------
 * </p>
 * <pre>
 *     v1.2
 *       - 增加 parsePem 逻辑, 支持带pem头的密钥字符串
 *       - 抽取部分共用代码
 *     v1.1
 *       - Base64Url编码(encodeBase64URLSafe())
 * </pre>
 * @author 玖富AI
 * @version 1.2
 */

public class RSA {
    public static final Logger log = LoggerFactory.getLogger(RSA.class);

    private static final String RSA = "RSA";
    private static final String SHA1WithRSA = "SHA1WithRSA";
    //private static final Base64.Encoder BASE64ENCODER = Base64.getEncoder();
    //private static final Base64.Decoder BASE64DECODER = Base64.getDecoder();
    private static final Charset UTF8 = Charset.forName("UTF-8");
    private static final String PEM_HEADER_ID_PKCS8_PRIV = "PRIVATE KEY";
    private static final String PEM_HEADER_ID_PKCS8_PUB = "PUBLIC KEY";

    /**
     * 指定key的大小
     */
    private static final int KEY_SIZE = 1024;

    /**
     * RSA最大加密明文大小, ==KEY_SIZE/8-11
     */
    private static final int MAX_ENCRYPT_BLOCK = 117;

    /**
     * RSA最大解密密文大小
     */
    private static final int MAX_DECRYPT_BLOCK = 128;

    /**
     * 生成密钥对
     */
    public static RSAInfo generateKeyPair() {
        /* RSA算法要求有一个可信任的随机数源 */
        SecureRandom sr = new SecureRandom();
        /* 为RSA算法创建一个KeyPairGenerator对象 */
        KeyPairGenerator kpg = null;
        try {
            kpg = KeyPairGenerator.getInstance(RSA);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        /* 利用上面的随机数据源初始化这个KeyPairGenerator对象 */
        kpg.initialize(KEY_SIZE, sr);
        /* 生成密匙对 */
        KeyPair kp = kpg.generateKeyPair();
		/* 得到公钥 */
        Key publicKey = kp.getPublic();
        byte[] publicKeyBytes = publicKey.getEncoded();
        //转成指定编码格式的base64字符串
        String pub = new String(Base64.encodeBase64URLSafe(publicKeyBytes), UTF8);
		/* 得到私钥 */
        Key privateKey = kp.getPrivate();
        byte[] privateKeyBytes = privateKey.getEncoded();
        String pri = new String(Base64.encodeBase64URLSafe(privateKeyBytes),
                UTF8);

//		Map<String, String> map = new HashMap<String, String>();
//		map.put("publicKey", pub);
//		map.put("privateKey", pri);
        RSAInfo info = new RSAInfo();
        info.PUBLIC_KEY = pub;
        info.PRIVATE_KEY = pri;

        RSAPublicKey rsp = (RSAPublicKey) kp.getPublic();
        BigInteger bint = rsp.getModulus();
        byte[] b = bint.toByteArray();
        byte[] deBase64Value = Base64.encodeBase64URLSafe(b);
        //map.put("modulus", retValue);
        info.MODULUS = new String(deBase64Value, UTF8);
        return info;
    }

    @Deprecated
    public static RSAInfo generateKeyPairBase64() {
        /* RSA算法要求有一个可信任的随机数源 */
        SecureRandom sr = new SecureRandom();
        /* 为RSA算法创建一个KeyPairGenerator对象 */
        KeyPairGenerator kpg = null;
        try {
            kpg = KeyPairGenerator.getInstance(RSA);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        /* 利用上面的随机数据源初始化这个KeyPairGenerator对象 */
        kpg.initialize(KEY_SIZE, sr);
        /* 生成密匙对 */
        KeyPair kp = kpg.generateKeyPair();
        /* 得到公钥 */
        Key publicKey = kp.getPublic();
        byte[] publicKeyBytes = publicKey.getEncoded();
        //转成指定编码格式的base64字符串
        String pub = new String(Base64.encodeBase64(publicKeyBytes), UTF8);
        /* 得到私钥 */
        Key privateKey = kp.getPrivate();
        byte[] privateKeyBytes = privateKey.getEncoded();
        String pri = new String(Base64.encodeBase64(privateKeyBytes),
                UTF8);

//		Map<String, String> map = new HashMap<String, String>();
//		map.put("publicKey", pub);
//		map.put("privateKey", pri);
        RSAInfo info = new RSAInfo();
        info.PUBLIC_KEY = pub;
        info.PRIVATE_KEY = pri;

        RSAPublicKey rsp = (RSAPublicKey) kp.getPublic();
        BigInteger bint = rsp.getModulus();
        byte[] b = bint.toByteArray();
        byte[] deBase64Value = Base64.encodeBase64(b);
        //map.put("modulus", retValue);
        info.MODULUS = new String(deBase64Value, UTF8);
        return info;
    }


    public static String encryptByPrivateKey(String source, String privateKey)
            throws Exception {
        Key key = getPrivateKey(privateKey);
	        /* 得到Cipher对象来实现对源数据的RSA加密 */
        //Cipher cipher = Cipher.getInstance(ConfigureEncryptAndDecrypt.RSA_ALGORITHM);
        Cipher cipher = Cipher.getInstance(RSA);
        cipher.init(Cipher.ENCRYPT_MODE, key);
        byte[] data = source.getBytes(UTF8);

        int inputLen = data.length;
        ByteArrayOutputStream out = blocking(cipher, data, MAX_ENCRYPT_BLOCK);

        /** 执行加密操作 */
        // byte[] b1 = cipher.doFinal(b);
        return new String(Base64.encodeBase64URLSafe(out.toByteArray()),
                UTF8);

    }


    /**
     * 加密方法 source： 源数据
     *
     * @param publicKey 公钥base64格式字符串
     */
    public static String encrypt(String source, String publicKey)
            throws InvalidKeySpecException, InvalidKeyException {
        Key key = getPublicKey(publicKey);
        /* 得到Cipher对象来实现对源数据的RSA加密 */
//        Cipher cipher = Cipher.getInstance(ConfigureEncryptAndDecrypt.RSA_ALGORITHM);
        Cipher cipher = null;
        try {
            cipher = Cipher.getInstance(RSA);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            throw new RuntimeException(e);
        }
        cipher.init(Cipher.ENCRYPT_MODE, key);
        byte[] data = source.getBytes(UTF8);


        // 对数据分段加密
        ByteArrayOutputStream out = blocking(cipher, data, MAX_ENCRYPT_BLOCK);

        /* 执行加密操作 */
        // byte[] b1 = cipher.doFinal(b);
//        return new String(BASE64ENCODER.encode(out.toByteArray()),CHARSET);
        return new String(Base64.encodeBase64URLSafe(out.toByteArray()), UTF8);
    }

    /**
     * 分段加密/解密, 指定块大小
     *
     * @param cipher
     * @param data
     * @param maxBlock
     * @return
     */
    private static ByteArrayOutputStream blocking(Cipher cipher, byte[] data, int maxBlock) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int offSet = 0;
        byte[] cache;
        int i = 0;
        int inputLen = data.length;
        while (inputLen - offSet > 0) {
            if (inputLen - offSet > maxBlock) {
                try {
                    cache = cipher.doFinal(data, offSet, maxBlock);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            } else {
                try {
                    cache = cipher.doFinal(data, offSet, inputLen - offSet);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
            out.write(cache, 0, cache.length);
            i++;
            offSet = i * maxBlock;
        }
        return out;
    }

    /**
     * 解密算法 cryptograph:密文
     *
     * @param cryptograph base64编码的密文.
     * @param privateKey  base64编码的私钥字符串.
     */
    public static String decrypt(String cryptograph, String privateKey) throws InvalidKeySpecException, InvalidKeyException {
        //InvalidKeySpecException => maybe私钥字符串无效
        Key key = getPrivateKey(privateKey);
        /* 得到Cipher对象对已用公钥加密的数据进行RSA解密 */
        Cipher cipher = null;
        try {
            cipher = Cipher.getInstance(RSA);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            throw new RuntimeException(e);
        }

        //InvalidKeyException
        cipher.init(Cipher.DECRYPT_MODE, key);
        //byte[] encryptedData = BASE64DECODER.decode(cryptograph.getBytes(CHARSET));        //原: 没有编码指定
        byte[] encryptedData = Base64.decodeBase64(cryptograph.getBytes(UTF8));

        // 对数据分段解密
        ByteArrayOutputStream out = blocking(cipher, encryptedData, MAX_DECRYPT_BLOCK);
        byte[] decryptedData = out.toByteArray();

        /* 执行解密操作 */
        // byte[] b = cipher.doFinal(b1);
        return new String(decryptedData, UTF8);
    }


    public static String decryptByPublicKey(String cryptograph, String publicKey)
            throws Exception {
        Key key = getPublicKey(publicKey);
            /* 得到Cipher对象对已用公钥加密的数据进行RSA解密 */
        Cipher cipher = Cipher.getInstance(RSA);
        cipher.init(Cipher.DECRYPT_MODE, key);
        //原文(base编码的)base64解码
//        byte[] encryptedData = BASE64DECODER.decode(cryptograph.getBytes(CHARSET));        //原: 没有指定编码
        byte[] encryptedData = Base64.decodeBase64(cryptograph.getBytes(UTF8));        //原: 没有指定编码

        int inputLen = encryptedData.length;
        ByteArrayOutputStream out = blocking(cipher, encryptedData, MAX_DECRYPT_BLOCK);
        byte[] decryptedData = out.toByteArray();

            /* 执行解密操作 */
        // byte[] b = cipher.doFinal(b1);
        return new String(decryptedData, UTF8);
    }


    /**
     * 得到公钥
     *
     * @param key 密钥字符串(经过base64编码), 且为utf-8编码的字符串.
     * @throws Exception InvalidKeySpecException => maybe所传入的公钥字符串非法
     */
    public static PublicKey getPublicKey(String key) throws InvalidKeySpecException {
        key=cleanPem(key,PEM_HEADER_ID_PKCS8_PUB);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(
                Base64.decodeBase64(key.getBytes(UTF8)));        //CHARSET, 原: key.getBytes()
        KeyFactory keyFactory = null;
        try {
            keyFactory = KeyFactory.getInstance(RSA);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        return keyFactory.generatePublic(keySpec);
    }



    /**
     * 得到私钥
     *
     * @param key 密钥字符串(经过base64编码), 且为utf-8编码的字符串.
     * @throws Exception
     */
    public static PrivateKey getPrivateKey(String key) throws InvalidKeySpecException {
        key = cleanPem(key, PEM_HEADER_ID_PKCS8_PRIV);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(
                Base64.decodeBase64(key.getBytes(UTF8)));
        KeyFactory keyFactory = null;
        try {
            keyFactory = KeyFactory.getInstance(RSA);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        return keyFactory.generatePrivate(keySpec);
    }

    private static String cleanPem(String key, String pemId) {
        if (StringUtils.isBlank(key)) {
            throw new IllegalArgumentException("`key` is NULL, not allowed");
        }
        // 剔除开头和结束标记, 若有
        if (pemId != null) {
            key = key.replace("-----BEGIN " + pemId + "-----", "");
            key = key.replace("-----END " + pemId + "-----", "");
        }else{
            key = key.replace("-----BEGIN [^-]+-----", "");
            key = key.replace("-----END [^-]+-----", "");
        }

        // 剔除空白字符, 如\n
        key=key.replaceAll("\r|\n|\\s", "");
        return key;
    }

    /**
     * 对文本内容签名
     *
     * @param content
     * @param privateKey
     * @return
     */
    public static String sign(String content, String privateKey) throws InvalidKeySpecException, InvalidKeyException, SignatureException {
//        privateKey = cleanPem(privateKey, PEM_HEADER_ID_PKCS8_PRIV);
//        PKCS8EncodedKeySpec priPKCS8 = new PKCS8EncodedKeySpec(
//                Base64.decodeBase64(privateKey.getBytes(UTF8)));
//        KeyFactory keyf = null;
//        try {
//            keyf = KeyFactory.getInstance(RSA);
//        } catch (NoSuchAlgorithmException e) {
//            throw new RuntimeException(e);
//        }
//        PrivateKey priKey = keyf.generatePrivate(priPKCS8);
        PrivateKey priKey = getPrivateKey(privateKey);

        Signature signature = null;
        try {
            signature = Signature.getInstance(SHA1WithRSA);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }

        signature.initSign(priKey);
        signature.update(content.getBytes(UTF8));

        byte[] signed = signature.sign();

        return new String(Base64.encodeBase64URLSafe(signed), UTF8);
    }

    public static boolean checkSign(String content, String sign, String publicKey) throws InvalidKeySpecException, InvalidKeyException, SignatureException {
        log.debug("content: {}, sign:{}, publicKey: {}", content, sign, publicKey);
//        KeyFactory keyFactory = null;
//        try {
//            keyFactory = KeyFactory.getInstance(RSA);
//        } catch (NoSuchAlgorithmException e) {
//            throw new RuntimeException(e);
//        }
//
//        publicKey = cleanPem(publicKey, PEM_HEADER_ID_PKCS8_PUB);
//
//        byte[] encodedKey = Base64.decodeBase64(publicKey.getBytes(UTF8));
//        //InvalidKeySpecException => maybe所传入的公钥字符串非法
//        PublicKey pubKey = keyFactory.generatePublic(new X509EncodedKeySpec(encodedKey));
        PublicKey pubKey = getPublicKey(publicKey);


        Signature signature = null;
        try {
            signature = Signature
                    .getInstance(SHA1WithRSA);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }

        //InvalidKeyException
        signature.initVerify(pubKey);
        //SignatureException
        signature.update(content.getBytes(UTF8));

        return signature.verify(Base64.decodeBase64(sign.getBytes(UTF8)));
    }


    /**
     * 存储Base64编码后的rsa公私钥字符串信息.
     */
    public static class RSAInfo {
        public String PUBLIC_KEY;
        public String PRIVATE_KEY;
        public String MODULUS;

        /**PEM格式中, PKCS #8 不带 RSA 字样, PKCS #1 带 RSA 字样
         * @return
         */
        @Override
        public String toString() {
            return "-----BEGIN PUBLIC KEY-----\n" + PUBLIC_KEY + "\n-----END PUBLIC KEY-----\n\n" +
                    "-----BEGIN PRIVATE KEY-----\n" + PRIVATE_KEY + "\n-----END PRIVATE KEY-----\n\n"/* +
                    "-----BEGIN MODULUS-----\n" + MODULUS + "\n-----END MODULUS-----\n\n"*/;
        }

        /**
         * 默认生成 Base64 Url格式的密钥对.
         * 此方法可以转换回 base64 格式
         */
        public RSAInfo toB64() {
            this.PUBLIC_KEY = Base64Util.b64utob64(this.PUBLIC_KEY);
            this.PRIVATE_KEY = Base64Util.b64utob64(this.PRIVATE_KEY);
            this.MODULUS = Base64Util.b64utob64(this.MODULUS);
            return this;
        }
    }


    public static void main(String[] args) throws Exception {
        String src = "好厉害";
        String pk = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQC2MmSduKbKTrYCpvFA6CfHCtwaB1L7l4lp4s0BSQf7H5TCAXN980UZjyikAsmwCmFS/U6J1tAoR8wTdccp25LgMglEFj1R9mO8EYLUNrgMbKVnoNJp92X+ipRvmmEzrk9NEECYFprc8HrcTgUxBeNIGblefUUv/HrUP1wyMhPV3wIDAQAB";
        String priK = "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBALYyZJ24pspOtgKm8UDoJ8cK3BoHUvuXiWnizQFJB/sflMIBc33zRRmPKKQCybAKYVL9TonW0ChHzBN1xynbkuAyCUQWPVH2Y7wRgtQ2uAxspWeg0mn3Zf6KlG+aYTOuT00QQJgWmtzwetxOBTEF40gZuV59RS/8etQ/XDIyE9XfAgMBAAECgYBWLdnnvIqwELEI6hwdPnJqNJnNef9nX9MwkYW0gO3Ue6iqZ3NQdPwPTebERUlPCmnkjkpS6pfFJcjCv6f4BZMfylspzUS/D8+c8MM2dPyWQPBmQvV7NI0khO81rC+rO/0PwWtDRRaawYyCWtdKkePaOhKiLTm31lPGA1LoEzX+GQJBANkeT6mLlQg7bieceiTu8JS4uX4d8U09TWO5+fWsT6ThEuQIg1UWR0KuwO0hqpZK3XDWWePVi5taGAUhruTSn6MCQQDW0x60JA4r0RtP0IouTR2/LVuZThMvoJHAwgKG+UqCUEbBJDKVBec2TijxqyJE5XUoG4cVB/mJ9/HOhAzPbiSVAkEAiWqU0uMkPSSYMHvqFswf+pD73eWHnvJIs/C2UpZvhhRqB2eX79d83KiMNMmVFTBm/Q69r+StSnL4Nu89qVfhbwJAaDzY0SANoEU+s2PFKOJSZMllgOXZeWq4TofVIhkkhkbdjio6QQU0Q615QIqBfly4lFWHHjVYaBHfSw3YCW6wYQJBAJs6VaoRL+V+NYX5FlISEwnsDmalsoyqRidBTINaTGGZv6MkkIURbG8y6yjrNGV9hQWn1opxF1XRc6eL/D5Ypj4=";
        String cryptograph = "YwKRblxSjW5WUC6q4rLVtXVEVNqkH5Sgc09gnF9JTY1C3IlaX8sCythbDMFacAMYdGoR9+7RiOJh9vC9W2AzfQDUAJrCm4Js4VMSLMPM1UpuBb93LtcW5w6ih4s5b2PZ7nZpp4Kdhu/tNkjR3HYhPr8lztIbrr0E75QTRiSmUJU=";

        //System.out.println(generateKeyPairBase64());
//        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(
//                Base64.decodeBase64(pk.getBytes(UTF8)));        //CHARSET, 原: key.getBytes()
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(
                Base64.decodeBase64(pk.getBytes(UTF8)));        //CHARSET, 原: key.getBytes()
        KeyFactory keyFactory = null;
        try {
            keyFactory = KeyFactory.getInstance(RSA);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }


        Key key = keyFactory.generatePublic(keySpec);
        /* 得到Cipher对象对已用公钥加密的数据进行RSA解密 */
        Cipher cipher = Cipher.getInstance(RSA);
        cipher.init(Cipher.DECRYPT_MODE, key);
        //原文(base编码的)base64解码
//        byte[] encryptedData = BASE64DECODER.decode(cryptograph.getBytes(CHARSET));        //原: 没有指定编码
        byte[] encryptedData = Base64.decodeBase64(cryptograph.getBytes(UTF8));        //原: 没有指定编码

        int inputLen = encryptedData.length;
        ByteArrayOutputStream out = blocking(cipher, encryptedData, MAX_DECRYPT_BLOCK);
        byte[] decryptedData = out.toByteArray();

        /* 执行解密操作 */
        // byte[] b = cipher.doFinal(b1);
        String dec = new String(decryptedData, UTF8);
        System.out.println(dec);
    }

}