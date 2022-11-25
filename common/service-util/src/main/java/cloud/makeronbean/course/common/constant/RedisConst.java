package cloud.makeronbean.course.common.constant;

/**
 * @author makeronbean
 * @createTime 2022-11-23  13:26
 * @description TODO
 */
public class RedisConst {
    public static final String USER_LOGIN_KEY_PREFIX = "user:login:";
    public static final int USER_KEY_TIMEOUT = 60 * 60 * 24 * 7;

    /**
     * 选课列表
     */
    public static final String XK_KIND_PREFIX = "xk:kind:";

    /**
     * 选课类别
     */
    public static final String XK_CODE_PREFIX = "xk:code:";

    /**
     * 防止超选
     */
    public static final String XK_LIST_PREFIX = "list:";

    /**
     * 是否选过课
     */
    public static final String XK_COURSE_PREFIX = "xk:course:";

    /**
     * 学生id是否被限制登录
     */
    public static final String LIMIT_PREFIX = "limit:";

    /**
     * 学生id登录次数记录
     */
    public static final String LIMIT_TIME_PREFIX = "limit:time:";
}
