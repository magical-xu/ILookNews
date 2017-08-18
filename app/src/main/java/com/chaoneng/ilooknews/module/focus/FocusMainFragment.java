package com.chaoneng.ilooknews.module.focus;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import butterknife.BindView;
import com.chaoneng.ilooknews.AppConstant;
import com.chaoneng.ilooknews.R;
import com.chaoneng.ilooknews.api.Constant;
import com.chaoneng.ilooknews.api.GankModel;
import com.chaoneng.ilooknews.api.GankService;
import com.chaoneng.ilooknews.base.BaseTitleFragment;
import com.chaoneng.ilooknews.data.BaseUser;
import com.chaoneng.ilooknews.module.home.adapter.NotifyAdapter;
import com.chaoneng.ilooknews.net.callback.SimpleJsonCallback;
import com.chaoneng.ilooknews.net.client.NetRequest;
import com.chaoneng.ilooknews.util.DividerHelper;
import com.chaoneng.ilooknews.widget.ILookTitleBar;
import com.magicalxu.library.blankj.ToastUtils;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadmoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;

/**
 * Created by magical on 17/8/15.
 * Description : 主页 - 关注
 */

public class FocusMainFragment extends BaseTitleFragment {

  @BindView(R.id.id_recycler) RecyclerView mRecyclerView;
  @BindView(R.id.id_refresh_layout) SmartRefreshLayout mRefreshLayout;

  private NotifyAdapter mAdapter;

  @Override
  protected void beginLoadData() {

  }

  @Override
  protected boolean isNeedShowLoadingView() {
    return false;
  }

  @Override
  public void init() {

    mTitleBar.setTitleImage(R.drawable.img_focus_title)
        .setRightImage(R.drawable.ic_care_plus_blue)
        .hideLeftImage()
        .setTitleListener(new ILookTitleBar.TitleCallbackAdapter() {
          @Override
          public void onClickRightImage(View view) {
            super.onClickRightImage(view);
            ToastUtils.showShort("加關注");
          }
        });

    configRecyclerView();
    loadData();
  }

  @Override
  public int getSubLayout() {
    return R.layout.simple_recycler_list;
  }

  int curPage = 1;

  private void loadData() {

    GankService service = NetRequest.getInstance().create(GankService.class);
    Call<GankModel> call = service.getData(Constant.PAGE_LIMIT, String.valueOf(curPage));

    call.enqueue(new SimpleJsonCallback<GankModel>() {
      @Override
      public void onSuccess(GankModel data) {
        buildFakeData();
      }

      @Override
      public void onFailed(int code, String message) {
        if (curPage == 1) {
          mRefreshLayout.finishRefresh();
        } else {
          mRefreshLayout.finishLoadmore();
        }
      }
    });
  }

  private void buildFakeData() {

    if (curPage == 4) {
      //mRefreshLayout.finishLoadmore();
      mRefreshLayout.setLoadmoreFinished(true);
      curPage = 3;
      return;
    }

    List<BaseUser> list = new ArrayList<>();
    for (int i = 0; i < 10; i++) {
      BaseUser user = new BaseUser();
      user.avatar = AppConstant.TEST_AVATAR;
      user.nickname = "系统消息";
      user.sign = "Shaun 关注了 你 -- > " + String.valueOf(curPage);
      list.add(user);
    }

    if (curPage == 1) {
      mAdapter.setNewData(list);
      mRefreshLayout.finishRefresh();
    } else {
      mAdapter.addData(list);
      mRefreshLayout.finishLoadmore();
    }
  }

  private void configRecyclerView() {

    LinearLayoutManager manager =
        new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
    mRecyclerView.setLayoutManager(manager);
    mRecyclerView.addItemDecoration(DividerHelper.drawVerticalLine(mContext, 0, false, false));
    mRecyclerView.setHasFixedSize(true);

    mRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
      @Override
      public void onRefresh(RefreshLayout refreshlayout) {

        curPage = 1;
        loadData();
      }
    }).setOnLoadmoreListener(new OnLoadmoreListener() {
      @Override
      public void onLoadmore(RefreshLayout refreshlayout) {
        curPage++;
        loadData();
      }
    });

    mAdapter = new NotifyAdapter(R.layout.item_home_notify);
    mRecyclerView.setAdapter(mAdapter);
  }
}
