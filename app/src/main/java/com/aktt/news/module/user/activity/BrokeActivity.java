package com.aktt.news.module.user.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import butterknife.BindView;
import com.aktt.news.AppConstant;
import com.aktt.news.R;
import com.aktt.news.api.HomeService;
import com.aktt.news.base.BaseActivity;
import com.aktt.news.data.ImageBean;
import com.aktt.news.instance.AccountManager;
import com.aktt.news.library.boxing.BoxingHelper;
import com.aktt.news.library.qiniu.QiNiuHelper;
import com.aktt.news.net.callback.SimpleCallback;
import com.aktt.news.net.client.NetRequest;
import com.aktt.news.net.data.HttpResult;
import com.aktt.news.util.PathHelper;
import com.aktt.news.util.SimpleNotifyListener;
import com.aktt.news.widget.ilook.ILookTitleBar;
import com.aktt.news.widget.selector.ImageSelector;
import com.aktt.news.widget.selector.ImageSelectorCallback;
import com.aktt.news.widget.text.CountEditText;
import com.bilibili.boxing.model.entity.BaseMedia;
import com.magicalxu.library.blankj.EmptyUtils;
import com.magicalxu.library.blankj.FileUtils;
import com.magicalxu.library.blankj.KeyboardUtils;
import com.magicalxu.library.blankj.SizeUtils;
import com.magicalxu.library.blankj.ThreadPoolUtils;
import com.magicalxu.library.blankj.ToastUtils;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import retrofit2.Call;
import timber.log.Timber;
import top.zibin.luban.Luban;
import top.zibin.luban.OnCompressListener;

/**
 * Created by magical on 17/8/21.
 * Description : 爆料界面
 */

public class BrokeActivity extends BaseActivity {

    @BindView(R.id.id_count_edit) CountEditText editText;
    @BindView(R.id.id_image_selector) ImageSelector imageSelector;

    private int selectSize;
    private HomeService service;
    private ThreadPoolUtils threadPoolUtils;
    private SparseArray<String> remoteArray;
    private ArrayList<ImageBean> sortCompressList;

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

        sortCompressList = new ArrayList<>();
        remoteArray = new SparseArray<>();
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

        QiNiuHelper.getInstance().getUpToken(new SimpleNotifyListener() {
            @Override
            public void onSuccess(String msg) {
                uploadImage(msg);
            }

            @Override
            public void onFailed(String msg) {
                onSimpleError(msg);
            }
        });
    }

    private void uploadImage(final String token) {

        List<String> localList = imageSelector.getImageList();
        if (EmptyUtils.isEmpty(localList)) {
            hideLoading();
            return;
        }

        //开启压缩
        compressImage(localList, token);
    }

    /**
     * 开线程池上传图片 最大任务数量为5
     */
    private void startUpload(final String token) {

        if (sortCompressList == null || sortCompressList.size() < 1) {
            return;
        }

        int size = sortCompressList.size();
        Collections.sort(sortCompressList, new MyComparator());

        for (int i = 0; i < size; i++) {

            final String path = sortCompressList.get(i).path;
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
     * 压缩图片
     *
     * @param localList 本地图片地址集合
     */
    private void compressImage(List<String> localList, final String token) {

        final int size = localList.size();
        for (int i = 0; i < size; i++) {

            String localPath = localList.get(i);
            final int finalI = i;
            Luban.with(this)
                    .load(localPath)
                    .ignoreBy(100)
                    .setTargetDir(PathHelper.getCachePath())
                    .setCompressListener(new OnCompressListener() {
                        @Override
                        public void onStart() {
                            Timber.d("start to compress image");
                        }

                        @Override
                        public void onSuccess(File file) {
                            if (null == file || !file.exists()) {
                                return;
                            }
                            String compressedPath = file.getAbsolutePath();
                            sortCompressList.add(new ImageBean(compressedPath, finalI));

                            if (sortCompressList.size() == size) {
                                //全部压缩完毕 开始上传
                                startUpload(token);
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            Timber.e("failed to compress image");
                            hideLoading();
                        }
                    })
                    .launch();
        }
    }

    /**
     * 真正调用七牛上传的方法
     */
    private void handleUpload(String token, String path, final int index) {

        QiNiuHelper.getInstance().upload(path, token, new SimpleNotifyListener() {
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

        String userId = AccountManager.getInstance().getUserId();
        if (TextUtils.isEmpty(userId)) {
            return;
        }

        Call<HttpResult<String>> call = service.addBaoLiao(userId, text, params);
        call.enqueue(new SimpleCallback<String>() {
            @Override
            public void onSuccess(String data) {

                hideLoading();
                ToastUtils.showShort("爆料成功");
                finish();
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

    class MyComparator implements Comparator<ImageBean> {

        @Override
        public int compare(ImageBean t1, ImageBean t2) {
            return t1.sort > t2.sort ? 1 : -1;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        deleteCacheImage();
    }

    @Override
    public ArrayList<Call> addRequestList() {
        return null;
    }

    private void deleteCacheImage() {

        String cachePath = PathHelper.getCachePath();
        boolean result = FileUtils.deleteAllInDir(cachePath);
        Timber.d("delete cache image result : " + result);
    }
}
