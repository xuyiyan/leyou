package com.leyou.user.service;

import com.leyou.user.mapper.UserMapper;
import com.leyou.user.pojo.User;
import com.leyou.user.utils.CodecUtils;
import com.leyou.utils.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.concurrent.TimeUnit;

@Service
public class UserService {

    @Autowired
    private StringRedisTemplate template;

    private static final  String  KEY_PREFIX = "user:code:phone:";


    @Autowired
    private UserMapper userMapper;

    public Boolean checkUser(String data, Integer type) {
        ///check/tom/1
        ///check/18696196333/2
        User user = new User();

        switch (type){
            case  1:
                user.setUsername(data);
                break;
            case 2:
                user.setPhone(data);
        }

 //如果等于1
//        //select count(*) from tb_user where username=tom
//如果等于2
//        //select count(*) from tb_user where phone=186213123

        int i = this.userMapper.selectCount(user);//1 0
        boolean boo=i!=1;//false
                         //true
        return boo;
    }

    public Boolean sendVerifyCode(String phone) {
        //phone 18696196389
        //产生5位数验证码
        String code = NumberUtils.generateCode(5);//23453

        try{
            //把验证码发送到18696196389 手机上。略过
            ValueOperations<String, String> stringStringValueOperations = template.opsForValue();
            stringStringValueOperations.set(KEY_PREFIX+phone,code,5, TimeUnit.MINUTES);
            //                              user:code:phone:18696196389 23453

            return  true;
        }catch (Exception e){

            e.printStackTrace();
            return false;
        }


    }

    public Boolean register(User user, String code) {
        //校验验证码
        ValueOperations<String, String> stringStringValueOperations = template.opsForValue();
        //从redis根据key获取验证码
        String s = stringStringValueOperations.get(KEY_PREFIX + user.getPhone());
        if(!code.equals(s)){
            //验证码不正确
            return false;

        }

        user.setId(null);
        user.setCreated(new Date());
        // 生成盐
        String salt = CodecUtils.generateSalt();
        user.setSalt(salt);//05b0f203987e49d2b72b20b95e0e57d9

        //密码加密
        user.setPassword(CodecUtils.md5Hex(user.getPassword(),salt));

        Boolean flag=this.userMapper.insertSelective(user)==1;

        //如果返回true
        if(true){
            //删除redis
            this.template.delete(KEY_PREFIX + user.getPhone());
        }
        return flag;


    }

    public User queryUser(String username, String password) {
        //根据用户名查询用户
        User user = new User();
        user.setUsername(username);
        //select * from tb_user where username=hehe
        User user1 = this.userMapper.selectOne(user);
        if(null==user1){
            return null;

        }
        //密码比较

        //使用盐对用户输入的密码进行加密
        String s = CodecUtils.md5Hex(password, user1.getSalt());

        //密码比较
        if(!user1.getPassword().equals(s)){

            return null;

        }
        return user1;


    }
}
