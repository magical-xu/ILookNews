package com.chaoneng.ilooknews.module.home.activity;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import butterknife.BindView;
import com.chaoneng.ilooknews.AppConstant;
import com.chaoneng.ilooknews.R;
import com.chaoneng.ilooknews.api.Constant;
import com.chaoneng.ilooknews.api.GankModel;
import com.chaoneng.ilooknews.api.GankService;
import com.chaoneng.ilooknews.base.BaseActivity;
import com.chaoneng.ilooknews.data.BaseUser;
import com.chaoneng.ilooknews.module.home.adapter.NotifyAdapter;
import com.chaoneng.ilooknews.net.callback.SimpleJsonCallback;
import com.chaoneng.ilooknews.net.client.NetRequest;
import com.chaoneng.ilooknews.util.DividerHelper;
import com.chaoneng.ilooknews.widget.ILookTitleBar;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadmoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;

/**
 * Created by magical on 17/8/17.
 * Description : 我的消息界面
 */

public class NotifyActivity extends BaseActivity {

  @BindView(R.id.id_recycler) RecyclerView mRecyclerView;
  @BindView(R.id.id_refresh_layout) SmartRefreshLayout mRefreshLayout;

  private NotifyAdapter mAdapter;

  @Override
  public int getLayoutId() {
    return R.layout.activity_notify;
  }

  @Override
  protected boolean addTitleBar() {
    return true;
  }

  @Override
  public void handleChildPage(Bundle savedInstanceState) {

    configTitle();
    configRecyclerView();
    loadData();
  }

  private void configTitle() {

    mTitleBar.setTitle("我的消息");
    mTitleBar.setTitleListener(new ILookTitleBar.TitleCallbackAdapter() {
      @Override
      public void onClickLeft(View view) {
        super.onClickLeft(view);
        finish();
      }
    });
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
      mRefreshLayout.finishLoadmore();
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
        new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
    mRecyclerView.setLayoutManager(manager);
    mRecyclerView.addItemDecoration(DividerHelper.drawVerticalLine(this, 0, false, false));
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
