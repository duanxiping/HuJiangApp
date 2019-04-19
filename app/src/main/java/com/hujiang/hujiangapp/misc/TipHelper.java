package com.hujiang.hujiangapp.misc;

import android.app.Activity;
import android.app.Service;
import android.os.Vibrator;

/**
 * author : 段工
 * date   : 2019-04-1615:56
 * desc   : 震动帮助类
 * version: 1.0
 */
public class TipHelper {

    public static void Vibrate(final Activity activity, long milliseconds) {
        Vibrator vib = (Vibrator) activity.getSystemService(Service.VIBRATOR_SERVICE);
        vib.vibrate(milliseconds);
    }

    public static void Vibrate(final Activity activity, long[] pattern, boolean isRepeat) {
        Vibrator vib = (Vibrator) activity.getSystemService(Service.VIBRATOR_SERVICE);
        vib.vibrate(pattern, isRepeat ? 1 : -1);
    }

}
