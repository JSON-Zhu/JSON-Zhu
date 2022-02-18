package com.atguigu.gmall.product.utils;

import org.csource.fastdfs.ClientGlobal;
import org.springframework.core.io.ClassPathResource;

/**
 * 自定义的文件的上传工具包
 */
public class FileUploadUtil {

    /**
     *  static code snippet
     */
    static {
        try {
            //read conf
            ClassPathResource resource = new ClassPathResource("tracker.conf");
            //init tracker
            ClientGlobal.init(resource.getPath());
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
