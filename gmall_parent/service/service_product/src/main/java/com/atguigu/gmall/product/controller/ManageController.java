package com.atguigu.gmall.product.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.product.*;
import com.atguigu.gmall.product.service.ManageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/admin/product")
public class ManageController {

    @Autowired
    private ManageService manageService;


    /**
     * 查询所有一级分类
     * @return
     */
    @GetMapping(value = "/getCategory1")
    public Result getCategory1(){
        List<BaseCategory1> category1List = manageService.getCategory1();
        return Result.ok(category1List);
    }

    /**
     * 根据一级id  查询二级
     * @param category1Id
     * @return
     */
    @GetMapping(value = "/getCategory2/{category1Id}")
    public Result getCategory2(@PathVariable(value = "category1Id")Long category1Id){
        List<BaseCategory2> category2List = manageService.getCategory2(category1Id);
        return Result.ok(category2List);
    }

    /**
     * 根据二级id  查询三级
     */
    @GetMapping(value = "/getCategory3/{category2Id}")
    public Result getCategory3(@PathVariable(value = "category2Id")Long category2Id){
        List<BaseCategory3> category3List = manageService.getCategory3(category2Id);
        return Result.ok(category3List);
    }

    /**
     * 保存平台属性
     * @param baseAttrInfo
     * @return
     */
    @PostMapping(value = "saveAttrInfo")
    public Result saveAttrInfo(@RequestBody BaseAttrInfo baseAttrInfo){
        manageService.saveBaseAttrInfo(baseAttrInfo);
        return Result.ok();
    }

    /**
     * 查询一级 二级 三级平台属性
     * @param category1Id
     * @param category2Id
     * @param category3Id
     * @return
     */
    @GetMapping(value = "/attrInfoList/{category1Id}/{category2Id}/{category3Id}")
    public Result attrInfoList(@PathVariable(value = "category1Id") Long category1Id,
                          @PathVariable(value = "category2Id") Long category2Id,
                          @PathVariable(value = "category3Id") Long category3Id
                          ){
        return Result.ok(manageService.attrInfoList(category1Id,category2Id,category3Id));
    }

    /**
     * 获取平台属性值
     * @param attrId
     * @return
     */
    @GetMapping(value = "/getAttrValueList/{attrId}")
    public Result getAttrValueList(@PathVariable(value = "attrId") Long attrId){
        return Result.ok(manageService.getAttrValueList(attrId));
    }

    /**
     * 获取全部商标数据
     */
    @GetMapping(value = "/baseTrademark/getTrademarkList")
    public Result getTrademarkList(){
        return Result.ok(manageService.getTrademarkList());
    }
}
