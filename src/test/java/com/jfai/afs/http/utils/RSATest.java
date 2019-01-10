package com.jfai.afs.http.utils;

import org.junit.Test;

import java.security.InvalidKeyException;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;

import static org.junit.Assert.*;

public class RSATest {

    @Test
    public void fun7() throws InvalidKeySpecException, InvalidKeyException, SignatureException {

        boolean b = RSA.checkSign("data=k3ubRthvzR+PdVfxRt2JuHOjmBpxrkvtMVHepGn6xVWX7/MnZdHL+R8+UqiNSU5lmGqMytS7vuyy54fsn1IQpg==&sessionId=5480E69BFFAB977D74386CB163707F7F&sessionKey=WtrdkWkJpB_uy2CRRj8CNwgIFrgLBsSSzvNDTyfJybrLN5icLwx2hAYUeVc9r7-63wDpIUWgsKklHYfkhfsz6cGpPitgx3iGuXUWtzrQ3coxnzEPBrWCgm_wVwBy95qtc-4gdDoJC0aLzxS7nHHptxVh48kn_Wh0PMmeRJWD3Wo&timestamp=1546914118000&token=7w9XJ4kWUwrBAuaSD4cGHpFoOF5y6L09XVdyuXy17FV2Z1W8mXf_tut0-gJ_K36iWEYJcpcOoE56xHYQdTh0u3CWFvQrY5AhlKdfjoRzo6uxBZ5WeVZG8Vc2mYEGazIqtF-BOA3hHTMtfbBUatUrq5nE5PcE5T_QXL7LU9dHA4G8aSHAMqJW4dCaosSJzamv7weMuZoIsABHkbtuw-H7RNm5XZTZXbmH5SmtM_hmvZE", "OGAjbIIHUXhN7_Q8BuK5RVp5LP0RgORnbRxvr_NiaDCZWTPG9rvTUPjHpjgakXupoc0Yslx23StGB4MQrcmOji0x8fXZSi6Qi9bMIJ-YJf0mQgJT7daHdouaO6dMU8owYJq2GwuP6Bci2MtW963M7LalWZaJYUMOSYQ5GeYNNcI", "-----BEGIN PUBLIC KEY-----\n" +
                "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQC927WqTYdREy2BKiLVr02lPLS3\n" +
                "a/cT63XSa4IsNfa5FFLXx60h3k1y6Emq8MNxJOMLcv97okCIEObsNKCIH/NTSdu6\n" +
                "FwA1n2lJsv9xeMTAy5++3BtdiAtp9vq24TH20tg42lkCn4OYdcc9dc0BZ64/Z+zd\n" +
                "Fs0QlL3dr7l8SR6NJQIDAQAB\n" +
                "-----END PUBLIC KEY-----");

        System.out.println(b);

    }

    @Test
    public void genPair(){
        RSA.RSAInfo rsaInfo = RSA.generateKeyPair().toB64();
        System.out.println(rsaInfo);
    }

