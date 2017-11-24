package top.winxblast.happymall.util;

import org.apache.commons.net.ftp.FTPClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

/**
 * FTP服务器工具类
 *
 * @author winxblast
 * @create 2017/11/12
 **/
public class FTPUtil {

    public static final Logger logger = LoggerFactory.getLogger(FTPUtil.class);

    private static String ftpIp = PropertiesUtil.getProperty("ftp.server.ip");
    private static String ftpUser = PropertiesUtil.getProperty("ftp.user");
    private static String ftpPass = PropertiesUtil.getProperty("ftp.pass");

    public FTPUtil(String ip, int port, String user, String pwd) {
        this.ip = ip;
        this.port = port;
        this.user = user;
        this.pwd = pwd;
    }

    public static boolean upload(List<File> fileList) throws IOException {
        FTPUtil ftpUtil = new FTPUtil(ftpIp,21,ftpUser,ftpPass);
        logger.info("开始连接FTP服务器");
        //异常抛出，这样在服务层能够有相应的处理
        //老师在这里remotePath加了img实际上不需要这样，不然要在图片服务器前缀那里也修改一下，我这里就不加了
        boolean result = ftpUtil.upload("", fileList);
        logger.info("开始连接FTP服务器，结束上传，上传结果:{}", result);
        return result;
    }

    private boolean upload(String remotePath, List<File> fileList) throws IOException {
        boolean uploaded = true;
        FileInputStream fis = null;
        //连接FTP服务器
        if(connectServer(this.ip, this.port, this.user, this.pwd)) {
            try {
                //这样可以修改文件目录
                ftpClient.changeWorkingDirectory(remotePath);
                //设置缓冲区
                ftpClient.setBufferSize(1024);
                ftpClient.setControlEncoding("UTF-8");
                //设置为二进制编码形式可以防止乱码
                ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
                //打开本地被动模式，详见vsftp安装
                ftpClient.enterLocalPassiveMode();
                //然后可以开始上传
                for(File fileItem : fileList) {
                    fis = new FileInputStream(fileItem);
                    ftpClient.storeFile(fileItem.getName(), fis);
                }

            } catch (IOException e) {
                logger.error("上传文件异常",e);
                uploaded = false;
            } finally {
                //要释放连接，不然会占用资源，时间长了会有问题
                fis.close();
                ftpClient.disconnect();
            }
        }
        return uploaded;
    }

    private boolean connectServer(String ip, int port, String user, String pwd) {
        ftpClient = new FTPClient();
        boolean isSuccess = false;
        try {
            ftpClient.connect(ip);
            isSuccess = ftpClient.login(user, pwd);
        } catch (IOException e) {
            logger.error("连接FTP服务器异常",e);
        }
        return isSuccess;
    }

    private String ip;
    private int port;
    private String user;
    private String pwd;
    private FTPClient ftpClient;

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public FTPClient getFtpClient() {
        return ftpClient;
    }

    public void setFtpClient(FTPClient ftpClient) {
        this.ftpClient = ftpClient;
    }
}
