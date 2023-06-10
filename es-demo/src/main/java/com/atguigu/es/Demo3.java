package com.atguigu.es;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.es.pojo.Article;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortOrder;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Iterator;

/**
 * 包名:com.atguigu.es
 *
 * @author Weirdo
 * 日期2023/4/1 16:50
 * 搜索
 */
public class Demo3 {

    /**
     * 文档下标查询 主键查询 只查询文档域一次
     *
     * @param args
     */
    public static void main1(String[] args) throws UnknownHostException {
        //初始化客户端
        PreBuiltTransportClient client = new PreBuiltTransportClient(Settings.EMPTY);
        //设置ip和端口号
        client.addTransportAddress(new TransportAddress(InetAddress.getByName("localhost"), 9300));
        //搜索
        GetResponse getResponse = client.prepareGet("java0926_new", "article", "1").get();
        //获取数据
        System.out.println(getResponse.getSourceAsString());
        //关闭资源
        client.close();

    }

    /**
     * 查询所有 查一次
     *
     * @param args
     * @throws UnknownHostException
     */
    public static void main2(String[] args) throws UnknownHostException {
        //初始化客户端
        PreBuiltTransportClient client = new PreBuiltTransportClient(Settings.EMPTY);
        //设置ip和端口号
        client.addTransportAddress(new TransportAddress(InetAddress.getByName("localhost"), 9300));
        //搜索
        SearchResponse searchResponse = client.
                prepareSearch("java0926_new").
                setTypes("article").
                setQuery(QueryBuilders.matchAllQuery()).get();
        //获取数据
        SearchHits hits = searchResponse.getHits();
        System.out.println(hits.getTotalHits());
        //获取迭代器
        Iterator<SearchHit> iterator = hits.iterator();
        while (iterator.hasNext()) {
            SearchHit next = iterator.next();
            System.out.println(next.getSourceAsString());
        }
        //关闭资源
        client.close();

    }

    /**
     * 字符创查询 查两次 经过索引域并且查询条件经过索引域会分词  对查询条件分词这个功能只支持字符串
     *
     * @param args
     * @throws UnknownHostException
     */
    public static void main3(String[] args) throws UnknownHostException {
        //初始化客户端
        PreBuiltTransportClient client = new PreBuiltTransportClient(Settings.EMPTY);
        //设置ip和端口号
        client.addTransportAddress(new TransportAddress(InetAddress.getByName("localhost"), 9300));
        //搜索
        SearchResponse searchResponse = client.
                prepareSearch("java0926_new").
                setTypes("article").
                setQuery(QueryBuilders.queryStringQuery("雄安").field("title"))
                .get();
        //获取数据
        SearchHits hits = searchResponse.getHits();
        System.out.println(hits.getTotalHits());
        //获取迭代器
        Iterator<SearchHit> iterator = hits.iterator();
        while (iterator.hasNext()) {
            SearchHit next = iterator.next();
            System.out.println(next.getSourceAsString());
        }
        //关闭资源
        client.close();

    }

    /**
     * 匹配查询 查询两次 对于输入的条件分词
     *
     * @param args
     * @throws UnknownHostException
     */
    public static void main4(String[] args) throws UnknownHostException {
        //初始化客户端
        PreBuiltTransportClient client = new PreBuiltTransportClient(Settings.EMPTY);
        //设置ip和端口号
        client.addTransportAddress(new TransportAddress(InetAddress.getByName("localhost"), 9300));
        //搜索
        SearchResponse searchResponse = client.
                prepareSearch("java0926_new").
                setTypes("article").
                setQuery(QueryBuilders.matchQuery("title", "雄安")).get();
        //获取数据
        SearchHits hits = searchResponse.getHits();
        System.out.println(hits.getTotalHits());
        //获取迭代器
        Iterator<SearchHit> iterator = hits.iterator();
        while (iterator.hasNext()) {
            SearchHit next = iterator.next();
            System.out.println(next.getSourceAsString());
        }
        //关闭资源
        client.close();
    }

    public static void main5(String[] args) throws UnknownHostException {
        //初始化客户端
        PreBuiltTransportClient client = new PreBuiltTransportClient(Settings.EMPTY);
        //设置ip和端口号
        client.addTransportAddress(new TransportAddress(InetAddress.getByName("localhost"), 9300));
        //搜索
        SearchResponse searchResponse = client.
                prepareSearch("java0926_new").
                setTypes("article").
                setQuery(QueryBuilders.termQuery("title", "48雄安")).get();
        //获取数据
        SearchHits hits = searchResponse.getHits();
        System.out.println(hits.getTotalHits());
        //获取迭代器
        Iterator<SearchHit> iterator = hits.iterator();
        while (iterator.hasNext()) {
            SearchHit next = iterator.next();
            System.out.println(next.getSourceAsString());
        }
        //关闭资源
        client.close();

    }

    public static void main7(String[] args) throws UnknownHostException {
        //初始化客户端
        PreBuiltTransportClient client = new PreBuiltTransportClient(Settings.EMPTY);
        //设置ip和端口号
        client.addTransportAddress(new TransportAddress(InetAddress.getByName("localhost"), 9300));
        //搜索
        SearchResponse searchResponse = client.
                prepareSearch("java0926_new").
                setTypes("article").
                setQuery(QueryBuilders.wildcardQuery("content", "*新区*")).get();
        //获取数据
        SearchHits hits = searchResponse.getHits();
        System.out.println(hits.getTotalHits());
        //获取迭代器
        Iterator<SearchHit> iterator = hits.iterator();
        while (iterator.hasNext()) {
            SearchHit next = iterator.next();
            System.out.println(next.getSourceAsString());
        }
        //关闭资源
        client.close();
    }

