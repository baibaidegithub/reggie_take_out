package com.itheima.test;

import com.itheima.reggie.ReggieApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

@SpringBootTest(classes = ReggieApplication.class)
@RunWith(SpringRunner.class)
public class SpingDataRedisTest {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Test
    public void testString(){
        stringRedisTemplate.opsForValue().set("city123","beijing");
    }
}
