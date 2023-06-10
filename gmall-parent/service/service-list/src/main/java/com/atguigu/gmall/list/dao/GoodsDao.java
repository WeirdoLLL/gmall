package com.atguigu.gmall.list.dao;

import com.atguigu.gmall.model.list.Goods;
import org.springframework.data.elasticsearch.repository.ElasticsearchCrudRepository;
import org.springframework.stereotype.Repository;

/**
 * 包名:com.atguigu.gmall.list.dao
 *
 * @author Weirdo
 * 日期2023/4/2 21:17
 */
@Repository
public interface GoodsDao extends ElasticsearchCrudRepository<Goods,Long> {
}
