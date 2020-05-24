package com.leyou.client;

import com.leyou.common.pojo.PageResult;
import com.leyou.item.pojo.Brand;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(value = "item-service")
public interface BrandClient {


    @GetMapping("brand/page")
    public PageResult<Brand> pageQuery(@RequestParam(value = "page",defaultValue = "1") Integer page,
                                       @RequestParam(value = "rows",defaultValue = "10") Integer rows,
                                       @RequestParam(value = "sortBy",required = false) String  sortBy,
                                       @RequestParam(value = "desc",required = false) Boolean desc,
                                       @RequestParam(value = "key",required = false) String  key);




    //http://api.leyou.com/api/item/brand/cid/76
    @GetMapping("brand/cid/{id}")
    public List<Brand> queryBrandByCategory(@PathVariable("id") Long cid);

    //根据品牌的id查询品牌
    @GetMapping("brand/bid/{bid}")
    public Brand queryBrandById(@PathVariable("bid") Long bid);
    //http://item-service/brand/bid/1
    //http://127.0.0.1:9081/brand/bid/1
}