    /**
     * 相似度查询
     * @param args
     * @throws UnknownHostException
     */
    public static void main8(String[] args) throws UnknownHostException {
        //初始化客户端
        PreBuiltTransportClient client = new PreBuiltTransportClient(Settings.EMPTY);
        //设置ip和端口号
        client.addTransportAddress(new TransportAddress(InetAddress.getByName("localhost"), 9300));
        //搜索
        SearchResponse searchResponse = client.
                prepareSearch("java0926_new").
                setTypes("article").
                setQuery(QueryBuilders.fuzzyQuery("content", "新区")).get();
        //获取数据
        SearchHits hits = searchResponse.getHits();
        System.out.println(hits.getTotalHits());
        //获取迭代器
        Iterator<SearchHit> iterator = hits.iterator();
        while (iterator.hasNext()) {
            SearchHit next = iterator.next();
            System.out.println(next.getSourceAsString());
        }
        //关闭资源
        client.close();
    }

    public static void main9(String[] args) throws UnknownHostException {
        PreBuiltTransportClient client = new PreBuiltTransportClient(Settings.EMPTY);
        client.addTransportAddress(new TransportAddress(InetAddress.getByName("localhost"),9300));
        SearchResponse searchResponse = client.prepareSearch("java0926_new")
                .setTypes("article")
                .setQuery(QueryBuilders.rangeQuery("id").lt(10).gte(5))
                .get();

        SearchHits hits = searchResponse.getHits();
        System.out.println(hits.getTotalHits());

        Iterator<SearchHit> iterator = hits.iterator();
        while(iterator.hasNext()){
            SearchHit next = iterator.next();
            System.out.println(next.getSourceAsString());
        }

        client.close();
    }

    public static void main10(String[] args) throws UnknownHostException {
        PreBuiltTransportClient client = new PreBuiltTransportClient(Settings.EMPTY);
        client.addTransportAddress(new TransportAddress(InetAddress.getByName("localhost"),9300));
        SearchResponse searchResponse = client.prepareSearch("java0926_new")
                .setTypes("article")
                .setQuery(QueryBuilders.boolQuery()
                            .must(QueryBuilders.rangeQuery("id").lte(10).gte(6))
                            .must(QueryBuilders.termQuery("title","发展"))
                            .must(QueryBuilders.matchQuery("content","设立"))
                )
                .get();

        SearchHits hits = searchResponse.getHits();
        System.out.println(hits.getTotalHits());

        Iterator<SearchHit> iterator = hits.iterator();
        while(iterator.hasNext()){
            SearchHit next = iterator.next();
            System.out.println(next.getSourceAsString());
        }

        client.close();
    }
    public static void main11(String[] args) throws UnknownHostException {
        PreBuiltTransportClient client = new PreBuiltTransportClient(Settings.EMPTY);
        client.addTransportAddress(new TransportAddress(InetAddress.getByName("localhost"),9300));
        SearchResponse searchResponse = client
                .prepareSearch("java0926_new")
                .setTypes("article")
                .setQuery(QueryBuilders.boolQuery()
                        .must(QueryBuilders.rangeQuery("id").lte(50))
                        .must(QueryBuilders.termQuery("title","发展"))
                        .must(QueryBuilders.matchQuery("content","设立"))
                )
                .setFrom(0)
                .setSize(100)
                .addSort("id", SortOrder.ASC)
                .get();

        SearchHits hits = searchResponse.getHits();
        System.out.println(hits.getTotalHits());

        Iterator<SearchHit> iterator = hits.iterator();
        while(iterator.hasNext()){
            SearchHit next = iterator.next();
            System.out.println(next.getSourceAsString());
        }

        client.close();
    }

    public static void main(String[] args) throws UnknownHostException {
        PreBuiltTransportClient client = new PreBuiltTransportClient(Settings.EMPTY);
        client.addTransportAddress(new TransportAddress(InetAddress.getByName("localhost"),9300));

        //构建高亮查询条件
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        highlightBuilder.field("content");
        highlightBuilder.preTags("<font style=color:red>");
        highlightBuilder.postTags("</font>");

        SearchResponse searchResponse = client
                .prepareSearch("java0926_new")
                .setTypes("article")
                .setQuery(QueryBuilders.termQuery("content","建设"))
                .highlighter(highlightBuilder)
                .get();
        SearchHits hits = searchResponse.getHits();
        System.out.println(hits.getTotalHits());

        Iterator<SearchHit> iterator = hits.iterator();
        while(iterator.hasNext()){
            SearchHit next = iterator.next();
            String sourceAsString = next.getSourceAsString();
            Article article = JSONObject.parseObject(sourceAsString, Article.class);
            HighlightField highlightField = next.getHighlightFields().get("content");
            if (highlightBuilder != null){
                Text[] fragments = highlightField.getFragments();
                String content = "";

            }
        }

        client.close();
    }


}