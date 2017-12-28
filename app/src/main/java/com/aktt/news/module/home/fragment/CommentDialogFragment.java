package com.aktt.news.module.home.fragment;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.aktt.news.util.IntentHelper;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.aktt.news.AppConstant;
import com.aktt.news.R;
import com.aktt.news.api.HomeService;
import com.aktt.news.base.BaseDialogFragment;
import com.aktt.news.data.CommentBean;
import com.aktt.news.data.NewsInfoWrapper;
import com.aktt.news.instance.AccountManager;
import com.aktt.news.module.video.adapter.CommentAdapter;
import com.aktt.news.net.callback.SimpleCallback;
import com.aktt.news.net.client.NetRequest;
import com.aktt.news.net.data.HttpResult;
import com.aktt.news.util.AnimHelper;
import com.aktt.news.util.CompatUtil;
import com.aktt.news.util.RefreshHelper;
import com.aktt.news.util.StringHelper;
import com.magicalxu.library.blankj.KeyboardUtils;
import com.magicalxu.library.blankj.ToastUtils;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import java.util.List;
import org.json.JSONObject;
import retrofit2.Call;

/**
 * Created by magical on 2017/8/18 .
 * 二级评论界面
 * 评论：ok
 * 点赞：ok
 */

public class CommentDialogFragment extends BaseDialogFragment {

    @BindView(R.id.id_recycler) RecyclerView mRecyclerView;
    @BindView(R.id.id_refresh_layout) SmartRefreshLayout mRefreshLayout;
    @BindView(R.id.tv_title) TextView mTvTitle;
    @BindView(R.id.iv_title_left) View mIvBack;
    @BindView(R.id.id_edit) EditText mInputView;
    @BindView(R.id.id_send) TextView mSendBtn;

    private CommentAdapter mAdapter;
    private RefreshHelper<CommentBean> mRefreshHelper;
    private HomeService homeService;

    private String PAGE_NEWS_ID;
    private int PAGE_NEWS_TYPE;
    private String PAGE_CID;

    private View mEmptyView;

