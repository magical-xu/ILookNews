package com.aktt.news.module.user.data;

import java.io.Serializable;

/**
 * Created by magical on 17/9/4.
 * Description : 系统消息详情
 */

public class NotifyDetail implements Serializable {

  /**
   * userid : 402881e55dc28260015dc2827ba10000
   * systemMessage : {"mid":"402881e65dfb23d8015dfb23db6a0000","message_type":"0","message_subtype":"0","title":"","content":"系统通知，哈哈哈哈或，我们已上线了4","html_url":"","created_time":"2017-08-19
   * 23:36:57","isread":"1"}
   * mid : 402881e55dc28260015dc2827ba10000
   */

  public String userid;
  public NotifyBean systemMessage;
  public String mid;
}
