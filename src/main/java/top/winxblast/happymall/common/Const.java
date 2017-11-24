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
        //普通用户
        int ROLE_CUSTOMER = 0;
        //管理员
        int ROLE_ADMIN = 1;
    }

    /**
     * 商品状态枚举类，指上下架状态
     */
    public enum ProductStatusEnum{
        ON_SALE(1,"在线");

        private int code;
        private String value;

        ProductStatusEnum(int code, String value) {
            this.code = code;
            this.value = value;
        }

        public int getCode() {
            return code;
        }

        public String getValue() {
            return value;
        }
    }
}
