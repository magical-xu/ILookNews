package com.chaoneng.ilooknews;

import android.app.Application;
import android.content.Context;
import com.magicalxu.library.Utils;

/**
 * Created by magical on 17/8/14.
 * Description : ILookNewsApplication
 */

public class ILookApplication extends Application{

  private static Context INSTANCE;

  @Override
  public void onCreate() {
    super.onCreate();

    INSTANCE = getApplicationContext();

    Utils.init(this);
  }

  public static Context getAppContext(){
    return INSTANCE;
  }
}
