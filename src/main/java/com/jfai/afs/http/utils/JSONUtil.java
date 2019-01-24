package com.jfai.afs.http.utils;

import com.alibaba.fastjson.JSON;
import org.apache.commons.codec.binary.Base64;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

/**
 * @author Nisus Liu
 * @version 0.0.1
 * @email liuhejun108@163.com
 * @date 2019/1/21 17:20
 */
public class JSONUtil {

    public static final char TAB = '\t';
    public static final char LINE_SEP = '\n';
    public static final char ESCAPE = '\\';
    public static final char STR_WRAPPER = '"';

    private static String getLevelStr(int level) {
        StringBuffer levelStr = new StringBuffer();
        for (int levelI = 0; levelI < level; levelI++) {
            levelStr.append(TAB);
        }
        return levelStr.toString();
    }


    /**
     * 格式化json字符串
     *
     * @param s
     * @return String
     */
    @Contract(value = "null->null", pure = true)
    public static String formatJson(String s) {
        if (s == null) {
            return null;
        }

        boolean escaped = false;
        boolean strStart = false;
        // 标记是否进入{},[]复杂类型中, 进入后, 这是遇到','换行的前提之一
        boolean ens = false;
        int level = 0;
        //存放格式化的json字符串
        StringBuilder fmt = new StringBuilder();
        for (int index = 0; index < s.length(); index++)    //将字符串中的字符逐个按行输出
        {
            //获取s中的每个字符
            char c = s.charAt(index);
//          System.out.println(s.charAt(index));

            //level大于0并且jsonForMatStr中的最后一个字符为\n,jsonForMatStr加入\t
            if (level > 0 && LINE_SEP == fmt.charAt(fmt.length() - 1)) {
                fmt.append(getLevelStr(level));
//                System.out.println("123"+fmt);
            }
            //遇到"{"和"["要增加空格和换行，遇到"}"和"]"要减少空格，以对应，遇到","要换行
            if (c == ESCAPE) {
                if (escaped) {
                    // 多重转义符 \\
                    fmt.append(c);
                    escaped = false;
                }
                escaped = true;     // 标识下一个字符被转义了
                continue;
            } else {
                if (escaped) {
                    // 被转义的字符直接append
                    fmt.append(c);
                    escaped = false;
                } else {
                    // 遇到字符串里的 , 不换行
                    if (c == STR_WRAPPER) { // 真正的双引号, 没有被转义的
                        strStart = !strStart;
                    }

                    if (strStart) {
                        // 字符串内, 所有符号原样添加
                        fmt.append(c);
                    } else {
                        switch (c) {
                            case '{':
                            case '[':
                                fmt.append(c).append(LINE_SEP);
                                level++;
                                ens = true;
                                break;
                            case ',':
                                fmt.append(c);
                                // 进入复杂类型后, 才要换行
                                if (ens) {
                                    fmt.append(LINE_SEP);
                                }
                                break;
                            case '}':
                            case ']':
                                fmt.append(LINE_SEP);
                                level--;
                                fmt.append(getLevelStr(level));
                                fmt.append(c);
                                break;
                            default:
                                fmt.append(c);
                                break;
                        }
                    }

                }


            }


        }
        return fmt.toString();
    }


    /**
     * 对象转Json: 根据不同情形不同处理方式
     * <p>'简单类型'不会转换成Json string, 而是直接转成普通字符串.</p>
     * <i>虽然可以转换, 但是再反过来需要parseObject(string, type). 但是JavaScript不支持这种json解析.
     * 为了交互方便和兼容性, 这里仅将非'简单类型'转成json.</i>
     * <p>byte[]和Byte[]类型, 会转成{@link Base64}编码的字符.</p>
     * <p>是否是'基本类型'由{@link TypeUtil#isSimple(Object)}决定.</p>
     *
     * @param obj
     * @return
     */
    public static String toJsonAdapt(Object obj) {
        if (obj == null) {
            return null;
        }
        String data;
        if (TypeUtil.isSimple(obj)) {
            data = String.valueOf(obj);
        } else if (obj instanceof Byte[]) {
            data = Base64.encodeBase64String(TypeUtil.toBytes((Byte[]) obj));
        } else if (obj instanceof byte[]) {
            // 转成b64
            data = Base64.encodeBase64String((byte[]) obj);
            //throw new RuntimeException("Don't support byte[]");
        } else {
            data = JSON.toJSONString(obj);
        }
        return data;
    }


    public static void main(String[] args) {
        //json 字符串
        String s = "{\"code\":10000,\"msg\":null,\"data\":{\"id\":\"7aa0eb56-1026-4497-a42e-4c39f5e3dcf1\",\"topicId\":\"0876ab84-a478-417b-91bc-849843c191a5\",\"title\":null,\"commentId\":null,\"content\":\"" +
                "开发者平台自动化测试：针对帖子发表评论" +
                "\",\"images\":\"\",\"time\":\"2017-10-15 18:09:56\",\"userId\":\"61028f94-de92-4c65-aad3-2fc8614e1d34\",\"userName\":\"devautotest\",\"commentNum\":0,\"status\":0}}";
        String formated = formatJson(s);
        System.out.println(formated);

    }
}
