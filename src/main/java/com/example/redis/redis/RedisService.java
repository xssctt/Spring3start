package com.example.redis.redis;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;



import java.util.List;

@Service
public class RedisService {
//    @Resource
//    StringRedisTemplate template;
//        @Resource
//        RedisTemplate<String, Object> template;
        @Resource
        private RedisTemplate<String, Object> redisTemplate;

    @PostConstruct
    public void init(){
        redisTemplate.setEnableTransactionSupport(true);   //需要开启事务
        redisTemplate.setValueSerializer(new Jackson2JsonRedisSerializer<>(Object.class));
    }

//    @Transactional    //需要添加此注解
//    public void test(){
//        redisTemplate.multi();
//        //redisTemplate.opsForValue().set("users", new User());
//        template.opsForValue().set("demotp",new User());
//        //template.opsForValue().set("d","111");
//        //将需要秒杀的商品 id 和库存数量，在秒杀开始前存入 Redis 中
//        //秒杀过程中，使用 incrby 相关原子自增（自减）指令，扣减库存
//        //将已参加秒杀的用户 id，存入 set 中，可以高效的防止同一用户多次抢购
//        //将已抢购成功的数据，放入 list 中
//        //异步将 Redis 中的数据，存入 MySQL，生成订单
//
//
//
//
//        redisTemplate.exec();
//    }

    @Transactional    //需要添加此注解
    public Boolean quitgood(String useid){

        String num= String.valueOf(redisTemplate.opsForValue().get("num"));

        //redisTemplate.watch("num");

        if(num == null || "".equals(num)){
            System.out.println("秒杀未开始");
            return false;
        }
        int nums=Integer.parseInt(num);

        if(redisTemplate.opsForSet().isMember("succues",useid)){
            System.out.println("用户已参与秒杀");
            return false;
        }

        if(nums <= 0){
            System.out.println("秒杀结束");
            return false;
        }

        try {
            redisTemplate.multi();
            redisTemplate.opsForValue().decrement("num", 1);
            redisTemplate.opsForSet().add("succues", useid);
            List<Object> list = redisTemplate.exec();

            if (list == null || list.isEmpty()) {
                // 事务失败，执行回滚操作
                redisTemplate.discard();
                // 记录错误信息
                System.out.println("库存不足，秒杀失败");
                // 可以添加其他回滚操作，如逆向递减库存
                redisTemplate.opsForValue().increment("num", 1);
                return false;
            }

            System.out.println("秒杀成功");
            return true;
        } catch (Exception e) {
            // 捕获Redis操作可能引发的异常
            // 记录异常信息并处理
            System.out.println("Redis操作异常: " + e.getMessage());
            return false;
        }

    }

    public void pubansub(){
        Jedis jedis=new Jedis("192.168.80.131",6359);


        jedis.publish("demo","1111111111");


        jedis.subscribe(new JedisPubSub() {
            @Override
            public void onMessage(String channel, String message) {
                System.out.println("channel"+channel+"message"+message);
            }
        }, "demo");


    }



}
