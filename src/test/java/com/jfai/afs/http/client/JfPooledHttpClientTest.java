package com.jfai.afs.http.client;

import com.jfai.afs.http.bean.JfResponse;
import org.junit.Test;

import java.net.BindException;

public class JfPooledHttpClientTest {

    /**
     *
     * <pre>
     *
     *     普通客户端: 100 2271ms
     *     连接池客户端: 100 610ms
     *     => 连接池更快
     *
     *     连接池/route  任务数  耗时
     *     2            1000    3438ms
     *     10           1000    3459ms
     *     20           1000    3615ms
     *     200          1000    2912ms
     *     200          1000    3467ms
     *     10          10000    22999ms
     *
     * </pre>
     *
     * @throws InterruptedException
     */
    @Test
    public void fun2() throws InterruptedException {
//        final JfHttpClient c = new JfHttpClient();
        final JfPoolHttpClient c = new JfPoolHttpClient();

        Runnable runnable = new Runnable() {

            @Override
            public void run() {
                try {
                    JfResponse resp =  c.doGet("http://localhost:18081/test", null, null);
//                  System.out.println(JfPooledHttpClient.getEntityString(resp));
                    // 极高竞争的场景下, 保证不出现Timeout waiting for connection from pool
                    //resp.close();   // !一定要关闭响应, 释放连接!

                } catch (BindException be) {    // 一个测试任务正在debug挂起状态, 已经占用了很多端口, 这时这个测试任务再起, 可能碰到了端口冲突的, 报绑定异常
                    System.out.println(be.getMessage());
                   /* try {
                        Thread.sleep(200);
                    } catch (InterruptedException e) {

                    }*/
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        long t0 = System.currentTimeMillis();
        int n = 10000;
        for (int i = 0; i < n; i++) {
            Thread t = new Thread(runnable);
            t.start();
            t.join();
        }
        long t1 = System.currentTimeMillis();
        System.out.println("耗时: " + (t1 - t0) + "ms");

        System.out.println("Done!");

    }

    @Test
    public void fun1() throws Exception {
        JfPoolHttpClient c = new JfPoolHttpClient();
        JfResponse resp = c.doGet("http://localhost:18081/test", null, null);
        System.out.println(resp.getBody());

    }

    @Test
    public void fun() throws Exception {
        JfPoolHttpClient c1 = new JfPoolHttpClient();
        JfPoolHttpClient c2 = new JfPoolHttpClient();

    }


}