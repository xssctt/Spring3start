# Spring3start

spring3框架的初始化，框架生成和拦截器 过滤器等

---

---



## 自定义注解

@RepeatSubmit  重复提交注解

![image-20231204164430119](README.assets/image-20231204164430119.png)

![image-20231204164459008](README.assets/image-20231204164459008.png)

配合拦截器repeatSubmitInterceptor



## 工具类

![image-20231204164636718](README.assets/image-20231204164636718.png)

## redis

redis序列化

```java
@Configuration
public class RedisConfig extends CachingConfigurerSupport {

    /**
     * RedisTemplate 默认配置的是使用 Java JDK 的序列化，如果是对象，在Redis里查看时不直观
     * key 使用 String， value 列序列化 json
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {

        RedisTemplate<String, Object> template = new RedisTemplate<>();

        // 连接工厂
        template.setConnectionFactory(factory);

        // key 使用 StringRedisSerializer 来序列化
        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
        template.setKeySerializer(stringRedisSerializer);
        template.setHashKeySerializer(stringRedisSerializer);

        // value 采用 json 序列化
        Jackson2JsonRedisSerializer<Object> jsonRedisSerializer = jsonSerializer();
        template.setValueSerializer(jsonRedisSerializer);
        template.setHashValueSerializer(jsonRedisSerializer);

        template.afterPropertiesSet();

        return template;
    }

    /**
     * 序列化器
     */
    private Jackson2JsonRedisSerializer<Object> jsonSerializer() {

        Jackson2JsonRedisSerializer<Object> jsonRedisSerializer = new Jackson2JsonRedisSerializer<>(Object.class);

        // 对象映射
        ObjectMapper om = new ObjectMapper();

        // 序列化所有字段和修饰符范围
        om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);

        // 将类名序列化到json中 {"@class":"model.User", "id":"1", "name":"hello"}
        // 否则不能反序列化 {"id":"1", "name":"hello"}
        om.activateDefaultTyping(LaissezFaireSubTypeValidator.instance, ObjectMapper.DefaultTyping.NON_FINAL, JsonTypeInfo.As.PROPERTY);

        jsonRedisSerializer.setObjectMapper(om);

        return jsonRedisSerializer;
    }
}

```



>  * spring redis 工具类



```java


@Component
public class RedisCache {

    @Resource
    public RedisTemplate redisTemplate;

    /**
     * string类型递增
     *
     * @param key 缓存的键值
     * @return 递增后返回值
     */
    public Long increment(final String key) {
        return redisTemplate.opsForValue().increment(key);
    }

    /**
     * string类型递减
     *
     * @param key redis键
     * @return 递减后返回值
     */
    public Long decrement(final String key) {
        return redisTemplate.opsForValue().decrement(key);

    }

    /**
     * string类型原子递减，不小于-1
     *
     * @param key redis键
     * @return 递减后返回值
     */
    public Long luaDecrement(final String key) {
        RedisScript<Long> redisScript = new DefaultRedisScript<>(buildLuaDecrScript(), Long.class);
        Number execute = (Number) redisTemplate.execute(redisScript, Collections.singletonList(key));
        if (execute == null) {
            return -1L;
        }
        return execute.longValue();
    }

    /**
     * lua原子自减脚本
     */
    private String buildLuaDecrScript() {
        return """
                local c
                c = redis.call('get',KEYS[1])
                if c and tonumber(c) < 0 then
                return c;
                end
                c = redis.call('decr',KEYS[1])
                return c;""";
    }

    /**
     * 设置redis键值对
     *
     * @param key   redis键
     * @param value redis值
     */
    public <T> void setCacheObject(final String key, final T value) {
        redisTemplate.opsForValue().set(key, value);
    }

    /**
     * 设置redis键值对
     *
     * @param key      redis键
     * @param value    redis值
     * @param timeout  过期时间
     * @param timeUnit 时间单位
     */
    public <T> void setCacheObject(final String key, final T value, final Integer timeout, final TimeUnit timeUnit) {
        redisTemplate.opsForValue().set(key, value, timeout, timeUnit);
    }

    /**
     * 设置过期时间
     *
     * @param key     redis键
     * @param timeout 超时时间
     * @return boolean
     */
    public boolean expire(final String key, final long timeout) {
        return expire(key, timeout, TimeUnit.SECONDS);
    }

    /**
     * 设置过期时间
     *
     * @param key     redis键
     * @param timeout 超时时间
     * @param unit    时间单位
     * @return true=设置成功；false=设置失败
     */
    public boolean expire(final String key, final long timeout, final TimeUnit unit) {
        return redisTemplate.expire(key, timeout, unit);
    }

    /**
     * 获得缓存的基本对象
     *
     * @param key redis键
     * @return redis值
     */
    public <T> T getCacheObject(final String key) {
        ValueOperations<String, T> operation = redisTemplate.opsForValue();
        return operation.get(key);
    }

    /**
     * 删除单个对象
     *
     * @param key redis键
     * @return boolean
     */
    public boolean deleteObject(final String key) {
        return redisTemplate.delete(key);
    }

    /**
     * 删除集合对象
     *
     * @param collection 多个对象
     * @return 删除个数
     */
    public long deleteObject(final Collection<Object> collection) {
        return redisTemplate.delete(collection);
    }

    /**
     * 删除指定前缀的key
     */
    public long deleteLikesKeyObject(String prefix) {
        return redisTemplate.delete(getLikesKeyList(prefix));
    }

    /**
     * 获取指定前缀键值对
     */
    public <T> List<T> getLikesKeyList(String prefix) {
        // 获取所有的key
        Set<String> keys = redisTemplate.keys(prefix);
        // 批量获取数据
        return redisTemplate.opsForValue().multiGet(keys);
    }

    /**
     * 缓存Set
     *
     * @param key   缓存键值
     * @param value 缓存的数据
     * @return 缓存数据的对象
     */
    public <T> long setCacheSet(final String key, final Object value) {
        Long count = redisTemplate.opsForSet().add(key, value);
        return count == null ? 0 : count;
    }

    /**
     * 判断key-set中是否存在value
     *
     * @param key   缓存键值
     * @param value 缓存的数据
     * @return boolean
     */
    public Boolean containsCacheSet(final String key, final Object value) {
        return redisTemplate.opsForSet().isMember(key, value);
    }

    /**
     * 缓存Map
     *
     * @param key     redis键
     * @param dataMap map对象
     */
    public <T> void setCacheMap(final String key, final Map<String, T> dataMap) {
        if (dataMap != null) {
            redisTemplate.opsForHash().putAll(key, dataMap);
        }
    }

}

```

