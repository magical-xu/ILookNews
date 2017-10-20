package com.aktt.news.base;

import android.view.LayoutInflater;
import android.widget.FrameLayout;
import butterknife.ButterKnife;
import com.aktt.news.R;
import com.aktt.news.widget.ilook.ILookTitleBar;

/**
 * Created by magical on 17/8/18.
 * Description : 带标题的
 */

public abstract class BaseTitleFragment extends BaseFragment {

    protected FrameLayout mSubRootView;

    protected ILookTitleBar mTitleBar;

    @Override
    protected void doInit() {

        mSubRootView = mRootView.findViewById(R.id.id_content_frame);
        mTitleBar = mRootView.findViewById(R.id.id_title_bar);
        mSubRootView.addView(LayoutInflater.from(mContext).inflate(getSubLayout(), null));
        ButterKnife.bind(this, mSubRootView);
        init();
    }

    @Override
    public boolean bind() {
        return false;
    }

    @Override
    protected int getLayoutName() {
        return R.layout.layout_title_base_normal;
    }

    public abstract void init();

    public abstract int getSubLayout();
}
