package cloud.makeronbean.course.common.limit;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author makeronbean
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Limit {
    // 默认限流访问次数
    String value() default "10";
    // 限流秒数
    String seconds() default "2";
}