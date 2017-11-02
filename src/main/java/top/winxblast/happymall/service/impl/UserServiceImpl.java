package top.winxblast.happymall.service.impl;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import top.winxblast.happymall.common.Const;
import top.winxblast.happymall.common.ServerResponse;
import top.winxblast.happymall.common.TokenCache;
import top.winxblast.happymall.dao.UserMapper;
import top.winxblast.happymall.pojo.User;
import top.winxblast.happymall.service.UserService;
import top.winxblast.happymall.util.MD5Util;

import java.util.UUID;

/**
 * 前台用户接口实现
 *
 * @author winxblast
 * @create 2017/10/19
 **/
//注意这个service的注解，名字改为接口的首字母小写，后续注入controller也是用这个名字
@Service("userService")
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public ServerResponse<User> login(String username, String password) {
        int resultCount = userMapper.checkUsername(username);
        if(resultCount == 0) {
            return ServerResponse.createByErrorMessage("用户名不存在");
        }

        //密码登录MD5
        String md5Password = MD5Util.MD5EncodeUtf8(password);
        User user = userMapper.selectLogin(username, md5Password);
        if(user == null) {
            //因为上面已经判断过有没有用户名了，所以这里就是密码错误
            return ServerResponse.createByErrorMessage("密码错误");
        }

        //如果到这都没有return，则就是登录成功
        user.setPassword(StringUtils.EMPTY);
        return ServerResponse.createBySuccess("登录成功", user);

    }


    @Override
    public ServerResponse<String> register(User user) {
//        int resultCount = userMapper.checkUsername(user.getUsername());
//        if(resultCount > 0) {
//            return ServerResponse.createByErrorMessage("用户名已存在");
//        }
//
//        resultCount = userMapper.checkEmail(user.getEmail());
//        if(resultCount > 0) {
//            return ServerResponse.createByErrorMessage("email已存在");
//        }
        //根据下面的checkValid方法改造这段内容
        ServerResponse validResponse = this.checkValid(user.getUsername(), Const.USERNAME);
        if(!validResponse.isSuccess()) {
            return validResponse;
        }
        validResponse = this.checkValid(user.getEmail(), Const.EMAIL);
        if(!validResponse.isSuccess()) {
            return validResponse;
        }

        user.setRole(Const.Role.ROLE_CUSTOMER);

        //MD5加密，数据库中不能存储明文，这里的安全知识还涉及重放攻击等，这里是比较简单的设计
        user.setPassword(MD5Util.MD5EncodeUtf8(user.getPassword()));

        //mybatis的insert、delete、update默认返回操作的条数
        int resultCount = userMapper.insert(user);
        if(resultCount == 0) {
            return ServerResponse.createByErrorMessage("注册失败");
        }

        return ServerResponse.createBySuccessMessage("注册成功");
    }

    /**
     * 通过type是email还是username去调用不同的sql判断
     * @param str
     * @param type 同样也把type的值设置常量
     * @return
     */
    @Override
    public ServerResponse<String> checkValid(String str, String type) {
        //注意isNotBlank和isNotEmpty的区别，请看源码
        if(StringUtils.isNotBlank(type)) {
            //type不空才开始校验
            if(Const.USERNAME.equals(type)) {
                int resultCount = userMapper.checkUsername(str);
                if(resultCount > 0) {
                    return ServerResponse.createByErrorMessage("用户名已存在");
                }
            }
            if(Const.EMAIL.equals(type)) {
                int resultCount = userMapper.checkEmail(str);
                if(resultCount > 0) {
                    return ServerResponse.createByErrorMessage("email已存在");
                }
            }

        } else {
            return ServerResponse.createByErrorMessage("参数错误");
        }
        return ServerResponse.createBySuccessMessage("校验成功");
    }

    /**
     * 通过用户名获取密码找回问题
     * @param username
     * @return
     */
    @Override
    public ServerResponse<String> selectQuestion(String username) {
        ServerResponse validResponse = this.checkValid(username, Const.USERNAME);
        if(validResponse.isSuccess()) {
            //用户不存在
            return ServerResponse.createByErrorMessage("用户不存在");
        }
        String question = userMapper.selectQuestionByUsername(username);
        if(StringUtils.isNotBlank(question)) {
            return ServerResponse.createBySuccess(question);
        }
        return ServerResponse.createByErrorMessage("找回密码的问题是空的");
    }

    /**
     * 检查密码提示问题的答案是否正确
     * @param username
     * @param question
     * @param answer
     * @return
     */
    @Override
    public ServerResponse<String> checkAnswer(String username, String question, String answer) {
        int resultCount = userMapper.checkAnswer(username,question,answer);
        if(resultCount > 0) {
            //说明问题及问题答案是这个用户的，并且是正确的
            //这个UUID类是java.util自带的类，可以生成一个重复概率非常非常低的字符串
            String forgeToken = UUID.randomUUID().toString();
            //然后需要把这个forgetToken放到cache中并设置有效期
            TokenCache.setKey(TokenCache.TOKEN_PREFIX + username, forgeToken);
            return ServerResponse.createBySuccess(forgeToken);
        }
        return ServerResponse.createByErrorMessage("问题的答案错误");
    }

    @Override
    public ServerResponse<String> forgetResetPassword(String username, String passwordNew, String forgetToken) {
        if(StringUtils.isBlank(forgetToken)) {
            return ServerResponse.createByErrorMessage("参数错误，token需要传递");
        }
        ServerResponse validResponse = this.checkValid(username,Const.USERNAME);
        if(validResponse.isSuccess()) {
            return ServerResponse.createByErrorMessage("用户不存在");
        }

        String token = TokenCache.getKey(TokenCache.TOKEN_PREFIX + username);
        if(StringUtils.isBlank(token)) {
            return ServerResponse.createByErrorMessage("token无效或者过期");
        }

        if(StringUtils.equals(forgetToken, token)) {
            //看了一些安全方面资料，这里的password应该已经是前端把密码加了固定盐值再MD5之后的内容了
            //所以这里可能是有漏洞的，不应该在后端加密
            //todo 修改加密的模式
            String md5Password = MD5Util.MD5EncodeUtf8(passwordNew);
            int rowCount = userMapper.updatePasswordByUsername(username,md5Password);

            if(rowCount > 0) {
                return ServerResponse.createBySuccessMessage("修改密码成功");
            }
        } else {
            return ServerResponse.createByErrorMessage("token错误，请重新获取重置密码的token");
        }
        return ServerResponse.createByErrorMessage("修改密码失败");
    }

    @Override
    public ServerResponse<String> resetPassword(User user, String passwordOld, String passwordNew) {
        //防止横向越权，要校验一下这个用户的旧密码，且一定要指定是不是这个用户的，因为我们会查询一个count(1)，所以还是要全部核对一遍再修改
        int resultCount = userMapper.checkPassword(user.getId(), MD5Util.MD5EncodeUtf8(passwordOld));
        if(resultCount == 0) {
            return ServerResponse.createByErrorMessage("旧密码错误");
        }

        user.setPassword(MD5Util.MD5EncodeUtf8(passwordNew));
        int updateCount = userMapper.updateByPrimaryKeySelective(user);
        if(updateCount > 0) {
            return ServerResponse.createBySuccessMessage("密码更新成功");
        }
        return ServerResponse.createByErrorMessage("密码更新失败");
    }

    @Override
    public ServerResponse<User> updateInformation(User user) {
        //username是不能被更新的
        //email也要进行一个校验，校验新的email是不是已经存在，并且存在的emai如果相同的话，不能是我们当前这个用户的
        int resultCount = userMapper.checkEmailByUserId(user.getId(), user.getEmail());
        if(resultCount > 0) {
            return ServerResponse.createByErrorMessage("email已存在，请更换email再尝试更新");
        }

        //todo 这个项目的实例还是很简单的，包括手机号的验证等都没有写，等待扩展
        User updateUser = new User();
        updateUser.setId(user.getId());
        updateUser.setUsername(user.getUsername());
        updateUser.setEmail(user.getEmail());
        updateUser.setPhone(user.getPhone());
        updateUser.setQuestion(user.getQuestion());
        updateUser.setAnswer(user.getAnswer());

        int updateCount = userMapper.updateByPrimaryKeySelective(updateUser);
        if(updateCount > 0) {
            return ServerResponse.createBySuccess("更新个人信息成功", updateUser);
        }
        return ServerResponse.createByErrorMessage("更新个人信息失败");
    }

    @Override
    public ServerResponse<User> getInformation(Integer userId) {
        User user = userMapper.selectByPrimaryKey(userId);
        if(user == null) {
            return ServerResponse.createByErrorMessage("找不到当前用户");
        }
        //不能把密码传出去
        user.setPassword(StringUtils.EMPTY);
        return ServerResponse.createBySuccess(user);
    }



    //backend

    /**
     * 校验是否为管理员
     * @param user
     * @return
     */
    @Override
    public ServerResponse<String> checkAdminRole(User user) {
        if(user != null && user.getRole().intValue() == Const.Role.ROLE_ADMIN) {
            return ServerResponse.createBySuccess();
        }
        return ServerResponse.createByError();
    }

}
