package com.jfai.afs.http.utils;

import com.alibaba.fastjson.JSON;
import org.apache.commons.codec.binary.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.Charset;
import java.util.concurrent.ThreadLocalRandom;

/**
 * AES加密工具(2代, 可与JavaScript兼容)
 *
 * <p>
 *   SecretKeySpec 和 IvParameterSpec 所用的key和iv可以不同, 但都必须时<b>16</b>位字符. 但本工具类, 为了使用方便, 借用key作为iv.
 *   所以, 传参时, 不用管iv了.
 * </p>
 * <p>
 *   为了兼容JS, 全部加密都是 AES/CBC/ZeroPadding 128位模式. 使用16位密钥字符, 便是选择了128位模式
 * </p>
 *
 * <pre>
 *     v2.1
 *       - fix: PKCS5Padding下, 原文尾部填充了0
 *
 *     v2.0
 *       - 修改成和js兼容
 * </pre>
 * @author 玖富AI
 * @version v2.1
 * @date 2018/12/28 18:28
 */
public class AES {

    public static final String AES = "AES";
    private static final Charset UTF8 = Charset.forName("UTF-8");

    /**
     * 算法/模式/填充方式
     * <p>为了兼容</p>
     * <p>PKCS5Padding 和 JS框架中的 PKCS7 兼容. 有些js框架没有 NoPadding 类型. 所以, 这里的模式, 根据前台框架灵活选择.</p>
     */
//    public static final String ALG_MODE_PAD = "AES/CBC/NoPadding";
    public static final String ALG_MODE_PAD = "AES/CBC/PKCS5Padding";
//    public static final String ALG_MODE_PAD = "AES/ECB/PKCS5Padding";
    public static final int KEY_LEN = 16;
    public static final String[] STR_POOL = "1234567890qwertyuiopasdfghjklzxcvbnmQWERTYUIOPASDFGHJKLZXCVBNM".split("");

    /**
     * @param plntxt 待加密的明文
     * @param key 16位密钥字符
     * @return
     * @throws Exception
     */
    public static byte[] encryptToBytes(byte[] plntxt, String key) {
        if (plntxt==null) {
            return null;
        }
        validateKey(key);
        try {

            Cipher cipher = Cipher.getInstance(ALG_MODE_PAD);
            int blockSize = cipher.getBlockSize();      // 16

            //byte[] dataBytes = plntxt.getBytes();
//            int plaintextLength = plntxt.length;
//            if (plaintextLength % blockSize != 0) {
//                plaintextLength = plaintextLength + (blockSize - (plaintextLength % blockSize));
//            }

//            byte[] plaintext = new byte[plaintextLength];
//            System.arraycopy(plntxt, 0, plaintext, 0, plntxt.length);

            SecretKeySpec keyspec = new SecretKeySpec(key.getBytes(), AES);
            // 借用 key / CBC模式需要IV, ECB模式不支持IV
            IvParameterSpec ivspec = new IvParameterSpec(key.getBytes());

            cipher.init(Cipher.ENCRYPT_MODE, keyspec, ivspec);
            byte[] encrypted = cipher.doFinal(plntxt);

            //return Base64.encodeBase64URLSafeString(encrypted);     // 自动转为 utf8 string
            return encrypted;

        } catch (Exception e) {
            throw new RuntimeException("AES encrypt exception: ",e);
        }
    }

    public static String encryptToB64(String plntxt, String key) {
        byte[] enc = encryptToBytes(plntxt.getBytes(), key);
        if (enc != null) {
            return Base64.encodeBase64String(enc);
        }
        return null;
    }

    /**
     * <p>
     *     默认返回base64url字符
     * </p>
     * @param plntxt 待加密的明文
     * @param key 16位密钥字符
     * @return
     */
    public static String encrypt(byte[] plntxt, String key) {
        byte[] enc = encryptToBytes(plntxt, key);
        if (enc != null) {
            return Base64.encodeBase64URLSafeString(enc);
        }
        return null;
    }

    public static String encrypt(String plntxt, String key) {
        return encrypt(plntxt.getBytes(), key);
    }


    public static byte[] decryptToBytes(String encB64, String key) {
        if (encB64==null) {
            return null;
        }
        validateKey(key);
        try
        {
            byte[] enc = Base64.decodeBase64(encB64);

            Cipher cipher = Cipher.getInstance(ALG_MODE_PAD);
            SecretKeySpec keyspec = new SecretKeySpec(key.getBytes(), "AES");
            IvParameterSpec ivspec = new IvParameterSpec(key.getBytes());

            cipher.init(Cipher.DECRYPT_MODE, keyspec, ivspec);

            byte[] original = cipher.doFinal(enc);
            return original;
        }
        catch (Exception e) {
            throw new RuntimeException("AES decrypt exception: ",e);
        }
    }

    public static String decrypt(String encB64, String key) {
        byte[] original = decryptToBytes(encB64, key);
        String originalString = new String(original,UTF8);
        return originalString;
    }

    /**生成16位随机字符串作为AES密钥
     * @return
     */
    public static String genAesKey() {
        ThreadLocalRandom rd = ThreadLocalRandom.current();
        StringBuilder sb = new StringBuilder(KEY_LEN);
        for (int i = 0; i < KEY_LEN; i++) {
            sb.append(STR_POOL[rd.nextInt(STR_POOL.length)]);
        }
        return sb.toString();
    }


    /**验证 key 的有效性, 必须为 16 位字符串
     * @param key
     * @return
     */
    private static boolean validateKey(String key) {
        if (key == null || key.length() != 16) {
            throw new RuntimeException("Invalid AES key, must an string length of 16");
        }
        return true;
    }

    public static void main(String[] args) throws Exception {
        // key : x4hnmkJR4Q9ZJxbf
        //String key = genAesKey();
        String key = "x4hnmkJR4Q9ZJxbf";
        System.out.println(key);
//        String src = "haoyaiyo我们的大众要高旧0-7个老人更合理厚古薄今";
        String src = "{\"appKey\":\"AK526873337134075904\",\"comId\":\"XY\",\"timestamp\":1546508713555,\"uid\":\"526870234234105856\",\"uname\":\"sunliangzheng\"}";
//        String src = "haohaiyo";
        String enc = encrypt(src, key);
        System.out.println(enc);

        String dec = decrypt(enc, key);
        System.out.println(dec);
        JSON.parseObject(dec);
    }



}
