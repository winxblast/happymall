package top.winxblast.happymall.controller.backend;

import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import top.winxblast.happymall.common.Const;
import top.winxblast.happymall.common.ResponseCode;
import top.winxblast.happymall.common.ServerResponse;
import top.winxblast.happymall.pojo.Product;
import top.winxblast.happymall.pojo.User;
import top.winxblast.happymall.service.FileService;
import top.winxblast.happymall.service.ProductService;
import top.winxblast.happymall.service.UserService;
import top.winxblast.happymall.util.PropertiesUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Map;

/**
 * 商品后台管理
 *
 * @author winxblast
 * @create 2017/11/02
 **/
@Controller
@RequestMapping(value = "/manage/product")
public class ProductManageController {

    @Autowired
    private UserService userService;
    @Autowired
    private ProductService productService;
    @Autowired
    private FileService fileService;

    /**
     * 新增或更新商品
     * @param session
     * @param product
     * @return
     */
    @RequestMapping(value = "save.do", method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse productSave(HttpSession session, Product product) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if(user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户未登录，请登录管理员");
        }
        if(userService.checkAdminRole(user).isSuccess()) {
            //填充我们增加产品的业务逻辑
            return productService.saveOrUpdateProduct(product);
        } else {
            return ServerResponse.createByErrorMessage("无权限操作");
        }
    }

    /**
     * 商品上下架
     * @param session
     * @param productId
     * @param status
     * @return
     */
    @RequestMapping(value = "set_sale_status.do", method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse setSaleStatus(HttpSession session, Integer productId, Integer status) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if(user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户未登录，请登录管理员");
        }
        if(userService.checkAdminRole(user).isSuccess()) {
            //商品上下架业务逻辑
            return productService.setSaleStatus(productId, status);
        } else {
            return ServerResponse.createByErrorMessage("无权限操作");
        }
    }

    /**
     * 商品详情
     * @param session
     * @param productId
     * @return
     */
    @RequestMapping(value = "detail.do", method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse getDetail(HttpSession session, Integer productId) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if(user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户未登录，请登录管理员");
        }

        if(userService.checkAdminRole(user).isSuccess()) {
            //填充业务
            return productService.manageProductDetail(productId);
        } else {
            return ServerResponse.createByErrorMessage("无权限操作");
        }
    }

    /**
     * 后台商品列表
     * @param session
     * @param pageNum
     * @param pageSize
     * @return
     */
    @RequestMapping(value = "list.do", method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse getList(HttpSession session, @RequestParam(value = "pageNum",defaultValue = "1") int pageNum, @RequestParam(value = "pageSize",defaultValue = "10")int pageSize) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if(user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户未登录，请登录管理员");
        }

        if(userService.checkAdminRole(user).isSuccess()) {
            //填充业务
            //分页的一些说明，使用pagehelper包辅助，使用AOP技术，监听我们自己的sql
            return productService.getProductList(pageNum, pageSize);
        } else {
            return ServerResponse.createByErrorMessage("无权限操作");
        }
    }

    /**
     * 通过商品名称和id来获取商品列表
     * @param session
     * @param productName
     * @param productId
     * @param pageNum
     * @param pageSize
     * @return
     */
    @RequestMapping(value = "search.do", method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse productSearch(HttpSession session, String productName, Integer productId, @RequestParam(value = "pageNum",defaultValue = "1") int pageNum, @RequestParam(value = "pageSize",defaultValue = "10")int pageSize) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if(user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户未登录，请登录管理员");
        }

        if(userService.checkAdminRole(user).isSuccess()) {
            //填充业务
            return productService.searchProduct(productName, productId, pageNum, pageSize);
        } else {
            return ServerResponse.createByErrorMessage("无权限操作");
        }
    }

    /**
     * 文件上传
     * @param session
     * @param file
     * @param request
     * @return
     */
    @RequestMapping(value = "upload.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse upload(HttpSession session, @RequestParam(value = "upload_file", required = false) MultipartFile file, HttpServletRequest request) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if(user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户未登录，请登录管理员");
        }

        if(userService.checkAdminRole(user).isSuccess()) {
            //填充业务
            String path = request.getSession().getServletContext().getRealPath("upload");
            String targetFileName = fileService.upload(file,path);
            //根据和前端的约定，需要放回完整的图片地址
            String url = PropertiesUtil.getProperty("ftp.server.http.prefix") + targetFileName;

            Map fileMap = Maps.newHashMap();
            fileMap.put("uri", targetFileName);
            fileMap.put("url", url);
            return ServerResponse.createBySuccess(fileMap);
        } else {
            return ServerResponse.createByErrorMessage("无权限操作");
        }
    }

    /**
     * 富文本上传文件(图片)，这里是这个意思，这里保存的不是富文本，还是图片，只不过是富文本编辑器来调用这个接口
     * 把富文本编辑器接收到的图片传到我们的ftp服务器中
     * @param session
     * @param file
     * @param request
     * @return
     */
    @RequestMapping(value = "richtext_img_upload.do", method = RequestMethod.POST)
    @ResponseBody
    public Map richtextImgUpload(HttpSession session, @RequestParam(value = "upload_file", required = false) MultipartFile file, HttpServletRequest request, HttpServletResponse response) {
        Map resultMap = Maps.newHashMap();
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if(user == null) {
            resultMap.put("success", false);
            resultMap.put("msg", "用户未登录，请登录管理员");
            return resultMap;
        }

        //富文本中对于返回值有自己的要求，我们使用的simditor，所以按照simditor要求返回
        //同时simditor还要求修改servletresponse
        //参考网站http://simditor.tower.im/docs/doc-config.html
//        {
//            "success": true/false,
//                "msg": "error message", # optional
//            "file_path": "[real file path]"
//        }
        if(userService.checkAdminRole(user).isSuccess()) {
            //填充业务
            String path = request.getSession().getServletContext().getRealPath("upload");
            String targetFileName = fileService.upload(file,path);
            if(StringUtils.isBlank(targetFileName)) {
                resultMap.put("success", false);
                resultMap.put("msg", "上传失败");
                return resultMap;
            } else {
                //根据和前端的约定，需要放回完整的图片地址
                String url = PropertiesUtil.getProperty("ftp.server.http.prefix") + targetFileName;
                resultMap.put("success", true);
                resultMap.put("msg", "上传成功");
                resultMap.put("file_path", url);
                //这就是修改response，算是和前端的约定
                response.addHeader("Access-Control-Allow-Headers", "X-File-Name");
                return resultMap;
            }
        } else {
            resultMap.put("success", false);
            resultMap.put("msg", "无权限操作");
            return resultMap;
        }
    }

}
