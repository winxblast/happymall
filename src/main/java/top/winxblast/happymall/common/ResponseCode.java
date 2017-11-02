package top.winxblast.happymall.common;

/**
 * 响应编码的枚举类
 *
 * @author winxblast
 * @create 2017/10/19
 **/
public enum ResponseCode {

    //以后想要扩展的时候修改下面这一部分就行了
    SUCCESS(0,"SUCCESS"),
    ERROR(1,"ERROR"),
    NEED_LOGIN(10,"NEED_LOGIN"),
    ILLEGAL_ARGUMENT(2,"ILLEGAL_ARGUMENT");

    private final int code;
    private final String desc;

    //这里使用default的修饰，只允许类内部及本包调用，我不能很好理解这样的好处
    ResponseCode(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public int getCode() {
        return code;
    }
    public String getDesc() {
        return desc;
    }
}
