package com.atguigu.gmall.web.controller;

import com.atguigu.gmall.list.feign.ListFeign;
import com.atguigu.gmall.web.utils.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

/**
 * SearchController
 *
 * @author XQ.Zhu
 * @version 1.0
 * 2022/3/1 19:14
 **/
@Controller
@RequestMapping(value = "/page/search")
public class SearchController {

    @Autowired
    private ListFeign listFeign;

    @Value("${item.url}")
    private String itemUrl;

    /**
     * 搜索页面打开
     * @param searchData
     * @return : java.lang.String
     */
    @GetMapping
    public  String search(@RequestParam Map<String,String> searchData, Model model){
        //远程服务 获取数据
        Map<String, Object> searchMap = listFeign.search(searchData);
        //数据保存到model中,页面展示使用
        model.addAllAttributes(searchMap);
        //查询条件回显
        model.addAttribute("searchData",searchData);
        String url = getUrl(searchData);
        model.addAttribute("url",url);
        //获取总数据量
        Object totalHits = searchMap.get("totalHits");
        //每页显示数量
        Integer size=100;
        //当前页
        Integer pageNum = getPageNum(searchData.get("pageNum"));
        //初始化页面对象
        Page<Object> page = new Page<>(Long.parseLong(totalHits.toString()), pageNum, size);
        model.addAttribute("page",page);
        //存储基础静态页面的url地址
        model.addAttribute("itemUrl",itemUrl);
        return "list";
    }



    /**
     * 拼接url
     * @param searchData
     * @return : java.lang.String
     */
    private String getUrl(Map<String,String> searchData){
        String url="/page/search?";
        //遍历map
        for (Map.Entry<String, String> entry : searchData.entrySet()) {
            //获取keyword
            String key = entry.getKey();
            String value = entry.getValue();
            //排序处理 (排序规则 排序字段 页码不拼接查询条件)
            if(!("sortRule".equals(key)||"sortField".equals(key)||"pageNum".equals(key))){
                url +=key+"="+value+"&";
            }
        }
        //删除最后一个& 并返回拼接的查询条件字符串
        return url.substring(0,url.length()-1);
    }

    /**
     * 计算页码
     * @param pageNum
     * @return : java.lang.Integer
     */
    private Integer getPageNum(String pageNum) {
        try {
            int i = Integer.parseInt(pageNum);
            return i>0?i:1;
        }catch (Exception e){
            //默认显示第一页
            return 1;
        }
    }
}
