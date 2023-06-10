package com.atguigu.gmall.list.service.impl;

import com.alibaba.fastjson.JSON;
import com.atguigu.gmall.list.service.SearchService;
import com.atguigu.gmall.model.list.Goods;
import com.atguigu.gmall.model.list.SearchResponseAttrVo;
import com.atguigu.gmall.model.list.SearchResponseTmVo;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang.StringUtils;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.nested.ParsedNested;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedLongTerms;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedStringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 包名:com.atguigu.gmall.list.service.impl
 *
 * @author Weirdo
 * 日期2023/4/3 14:12
 * 商品搜索接口实现类
 */
@Service
@Log4j2
public class SearchServiceImpl implements SearchService {
    @Resource
    private RestHighLevelClient restHighLevelClient;

    /**
     * 商品搜索
     *
     * @param searchData
     * @return
     */
    @Override
    public Map<String, Object> search(Map<String, String> searchData) {
        try {
            //参数校验 可以做例如广告位  参数为空跳转配置的广告页面
            //构建条件->拼接条件->存储条件->条件构建后查询前可以做聚合查询(聚合查询还可以设置size(显示多少条)->排序->分页
            SearchRequest searchRequest = getSearchRequest(searchData);
            //执行搜索
            SearchResponse searchResponse =
                    restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            //解析结果并返回
            return getSearchResult(searchResponse);

        } catch (Exception e) {
            log.error("搜索发生异常,异常内容为" + e.getMessage());
        }
        return null;
    }

    /**
     * 构建查询条件
     *
     * @param searchData
     * @return
     */
    private SearchRequest getSearchRequest(Map<String, String> searchData) {
        //初始化查询构造器
        SearchSourceBuilder builder = new SearchSourceBuilder();
        //构建组合查询对象
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        //关键字作为查询条件
        String keywords = searchData.get("keywords");
        if (!StringUtils.isEmpty(keywords)) {
            boolQueryBuilder.must(QueryBuilders.matchQuery("title", keywords));
        }
        //分类作为查询条件
        String category = searchData.get("category");
        if (!StringUtils.isEmpty(category)) {
            //参数不为空处理参数
            String[] split = category.split(":");
            boolQueryBuilder.must(QueryBuilders.termQuery("category3Id", split[0]));
        }
        //品牌作为查询条件
        String tradeMark = searchData.get("tradeMark");
        if (!StringUtils.isEmpty(tradeMark)) {
            String[] split = tradeMark.split(":");
            boolQueryBuilder.must(QueryBuilders.termQuery("tmId", split[0]));
        }
        //平台属性拼接
        searchData.entrySet().stream().forEach(data -> {
            String key = data.getKey();
            if (key.startsWith("attr_")) {
                //平台属性参数的值
                String value = data.getValue();
                //切分
                String[] split = value.split(":");
//                //构建nested对象查询  放到下面查询里了
//                QueryBuilders.nestedQuery("attrs",
//                        QueryBuilders.boolQuery()
//                                .must(QueryBuilders.termQuery("attrs.attrName",split[1]))
//                                .must(QueryBuilders.termQuery("attrs.attrId",split[0])),
//                        ScoreMode.None);
                //将构建好的条件存入原查询条件中
                boolQueryBuilder.must(QueryBuilders.nestedQuery("attrs",
                        QueryBuilders.boolQuery()
                                .must(QueryBuilders.termQuery("attrs.attrValue", split[1]))
                                .must(QueryBuilders.termQuery("attrs.attrId", split[0])),
                        ScoreMode.None));
            }
        });
        //价格作为查询条件 500-100元  3000元以上
        String price = searchData.get("price");
        if (!StringUtils.isEmpty(price)) {
            //把除价格数字以外的替换掉
            price = price.replace("元", "").replace("以上", "");
            //把区间的价格切分
            String[] split = price.split("-");
            //大于等于第一个价格值
            boolQueryBuilder.must(QueryBuilders.rangeQuery("price").gte(split[0]));
            //如果价格长度大于1就是价格区间
            if (split.length > 1) {
                boolQueryBuilder.must(QueryBuilders.rangeQuery("price").lt(split[1]));
            }
        }
        //存储查询条件
        builder.query(boolQueryBuilder);
        //查询条件都做好了以后 将查询条件放入查询之前 可以设置聚合条件
        //品牌聚合条件
        builder.aggregation(AggregationBuilders.terms("aggTmId").field("tmId")
                .subAggregation(AggregationBuilders.terms("aggTmName").field("tmName"))
                .subAggregation(AggregationBuilders.terms("aggTmLogoUrl").field("tmLogoUrl"))
        ).size(100);//.size()是主聚合条件下的
        //设置平台属性聚合条件
        builder.aggregation(
                AggregationBuilders.nested("aggAttrs", "attrs")
                        .subAggregation(
                                AggregationBuilders.terms("aggAttrId").field("attrs.attrId")
                                        .subAggregation(AggregationBuilders.terms("aggAttrName").field("attrs.attrName"))
                                        .subAggregation(AggregationBuilders.terms("aggAttrValue").field("attrs.attrValue"))

                                        .size(100)
                        )
        );
        //设置排序
        String sortField = searchData.get("sortField");
        String sortRule = searchData.get("sortRule");
        if (!StringUtils.isEmpty(sortField) &&
                !StringUtils.isEmpty(sortRule)) {
            builder.sort(sortField, SortOrder.valueOf(sortRule));
        }
        builder.sort("id", SortOrder.ASC);

        //分页
        int pageNum = getPage(searchData.get("pageNum"));
        builder.size(50);
        builder.from((pageNum - 1) * 50);
        //设置高亮条件
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        highlightBuilder.field("title");
        highlightBuilder.preTags("<font style=color:red>");
        highlightBuilder.postTags("</font>");
        builder.highlighter(highlightBuilder);
        //拼接查询条件
        SearchRequest searchRequest = new SearchRequest("goods_java0107");
        searchRequest.source(builder);
        //返回
        return searchRequest;
    }

