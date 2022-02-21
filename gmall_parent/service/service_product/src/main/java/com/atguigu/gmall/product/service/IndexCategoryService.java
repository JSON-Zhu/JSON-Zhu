package com.atguigu.gmall.product.service;

import com.alibaba.fastjson.JSONObject;

import java.util.List;

/**
 * IndexCategory 商城首页展示商品分类的service
 *
 * @author XQ.Zhu
 * @version 1.0
 * 2022/2/21 16:35
 **/

public interface IndexCategoryService {

    /**
     *查询首页的分类信息
     * @return : List<JSONObject>
     */
    List<JSONObject> getIndexCategory();
}
