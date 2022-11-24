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

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;

/**
 * @author makeronbean
 * @createTime 2022-11-23  22:16
 * @description TODO
 */
@Service
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
            //courseSelectableKind.setCourseSelectableList(null);
            /*
                set:
                key  xkCode:xkCode
                value  courseSelectableKind
             */
            BoundSetOperations boundSetOps = redisTemplate.boundSetOps(RedisConst.XK_CODE_PREFIX + courseSelectableKind.getXkCode());
            boundSetOps.add(JSON.toJSONString(courseSelectableKind));

            /*
                key：xk:xkCode
                hashKey:courseSelectableId
                value:courseSelectable
             */
            BoundHashOperations boundHashOps = redisTemplate.boundHashOps(RedisConst.XK_PREFIX + courseSelectableKind.getXkCode());
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
                    StateCacheHelper.put(courseSelectableId, courseSelectable.getCount());
                });
            }
        }
    }


    /**
     * 根据选课码查询可选课的类别
     */
    @Override
    public List<CourseSelectableKind> getKindList(String xkCode) {
        List<CourseSelectableKind> list = new ArrayList<>();
        BoundSetOperations boundSetOps = redisTemplate.boundSetOps(RedisConst.XK_CODE_PREFIX + xkCode);
        Set<String> set = boundSetOps.members();
        if (!CollectionUtils.isEmpty(set)) {
            set.forEach(item -> {
                CourseSelectableKind courseSelectableKind = JSON.parseObject(item, CourseSelectableKind.class);
                list.add(courseSelectableKind);
            });
        }
        return list;
    }


    /**
     * 获取具体可选课程列表
     */
    @Override
    public List<CourseSelectable> getDetailList(String xkCode, Long kindId) {
        // 在redis中存放
        BoundHashOperations boundHashOps = this.redisTemplate.boundHashOps(RedisConst.XK_PREFIX + xkCode);
        List<String> list = boundHashOps.values();

        if (!CollectionUtils.isEmpty(list)) {
            return list.stream()
                    .map(item -> JSON.parseObject(item, CourseSelectable.class))
                    .filter(item -> kindId.equals(item.getKindId()))
                    .peek(item -> item.setCount(StateCacheHelper.get(item.getId())))
                    .collect(Collectors.toList());
        }
        return null;
    }


    /**
     * 选择具体的课程
     */
    @Override
    public Result select(Long courseSelectableId, Long studentId, String xkCode) {

        /*
            key   xk:student:courseSelectableId
            hashKey  studentId
            value    0(正在处理中) 1(处理成功) 2(处理失败)
         */
        BoundHashOperations boundHashOps = redisTemplate.boundHashOps(RedisConst.XK_STUDENT_PREFIX + courseSelectableId);
        String flag = (String) boundHashOps.get(studentId.toString());
        // 判断是否已经选过课了
        if (flag != null && !"2".equals(flag)) {
            return Result.build(null, ResultCodeEnum.REPEAT);
        }


        // 判断课程是否还存在名额
        if (!StateCacheHelper.canHandle(courseSelectableId)) {
            return Result.build(null, ResultCodeEnum.FULL);
        }

        // 从选课list中弹出一个元素
        Object obj = redisTemplate.boundListOps(RedisConst.XK_LIST_PREFIX + courseSelectableId).rightPop();
        if (obj != null) {

            executorService.execute(() -> {
                // 状态位数量-1
                StateCacheHelper.sub(courseSelectableId);
            });
            executorService.execute(() -> {
                // 向队列中发送消息
                XkRecode recode = new XkRecode(studentId, courseSelectableId, xkCode);
                rabbitService.send(RabbitConst.EXCHANGE_DIRECT_XK, RabbitConst.ROUTING_XK, recode);
            });

            executorService.execute(() -> {
                // redis中添加标记，防止重复点击选课
                boundHashOps.put(studentId.toString(), "0");
            });
            return Result.ok().message("正在处理中");
        } else {
            return Result.build(null, ResultCodeEnum.FULL);
        }
    }


    /**
     * 数据回滚
     */
    @Override
    public void rollback(XkRecode xkRecode) {
        // 1、状态位
        StateCacheHelper.rollback(xkRecode.getCourseSelectableId());

        // 2、防止超选的list
        redisTemplate.boundListOps(RedisConst.XK_LIST_PREFIX + xkRecode.getCourseSelectableId()).leftPush(xkRecode.getCourseSelectableId().toString());

        // 3、用户选课状态
        redisTemplate.boundHashOps(RedisConst.XK_STUDENT_PREFIX + xkRecode.getXkCode()).put(xkRecode.getStudentId().toString(), "2");
    }


    /**
     * 查询选课是否成功
     */
    @Override
    public Result isSuccess(Long courseSelectableId, String xkCode, Long studentId) {
        BoundHashOperations boundHashOps = redisTemplate.boundHashOps(RedisConst.XK_STUDENT_PREFIX + courseSelectableId);
        String value = (String) boundHashOps.get(studentId.toString());
        // 没有选课记录
        if (StringUtils.isEmpty(value)) {
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
                    return Result.build(null,ResultCodeEnum.ILLEGAL_REQUEST);
            }
        }
    }

}