    /**
     * 计算分页
     *
     * @param pageNum
     * @return
     */
    private int getPage(String pageNum) {
        try {
            int i = Integer.parseInt(pageNum);
            return i > 0 ? i : 1;
        } catch (Exception e) {
            return 1;
        }
    }

    /**
     * 解析搜索结果
     *
     * @param searchResponse
     * @return
     */
    private Map<String, Object> getSearchResult(SearchResponse searchResponse) {
        //返回结果初始化1
        Map<String, Object> result = new HashMap<>();
        //获取所有符合条件的数据
        SearchHits hits = searchResponse.getHits();
        //获取数据总条数
        long totalHits = hits.getTotalHits();
        result.put("totalHits", totalHits);
        //获取迭代器
        Iterator<SearchHit> iterator = hits.iterator();
        //初始化商品列表
        List<Goods> goodsList = new ArrayList<>();
        //遍历每条数据
        while (iterator.hasNext()) {
            //获取到每条命中的数据
            SearchHit next = iterator.next();
            //获取JSON类型的原始数据
            String sourceAsString = next.getSourceAsString();
            //对数据进行反序列化
            Goods goods = JSON.parseObject(sourceAsString, Goods.class);
            //获取高亮数据
            HighlightField highlightField = next.getHighlightFields().get("title");
            if (highlightField != null){
                Text[] fragments = highlightField.getFragments();
                if (null != fragments && fragments.length>0){
                    String title = "";
                    //遍历获取高亮数据
                    for (Text fragment : fragments){
                        title += fragment;
                    }
                    goods.setTitle(title);
                }
            }
            //保存
            goodsList.add(goods);
        }
        result.put("goodsList", goodsList);
        //获取聚合数据
        Aggregations aggregations = searchResponse.getAggregations();
        //获取品牌聚合结果
        List<SearchResponseTmVo> searchResponseTmVoList = getTmAggResult(aggregations);
        result.put("searchResponseTmVoList", searchResponseTmVoList);
        //获取平台属性聚合结果
        List<SearchResponseAttrVo> searchResponseAttrVoList = getAttrAggResult(aggregations);
        result.put("searchResponseAttrVoList", searchResponseAttrVoList);
        //返回商品列表
        return result;
    }

