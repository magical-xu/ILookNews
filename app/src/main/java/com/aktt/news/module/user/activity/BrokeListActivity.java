package com.aktt.news.module.user.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import com.aktt.news.R;
import com.aktt.news.base.BaseActivity;
import com.aktt.news.instance.AccountManager;
import com.aktt.news.module.user.fragment.BrokeNewsListFragment;
import com.aktt.news.widget.ilook.ILookTitleBar;

/**
 * Created by magical on 2017/10/23.
 * Description : 爆料列表
 */

public class BrokeListActivity extends BaseActivity {

    private BrokeNewsListFragment mBrokeListFragment;

    @Override
    public int getLayoutId() {
        return R.layout.activity_broke_list;
    }

    @Override
    protected boolean addTitleBar() {
        return true;
    }

    @Override
    public void handleChildPage(Bundle savedInstanceState) {

        mTitleBar.setTitle("爆料列表").setTitleListener(new ILookTitleBar.TitleCallbackAdapter());

        String userId = AccountManager.getInstance().getUserId();
        if (TextUtils.isEmpty(userId)) {
            return;
        }

        if (null == mBrokeListFragment) {
            mBrokeListFragment = BrokeNewsListFragment.getInstance(userId);
        }

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.id_fragment_container, mBrokeListFragment);
        fragmentTransaction.commit();

        mBrokeListFragment.setUserVisibleHint(true);
    }
}
