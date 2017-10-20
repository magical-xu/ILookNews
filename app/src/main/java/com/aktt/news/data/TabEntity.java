package com.aktt.news.data;

import com.flyco.tablayout.listener.CustomTabEntity;

/**
 * 首页TAB栏 实体类
 */
public class TabEntity implements CustomTabEntity {

  public String title;
  public int selectedIcon;
  public int unSelectedIcon;

  public TabEntity(String title, int selectedIcon, int unSelectedIcon) {
    this.title = title;
    this.selectedIcon = selectedIcon;
    this.unSelectedIcon = unSelectedIcon;
  }

  @Override
  public String getTabTitle() {
    return title;
  }

  @Override
  public int getTabSelectedIcon() {
    return selectedIcon;
  }

  @Override
  public int getTabUnselectedIcon() {
    return unSelectedIcon;
  }
}
