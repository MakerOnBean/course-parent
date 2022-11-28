package cloud.makeronbean.course.activity.service.impl;

import cloud.makeronbean.course.activity.cache.StateCacheHelper;
import cloud.makeronbean.course.activity.service.ActivityService;
import cloud.makeronbean.course.common.constant.RedisConst;
import cloud.makeronbean.course.common.result.Result;
import cloud.makeronbean.course.common.result.ResultCodeEnum;
import cloud.makeronbean.course.model.activity.XkRecode;
import cloud.makeronbean.course.model.course.CourseSelectable;
import cloud.makeronbean.course.model.course.CourseSelectableKind;
import cloud.makeronbean.course.rabbit.constant.RabbitConst;
import cloud.makeronbean.course.rabbit.servcie.RabbitService;
import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.*;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author makeronbean
 * @createTime 2022-11-23  22:16
 * @description TODO
 */
@Service
@SuppressWarnings("all")
public class ActivityServiceImpl implements ActivityService {

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private ExecutorService executorService;

    @Autowired
    private RabbitService rabbitService;

    /**
     * 导入选课数据到redis
     */
    @Override
    public void importToRedis(List<CourseSelectableKind> courseSelectableKindList) {
        if (CollectionUtils.isEmpty(courseSelectableKindList)) {
            return;
        }

        for (CourseSelectableKind courseSelectableKind : courseSelectableKindList) {
            List<CourseSelectable> courseSelectableList = courseSelectableKind.getCourseSelectableList();

            /*
                key xkCode:xkCode
                hashKey  kindId
                value  courseSelectableKind
             */
            BoundHashOperations hashXkCode = redisTemplate.boundHashOps(RedisConst.XK_CODE_PREFIX + courseSelectableKind.getXkCode());
            hashXkCode.put(courseSelectableKind.getId().toString(),JSON.toJSONString(courseSelectableKind));

            /*
                key：xkKind:kindId
                hashKey:courseSelectableId
                value:courseSelectable
             */
            BoundHashOperations boundHashOps = redisTemplate.boundHashOps(RedisConst.XK_KIND_PREFIX + courseSelectableKind.getId());
            for (CourseSelectable courseSelectable : courseSelectableList) {
                Long courseSelectableId = courseSelectable.getId();
                boundHashOps.put(courseSelectableId.toString(), JSON.toJSONString(courseSelectable));

                /*
                    list
                    key:courseSelectableId
                    value:courseSelectableId * size
                 */
                BoundListOperations boundListOps = redisTemplate.boundListOps(RedisConst.XK_LIST_PREFIX + courseSelectable.getId());
                for (Integer i = 0; i < courseSelectable.getCount(); i++) {
                    boundListOps.leftPush(courseSelectableId.toString());
                }

                executorService.execute(() -> {
                    //StateCacheHelper.setStateTrue(courseSelectableId);
                    redisTemplate.convertAndSend(RedisConst.STATE_TOPIC,courseSelectableId+":true");
                });
            }
        }
    }


    /**
     * 根据选课码查询可选课的类别
     */
    @Override
    public List<CourseSelectableKind> getKindList(String xkCode) {
        List<CourseSelectableKind> list = null;
        BoundHashOperations hashXkCode = redisTemplate.boundHashOps(RedisConst.XK_CODE_PREFIX + xkCode);
        List<String> values = hashXkCode.values();
        if (!CollectionUtils.isEmpty(values)) {
            list = values.stream()
                    .map(item -> JSON.parseObject(item, CourseSelectableKind.class))
                    .collect(Collectors.toList());
        }
        return list;
    }


    /**
     * 获取具体可选课程列表
     */
    @Override
    public List<CourseSelectable> getDetailList(String xkCode, Long kindId, String studentId) {

        // 判断选课是否对应类别
        BoundHashOperations hashXkCode = redisTemplate.boundHashOps(RedisConst.XK_CODE_PREFIX + xkCode);
        if (Boolean.FALSE.equals(hashXkCode.hasKey(kindId.toString()))) {
            redisTemplate.opsForValue().set(RedisConst.LIMIT_PREFIX + studentId, "select --> 选课码不对应类别", 30L, TimeUnit.MINUTES);
            return null;
        }

        // 判断是否已经选过了 kindId 对应类别下的课程
        Long courseSelectableId = null;
        BoundHashOperations hashCourse = redisTemplate.boundHashOps(RedisConst.XK_KIND_PREFIX + kindId);
        Set<String> keys = hashCourse.keys();
        for (String item : keys) {
            BoundHashOperations hashStudent = redisTemplate.boundHashOps(RedisConst.XK_COURSE_PREFIX + item);
            String flag = (String) hashStudent.get(studentId);
            if (flag != null && !"2".equals(flag)) {
                courseSelectableId = Long.valueOf(item);
            }
        }

        List<String> values = hashCourse.values();

        // 返回结果集处理
        List<CourseSelectable> resultList = values.stream()
                .map(item -> JSON.parseObject(item, CourseSelectable.class))
                .peek(item -> {
                    Long size = redisTemplate.boundListOps(RedisConst.XK_LIST_PREFIX + item.getId()).size();
                    item.setCount(size);
                    if (size == null || size == 0) {
                        item.setSelectFlag("不可选");
                    }
                })
                .collect(Collectors.toList());

        // 处理返回数据是否可以被选 可选:1 被选择:2 不可选:3
        for (CourseSelectable courseSelectable : resultList) {
            if (courseSelectable.getSelectFlag() == null) {
                if (courseSelectableId != null) {
                    if (courseSelectableId.equals(courseSelectable.getId())) {
                        // 选择了这门课课程 2
                        courseSelectable.setSelectFlag("已选");
                    } else {
                        // 选择了其他课程 3
                        courseSelectable.setSelectFlag("不可选");
                    }
                } else {
                    // 可以选择 1
                    courseSelectable.setSelectFlag("可选");
                }
            }
        }
        return resultList;
    }


