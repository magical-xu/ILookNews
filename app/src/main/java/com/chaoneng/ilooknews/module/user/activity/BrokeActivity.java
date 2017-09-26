package com.chaoneng.ilooknews.module.user.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import butterknife.BindView;
import com.bilibili.boxing.model.entity.BaseMedia;
import com.chaoneng.ilooknews.AppConstant;
import com.chaoneng.ilooknews.R;
import com.chaoneng.ilooknews.api.HomeService;
import com.chaoneng.ilooknews.base.BaseActivity;
import com.chaoneng.ilooknews.library.boxing.BoxingHelper;
import com.chaoneng.ilooknews.library.qiniu.QiNiuHelper;
import com.chaoneng.ilooknews.net.callback.SimpleCallback;
import com.chaoneng.ilooknews.net.client.NetRequest;
import com.chaoneng.ilooknews.net.data.HttpResult;
import com.chaoneng.ilooknews.util.SimpleNotifyListener;
import com.chaoneng.ilooknews.widget.ilook.ILookTitleBar;
import com.chaoneng.ilooknews.widget.selector.ImageSelector;
import com.chaoneng.ilooknews.widget.selector.ImageSelectorCallback;
import com.chaoneng.ilooknews.widget.text.CountEditText;
import com.magicalxu.library.blankj.EmptyUtils;
import com.magicalxu.library.blankj.KeyboardUtils;
import com.magicalxu.library.blankj.SizeUtils;
import com.magicalxu.library.blankj.ThreadPoolUtils;
import com.magicalxu.library.blankj.ToastUtils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import retrofit2.Call;
import timber.log.Timber;

/**
 * Created by magical on 17/8/21.
 * Description : 爆料界面
 */

public class BrokeActivity extends BaseActivity {

    @BindView(R.id.id_count_edit) CountEditText editText;
    @BindView(R.id.id_image_selector) ImageSelector imageSelector;

    private int selectSize;
    private HomeService service;
    private QiNiuHelper qiNiuHelper;
    private ThreadPoolUtils threadPoolUtils;
    private SparseArray<String> remoteArray;

    @Override
    public int getLayoutId() {
        return R.layout.activity_user_broke_news;
    }

    @Override
    protected boolean addTitleBar() {
        return true;
    }

    @Override
    public void handleChildPage(Bundle savedInstanceState) {

        remoteArray = new SparseArray<>();
        qiNiuHelper = new QiNiuHelper();
        threadPoolUtils = new ThreadPoolUtils(ThreadPoolUtils.FixedThread, 5);

        mTitleBar.setTitle("我要爆料")
                .setRightText("提交")
                .setTitleListener(new ILookTitleBar.TitleCallbackAdapter() {
                    @Override
                    public void onClickLeft(View view) {
                        super.onClickLeft(view);
                        finish();
                    }

                    @Override
                    public void onClickRightText(View view) {
                        super.onClickRightText(view);
                        onSubmit();
                    }
                });

        editText.setHeight(SizeUtils.dp2px(230))
                .setMaxNum(300)
                .setType(CountEditText.MULTI)
                .commit();

        service = NetRequest.getInstance().create(HomeService.class);

        imageSelector.setImageCallback(new ImageSelectorCallback() {
            @Override
            public void onAdd() {
                BoxingHelper.startMulti(BrokeActivity.this, 9 - selectSize);
            }

            @Override
            public void onRemove(int position) {
                imageSelector.removeImage(position);
                selectSize--;
            }
        });
    }

    /**
     * 提交按钮
     */
    private void onSubmit() {

        KeyboardUtils.hideSoftInput(editText);
        String text = editText.getText();
        if (TextUtils.isEmpty(text)) {
            return;
        }

        showLoading();
        if (imageSelector.getImageList().size() == 0) {
            // no image
            HashMap<String, String> params = new HashMap<>();
            params.put("picTotal", AppConstant.NONE_VALUE);
            handleSubmitBroke(text, params);
        } else {
            getUploadToken();
        }
    }

    /**
     * 获取七牛的 token
     */
    private void getUploadToken() {

        qiNiuHelper.getUpToken(new SimpleNotifyListener() {
            @Override
            public void onSuccess(String msg) {
                uploadImage(msg);
            }

            @Override
            public void onFailed(String msg) {
                hideLoading();
                ToastUtils.showShort(msg);
            }
        });
    }

    /**
     * 开线程池上传图片 最大任务数量为5
     */
    private void uploadImage(final String token) {

        List<String> localList = imageSelector.getImageList();
        if (EmptyUtils.isEmpty(localList)) {
            hideLoading();
            return;
        }

        int size = localList.size();
        for (int i = 0; i < size; i++) {

            final String path = localList.get(i);
            final int finalI = i;
            threadPoolUtils.execute(new Runnable() {
                @Override
                public void run() {

                    handleUpload(token, path, finalI);
                }
            });
        }
    }

    /**
     * 真正调用七牛上传的方法
     */
    private void handleUpload(String token, String path, final int index) {

        qiNiuHelper.upload(path, token, new SimpleNotifyListener() {
            @Override
            public void onSuccess(String msg) {

                remoteArray.put(index, msg);
                if (remoteArray.size() == imageSelector.getImageList().size()) {
                    // 全部上传成功
                    buildBrokeParams();
                }
            }

            @Override
            public void onFailed(String msg) {
                Timber.e("upload to qi_niu failed by index : " + index);
            }
        });
    }

    /**
     * 构造上传的json
     */
    private void buildBrokeParams() {

        HashMap<String, String> params = new HashMap<>();
        String text = editText.getText();

        StringBuilder builder = new StringBuilder();
        builder.append("[");
        int size = remoteArray.size();
        for (int i = 0; i < size; i++) {

            String picUrl = remoteArray.get(i);
            builder.append("\"");
            builder.append(picUrl);
            builder.append("\"");
            if (i != remoteArray.size() - 1) {
                builder.append(",");
            }
        }
        builder.append("]");

        String json = builder.toString();
        Log.e("qiniu", " the upload json : " + json);
        //Gson gson = new Gson();
        //String json = gson.toJson(remotePathList);
        params.put("pitcUrl", json);
        params.put("picTotal", String.valueOf(size));
        handleSubmitBroke(text, params);
    }

    /**
     * 提交爆料
     */
    private void handleSubmitBroke(String text, HashMap<String, String> params) {

        Call<HttpResult<String>> call = service.addBaoLiao(AppConstant.TEST_USER_ID, text, params);
        call.enqueue(new SimpleCallback<String>() {
            @Override
            public void onSuccess(String data) {

                hideLoading();
                ToastUtils.showShort("爆料成功");
            }

            @Override
            public void onFail(String code, String errorMsg) {

                hideLoading();
                ToastUtils.showShort(errorMsg);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        List<BaseMedia> baseMedias =
                BoxingHelper.onActivityResult(this, requestCode, resultCode, data);
        if (null != baseMedias && baseMedias.size() > 0) {
            List<String> list = new ArrayList<>(baseMedias.size());
            for (BaseMedia media : baseMedias) {
                list.add(media.getPath());
            }
            imageSelector.addImage(list);
            selectSize += list.size();
        }
    }
}
