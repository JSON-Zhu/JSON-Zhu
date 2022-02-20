package com.atguigu.gmall.item.controller;

import com.atguigu.gmall.item.service.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;

/**
 * ItemController
 *
 * @author XQ.Zhu
 * @version 1.0
 * 2022/2/20 0:51
 **/

@RestController
@RequestMapping(value = "/admin/item")
public class ItemController {

    @Autowired
    private ItemService itemService;

    /**
     * 获取商品详情
     * @param skuId
     * @return : java.util.HashMap<java.lang.String,java.lang.Object>
     */
    @GetMapping(value = "/getItemInfo/{skuId}")
    public HashMap<String, Object> getItemInfo(@PathVariable(value = "skuId")Long skuId){
        HashMap<String, Object> skuItem = itemService.getSkuItem(skuId);
        return skuItem;
    }
}