    /**
     * 选择具体的课程
     */
    @Override
    public Result select(Long courseSelectableId, Long studentId, String xkCode, Long kindId) {

        // 选课码不对应类别
        BoundHashOperations hashXkCode = redisTemplate.boundHashOps(RedisConst.XK_CODE_PREFIX + xkCode);
        if (Boolean.FALSE.equals(hashXkCode.hasKey(kindId.toString()))) {
            redisTemplate.opsForValue().set(RedisConst.LIMIT_PREFIX + studentId, "select --> 选课码不对应类别", 30L, TimeUnit.MINUTES);
            return Result.build(null,ResultCodeEnum.ILLEGAL_REQUEST);
        }

        BoundHashOperations hashKind = redisTemplate.boundHashOps(RedisConst.XK_KIND_PREFIX + kindId);
        Set<String> keys = hashKind.keys();
        // 类别中没有对应选课
        if (!keys.contains(String.valueOf(courseSelectableId))) {
            redisTemplate.opsForValue().set(RedisConst.LIMIT_PREFIX + studentId, "select --> 类别中没有对应选课", 30L, TimeUnit.MINUTES);
            return Result.build(null,ResultCodeEnum.ILLEGAL_REQUEST);
        }

        /*
            key   xk:student:courseSelectableId
            hashKey  studentId
            value    0(正在处理中) 1(处理成功) 2(处理失败)
         */
        for (String item : keys) {
            BoundHashOperations hashResult = redisTemplate.boundHashOps(RedisConst.XK_COURSE_PREFIX + item);
            String flag = (String) hashResult.get(studentId.toString());
            if (flag != null && !"2".equals(flag)) {
                return Result.build(null, ResultCodeEnum.REPEAT);
            }
        }

        // 判断课程是否还存在名额
        if (!StateCacheHelper.canHandle(courseSelectableId)) {
            return Result.build(null, ResultCodeEnum.FULL);
        }

        // 从选课list中弹出一个元素
        Object obj = redisTemplate.boundListOps(RedisConst.XK_LIST_PREFIX + courseSelectableId).rightPop();
        if (obj != null) {
            executorService.execute(() -> {
                // 向队列中发送消息
                XkRecode recode = new XkRecode(studentId, courseSelectableId, xkCode);
                rabbitService.send(RabbitConst.EXCHANGE_DIRECT_XK, RabbitConst.ROUTING_XK, recode);
            });
            executorService.execute(() -> {
                // redis中添加标记，防止重复点击选课
                BoundHashOperations hashResult = redisTemplate.boundHashOps(RedisConst.XK_COURSE_PREFIX + courseSelectableId);
                hashResult.put(studentId.toString(), "0");
            });
            return Result.ok().message("正在处理中");
        } else {
            redisTemplate.convertAndSend(RedisConst.STATE_TOPIC,courseSelectableId+":false");
            return Result.build(null, ResultCodeEnum.FULL);
        }
    }


    /**
     * 数据回滚
     */
    @Override
    public void rollback(XkRecode xkRecode) {
        // 1、状态位
        redisTemplate.convertAndSend(RedisConst.STATE_TOPIC,xkRecode.getCourseSelectableId()+":true");

        // 2、防止超选的list
        redisTemplate.boundListOps(RedisConst.XK_LIST_PREFIX + xkRecode.getCourseSelectableId()).leftPush(xkRecode.getCourseSelectableId().toString());

        // 3、用户选课状态
        redisTemplate.boundHashOps(RedisConst.XK_COURSE_PREFIX + xkRecode.getCourseSelectableId()).put(xkRecode.getStudentId().toString(), "2");
    }


    /**
     * 查询选课是否成功
     */
    @Override
    public Result isSuccess(Long courseSelectableId, String xkCode, Long studentId) {
        BoundHashOperations boundHashOps = redisTemplate.boundHashOps(RedisConst.XK_COURSE_PREFIX + courseSelectableId);
        String value = (String) boundHashOps.get(studentId.toString());
        // 没有选课记录
        if (StringUtils.isEmpty(value)) {
            redisTemplate.opsForValue().set(RedisConst.LIMIT_PREFIX + studentId, "isSuccess ---> 不存在选课记录", 30L, TimeUnit.MINUTES);
            return Result.build(null,ResultCodeEnum.ILLEGAL_REQUEST);
        } else {
            switch (value) {
                case "0":
                    return Result.build(null, ResultCodeEnum.RUN);
                case "1":
                    return Result.build(null, ResultCodeEnum.SUCCESS);
                case "2":
                    return Result.build(null, ResultCodeEnum.FULL);
                default:
                    redisTemplate.opsForValue().set(RedisConst.LIMIT_PREFIX + studentId, "isSuccess --> 查询不存在的选课数据", 30L, TimeUnit.MINUTES);
                    return Result.build(null,ResultCodeEnum.ILLEGAL_REQUEST);
            }
        }
    }


    /**
     * 清除过期的选课数据
     */
    @Override
    public void removeCache() {
        Set<String> keys = redisTemplate.keys("*");
        redisTemplate.delete(keys);
        redisTemplate.convertAndSend(RedisConst.STATE_TOPIC,"clear");
    }
}
