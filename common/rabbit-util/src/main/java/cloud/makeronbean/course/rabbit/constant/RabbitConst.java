package cloud.makeronbean.course.rabbit.constant;

/**
 * @author makeronbean
 * @createTime 2022-11-23  18:26
 * @description TODO
 */
public class RabbitConst {
    /**
     * 定时任务
     */
    public static final String EXCHANGE_DIRECT_TASK_IMPORT = "exchange.direct.task";
    public static final String ROUTING_TASK_IMPORT = "routing.task";
    public static final String QUEUE_TASK_IMPORT = "queue.task";

    public static final String EXCHANGE_DIRECT_TASK_CLEAR = "exchange.direct.task";
    public static final String ROUTING_TASK_CLEAR = "routing.task";
    public static final String QUEUE_TASK_CLEAR = "queue.task";


    public static final String EXCHANGE_DIRECT_XK = "exchange.direct.xk";
    public static final String ROUTING_XK = "routing.xk";
    public static final String QUEUE_XK  = "queue.xk";

    public static final String EXCHANGE_DIRECT_ROLLBACK = "exchange.direct.rollback";
    public static final String ROUTING_ROLLBACK = "routing.rollback";
    public static final String QUEUE_ROLLBACK  = "queue.rollback";
}
