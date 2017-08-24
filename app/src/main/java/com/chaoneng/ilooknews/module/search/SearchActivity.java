package com.chaoneng.ilooknews.module.search;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.OnClick;
import com.chaoneng.ilooknews.R;
import com.chaoneng.ilooknews.base.BaseActivity;
import com.chaoneng.ilooknews.module.user.widget.SettingItemView;
import com.chaoneng.ilooknews.util.CompatUtil;
import com.chaoneng.ilooknews.widget.adapter.AbsTextWatcher;
import com.magicalxu.library.blankj.ToastUtils;

/**
 * Created by magical on 2017/8/23.
 * Description : 搜索界面
 */

public class SearchActivity extends BaseActivity {

  @BindView(R.id.et_key) EditText etKey;
  @BindView(R.id.btn_search) TextView btnSearch;
  @BindView(R.id.id_history) SettingItemView mHistory;
  @BindView(R.id.id_recommend) SettingItemView mRecommend;

  @OnClick({ R.id.iv_back, R.id.btn_search })
  public void onViewClicked(View view) {
    switch (view.getId()) {
      case R.id.iv_back:
        finish();
        break;
      case R.id.btn_search:
        if (btnSearch.isClickable()) {
          ToastUtils.showShort("搜索");
        }
        break;
    }
  }

  @Override
  public int getLayoutId() {
    return R.layout.activity_search;
  }

  @Override
  public void handleChildPage(Bundle savedInstanceState) {

    btnSearch.setClickable(false);
    btnSearch.setTextColor(getResources().getColor(R.color.three_text_color));

    etKey.addTextChangedListener(new AbsTextWatcher() {
      @Override
      public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        super.onTextChanged(charSequence, i, i1, i2);

        boolean access = !TextUtils.isEmpty(charSequence);

        btnSearch.setClickable(access);
        btnSearch.setTextColor(CompatUtil.getColor(SearchActivity.this,
            access ? R.color.main_color : R.color.three_text_color));
      }
    });

    mHistory.setTitle("历史记录")
        .setTitleColor(R.color.three_text_color)
        .setRightDrawable(R.drawable.ic_delete_history);
    mRecommend.setTitle("猜你想搜的")
        .setTitleColor(R.color.three_text_color)
        .setRightDrawable(R.drawable.ic_delete_history);
  }
}