## 过滤器

![image-20231204165049722](README.assets/image-20231204165049722.png)

Xss过滤器

以及接口访问过滤



## 自定义全局异常处理

![image-20231204165146176](README.assets/image-20231204165146176.png)

```java
@ControllerAdvice
public class CommmonExectionHander {

    @ExceptionHandler(Throwable.class)//所有可抛出的异常的跟类
    @ResponseBody
    public Map<String,String> RunTimeExection(Throwable e){
        e.printStackTrace();
        Map<String,String> map=new HashMap<>();
        map.put("msg",e.getMessage());
        map.put("error","error");
        map.put("data",null);
        return map;
    }

    @ExceptionHandler(BindException.class)
    @ResponseBody
    public Map<String,String> RuntiomeExection(BindException e){

        e.printStackTrace();
        Map<String,String> map=new HashMap<String,String>();
        map.put("msg",e.getBindingResult().getAllErrors().get(0).getDefaultMessage());
        map.put("error","error");
        map.put("data",null);

        return map;
    }

}
```

## 接口文档

```java
@Configuration
public class SwaggerConfiguration {

    @Bean
    public Docket docket() {
        ApiInfo info = new ApiInfoBuilder()
                .contact(new Contact("你的名字", "https://www.bilibili.com", "javastudy111@163.com"))
                .title("图书管理系统 - 在线API接口文档")
                .description("这是一个图书管理系统的后端API文档，欢迎前端人员查阅！")
                .build();
        return new Docket(DocumentationType.OAS_30)
                .apiInfo(info)
                .select()       //对项目中的所有API接口进行选择
                .apis(RequestHandlerSelectors.basePackage("ltd.newbee.mall.controller"))
                .build();
    }



}
```

## 规范化返回

```java


@Data
public class JsonResult<T> {
    private String code;
    private String message;
    private T data;

    public static final String CODE_SUCCESS = "SUCCESS";
    public static final String CODE_ERROR = "ERROR";

    //成功, 没有数据
    public JsonResult() {
        this.data = null;
        this.code = CODE_SUCCESS;
        this.message = "";
    }

    //成功，有数据
    public JsonResult(T data) {
        this.data = data;
        this.message = "";
        this.code = CODE_SUCCESS;
    }

    //成功或失败，决于status，不携带数据
    public JsonResult(boolean status, String message) {
        this.data = null;
        this.message = message;
        this.code = status ? CODE_SUCCESS : CODE_ERROR;
    }

    public JsonResult(String code, String message) {
        this.data = null;
        this.message = message;
        this.code = code;
    }

    public JsonResult(String code, String message, T data) {
        this.data = data;
        this.message = message;
        this.code = code;
    }

    public JsonResult(boolean status, String message, T data) {
        this.data = data;
        this.message = message;
        this.code = status ? CODE_SUCCESS : CODE_ERROR;
    }

    @JsonIgnore //json忽略此字段
    public boolean isSuccess() {
        return code.equals(CODE_SUCCESS);
    }
}

```

