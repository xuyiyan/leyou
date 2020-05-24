package com.leyou.cart.controller;


import com.leyou.cart.pojo.Cart;
import com.leyou.cart.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class CartController {

    @Autowired
    private CartService cartService;

    @PostMapping
    public ResponseEntity<Void> addCart(@RequestBody Cart cart){
        //增加
        this.cartService.addCart(cart);
        return ResponseEntity.status(HttpStatus.CREATED).build();

    }

    //http://api.leyou.com/api/cart
    @GetMapping
    public ResponseEntity<List<Cart>> queryCarts(){
        List<Cart> carts= cartService.queryCarts();
        if(null!=carts&&carts.size()>0){
            return ResponseEntity.ok(carts);
        }
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();

    }

    //http://api.leyou.com/api/cart/increment
    @PutMapping("increment")
    public ResponseEntity<Void> updateIncrementCart(@RequestBody Cart cart){

        cartService.updateIncrementCart(cart);
        return  ResponseEntity.status(HttpStatus.CREATED).build();


    }

    //http://api.leyou.com/api/cart/12613550297
    @DeleteMapping("{skuId}")
    public  ResponseEntity<Void> deleteCart(@PathVariable("skuId") Long skuId){
        cartService.deleteCart(skuId);

        return  ResponseEntity.status(HttpStatus.CREATED).build();
    }


}
