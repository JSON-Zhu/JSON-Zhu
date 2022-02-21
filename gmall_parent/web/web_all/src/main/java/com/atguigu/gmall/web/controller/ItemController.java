package com.atguigu.gmall.web.controller;

import com.atguigu.gmall.item.feign.ItemFeign;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.File;
import java.io.PrintWriter;
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

    @Autowired
    private TemplateEngine templateEngine;
    /**
     * 创建商品的静态页面
     * @param skuId
     * @return : java.lang.String
     */
    @GetMapping(value = "/createItemHtml/{skuId}")
    @ResponseBody
    public String createItemHtml (@PathVariable(value = "skuId")Long skuId) throws Exception {
        //根据skuId查询sku详情
        HashMap<String, Object> itemInfo = itemFeign.getItemInfo(skuId);
        //创建数据容器对象
        Context context = new Context();
        context.setVariables(itemInfo);
        //创建文件对象
        File file = new File("d://00", skuId + ".html");
        //创建输出写对象,设置utf-8编码
        PrintWriter printWriter = new PrintWriter(file,"utf-8");
        /**
         * 1.使用哪个模板页面生成静态页面
         * 2.数据容器
         * 3.将生成的静态页面存储到哪里去?
         */
        templateEngine.process("item",context,printWriter);
        //关闭输出流
        printWriter.close();
        return "创建成功";
    }

}
