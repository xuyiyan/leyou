package com.leyou;

import com.leyou.common.pojo.PageResult;
import com.leyou.item.bo.SpuBo;
import com.leyou.search.client.GoodsClient;
import com.leyou.search.pojo.Goods;
import com.leyou.search.repository.GoodsRepository;
import com.leyou.search.service.IndexService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = SearchApplication.class)
public class EsTest {

    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;

    @Autowired
    private GoodsRepository goodsRepository;
    @Autowired
    private GoodsClient goodsClient;
    @Autowired
    private IndexService indexService;


    @Test
    public void index(){
        elasticsearchTemplate.createIndex(Goods.class);
        elasticsearchTemplate.putMapping(Goods.class);
    }


    @Test
    public void loadData(){
        int page=1;

        while(true){
            //使用Feign调用item-service微服务 查询spubo
            PageResult<SpuBo> pageResult = this.goodsClient.querySpuByPage(page, 50, null, null);

            //如果没有查询到数据
            if(null==pageResult){
                break;

            }
            page++;
            //获取spubo
            List<SpuBo> spuBoList = pageResult.getItems();

            List<Goods> goodsList=new ArrayList<>();
            for(SpuBo spuBo:spuBoList){
               //把spubo 转成goods
                Goods goods=indexService.buildGoods(spuBo);
                goodsList.add(goods);
            }
            //保存到索引库
            this.goodsRepository.saveAll(goodsList);

        }



    }

}