    @Test
    public void testSign() throws InvalidKeySpecException, InvalidKeyException, SignatureException {
        String src = "code=0&data=8evA5YU0uP7I2LpkrpkPSMlBFYsQxOiQzBBVE00v41oq-X2w28XlGP496XzG0L8i6G7Ags-zzObN5P-xa1cgFVkdxyClAQBBtUVt4i6J91WyTZy2D6V8myOzfv3sojRXWe3IaCsBAWHEU-0nCFJfrhpH4zGblqx56cEQLSIDmAiICEfYoBy7pE6IgZvhUacd0hkKpEvBuZh1q2q10Wzcbvz4C4yDq_R-Vth4PO35UJ5LGDly19CNoO290U9d-LlOgX89tkYtvXi_wkladb-EgDk0495RZXwl-ZGwlUfOwwC79uqzVsZewNlNHQQk-x-SixgXPEp6jB3V5U7tmjWdwrD2ox4a7Ue16LVWXfWPewNJ2ShfXWGRvZnezARSkpzW&encryption=true&message=SUCCESS&requestId=531799444375482368_20190107114107.895&sessionKey=Yn1rlGXv5e8xgoozQKS7aI5tjrLzMXkYE-IJZqmEigSoto87w7aPjiWqtOuuXwrfplC4zftCoSO88bgjTBI9QMCZbc1vel_VcKaHkGdqmHPDVobqJRNZ1CBYDpDDYWHCHl27J5KA0MkqzIHxnC6saaPeZcjNnMVEz9qqcdrSTDA&spendTime=16&startTime=1546832467895";
        String spub = "-----BEGIN PUBLIC KEY-----\n" +
                "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDHZKWKgbksDvN6fNsFpUYrGa85C2N58guHlriM+nuIJUCD/CbDcoRjh+QJGAAFMQI6ZX/QAMbl0uR0avnan4jIhxXJBFs1SK13YkE8Ki3geW0swgjHk/phnfrqviTY/BrFZOVzlY+T7D/mi53PETR3zSToZ0q1pVuHX/XPjmnexQIDAQAB" +
                "-----END PUBLIC KEY-----";
        String sprv = "MIICdQIBADANBgkqhkiG9w0BAQEFAASCAl8wggJbAgEAAoGBAMdkpYqBuSwO83p82wWlRisZrzkLY3nyC4eWuIz6e4glQIP8JsNyhGOH5AkYAAUxAjplf9AAxuXS5HRq+dqfiMiHFckEWzVIrXdiQTwqLeB5bSzCCMeT+mGd+uq+JNj8GsVk5XOVj5PsP+aLnc8RNHfNJOhnSrWlW4df9c+Oad7FAgMBAAECgYBg8EejHQpfpy0sYcNB7WY90hDkVOsaAFkmpFo5ABZKzIGfKVnXL7e5g/mTij2ISJlitrH6+EW6ulsjVlb+uQ0QRR13cQzNlcZVHzwjP5S5fu3g4ja5P5IW19DhPftzG8bXSkeGtBlZS2Elzq43hvIKjqQwgrSv/Xial0Yl89KJiQJBAOILKbUwEmckJCY0OGDrOVmsW7V0I93DiBhbg43xtUGmXDIxoNIW9ovZtpn5vekrcPCUsdhKKdFGaFU2vR4QP5sCQQDh0VSt5SIfAI1YOeyyZNsQExfJE5+YfcrV2+EhmnC4+gz7wUPxUQJA9s0q1NHRZKA0lb9uAJKf3ltJaSO+6LEfAkAVJJooGnD+OMEw2Z2UYywgGSES3eQk8A8gyB6L/lnH5DmncEDspb01441S5as/zhJoTmA8LOOwcXd80PvrffWVAkA2WuvWG8RV0A01wmOeITa1rNLZ3LZav1S2m4GaVBF0BQRhCQkwqf4v4vhJqgaDL4R8g9sbQFoKzzwL9JndFyejAkAMNgEgrU1MI2DMX0rzg9yqCoJIo55SH1YF4ZcasxmOfjL0e5ZLey9/bNpLhejBFVxSkFgUveOOac6NnTQLpsVb";

        String sign = RSA.sign(src, sprv);
        System.out.println(sign);

        System.out.println(RSA.checkSign(src, sign, spub));

    }


