package top.winxblast.happymall.controller.backend;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import top.winxblast.happymall.common.Const;
import top.winxblast.happymall.common.ServerResponse;
import top.winxblast.happymall.pojo.User;
import top.winxblast.happymall.service.UserService;

import javax.servlet.http.HttpSession;

/**
 * 后台管理员相关功能类，控制层
 *
 * @author winxblast
 * @create 2017/10/22
 **/
@Controller
//这里有个问题RequestMapping这里最好不要用manager，因为可能汇合tomcat自己的manager界面冲突，所以改为manage
@RequestMapping("/manage/user/")
public class UserManageController {

    @Autowired
    private UserService userService;

    @RequestMapping(value = "login.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> login(String username, String password, HttpSession session) {
        ServerResponse<User> response = userService.login(username, password);
        if(response.isSuccess()) {
            User user = response.getData();
            if(user.getRole() == Const.Role.ROLE_ADMIN) {
                //说明登录的是管理员
                session.setAttribute(Const.CURRENT_USER, user);
                return response;
            } else {
                return ServerResponse.createByErrorMessage("不是管理员，无法登录");
            }
        }
        return response;
    }
}
