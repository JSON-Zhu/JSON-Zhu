package com.atguigu.gmall.web.controller;

import com.atguigu.gmall.item.feign.ItemFeign;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.HashMap;

/**
 * ItemController
 *
 * @author XQ.Zhu
 * @version 1.0
 * 2022/2/21 0:06
 **/

@Controller
@RequestMapping(value = "/page/item")
public class ItemController {

    @Autowired
    private ItemFeign itemFeign;
    /**
     * 打开商品详情页
     * @param skuId
     * @return
     */
    @GetMapping(value = "/getItemPage/{skuId}")
    public String getItemPage(@PathVariable(value = "skuId") Long skuId,Model model){
        HashMap<String, Object> itemInfo = itemFeign.getItemInfo(skuId);
        //将全部结果存入model
        model.addAllAttributes(itemInfo);
        System.out.println("itemInfo = " + itemInfo);
        return "item";
    }

}
