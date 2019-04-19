package com.hujiang.hujiangapp.misc;

import android.content.Context;
import android.os.Build;

import com.blankj.utilcode.util.FileUtils;
import com.blankj.utilcode.util.StringUtils;
import com.blankj.utilcode.util.TimeUtils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class MyUtils {
    public static final String MobileRegex = "^1\\d{10}$";
    public static final String DateFormat_10 = "yyyy-MM-dd";
    public static final String DateFormat_8 = "yyyyMMdd";

    public static String convertIdCardBirthDate(String dateString) {
        if (!StringUtils.isEmpty(dateString)) {
            String ymd = DateFormat_10;
            if (dateString.length() == 8) {
                ymd = DateFormat_8;
            }

            try {
                SimpleDateFormat format = new SimpleDateFormat(ymd, Locale.getDefault());
                Date date = format.parse(dateString);

                SimpleDateFormat serverFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                return serverFormat.format(date);
            } catch (Exception ex) {
                // do nothing
            }
        }
        return dateString;
    }

    public static Date dateFromString(String string) {
        if (StringUtils.isEmpty(string)) {
            return new Date();
        }

        String formatString = null;
        int length = StringUtils.length(string);
        if (length == 8) {
            formatString = DateFormat_8;
        } else if (length == 10) {
            formatString = DateFormat_10;
        }

        if (StringUtils.isEmpty(formatString)) {
            return new Date();
        }

        try {
            SimpleDateFormat format = new SimpleDateFormat(formatString, Locale.getDefault());
            return format.parse(string);
        } catch (Exception ex) {
            // do nothing
        }

        return new Date();
    }

    public static String stripTimeFromDate(String dateTimeString) {
        if (StringUtils.isEmpty(dateTimeString)) {
            return "";
        }

        int index = dateTimeString.indexOf(" ");
        if (0 < index) {
            return dateTimeString.substring(0, index);
        }

        return dateTimeString;
    }

    public static boolean isValidMobile(String phone) {
        if (phone == null) {
            return false;
        }

        return phone.matches(MobileRegex);
    }

    public static File appImagesDir(Context context) {
        File base = context.getFilesDir();
        return new File(base, "app_images");
    }

    public static File newBitmapFile(Context context) {
        String timestamp = TimeUtils.date2String(new Date(), new SimpleDateFormat("yyyyMMddHHmmssSSS", Locale.getDefault()));
        String filename = timestamp + ".jpg";

        File imageDir = appImagesDir(context);
        FileUtils.createOrExistsDir(imageDir);

        return new File(imageDir, filename);
    }

    public static MultipartBody.Part imageMultipartFile(File imageFile) {
        RequestBody requestFile = RequestBody.create(MediaType.parse("image/jpeg"), imageFile);
        return MultipartBody.Part.createFormData("file", imageFile.getName(), requestFile);
    }

    //获得独一无二的Pseudo Device ID
    public static String getUniquePseudoDeviceID() {
        String serial = null;

        String m_szDevIDShort = "35" +
                Build.BOARD.length() % 10 + Build.BRAND.length() % 10 +
                Build.CPU_ABI.length() % 10 + Build.DEVICE.length() % 10 +
                Build.DISPLAY.length() % 10 + Build.HOST.length() % 10 +
                Build.ID.length() % 10 + Build.MANUFACTURER.length() % 10 +
                Build.MODEL.length() % 10 + Build.PRODUCT.length() % 10 +
                Build.TAGS.length() % 10 + Build.TYPE.length() % 10 +
                Build.USER.length() % 10; //13 位

        try {
            //API>=9 使用serial号
            serial = android.os.Build.class.getField("SERIAL").get(null).toString();
        } catch (Exception exception) {
            //serial需要一个初始化
            serial = "serial"; // 随便一个初始化
        }
        //使用硬件信息拼凑出来的15位号码
        return new UUID(m_szDevIDShort.hashCode(), serial.hashCode()).toString();
    }
}
