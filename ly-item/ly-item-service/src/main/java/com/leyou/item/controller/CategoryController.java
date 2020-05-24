package com.leyou.item.controller;

import com.leyou.item.pojo.Category;
import com.leyou.item.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("category")
public class CategoryController {
    //#http://127.0.0.1:9081/category/list?pid=0

    @Autowired
    private CategoryService categoryService;

    @RequestMapping("list")
    public ResponseEntity<List<Category>> queryByParentId(@RequestParam("pid") Long id){

        //查询分类表
        List<Category> categories=categoryService.queryByParentId(id);
        if(null!=categories&&categories.size()!=0){


            return ResponseEntity.ok(categories);//返回页面相应码200，数据

        }
        return  ResponseEntity.status(HttpStatus.NO_CONTENT).build();//204


    }

    //http://api.leyou.com/api/item/category/bid/325402
    @GetMapping("bid/{bid}")
    public ResponseEntity<List<Category>> queryByBrandId(@PathVariable("bid") Long bid){

        List<Category> categories=categoryService.queryByBrandId(bid);
        if(categories!=null&&categories.size()>0){

            return  ResponseEntity.ok(categories);
        }

        return  ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping("names")
    public ResponseEntity<List<String>> queryNamesByIds(@RequestParam("ids") List<Long> ids){
        //根据分类id 查询分类名称
        List<String> list = this.categoryService.queryNamesByIds(ids);
        if(null!=list&&list.size()>0){
            return ResponseEntity.ok(list);

        }
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();

    }


}
