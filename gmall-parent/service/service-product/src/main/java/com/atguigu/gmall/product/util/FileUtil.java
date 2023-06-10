package com.atguigu.gmall.product.util;


import com.sun.demo.jvmti.hprof.Tracker;
import org.apache.commons.io.IOUtils;
import org.csource.common.MyException;
import org.csource.fastdfs.ClientGlobal;
import org.csource.fastdfs.StorageClient;
import org.csource.fastdfs.TrackerClient;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;

/**
 * 包名:com.atguigu.gmall.product.util
 *
 * @author Weirdo
 * 日期2023/3/25 16:50
 * 文件管理工具类
 */
public class FileUtil {

    /**
     * 加载配置类 使用静态代码实现一次加载
     */
    static{

        try {
            //加载配置文件
            ClassPathResource classPathResource = new ClassPathResource("tracker.conf");
            //初始化FastDFS
            ClientGlobal.init(classPathResource.getPath());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (MyException e) {
            e.printStackTrace();
        }
    }


    /**
     * 文件上传
     * @param file
     * @return
     */
    public static String fileUpload(MultipartFile file){
        try {
            //初始化tacker
            TrackerClient trackerClient = new TrackerClient();
            //通过tracker获取storage
            StorageClient storageClient = new StorageClient(trackerClient.getConnection(),null);
            /*
                通过storage上传文件
                需要:
                1 文件字节码
                2 文件扩展名
                3 附加参数: 时间地点水印等
             */
            String[] strings = storageClient.upload_file(file.getBytes(),
                    StringUtils.getFilenameExtension(file.getOriginalFilename()), null);
            //获取文件的访问地址 0-组名-group1 1-路径名-M00/00/00/123.jpg
            return strings[0] + "/"+ strings[1];
        } catch (IOException | MyException e) {
            e.printStackTrace();
        }
        return null;

    }

    /**
     * 文件下载
     * @param groupName
     * @param path
     * @return
     */
    public static byte[] download(String groupName,String path){
        try {
            //初始化tracker和storage
            TrackerClient trackerClient = new TrackerClient();
            StorageClient storageClient = new StorageClient(trackerClient.getConnection(), null);
            //通过storage下载
            byte[] bytes = storageClient.download_file(groupName, path);
            IOUtils.write(bytes,new FileOutputStream("D:/"+UUID.randomUUID().toString()+".jpg"));
            //return storageClient.download_file(groupName,path);
        } catch (IOException | MyException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 文件删除
     * @param groupName
     * @param path
     * @return
     */
    public static Boolean delete(String groupName,String path){
        try {
            //初始化tacker和storage
            TrackerClient trackerClient = new TrackerClient();
            StorageClient storageClient = new StorageClient(trackerClient.getConnection(),null);
            //通过storage删除
            return storageClient.delete_file(groupName, path) == 0;
        } catch (IOException | MyException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void main(String[] args) {
        //delete("group1","M00/00/03/wKjIgGQfIWyAD3MKAAGbWtNBNtQ295.jpg");
        download("group1","M00/00/03/wKjIgGQgtZOAWzwCAAGbWtNBNtQ241.jpg");
    }
}
