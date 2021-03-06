package com.aktt.news;

/**
 * Created by magical on 17/8/15.
 * Description :
 */

public interface AppConstant {

    int REQUEST_CODE = 1001;

    String UID = "UID";
    String USER_ICON = "USER_ICON";
    String WIFI_4G_TIP = "wifi_4g_tip";
    String NEWS_TEXT_SIZE = "news_text_size";
    String SEARCH_HISTORY = "search_history";

    String VIDEO_SUFFIX = "?vframe/jpg/offset/1";

    String NONE_VALUE = "0";

    int DEFAULT_PAGE_SIZE = 10;   //列表页默认10条
    int INVALIDATE = -1;

    String COMMENT_LEVEL_ONE = "0"; //表示为一级评论

    String USER_FOLLOW = "1";     //已关注
    String USER_UN_FOLLOW = "0";  //未关注
    int USER_FOLLOW_INT = 1;

    String HAS_PRAISE = "1";
    String UN_PRAISE = "0";

    String PARAMS_NEWS_ID = "params_news_id";
    String PARAMS_NEWS_TYPE = "params_news_type";
    String PARAMS_COMMENT_ID = "params_comment_id";
    String PARAMS_USER_ID = "params_user_id";

    String PAGE_TYPE = "PAGE_TYPE";
    String PAGE_PROGRESS = "PAGE_PROGRESS";

    String PHOTO_DETAIL_IMGSRC = "photo_detail_imgsrc";
}
