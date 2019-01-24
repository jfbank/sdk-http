package com.jfai.afs.http.utils;

import com.alibaba.fastjson.JSON;

/**
 * 通用零散工具
 *
 * @author Nisus Liu
 * @version 0.0.1
 * @email liuhejun108@163.com
 * @date 2019/1/15 16:44
 */
public class CommonUtil {

    /**
     *
     * @param obj
     * @return
     */
    public static String getJsonString(Object obj) {
        if (obj == null) {
            return null;
        }
        //region 基本类型也toJson --Nisus Liu 2019/1/17 18:34
        /*
        String data;
        if (CommonUtil.isPrimitive(obj)) {
             data = String.valueOf(obj);
         } else
        if (obj instanceof Byte[] || obj instanceof byte[]) {
            throw new RuntimeException("Don't support byte[]");
        } else {
            data = JSON.toJSONString(obj);
        }
         */
        //endregion
        return JSON.toJSONString(obj);
    }

    /**
     * 判断是否是"基本类型"
     * <p>
     *     Note: 这里的基本类型特指简单的值, "xxx",99,'u',true,...
     *     这些类型不适宜转换成Json string, 虽然可以转换, 但是再反过来需要parseObject(string, type).
     *     toJsonString("xxx") ==> ""xxx""
     * </p>
     *
     * @param o
     * @return
     */
    public static boolean isPrimitive(Object o) {
        if (o instanceof String || o instanceof Number || o instanceof Boolean
                || o instanceof Character || o instanceof Enum) {
            return true;
        }
        return false;
    }



    public static String parseString(Object obj) {
        if (obj == null) {
            return null;
        }
        return String.valueOf(obj);
    }

    public static Long parseLong(Object obj) {
        if (obj == null) {
            return null;
        }
        if (obj instanceof Long) {
            return (Long) obj;
        }
        return Long.valueOf(String.valueOf(obj));
    }

    public static Integer parseInteger(Object obj) {
        if (obj == null) {
            return null;
        }
        if (obj instanceof Integer) {
            return (Integer) obj;
        }
        return Integer.valueOf(String.valueOf(obj));
    }

}
