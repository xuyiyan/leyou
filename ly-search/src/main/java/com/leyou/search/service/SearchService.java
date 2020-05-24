package com.leyou.search.service;

import com.leyou.item.pojo.Brand;
import com.leyou.item.pojo.Category;
import com.leyou.item.pojo.SpecParam;
import com.leyou.search.client.BrandClient;
import com.leyou.search.client.CategoryClient;
import com.leyou.search.client.SpecClient;
import com.leyou.search.pojo.Goods;
import com.leyou.search.repository.GoodsRepository;
import com.leyou.search.utils.SearchRequest;
import com.leyou.search.utils.SearchResult;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.Operator;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.LongTerms;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SearchService {

    @Autowired
    private GoodsRepository goodsRepository;
    @Autowired
    private CategoryClient categoryClient;
    @Autowired
    private SpecClient specClient;

    @Autowired
    private BrandClient brandClient;


    public SearchResult search(SearchRequest searchRequest) {
        String key = searchRequest.getKey();
        if(StringUtils.isBlank(key)){
            return null;
        }
        //自定义查询
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
        //调用方法，返回一个查询条件对象
        QueryBuilder query=buildBasicQueryWithFilter(searchRequest);

        //添加查询条件
        queryBuilder.withQuery(query);
        //添加分页
        queryBuilder.withPageable(PageRequest.of(searchRequest.getPage()-1,searchRequest.getSize()));

        //添加聚合
        String categoryAggName="category";//商品分类聚合名称
        String brandAggName="brand";//商品品牌聚合名称
        queryBuilder.addAggregation(AggregationBuilders.terms(categoryAggName).field("cid3"));
        queryBuilder.addAggregation(AggregationBuilders.terms(brandAggName).field("brandId"));

        List<Category> categories=new ArrayList<>();
        List<Brand> brands=new ArrayList<>();
        //要对商品的分类和品牌进行聚合
        //执行查询
        AggregatedPage<Goods> goodsPage=(AggregatedPage<Goods>)this.goodsRepository.search(queryBuilder.build());

        //获取分类聚合结果
        LongTerms categoryTerms=(LongTerms)goodsPage.getAggregation(categoryAggName);
        List<LongTerms.Bucket> buckets = categoryTerms.getBuckets();
        List<Long> cids=new ArrayList<>();
        //从分类的聚合分桶中获取所有分类的id
        for(LongTerms.Bucket bucket:buckets){

            cids.add(bucket.getKeyAsNumber().longValue());//76 100
        }
        //根据分类的id查询分类名称
        List<String> names = this.categoryClient.queryNamesByIds(cids);
        for(int i=0;i<cids.size();i++){
            Category category = new Category();
            category.setId(cids.get(i));//76
            category.setName(names.get(i));//手机
            categories.add(category);
        }

        //根据聚合名称取 品牌聚合的结果
        LongTerms brandTerms = (LongTerms) goodsPage.getAggregation(brandAggName);

        List<LongTerms.Bucket> buckets1 = brandTerms.getBuckets();
        List<Long> brandIds= new ArrayList<>();
        for(LongTerms.Bucket b:buckets1){
            brandIds.add(b.getKeyAsNumber().longValue());//12669 8557

        }

        //把品牌id转对象集合
        for(Long i:brandIds){
            //根据品牌id查询品牌
            Brand brand = brandClient.queryBrandById(i);
            brands.add(brand);
        }

        //只有分类唯一才展示规格参数
        List<Map<String,Object>> specs=null;
        if(categories.size()==1){
            specs=getSpecs(categories.get(0).getId(),query);
        }

        return new SearchResult(goodsPage.getTotalElements(),new Long(goodsPage.getTotalPages()),goodsPage.getContent(),categories,brands,specs);

    }

    //构建基本查询条件
    private QueryBuilder buildBasicQueryWithFilter(SearchRequest request) {

        //创建基本的bool的查询
        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
        queryBuilder.must(QueryBuilders.matchQuery("all",request.getKey()).operator(Operator.AND));

        //过滤条件构建器
        BoolQueryBuilder filterQueryBuilder = QueryBuilders.boolQuery();
        //整理过滤的条件
        Map<String, String> filter = request.getFilter();
        for(Map.Entry<String,String> entry:filter.entrySet()){

            String key = entry.getKey();
            String value = entry.getValue();
            //商品分类和品牌可以直接查询不需要拼接
            if(key!="cid3"&&key!="brandId"){
                key="specs."+key+".keyword";
            }
            //字符串类型进行term查询
            filterQueryBuilder.must(QueryBuilders.termQuery(key,value));

        }
        //添加过滤条件
        queryBuilder.filter(filterQueryBuilder);
        return queryBuilder;

    }

    /**
     * 对查询到数据进行可搜索规格参数的聚合操作，聚合时要和，搜索条件相关，搜索到什么内容，聚合什么，不要全部聚合
     * @param id
     * @return
     */
    private List<Map<String, Object>> getSpecs(Long id, QueryBuilder query) {

        List<Map<String,Object>> specList = new ArrayList<>();

        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();

        //先根据查询条件，执行查询
        queryBuilder.withQuery(query);

        //对规格参数进行聚合，聚合要拿到所有的可搜索的规格参数

        List<SpecParam> searchingSpecParams = this.specClient.querySpecParam(null, id, true,null);

        //添加聚合条件,聚合的名称就是可搜索规格参数的名称,聚合的字段就是合成字段
        searchingSpecParams.forEach(specParam -> queryBuilder.addAggregation(
                AggregationBuilders.terms(specParam.getName()).field("specs."+specParam.getName()+".keyword")));

        AggregatedPage<Goods> page = (AggregatedPage<Goods>) this.goodsRepository.search(queryBuilder.build());

        searchingSpecParams.forEach(specParam -> {
            //可搜索规格参数的名称
            String name = specParam.getName();

            //根据聚合名称获取聚合的结果
            StringTerms stringTerms = (StringTerms) page.getAggregation(name);

            List<String> values = new ArrayList<>();
            List<StringTerms.Bucket> buckets = stringTerms.getBuckets();

            //把聚合分桶中每个值存入values集合中
            buckets.forEach(bucket -> values.add(bucket.getKeyAsString()));

            Map<String,Object> specMap = new HashMap<>();
            specMap.put("k",name);//k===>CPU品牌

            specMap.put("options",values);//options===》["骁龙","联发科","展讯"]
            specList.add(specMap);
        });
        return specList;
    }

//    public PageResult<Goods> search(SearchRequest searchRequest) {
//        String key = searchRequest.getKey();//小米电视
//        if(null==key){
//            return null;
//        }
//        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
//        //小米电视
//        queryBuilder.withQuery(QueryBuilders.matchQuery("all",key).operator(Operator.AND));
//        //过滤字段
//        queryBuilder.withSourceFilter(new FetchSourceFilter(new String[]{"id","skus","subTitle"},null));
//
//        //分页
//        queryBuilder.withPageable(PageRequest.of(searchRequest.getPage()-1,searchRequest.getSize()));//0 20
//
//        //查询
//        Page<Goods> goodsPage = this.goodsRepository.search(queryBuilder.build());
//
//        return new PageResult<>(goodsPage.getTotalElements(),new Long(goodsPage.getTotalPages()),goodsPage.getContent());
//    }
}