    @Test
    public void fun3() throws InvalidKeySpecException, InvalidKeyException, SignatureException {
        String spubk = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCADKetUrAGeEoSWHG3iCSzPn3mETGmxDzoCYFHtC/rvB3xJFOBxHi9hRDTYRbZZCrVvr+C4uu/y1QMi3JCc8oUJsfqrGtQGFPCRVHFhmCnt+emjJrQDR52BxQ4WKwkdEZNlId0KWxhmMby+37pUFvKwEnebfBPhPAACjeY5E5zIQIDAQAB";
        String sprvk = "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBAIAMp61SsAZ4ShJYcbeIJLM+feYRMabEPOgJgUe0L+u8HfEkU4HEeL2FENNhFtlkKtW+v4Li67/LVAyLckJzyhQmx+qsa1AYU8JFUcWGYKe356aMmtANHnYHFDhYrCR0Rk2Uh3QpbGGYxvL7fulQW8rASd5t8E+E8AAKN5jkTnMhAgMBAAECgYBfaA5vB5L73xhhfuCLvLaLjt5DU0BJXyF6Scq3YDvsSqeMimJl57uGieS1ejuKEiYQSCVtIiJBLwAasLOba6Z+OxDenM/BYYEjPDPtZozq75E3N+W6Uhfrjz5XpTtd1zCHfDAUZqZ3hwSWxndrayAFXnQLg0YWlWEF8YoA6R9WgQJBAL7j+YauRgxaCaqlMHtwfGDwpvHy1s+cABOfzBuaE/Nt65E5cqudgWOu0bgIICtvHm9iZKCHKIkfTTeGto5EXvkCQQCruZKEy/gposXMWQFoCTYFkPNqD+HhPLl1ycznwaLa4Y3T1EyYHy6mq8krppbNGiKRQYOD8SM7kXmyIlqzvDdpAkEAsQyfljQzkic8igK2yfbV1c++9++lH1/wjkMLO5qX4JNWBxdCbTwkE0HFECyxMbfZgbO/40gY572Zj+OplKCDeQJBAJOKLfOAl2eWXSEz+3xaJZrkre4bVTPCi4lC55TbDkPGdKdiCKjOabNzdrTVPU1cvgRU6FzNKjs8wvUkhQ9Vj7kCQCn3GmcE/BFeIX+RjQ1vvMHz45Bsq8JWYcBzf3f/2m1W2jC6Mf6cCG1czkJXq2up+a9XOpGqN57pTlaCvLKyJLY=";
        String src = "haoyaiyo, 感觉人生已经达到了高潮";
        String sign = RSA.sign(src, sprvk);
        System.out.println(sign);

    }


    @Test
    public void fun2() throws InvalidKeySpecException, InvalidKeyException, SignatureException {

        String src = "haoyaiyo, 感觉人生已经达到了高潮";
        String sign = RSA.sign(src, "-----BEGIN PRIVATE KEY-----\n" +
                "MIICdAIBADANBgkqhkiG9w0BAQEFAASCAl4wggJaAgEAAoGAYy5V+zJCcLk2mkUc\n" +
                "3Q9tXuuB7hSKsdIn3L5KRtJbcbway11UH6M8IErLF57HFbZVwESH2hggB4KCjuOb\n" +
                "xVwsqbkc64gkrjlgblYKG8FzX4QkrZbcHC7JA7j3B8fGuPS25yuqCVbtnVAMq7u0\n" +
                "yANzszGlL3uIOuv/IOFrzW60JR0CAwEAAQKBgCxy6K/CL4qLRxBOZwgfUNnYXAuS\n" +
                "AzJ8R8cjNPCWd8r6DVwUwI3JPzoNqwUz8G8nnziiveudIkWbVN2q3fXOQGlkkP+Z\n" +
                "SouAcGDIgOxdW50IY39VbgJJti2RlCSX3nY67xDfUzjKUeEljOXWhT6TusrBuA/W\n" +
                "BfJFWlxa4vbexyOBAkEArrz3GYkk4kAMzrrWuwJybLdT4OSuqhdDnazHzI18Oc1Q\n" +
                "YNOt+hZKaeXrsx3TXWTqIkXlBwNl3vgVNRzDN7+xYQJBAJFOGBiCRTcxGu3owJK6\n" +
                "BAp6qYz9Pm++22GfYxkr6w6XzIy5yNlugAtph4PMqc3Mw/ubEdE71R41yuAmMs7F\n" +
                "gT0CQCdGzFgODp9YfEh/kMKqUyQCtTTo0iXjYsxvSN2+s7yeXMHW8tUy63kQOEGj\n" +
                "o53rkAEyKr+/0MckhkfQ9kRyryECQCR982k8Vju9NmEngi2XT+p90dUZyLNxwFDt\n" +
                "IYsxz2+zyehRxFJvAPNcxm7gWIjZ8yJeWIQuvaA/rDrM/ReW3JUCQDJZVgG6kdcr\n" +
                "xnd94gC/kPwHb4z+lfif7QqT6W3C7sVQrITBSPba/ipRNV50TDbRUeK/PF1d48IE\n" +
                "Fu44Hk+uo/s=\n" +
                "-----END PRIVATE KEY-----");

        System.out.println(sign);
        System.out.println(Base64Util.b64utob64(sign));


    }


