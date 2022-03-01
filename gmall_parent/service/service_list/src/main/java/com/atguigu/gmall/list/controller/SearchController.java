package com.atguigu.gmall.list.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.list.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * SearchController
 *
 * @author XQ.Zhu
 * @version 1.0
 * 2022/3/1 16:08
 **/
@RestController
@RequestMapping(value = "/api/list")
public class SearchController {

    @Autowired
    private SearchService searchService;

    /**
     * 商品搜索
     * @param searchData
     * @return : com.atguigu.gmall.common.result.Result
     */
    @GetMapping(value = "/search")
    public Result search(@RequestParam Map<String,String> searchData){
        return Result.ok(searchService.search(searchData));
    }
}
