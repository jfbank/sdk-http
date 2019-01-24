package com.jfai.afs.http.utils;

import com.alibaba.fastjson.JSON;
import com.jfai.afs.http.bean.JfReqParam;
import com.jfai.afs.http.exception.JfParseException;
import org.junit.Test;

import java.util.Date;
import java.util.concurrent.TimeUnit;

public class TypeUtilTest {

    @Test
    public void fun6(){
        JfReqParam o = new JfReqParam();
        o.setAppKey("ak 47");
        String x = JSONUtil.toJsonAdapt(o);
        System.out.println(x);
        System.out.println(JSONUtil.toJsonAdapt(x));

        System.out.println();

        System.out.println(JSONUtil.toJsonAdapt(new Date()));
        System.out.println(JSONUtil.toJsonAdapt(TestEnum.A));
        System.out.println(JSONUtil.toJsonAdapt(String.class));

        System.out.println(JSONUtil.toJsonAdapt(new byte[]{1, 2, 3, 4}));
    }

    @Test
    public void fun5() throws JfParseException {

        //String s = TypeUtil.parseObject("6979777", String.class);
        JfReqParam o = new JfReqParam();
        o.setAppKey("ak 47");
        String s = TypeUtil.parseString(o);
        System.out.println(s);


        TestEnum a = (TestEnum) TypeUtil.EnumParser.instance(TestEnum.class).parse("A");
        TestEnum a1 = TypeUtil.parseObject("\"A\"", TestEnum.class);


    }



    @Test
    public void fun4(){

        // 日期 -fastjson-> 转成毫秒在转成字符
//        Date d = new Date();
//        String s = JSON.toJSONString(d);
//        System.out.println(s);
//        Date o = JSON.parseObject(s, Date.class);
//        System.out.println(o);

        Long o = JSON.parseObject("2018-01-18 20:21:10", Long.class);
        System.out.println(o);

    }

    @Test
    public void fun3(){
//        Class<TestEnum> ec = TestEnum.class;
//        System.out.println(ec.isEnum());


        String s = JSON.toJSONString(TestEnum.A);
        Enum anEnum = JSON.parseObject(s, TestEnum.class);
//        Enum.class.is

    }

    @Test
    public void fun2(){
        Byte[] bs = {97, 98, 99, 100};
        System.out.println(JSONUtil.toJsonAdapt(bs));

    }

    @Test
    public void fun1(){
        System.out.println(TypeUtil.isSimple('a'));
        System.out.println(TypeUtil.isSimple(TimeUnit.MINUTES));
        System.out.println(TypeUtil.isSimple(TestEnum.A));
        System.out.println(TypeUtil.isSimple(89));


        System.out.println(JSON.toJSONString('a'));
        System.out.println(JSON.toJSONString(TimeUnit.MINUTES));
        System.out.println(JSON.toJSONString(TestEnum.A));



        // PrimitiveArraySerializer 处理各种基本类型数组, 包括byte
        // Byte[] -fastjson-> 数字数组, 元素就是Unicode码
        //Byte[] bs = {97, 98, 99, 100};
        byte[] bs = "abcd".getBytes();
        // --> b64 json 字符串
        System.out.println(JSON.toJSONString(bs));
        //System.out.println(Base64.encodeBase64String(bs));


    }

    public static enum TestEnum{
        A(8,"8888"),
        B(9,"9999"),
        C(10,"1010");

        private int i;
        private String s;
        TestEnum(int i, String s) {
            this.i=i;
            this.s=s;
        }

        public int getI() {
            return i;
        }

        public void setI(int i) {
            this.i = i;
        }

        public String getS() {
            return s;
        }

        public void setS(String s) {
            this.s = s;
        }
    }

}