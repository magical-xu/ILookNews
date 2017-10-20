package com.aktt.news.widget;

import android.content.Context;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by magical on 2017/08/27.
 */
//from http://stackoverflow.com/questions/30923889/flinging-with-recyclerview-appbarlayout
public class FlingBehavior extends AppBarLayout.Behavior {

  private static final int TOP_CHILD_FLING_THRESHOLD = 3;
  private boolean isPositive;

  public FlingBehavior() {
  }

  public FlingBehavior(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  @Override
  public boolean onNestedFling(CoordinatorLayout coordinatorLayout, AppBarLayout child, View target,
      float velocityX, float velocityY, boolean consumed) {
    if (target instanceof RecyclerView) {
      final RecyclerView recyclerView = (RecyclerView) target;
      consumed = velocityY > 0 || recyclerView.computeVerticalScrollOffset() > 0;
    }
    return super.onNestedFling(coordinatorLayout, child, target, velocityX, velocityY, consumed);
  }

  @Override
  public void onNestedPreScroll(CoordinatorLayout coordinatorLayout, AppBarLayout child,
      View target, int dx, int dy, int[] consumed) {
    super.onNestedPreScroll(coordinatorLayout, child, target, dx, dy, consumed);
    isPositive = dy > 0;
  }
}

