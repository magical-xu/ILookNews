package com.chaoneng.ilooknews.module.user.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import butterknife.BindView;
import com.bilibili.boxing.model.entity.BaseMedia;
import com.chaoneng.ilooknews.R;
import com.chaoneng.ilooknews.base.BaseActivity;
import com.chaoneng.ilooknews.library.boxing.BoxingHelper;
import com.chaoneng.ilooknews.widget.ilook.ILookTitleBar;
import com.chaoneng.ilooknews.widget.selector.ImageSelector;
import com.chaoneng.ilooknews.widget.selector.ImageSelectorCallback;
import com.chaoneng.ilooknews.widget.text.CountEditText;
import com.magicalxu.library.blankj.SizeUtils;
import com.magicalxu.library.blankj.ToastUtils;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by magical on 17/8/21.
 * Description : 爆料界面
 */

public class BrokeActivity extends BaseActivity {

  @BindView(R.id.id_count_edit) CountEditText editText;
  @BindView(R.id.id_image_selector) ImageSelector imageSelector;

  private int selectSize;

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

    editText.setHeight(SizeUtils.dp2px(230)).setMaxNum(300).setType(CountEditText.MULTI).commit();

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

  private void onSubmit() {
    ToastUtils.showShort(imageSelector.getImageList().size() + " 提交");
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    List<BaseMedia> baseMedias = BoxingHelper.onActivityResult(this, requestCode, resultCode, data);
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
