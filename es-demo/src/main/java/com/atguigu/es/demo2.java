package com.atguigu.es;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.es.pojo.Article;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * 包名:com.atguigu.es
 *
 * @author Weirdo
 * 日期2023/4/1 15:39
 *  创建数据
 */
public class demo2 {
    public static void main(String[] args) throws UnknownHostException {
        //初始化es客户端连接对象  TCP方式  Settings.EMPTY 不使用es集群
        PreBuiltTransportClient client = new PreBuiltTransportClient(Settings.EMPTY);
        //设置es的IP地址和端口号
        client.addTransportAddress(new TransportAddress(InetAddress.getByName("localhost"),9300));
        //准备一个批量请求器
        BulkRequestBuilder bulk = client.prepareBulk();
        for (long i = 100; i < 200; i++) {
            //构建对象
            Article article = new Article();
            article.setId(i);
            article.setTitle(i+"雄安蓬勃发展每一天");
            article.setContent(i+"雄安新区设立以来，无数逐梦者满怀激情，参与到热火朝天的建设中来，见证了这片土地日新月异的变化");
            bulk.add(//新增数据
                    client.prepareIndex("java0926_new","article",i+"").
                            setSource(JSONObject.toJSONString(article),XContentType.JSON));

            //分批次
            if (i %10 ==0){
                bulk.execute().actionGet();
            }
        }
        bulk.execute().actionGet();
        //关闭连接
        client.close();
    }

}
