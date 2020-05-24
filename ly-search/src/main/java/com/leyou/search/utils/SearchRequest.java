package com.leyou.search.utils;

import java.util.Map;

//接收浏览器传过来的数据
public class SearchRequest {

    private String key;//搜索关键字
    private Integer page;//当前页
    private Map<String,String> filter;

    private static final   Integer DEFAULT_PAGE=1;//缺省的第几页
    private static  final  Integer DEFAULT_SIZE=20;//默认显示多少条数据

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Integer getPage() {
        if(page==null){

            return DEFAULT_PAGE;//1
        }

        //-1 1
        return Math.max(page,DEFAULT_PAGE);//1
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Map<String, String> getFilter() {
        return filter;
    }

    public void setFilter(Map<String, String> filter) {
        this.filter = filter;
    }

    public Integer getSize(){

        return DEFAULT_SIZE;
    }
}
