package com.chaoneng.ilooknews.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import butterknife.ButterKnife;

/**
 * Created by magical on 17/8/14.
 * Description :
 */

public abstract class BaseActivity extends AppCompatActivity {

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(getLayoutId());
    ButterKnife.bind(this);
  }

  public abstract int getLayoutId();

  protected boolean addCommonTitle(){
    return false;
  }
}
