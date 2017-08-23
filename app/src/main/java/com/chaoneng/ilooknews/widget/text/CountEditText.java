package com.chaoneng.ilooknews.widget.text;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.chaoneng.ilooknews.R;

/**
 * Created by pc on 2017/2/27.
 * 带计数的 EditText
 */
public class CountEditText extends LinearLayout {

  public static final String SINGLE = "single";   //单数
  public static final String MULTI = "multi"; // xx/xx

  private View mRoot;
  private TextView mCountTv;
  private EditText mEditText;
  private View mContent;

  private String TYPE = SINGLE;   //默认单数模式
  int maxNum = 10;                //默认长度为10

  public CountEditText(Context context) {
    this(context, null);
  }

  public CountEditText(Context context, AttributeSet attrs) {
    super(context, attrs);
    init();
  }

  private void init() {
    mRoot = LayoutInflater.from(getContext()).inflate(R.layout.widget_count_edit, this, true);

    mContent = mRoot.findViewById(R.id.id_root);
    mEditText = mRoot.findViewById(R.id.id_edit);
    mCountTv = mRoot.findViewById(R.id.id_only_text);
  }

  /**
   * 设置类型
   */
  public CountEditText setType(String type) {
    TYPE = type;
    return this;
  }

  /**
   * 设置限制字数
   */
  public CountEditText setMaxNum(int num) {
    if (num > 0) {
      maxNum = num;
    }
    return this;
  }

  /**
   * 设置提示语
   */
  public CountEditText setHint(String hint) {
    mEditText.setHint(hint);
    return this;
  }

  /**
   * 获取文本内容
   */
  public String getText() {
    return mEditText.getText().toString();
  }

  public void setText(@NonNull CharSequence charSequence) {
    mEditText.setText(charSequence);
  }

  /**
   * 动态改变 高度
   */
  public CountEditText setHeight(int rqHeight) {

    ViewGroup.LayoutParams layoutParams = mContent.getLayoutParams();
    layoutParams.height = rqHeight;
    mContent.setLayoutParams(layoutParams);
    return this;
  }

  public CountEditText setRootBg(int resId) {
    mRoot.setBackgroundResource(resId);
    return this;
  }

  public CountEditText commit() {

    if (TYPE.equals(SINGLE)) {
      mCountTv.setText(String.valueOf(maxNum));
    } else if (TYPE.equals(MULTI)) {
      mCountTv.setText(0 + " / " + maxNum);
    }

    mEditText.setFilters(new InputFilter[] { new InputFilter.LengthFilter(maxNum) });
    mEditText.addTextChangedListener(new TextWatcher() {

      int textStart;
      int textEnd;

      @Override
      public void beforeTextChanged(CharSequence s, int start, int count, int after) {
      }

      @Override
      public void onTextChanged(CharSequence s, int start, int before, int count) {
      }

      @Override
      public void afterTextChanged(Editable s) {

        textStart = mEditText.getSelectionStart();
        textEnd = mEditText.getSelectionEnd();
        //先去掉监听器 否则会出现栈溢出
        mEditText.removeTextChangedListener(this);

        while (calculateLength(s.toString()) > maxNum) {
          s.delete(textStart - 1, textEnd);
          textStart--;
          textEnd--;
        }
        //恢复监听器
        mEditText.addTextChangedListener(this);
        setLeftCount();
      }
    });
    return this;
  }

  private void setLeftCount() {
    if (TYPE.equals(SINGLE)) {
      mCountTv.setText(String.valueOf(maxNum - getInputCount()));
    } else if (TYPE.equals(MULTI)) {
      mCountTv.setText(getInputCount() + " / " + maxNum);
    }
  }

  private long getInputCount() {
    return calculateLength(mEditText.getText().toString());
  }

  public long calculateLength(CharSequence cs) {

    double len = 0;
    for (int i = 0; i < cs.length(); i++) {
      int tmp = cs.charAt(i);
      if (tmp > 0 && tmp < 127) {
        len += 1;
      } else {
        len++;
      }
    }
    return Math.round(len);
  }
}
