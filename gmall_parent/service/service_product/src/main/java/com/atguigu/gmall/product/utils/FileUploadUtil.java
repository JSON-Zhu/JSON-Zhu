package com.atguigu.gmall.product.utils;

import org.csource.fastdfs.ClientGlobal;
import org.csource.fastdfs.StorageClient;
import org.csource.fastdfs.TrackerClient;
import org.csource.fastdfs.TrackerServer;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;

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

    /**
     * 文件上传
     */
    public static String upload(MultipartFile file){
        try {
            //create trackerServer
            TrackerClient trackerClient = new TrackerClient();
            TrackerServer trackerServer = trackerClient.getConnection();
            //通过trackerServer获取storage的信息
            StorageClient storageClient = new StorageClient(trackerServer, null);
            //获取文件的名字
            String originalFilename = file.getOriginalFilename();
            //通过storage进行文件的上传
            /**
             * 1. 文件的字节码
             * 2. 文件的扩展名: a.jpg 123.png
             * 3. 附加参数: 时间 地点 描述
             */
            String[] strings = storageClient.upload_file(file.getBytes(),
                    StringUtils.getFilenameExtension(originalFilename), null);
            return strings[0]+"/"+strings[1];
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 文件下载
     * @param groupName
     * @param path
     * @return : byte[]
     */
    public static byte[] download(String groupName,String path){
        try {
            //create trackerServer
            TrackerClient trackerClient = new TrackerClient();
            TrackerServer trackerServer = trackerClient.getConnection();
            //通过trackerServer获取storage的信息
            StorageClient storageClient = new StorageClient(trackerServer, null);
            //通过storage进行文件的下载
            byte[] bytes = storageClient.download_file(groupName, path);
            //返回结果
            System.out.println("bytes.toString() = " + bytes.toString());
            return bytes;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 删除文件
     * @param groupName
     * @param path
     * @return : void
     */
    public static void deleteFile(String groupName,String path){
        try {
            //create trackerServer
            TrackerClient trackerClient = new TrackerClient();
            TrackerServer trackerServer = trackerClient.getConnection();
            //通过trackerServer获取storage的信息
            StorageClient storageClient = new StorageClient(trackerServer, null);
            //通过storage进行文件的下载
            storageClient.delete_file(groupName,path);
            //返回结果
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * test 下载文件到硬盘
     * @param args
     * @return : void
     */
    public static void main(String[] args) throws Exception {
        //文件的字节数组
        byte[] downloadBytes = FileUploadUtil.download("group1", "M00/00/02/wKjIgGIPUGKADvmmAAS_vwT4Xio418.jpg");

        //文件输出流
//        FileOutputStream FileOutputStreameOutputStream = new FileOutputStream("beauty.jpg");
        FileOutputStream fileOutputStream = new FileOutputStream("C:\\3000soft");
        //字节输入流
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(downloadBytes);
        //buff
        byte[] buff = new byte[1024];
        int len=0;
        while ((len=byteArrayInputStream.read(buff))!=-1){
            System.out.println("len = " + len);
            fileOutputStream.write(buff,0,len);
        }
        //关闭流
        byteArrayInputStream.close();
        fileOutputStream.close();

    }
}
