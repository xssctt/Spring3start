package com.example.redis.redis;


import redis.clients.jedis.Jedis;

public class sentinel {
    public static void main(String[] args) {

//
//        try(JedisSentinelPool pool=new JedisSentinelPool("lbwnb",
//                new HashSet<>(Arrays.asList("127.0.0.1:20001","127.0.0.1:20002","127.0.0.1:20003")))) {
//            Jedis jedis=pool.getResource();
//            jedis.set("a","1111");
//
//            Jedis jedis1=pool.getResource();
//            System.out.println(jedis1.get("a"));
//        }catch (Exception e){
//            e.printStackTrace();
//        }


     Jedis jedis=new Jedis("192.168.80.131",6379);
     jedis.auth("1234");
    // jedis.set("name","jack");

        System.out.println(jedis.get("name"));


        jedis.close();


    }
}
