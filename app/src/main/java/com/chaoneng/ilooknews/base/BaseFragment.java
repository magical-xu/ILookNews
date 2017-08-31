package com.chaoneng.ilooknews.base;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by magical on 2017/3/17.
 */

public abstract class BaseFragment extends Fragment {

    private boolean isFragmentVisible;

    private boolean isPrepare;

    private boolean isFirstLoad = true;

    protected View mRootView;
    protected Context mContext;

    public Unbinder unbinder;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (getUserVisibleHint()) {
            onVisible();
        } else {
            onInvisible();
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (hidden) {
            onInvisible();
        } else {
            onVisible();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        if (0 != getLayoutName()) {

            mRootView = inflater.inflate(getLayoutName(), container, false);
            isPrepare = true;

            if (bind()) {
                unbinder = ButterKnife.bind(this, mRootView);
            }

            return mRootView;
        }
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        doInit();
        // TODO: 2017/9/1 这里手动调了一次 解决了 ViewPager 第一屏 因为回调 lazyLoad 时 isPrepared 标记还未变更导致的不加载问题
        initData();
        beginLoadData();
    }

    public boolean bind() {
        return true;
    }

    protected void beginLoadData() {

    }

    protected void doInit() {
    }

    protected void onVisible() {
        isFragmentVisible = true;
        initData();
    }

    protected void onInvisible() {
        isFragmentVisible = false;
    }

    protected void initData() {

        // 视图未销毁 且 可见
        if (isPrepare && isFragmentVisible) {
            if (isFirstLoad) {
                isFirstLoad = false;
                lazyLoad();
            }
        }
    }

    /**
     * 需要懒加载的界面 实现此方法
     * 且只会在首次加载数据时回调
     */
    protected void lazyLoad() {

    }

    protected abstract int getLayoutName();

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        isPrepare = false;
        isFirstLoad = true;
        if (null != unbinder) {
            unbinder.unbind();
        }
    }
}