    /**
     * 获取平台属性聚合结果
     *
     * @param aggregations
     * @return
     */
    private List<SearchResponseAttrVo> getAttrAggResult(Aggregations aggregations) {
        //获取nested聚合的结果
        ParsedNested aggAttr = aggregations.get("aggAttrs");
        //获取子聚合
        ParsedLongTerms aggAttrId = aggAttr.getAggregations().get("aggAttrId");
        //遍历获取
        return aggAttrId.getBuckets().stream().map(aggAttrIdBucket -> {
            SearchResponseAttrVo searchResponseAttrVo = new SearchResponseAttrVo();
            //获取attrId
            long attrId = aggAttrIdBucket.getKeyAsNumber().longValue();
            searchResponseAttrVo.setAttrId(attrId);
            //获取attrName
            ParsedStringTerms aggAttrName =
                    ((Terms.Bucket) aggAttrIdBucket).getAggregations().get("aggAttrName");
            if (null != aggAttrName.getBuckets() && !aggAttrName.getBuckets().isEmpty()) {
                String attrName = aggAttrName.getBuckets().get(0).getKeyAsString();
                searchResponseAttrVo.setAttrName(attrName);
            }
            //获取attrValue
            ParsedStringTerms aggAttrValue =
                    ((Terms.Bucket) aggAttrIdBucket).getAggregations().get("aggAttrValue");
            if (null != aggAttrValue.getBuckets() && !aggAttrValue.getBuckets().isEmpty()) {
                List<String> attrValueList = aggAttrValue.getBuckets().stream().map(attrValueBucket -> {
                    //获取值的内容并返回
                    return attrValueBucket.getKeyAsString();
                }).collect(Collectors.toList());
                searchResponseAttrVo.setAttrValueList(attrValueList);
            }
            return searchResponseAttrVo;
        }).collect(Collectors.toList());
    }

    /**
     * 获取品牌的聚合结果
     *
     * @param aggregations
     * @return
     */
    private List<SearchResponseTmVo> getTmAggResult(Aggregations aggregations) {
        //获取品牌的聚合结果
        ParsedLongTerms aggTmId = aggregations.get("aggTmId");
        //遍历品牌结果
        return aggTmId.getBuckets().stream().map(tmIdBucket -> {
            SearchResponseTmVo searchResponseTmVo = new SearchResponseTmVo();
            //获取品牌的id
            long tmId = ((Terms.Bucket) tmIdBucket).getKeyAsNumber().longValue();
            searchResponseTmVo.setTmId(tmId);
            //子聚合 品牌的名字
            ParsedStringTerms aggTmName =
                    ((Terms.Bucket) tmIdBucket).getAggregations().get("aggTmName");
            if (null != aggTmName.getBuckets() && !aggTmName.getBuckets().isEmpty()) {
                String tmName = aggTmName.getBuckets().get(0).getKeyAsString();
                searchResponseTmVo.setTmName(tmName);
            }
            //子聚合 品牌的logo
            ParsedStringTerms aggTmLogoUrl =
                    ((Terms.Bucket) tmIdBucket).getAggregations().get("aggTmLogoUrl");
            if (null != aggTmLogoUrl && !aggTmLogoUrl.getBuckets().isEmpty()) {
                String tmLogoUrl = aggTmLogoUrl.getBuckets().get(0).getKeyAsString();
                searchResponseTmVo.setTmLogoUrl(tmLogoUrl);
            }
            //返回聚合结果
            return searchResponseTmVo;
        }).collect(Collectors.toList());
    }
}
