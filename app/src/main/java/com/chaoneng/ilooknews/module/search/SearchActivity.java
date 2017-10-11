package com.chaoneng.ilooknews.module.search;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.OnClick;
import com.chaoneng.ilooknews.AppConstant;
import com.chaoneng.ilooknews.R;
import com.chaoneng.ilooknews.api.SearchService;
import com.chaoneng.ilooknews.base.BaseActivity;
import com.chaoneng.ilooknews.module.search.data.SearchHistory;
import com.chaoneng.ilooknews.module.search.data.SearchRecommend;
import com.chaoneng.ilooknews.module.user.widget.SettingItemView;
import com.chaoneng.ilooknews.net.callback.SimpleCallback;
import com.chaoneng.ilooknews.net.client.NetRequest;
import com.chaoneng.ilooknews.net.data.HttpResult;
import com.chaoneng.ilooknews.util.CompatUtil;
import com.chaoneng.ilooknews.util.IntentHelper;
import com.chaoneng.ilooknews.widget.adapter.AbsTextWatcher;
import com.google.android.flexbox.FlexboxLayout;
import com.magicalxu.library.blankj.SizeUtils;
import com.magicalxu.library.blankj.ToastUtils;
import java.util.List;
import retrofit2.Call;

/**
 * Created by magical on 2017/8/23.
 * Description : 搜索界面
 */

public class SearchActivity extends BaseActivity {

    @BindView(R.id.et_key) EditText etKey;
    @BindView(R.id.btn_search) TextView btnSearch;
    @BindView(R.id.id_history) SettingItemView mHistory;
    @BindView(R.id.id_recommend) SettingItemView mRecommend;
    @BindView(R.id.id_history_container) FlexboxLayout mHistoryContainer;
    @BindView(R.id.id_recommend_container) FlexboxLayout mRecommendContainer;

    private SearchService service;

    @OnClick({ R.id.iv_back, R.id.btn_search })
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.btn_search:
                if (btnSearch.isClickable()) {
                    ToastUtils.showShort("搜索");
                    handleSearch();
                }
                break;
        }
    }

    private void handleSearch() {

        String keyword = etKey.getText().toString().trim();
        if (TextUtils.isEmpty(keyword)) {
            return;
        }

        IntentHelper.openSearchDetailPage(this, keyword);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_search;
    }

    @Override
    public void handleChildPage(Bundle savedInstanceState) {

        service = NetRequest.getInstance().create(SearchService.class);
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
                .setRightDrawable(R.drawable.ic_delete_history)
                .setRightDrawableClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        onDelHistory();
                    }
                });
        mRecommend.setTitle("猜你想搜的").setTitleColor(R.color.three_text_color)
                //.setRightDrawable(R.drawable.ic_delete_history)
                .hideRightArrow();

        loadHistory();
        loadRecommend();
    }

    /**
     * 删除搜索历史
     */
    private void onDelHistory() {

    }

    private void loadRecommend() {

        Call<HttpResult<SearchRecommend>> call = service.getSearchRecommend();
        call.enqueue(new SimpleCallback<SearchRecommend>() {
            @Override
            public void onSuccess(SearchRecommend data) {

                if (null != data) {
                    List<SearchRecommend.ListBean> list = data.list;
                    addRecommendView(list);
                }
            }

            @Override
            public void onFail(String code, String errorMsg) {
                onSimpleError(errorMsg);
            }
        });
    }

    private void loadHistory() {

        Call<HttpResult<SearchHistory>> searchHistory =
                service.getSearchHistory(AppConstant.TEST_USER_ID);
        searchHistory.enqueue(new SimpleCallback<SearchHistory>() {
            @Override
            public void onSuccess(SearchHistory data) {

                List<SearchHistory.ListBean> list = data.list;
                addHistoryView(list);
            }

            @Override
            public void onFail(String code, String errorMsg) {

            }
        });
    }

    /**
     * 将搜索历史添加到 FlexboxLayout中
     */
    private void addHistoryView(List<SearchHistory.ListBean> list) {

        LayoutInflater inflater = getLayoutInflater();
        for (int i = 0; i < list.size(); i++) {
            SearchHistory.ListBean listBean = list.get(i);
            ViewGroup view = (ViewGroup) inflater.inflate(R.layout.include_search_item_view, null);

            //TextView textView = new TextView(this);
            //textView.setTextColor(CompatUtil.getColor(this, R.color.one_text_color));
            TextView textView = (TextView) view.getChildAt(0);
            textView.setText(listBean.keyText);
            //textView.setPadding(SizeUtils.dp2px(8), 0, 0, 0);
            //textView.setGravity(Gravity.CENTER_VERTICAL);

            FlexboxLayout.LayoutParams params = new FlexboxLayout.LayoutParams(SizeUtils.dp2px(42),
                    ViewGroup.LayoutParams.MATCH_PARENT);
            params.setFlexBasisPercent(0.49f);
            mHistoryContainer.addView(view, params);
        }
    }

    /**
     * 将搜索推荐添加到 FlexboxLayout中
     */
    private void addRecommendView(List<SearchRecommend.ListBean> list) {

        LayoutInflater inflater = getLayoutInflater();
        for (int i = 0; i < list.size(); i++) {
            SearchRecommend.ListBean listBean = list.get(i);
            ViewGroup view = (ViewGroup) inflater.inflate(R.layout.include_search_item_view, null);

            //TextView textView = new TextView(this);
            //textView.setTextColor(CompatUtil.getColor(this, R.color.one_text_color));
            TextView textView = (TextView) view.getChildAt(0);
            textView.setText(listBean.keyText);
            //textView.setPadding(SizeUtils.dp2px(8), 0, 0, 0);
            //textView.setGravity(Gravity.CENTER_VERTICAL);

            FlexboxLayout.LayoutParams params = new FlexboxLayout.LayoutParams(SizeUtils.dp2px(42),
                    ViewGroup.LayoutParams.MATCH_PARENT);
            params.setFlexBasisPercent(0.49f);
            mRecommendContainer.addView(view, params);
        }
    }
}
