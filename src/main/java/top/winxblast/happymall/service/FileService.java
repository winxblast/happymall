package top.winxblast.happymall.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * 文件服务接口
 *
 * @author winxblast
 * @create 2017/11/12
 **/
public interface FileService {

    String upload(MultipartFile file, String path);

}
