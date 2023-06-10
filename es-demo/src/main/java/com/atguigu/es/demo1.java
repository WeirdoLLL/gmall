package com.atguigu.es;

import org.elasticsearch.action.admin.indices.mapping.put.PutMappingRequest;
import org.elasticsearch.client.ElasticsearchClient;
import org.elasticsearch.client.Request;
import org.elasticsearch.client.Requests;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Setting;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * 包名:com.atguigu.es
 *
 * @author Weirdo
 * 日期2023/4/1 14:26
 * Es练习创建索引和映射
 */
public class demo1 {
//    /**
//     * 创建索引
//     * @param args
//     * @throws UnknownHostException
//     */
//    public static void main1(String[] args) throws UnknownHostException {
//        //初始化es的客户端连接对象  TCP方式操作es ---> Settings.Empty 不使用Es集群
//        TransportClient client = new PreBuiltTransportClient(Settings.EMPTY);
//        //设置es的ip地址和端口号
//        client.addTransportAddress(new TransportAddress(InetAddress.getByName("localhost"),9300));
//        //创建索引
//        client.admin().indices().prepareCreate("java0926").get();
//        //关闭连接
//        client.close();
//    }

    /**
     * 创建索引加映射
     * @param args
     * @throws UnknownHostException
     */
    public static void main(String[] args) throws IOException {
        //初始化es的客户端连接对象  TCP方式操作es ---> Settings.Empty 不使用Es集群
        TransportClient client = new PreBuiltTransportClient(Settings.EMPTY);
        //设置es的ip地址和端口号
        client.addTransportAddress(new TransportAddress(InetAddress.getByName("localhost"),9300));
        //创建索引
        client.admin().indices().prepareCreate("java0926_new").get();
        //构建映射
        XContentBuilder builder = XContentFactory.jsonBuilder();
        //组装映射对象
        builder
                .startObject()
                    .startObject("article2")
                        .startObject("properties")
                            .startObject("2")
                                .field("type","long")
                            .endObject()
                            .startObject("title2")
                                .field("analyzer","ik_max_word")
                                .field("type","text")
                            .endObject()
                            .startObject("content2")
                                .field("analyzer","ik_max_word")
                                .field("type","text")
                            .endObject()
                        .endObject()
                    .endObject()
                .endObject();
        //构建映射请求对象
        PutMappingRequest request = Requests.putMappingRequest("java0926_new").type("article").source(builder);
        //设置映射
        client.admin().indices().putMapping(request);
        //关闭连接
        client.close();
    }
}
