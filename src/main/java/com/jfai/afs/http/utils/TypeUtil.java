package com.jfai.afs.http.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.fastjson.annotation.JSONField;
import com.jfai.afs.http.exception.JfParseException;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;

/**
 * 类型工具类
 *
 * @author Nisus Liu
 * @version 0.0.1
 * @email liuhejun108@163.com
 * @date 2019/1/15 16:44
 */
public class TypeUtil {
    /**
     * 定义简单类型
     */
    private static final Map<Class, Parser> simples=new HashMap<>();;

    //private final static Set<Class<?>> primitiveJsonClasses = new HashSet<Class<?>>();


    static {
        // Boolean boolean
        Parser booleanParser = new Parser() {
            @Override
            public Boolean parse(Object obj) {
                if (obj instanceof Boolean) {
                    return (Boolean) obj;
                }
                // 'true' 'TRUE' 才会 ==> true
                return Boolean.valueOf(String.valueOf(obj));
            }
        };
        // Character char
        Parser charParser = new Parser() {
            @Override
            public Character parse(Object obj){
                if (obj == null) {
                    return null;
                }
                if (obj instanceof Character) {
                    return (Character) obj;
                }
                if (!(obj instanceof String)) {
                    throw new JfParseException("'obj' neither char or string, can't parse to char");
                } else {
                    if (((String) obj).length() > 1) {
                        throw new JfParseException("'obj' is string, but length > 1, can't parse to char");
                    }
                    return ((String) obj).charAt(0);
                }
            }
        };
        // Byte byte
        Parser byteParser = new Parser() {
            /**
             * @param obj 必须是有效的数字字符
             * @return
             */
            @Override
            public Byte parse(Object obj){
                if (obj == null) {
                    return null;
                }
                if (obj instanceof Byte) {
                    return (Byte) obj;
                }
                // 默认按 10 进制转换
                String s = String.valueOf(obj);
                try {
                    return Byte.valueOf(s);
                } catch (Exception e) {
                    throw new JfParseException("`Byte.valueOf( "+ s +" )` EX",e);
                }
            }
        };
        // Short short
        Parser shortParser = new Parser() {
            @Override
            public Short parse(Object obj){
                if (obj == null) {
                    return null;
                }
                if (obj instanceof Short) {
                    return (Short) obj;
                }
                String s = String.valueOf(obj);
                try {
                    return Short.valueOf(s);
                } catch (Exception e) {
                    throw new JfParseException("`Short.valueOf("+s+")` EX", e);
                }
            }
        };
        // Integer int
        Parser intParser = new Parser() {
            @Override
            public Integer parse(Object obj){
                if (obj == null) {
                    return null;
                }
                if (obj instanceof Integer) {
                    return (Integer) obj;
                }
                String s = String.valueOf(obj);
                try {
                    return Integer.valueOf(s);
                } catch (Exception e) {
                    throw new JfParseException("`Integer.valueOf(" + s + ")` EX");
                }
            }
        };
        // Long long
        final Parser longParser = new Parser() {
            @Override
            public Long parse(Object obj){
                if (obj == null) {
                    return null;
                }
                if (obj instanceof Long) {
                    return (Long) obj;
                }
                    String s = String.valueOf(obj);
                try {
                    return Long.valueOf(s);
                } catch (Exception e) {
                    throw new JfParseException("`Long.valueOf(" + s + ")` EX");
                }
            }
        };
        // Float float
        Parser floatParser = new Parser() {
            @Override
            public Float parse(Object obj){
                if (obj == null) {
                    return null;
                }
                if (obj instanceof Float) {
                    return (Float) obj;
                }
                    String s = String.valueOf(obj);
                try {
                    return Float.valueOf(s);
                } catch (Exception e) {
                    throw new JfParseException("`Float.valueOf(" + s + ")` EX");
                }
            }
        };
        // Double double
        final Parser doubleParser = new Parser() {
            @Override
            public Double parse(Object obj){
                if (obj == null) {
                    return null;
                }
                if (obj instanceof Double) {
                    return (Double) obj;
                }
                    String s = String.valueOf(obj);
                try {
                    return Double.valueOf(s);
                } catch (Exception e) {
                    throw new JfParseException("`Double.valueOf(" + s + ")` EX");
                }
            }
        };
        // BigInteger
        Parser bigIntegerParser = new Parser() {
            @Override
            public BigInteger parse(Object obj){
                if (obj == null) {
                    return null;
                }
                if (obj instanceof BigInteger) {
                    return (BigInteger) obj;
                }

                Long l = longParser.parse(obj);
                try {
                    return BigInteger.valueOf(l);
                } catch (Exception e) {
                    throw new JfParseException("`BigInteger.valueOf(" + l + ")` EX");
                }
            }
        };

        Parser bigDecimalParser = new Parser() {
            @Override
            public BigDecimal parse(Object obj){
                if (obj == null) {
                    return null;
                }
                if (obj instanceof BigDecimal) {
                    return (BigDecimal) obj;
                }
                String s = String.valueOf(obj);
                if (s.contains(".")) {
                    Double db = doubleParser.parse(s);
                    try {
                        return BigDecimal.valueOf(db);
                    } catch (Exception e) {
                        throw new JfParseException("`BigDecimal.valueOf(" + db + ")` EX");
                    }
                } else {
                    Long l = longParser.parse(s);
                    try {
                        return BigDecimal.valueOf(l);
                    } catch (Exception e) {
                        throw new JfParseException("`BigDecimal.valueOf(" + l + ")` EX");
                    }
                }
            }
        };
        // String
        Parser stringParser = new Parser() {
            @Override
            public String parse(Object obj) {
                if (obj == null) {
                    return null;
                }
                return String.valueOf(obj);
            }
        };



        // 预先定义简单类型 parser
        simples.put(boolean.class, booleanParser);
        simples.put(Boolean.class, booleanParser);
        simples.put(Character.class, charParser);
        simples.put(char.class, charParser);
        simples.put(Byte.class, byteParser);
        simples.put(byte.class, byteParser);
        simples.put(Short.class, shortParser);
        simples.put(short.class, shortParser);
        simples.put(Integer.class, intParser);
        simples.put(int.class, intParser);
        simples.put(Long.class, longParser);
        simples.put(long.class, longParser);
        simples.put(Float.class, floatParser);
        simples.put(float.class, floatParser);
        simples.put(Double.class, doubleParser);
        simples.put(BigInteger.class, bigIntegerParser);
        simples.put(BigDecimal.class, bigDecimalParser);
        simples.put(String.class, stringParser);


    }


