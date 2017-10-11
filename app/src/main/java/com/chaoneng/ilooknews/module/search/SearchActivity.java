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
import com.magicalxu.library.blankj.SPUtils;
import com.magicalxu.library.blankj.SizeUtils;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.qmuiteam.qmui.widget.dialog.QMUIDialogAction;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import retrofit2.Call;

import static com.chaoneng.ilooknews.AppConstant.SEARCH_HISTORY;

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
    private ArrayList<String> historyList;
    private QMUIDialog mClearDialog;

    @OnClick({ R.id.iv_back, R.id.btn_search })
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.btn_search:
                if (btnSearch.isClickable()) {
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

        saveSearchHistory(keyword);

        IntentHelper.openSearchDetailPage(this, keyword);
    }

    private void handleSearch(String keyword) {

        saveSearchHistory(keyword);
        IntentHelper.openSearchDetailPage(this, keyword);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_search;
    }

    @Override
    public void handleChildPage(Bundle savedInstanceState) {

        initHistorySearch();

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

        //loadHistory();
        loadRecommend();
    }

    private void initHistorySearch() {
        String longHistory = SPUtils.getInstance().getString(AppConstant.SEARCH_HISTORY, "");
        String[] tmpHistory = longHistory.split(",");                            //split后长度为1有一个空串对象
        historyList = new ArrayList<>(Arrays.asList(tmpHistory));

        if (historyList.size() == 1 && historyList.get(0)
                .equals("")) {          //如果没有搜索记录，split之后第0位是个空串的情况下
            historyList.clear();        //清空集合，这个很关键
        }

        if (historyList.size() > 0) {
            mHistory.setVisibility(View.VISIBLE);
        } else {
            mHistory.setVisibility(View.GONE);
        }

        addHistoryView(historyList);
    }

    /**
     * 删除搜索历史
     */
    private void onDelHistory() {

        if (null == mClearDialog) {
            mClearDialog = new QMUIDialog.MessageDialogBuilder(this).setTitle("提示")
                    .setMessage("确定删除搜索历史吗")
                    .addAction("取消", new QMUIDialogAction.ActionListener() {
                        @Override
                        public void onClick(QMUIDialog dialog, int index) {
                            dialog.dismiss();
                        }
                    })
                    .addAction("确定", new QMUIDialogAction.ActionListener() {
                        @Override
                        public void onClick(QMUIDialog dialog, int index) {
                            dialog.dismiss();
                            historyList.clear();                            //清空集合
                            SPUtils.getInstance().remove(SEARCH_HISTORY);   //清空sp
                            mHistoryContainer.removeAllViews();
                            mHistory.setVisibility(View.GONE);
                        }
                    })
                    .create();
        }
        mClearDialog.show();
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

    @Deprecated
    private void loadHistory() {

        Call<HttpResult<SearchHistory>> searchHistory =
                service.getSearchHistory(AppConstant.TEST_USER_ID);
        searchHistory.enqueue(new SimpleCallback<SearchHistory>() {
            @Override
            public void onSuccess(SearchHistory data) {

                List<SearchHistory.ListBean> list = data.list;
                //addHistoryView(list);
            }

            @Override
            public void onFail(String code, String errorMsg) {

            }
        });
    }

    /**
     * 将搜索历史添加到 FlexboxLayout中
     */
    private void addHistoryView(List<String> list) {

        LayoutInflater inflater = getLayoutInflater();
        for (int i = 0; i < list.size(); i++) {
            final String keyword = list.get(i);
            ViewGroup view = (ViewGroup) inflater.inflate(R.layout.include_search_item_view, null);

            //TextView textView = new TextView(this);
            //textView.setTextColor(CompatUtil.getColor(this, R.color.one_text_color));
            TextView textView = (TextView) view.getChildAt(0);
            textView.setText(keyword);
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    handleSearch(keyword);
                }
            });
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
            final SearchRecommend.ListBean listBean = list.get(i);
            ViewGroup view = (ViewGroup) inflater.inflate(R.layout.include_search_item_view, null);

            //TextView textView = new TextView(this);
            //textView.setTextColor(CompatUtil.getColor(this, R.color.one_text_color));
            TextView textView = (TextView) view.getChildAt(0);
            textView.setText(listBean.keyText);
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    handleSearch(listBean.keyText);
                }
            });
            //textView.setPadding(SizeUtils.dp2px(8), 0, 0, 0);
            //textView.setGravity(Gravity.CENTER_VERTICAL);

            FlexboxLayout.LayoutParams params = new FlexboxLayout.LayoutParams(SizeUtils.dp2px(42),
                    ViewGroup.LayoutParams.MATCH_PARENT);
            params.setFlexBasisPercent(0.49f);
            mRecommendContainer.addView(view, params);
        }
    }

    /**
     * 保存搜索记录
     *
     * @param inputText 输入的历史记录
     */
    private void saveSearchHistory(String inputText) {

        if (TextUtils.isEmpty(inputText)) {
            return;
        }

        String longHistory = SPUtils.getInstance()
                .getString(AppConstant.SEARCH_HISTORY, "");        //获取之前保存的历史记录

        String[] tmpHistory = longHistory.split(",");              //逗号截取 保存在数组中

        historyList = new ArrayList<>(Arrays.asList(tmpHistory));          //将改数组转换成ArrayList

        if (historyList.size() > 0) {
            //移除之前重复添加的元素
            for (int i = 0; i < historyList.size(); i++) {
                if (inputText.equals(historyList.get(i))) {
                    historyList.remove(i);
                    break;
                }
            }

            historyList.add(0, inputText);                           //将新输入的文字添加集合的第0位也就是最前面

            if (historyList.size() > 6) {
                historyList.remove(historyList.size() - 1);         //最多保存6条搜索记录 删除最早搜索的那一项
            }

            //逗号拼接
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < historyList.size(); i++) {
                sb.append(historyList.get(i)).append(",");
            }
            //保存到sp
            SPUtils.getInstance().put(AppConstant.SEARCH_HISTORY, sb.toString());
        } else {
            //之前未添加过
            SPUtils.getInstance().put(AppConstant.SEARCH_HISTORY, inputText + ",");
        }
    }
}
