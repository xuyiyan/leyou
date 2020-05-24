package com.leyou.item.service;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.leyou.common.pojo.PageResult;
import com.leyou.item.bo.SpuBo;
import com.leyou.item.mapper.BrandMapper;
import com.leyou.item.mapper.SkuMapper;
import com.leyou.item.mapper.SpuDetailMapper;
import com.leyou.item.mapper.SpuMapper;
import com.leyou.item.mapper.StockMapper;
import com.leyou.item.pojo.Brand;
import com.leyou.item.pojo.Sku;
import com.leyou.item.pojo.Spu;
import com.leyou.item.pojo.SpuDetail;
import com.leyou.item.pojo.Stock;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class GoodsService {

    @Autowired(required = false)
    private SpuMapper spuMapper;

    @Autowired(required = false)
    private SpuDetailMapper spuDetailMapper;

    @Autowired
    private CategoryService categoryService;

    @Autowired(required = false)
    private BrandMapper brandMapper;

    @Autowired(required = false)
    private SkuMapper skuMapper;

    @Autowired(required = false)
    private StockMapper stockMapper;

    @Autowired
    private AmqpTemplate template;

    public PageResult<SpuBo> querySpuByPage(Integer page, Integer rows, Boolean saleable, String key) {
        //开启分页助手
        PageHelper.startPage(page,rows);
        //创建查询条件
        Example example = new Example(Spu.class);
        //查询条件的构建工具
        Example.Criteria criteria = example.createCriteria();
        //是否进行模糊查询
        if(StringUtils.isNoneBlank(key)){
            criteria.andLike("title","%"+key+"%");
        }
        if(saleable!=null){
            criteria.andEqualTo("saleable",saleable);
        }

        //查询spu表 select * from tb_spu where title like '%+小米+%' and saleable=false
        List<Spu> spus = this.spuMapper.selectByExample(example);
        //转成分页助手的page对象
        Page<Spu> spuPage = (Page<Spu>)spus;

        //用来存放spubo
        List<SpuBo> spuBos = new ArrayList<>();

        for(Spu spu:spus){

            SpuBo spuBo = new SpuBo();
            //属性拷贝
            BeanUtils.copyProperties(spu,spuBo);
            //根据spu的商品分类的id,查询商品分类的名称
            List<String> names=categoryService.queryNamesByIds(Arrays.asList(spu.getCid1(),spu.getCid2(),spu.getCid3()));


            spuBo.setCname(StringUtils.join(names, "/"));//设置商品分类的名称

            Brand brand = brandMapper.selectByPrimaryKey(spu.getBrandId());
            spuBo.setBname(brand.getName());//设置商品的名称

            spuBos.add(spuBo);//放入list

        }

        return new PageResult<>(spuPage.getTotal(),new Long(spuPage.getPages()),spuBos);


    }

    @Transactional
    public void saveGoods(SpuBo spuBo) {

        spuBo.setSaleable(true);
        spuBo.setValid(true);
        spuBo.setCreateTime(new Date());
        spuBo.setLastUpdateTime(new Date());
        //保存spu
        this.spuMapper.insertSelective(spuBo);

        SpuDetail spuDetail = spuBo.getSpuDetail();
        spuDetail.setSpuId(spuBo.getId());
        //保存spudetail
        this.spuDetailMapper.insertSelective(spuDetail);

        //保存sku和stock
        List<Sku> skus = spuBo.getSkus();
        saveSkus(spuBo,skus);

        //发一条消息
        send("insert",spuBo.getId());


    }

    private void saveSkus(SpuBo spuBo, List<Sku> skus) {
        for(Sku sku:skus){
            sku.setSpuId(spuBo.getId());
            sku.setCreateTime(new Date());
            sku.setLastUpdateTime(new Date());
            //保存sku
            skuMapper.insertSelective(sku);

            Stock stock = new Stock();
            stock.setSkuId(sku.getId());
            stock.setStock(sku.getStock());
            this.stockMapper.insert(stock);

        }

    }

    public SpuDetail querySpuDetailBySpuId(Long spuId) {
        return this.spuDetailMapper.selectByPrimaryKey(spuId);
    }

    public List<Sku> querySkuBySpuId(Long spuId) {
        Sku record = new Sku();
        record.setSpuId(spuId);
        List<Sku> skus = this.skuMapper.select(record);//根据id 查询spuid的每个数据并且装入集合

        //同时要查询每个sku的库存
        skus.forEach(sku -> {
            //sku的id就是库存stock的主键
            sku.setStock(this.stockMapper.selectByPrimaryKey(sku.getId()).getStock());
        });
        return skus;
    }

    @Transactional
    public void updateGoods(SpuBo spuBo) {
        //spu，spuDetail可以直接更新
        spuBo.setLastUpdateTime(new Date());
        this.spuMapper.updateByPrimaryKeySelective(spuBo);

        this.spuDetailMapper.updateByPrimaryKeySelective(spuBo.getSpuDetail());

        //先删除当前spu对应的所有的sku，然后重新添加
        //先查，再根据查到的结果删除
        Sku record = new Sku();
        record.setSpuId(spuBo.getId());
        //从数据库中查询到的skus
        List<Sku> skus = this.skuMapper.select(record);

        //根据sku信息，删除对应数据库中的信息
        //删除库存数据
        if(!CollectionUtils.isEmpty(skus)){
            List<Long> ids = skus.stream().map(Sku::getId).collect(Collectors.toList());
            this.stockMapper.deleteByIdList(ids);
            this.skuMapper.delete(record);
        }

        //循环添加sku
        saveSkus(spuBo, spuBo.getSkus());

        //发一条消息
        send("update",spuBo.getId());

    }

    public Spu querySpuById(Long spuId) {

        return this.spuMapper.selectByPrimaryKey(spuId);

    }

    //发送消息
    public void send(String type,Long id){

        template.convertAndSend("item."+type,id);

    }

}
