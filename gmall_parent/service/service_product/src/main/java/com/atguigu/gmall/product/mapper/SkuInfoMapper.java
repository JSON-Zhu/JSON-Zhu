package com.atguigu.gmall.product.mapper;

import com.atguigu.gmall.model.product.SkuInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

/**
 * skuInfo Mapper
 * @author XQ.Zhu
 */
@Mapper
public interface SkuInfoMapper extends BaseMapper<SkuInfo> {

    /**
     * 扣减库存
     * @param skuId
     * @param num
     * @return : int
     * 使用乐观锁
     */
    @Update("update sku_info set stock=stock-#{num} where id=${skuId} and stock>=#{num}")
    int decreaseStock(@Param("skuId") Long skuId, @Param("num") Integer num);


    /**
     * 回滚库存
     * @param skuId
     * @param num
     * @return : int
     * 使用乐观锁
     */
    @Update("update sku_info set stock=stock+#{num} where id=${skuId} ")
    int rollbackStock(@Param("skuId") Long skuId, @Param("num") Integer num);

    /**
     * 数据库的隔离级别:
     * 读未提交: 一个事务能够读到另外其他事务没有提交的操作(新增 修改 删除)的结果(脏读 幻读 不可重复读)
     * 读已提交: 一个事务只能够读到另外其他事务已经提交的操作(新增 修改 删除)的结果(幻读 不可重复读)
     * 可重复读: 在同一个事务内,对同一份数据多次读到结果是一样的(防止修改的情况)(幻读)
     * 串行化: 排队一个一个操作(没有任何的数据库的问题)(效率很低)
     */

    /**
     * 脏读: 毛若鹏的事务做了操作(修改)但是没有提交事务, 蒋开为的事务读到了这条数据(毛若鹏修改后的数据), 毛若鹏的事务提交失败!
     *
     * 幻读: (新增)数据像出现幻觉一样: 无法阻止其他的事务进行新增操作(行锁),对同一份查询(SQL) select count(*) from sku_info
     *
     * 不可重复读: 在同一个事务内,对同一份数据多次读到结果是不一样的（update）
     */
}
