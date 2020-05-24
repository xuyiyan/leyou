package com.leyou.cart.service;


import com.fasterxml.jackson.core.type.TypeReference;
import com.leyou.auth.entity.UserInfo;
import com.leyou.cart.interceptors.LoginInterceptor;
import com.leyou.cart.pojo.Cart;
import com.leyou.utils.JsonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CartService {

    @Autowired
    private StringRedisTemplate template;

    private  static final String KEY_PREFIX = "ly:cart:uid:";


    public void addCart(Cart cart) {
        //怎么获得用户id
        //获取用户信息
        UserInfo userInfo = LoginInterceptor.getLoginUser();
        BoundHashOperations<String, Object, Object> boundHashOperations = template.boundHashOps(KEY_PREFIX + userInfo.getId());

        Object o = boundHashOperations.get(cart.getSkuId().toString());

        if(null!=o){
            //json字符串变成cart对象
            Cart cart1 = JsonUtils.nativeRead(o.toString(), new TypeReference<Cart>() {
            });
            cart1.setNum(cart1.getNum()+cart.getNum());
            //放入redis
            boundHashOperations.put(cart.getSkuId().toString(), JsonUtils.serialize(cart1));
        }else{
            //把cart变成json格式字符串
            String c = JsonUtils.serialize(cart);

            //放入redis
            boundHashOperations.put(cart.getSkuId().toString(),c);
        }




    }


    public List<Cart> queryCarts() {

        //根据用户id查询redis
        //获取用户信息
        UserInfo userInfo = LoginInterceptor.getLoginUser();

        BoundHashOperations<String, Object, Object> boundHashOperations = template.boundHashOps(KEY_PREFIX + userInfo.getId());


        List<Object> values = boundHashOperations.values();

        List<Cart>  carts=new ArrayList<>();

        if(values!=null){
            for(Object o:values){
                //把o对象变成cart
                Cart cart = JsonUtils.nativeRead(o.toString(), new TypeReference<Cart>() {
                });
                carts.add(cart);
            }
        }
        return carts;
    }

    public void updateIncrementCart(Cart cart) {

        //根据用户id查询redis
        //获取用户信息
        UserInfo userInfo = LoginInterceptor.getLoginUser();

        BoundHashOperations<String, Object, Object> boundHashOperations = template.boundHashOps(KEY_PREFIX + userInfo.getId());


        String  o = boundHashOperations.get(cart.getSkuId().toString()).toString();

        //{"userId":null,"skuId":12613550297,"title":"小米（MI） 小米4A 红米4A 4G手机 金色 公开全网通(2G+16G)","image":"http://image.leyou.com/images/8/4/1524297394988.jpg","price":54100,"num":1,"ownSpec":"{\"4\":\"金色\",\"12\":\"2GB\",\"13\":\"16GB\"}"}

        //把json格式字符串变成对象
        Cart cart1 = JsonUtils.nativeRead(o, new TypeReference<Cart>() {
        });
        cart1.setNum(cart1.getNum()+1);

        //cart1放入redis
        boundHashOperations.put(cart.getSkuId().toString(), JsonUtils.serialize(cart1));


    }

    public void deleteCart(Long skuId) {



        //获取用户信息
        UserInfo userInfo = LoginInterceptor.getLoginUser();

        BoundHashOperations<String, Object, Object> boundHashOperations = template.boundHashOps(KEY_PREFIX + userInfo.getId());

        //删除redis
        boundHashOperations.delete(String.valueOf(skuId));
    }
}
