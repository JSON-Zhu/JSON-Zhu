package com.atguigu.gmall.product.service.impl;

import com.atguigu.gmall.common.constant.ProductConst;
import com.atguigu.gmall.model.product.*;
import com.atguigu.gmall.product.mapper.*;
import com.atguigu.gmall.product.service.ManageService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
 * 管理控制台接口的实现类,实现管理控制台页面的接口
 * @author XQ.Zhu
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class ManageServiceImpl implements ManageService {

    @Resource
    private BaseCategory1Mapper baseCategory1Mapper;

    @Resource
    private BaseCategory2Mapper baseCategory2Mapper;

    @Resource
    private BaseCategory3Mapper baseCategory3Mapper;

    @Resource
    private BaseAttrInfoMapper baseAttrInfoMapper;

    @Resource
    private BaseAttrValueMapper baseAttrValueMapper;

    @Resource
    private BaseTrademarkMapper baseTrademarkMapper;

    @Resource
    private BaseSaleAttrMapper baseSaleAttrMapper;
    /**
     * 查询所有的一级分类
     *
     * @return
     */
    @Override
    public List<BaseCategory1> getCategory1() {
        return baseCategory1Mapper.selectList(null);
    }

    /**
     * 根据一级id 查询二级分类
     *
     * @param category1Id
     * @return
     */
    @Override
    public List<BaseCategory2> getCategory2(Long category1Id) {
        LambdaQueryWrapper<BaseCategory2> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BaseCategory2::getCategory1Id, category1Id);
        List<BaseCategory2> baseCategory2List = baseCategory2Mapper.selectList(wrapper);
        return baseCategory2List;
    }

    /**
     * 根据二级id 查询三级分类
     *
     * @param category2Id
     * @return
     */
    @Override
    public List<BaseCategory3> getCategory3(Long category2Id) {
        LambdaQueryWrapper<BaseCategory3> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BaseCategory3::getCategory2Id, category2Id);
        List<BaseCategory3> baseCategory3List = baseCategory3Mapper.selectList(wrapper);
        return baseCategory3List;
    }

    /**
     * 保存平台属性
     *
     * @param baseAttrInfo
     */
    @Override
    public void saveBaseAttrInfo(BaseAttrInfo baseAttrInfo) {
        //参数校验
        if (baseAttrInfo == null) {
            throw new RuntimeException("参数错误");
        }
        //判断是新增还是修改
        if (baseAttrInfo.getId() != null) {
            //前端传递了id,此时为修改,先修改attr_info表
            int update = baseAttrInfoMapper.updateById(baseAttrInfo);
            if (update <= 0) {
                throw new RuntimeException("修改平台属性失败!");
            }
            //将旧的平台属性的所有的值全部删除
            baseAttrValueMapper.delete(
                    new LambdaQueryWrapper<BaseAttrValue>().
                            eq(BaseAttrValue::getAttrId, baseAttrInfo.getId()));
        } else {
            //新增
            int insert = baseAttrInfoMapper.insert(baseAttrInfo);
            if (insert <= 0) {
                throw new RuntimeException("保存平台属性失败");
            }
        }
        //获取平台属性名称的id
        Long attrInfoId = baseAttrInfo.getId();
        //获取平台属性对应的值的列表
        List<BaseAttrValue> attrValueList = baseAttrInfo.getAttrValueList();
        attrValueList.stream().forEach(baseAttrValue -> {
            //为每一个平台属性补充这个值所属的平台属性名称的id
            baseAttrValue.setAttrId(attrInfoId);
            //保存平台属性值到数据库
            int insert = baseAttrValueMapper.insert(baseAttrValue);
            if (insert <= 0) {
                throw new RuntimeException("保存平台属性值失败");
            }
        });

//        for (BaseAttrValue baseAttrValue : attrValueList) {
//            //为每一个平台属性补充这个值所属的平台属性名称的id
//            baseAttrValue.setAttrId(attrInfoId);
//            //保存平台属性值到数据库
//            int insert = baseAttrValueMapper.insert(baseAttrValue);
//            if (insert <= 0) {
//                throw new RuntimeException("保存平台属性值失败");
//            }
//        }

    }

    /**
     * 根据分类查询平台属性信息
     *
     * @param category1Id
     * @param category2Id
     * @param category3Id
     * @return
     */
    @Override
    public List<BaseAttrInfo> attrInfoList(Long category1Id, Long category2Id, Long category3Id) {
        List<BaseAttrInfo> baseAttrInfoList = baseAttrInfoMapper.selectBaseAttrInfoByCategoryId
                (category1Id, category2Id, category3Id);
        return baseAttrInfoList;
    }

    /**
     * 获取属性值列表
     *
     * @param attrId
     * @return
     */
    @Override
    public List<BaseAttrValue> getAttrValueList(Long attrId) {
        List<BaseAttrValue> baseAttrValueList = baseAttrValueMapper.
                selectList(new LambdaQueryWrapper<BaseAttrValue>().eq(BaseAttrValue::getAttrId, attrId));
        return baseAttrValueList;
    }

    /**
     * 获取全部商标数据
     *
     * @return
     */
    @Override
    public List<BaseTrademark> getTrademarkList() {
        return baseTrademarkMapper.selectList(null);
    }

    /**
     * 获取属性列表
     *
     * @return
     */
    @Override
    public List<BaseSaleAttr> baseSaleAttrList() {
        return baseSaleAttrMapper.selectList(null);
    }

    @Resource
    private SpuInfoMapper spuInfoMapper;
    /**
     * 保存spuInfo
     * @Author: XQ.Zhu
     * @Date: 2022/2/18 17:34
     * @param spuInfo
     * @return : void
     */
    @Override
    public void saveSpuInfo(SpuInfo spuInfo) {
        //check spuInfo
        if(spuInfo==null){
            throw new RuntimeException("参数错误");
        }
        //判断修改还是新增
        Long spuInfoId = spuInfo.getId();
        if(spuInfoId==null){
            //insert
            //保存到spuInfo表
            int insert = spuInfoMapper.insert(spuInfo);
            if(insert<=0){
                throw new RuntimeException("保存spuInfo错误,请重试");
            }
        }else {
            //update
            int update = spuInfoMapper.updateById(spuInfo);
            if(update<=0){
                throw new RuntimeException("修改spuInfo错误,请重试");
            }
            //删除旧的图片,销售属性,销售属性值
            spuImageMapper.delete(new LambdaQueryWrapper<SpuImage>()
                    .eq(SpuImage::getSpuId,spuInfoId));

            spuSaleAttrMapper.delete(new LambdaQueryWrapper<SpuSaleAttr>()
                    .eq(SpuSaleAttr::getSpuId,spuInfoId));

            spuSaleAttrValueMapper.delete(new LambdaQueryWrapper<SpuSaleAttrValue>()
                    .eq(SpuSaleAttrValue::getSpuId,spuInfoId));
        }

        //获取spu id
        Long spuId = spuInfo.getId();
        //保存spuImage信息
        saveSpuImageList(spuId,spuInfo.getSpuImageList());

        //保存销售属性
        saveSpuAttrList(spuId,spuInfo.getSpuSaleAttrList());

    }

    /**
     * 查询spuInfo列表
     * @param page
     * @param size
     * @param category3Id
     * @return : com.baomidou.mybatisplus.core.metadata.IPage<com.atguigu.gmall.model.product.SpuInfo>
     */
    @Override
    public IPage<SpuInfo> getSpuInfoList(Integer page, Integer size, Long category3Id) {
        IPage<SpuInfo> spuInfoIPage = spuInfoMapper.selectPage(new Page<SpuInfo>(page, size),
                new LambdaQueryWrapper<SpuInfo>().eq(SpuInfo::getCategory3Id, category3Id));
        return spuInfoIPage;
    }

    /**
     * 根据spuId获取销售属性值的列表
     *
     * @param spuId
     * @return : java.util.List<com.atguigu.gmall.model.product.SpuSaleAttr>
     */
    @Override
    public List<SpuSaleAttr> getSpuSaleAttrList(Long spuId) {
        return spuSaleAttrMapper.getSpuSaleAttrValueBySpuId(spuId);
    }

    /**
     * 根据spuId获取图片列表
     *
     * @param spuId
     * @return : java.util.List<com.atguigu.gmall.model.product.SpuImage>
     */
    @Override
    public List<SpuImage> getSpuImageList(Long spuId) {

        return spuImageMapper.selectList(new LambdaQueryWrapper<SpuImage>()
                .eq(SpuImage::getSpuId,spuId));
    }

    @Resource
    private SkuInfoMapper skuInfoMapper;
    @Resource
    private SkuImageMapper skuImageMapper;
    @Resource
    private SkuAttrValueMapper skuAttrValueMapper;
    @Resource
    private SkuSaleAttrValueMapper skuSaleAttrValueMapper;
    /**
     * 保存SkuInfo
     *
     * @param skuInfo
     * @return : void
     */
    @Override
    public void saveSkuInfo(SkuInfo skuInfo) {
        //检查参数
        if(skuInfo==null){
            throw new RuntimeException("参数错误");
        }
        //判断更新或者删除
        if(skuInfo.getId()==null){
            int insert = skuInfoMapper.insert(skuInfo);
            if(insert<=0){
                throw new RuntimeException("新增Sku失败");
            }
        }else {
            int update = skuInfoMapper.updateById(skuInfo);
            if(update<=0){
                throw new RuntimeException("修改Sku失败");
            }
            //根据skuId删除对应的skuImage,skuAttr, skuSaleAttr
            skuImageMapper.delete(new LambdaQueryWrapper<SkuImage>().
                    eq(SkuImage::getSkuId,skuInfo.getId()));
            skuAttrValueMapper.delete(new LambdaQueryWrapper<SkuAttrValue>().
                    eq(SkuAttrValue::getSkuId,skuInfo.getId()));
            skuSaleAttrValueMapper.delete(new LambdaQueryWrapper<SkuSaleAttrValue>().
                    eq(SkuSaleAttrValue::getSkuId,skuInfo.getId()));
        }
        //保存skuImage
        saveSkuImageList(skuInfo.getId(),skuInfo.getSkuImageList());
        //保存sku平台属性值
        saveSkuAttrValueList(skuInfo.getId(),skuInfo.getSkuAttrValueList());
        //保存sku销售属性值
        saveSkuSaleAttrValueList(skuInfo.getId(),skuInfo.getSkuSaleAttrValueList(),skuInfo.getSpuId());

    }

    /**
     * 获取skuInfo列表
     * @param page
     * @param size
     * @return : com.baomidou.mybatisplus.core.metadata.IPage<com.atguigu.gmall.model.product.SkuInfo>
     */
    @Override
    public IPage<SkuInfo> getSkuList(Integer page, Integer size) {
        IPage<SkuInfo> skuInfoIPage = skuInfoMapper.selectPage(new Page<>(page, size), null);
        return skuInfoIPage;
    }

    @Autowired
    private RabbitTemplate rabbitTemplate;

    /**
     * 更新商品上架状态
     * @param skuId
     * @param Status
     * @return : void
     */
    @Override
    public void updateSkuSaleStatus(Long skuId, Integer Status) {
        //参数校验
        if(skuId==null||Status==null){
            throw new RuntimeException("参数错误");
        }
        //查询商品数据并判断
        SkuInfo skuInfo = skuInfoMapper.selectById(skuId);
        if(skuInfo==null||skuInfo.getId()==null){
            throw new RuntimeException("商品不存在,无法完成上下架");
        }
        //更新装填
        skuInfo.setIsSale(Status);
        skuInfoMapper.updateById(skuInfo);
        if(ProductConst.SKU_ON_SALE.equals(Status)){
            //上架的商品需要写入es, 消息格式使用字符串格式
            rabbitTemplate.convertAndSend("list_exchange","sku.up",skuId+"");
        }else {
            //下架从es删除
            rabbitTemplate.convertAndSend("list_exchange","sku.down",skuId+"");
        }
    }

    /**
     * 查询Trademark列表
     *
     * @param page
     * @param size
     * @return : com.baomidou.mybatisplus.core.metadata.IPage
     */
    @Override
    public IPage<BaseTrademark> selectTrademarkPage(Integer page, Integer size) {
        IPage<BaseTrademark> trademarkIPage = baseTrademarkMapper.
                selectPage(new Page<>(page, size), null);
        return trademarkIPage;
    }

    /**
     * 保存sku销售属性
     * @param id
     * @param skuSaleAttrValueList
     * @param spuId
     * @return : void
     */
    private void saveSkuSaleAttrValueList(Long id, List<SkuSaleAttrValue> skuSaleAttrValueList, Long spuId) {
        skuSaleAttrValueList.stream().forEach(skuSaleAttrValue -> {
            //补全skuId
            skuSaleAttrValue.setSkuId(id);
            //补全spuId
            skuSaleAttrValue.setSpuId(spuId);
            //save
            int insert = skuSaleAttrValueMapper.insert(skuSaleAttrValue);
            if(insert<=0){
                throw new RuntimeException("保存sku的销售属性失败");
            }
        });
    }

    /**
     * 保存sku平台属性
     * @param id
     * @param skuAttrValueList
     * @return : void
     */
    private void saveSkuAttrValueList(Long id, List<SkuAttrValue> skuAttrValueList) {
        skuAttrValueList.stream().forEach(skuAttrValue -> {
            //补全skuId
            skuAttrValue.setSkuId(id);
            //save
            int insert = skuAttrValueMapper.insert(skuAttrValue);
            if(insert<=0){
                throw new RuntimeException("保存sku的属性失败");
            }
        });
    }

    /**
     * 保存Sku图片列表
     * @param id
     * @param skuImageList
     * @return : void
     */
    private void saveSkuImageList(Long id, List<SkuImage> skuImageList) {
        skuImageList.stream().forEach(skuImage -> {
            //set spuId
            skuImage.setSkuId(id);
            int insert = skuImageMapper.insert(skuImage);
            if(insert<=0){
                throw new RuntimeException("保存sKuImage错误,请重试");
            }
        });
    }

    @Resource
    private SpuSaleAttrMapper spuSaleAttrMapper;
    /**
     * 保存销售属性
     * @param spuId
     * @param spuSaleAttrList
     * @return : void
     */
    private void saveSpuAttrList(Long spuId, List<SpuSaleAttr> spuSaleAttrList) {
        //保存销售属性
        spuSaleAttrList.stream().forEach(spuSaleAttr -> {
            //set spuId
            spuSaleAttr.setSpuId(spuId);
            //保存spu销售属性名称表,保存完成以后,销售属性名称就有了id
            int insert = spuSaleAttrMapper.insert(spuSaleAttr);
            if(insert<=0){
                throw new RuntimeException("保存spuSaleAttr错误,请重试");
            }
            //获取销售属性的值
            List<SpuSaleAttrValue> spuSaleAttrValueList = spuSaleAttr.getSpuSaleAttrValueList();
            //保存销售属性的值
            saveSpuAttrValueList(spuId,spuSaleAttr.getSaleAttrName(),spuSaleAttrValueList);
        });
    }

    @Resource
    private SpuSaleAttrValueMapper spuSaleAttrValueMapper;
    /**
     * 保存销售属性值表
     * @param spuId
     * @param saleAttrName
     * @param spuSaleAttrValueList
     * @return : void
     */
    private void saveSpuAttrValueList(Long spuId,
                                      String saleAttrName,
                                      List<SpuSaleAttrValue> spuSaleAttrValueList) {
        //save 属性值表
        spuSaleAttrValueList.stream().forEach(spuSaleAttrValue -> {
            //set spuId
            spuSaleAttrValue.setSpuId(spuId);
            spuSaleAttrValue.setSaleAttrName(saleAttrName);
            int insert = spuSaleAttrValueMapper.insert(spuSaleAttrValue);
            if(insert<=0){
                throw new RuntimeException("保存spuAttrValue错误,请重试");
            }
        });
    }

    @Resource
    private SpuImageMapper spuImageMapper;
    /**
     * 保存SpuImage
     * @param spuId
     * @param spuImageList
     * @return : void
     */
    private void saveSpuImageList(Long spuId, List<SpuImage> spuImageList) {
        //save spuImage
        spuImageList.stream().forEach(spuImage -> {
            //set spuId
            spuImage.setSpuId(spuId);
            int insert = spuImageMapper.insert(spuImage);
            if(insert<=0){
                throw new RuntimeException("保存spuImage错误,请重试");
            }
        });
    }
}




