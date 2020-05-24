package com.leyou.item.service;

import com.leyou.item.mapper.SpecGroupMapper;
import com.leyou.item.mapper.SpecParamMapper;
import com.leyou.item.pojo.SpecGroup;
import com.leyou.item.pojo.SpecParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SpecService {

    @Autowired(required = false)
    private SpecGroupMapper specGroupMapper;

    @Autowired(required = false)
    private SpecParamMapper specParamMapper;


    public List<SpecGroup> querySpecGroups(Long cid) {

        SpecGroup specGroup = new SpecGroup();
        specGroup.setCid(cid);

        //查询规格组表
        List<SpecGroup> specGroups = this.specGroupMapper.select(specGroup);
        //select * from tb_spec_group where cid=76

        //为以后
        //查询规格参数封装到规格组
        specGroups.forEach(record->{

            SpecParam specParam = new SpecParam();
            specParam.setGroupId(record.getId());
            //select * from tb_spec_param where group_id=1
            //根据规格组的id查询规格参数
            List<SpecParam> specParams = this.specParamMapper.select(specParam);

            //封装到规格组里面
            record.setSpecParams(specParams);

        });


        return specGroups;

    }

    public List<SpecParam> querySpecParam(Long gid, Long cid, Boolean searching, Boolean generic) {
        SpecParam specParam = new SpecParam();
        specParam.setGroupId(gid);//规格组id
        specParam.setCid(cid);//商品分类id
        specParam.setSearching(searching);//是否可以搜索
        specParam.setGeneric(generic);//是否是通用属性

        //select * from tb_spec_param where group_id=1 and cid= and searching= and generic=
        List<SpecParam> specParams = this.specParamMapper.select(specParam);
        return  specParams;

    }

    @Transactional
    public void addSpecGroup(SpecGroup specGroup) {


        specGroupMapper.insert(specGroup);
    }

    public void updateSpecGroup(SpecGroup group) {
        specGroupMapper.updateByPrimaryKey(group);
    }

    public void deleteSpecGroup(Integer id) {
        specGroupMapper.deleteByPrimaryKey(id);
    }

    @Transactional
    public void addSpecParam(SpecParam specParam) {
        specParamMapper.insertSelective(specParam);
    }

    public void updateSpecParam(SpecParam specParam) {
        specParamMapper.updateByPrimaryKey(specParam);
    }

    public void deleteSpecParam(Integer id){
        specParamMapper.deleteByPrimaryKey(id);
    }


}
