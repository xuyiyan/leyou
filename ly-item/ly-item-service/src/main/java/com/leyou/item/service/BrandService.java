package com.leyou.item.service;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.leyou.common.pojo.PageResult;
import com.leyou.item.mapper.BrandMapper;
import com.leyou.item.pojo.Brand;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

@Service
public class BrandService {

    @Autowired(required = false)
    private BrandMapper brandMapper;

    public PageResult<Brand> pageQuery(Integer page, Integer rows, String sortBy, Boolean desc, String key) {
        //page=1&rows=5&sortBy=id&desc=false&key=iphone

        //开启分页助手
        PageHelper.startPage(page,rows);
        //过滤
        Example example = new Example(Brand.class);
        if(StringUtils.isNoneBlank(key)){
            //创建查询条件的构造对象
            Example.Criteria criteria = example.createCriteria();
            criteria.andLike("name","%"+key+"%");
            //name like "%"+iphone+"%"
        }
        //判断排序字段不为空
        if(StringUtils.isNoneBlank(sortBy)){

            example.setOrderByClause(sortBy+(desc?" DESC":" ASC"));
            //order by id ASC

        }
        //查询结果并转化分页条件结果
        Page<Brand> brandPage=(Page<Brand>)brandMapper.selectByExample(example);
        //通用mappper select * from tb_brand where name like "%"+iphone+"%" order by id ASC
        //分页助手 拦截sql
        //select * from tb_brand where name like "%"+iphone+"%" order by id ASC  limit 0,5
        //select count(*) from tb_brand name like "%"+iphone+"%"

        //PageResult(Long total, Long totalPage, List<T> items)
        PageResult pageResult = new PageResult(brandPage.getTotal(), new Long(brandPage.getPages()), brandPage.getResult());
        return pageResult;


    }

    @Transactional //事务
    public void addBrand(Brand brand, List<Long> cids) {

        //新增品牌信息
        brandMapper.insertSelective(brand);//id在brand里

        //第一种写法
        /*for (Long cid : cids) {

            brandMapper.insertBrandCategory(brand.getId(),cid);

        }*/

        //第二种写法
        cids.forEach(cid->{

            brandMapper.insertBrandCategory(brand.getId(),cid);

        });

    }

    public void updateBrand(Brand brand, List<Long> cids) {
        //更新品牌
        brandMapper.updateByPrimaryKey(brand);
        //先删除品牌之前的分类，然后再重新关联品牌和分类
        //删除中间表
        brandMapper.deleteBrandCategory(brand.getId());


        //插入
        cids.forEach(cid->{

            //325400  76
            //325400  105
            //中间表插入数据
            brandMapper.insertBrandCategory(brand.getId(),cid);


        });

    }

    public List<Brand> queryBrandByCategory(Long cid) {

        return this.brandMapper.queryBrandByCategory(cid);
    }


    public Brand queryBrandById(Long bid) {
        return this.brandMapper.selectByPrimaryKey(bid);
    }


}

