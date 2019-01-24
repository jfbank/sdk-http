package com.jfai.afs.http.bean;

import com.alibaba.fastjson.JSON;
import com.jfai.afs.http.exception.JfParseException;
import org.junit.Test;

import java.text.ParseException;

import static org.junit.Assert.*;

public class HttpVoTest {


    @Test
    public void fun1()  {

        JfReqParam req = new JfReqParam();
        req.setZip(true);
        req.setSessionKey("xfjsluos9uge");
//        req.setData("幕后之王-原文");
//        req.setEncData("幕后之王-加密");
        req.setData(89);

        System.out.println(req);
        System.out.println(req.getData(Long.class));


        JfResBody res = new JfResBody();
        res.setCode(127);
        res.setMessage("好呀哟");
        res.setData("6666");

        System.out.println(res);

    }


}