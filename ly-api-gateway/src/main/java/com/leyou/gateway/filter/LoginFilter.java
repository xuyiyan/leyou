package com.leyou.gateway.filter;

import com.leyou.auth.utils.JwtUtils;
import com.leyou.gateway.config.FilterProperties;
import com.leyou.gateway.config.JwtProperties;
import com.leyou.utils.CookieUtils;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Component
@EnableConfigurationProperties({JwtProperties.class, FilterProperties.class})
public class LoginFilter  extends ZuulFilter {
    @Autowired
    private FilterProperties filterProperties;

    @Autowired
    private  JwtProperties jwtProperties;


    //过滤类型
    @Override
    public String filterType() {
        return FilterConstants.PRE_TYPE;
    }

    @Override
    public int filterOrder() {
        //请求头之前，查看请求参数
        return FilterConstants.PRE_DECORATION_FILTER_ORDER-1;
    }

    @Override
    public boolean shouldFilter() {

        //在白名单不进行过滤
        RequestContext requestContext = RequestContext.getCurrentContext();
        HttpServletRequest request = requestContext.getRequest();
        //获取用户请求uri
        String requestURI = request.getRequestURI();
        //获取白名单
        List<String> allowPaths = filterProperties.getAllowPaths();
        for(String s:allowPaths){
            if(requestURI.contains(s)){
                return false;//不进行过滤



            }

        }


        return true;
    }

    @Override
    public Object run() throws ZuulException {

        //从请求中获取cookie,然后进行解密
        RequestContext requestContext = RequestContext.getCurrentContext();
        HttpServletRequest request = requestContext.getRequest();

        try{

            //从request获取cookie
            String token = CookieUtils.getCookieValue(request, jwtProperties.getCookieName());
            //eyJhbGciOiJSUzI1NiJ9.eyJpZCI6MzMsInVzZXJuYW1lIjoibWlrZTEyMzQiLCJleHAiOjE1ODk0NDMyODV9.RdVweZ-oHtJCZIJqVndp-SE0l7KB5H_tX3hDwjrJFKsbzGbO_QbWPhkPwuAmN3YssOdQq554diQIPMpv3SKPjtm_uIdDrSsmszK5GJREdyu32QwaFDhkzNKWBEHqcW5TpXhtD4hqmEMIRS2RPX84Rh13NpT3AT44jujHg6MrSq0
            JwtUtils.getInfoFromToken(token,jwtProperties.getPublicKey());

        }catch (Exception e){
            //拦截

            //不相应
            requestContext.setSendZuulResponse(false);
            //返回相应码
            requestContext.setResponseStatusCode(401);



        }


        return null;
    }
}
