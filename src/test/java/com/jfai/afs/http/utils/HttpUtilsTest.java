package com.jfai.afs.http.utils;

import org.apache.http.HttpResponse;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.*;

public class HttpUtilsTest {

    @Test
    public void doGet() throws Exception {
/*
* 每次都是重新创建的连接, 即使时带了连接池*/
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                HttpResponse resp = null;
                try {
                    resp = HttpUtils.doGet("http://localhost:18081/api/test/ping", null, null);

                    System.out.println(Thread.currentThread().getName()+": "+HttpUtils.getJsonEntity(resp));

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        for (int i = 0; i < 100; i++) {
            Thread t = new Thread(runnable);
            t.start();
            t.join();
        }

        System.out.println("done");

    }
}