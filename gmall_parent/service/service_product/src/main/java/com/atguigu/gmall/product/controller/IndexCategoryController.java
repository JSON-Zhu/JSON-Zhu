package com.atguigu.gmall.product.controller;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.product.service.IndexCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * IndexCategoryController 首页分类信息的控制层
 *
 * @author XQ.Zhu
 * @version 1.0
 * 2022/2/21 17:54
 **/
@RestController
@RequestMapping(value = "/api/product/category")
public class IndexCategoryController {

    @Autowired
    private IndexCategoryService indexCategoryService;

    /**
     *获取首页的分类信息
     * @return : com.atguigu.gmall.common.result.Result
     */
    @GetMapping(value = "/getIndexCategory")
    public Result getIndexCategory(){
        List<JSONObject> indexCategory = indexCategoryService.getIndexCategory();
        return Result.ok(indexCategory);
    }
}
