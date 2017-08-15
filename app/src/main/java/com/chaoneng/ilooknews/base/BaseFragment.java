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

  private static final String PARAM_BASEFRAGMENT_STATE = "basefragment_state";

  protected View mRootView;
  protected Context mContext;

  protected Bundle mBundle;
  public Unbinder unbinder;

  @Override
  public void onAttach(Context context) {
    super.onAttach(context);
    mContext = context;
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    if (savedInstanceState != null) {
      mBundle = savedInstanceState.getBundle(PARAM_BASEFRAGMENT_STATE);
    }
    if (mBundle == null) {
      mBundle = getArguments();
    }
  }

  @Override
  public void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    if (mBundle != null) {
      outState.putBundle(PARAM_BASEFRAGMENT_STATE, mBundle);
    }
  }

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    if (0 != getLayoutName()) {

      if (isNeedShowLoadingView()) {
        //需要加载动画
        //mRootView = inflateWitchBlankLoading(inflater, getLayoutName());
      } else {
        //不需要加载动画
        mRootView = inflater.inflate(getLayoutName(), container, false);
      }
      unbinder = ButterKnife.bind(this, mRootView);
      return mRootView;
    }
    return super.onCreateView(inflater, container, savedInstanceState);
  }

  //protected abstract View inflateWitchBlankLoading(LayoutInflater inflater, int layoutName);

  @Override
  public void onActivityCreated(@Nullable Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);

    doInit();

    beginLoadData();
  }

  protected abstract void beginLoadData();

  protected abstract void doInit();

  /**
   * 是否开启加载动画
   * 默认不开启
   * 如果需要加载动画 可以在子类重写此方法 并返回 true
   */
  protected abstract boolean isNeedShowLoadingView();

  /**
   * 获取layout的名字
   *
   * @return String
   */
  protected abstract int getLayoutName();

  @Override
  public void onDestroyView() {
    super.onDestroyView();
    unbinder.unbind();
  }
}
