package top.winxblast.happymall.controller.backend;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import top.winxblast.happymall.common.Const;
import top.winxblast.happymall.common.ResponseCode;
import top.winxblast.happymall.common.ServerResponse;
import top.winxblast.happymall.pojo.User;
import top.winxblast.happymall.service.CategoryService;
import top.winxblast.happymall.service.UserService;

import javax.servlet.http.HttpSession;

/**
 * 目录管理模块
 *
 * @author winxblast
 * @create 2017/10/28
 **/
@Controller
@RequestMapping(value = "/manage/category")
public class CategoryManageController {

    @Autowired
    private UserService userService;

    @Autowired
    private CategoryService categoryService;

    /**
     * 添加商品类别
     * 添加这个注解@RequestParam(value = "parentId",defaultValue = "0")主要是为了没有传这个参数时有个默认值。
     * @param session 通过session验证用户是否已登录以及是否为管理员
     * @param categoryName
     * @param parentId
     * @return
     */
    @RequestMapping(value = "add_category.do", method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<String> addCategory(HttpSession session, String categoryName, @RequestParam(value = "parentId",defaultValue = "0") int parentId) {
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户未登录，请登录");
        }

        //校验是否为管理员，功能放到service中
        if(userService.checkAdminRole(user).isSuccess()) {
            //是管理员
            //增加处理分类的逻辑
            return categoryService.addCategory(categoryName, parentId);
        } else {
            return ServerResponse.createByErrorMessage("无权限，需要管理员权限");
        }
    }

    /**
     * 修改品类名称
     * @param session
     * @param categoryId
     * @param categoryName
     * @return
     */
    @RequestMapping(value = "set_category_name.do", method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<String> setCategoryName(HttpSession session, Integer categoryId, String categoryName) {
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户未登录，请登录");
        }

        //校验是否为管理员，功能放到service中
        if(userService.checkAdminRole(user).isSuccess()) {
            //是管理员
            //更新categoryName
            return categoryService.updateCategoryName(categoryId, categoryName);
        }else {
            return ServerResponse.createByErrorMessage("无权限，需要管理员权限");
        }
    }

    /**
     * 通过父类id获取下一级子分类
     * @param session
     * @param categoryId 不传参数默认为0
     * @return
     */
    @RequestMapping(value = "get_category.do", method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse getChildrenParallelCategory(HttpSession session, @RequestParam(value = "categoryId", defaultValue = "0")Integer categoryId) {
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户未登录，请登录");
        }

        //校验管理员
        if(userService.checkAdminRole(user).isSuccess()) {
            //查询子节点的category信息，并且不递归，只查询下一级
            return categoryService.getChildrenParallelCategory(categoryId);
        } else {
            return ServerResponse.createByErrorMessage("无权限，需要管理员权限");
        }
    }

    /**
     * 通过递归查询该分类下所有子分类
     * 这个方法的名字取的我也是醉了···最讨厌取名字了，这里老师取名感觉也是随意
     * @param session
     * @param categoryId 不传参数默认为0
     * @return
     */
    @RequestMapping(value = "get_deep_category.do", method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse getCategoryAndDeepChildrenCategory(HttpSession session, @RequestParam(value = "categoryId", defaultValue = "0")Integer categoryId) {
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户未登录，请登录");
        }

        //校验管理员
        if(userService.checkAdminRole(user).isSuccess()) {
            //查询当前节点及递归子节点的id
            return categoryService.selectCategoryAndChildrenById(categoryId);
        } else {
            return ServerResponse.createByErrorMessage("无权限，需要管理员权限");
        }
    }

}
