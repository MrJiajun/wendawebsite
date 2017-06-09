package com.nwecoder.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.nwecoder.model.User;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;
import redis.clients.jedis.BinaryClient;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Tuple;

import java.util.List;

/**
 * Created by Administrator on 2017/5/12 0012.
 */
@Service
public class JedisAdapter implements InitializingBean {
    private static final Logger logger = LoggerFactory.getLogger(JedisAdapter.class);

    private JedisPool pool;


    public static void print(int index,Object object){
        System.out.println(String.format("%d, %s", index, object.toString()));
    }

    public static void main(String[] argv){
        Jedis jedis = new Jedis("redis://localhost:6379/9");
        jedis.flushDB();

        jedis.set("hello", "world");
        print(1,jedis.get("hello"));//有些指定的数值可以存进去，对应为数据结构中的Hash
        jedis.rename("hello","newhello");
        print(1,jedis.get("newhello"));
        jedis.setex("hello2", 15, "world");//在时间到了之后会把变量删除掉（比如我们可以在注册时可以按时间删除验证码或者是短信验证器）

        jedis.set("pv", "100");
        jedis.incr("pv");
        jedis.incrBy("pv",5);
        print(2,jedis.get("pv"));
        jedis.decrBy("pv",3);
        print(2,jedis.get("pv"));

        print(3,jedis.keys("*"));

        //list操作
        String listName = "list";
        jedis.del(listName);
        for(int i = 0; i < 10; i++) {
            jedis.lpush(listName, " a" + String.valueOf(i));
        }
        print(4, jedis.lrange(listName, 0, 12));
        print(4, jedis.lrange(listName, 0, 3));
        print(5,jedis.llen(listName));
        print(6,jedis.lpop(listName));
        print(7,jedis.llen(listName));
        print(8,jedis.lrange(listName, 2, 6));
        print(9,jedis.lindex(listName,3));
        print(10,jedis.linsert(listName, BinaryClient.LIST_POSITION.AFTER,"a4","xx"));//插入
        print(10,jedis.linsert(listName, BinaryClient.LIST_POSITION.BEFORE,"a4","bb"));
        print(11,jedis.lrange(listName,0,12));

        //hash
        String userKey = "userxxx";
        jedis.hset(userKey,"name", "Jim");
        jedis.hset(userKey,"age", "12");
        jedis.hset(userKey,"phone", "1812468798");
        print(12,jedis.hget(userKey,"name"));
        print(13,jedis.hgetAll(userKey));
        jedis.hdel(userKey,"phone");
        print(14,jedis.hgetAll(userKey));
        print(15,jedis.hexists(userKey,"email"));
        print(16,jedis.hexists(userKey,"age"));
        print(17,jedis.hkeys(userKey));
        print(18,jedis.hvals(userKey));
        jedis.hsetnx(userKey,"school","z_ju");
        jedis.hsetnx(userKey,"name","yxy");
        print(19,jedis.hgetAll(userKey));

        //collection，集合的天生特性就是去重，比如可以用于点赞业务
        String likeKey1 = "commentLike1";
        String likeKey2 = "commentLike2";
        for(int i = 0; i < 10; i++){
            jedis.sadd(likeKey1,String.valueOf(i));
            jedis.sadd(likeKey2,String.valueOf(i*i));
        }
        print(20,jedis.smembers(likeKey1));
        print(21,jedis.smembers(likeKey2));
        print(22,jedis.sunion(likeKey1,likeKey2));
        print(23,jedis.sdiff(likeKey1, likeKey2));
        print(24,jedis.sinter(likeKey1, likeKey2));
        print(25,jedis.sismember(likeKey1,"12"));
        print(25,jedis.sismember(likeKey2,"16"));
        jedis.srem(likeKey1,"5");
        print(27,jedis.smembers(likeKey1));
        jedis.smove(likeKey2,likeKey1,"25");
        print(28,jedis.smembers(likeKey1));
        print(29,jedis.scard(likeKey1));


        //Sorted Sets优先队列
        String rangKey = "rangKey";
        jedis.zadd(rangKey,75,"Jim");
        jedis.zadd(rangKey,60,"Ben");
        jedis.zadd(rangKey,72,"Lee");
        jedis.zadd(rangKey,95,"Chen");
        jedis.zadd(rangKey,80,"Lucy");
        jedis.zadd(rangKey,46,"Mei");
        print(30,jedis.zcard(rangKey));
        print(31,jedis.zcount(rangKey,71,100));
        print(32,jedis.zscore(rangKey,"Chen"));
        jedis.zincrby(rangKey,2,"Lucy");//加
        print(33,jedis.zscore(rangKey,"Lucy"));
        jedis.zincrby(rangKey,2,"Luc");//加
        print(34,jedis.zscore(rangKey,"Luc"));
        print(35,jedis.zcount(rangKey,0,100));
        print(36,jedis.zrange(rangKey,1,3));//第一到第三名，默认是从小到大排序的
        print(37,jedis.zrevrange(rangKey,1,3));
        for(Tuple tuple: jedis.zrangeByScoreWithScores(rangKey,"60","100")){
            print(37,tuple.getElement()+"："+String.valueOf(tuple.getScore()));
        }
        print(38,jedis.zrank(rangKey,"Ben"));
        print(39,jedis.zrevrank(rangKey,"Ben"));//排名、排序

       /* String setKey = "zset";
        jedis.zadd(setKey,1,"a");
        jedis.zadd(setKey,1,"b");
        jedis.zadd(setKey,1,"c");
        jedis.zadd(setKey,1,"d");
        jedis.zadd(setKey,1,"e");
        print(40,jedis.zlexcount(setKey,"-","+"));
        print(41,jedis.zlexcount(setKey,"(b","d]"));//数学中的边界
        print(41,jedis.zlexcount(setKey,"[b","d]"));
        jedis.zrem(setKey,"b");
        print(43,jedis.zrange(setKey,0,10));
        jedis.zremrangeByLex(setKey,"(c","+");
        print(44,jedis.zrange(setKey,0,2));//socketSet
        */

/*        JedisPool pool = new JedisPool();//访问连接池的概念
        for(int i = 0; i < 100; i++){
            Jedis j = pool.getResource();//拿连接池
            print(45,j.get("pv"));//默认的连接池只有8条线程
            j.close();
        }*/

        //redis做缓存
        User user = new User();
        user.setName("xxx");
        user.setPassword("ppp");
        user.setHeadUrl("a.p");
        user.setSalt("salt");
        user.setId(1);
        print(46,JSONObject.toJSONString(user));
        jedis.set("user1", JSONObject.toJSONString(user));

        String value = jedis.get("user1");
        User user2 = JSON.parseObject(value,User.class);
        print(47,user2);
    }

