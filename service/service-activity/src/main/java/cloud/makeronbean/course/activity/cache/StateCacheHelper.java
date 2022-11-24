package cloud.makeronbean.course.activity.cache;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author makeronbean
 * @createTime 2022-11-24  11:07
 * @description TODO 系统缓存类，状态位在此保存，选课id对应的数量
 */
public class StateCacheHelper {
    /**
     * 缓存容器
     */
    private final static Map<Long, Integer> CACHE_MAP = new ConcurrentHashMap<Long, Integer>();

    /*static {
        CACHE_MAP.put(1L,10);
        CACHE_MAP.put(2L,10);
        CACHE_MAP.put(3L,10);
        CACHE_MAP.put(4L,10);
    }*/

    /**
     * 回滚 1 数量
     */
    public static void rollback(Long key) {
        synchronized (StateCacheHelper.class) {
            Integer integer = CACHE_MAP.get(key);
            CACHE_MAP.put(key,integer+1);
        }
    }


    /**
     * 减少 1 数量
     */
    public static void sub(Long key) {
        synchronized (StateCacheHelper.class) {
            Integer integer = CACHE_MAP.get(key);
            CACHE_MAP.put(key,integer-1);
        }
    }

    /**
     * 加入缓存
     */
    public static void put(Long key, Integer count) {
        CACHE_MAP.put(key, count);
    }

    /**
     * key对应的count是否为0
     */
    public static boolean canHandle(Long key) {
        Integer count = CACHE_MAP.get(key);
        return count != null && count > 0;
    }

    /**
     * 获取缓存
     */
    public static Integer get(Long key) {
        return CACHE_MAP.get(key);
    }

    /**
     * 清除缓存
     */
    public static void remove(Long key) {
        CACHE_MAP.remove(key);
    }

    public static synchronized void removeAll() {
        CACHE_MAP.clear();
    }
}
