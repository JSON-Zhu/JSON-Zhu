package com.atguigu.gmall.item.service;

import java.util.HashMap;

/**
 * ItemService 商品详情服务类实现数据聚合的接口
 *
 * @author XQ.Zhu
 * @version 1.0
 * 2022/2/19 23:18
 **/
public interface ItemService {
    /**
     * 获取商品详情页中的所有数据的接口
     * @param skuId
     * @return : map
     */
    HashMap<String, Object> getSkuItem(Long skuId);
}
