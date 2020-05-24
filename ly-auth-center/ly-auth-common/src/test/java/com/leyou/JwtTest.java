package com.leyou;


import com.leyou.auth.entity.UserInfo;
import com.leyou.auth.utils.JwtUtils;
import com.leyou.auth.utils.RsaUtils;
import org.junit.Before;
import org.junit.Test;

import java.security.PrivateKey;
import java.security.PublicKey;

public class JwtTest {


    private static final String pubKeyPath="d:\\temp\\rsa.pub";//公钥路径
    private static final String priKeyPath="d:\\temp\\rsa.pri";//私钥路径

    private PublicKey publicKey;//公钥对象
    private PrivateKey privateKey;//私钥对象

//    @Test
//    public void  test1() throws Exception {
//        //公钥路径 私钥路径 密钥
//        RsaUtils.generateKey(pubKeyPath,priKeyPath,"1234");
//
//    }
    @Before
    public void load() throws Exception {
        //读取公钥赋值 publicKey
        publicKey= RsaUtils.getPublicKey(pubKeyPath);
        privateKey= RsaUtils.getPrivateKey(priKeyPath);

    }

    @Test
    public void test2() throws Exception {
        //创建对象

        UserInfo userInfo = new UserInfo(100L, "tom");
        //使用工具方法加密  参数（载荷，私钥 5）
        String token = JwtUtils.generateToken(userInfo, privateKey, 5);

        System.out.println(token);

    }


    @Test
    public void test3() throws Exception {

        String s="eyJhbGciOiJSUzI1NiJ9.eyJpZCI6MTAwLCJ1c2VybmFtZSI6InRvbSIsImV4cCI6MTU4OTQyNTM0MH0.W7h04EXg4l9Sf4C8mhtM92wVz_sZY_lsjVMjtlJylNrkb1Edi6pZBiGJAtWbOmMXr3speqWw3pYqUrNlAgTuxtTUsMPsESWgmnsH2lWfnSmoIyiACHSl_a9iSbpi3H3bSs-czdKcFqh-IykD-2Q1771gzfhtkIAYDHmi4SBpPAg";


        //解密  （需要解密的字符串，公钥）
        UserInfo userInfo = JwtUtils.getInfoFromToken(s, publicKey);
        System.out.println(userInfo.getUsername());
        System.out.println(userInfo.getId());

    }


}