    /**
     * 判断是否是 <b>简单类型</b>
     * <p>
     *      简单类型指基本类型等一些简单的类型,如: "xxx", 99, 'u', true, ...
     * <br>
     *     拿String来说, <code>JSON.toJsonString("xxx") ==> ""xxx""</code>, 多余的<code>"</code>会带来一些不便.
     *     所以, 针对简单类型, 在转{@link JSONUtil#toJsonAdapt(Object)}时, 会特殊处理.
     * </p>
     *
     * @param o
     * @return
     */
    public static Boolean isSimple(Object o) {
        if (o == null) {
            return null;
        }
        /*if (o instanceof String || o instanceof Number || o instanceof Boolean
                || o instanceof Character || o instanceof Enum) {
            return true;
        }*/
        // 非包装类型可以用内置方法 isPrimitive()
        return o.getClass().isPrimitive() || simples.containsKey(o.getClass());
    }

    public static byte[] toBytes(Byte[] Bs) {
        if (Bs == null) {
            return null;
        }
        byte[] bytes = new byte[Bs.length];
        for (int i = 0; i < Bs.length; i++) {
            Byte b = Bs[i];
            bytes[i] = b;
        }
        return bytes;
    }

    /**
     * {@link JSON#parseObject(java.lang.String, java.lang.Class)}的包装.
     * 对于简单类型({@link TypeUtil#isSimple(java.lang.Object)})使用定制的解析器{@link Parser}解析.
     * 非简单类型, 交给fastjson处理.
     * @param src
     * @param type
     * @param <T>
     * @return
     */
    public static <T> T parseObject(String src, Class<T> type){
        if (src == null) {
            return null;
        }
        Parser parser = simples.get(type);
        if (parser != null) {
            // 简单类型
            return parser.parse(src);
        }

        // 非简单类型, 需要用 fastjson parse
        return JSON.parseObject(src, type);
    }

