package com.leyou.service;

import com.leyou.utils.ThreadUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

@Service
public class FileServcie {

    @Value("${ly.thymeleaf.destPath}")
    private String destPath;//D:/nginx-1.14.2/html/item

    @Autowired
    private TemplateEngine templateEngine; //resttemplate

    @Autowired
    private PageService pageService;

    //判断文件夹下有没有指定html
    public boolean exist(Long spuId) {
        File file = new File(destPath);
        //判断是否存在D:/nginx-1.14.2/html/item 文件夹，如果没有这个文件夹，就创建
        if(!file.exists()){
            file.mkdirs();
        }
        File file1 = new File(destPath, spuId + ".html");//113.html
        return file1.exists();//第一次false
    }

    public void syncCreateHtml(Long spuId) {
        //使用多线程，调用产生静态页面方法
        ThreadUtils.execute(()->{
            createHtml(spuId);

        });



    }

    private void createHtml(Long spuId) {

        //创建上下问对象
        Context context = new Context();
        context.setVariables(pageService.loadData(spuId));//把数据放入到上下文对象

        File file = new File(destPath, spuId + ".html");//113.html

        try {
            //创建printwriter对象
            PrintWriter printWriter = new PrintWriter(file,"utf-8");
            //产生113.html
            templateEngine.process("item",context,printWriter);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }


    }

    public void deleteHtml(Long id) {

        File file = new File(destPath, id + ".html");
        file.deleteOnExit();

    }
}
