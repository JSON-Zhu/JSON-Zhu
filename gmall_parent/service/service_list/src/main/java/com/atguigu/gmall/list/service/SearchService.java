package com.atguigu.gmall.list.service;

import java.util.Map;

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
     * @param searchData
     * @return : java.util.Map<java.lang.String,java.lang.Object>
     */
    Map<String,Object> search(Map<String,String> searchData);

}
