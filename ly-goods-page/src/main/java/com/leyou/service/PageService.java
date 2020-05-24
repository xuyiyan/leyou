package com.leyou.service;

import com.leyou.client.GoodsClient;
import com.leyou.client.SpecClient;
import com.leyou.item.pojo.Sku;
import com.leyou.item.pojo.SpecGroup;
import com.leyou.item.pojo.SpecParam;
import com.leyou.item.pojo.Spu;
import com.leyou.item.pojo.SpuDetail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PageService {
    @Autowired
    private GoodsClient goodsClient;
    @Autowired
    private SpecClient specClient;


    public Map<String, Object> loadData(Long spuId) {

//        let spu = /*[[${spu}]]*/ {};
//        let spuDetail = /*[[${spuDetail}]]*/ {};
//        let skus = /*[[${skus}]]*/ {};
//        let specParams = /*[[${specParams}]]*/ {};
//        let specGroups = /*[[${specGroups}]]*/ {};
        Map<String, Object> map=new HashMap<String, Object>();
        //根据spuid查询spu
        Spu spu1 = goodsClient.querySpuById(spuId);
        //放入map
        map.put("spu",spu1);

        //根据spuid查询spudetail
        SpuDetail spuDetail = this.goodsClient.querySpuDetailBySpuId(spuId);
        map.put("spuDetail",spuDetail);

        //根据spuid 查询sku
        List<Sku> skus = this.goodsClient.querySkuBySpuId(spuId);
        map.put("skus",skus);

        //根据spu的三级分类查询特有的规格参数
        List<SpecParam> specParams = this.specClient.querySpecParam(null, spu1.getCid3(), null, false);

        Map<Long,Object>  spMap=new HashMap<>();
        for(SpecParam s:specParams){
            //把规格参数id，和规格参数名称放入map
            spMap.put(s.getId(),s.getName());

        }

        map.put("specParams",spMap);

        //根据spu的三级分类id查询规格组
        List<SpecGroup> specGroups = this.specClient.querySpecGroups(spu1.getCid3());
        map.put("specGroups",specGroups);
        return map;
        
    }
}
