package com.chaoneng.ilooknews.net.data;

import java.io.Serializable;

/**
 * Created by magical on 17/8/18.
 * Description : 固定返回格式
 * {
 * "code":"0"
 * "msg":"0"
 * "data":[]or{}
 * }
 */

public class HttpResult<T> implements Serializable {

  public String code;

  public String msg;

  public T data;
}
