package cloud.makeronbean.course.activity.cache;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author makeronbean
 * @createTime 2022-11-24  11:07
 * @description TODO 系统缓存类，状态位在此保存， 选课id : boolean
 */
public class StateCacheHelper {

    /**
     * 缓存容器
     */
    private final static Map<Long, Boolean> CACHE_MAP = new ConcurrentHashMap<>();

    static {
        CACHE_MAP.put(1L,true);
        CACHE_MAP.put(2L,true);
        CACHE_MAP.put(3L,true);
        CACHE_MAP.put(4L,true);
        CACHE_MAP.put(5L,true);
        CACHE_MAP.put(6L,true);
        CACHE_MAP.put(7L,true);
        CACHE_MAP.put(8L,true);
    }


    /**
     * 状态位设置为true
     */
    public static void setStateTrue(Long key) {
        CACHE_MAP.put(key,true);
    }

    /**
     * 状态位设置为false
     */
    public static void setStateFalse(Long key) {
        CACHE_MAP.put(key,false);
    }


    /**
     * key对应的count是否为0
     */
    public static boolean canHandle(Long key) {
        return CACHE_MAP.get(key);
    }


    /**
     * 获取缓存
     */
    public static Boolean get(Long key) {
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
