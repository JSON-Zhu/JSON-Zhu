package com.atguigu.gmall.product.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.product.BaseCategory1;
import com.atguigu.gmall.product.service.BaseCategory1Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    /**
     * 保存数据
     * @param baseCategory1
     */
    @PostMapping
    public Result save(@RequestBody BaseCategory1 baseCategory1){
        baseCategory1Service.add(baseCategory1);
        return Result.ok();
    }
    /**
     * 修改数据
     * @param baseCategory1
     */
    @PutMapping
    public Result update(@RequestBody BaseCategory1 baseCategory1){
        baseCategory1Service.update(baseCategory1);
        return Result.ok();
    }
    /**
     * 删除数据
     * @param id
     */
    @DeleteMapping(value = "/delete/{id}")
    public Result save(@PathVariable(value = "id")Long id){
        baseCategory1Service.delete(id);
        return Result.ok();
    }

    /**
     * 条件查询
     * @param baseCategory1
     * @return
     */
    @PostMapping(value = "/query")
    public Result query(@RequestBody BaseCategory1 baseCategory1){
       List<BaseCategory1> baseCategory1List= baseCategory1Service.query(baseCategory1);
       return Result.ok(baseCategory1List);
    }

    /**
     * 分页查询
     * @param page
     * @param limit
     * @return
     */
    @GetMapping(value = "/page/{page}/{limit}")
    public Result page(@PathVariable(value = "page") Integer page,
                       @PathVariable(value = "limit") Integer limit){
        return Result.ok(baseCategory1Service.page(page,limit));
    }

    /**
     * 条件分页查询
     * @param page
     * @param limit
     * @param baseCategory1
     * @return
     */
    @PostMapping(value = "/search/{page}/{limit}")
    public Result query(@PathVariable(value = "page") Integer page,
                       @PathVariable(value = "limit") Integer limit,
                       @RequestBody BaseCategory1 baseCategory1
                       ){
        return Result.ok(baseCategory1Service.query(page,limit,baseCategory1));
    }
}
