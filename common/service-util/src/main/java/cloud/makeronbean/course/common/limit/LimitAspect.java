package cloud.makeronbean.course.common.limit;

import cloud.makeronbean.course.common.constant.RedisConst;
import cloud.makeronbean.course.common.result.Result;
import cloud.makeronbean.course.common.result.ResultCodeEnum;
import cloud.makeronbean.course.common.util.AuthContextHolder;
import cloud.makeronbean.course.common.util.DateUtil;
import cloud.makeronbean.course.common.util.IpUtil;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * @author makeronbean
 * @createTime 2022-11-25  12:36
 * @description TODO
 */
@Component
@Aspect
@Slf4j
public class LimitAspect implements Ordered {

    @Autowired
    private RedisTemplate redisTemplate;

    @Around("@annotation(Limit)")
    public Object ipLimit(ProceedingJoinPoint joinPoint) throws Throwable {
        // 获取方法签名
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        // 获取注解
        Limit limit = signature.getMethod().getAnnotation(Limit.class);
        // 注解中最多连接次数
        String maxTime = limit.value();
        // 注解中检测周期
        String seconds = limit.seconds();

        // 当前request请求
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        // 当前ip
        String ip = IpUtil.getIpAddress(request);
        // 当前请求学生id
        String studentId = AuthContextHolder.getStudentId(request);

        Boolean isLimit = redisTemplate.hasKey(RedisConst.LIMIT_PREFIX + studentId);
        if (Boolean.TRUE.equals(isLimit)) {
            return Result.build(null, ResultCodeEnum.LIMIT);
        }

        Boolean hasKey = redisTemplate.hasKey(RedisConst.LIMIT_TIME_PREFIX + studentId);
        if (hasKey) {
            Long result = redisTemplate.opsForValue().increment(RedisConst.LIMIT_TIME_PREFIX + studentId);
            if (result != null && result > Long.parseLong(maxTime)) {
                String date = DateUtil.formatDate(new Date());
                redisTemplate.opsForValue().set(RedisConst.LIMIT_PREFIX + studentId, ip, 30L, TimeUnit.MINUTES);
                log.info("新增限制操作信息：studentId:{},ip:{},时间:{}",studentId,ip,date);
                return Result.build(null,ResultCodeEnum.LIMIT);
            }
        } else {
            redisTemplate.opsForValue().set(RedisConst.LIMIT_TIME_PREFIX + studentId, 1, Long.parseLong(seconds), TimeUnit.SECONDS);
        }
        return joinPoint.proceed(joinPoint.getArgs());
    }

/*    @Before("@annotation(limit)")
    public Object ipLimit(JoinPoint joinPoint, Limit limit) {

    }*/

    @Override
    public int getOrder() {
        return Integer.MIN_VALUE;
    }
}
