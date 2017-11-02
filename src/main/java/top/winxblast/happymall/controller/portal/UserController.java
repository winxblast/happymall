package top.winxblast.happymall.controller.portal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import top.winxblast.happymall.common.Const;
import top.winxblast.happymall.common.ResponseCode;
import top.winxblast.happymall.common.ServerResponse;
import top.winxblast.happymall.pojo.User;
import top.winxblast.happymall.service.UserService;

import javax.servlet.http.HttpSession;

/**
 * 前台用户接口设计的控制层
 *
 * @author winxblast
 * @create 2017/10/19
 **/
@Controller
//因为都要给到这个地址下，所以放在类前面，方法前面放更细的地址
@RequestMapping("/user/")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 用户登录
     * @param username
     * @param password
     * @param session
     * @return
     */
    //这里login.do的设计要与之前的用户接口定义相同，method指定请求的方式
    @RequestMapping(value = "login.do",method = RequestMethod.POST)
    //responsebody注解表示返回时自动使用SpringMVC Jackson插件将返回值序列化为json
    //它的配置在dispatcher-servlet.xml中
    @ResponseBody
    public ServerResponse<User> login(String username, String password, HttpSession session) {
        //service-->mybatis-->dao
        ServerResponse<User> response = userService.login(username,password);
        if(response.isSuccess()) {
            session.setAttribute(Const.CURRENT_USER, response.getData());
        }
        return response;
    }

    /**
     * 用户登出
     * @param session
     * @return
     */
    //这里method也就只使用了GET方法，登出比较简单
    @RequestMapping(value = "logout.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> logout(HttpSession session) {
        //登出就直接在session中把当前用户删除即可
        session.removeAttribute(Const.CURRENT_USER);
        return ServerResponse.createBySuccess();
    }

    /**
     * 用户注册
     * @param user
     * @return
     */
    @RequestMapping(value = "register.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> register(User user) {
        return userService.register(user);
    }

    /**
     * 检查用户名，email是否存在
     * 虽然注册时已经含有这个检查了（注册中的检查是为了防止恶意调用注册接口）
     * 这里的检查是为了返回前端一个检查结果，这样好实时显示
     * @param str
     * @param type 通过type是email还是username,去判断str
     * @return
     */
    @RequestMapping(value = "check_valid.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> checkValid(String str, String type) {
        return userService.checkValid(str, type);
    }

    /**
     * 获取用户信息
     * @param session
     * @return
     */
    @RequestMapping(value = "get_user_info.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> getUserInfo(HttpSession session) {
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user != null) {
            return ServerResponse.createBySuccess(user);
        }
        return ServerResponse.createByErrorMessage("用户未登录，无法获取当前用户信息");
    }

    /**
     * 忘记密码，获得密码提示问题
     * @param username
     * @return
     */
    @RequestMapping(value = "forget_get_question.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> forgetGetQuestion(String username) {
        return userService.selectQuestion(username);
    }

    /**
     * 验证密码提示问题的答案，token要放到ServerResponse的泛型T中
     * 在写service时就会用到guava，先用本地的guava缓存来做token，利用
     * 缓存的有效期来搞定token的有效期
     * @param username
     * @param question
     * @param answer
     * @return
     */
    @RequestMapping(value = "forget_check_answer.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> forgetCheckAnswer(String username, String question, String answer) {
        return userService.checkAnswer(username,question,answer);
    }


    /**
     * 重置密码
     * 现在更加能理解为什么要使用token的理由了，原来一直天真的以为验证答案正确
     * 直接进行修改不是一连串的动作么，现在发现不是这样，修改密码是通过另外一个
     * 接口的，那么破坏者就能利用那个没有验证答案的接口直接修改密码了
     * @param username
     * @param passwordNew
     * @param forgetToken
     * @return
     */
    @RequestMapping(value = "forget_reset_password.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> forgetResetPassword(String username, String passwordNew, String forgetToken) {
        return userService.forgetResetPassword(username, passwordNew, forgetToken);
    }

    /**
     * 登录状态下重置密码
     * @param session
     * @param passwordOld
     * @param passwordNew
     * @return
     */
    @RequestMapping(value = "reset_password.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> resetPassword(HttpSession session, String passwordOld, String passwordNew) {
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user == null) {
            return ServerResponse.createByErrorMessage("用户未登录");
        }

        return userService.resetPassword(user, passwordOld, passwordNew);
    }

    /**
     * 更新用户信息
     * @param session
     * @param user
     * @return 这里消息的泛型选择User，这样把新的用户信息更新到session中，返回前端后，前端也要把新信息直接更新
     */
    @RequestMapping(value = "update_information.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> updateInformation(HttpSession session, User user) {
        User currentUser = (User) session.getAttribute(Const.CURRENT_USER);
        if(currentUser == null) {
            return ServerResponse.createByErrorMessage("用户未登录");
        }
        //因为user放的更新信息，里面没有用户的id，所以要先放进去，这里也是为了防止越权
        user.setId(currentUser.getId());
        user.setUsername(currentUser.getUsername());

        ServerResponse<User> response = userService.updateInformation(user);
        if(response.isSuccess()) {
            //成功则要更新session
            session.setAttribute(Const.CURRENT_USER, response.getData());
        }
        //最后不管成功失败，直接返回就行
        return response;
    }

    /**
     * 获取用户信息
     * 调用这个方法如果发现没有登录要强制登录
     * @param session
     * @return
     */
    @RequestMapping(value = "get_information.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> getInformation(HttpSession session) {
        User currentUser = (User) session.getAttribute(Const.CURRENT_USER);
        if(currentUser == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "未登录，需要强制登录status=10");
        }
        return userService.getInformation(currentUser.getId());
    }

}
