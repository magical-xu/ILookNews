package com.chaoneng.ilooknews.module.user.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.chaoneng.ilooknews.AppConstant;
import com.chaoneng.ilooknews.R;
import com.chaoneng.ilooknews.module.home.fragment.PhotoDetailFragment;
import com.chaoneng.ilooknews.widget.adapter.BaseFragmentAdapter;
import com.chaoneng.ilooknews.widget.adapter.OnPageChangeListener;
import com.chaoneng.ilooknews.widget.viewpager.ViewPagerFixed;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by magical on 17/10/19.
 * Description :
 */

public class ImageBrowseActivity extends AppCompatActivity {

    private static final String PARAMS_IMAGES = "params_images";
    private static final String PARAMS_POSITION = "params_position";

    @BindView(R.id.id_back) ImageView idBack;
    @BindView(R.id.view_pager) ViewPagerFixed mViewPager;
    @BindView(R.id.id_position) TextView idPosition;

    private int curPos;
    private int totalSize;
    private List<Fragment> mPhotoDetailFragmentList = new ArrayList<>();

    public static void getInstance(Context context, int curPos, ArrayList<String> images) {

        Intent intent = new Intent(context, ImageBrowseActivity.class);
        intent.putStringArrayListExtra(PARAMS_IMAGES, images);
        intent.putExtra(PARAMS_POSITION, curPos);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_browse);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        ArrayList<String> images = intent.getStringArrayListExtra(PARAMS_IMAGES);
        curPos = intent.getIntExtra(PARAMS_POSITION, 0);
        totalSize = images.size();

        config(images);
        checkCurPosition(curPos);
    }

    @OnClick(R.id.id_back)
    public void onViewClicked() {
        finish();
    }

    private void initViewPager() {
        BaseFragmentAdapter photoPagerAdapter =
                new BaseFragmentAdapter(getSupportFragmentManager(), mPhotoDetailFragmentList);
        mViewPager.setAdapter(photoPagerAdapter);
        mViewPager.setOffscreenPageLimit(5);
        mViewPager.addOnPageChangeListener(new OnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                checkCurPosition(position);
            }
        });

        mViewPager.setCurrentItem(curPos);
    }

    private void checkCurPosition(int position) {

        String des = (position + 1) + " / " + totalSize;

        idPosition.setText(des);
    }

    private void config(ArrayList<String> list) {
        mPhotoDetailFragmentList.clear();
        for (String url : list) {
            PhotoDetailFragment fragment = new PhotoDetailFragment();
            Bundle bundle = new Bundle();
            bundle.putString(AppConstant.PHOTO_DETAIL_IMGSRC, url);
            fragment.setArguments(bundle);
            mPhotoDetailFragmentList.add(fragment);
        }

        initViewPager();
    }
}
