package com.atguigu.gmall.list.service;

import com.atguigu.gmall.model.list.Goods;

import java.util.List;

/**
 * SearchService商品搜索的接口
 *
 * @author XQ.Zhu
 * @version 1.0
 * 2022/2/26 13:26
 **/

public interface SearchService {

    /**
     * 商品搜索
     * @param keyword
     * @return : void
     */
    List<Goods> search(String keyword);

}
