package com.chaoneng.ilooknews.data;

import com.chaoneng.ilooknews.AppConstant;
import com.chaoneng.ilooknews.api.Constant;
import com.chaoneng.ilooknews.api.GankModel;
import com.chaoneng.ilooknews.api.GankService;
import com.chaoneng.ilooknews.module.focus.data.FocusBean;
import com.chaoneng.ilooknews.net.callback.SimpleJsonCallback;
import com.chaoneng.ilooknews.net.client.NetRequest;
import com.chaoneng.ilooknews.util.RefreshHelper;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;

/**
 * Created by magical on 17/8/19.
 * Description :
 */

public class MockServer<T> {

  public RefreshHelper mRefreshHelper;

  private MockServer() {
  }

  public static MockServer getInstance() {
    return MockServerHolder.instance;
  }

  private static class MockServerHolder {
    private static final MockServer instance = new MockServer();
  }

  public void init(RefreshHelper helper) {
    this.mRefreshHelper = helper;
  }

  public void ensureHelper() {
    if (null == mRefreshHelper) {
      throw new NullPointerException("MockServer: init method must be called first");
    }
  }

  public void buildFakeData(int type) {

    ensureHelper();
    if (mRefreshHelper.mockNoMoreData()) {
      return;
    }

    mRefreshHelper.setData(getFakeData(type));
  }

  public interface Type {
    int NOTIFY = 1;
    int FOCUS = 2;
  }

  public void mockGankCall(int page, final int type) {
    GankService service = NetRequest.getInstance().create(GankService.class);
    Call<GankModel> call = service.getData(Constant.PAGE_LIMIT, String.valueOf(page));

    call.enqueue(new SimpleJsonCallback<GankModel>() {
      @Override
      public void onSuccess(GankModel data) {
        buildFakeData(type);
      }

      @Override
      public void onFailed(int code, String message) {
        mRefreshHelper.onFail();
      }
    });
  }

  public List<T> getFakeData(int type) {

    switch (type) {
      case Type.NOTIFY:
        return (List<T>) getUserData();
      case Type.FOCUS:
        return (List<T>) getFocusBean();
    }
    return null;
  }

  public List<BaseUser> getUserData() {
    List<BaseUser> list = new ArrayList<>();
    for (int i = 0; i < 10; i++) {
      BaseUser user = new BaseUser();
      user.avatar = AppConstant.TEST_AVATAR;
      user.nickname = "系统消息";
      user.sign = "Shaun 关注了 你 -- > " + String.valueOf(mRefreshHelper.getCurPage());
      list.add(user);
    }
    return list;
  }

  public List<FocusBean> getFocusBean() {
    List<FocusBean> list = new ArrayList<>();
    for (int i = 0; i < 10; i++) {
      FocusBean user = new FocusBean();
      user.avatar = AppConstant.TEST_AVATAR;
      user.name = "超能工作室";
      user.intro = "简介：我们不能改变潮水的方向，但总要试试。。。。。。。";
      user.time = "四天之前";
      user.content = "不论何时，做你认为最重要的事情";
      list.add(user);
    }
    return list;
  }
}
