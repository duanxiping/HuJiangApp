package com.hujiang.hujiangapp.model;

public class User {
    private long id;
    private String userName;
    private String displayName;

    private boolean isProjectUser;

    private boolean authority = false;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public boolean isProjectUser() {
        return isProjectUser;
    }

    public void setProjectUser(boolean projectUser) {
        isProjectUser = projectUser;
    }

    public boolean isAuthority() {
        return authority;
    }

    public void setAuthority(boolean authority) {
        this.authority = authority;
    }
}
