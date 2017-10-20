package com.aktt.news.widget.selector;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.aktt.news.R;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by magical on 17/8/23.
 * Description :
 */

public class ImageSelector extends LinearLayout {

    @BindView(R.id.id_recycler_view) RecyclerView mRecyclerView;
    @BindView(R.id.id_count) TextView mCountView;

    public static final int MAX = 9;
    private ImageSelectorAdapter mAdapter;
    private List<String> mDataList;

    public ImageSelector(Context context) {
        this(context, null);
    }

    public ImageSelector(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ImageSelector(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {

        setOrientation(VERTICAL);
        LayoutInflater.from(getContext()).inflate(R.layout.widget_image_selector, this, true);
        ButterKnife.bind(this);

        mDataList = new ArrayList<>();

        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        manager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mRecyclerView.setLayoutManager(manager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(), HORIZONTAL));

        mAdapter = new ImageSelectorAdapter(getContext(), mDataList);
        mRecyclerView.setAdapter(mAdapter);
    }

    public void setImageCallback(ImageSelectorCallback callback) {
        mAdapter.setOnImageCallback(callback);
    }

    public void addImage(List<String> list) {

        int itemCount = mAdapter.getItemCount();
        int insertIndex = itemCount - 1;

        mDataList.addAll(insertIndex, list);
        mAdapter.notifyDataSetChanged();
        notifyBottomCount();
        mRecyclerView.smoothScrollToPosition(mAdapter.getItemCount() - 1);
    }

    public void removeImage(int position) {
        mDataList.remove(position);
        mAdapter.notifyItemRemoved(position);
        notifyBottomCount();
    }

    private void notifyBottomCount() {

        int size = mDataList.size();
        String count = size + "/" + MAX;
        mCountView.setText(count);
    }

    public List<String> getImageList() {
        return mDataList;
    }
}
