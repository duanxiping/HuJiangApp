package com.hujiang.hujiangapp.shared;

import android.content.Context;
import android.os.Build;
import android.telephony.TelephonyManager;

import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.StringUtils;
import com.google.gson.Gson;
import com.hujiang.hujiangapp.misc.MyUtils;
import com.hujiang.hujiangapp.model.Dict;
import com.hujiang.hujiangapp.model.Project;
import com.hujiang.hujiangapp.model.User;
import com.orhanobut.logger.Logger;

public class SessionManager {
    private static volatile SessionManager manager = new SessionManager();
    private static final String KeyUserJson = "KeyCurrentUserJson";
    private static final String KeyProjectJson = "KeyCurrentProjectJson";
    private static final String KeyDeviceSerial = "KeyDeviceSerial";
    private static final String KeyUseFrontCamera = "KeyUseFrontCamera";
    private static final String KeyAttendTimeInterval = "KeyAttendTimeInterval";

    private Project currentProject;
    private User currentUser;
    private Dict attendInOut = Dict.attendIn();

    private SessionManager() {
        loadUser();
        loadProject();
    }

    public static SessionManager shared() {
        return manager;
    }

    public boolean isSignIn() {
        return currentProject != null && currentUser != null;
    }

    public void signIn(User user) {
        setCurrentUser(user);
        setCurrentProject(null);
    }

    public void signOut() {
        setCurrentUser(null);
        setCurrentProject(null);
    }

    public Project getCurrentProject() {
        return currentProject;
    }

    public void setCurrentProject(Project currentProject) {
        this.currentProject = currentProject;
        saveProject();
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
        saveUser();
    }

    private void loadUser() {
        String json = SPUtils.getInstance().getString(KeyUserJson);
        if (!StringUtils.isEmpty(json)) {
            Gson gson = new Gson();
            currentUser = gson.fromJson(json, User.class);
        }
    }

    private void saveUser() {
        if (currentUser == null) {
            SPUtils.getInstance().remove(KeyUserJson);
        } else {
            Gson gson = new Gson();
            String json = gson.toJson(currentUser);
            SPUtils.getInstance().put(KeyUserJson, json);
        }
    }

    private void loadProject() {
        String json = SPUtils.getInstance().getString(KeyProjectJson);
        if (!StringUtils.isEmpty(json)) {
            Gson gson = new Gson();
            currentProject = gson.fromJson(json, Project.class);
        }
    }

    private void saveProject() {
        if (currentProject == null) {
            SPUtils.getInstance().remove(KeyProjectJson);
        } else {
            Gson gson = new Gson();
            String json = gson.toJson(currentProject);
            SPUtils.getInstance().put(KeyProjectJson, json);
        }
    }

    public boolean isSerialOK() {
        String serial = SPUtils.getInstance().getString(KeyDeviceSerial);
        return !StringUtils.isEmpty(serial);
    }

    public String getDeviceSerial() {
        return SPUtils.getInstance().getString(KeyDeviceSerial, MyUtils.getUniquePseudoDeviceID());
    }

    // NOTE: need runtime permission
    public void prepareDeviceSerial(Context context) {
        String serial;
        if (!StringUtils.equals(Build.SERIAL, Build.UNKNOWN)) {
            serial = Build.SERIAL;
        } else {
            serial = Build.getSerial();
        }
        if (StringUtils.isEmpty(serial)) {
            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            serial = telephonyManager.getDeviceId();
        }
        if (StringUtils.isEmpty(serial)) {
            serial = MyUtils.getUniquePseudoDeviceID();
        }

        SPUtils.getInstance().put(KeyDeviceSerial, serial);
        Logger.d(serial);
    }

    public Dict getAttendInOut() {
        return attendInOut;
    }

    public void setAttendInOut(Dict attendInOut) {
        this.attendInOut = attendInOut;
    }

    public boolean isUseFrontCamera() {
        return SPUtils.getInstance().getBoolean(KeyUseFrontCamera, true);
    }

    public void setUseFrontCamera(boolean useFront) {
        SPUtils.getInstance().put(KeyUseFrontCamera, useFront);
    }

    public int getAttendTimeInterval() {
        return SPUtils.getInstance().getInt(KeyAttendTimeInterval, 10);
    }

    public void setAttendTimeInterval(int interval) {
        SPUtils.getInstance().put(KeyAttendTimeInterval, interval);
    }
}
