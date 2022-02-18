package com.atguigu.gmall.product.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.product.*;
import com.atguigu.gmall.product.service.ManageService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 管理控制台的控制器
 * @author XQ.Zhu
 */
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

    /**
     * 获取销售属性列表
     * @return
     */
    @GetMapping(value = "/baseSaleAttrList")
    public Result baseSaleAttrList(){
       return Result.ok(manageService.baseSaleAttrList());
    }

    /**
     * 保存spu信息
     * @param spuInfo
     * @return : void
     */
    @PostMapping(value = "/saveSpuInfo")
    public void saveSpuInfo(@RequestBody SpuInfo spuInfo){
        manageService.saveSpuInfo(spuInfo);
    }

    /**
     * 查询spuInfo列表
     * @param page
     * @param size
     * @param category3Id
     * @return : com.atguigu.gmall.common.result.Result
     */
    @GetMapping(value = "/{page}/{size}")
    public Result getSpuInfoList(@PathVariable("page") Integer page,
                                 @PathVariable("size") Integer size,
                                 @RequestParam("category3Id") Long category3Id
                                 ){
        IPage<SpuInfo> spuInfoList = manageService.getSpuInfoList(page, size, category3Id);
        return Result.ok(spuInfoList);
    }

    /**
     * 查询销售属性的列表
     * @param spuId
     * @return : com.atguigu.gmall.common.result.Result
     */
    @GetMapping(value = "spuSaleAttrList/{spuId}")
    public Result getSpuSaleAttrList(@PathVariable("spuId")Long spuId){
        return Result.ok(manageService.getSpuSaleAttrList(spuId));
    }

    /**
     * 根据spuId获取图片列表
     * @param spuId
     * @return : com.atguigu.gmall.common.result.Result
     */
    @GetMapping(value = "spuImageList/{spuId}")
    public Result getSpuImageList(@PathVariable("spuId") Long spuId){
        return Result.ok(manageService.getSpuImageList(spuId));
    }

}