    @Override
    public void afterPropertiesSet() throws Exception{
        pool = new JedisPool("redis://localhost:6379/10");

    }


    public long sadd(String key, String value){
        Jedis jedis = null;
        try{
            jedis = pool.getResource();
            return jedis.sadd(key, value);
        }catch (Exception e){
            logger.error("发生异常"+e.getMessage());
        }finally {
            if(jedis != null)
            {
                jedis.close();
            }
        }
        return 0;

    }

    public long srem(String key, String value){
        Jedis jedis = null;
        try{
            jedis = pool.getResource();
            return jedis.srem(key, value);
        }catch (Exception e){
            logger.error("发生异常"+e.getMessage());
        }finally {
            if(jedis != null)
            {
                jedis.close();
            }
        }
        return 0;

    }

    public long scard(String key){
        Jedis jedis = null;
        try{
            jedis = pool.getResource();
            return jedis.scard(key);
        }catch (Exception e){
            logger.error("发生异常"+e.getMessage());
        }finally {
            if(jedis != null)
            {
                jedis.close();
            }
        }
        return 0;
    }

    public boolean sismember(String key, String value){
        Jedis jedis = null;
        try{
            jedis = pool.getResource();
            return jedis.sismember(key, value);
        }catch (Exception e){
            logger.error("发生异常"+e.getMessage());
        }finally {
            if(jedis != null)
            {
                jedis.close();
            }
        }
        return false;

    }

/*    public List<String> brpop(int timeout, String key) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.brpop(timeout, key);
        } catch (Exception e) {
            logger.error("发生异常" + e.getMessage());
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return null;
    }

    public long lpush(String key, String value) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.lpush(key, value);
        } catch (Exception e) {
            logger.error("发生异常" + e.getMessage());
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return 0;
    }*/
}
