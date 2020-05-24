package com.leyou.search.client;

import com.leyou.common.pojo.PageResult;
import com.leyou.item.bo.SpuBo;
import com.leyou.item.pojo.Sku;
import com.leyou.item.pojo.Spu;
import com.leyou.item.pojo.SpuDetail;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(value = "item-service")
public interface GoodsClient {


    @RequestMapping("spu/page")
    public PageResult<SpuBo> querySpuByPage(@RequestParam(value = "page",defaultValue = "1") Integer page,
                                            @RequestParam(value = "rows",defaultValue = "10") Integer rows,
                                            @RequestParam(value = "saleable",required = false) Boolean saleable,
                                            @RequestParam(value = "key",required = false) String  key);
    //http://item-service/spu/page
    //http://127.0.0.1:9081/spu/page?page=1&rows=50

    //spu/detail/2
    @GetMapping("spu/detail/{spuId}")
    public SpuDetail querySpuDetailBySpuId(@PathVariable("spuId") Long spuId);

    //http://api.leyou.com/api/item/sku/list?id=2
    @GetMapping("sku/list")
    public List<Sku> querySkuBySpuId(@RequestParam("id") Long spuId);
    //http://item-service/sku/list


    // 根据商品的spu的id查询spu
    @GetMapping("spu/{id}")
    public Spu querySpuById(@PathVariable("id") Long spuId);

}
