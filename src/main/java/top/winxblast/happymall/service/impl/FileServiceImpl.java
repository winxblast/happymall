package top.winxblast.happymall.service.impl;

import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import top.winxblast.happymall.service.FileService;
import top.winxblast.happymall.util.FTPUtil;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

/**
 * 文件服务接口实现类
 *
 * @author winxblast
 * @create 2017/11/12
 **/
@Service(value = "fileService")
public class FileServiceImpl implements FileService{

    private Logger logger = LoggerFactory.getLogger(FileServiceImpl.class);

    /**
     * 文件上传
     * @param file
     * @param path
     * @return 保存时的文件名，异常是返回null
     */
    @Override
    public String upload(MultipartFile file, String path) {
        String fileName = file.getOriginalFilename();
        //扩展名
        //abc.jpg
        String fileExtensionName = fileName.substring(fileName.lastIndexOf(".")+1);
        String uploadFileName = UUID.randomUUID().toString() + "." + fileExtensionName;
        logger.info("开始上传文件,上传文件的文件名:{},上传路径:{},新文件名:{}",fileName,path,uploadFileName);

        File fileDir = new File(path);
        if(!fileDir.exists()) {
            fileDir.setWritable(true);
            fileDir.mkdirs();
        }
        File targetFile = new File(path, uploadFileName);

        try {
            file.transferTo(targetFile);
            //至此文件已经上传成功了

            //将targetFile上传到我们的ftp服务器上
            FTPUtil.upload(Lists.newArrayList(targetFile));

            //上传完成之后删除upload下面的文件
            targetFile.delete();

        } catch (IOException e) {
            logger.error("上传文件异常", e);
            return null;
        }

        return targetFile.getName();
    }

}
