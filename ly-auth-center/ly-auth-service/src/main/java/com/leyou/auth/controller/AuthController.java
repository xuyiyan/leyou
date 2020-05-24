package com.leyou.auth.controller;


import com.leyou.auth.config.JwtProperties;
import com.leyou.auth.entity.UserInfo;
import com.leyou.auth.service.AuthService;
import com.leyou.auth.utils.JwtUtils;
import com.leyou.utils.CookieUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
@EnableConfigurationProperties(JwtProperties.class)
public class AuthController {

    @Autowired
    private AuthService authService;
    @Autowired(required = false)
    private JwtProperties jwtProperties;


    @PostMapping("accredit")
    public ResponseEntity<Void> accredit(@RequestParam("username") String username,
                                         @RequestParam("password") String password, HttpServletRequest request, HttpServletResponse response){


        String token=authService.accredit(username,password);
        if(StringUtils.isBlank(token)){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();//401

        }
        //使用工具类，把token写入cookie中
        CookieUtils.setCookie(request,response,jwtProperties.getCookieName(),token,jwtProperties.getCookieMaxAge(),null,true);


        //返回
        return ResponseEntity.ok().build();

    }

    //http://api.leyou.com/api/auth/verify

    @GetMapping("verify")
    public ResponseEntity<UserInfo> verify(@CookieValue("LY_TOKEN") String token, HttpServletRequest request, HttpServletResponse response){
        try{
            //解密  （需要解密的字符串，公钥）
            UserInfo userInfo = JwtUtils.getInfoFromToken(token, jwtProperties.getPublicKey());


            //再次产生token,写入cookie
            String token1 = JwtUtils.generateToken(userInfo, jwtProperties.getPrivateKey(), jwtProperties.getCookieMaxAge());
            //使用工具类，把token写入cookie中
            CookieUtils.setCookie(request,response,jwtProperties.getCookieName(),token1,jwtProperties.getCookieMaxAge(),null,true);


            return ResponseEntity.ok(userInfo);

        }catch (Exception e){

            e.printStackTrace();

        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();





    }


}
