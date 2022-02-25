package com.atguigu.gmall.list.dao;

import com.atguigu.gmall.model.list.Goods;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

/**
 * GoodsRepository es商品管理的持久层接口
 *
 * @author XQ.Zhu
 * @version 1.0
 * 2022/2/23 15:08
 **/

@Repository
public interface GoodsDao extends ElasticsearchRepository<Goods,Long> {
}
