package com.jfai.afs.http.client;

import org.junit.Test;

import static org.junit.Assert.*;

public class ConfigTest {
    @Test
    public void fun2(){
        Config c = new Config();
        c.setAppKey("akkkkkk");
//        if (c.getEncryption()) {
//            System.out.println(true);
//        }
    }

    @Test
    public void fun1(){
        Config c1 = new Config();
        c1.setAppKey("ak00001");
        c1.setZip(true);
        System.out.println("c1: "+c1);

        Config c2 = new Config();
        c2.setAppKey("ak00002");
        c2.setEncryption(true);
        System.out.println("c2: "+c2);


        System.out.println(c1.extend(c2));

    }

}