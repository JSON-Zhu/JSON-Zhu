package com.atguigu.gmall.product.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.product.utils.FileUploadUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;


/**
 * 文件上传的controller
 * @author XQ.Zhu
 */
@RestController
@RequestMapping(value = "/admin/product")
public class FileUploadController {

    /**
     * addr prefix
     * @param null
     * @return : null
     */
    @Value("${fileServer.url}")
    private String fileUrl;

    /**
     * 文件上传
     * @param file
     * @return
     */
    @PostMapping(value = "/fileUpload")
    public Result fileUpload(@RequestParam MultipartFile file) throws Exception {
        String upload = FileUploadUtil.upload(file);
        return Result.ok(fileUrl+upload);
    }
}
