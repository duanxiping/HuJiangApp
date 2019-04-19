package com.hujiang.hujiangapp.model;

import com.blankj.utilcode.util.StringUtils;

public class AttendResult {
    private boolean result;
    private boolean noPermission = false;
    private String score;

    public boolean isResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public String getDisplayScore() {
        if (StringUtils.isEmpty(score)) {
            return "0";
        }
        return score;
    }

    public boolean isNoPermission() {
        return noPermission;
    }

    public void setNoPermission(boolean noPermission) {
        this.noPermission = noPermission;
    }
}
