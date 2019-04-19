package com.hujiang.hujiangapp.model;

import android.os.Build;

import com.hujiang.hujiangapp.shared.SessionManager;

public class AttendDevice {
    private String title = Build.MODEL;	//设备名称
    private String sn = SessionManager.shared().getDeviceSerial();		//设备序列号

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSn() {
        return sn;
    }

    public void setSn(String sn) {
        this.sn = sn;
    }
}
