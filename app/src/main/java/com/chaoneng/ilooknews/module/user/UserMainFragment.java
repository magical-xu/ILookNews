package com.chaoneng.ilooknews.module.user;

import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.OnClick;
import com.chaoneng.ilooknews.AppConstant;
import com.chaoneng.ilooknews.R;
import com.chaoneng.ilooknews.api.Constant;
import com.chaoneng.ilooknews.api.GankModel;
import com.chaoneng.ilooknews.api.GankService;
import com.chaoneng.ilooknews.base.BaseFragment;
import com.chaoneng.ilooknews.module.user.widget.RelationView;
import com.chaoneng.ilooknews.net.callback.SimpleJsonCallback;
import com.chaoneng.ilooknews.net.client.NetRequest;
import com.chaoneng.ilooknews.util.IntentHelper;
import com.chaoneng.ilooknews.widget.image.HeadImageView;
import com.magicalxu.library.blankj.ToastUtils;
import retrofit2.Call;

/**
 * Created by magical on 17/8/15.
 * Description : 主页 - 个人
 */

public class UserMainFragment extends BaseFragment {

  @BindView(R.id.iv_avatar) HeadImageView mHeadIv;
  @BindView(R.id.id_nickname) TextView mTvNick;
  @BindView(R.id.id_sign) TextView mTvSign;
  @BindView(R.id.credit_topic) RelationView mTvTopic;
  @BindView(R.id.credit_following) RelationView mTvFollow;
  @BindView(R.id.credit_fans) RelationView mTvFans;
  @BindView(R.id.credit_visitors) RelationView mTvVisit;
  @BindView(R.id.id_item_msg) RelativeLayout mItemMsg;
  @BindView(R.id.id_item_collect) RelativeLayout mItemCollect;
  @BindView(R.id.id_item_broke) RelativeLayout mItemBroke;
  @BindView(R.id.id_item_feedback) RelativeLayout mItemFeed;
  @BindView(R.id.id_item_setting) RelativeLayout mItemSetting;

  @Override
  protected void beginLoadData() {

    GankService service = NetRequest.getInstance().create(GankService.class);
    Call<GankModel> call = service.getData(Constant.BASE_URL + "1");

    call.enqueue(new SimpleJsonCallback<GankModel>() {
      @Override
      public void onSuccess(GankModel data) {
        mHeadIv.setHeadImage(AppConstant.TEST_AVATAR);
        mTvNick.setText("magical");
        mTvSign.setText("Life is short , just do IT !");
      }

      @Override
      public void onFailed(int code, String message) {
      }
    });
  }

  @Override
  protected void doInit() {

    mTvTopic.setBottom("动态").setCount("0").hideRedPoint();

    mTvFollow.setBottom("关注").setCount("100").hideRedPoint();

    mTvFans.setBottom("粉丝").setCount("9984").setRedPoint("45");

    mTvVisit.setBottom("访客").setCount("0").hideRedPoint();

    mTvTopic.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        ToastUtils.showShort("动态");
      }
    });
  }

  @Override
  protected int getLayoutName() {
    return R.layout.layout_main_user_fg;
  }

  @OnClick({
      R.id.id_item_msg, R.id.id_item_collect, R.id.id_item_broke, R.id.id_item_feedback,
      R.id.id_item_setting, R.id.credit_topic, R.id.credit_following, R.id.credit_fans,
      R.id.credit_visitors
  })
  public void onItemClick(View view) {

    switch (view.getId()) {
      case R.id.id_item_msg:
        IntentHelper.openNotifyPage(getActivity());
        break;
      case R.id.id_item_collect:
        ToastUtils.showShort("我的收藏");
        break;
      case R.id.id_item_broke:
        IntentHelper.openBrokePage(getActivity());
        break;
      case R.id.id_item_feedback:
        IntentHelper.openFeedbackPage(getActivity());
        break;
      case R.id.id_item_setting:
        IntentHelper.openSettingPage(getActivity());
        break;
      case R.id.credit_topic:
        ToastUtils.showShort("动态");
        break;
      case R.id.credit_following:
        ToastUtils.showShort("关注");
        break;
      case R.id.credit_fans:
        ToastUtils.showShort("粉丝");
        break;
      case R.id.credit_visitors:
        ToastUtils.showShort("访客");
        break;
    }
  }
}
