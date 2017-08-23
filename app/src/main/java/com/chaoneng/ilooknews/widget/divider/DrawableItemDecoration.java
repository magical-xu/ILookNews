package com.chaoneng.ilooknews.widget.divider;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import com.chaoneng.ilooknews.util.CompatUtil;

/**
 * RecyclerView 分割线
 */
public class DrawableItemDecoration extends RecyclerView.ItemDecoration {

  private Drawable dividerDrawable;
  private int dividerHeight;
  private boolean isShowFirst;
  private boolean isShowLast;
  private boolean isVertical;
  private int dividerMargin;

  private final Rect mBounds = new Rect();

  public DrawableItemDecoration(Context context, int dividerDrawableId, int dividerHeight,
      int dividerMargin) {

    this.dividerDrawable = CompatUtil.getDrawable(context, dividerDrawableId);
    this.dividerHeight = dividerHeight;
    this.dividerMargin = dividerMargin;
    this.isVertical = true;
  }

  public DrawableItemDecoration(Context context, boolean isVertical, int dividerDrawableId,
      int dividerHeight, int dividerMargin, boolean isShowFirst, boolean isShowLast) {

    this.dividerDrawable = CompatUtil.getDrawable(context, dividerDrawableId);
    this.dividerHeight = dividerHeight;
    this.dividerMargin = dividerMargin;
    this.isShowFirst = isShowFirst;
    this.isShowLast = isShowLast;
    this.isVertical = isVertical;
  }

  @Override
  public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
    if (isVertical) {
      drawVertical(c, parent);
    } else {
      drawHorizontal(c, parent);
    }
  }

  // Refer: https://android.googlesource.com/platform/development/%2B/master/samples/Support7Demos/src/com/example/android/supportv7/widget/decorator/DividerItemDecoration.java
  private void drawVertical(Canvas c, RecyclerView parent) {
    final int left = parent.getPaddingLeft() + dividerMargin;
    final int right = parent.getWidth() - parent.getPaddingRight() - dividerMargin;

    for (int i = 0; i < parent.getChildCount(); i++) {
      final View child = parent.getChildAt(i);
      final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();

      int top;
      int bottom;

      // 第一行额外绘制 top divider
      if (i == 0 && isShowFirst) {
        top = parent.getPaddingTop();
        bottom = top + dividerHeight;
        dividerDrawable.setBounds(left, top, right, bottom);
        dividerDrawable.draw(c);
      }

      // 判断是否需要绘制 bottom divide
      boolean drawBottomDivide = true;
      if (i == parent.getChildCount() - 1 && !isShowLast) {
        drawBottomDivide = false;
      }

      if (drawBottomDivide) {
        top =
            child.getBottom() + params.bottomMargin + Math.round(ViewCompat.getTranslationY(child));
        bottom = top + dividerHeight;
        dividerDrawable.setBounds(left, top, right, bottom);
        dividerDrawable.draw(c);
      }
    }
  }

  @SuppressLint("NewApi")
  private void drawHorizontal(Canvas canvas, RecyclerView parent) {
    //final int top = parent.getPaddingTop();
    //final int bottom = parent.getHeight() - parent.getPaddingBottom();
    //
    //for (int i = 0; i < parent.getChildCount(); i++) {
    //  final View child = parent.getChildAt(i);
    //  final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
    //
    //  //todo: isShowFirst, isShowLast
    //
    //  final int left =
    //      child.getRight() + params.rightMargin + Math.round(ViewCompat.getTranslationX(child));
    //  final int right = left + dividerHeight;
    //  dividerDrawable.setBounds(left, top, right, bottom);
    //  dividerDrawable.draw(c);
    //}

    canvas.save();
    final int top;
    final int bottom;
    if (parent.getClipToPadding()) {
      top = parent.getPaddingTop();
      bottom = parent.getHeight() - parent.getPaddingBottom();
      canvas.clipRect(parent.getPaddingLeft(), top, parent.getWidth() - parent.getPaddingRight(),
          bottom);
    } else {
      top = 0;
      bottom = parent.getHeight();
    }

    final int childCount = parent.getChildCount();
    for (int i = 0; i < childCount; i++) {
      final View child = parent.getChildAt(i);
      parent.getLayoutManager().getDecoratedBoundsWithMargins(child, mBounds);
      final int right = mBounds.right + Math.round(child.getTranslationX()) + dividerHeight;
      final int left = right - dividerDrawable.getIntrinsicWidth();
      dividerDrawable.setBounds(left, top, right, bottom);
      dividerDrawable.draw(canvas);
    }
    canvas.restore();
  }

  @Override
  public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
      RecyclerView.State state) {
    //int position = parent.getChildAdapterPosition(view);
    //int count = state.getItemCount();
    //
    ////只有第一行需要top，避免space = top+bottom
    //if (isShowFirst && position == 0) {
    //  outRect.top = dividerHeight;
    //}
    //
    //outRect.bottom = dividerHeight;
    if (isVertical) {
      outRect.set(0, 0, 0, dividerDrawable.getIntrinsicHeight());
    } else {
      outRect.set(0, 0, dividerDrawable.getIntrinsicWidth(), 0);
    }
  }
}
