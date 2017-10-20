package com.aktt.news.api;

import com.aktt.news.net.data.HttpResult;
import java.io.Serializable;
import java.util.List;

/**
 * Created by magical on 17/8/18.
 * Description :
 */

public class GankModel extends HttpResult implements Serializable {

  /**
   * error : false
   * results : [{"_id":"5992ad85421aa9672cdf0810","createdAt":"2017-08-15T16:15:01.769Z","desc":"致Android开发
   * \u2014\u2014 灵活的Class替换插件","images":["http://img.gank.io/25efa54d-1baf-4e8c-8bde-260c476b4990"],"publishedAt":"2017-08-17T11:36:42.967Z","source":"web","type":"Android","url":"https://github.com/dinuscxj/ClassPlugin","used":true,"who":"dinus_developer"},{"_id":"59945873421aa9672f354dd3","createdAt":"2017-08-16T22:36:35.753Z","desc":"Android
   * 中东阿拉伯语适配，看这一篇够了","images":["http://img.gank.io/1abbc894-96d2-4bca-8239-a29d81ddbd1b"],"publishedAt":"2017-08-17T11:36:42.967Z","source":"web","type":"Android","url":"https://github.com/Freelander/Blog/blob/master/201708/01.md","used":true,"who":"Paul
   * General"},{"_id":"5994e223421aa967262e1c34","createdAt":"2017-08-17T08:24:03.687Z","desc":"欲善其事先利其器，学到的不仅是
   * TODO 技巧，提升的不仅是效率，更有实际项目开发经验、设置代码模版等","publishedAt":"2017-08-17T11:36:42.967Z","source":"web","type":"Android","url":"https://mp.weixin.qq.com/s?__biz=MzIxOTU1MDg5Ng==&mid=2247484097&idx=1&sn=e893157f24d7f8c5b59c40afb1fef3c8&chksm=97d8c71ea0af4e08f0f72dc948c69590a70e9586c21559a5d84fc2389c4b71ab62d34a6a3e98#rd","used":true,"who":"ruicbAndroid"},{"_id":"5994f49c421aa96729c57256","createdAt":"2017-08-17T09:42:52.114Z","desc":"如何实现日夜间主题切换功能？","publishedAt":"2017-08-17T11:36:42.967Z","source":"web","type":"Android","url":"http://url.cn/4ESGFKm","used":true,"who":"陈宇明"},{"_id":"599504a3421aa96729c57257","createdAt":"2017-08-17T10:51:15.440Z","desc":"Kotlin
   * 100% 仿真实现 开眼视频。","images":["http://img.gank.io/404b4cd5-f051-4735-a95c-852f108569ac"],"publishedAt":"2017-08-17T11:36:42.967Z","source":"chrome","type":"Android","url":"https://github.com/LRH1993/Eyepetizer-in-Kotlin","used":true,"who":"代码家"},{"_id":"5995066c421aa9672f354dd9","createdAt":"2017-08-17T10:58:52.958Z","desc":"可能是目前功能最完整的数字计算器了。","images":["http://img.gank.io/bffd7f3b-591c-454a-b288-656d831ddc33"],"publishedAt":"2017-08-17T11:36:42.967Z","source":"chrome","type":"Android","url":"https://github.com/HK-SHAO/DarkCalculator","used":true,"who":"代码家"},{"_id":"59818615421aa90ca209c547","createdAt":"2017-08-02T15:58:13.659Z","desc":"垃圾回收算法与
   * JVM 垃圾回收器综述","publishedAt":"2017-08-15T13:32:36.998Z","source":"chrome","type":"Android","url":"https://zhuanlan.zhihu.com/p/28258571","used":true,"who":"王下邀月熊"},{"_id":"59848574421aa90ca209c564","createdAt":"2017-08-04T22:32:20.718Z","desc":"腾讯开源,
   * 在Android设备上实现可信的指纹认证","images":["http://img.gank.io/e8529e62-606c-44f3-b10d-963d58a2ef83"],"publishedAt":"2017-08-15T13:32:36.998Z","source":"web","type":"Android","url":"https://github.com/Tencent/soter","used":true,"who":"ShineYang"},{"_id":"598bdc54421aa90ca209c589","createdAt":"2017-08-10T12:08:52.490Z","desc":"Android-自定义应用选择器","images":["http://img.gank.io/6d5aedbc-f87f-4edc-98d2-3c55af699c1a"],"publishedAt":"2017-08-15T13:32:36.998Z","source":"web","type":"Android","url":"http://www.jianshu.com/p/3f65576f89b7","used":true,"who":"Julian
   * Chu"},{"_id":"5990fb20421aa96729c57239","createdAt":"2017-08-14T09:21:36.634Z","desc":"PlayPauseView：让播放、暂停按钮优雅的过渡","images":["http://img.gank.io/8ca1bb97-8d30-4deb-9645-a2c2da1970ab"],"publishedAt":"2017-08-14T17:04:50.266Z","source":"web","type":"Android","url":"https://github.com/Lauzy/PlayPauseView","used":true,"who":"Lauzy"}]
   */

  //private boolean error;
  private List<ResultsBean> results;

  public List<ResultsBean> getResults() {
    return results;
  }

  public void setResults(List<ResultsBean> results) {
    this.results = results;
  }

  public static class ResultsBean {
    /**
     * _id : 5992ad85421aa9672cdf0810
     * createdAt : 2017-08-15T16:15:01.769Z
     * desc : 致Android开发 —— 灵活的Class替换插件
     * images : ["http://img.gank.io/25efa54d-1baf-4e8c-8bde-260c476b4990"]
     * publishedAt : 2017-08-17T11:36:42.967Z
     * source : web
     * type : Android
     * url : https://github.com/dinuscxj/ClassPlugin
     * used : true
     * who : dinus_developer
     */

    private String _id;
    private String createdAt;
    private String desc;
    private String publishedAt;
    private String source;
    private String type;
    private String url;
    private boolean used;
    private String who;
    private List<String> images;

    public String get_id() {
      return _id;
    }

    public void set_id(String _id) {
      this._id = _id;
    }

    public String getCreatedAt() {
      return createdAt;
    }

    public void setCreatedAt(String createdAt) {
      this.createdAt = createdAt;
    }

    public String getDesc() {
      return desc;
    }

    public void setDesc(String desc) {
      this.desc = desc;
    }

    public String getPublishedAt() {
      return publishedAt;
    }

    public void setPublishedAt(String publishedAt) {
      this.publishedAt = publishedAt;
    }

    public String getSource() {
      return source;
    }

    public void setSource(String source) {
      this.source = source;
    }

    public String getType() {
      return type;
    }

    public void setType(String type) {
      this.type = type;
    }

    public String getUrl() {
      return url;
    }

    public void setUrl(String url) {
      this.url = url;
    }

    public boolean isUsed() {
      return used;
    }

    public void setUsed(boolean used) {
      this.used = used;
    }

    public String getWho() {
      return who;
    }

    public void setWho(String who) {
      this.who = who;
    }

    public List<String> getImages() {
      return images;
    }

    public void setImages(List<String> images) {
      this.images = images;
    }
  }
}
