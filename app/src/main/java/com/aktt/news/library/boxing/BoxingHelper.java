package com.aktt.news.library.boxing;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import com.bilibili.boxing.Boxing;
import com.bilibili.boxing.BoxingCrop;
import com.bilibili.boxing.BoxingMediaLoader;
import com.bilibili.boxing.model.config.BoxingConfig;
import com.bilibili.boxing.model.entity.BaseMedia;
import com.bilibili.boxing.model.entity.impl.ImageMedia;
import com.bilibili.boxing.utils.ImageCompressor;
import com.bilibili.boxing_impl.ui.BoxingActivity;
import com.aktt.news.R;
import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;

/**
 * Created by magical on 2017/8/22.
 * Description : Boxing 调用帮助类
 */

public class BoxingHelper {

    private static final String TAG = "BoxingHelper";
    private static final int REQUEST_CODE = 1024;
    private static final int COMPRESS_REQUEST_CODE = 2048;

    /**
     * 初始化 Boxing
     */
    public static void init() {

        BoxingMediaLoader.getInstance().init(new BoxingGlideLoader());
        BoxingCrop.getInstance().init(new BoxingUcrop());
    }

    /**
     * 单图模式 - 手动裁剪
     *
     * @param context Activity or Fragment
     */
    public static void startSingle(Object context) {

        BoxingConfig singleImgConfig =
                new BoxingConfig(BoxingConfig.Mode.SINGLE_IMG).withMediaPlaceHolderRes(
                        R.drawable.ic_boxing_default_image);
        if (context instanceof Activity) {
            Activity activity = (Activity) context;
            Boxing.of(singleImgConfig)
                    .withIntent(activity, BoxingActivity.class)
                    .start(activity, COMPRESS_REQUEST_CODE);
        } else if (context instanceof Fragment) {
            Fragment fragment = (Fragment) context;
            Boxing.of(singleImgConfig)
                    .withIntent(fragment.getActivity(), BoxingActivity.class)
                    .start(fragment, COMPRESS_REQUEST_CODE);
        } else {
            throw new IllegalArgumentException(" params can be Activity or Fragment only.");
        }
    }

    /**
     * 多图模式 不进行裁切
     *
     * @param context Activity or Fragment
     * @param limit 多选限制张数
     */
    public static void startMulti(Object context, int limit) {
        BoxingConfig config = new BoxingConfig(BoxingConfig.Mode.MULTI_IMG).needCamera(
                R.drawable.ic_boxing_camera_white).needGif().withMaxCount(limit);
        if (context instanceof Activity) {
            Activity activity = (Activity) context;
            Boxing.of(config)
                    .withIntent(activity, BoxingActivity.class)
                    .start(activity, REQUEST_CODE);
        } else if (context instanceof Fragment) {
            Fragment fragment = (Fragment) context;
            Boxing.of(config)
                    .withIntent(fragment.getActivity(), BoxingActivity.class)
                    .start(fragment, REQUEST_CODE);
        } else {
            throw new IllegalArgumentException(" params can be Activity or Fragment only.");
        }
    }

    /**
     * 结果回调
     */
    public static List<BaseMedia> onActivityResult(Context context, int requestCode, int resultCode,
            Intent data) {

        if (resultCode != RESULT_OK) {
            return null;
        }

        final ArrayList<BaseMedia> medias = Boxing.getResult(data);
        if (requestCode == REQUEST_CODE) {
            return medias;
        } else if (requestCode == COMPRESS_REQUEST_CODE) {
            //单张图片并压缩
            final List<BaseMedia> imageMedias = new ArrayList<>(1);
            if (null != medias && medias.size() >= 1) {
                BaseMedia baseMedia = medias.get(0);
                if (!(baseMedia instanceof ImageMedia)) {
                    return null;
                }

                final ImageMedia imageMedia = (ImageMedia) baseMedia;
                // the compress task may need time
                if (imageMedia.compress(new ImageCompressor(context))) {
                    imageMedia.removeExif();
                    imageMedias.add(imageMedia);
                    return imageMedias;
                } else {
                    return null;
                }
            } else {
                return null;
            }
        } else {
            return null;
        }
    }
}
