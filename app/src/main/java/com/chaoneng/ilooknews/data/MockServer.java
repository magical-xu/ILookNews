package com.chaoneng.ilooknews.data;

import com.chaoneng.ilooknews.AppConstant;
import com.chaoneng.ilooknews.api.Constant;
import com.chaoneng.ilooknews.api.GankModel;
import com.chaoneng.ilooknews.api.GankService;
import com.chaoneng.ilooknews.module.focus.data.FocusBean;
import com.chaoneng.ilooknews.module.home.data.NewsListBean;
import com.chaoneng.ilooknews.module.user.data.BrokeNewsBean;
import com.chaoneng.ilooknews.module.user.data.UserStateBean;
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
        return new MockServer();
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

        mRefreshHelper.setData(getFakeData(type),true);
    }

    public interface Type {
        int NOTIFY = 1;
        int FOCUS = 2;
        int VIDEO_LIST = 3;
        int VIDEO_COMMENT = 4;
        int USER_STATE = 5;
        int USER_BROKE = 6;
    }

    public void mockGankCall(int page, final int type) {
        GankService service = NetRequest.getInstance().create(GankService.class);
        Call<GankModel> call = service.getData(Constant.BASE_URL + page);

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
            case Type.VIDEO_LIST:
                return (List<T>) getVideoList();
            case Type.VIDEO_COMMENT:
                return (List<T>) getVideoComment();
            case Type.USER_STATE:
                return (List<T>) getUserStateList();
            case Type.USER_BROKE:
                return (List<T>) getBrokeNewsList();
        }
        return null;
    }

    private List<BaseUser> getUserData() {
        List<BaseUser> list = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            BaseUser user = new BaseUser();
            user.icon = AppConstant.TEST_AVATAR;
            user.username = "系统消息";
            user.introduce = "Shaun 关注了 你 -- > " + String.valueOf(mRefreshHelper.getCurPage());
            list.add(user);
        }
        return list;
    }

    private List<FocusBean> getFocusBean() {
        List<FocusBean> list = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            FocusBean user = new FocusBean();
            list.add(user);
        }
        return list;
    }

    private List<NewsListBean> getVideoList() {
        List<NewsListBean> list = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            NewsListBean user = new NewsListBean();
            user.newId = String.valueOf(i);
            user.videoUrl = AppConstant.TEST_VIDEO_URL;
            list.add(user);
        }
        return list;
    }

    private List<CommentBean> getVideoComment() {
        List<CommentBean> list = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            CommentBean user = new CommentBean();
            list.add(user);
        }
        return list;
    }

    private List<UserStateBean> getUserStateList() {
        List<UserStateBean> list = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            UserStateBean user = new UserStateBean();
            list.add(user);
        }
        return list;
    }

    private List<BrokeNewsBean> getBrokeNewsList() {
        List<BrokeNewsBean> list = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            BrokeNewsBean user = new BrokeNewsBean();
            list.add(user);
        }
        return list;
    }
}
