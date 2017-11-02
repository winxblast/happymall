package top.winxblast.happymall.common;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * token的缓存
 *
 * @author winxblast
 * @create 2017/10/21
 **/
public class TokenCache {

    //先声明日志，使用logback的日志
    private static Logger logger = LoggerFactory.getLogger(TokenCache.class);

    public static final String TOKEN_PREFIX = "token_";

    //声明一个静态的内存块，下面这个是guava里的本地缓存，key和value都是String
    //这是一个调用链的模式，1000是缓存的初始化容量，max是最大缓存量，当达到最大时
    //guava会使用LRU（最近最少使用算法）来移除缓存项
    //expireAfterAccess这里设置了12小时的有效期，也可以设置成其他
    private static LoadingCache<String,String> localCache =
            CacheBuilder.newBuilder().initialCapacity(1000).maximumSize(10000).expireAfterAccess(12, TimeUnit.HOURS)
            .build(new CacheLoader<String, String>() {
                //默认的数据加载实现，当调用get取值的时候，如果key没有对应的值（没有命中），就调用这个方法进行返回
                @Override
                public String load(String s) throws Exception {
                    //Map 中对应某个键的值是null，和map中没有对应某个键的值，是非常容易混淆的两种情况。
                    // 因此，最好把值为null的键分离开，参考http://ifeve.com/google-guava-using-and-avoiding-null/
                    return "null";
                }
            });

    /**
     * 将键值对放入cache
     * @param key
     * @param value
     */
    public static void setKey(String key, String value) {
        localCache.put(key, value);
    }

    public static String getKey(String key) {
        String value = null;
        try {
            value = localCache.get(key);
            if("null".equals(value)) {
                //根据上面的设置“null”表示没有对应的键值
                return null;
            }
            return value;
        } catch (Exception e) {
            logger.error("localCache get error",e);
        }
        return null;
    }
}
