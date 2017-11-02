package top.winxblast.happymall.service;

import top.winxblast.happymall.common.ServerResponse;
import top.winxblast.happymall.pojo.User;

/**
 * 前台用户接口设计
 * 使用接口及接口实现，为以后的AOP做准备；无论在用静态代理还是动态代理包括后续发展
 * 成的AOP，我们都用接口代理。类的代理扩展性没有接口强
 *
 * @author winxblast
 * @create 2017/10/19
 **/
public interface UserService {

    ServerResponse<User> login(String username, String password);

    ServerResponse<String> register(User user);

    ServerResponse<String> checkValid(String str, String type);

    ServerResponse<String> selectQuestion(String username);

    ServerResponse<String> checkAnswer(String username, String question, String answer);

    ServerResponse<String> forgetResetPassword(String username, String passwordNew, String forgetToken);

    ServerResponse<String> resetPassword(User user, String passwordOld, String passwordNew);

    ServerResponse<User> updateInformation(User user);

    ServerResponse<User> getInformation(Integer userId);

    ServerResponse<String> checkAdminRole(User user);

}
