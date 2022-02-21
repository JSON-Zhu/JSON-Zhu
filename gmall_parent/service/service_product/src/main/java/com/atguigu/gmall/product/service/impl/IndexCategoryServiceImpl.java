package com.atguigu.gmall.product.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.gmall.model.product.BaseCategoryView;
import com.atguigu.gmall.product.mapper.BaseCategoryViewMapper;
import com.atguigu.gmall.product.service.IndexCategoryService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * IndexCategoryServiceImpl
 *
 * @author XQ.Zhu
 * @version 1.0
 * 2022/2/21 17:58
 **/
@Service
public class IndexCategoryServiceImpl implements IndexCategoryService {

    @Resource
    private BaseCategoryViewMapper baseCategoryViewMapper;

    /**
     * 查询首页的分类信息
     *
     * @return : void
     */
    @Override
    public List<JSONObject> getIndexCategory() {
        //查询全部分类数据
        List<BaseCategoryView> baseCategoryViews1 = baseCategoryViewMapper.selectList(null);
        //以一级id进行分桶
        Map<Long, List<BaseCategoryView>> category1Map =
                baseCategoryViews1.stream().collect(Collectors.groupingBy(BaseCategoryView::getCategory1Id));
        //新建数据容器
        List<JSONObject> category1JsonList = new ArrayList<>();
        for (Map.Entry<Long, List<BaseCategoryView>> category1Entry : category1Map.entrySet()) {
            JSONObject category1Json = new JSONObject();
            //获取每个一级分类的id
            Long category1Id = category1Entry.getKey();
            category1Json.put("categoryId",category1Id);
            //获取此一级id对应的二级和三级分类的信息
            List<BaseCategoryView> baseCategoryViews2 = category1Entry.getValue();
            //基于二级id再次分桶
            Map<Long, List<BaseCategoryView>> category2Map =
                    baseCategoryViews2.stream().collect(Collectors.groupingBy(BaseCategoryView::getCategory2Id));
            // 每个value这个二级分类对应三级分类的List
            List<JSONObject> category2JsonList = new ArrayList<>();
            for (Map.Entry<Long, List<BaseCategoryView>> category2Entry : category2Map.entrySet()) {
                JSONObject category2Json = new JSONObject();
                //获取每个二级分类的id
                Long category2Id = category2Entry.getKey();
                category2Json.put("categoryId",category2Id);
                //获取每个二级分类对应三级分类的List: 一级二级分类都一样的三级分类的信息
                List<BaseCategoryView> baseCategoryViews3 = category2Entry.getValue();
                //页面需要三级分类的id和名字
                List<JSONObject> category3JsonList = baseCategoryViews3.stream().map(baseCategoryView -> {
                    JSONObject category3Json = new JSONObject();
                    //获取三级分类 id和名字
                    Long category3Id = baseCategoryView.getCategory3Id();
                    String category3Name = baseCategoryView.getCategory3Name();
                    category3Json.put("categoryId",category3Id);
                    category3Json.put("categoryName",category3Name);
                    return category3Json;
                }).collect(Collectors.toList());
                //保存这个二级分类以及对应的三级分类的列表
                category2Json.put("childCategory",category3JsonList);
                String category2Name = baseCategoryViews3.get(0).getCategory2Name();
                category2Json.put("categoryName",category2Name);
                //保存二级json对象到list
                category2JsonList.add(category2Json);
            }
            //保存一级分类以及对应的二级分类的信息
            category1Json.put("childCategory",category2JsonList);
            String category1Name = baseCategoryViews2.get(0).getCategory1Name();
            category1Json.put("categoryName",category1Name);
            category1JsonList.add(category1Json);
        }
        return category1JsonList;
    }
}
