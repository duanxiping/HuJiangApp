package com.hujiang.hujiangapp.misc;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.ScrollView;

import com.hujiang.hujiangapp.activity.CardCameraActivity;
import com.hujiang.hujiangapp.activity.FaceActivity;
import com.hujiang.hujiangapp.activity.OcrInfoActivity;
import com.hujiang.hujiangapp.activity.SignInActivity;
import com.hujiang.hujiangapp.model.Hire;
import com.hujiang.hujiangapp.shared.SessionManager;

import java.util.Calendar;
import java.util.List;

import cn.qqtheme.framework.picker.DatePicker;
import cn.qqtheme.framework.picker.OptionPicker;
import cn.qqtheme.framework.picker.SinglePicker;

public class MyUiUtils {
    public static void showYearMonthDayPicker(Activity activity, String selectedDate, DatePicker.OnYearMonthDayPickListener listener) {
        Calendar startCalendar = Calendar.getInstance();
        int startYear = startCalendar.get(Calendar.YEAR);

        Calendar selectedCalendar = Calendar.getInstance();
        selectedCalendar.setTime(MyUtils.dateFromString(selectedDate));
        int selectedYear = selectedCalendar.get(Calendar.YEAR);
        int selectedMonth = selectedCalendar.get(Calendar.MONTH) + 1;
        int selectedDay = selectedCalendar.get(Calendar.DAY_OF_MONTH);

        int minYear = Math.min(startYear, selectedYear);
        int maxYear = Math.max(startYear, selectedYear);

        final DatePicker picker = new DatePicker(activity);
        picker.setCanceledOnTouchOutside(true);
        picker.setUseWeight(true);
        picker.setTopPadding(20);
        picker.setRangeStart(minYear - 5, 1, 1);
        picker.setRangeEnd(maxYear + 5,12, 31);
        picker.setSelectedItem(selectedYear, selectedMonth, selectedDay);
        picker.setResetWhileWheel(false);
        picker.setOnDatePickListener(listener);
        picker.setOnWheelListener(new DatePicker.OnWheelListener() {
            @Override
            public void onYearWheeled(int index, String year) {
                picker.setTitleText(year + "-" + picker.getSelectedMonth() + "-" + picker.getSelectedDay());
            }

            @Override
            public void onMonthWheeled(int index, String month) {
                picker.setTitleText(picker.getSelectedYear() + "-" + month + "-" + picker.getSelectedDay());
            }

            @Override
            public void onDayWheeled(int index, String day) {
                picker.setTitleText(picker.getSelectedYear() + "-" + picker.getSelectedMonth() + "-" + day);
            }
        });
        picker.show();
    }

    public static void signOut(Activity activity) {
        SessionManager.shared().signOut();

        Intent intent = new Intent(activity.getApplicationContext(), SignInActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(intent);
    }

    public static void showCardCameraActivity(Context context, MyConstants.ScanType scanType) {
        Intent intent = new Intent(context, CardCameraActivity.class);
        intent.putExtra(MyConstants.ExtraScanType, scanType.ordinal());
        context.startActivity(intent);
    }

    public static void showFaceDetectActivity(Context context, boolean useFrontCamera) {
        Intent intent = new Intent(context, FaceActivity.class);
        intent.putExtra(MyConstants.ExtraFrontCamera, useFrontCamera);
        context.startActivity(intent);
    }
    public static void changeFaceDetectActivity(Context context, boolean useFrontCamera, boolean isChangeFace,String jsonFace) {
        Intent intent = new Intent(context, FaceActivity.class);
        intent.putExtra("isChangeFace",isChangeFace);
        intent.putExtra("jsonFace",jsonFace);
        intent.putExtra(MyConstants.ExtraFrontCamera, useFrontCamera);
        context.startActivity(intent);
    }

    public static void showOcrInfoActivity(Context context, MyConstants.ScanType scanType) {
        Intent intent = new Intent(context, OcrInfoActivity.class);
        intent.putExtra(MyConstants.ExtraScanType, scanType.ordinal());
        context.startActivity(intent);
    }



    public static void scrollToBottom(ScrollView scrollView) {
        View lastChild = scrollView.getChildAt(scrollView.getChildCount() - 1);
        int bottom = lastChild.getBottom() + scrollView.getPaddingBottom();
        int sy = scrollView.getScrollY();
        int sh = scrollView.getHeight();
        int delta = bottom - (sy + sh);

        scrollView.smoothScrollBy(0, delta);
    }

    public static void showOptionPicker(Activity activity, String[] data, int selectedIndex, OptionPicker.OnOptionPickListener onOptionPickListener) {
        if (data == null || data.length == 0) {
            return;
        }
        if (selectedIndex < 0 || data.length <= selectedIndex) {
            selectedIndex = 0;
        }

        OptionPicker picker = new OptionPicker(activity, data);
        picker.setCanceledOnTouchOutside(true);
        picker.setSelectedIndex(selectedIndex);
        picker.setCycleDisable(true);
        if (onOptionPickListener != null) {
            picker.setOnOptionPickListener(onOptionPickListener);
        }
        picker.show();
    }

    public static <T> void showPicker(Activity activity, List<T> data, int selectedIndex, SinglePicker.OnItemPickListener<T> onItemPickListener) {
        if (data == null || data.size() == 0) {
            return;
        }
        if (selectedIndex < 0 || data.size() <= selectedIndex) {
            selectedIndex = 0;
        }
        SinglePicker<T> picker = new SinglePicker<T>(activity, data);
        picker.setCanceledOnTouchOutside(true);
        picker.setSelectedIndex(selectedIndex);
        picker.setCycleDisable(true);
        if (onItemPickListener != null) {
            picker.setOnItemPickListener(onItemPickListener);
        }
        picker.show();
    }
}