    public static CommentDialogFragment newInstance(@NonNull String newsId, int newsType,
            String cid) {
        CommentDialogFragment dialogFragment = new CommentDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putString(AppConstant.PARAMS_NEWS_ID, newsId);
        bundle.putInt(AppConstant.PARAMS_NEWS_TYPE, newsType);
        bundle.putString(AppConstant.PARAMS_COMMENT_ID, cid);
        dialogFragment.setArguments(bundle);
        return dialogFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Black_NoTitleBar);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        Dialog dialog = getDialog();
        if (dialog != null && dialog.getWindow() != null) {
            //添加动画
            dialog.getWindow().setWindowAnimations(R.style.dialogSlideAnim);
        }
        return inflater.inflate(R.layout.fragment_comment_list, null);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        processLogic();
    }

    private void processLogic() {

        Bundle arguments = getArguments();
        PAGE_NEWS_ID = arguments.getString(AppConstant.PARAMS_NEWS_ID);
        PAGE_NEWS_TYPE = arguments.getInt(AppConstant.PARAMS_NEWS_TYPE);
        PAGE_CID = arguments.getString(AppConstant.PARAMS_COMMENT_ID);

        mTvTitle.setText("楼层回复");
        mTvTitle.setTextColor(CompatUtil.getColor(getActivity(), R.color.one_text_color));
        mIvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                KeyboardUtils.hideSoftInput(mInputView);
                dismiss();
            }
        });

        homeService = NetRequest.getInstance().create(HomeService.class);

        mAdapter = new CommentAdapter(true, R.layout.item_video_comment);
        mRefreshHelper = new RefreshHelper<CommentBean>(mRefreshLayout, mAdapter, mRecyclerView) {
            @Override
            public void onRequest(int page) {
                loadComment(page);
            }
        };

        mAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                switch (view.getId()) {
                    case R.id.tv_up:
                        onPraise(position);
                        break;
                    case R.id.iv_avatar:
                        CommentBean commentBean = mAdapter.getData().get(position);
                        if (TextUtils.isEmpty(commentBean.nickname)) {
                            return;
                        }
                        IntentHelper.openUserCenterPage(getActivity(), commentBean.userid);
                        break;
                }
            }
        });

        mEmptyView = LayoutInflater.from(getActivity())
                .inflate(R.layout.base_empty_view, (ViewGroup) mRecyclerView.getParent(), false);
    }

    private void onPraise(final int position) {

        AnimHelper.showAnim(mAdapter.getViewByPosition(mRecyclerView, position, R.id.tv_up));

        int type;
        final boolean hasPraise;
        String cid;
        //操作 评论列表

        type = 11;
        CommentBean commentBean = mAdapter.getData().get(position);
        cid = commentBean.cid;
        //hasPraise = TextUtils.equals(AppConstant.HAS_PRAISE, commentBean.isFollow);
        hasPraise = false;

        int subType = hasPraise ? 2 : 1;

        String userId = AccountManager.getInstance().getUserId();
        showLoading();
        Call<HttpResult<JSONObject>> call =
                homeService.optLike(StringHelper.getString(userId), null, type, cid, subType);
        call.enqueue(new SimpleCallback<JSONObject>() {
            @Override
            public void onSuccess(JSONObject data) {

                hideLoading();

                List<CommentBean> listData = mAdapter.getData();
                if (listData.size() > position) {
                    CommentBean commentBean = listData.get(position);
                    if (hasPraise) {
                        //取消点赞成功
                        //ToastUtils.showShort("取消点赞成功");
                        //commentBean.isFollow = AppConstant.UN_PRAISE;
                        //commentBean.careCount--;
                        //mAdapter.notifyDataSetChanged();
                    } else {
                        //点赞成功
                        ToastUtils.showShort("点赞成功");
                        commentBean.isFollow = AppConstant.HAS_PRAISE;
                        ++commentBean.careCount;
                        mAdapter.notifyItemChanged(position + mAdapter.getHeaderLayoutCount());
                    }
                }
            }

            @Override
            public void onFail(String code, String errorMsg) {
                onSimpleError(errorMsg);
            }
        });
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mRefreshHelper.beginLoadData();
    }

    private void loadComment(final int page) {

        String userId = AccountManager.getInstance().getUserId();

        showLoading();
        Call<HttpResult<NewsInfoWrapper>> call =
                homeService.getNewsComment(StringHelper.getString(userId), PAGE_NEWS_ID,
                        PAGE_NEWS_TYPE, PAGE_CID, page, AppConstant.DEFAULT_PAGE_SIZE);
        call.enqueue(new SimpleCallback<NewsInfoWrapper>() {
            @Override
            public void onSuccess(NewsInfoWrapper data) {

                hideLoading();
                if (page == 1 && (null == data
                        || null == data.commentlist
                        || data.commentlist.size() == 0)) {
                    mAdapter.setEmptyView(mEmptyView);
                    return;
                }

                mRefreshHelper.setData(data.commentlist, data.haveNext);
            }

            @Override
            public void onFail(String code, String errorMsg) {

                mRefreshHelper.onFail();
                onSimpleError(errorMsg);
            }
        });
    }

    @OnClick(R.id.id_send)
    public void sendComment() {

        if (isLoading()) {
            return;
        }

        String comment = mInputView.getText().toString().trim();
        if (TextUtils.isEmpty(comment)) {
            ToastUtils.showShort(R.string.comment_can_not_be_null);
            return;
        }

        String userId = AccountManager.getInstance().getUserId();

        KeyboardUtils.hideSoftInput(getActivity());
        showLoading();
        Call<HttpResult<JSONObject>> call =
                homeService.postNewsComment(StringHelper.getString(userId), PAGE_NEWS_ID,
                        PAGE_NEWS_TYPE, PAGE_CID, comment);
        call.enqueue(new SimpleCallback<JSONObject>() {

            @Override
            public void onSuccess(JSONObject data) {
                onCommentSuccess();
            }

            @Override
            public void onFail(String code, String errorMsg) {
                onSimpleError(errorMsg);
            }
        });
    }

    private void onCommentSuccess() {
        hideLoading();
        mInputView.setText("");
        loadComment(1);
    }
}
