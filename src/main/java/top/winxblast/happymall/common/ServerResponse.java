package top.winxblast.happymall.common;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import java.io.Serializable;

/**
 * 高可用服务响应对象
 * JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)意思是
 * 值为null时不要序列化，因为在生成错误对象时没有data，这时候就不要把data
 * 序列化进json了（有时候msg也为null）（保证序列化json的时候，如果是null的
 * 对象，key也会消失）
 *
 * @author winxblast
 * @create 2017/10/19
 **/
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class ServerResponse<T> implements Serializable {

    //这些都是在5-1接口设计中返回内容提到过的
    /**
     * 状态码
     */
    private int status;
    /**
     * 返回的消息
     */
    private String msg;
    /**
     * 返回数据，可以是一些类的json格式
     * 泛型的好处：这样在返回的时候可以指定泛型里面的内容，也可以不指定泛型里面的强制类型
     */
    private T data;

    //私有构造器,这里老师提到一种问题，我的理解就是泛型T万一是String类型
    //那么构造方法会选择2，3中的哪一个？这个问题需要在后面public方法中考虑
    //所以下面公开的方法要区分有message和data，不过调用构造方法是不会错的了
    private ServerResponse(int status) {
        this.status = status;
    }
    private ServerResponse(int status, T date) {
        this.status = status;
        this.data = date;
    }
    private ServerResponse(int status, String msg) {
        this.status = status;
        this.msg = msg;
    }
    private ServerResponse(int status, String msg, T data) {
        this.status = status;
        this.msg = msg;
        this.data = data;
    }


    /**
     * 根据状态码返回是否成功
     * JsonIgnore使被注解内容不在json序列化结果当中
     * 大概看了一下，JsonSerialize是通过getter方法来获取要序列化的内容的
     * 所以哪个内容不想被包含，就加上jasonignore注解
     * @return
     */
    @JsonIgnore
    public boolean isSuccess() {
        return this.status == ResponseCode.SUCCESS.getCode();
    }

    public int getStatus() {
        return status;
    }
    public String getMsg() {
        return msg;
    }
    public T getData() {
        return data;
    }


    /**
     * 通过该公开方法直接获取一个成功状态码的本对象
     * @param <T>
     * @return
     */
    public static <T> ServerResponse<T> createBySuccess() {
        return new ServerResponse<T>(ResponseCode.SUCCESS.getCode());
    }

    public static <T> ServerResponse<T> createBySuccessMessage(String msg) {
        return new ServerResponse<T>(ResponseCode.SUCCESS.getCode(), msg);
    }

    public static <T> ServerResponse<T> createBySuccess(T data) {
        return new ServerResponse<T>(ResponseCode.SUCCESS.getCode(), data);
    }

    public static <T> ServerResponse<T> createBySuccess(String msg, T data) {
        return new ServerResponse<T>(ResponseCode.SUCCESS.getCode(), msg, data);
    }


    /**
     * 获取错误对象
     * @param <T>
     * @return
     */
    public static <T> ServerResponse<T> createByError() {
        return new ServerResponse<T>(ResponseCode.ERROR.getCode(), ResponseCode.ERROR.getDesc());
    }

    public static <T> ServerResponse<T> createByErrorMessage(String errorMessage) {
        return new ServerResponse<T>(ResponseCode.ERROR.getCode(), errorMessage);
    }

    public static <T> ServerResponse<T> createByErrorCodeMessage(int errorCode, String errorMessage) {
        return new ServerResponse<T>(errorCode, errorMessage);
    }

}


