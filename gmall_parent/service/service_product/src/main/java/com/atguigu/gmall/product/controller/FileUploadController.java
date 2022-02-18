package com.atguigu.gmall.product.controller;

import com.atguigu.gmall.common.result.Result;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * 文件上传的controller
 */
@RestController
@RequestMapping(value = "/admin/product")
public class FileUploadController {
    @Value("${fileServer.url}")
    private String fileUrl;

    @PostMapping(value = "/fileUpload")
    public Result fileUpload(@RequestParam MultipartFile file){
        return Result.ok();
    }
}
