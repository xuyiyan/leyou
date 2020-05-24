package com.leyou;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Map;
import java.util.concurrent.TimeUnit;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = LyUserService.class)
public class RedisTest {

    @Autowired
    private StringRedisTemplate template;

    @Test
    public void test1(){

        //string
        //创建对象ValueOperations
        ValueOperations<String, String> stringStringValueOperations = template.opsForValue();
        stringStringValueOperations.set("name","tom");

    }


    @Test
    public void test2(){

        //string
        //创建对象ValueOperations
        ValueOperations<String, String> stringStringValueOperations = template.opsForValue();
        stringStringValueOperations.set("age","11",1, TimeUnit.MINUTES);

    }
    @Test
    public void test3(){

        //创建对象BoundHashOperations
        BoundHashOperations<String, Object, Object> boundHashOperations = template.boundHashOps("myhash1");
        boundHashOperations.put("address","beijing");
        boundHashOperations.put("school","wuda");


        Object address = boundHashOperations.get("address");
        System.out.println(address);

        Map<Object, Object> entries = boundHashOperations.entries();

        for(Map.Entry s:entries.entrySet()){
            System.out.println(s.getKey()+"---"+s.getValue());


        }
    }


}
