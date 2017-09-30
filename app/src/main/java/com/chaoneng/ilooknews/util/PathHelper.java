package com.chaoneng.ilooknews.util;

import android.os.Environment;
import java.io.File;

/**
 * Created by magical on 17/9/30.
 * Description :
 */

public class PathHelper {

    public static String getCachePath() {
        String path = Environment.getExternalStorageDirectory() + "/ILookNews/image/";
        File file = new File(path);
        if (file.mkdirs()) {
            return path;
        }
        return path;
    }
}
