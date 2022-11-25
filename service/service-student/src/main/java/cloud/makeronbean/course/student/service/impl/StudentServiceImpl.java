package cloud.makeronbean.course.student.service.impl;

import cloud.makeronbean.course.common.constant.RedisConst;
import cloud.makeronbean.course.common.util.IpUtil;
import cloud.makeronbean.course.common.util.MD5;
import cloud.makeronbean.course.model.student.StudentInfo;
import cloud.makeronbean.course.model.student.StudentLogin;
import cloud.makeronbean.course.student.mapper.StudentInfoMapper;
import cloud.makeronbean.course.student.mapper.StudentLoginMapper;
import cloud.makeronbean.course.student.service.StudentService;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @author makeronbean
 * @createTime 2022-11-23  14:27
 * @description TODO
 */
@Service
public class StudentServiceImpl implements StudentService {

    @Autowired
    private StudentLoginMapper studentLoginMapper;

    @Autowired
    private StudentInfoMapper studentInfoMapper;

    @Autowired
    private StringRedisTemplate redisTemplate;



    /**
     * 登录
     */
    @Override
    public Map<String, Object> login(StudentLogin login, HttpServletRequest request) {
        if (StringUtils.isEmpty(login.getLoginName()) || StringUtils.isEmpty(login.getLoginPass())) {
            return null;
        }

        // 判断登录
        String password = MD5.encrypt(login.getLoginPass());
        LambdaQueryWrapper<StudentLogin> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(StudentLogin::getLoginName,login.getLoginName())
                        .eq(StudentLogin::getLoginPass,password);
        login = studentLoginMapper.selectOne(wrapper);
        if (login == null || login.getStudentId() == null) {
            return null;
        }
        Long studentId = login.getStudentId();

        // 查询选课码
        // 根据studentId查询xkCode
        String xkCode = studentInfoMapper.selectXkCode(studentId);

        // 生成token
        String token = UUID.randomUUID().toString().replaceAll("-", "");

        // 获取请求的ip
        String ip = IpUtil.getIpAddress(request);

        // 将登录结果存储到redis中
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("studentId",studentId);
        jsonObject.put("ip",ip);
        jsonObject.put("xkCode",xkCode);
        redisTemplate.opsForValue().set(RedisConst.USER_LOGIN_KEY_PREFIX + token,jsonObject.toJSONString(),RedisConst.USER_KEY_TIMEOUT, TimeUnit.SECONDS);

        // 构建返回值结果
        Map<String,Object> result = new HashMap<>(1);
        result.put("token",token);
        return result;
    }


    /**
     * 查询个人信息
     */
    @Override
    public StudentInfo detail(Long studentId) {
        return studentInfoMapper.selectStudentById(studentId);
    }


    /**
     * 通过studentId获取classId
     */
    @Override
    public Long getClassIdByStudentId(Long studentId) {
        StudentInfo studentInfo = studentInfoMapper.selectById(studentId);
        if (studentInfo != null) {
            return studentInfo.getClassId();
        }
        return null;
    }
}
