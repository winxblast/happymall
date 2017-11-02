package top.winxblast.happymall.common;

/**
 * 常量类
 *
 * @author winxblast
 * @create 2017/10/20
 **/
public class Const {

    public static final String CURRENT_USER = "currentUser";

    public static final String EMAIL = "email";
    public static final String USERNAME = "username";

    /**
     * 这个分组如果使用枚举类可能显得比较重，这里用一个接口
     * 接口不能包含实例域或静态方法，但可以包含常量
     * 接口中的域将被自动设为 public static final
     */
    public interface Role{
        int ROLE_CUSTOMER = 0; //普通用户
        int ROLE_ADMIN = 1; //管理员
    }
}
