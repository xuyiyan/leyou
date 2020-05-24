package com.leyou.item.controller;

import com.leyou.item.pojo.SpecGroup;
import com.leyou.item.pojo.SpecParam;
import com.leyou.item.service.SpecService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("spec")
public class SpecController {

    @Autowired
    private SpecService specService;
    //http://api.leyou.com/api/item/spec/groups/76

    @GetMapping("groups/{cid}")
    public ResponseEntity<List<SpecGroup>> querySpecGroups(@PathVariable("cid") Long cid){
        //根据分类id查询规格组
        List<SpecGroup> specGroups=this.specService.querySpecGroups(cid);
        if(specGroups != null && specGroups.size() > 0){

            return ResponseEntity.ok(specGroups);
        }
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();

    }

    //http://api.leyou.com/api/item/spec/params?gid=1
    @GetMapping("params")
    public ResponseEntity<List<SpecParam>> querySpecParam(@RequestParam(value = "gid",required = false) Long gid,
                                                          @RequestParam(value = "cid",required = false) Long cid,
                                                          @RequestParam(value = "searching",required = false) Boolean searching,
                                                          @RequestParam(value = "generic",required = false) Boolean generic){

        //根据规格组id 查询规格参数
        List<SpecParam> specParams=this.specService.querySpecParam(gid,cid,searching,generic);
        if(specParams!=null&&specParams.size()>0){

            return ResponseEntity.ok(specParams);
        }
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PostMapping("group")
    public ResponseEntity<Void> addSpecGroup(@RequestBody SpecGroup group) {
        specService.addSpecGroup(group);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PutMapping("group")
    public ResponseEntity<Void> updateSpecGroup(@RequestBody SpecGroup group) {
        specService.updateSpecGroup(group);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @DeleteMapping("group/{id}") //http://api.leyou.com/api/item/spec/group/30
    public ResponseEntity<Void> deleteSpecGroup(@PathVariable("id") Integer id) {
        specService.deleteSpecGroup(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PostMapping("param")
    public ResponseEntity<Void> addSpecParam(@RequestBody SpecParam specParam) {
        specService.addSpecParam(specParam);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PutMapping("param")
    public ResponseEntity<Void> updateSpecParam(@RequestBody SpecParam specParam) {
        specService.updateSpecParam(specParam);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @DeleteMapping("param/{id}")
    public ResponseEntity<Void> deleteParam(@PathVariable("id") Integer id) {
        specService.deleteSpecParam(id);
        return ResponseEntity.status(HttpStatus.OK).build();
    }



}
