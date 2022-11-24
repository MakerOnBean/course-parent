package cloud.makeronbean.course.gateway.filter;

import cloud.makeronbean.course.common.result.Result;
import cloud.makeronbean.course.common.result.ResultCodeEnum;
import cloud.makeronbean.course.common.util.IpUtil;
import cloud.makeronbean.course.gateway.constant.RedisConst;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpCookie;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.CollectionUtils;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author makeronbean
 */
@Component
public class AuthGlobalFilter implements GlobalFilter {

    /**
     * 路径匹配器
     */
    private final AntPathMatcher matcher = new AntPathMatcher();

    @Autowired
    private StringRedisTemplate redisTemplate;

/*    @Value("${authUrls.url}")
    private String authUrls;*/

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // 获取请求和响应对象
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();
        // 拦截内部请求
        String path = request.getURI().getPath();
        if (matcher.match("/api/**",path)) {
            return this.out(response, ResultCodeEnum.PERMISSION);
        }

        // 获取Token所存储的studentId
        List<String> dataList = getDataList(request);

        // 判断ip是否被盗用
        if (CollectionUtils.isEmpty(dataList) || "-1".equals(dataList.get(0))) {
            return out(response,ResultCodeEnum.ILLEGAL_REQUEST);
        }

        // 判断路径
        if (!matcher.match("/student/login",path) && StringUtils.isEmpty(dataList.get(1))) {
            return out(response,ResultCodeEnum.LOGIN_AUTH);
        }

        // 将studentId与xkCode存储到request中
        // 固定写法
        String studentId = dataList.get(1);
        String xkCode = dataList.get(2);

        if (!StringUtils.isEmpty(studentId) && !StringUtils.isEmpty(xkCode)) {
            request.mutate().header("studentId", studentId).header("xkCode",xkCode).build();
            return chain.filter(exchange.mutate().request(request).build());
        }

        // 放行
        return chain.filter(exchange);
    }


    /**
     * 从请求头中获取studentId
     */
    private List<String> getDataList(ServerHttpRequest request) {
        String token = "";
        String studentId = "";
        String xkCode = "";
        String ipFlag = "1";
        // 从请求头中获取 token
        List<String> tokenList = request.getHeaders().get("token");
        if (!CollectionUtils.isEmpty(tokenList)) {
            token = tokenList.get(0);
        }
        // 从cookie中获取 token
        if (StringUtils.isEmpty(token)){
            MultiValueMap<String, HttpCookie> cookies = request.getCookies();
            List<HttpCookie> httpCookies = cookies.get("token");
            if (!CollectionUtils.isEmpty(httpCookies)) {
                token = httpCookies.get(0).getValue();
            }
        }

        // 通过 token 获取 studentId
        if (!StringUtils.isEmpty(token)){
            String jsonStr = redisTemplate.opsForValue().get(RedisConst.USER_LOGIN_KEY_PREFIX + token);
            if (!StringUtils.isEmpty(jsonStr)) {
                JSONObject jsonObject = JSONObject.parseObject(jsonStr);
                String ip = jsonObject.getString("ip");
                String nowIp = IpUtil.getGatwayIpAddress(request);
                if (nowIp.equals(ip)){
                    studentId = jsonObject.getString("studentId");
                    xkCode = jsonObject.getString("xkCode");
                } else {
                    ipFlag = "-1";
                }
            }
        }

        // 如果token还没有值，那么就不存在token
        return Arrays.asList(ipFlag,studentId,xkCode);
    }


    /**
     * 拦截访问内部api的请求
     */
    private Mono<Void> out(ServerHttpResponse response, ResultCodeEnum resultCodeEnum) {
        // 构建返回的数据对象
        Result<Object> build = Result.build(null, resultCodeEnum);
        byte[] bytes = JSONObject.toJSONString(build).getBytes(StandardCharsets.UTF_8);
        DataBuffer data =response.bufferFactory().wrap(bytes);
        // 设置返回的编码格式
        response.getHeaders().add("Content-Type", "application/json;charset=UTF-8");
        // 返回
        return response.writeWith(Mono.just(data));
    }


}
