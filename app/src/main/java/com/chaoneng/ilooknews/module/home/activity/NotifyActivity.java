package com.chaoneng.ilooknews.module.home.activity;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import butterknife.BindView;
import com.chaoneng.ilooknews.AppConstant;
import com.chaoneng.ilooknews.R;
import com.chaoneng.ilooknews.api.Constant;
import com.chaoneng.ilooknews.api.GankService;
import com.chaoneng.ilooknews.base.BaseActivity;
import com.chaoneng.ilooknews.data.BaseUser;
import com.chaoneng.ilooknews.module.home.adapter.NotifyAdapter;
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
import retrofit2.Callback;
import retrofit2.Response;

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

  int page = 1;

  private void loadData() {

    GankService service = NetRequest.getInstance().create(GankService.class);
    Call<BaseUser> call = service.getData(Constant.PAGE_LIMIT, String.valueOf(page));
    call.enqueue(new Callback<BaseUser>() {
      @Override
      public void onResponse(Call<BaseUser> call, Response<BaseUser> response) {
        List<BaseUser> list = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
          BaseUser user = new BaseUser();
          user.avatar = AppConstant.TEST_AVATAR;
          user.nickname = "系统消息";
          user.sign = "Shaun 关注了 你";
          list.add(user);
        }
        mAdapter.setNewData(list);
      }

      @Override
      public void onFailure(Call<BaseUser> call, Throwable t) {
        ToastUtils.showShort(t.getMessage());
      }
    });
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
        mRefreshLayout.finishRefresh(3000);
      }
    }).setOnLoadmoreListener(new OnLoadmoreListener() {
      @Override
      public void onLoadmore(RefreshLayout refreshlayout) {
        mRefreshLayout.finishLoadmore(3000);
      }
    });

    mAdapter = new NotifyAdapter(R.layout.item_home_notify);
    mRecyclerView.setAdapter(mAdapter);
  }
}
