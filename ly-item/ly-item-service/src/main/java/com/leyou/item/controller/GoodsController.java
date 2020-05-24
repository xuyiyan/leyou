package com.leyou.item.controller;

import com.leyou.common.pojo.PageResult;
import com.leyou.item.bo.SpuBo;
import com.leyou.item.pojo.Sku;
import com.leyou.item.pojo.Spu;
import com.leyou.item.pojo.SpuDetail;
import com.leyou.item.service.GoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class GoodsController {

    @Autowired
    private GoodsService goodsService;
    //http://api.leyou.com/api/item/spu/page?key=小米&saleable=true&page=1&rows=5
    @RequestMapping("spu/page")
    public ResponseEntity<PageResult<SpuBo>> querySpuByPage(@RequestParam(value = "page",defaultValue = "1") Integer page,
                                                                              @RequestParam(value = "rows",defaultValue = "10") Integer rows,
                                                                              @RequestParam(value = "saleable",required = false) Boolean saleable,
                                                                              @RequestParam(value = "key",required = false) String  key){


        PageResult<SpuBo> pageResult=this.goodsService.querySpuByPage(page,rows,saleable,key);

        if(pageResult!=null&&pageResult.getItems()!=null&&pageResult.getItems().size()!=0){
            return ResponseEntity.ok(pageResult);


        }
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();

    }

    //http://api.leyou.com/api/item/goods
    @PostMapping("goods")
    public ResponseEntity<Void> saveGoods(@RequestBody SpuBo spuBo){
        //@RequestBody接受前台json格式字符串

        this.goodsService.saveGoods(spuBo);
        return ResponseEntity.status(HttpStatus.CREATED).build();//相应码201

    }

    /**
     *因为在商品列表页面，只有spu的基本信息：id、标题、品牌、商品分类等。
     * 比较复杂的商品详情（spuDetail)和sku信息都没有，编辑页面要回显数据，就需要查询这些内容。
     * @param spuId
     * @return
     */
    @GetMapping("spu/detail/{spuId}")
    public ResponseEntity<SpuDetail> querySpuDetailBySpuId(@PathVariable("spuId")Long spuId)   {
        SpuDetail spuDetail = this.goodsService.querySpuDetailBySpuId(spuId);

        if (spuDetail != null) {
            return ResponseEntity.ok(spuDetail);
        }

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();


    }
    /**
     *因为在商品列表页面，只有spu的基本信息：id、标题、品牌、商品分类等。
     * 比较复杂的商品详情（spuDetail)和sku信息都没有，编辑页面要回显数据，就需要查询这些内容。
     * @param spuId
     * @return
     */
    @GetMapping("sku/list")
    public ResponseEntity<List<Sku>> querySkuBySpuId(@RequestParam("id")Long spuId){

        List<Sku> skus = this.goodsService.querySkuBySpuId(spuId);
        if (skus != null && 0!=skus.size()) {
            return ResponseEntity.ok(skus);
        }

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    /**
     * 更新商品
     * @param spuBo
     * @return
     */
    @PutMapping("goods")
    public ResponseEntity<Void> updateGoods(@RequestBody SpuBo spuBo){

        this.goodsService.updateGoods(spuBo);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    // 根据商品的spu的id查询spu
    @GetMapping("spu/{id}")
    public ResponseEntity<Spu> querySpuById(@PathVariable("id") Long spuId){
        Spu spu=this.goodsService.querySpuById(spuId);
        if(spu==null){
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();

        }
        return ResponseEntity.ok(spu);

    }


}
