package com.atguigu.gmall.product.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.product.BaseCategory1;
import com.atguigu.gmall.product.service.BaseCategory1Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 一级分类的控制层
 */
@RestController
@RequestMapping(value = "/api/category1")
public class BaseCategory1Controller {

    @Autowired
    private BaseCategory1Service baseCategory1Service;

    /**
     * 根据主键id查询
     * @param id
     * @return
     */
    @GetMapping(value = "/getById/{id}")
    public Result getById(@PathVariable(value = "id") Long id){
       BaseCategory1 baseCategory1= baseCategory1Service.getById(id);
       return Result.ok(baseCategory1);
    }
    /**
     * 查询素有数据
     */
    @GetMapping("/getAll")
    public Result getAll(){
        return Result.ok(baseCategory1Service.getAll());
    }

}
