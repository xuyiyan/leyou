package com.leyou.item.service;

import com.leyou.item.mapper.CategoryMapper;
import com.leyou.item.pojo.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CategoryService {

    @Autowired(required = false)
    private CategoryMapper categoryMapper;

    public List<Category> queryByParentId(Long id) {

        Category category = new Category();
        category.setParentId(id);

        List<Category> categories = categoryMapper.select(category);
        //select * from tb_category parent_id=0;
        return categories;
    }


    public List<Category> queryByBrandId(Long bid) {

        //根据品牌id查询所有的分类
        return categoryMapper.queryByBrandId(bid);
    }


    public List<String> queryNamesByIds(List<Long> cids) {
        List<String> names=new ArrayList<>();
        //select * from tb_category where id in (74,75,76);
        List<Category> categories = this.categoryMapper.selectByIdList(cids);

        for(Category category:categories){

            names.add(category.getName());
        }
        return names;


    }
}
