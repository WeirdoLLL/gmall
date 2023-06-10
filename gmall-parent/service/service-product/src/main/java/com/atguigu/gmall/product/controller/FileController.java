package com.atguigu.gmall.product.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.product.util.FileUtil;
import org.csource.common.MyException;
import org.csource.fastdfs.ClientGlobal;
import org.csource.fastdfs.StorageClient;
import org.csource.fastdfs.TrackerClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * 包名:com.atguigu.gmall.product.controller
 *
 * @author Weirdo
 * 日期2023/3/25 15:31
 */
@RestController
@RequestMapping("/admin/product")
public class FileController {
    @Value(("${fileServer.url}"))
    private String fileURL;
    @PostMapping("/fileUpload")
    public Result<String> fileUpload(@RequestParam("file")MultipartFile file) throws IOException, MyException {
        return Result.ok(fileURL+ FileUtil.fileUpload(file));
    }
}
