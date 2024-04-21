package com.itheima.reggie.common;

/**
 * 自定义业务异常
 */
public class CustomException extends RuntimeException{
    //构造函数
    public CustomException(String message){
        super(message);
    }

}
