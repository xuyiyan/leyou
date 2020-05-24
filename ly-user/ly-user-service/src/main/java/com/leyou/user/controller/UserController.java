package com.leyou.user.controller;


import com.leyou.user.pojo.User;
import com.leyou.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class UserController {

    @Autowired
    private UserService userService;

    ///check/tom/1
    ///check/18696196333/2

    //GET /check/{data}/{type}
    @GetMapping("/check/{data}/{type}")
    public ResponseEntity<Boolean> checkUser(@PathVariable("data") String data,@PathVariable("type") Integer type){
        Boolean boo=this.userService.checkUser(data,type);
        if (null==boo) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        return ResponseEntity.ok(boo);

    }


    //生成短信验证码
    @PostMapping("code")
    public ResponseEntity<Void>  sendVerifyCode(@RequestParam("phone") String phone){

        Boolean boo=userService.sendVerifyCode(phone);
        if(boo ==null ||!boo){


            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();//500
        }
        return  ResponseEntity.status(HttpStatus.CREATED).build();//201


    }


    //用户注册
    @PostMapping("register")
    public ResponseEntity<Void> createUser(User user, @RequestParam("code") String code){

        Boolean boo=this.userService.register(user,code);

        if(boo ==null ||!boo){


            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();//500
        }
        return  ResponseEntity.status(HttpStatus.CREATED).build();//201


    }

    //GET /query
    @GetMapping("query")
    public ResponseEntity<User> queryUser(@RequestParam("username") String username, @RequestParam("password") String password){

       User user= userService.queryUser(username,password);
       if(null==user){
           return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

       }
       return ResponseEntity.ok(user);


    }


}
