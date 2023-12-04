package com.example.redis.redis;


import jakarta.annotation.Resource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import java.util.*;

@RestController
@RequestMapping("/redis")
public class TestController {

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Resource
    RedisService redisService;

    @GetMapping("/test")
    public String test() {

        // string
        redisTemplate.opsForValue().set("foo", "hello");
        redisTemplate.opsForValue().set("bar", 100);

        String foo = (String) redisTemplate.opsForValue().get("foo");
        System.out.println(foo);  // hello

        String noData = (String) redisTemplate.opsForValue().get("noData");
        System.out.println(noData); // null

        redisTemplate.opsForValue().increment("bar");
        int bar2 = (int) redisTemplate.opsForValue().get("bar"); // 101
        System.out.println(bar2);

        // hash
        User user = new User(3, "Jack");
        redisTemplate.opsForValue().set("user1", user);
        User obj = (User) redisTemplate.opsForValue().get("obj");
        System.out.println(obj);

        Map<String, Object> map = new HashMap<>();
        map.put("name", "Mary");
        map.put("age", 18);
        redisTemplate.opsForHash().putAll("user2", map);


        Map<Object, Object> user3 = redisTemplate.opsForHash().entries("user2");
        System.out.println(user3);


        // list
        redisTemplate.opsForList().leftPush("names", "jack");
        redisTemplate.opsForList().leftPush("names", "mary");
        redisTemplate.opsForList().leftPush("names", "lily");


        System.out.println(redisTemplate.opsForList().rightPop("names"));  // jack
        System.out.println(redisTemplate.opsForList().rightPop("names"));  // mary
        System.out.println(redisTemplate.opsForList().rightPop("names"));  // lily
        System.out.println(redisTemplate.opsForList().rightPop("names"));  // null


        return "ok";
    }

//    @PostConstruct
//    public void init(){
//        redisTemplate.setEnableTransactionSupport(true);   //需要开启事务
//        redisTemplate.setValueSerializer(new Jackson2JsonRedisSerializer<>(Object.class));
//    }

    @GetMapping("/demo")
    public String demo(){

        System.out.println("-----------------------------------------------------------------------");

        Random random=new Random();
        String userid= String.valueOf(random.nextInt(20));

        //redisTemplate.watch("num");
        redisService.quitgood(userid);



        //将需要秒杀的商品 id 和库存数量，在秒杀开始前存入 Redis 中
        System.out.println(redisTemplate.opsForValue().get("num"));
        //秒杀过程中，使用 incrby 相关原子自增（自减）指令，扣减库存
        //将已参加秒杀的用户 id，存入 set 中，可以高效的防止同一用户多次抢购
        //将已抢购成功的数据，放入 list 中
        //异步将 Redis 中的数据，存入 MySQL，生成订单
       // redisService.quitgood(userid);



        return "";
    }



    @RequestMapping("/stringTest")
    public Object stringTest() {
        this.redisTemplate.delete("name");
        this.redisTemplate.opsForValue().set("name", "路人");
        Object name = this.redisTemplate.opsForValue().get("name");
        return name;
    }


    @RequestMapping("/listTest")
    public List<Object> listTest() {
        this.redisTemplate.delete("names");
        this.redisTemplate.opsForList().rightPushAll("names", "刘德华", "张学友",
                "郭富城", "黎明");
        List<Object> courses = this.redisTemplate.opsForList().range("names", 0,
                -1);
        return courses;
    }



    @RequestMapping("setTest")
    public Set<Object> setTest() {
        this.redisTemplate.delete("courses");
        this.redisTemplate.opsForSet().add("courses", "java", "spring",
                "springboot");
        Set<Object> courses = this.redisTemplate.opsForSet().members("courses");
        return courses;
    }



    @RequestMapping("hashTest")
    public Map<Object, Object> hashTest() {
        this.redisTemplate.delete("userMap");
        Map<String, String> map = new HashMap<>();
        map.put("name", "路人");
        map.put("age", "30");
        this.redisTemplate.opsForHash().putAll("userMap", map);
        Map<Object, Object> userMap =
                this.redisTemplate.opsForHash().entries("userMap");
        return userMap;
    }



    @RequestMapping("zsetTest")
    public Set<Object> zsetTest() {
        this.redisTemplate.delete("languages");
        this.redisTemplate.opsForZSet().add("languages", "java", 100d);
        this.redisTemplate.opsForZSet().add("languages", "c", 95d);
        this.redisTemplate.opsForZSet().add("languages", "php", 70);
        Set<Object> languages =
                this.redisTemplate.opsForZSet().range("languages", 0, -1);
        return languages;
    }































    public static class User {
        private int id;
        private String name;

        public User() {
        }

        public User(int id, String name) {
            this.id = id;
            this.name = name;
        }

        @Override
        public String toString() {
            return "User{" +
                    "id=" + id +
                    ", name='" + name + '\'' +
                    '}';
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}