    @Test
    public void fun1() throws InvalidKeySpecException, InvalidKeyException {

        String src = "haoyaiyo, 干掘人生已经达到了高潮";
        String enc = RSA.encrypt(src, "-----BEGIN PUBLIC KEY-----\n" +
                "MIGeMA0GCSqGSIb3DQEBAQUAA4GMADCBiAKBgGMuVfsyQnC5NppFHN0PbV7rge4U\n" +
                "irHSJ9y+SkbSW3G8GstdVB+jPCBKyxeexxW2VcBEh9oYIAeCgo7jm8VcLKm5HOuI\n" +
                "JK45YG5WChvBc1+EJK2W3BwuyQO49wfHxrj0tucrqglW7Z1QDKu7tMgDc7MxpS97\n" +
                "iDrr/yDha81utCUdAgMBAAE=\n" +
                "-----END PUBLIC KEY-----");
        System.out.println(enc);

        String dec = RSA.decrypt(enc, "-----BEGIN PRIVATE KEY-----\n" +
                "MIICdAIBADANBgkqhkiG9w0BAQEFAASCAl4wggJaAgEAAoGAYy5V+zJCcLk2mkUc\n" +
                "3Q9tXuuB7hSKsdIn3L5KRtJbcbway11UH6M8IErLF57HFbZVwESH2hggB4KCjuOb\n" +
                "xVwsqbkc64gkrjlgblYKG8FzX4QkrZbcHC7JA7j3B8fGuPS25yuqCVbtnVAMq7u0\n" +
                "yANzszGlL3uIOuv/IOFrzW60JR0CAwEAAQKBgCxy6K/CL4qLRxBOZwgfUNnYXAuS\n" +
                "AzJ8R8cjNPCWd8r6DVwUwI3JPzoNqwUz8G8nnziiveudIkWbVN2q3fXOQGlkkP+Z\n" +
                "SouAcGDIgOxdW50IY39VbgJJti2RlCSX3nY67xDfUzjKUeEljOXWhT6TusrBuA/W\n" +
                "BfJFWlxa4vbexyOBAkEArrz3GYkk4kAMzrrWuwJybLdT4OSuqhdDnazHzI18Oc1Q\n" +
                "YNOt+hZKaeXrsx3TXWTqIkXlBwNl3vgVNRzDN7+xYQJBAJFOGBiCRTcxGu3owJK6\n" +
                "BAp6qYz9Pm++22GfYxkr6w6XzIy5yNlugAtph4PMqc3Mw/ubEdE71R41yuAmMs7F\n" +
                "gT0CQCdGzFgODp9YfEh/kMKqUyQCtTTo0iXjYsxvSN2+s7yeXMHW8tUy63kQOEGj\n" +
                "o53rkAEyKr+/0MckhkfQ9kRyryECQCR982k8Vju9NmEngi2XT+p90dUZyLNxwFDt\n" +
                "IYsxz2+zyehRxFJvAPNcxm7gWIjZ8yJeWIQuvaA/rDrM/ReW3JUCQDJZVgG6kdcr\n" +
                "xnd94gC/kPwHb4z+lfif7QqT6W3C7sVQrITBSPba/ipRNV50TDbRUeK/PF1d48IE\n" +
                "Fu44Hk+uo/s=\n" +
                "-----END PRIVATE KEY-----");
        System.out.println(dec);

    }

}