    public static <T> T parseObject(String src, TypeReference<T> ref){
        if (src==null || ref==null) {
            return null;
        }
        if (ref.getType() instanceof Class) {
            return parseObject(src, (Class<T>) ref.getType());
        }
        // 复杂 Type, 借助 fastjson
        return JSON.parseObject(src, ref);
    }




    //
    // 为常用'简单类型'提供快捷方式
    //

    public static String parseString(Object obj){
        return simples.get(String.class).parse(obj);
    }

    public static Long parseLong(Object obj){
        return simples.get(Long.class).parse(obj);
    }

    public static Integer parseInteger(Object obj){
        return simples.get(Integer.class).parse(obj);
    }
    public static Boolean parseBoolean(Object obj){
        return simples.get(Boolean.class).parse(obj);
    }







    /**
     * <p>
     *     使用前, 请确保类型传入正确, 否则, 会输出非预期的结果, 或异常.
     * </p>
     */
    public interface Parser {


        <T> T parse(Object obj) throws JfParseException;
    }

    /**
     * 枚举类解析器
     * <p>
     *     指定枚举类型, 构造实例.
     *     调用{@link EnumParser#parse(Object)}可将目标字符串转成对应枚举实例.
     * </p>
     * <p>
     *     注意: 自定义的Enum解析器是将纯字符串转成Enum实例, 如: <code>enumParser.parse("A") ==> A</code>;<br>
     *     不同于JSON框架中, <code>""A"" ==> A</code>
     *     <pre>
     * TestEnum a = (TestEnum) TypeUtil.EnumParser.instance(TestEnum.class).parse("A");
     * TestEnum a1 = TypeUtil.parseObject("\"A\"", TestEnum.class);
     *     </pre>
     * </p>
     */
    @Deprecated // 没卵用
    public static class EnumParser implements Parser {
        public static EnumParser instance(Class enumClass){
            return new EnumParser(enumClass);
        }

        protected final Enum[] ordinalEnums;
        /**
         * enum 名为键, Enum实例为值, 构建Map
         */
        private final Map<String, Enum> enumMap = new HashMap<>();

        public EnumParser(Class enumClass) {
            ordinalEnums = (Enum[]) enumClass.getEnumConstants();
            for (int i = 0; i < ordinalEnums.length; i++) {
                Enum e = ordinalEnums[i];
                String name = e.name();
                // 考虑注解别名 / 仅支持fastjson的
                JSONField jsonField = null;
                try {
                    Field field = enumClass.getField(name);
                    jsonField = field.getAnnotation(JSONField.class);
                    if (jsonField != null) {
                        String jsonFieldName = jsonField.name();
                        if (jsonFieldName != null && jsonFieldName.length() > 0) {
                            name = jsonFieldName;
                        }
                    }
                } catch (Exception ex) {
                    // skip
                }
                enumMap.put(name, e);
            }
        }

        private Enum<?> getEnumByName(String name) {
            return enumMap.get(name);
        }

        @Override
        public Enum<?> parse(Object obj){
            if (obj == null) {
                return null;
            }
            return getEnumByName(String.valueOf(obj));

        }
    }
}




    /*
     fastjson ParserConfig 内置有很多序列化器

     fastjson定义的基本类型
     com.alibaba.fastjson.parser.DefaultJSONParser
     Class<?>[] classes = new Class[] {
                boolean.class, // boolean
                byte.class, // class java.lang.Boolean
                short.class,
                int.class,
                long.class,
                float.class,
                double.class,

                Boolean.class,
                Byte.class,
                Short.class,
                Integer.class,
                Long.class,
                Float.class,
                Double.class,

                BigInteger.class,
                BigDecimal.class,
                String.class
        };
     *
     *
     * String.valueOf(xx) <--> XX.parseXxx
     * 保证正反结果一致


    public static boolean isPrimitive2(Class<?> clazz) {
        return clazz.isPrimitive() //
                || clazz == Boolean.class //
                || clazz == Character.class //
                || clazz == Byte.class //
                || clazz == Short.class //
                || clazz == Integer.class //
                || clazz == Long.class //
                || clazz == Float.class //
                || clazz == Double.class //
                || clazz == BigInteger.class //
                || clazz == BigDecimal.class //
                || clazz == String.class //
                || clazz == java.util.Date.class //
                || clazz == java.sql.Date.class //
                || clazz == java.sql.Time.class //
                || clazz == java.sql.Timestamp.class //
                || clazz.isEnum() //
                ;
    }

     * */



