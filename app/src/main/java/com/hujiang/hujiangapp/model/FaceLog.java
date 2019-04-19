package com.hujiang.hujiangapp.model;

import com.hujiang.hujiangapp.shared.SessionManager;

public class FaceLog {
    private ImageResource facePhoto;//脸部照片
    private Dict inOut = SessionManager.shared().getAttendInOut();				//进出标识
    private Project project = SessionManager.shared().getCurrentProject();
    private User attendanceUser = SessionManager.shared().getCurrentUser();	//考勤用户
    private AttendDevice device = new AttendDevice();			//打卡设备

    private Hire hire;
    private String createTime;

    public FaceLog(ImageResource facePhoto) {
        this.facePhoto = facePhoto;
    }

    public ImageResource getFacePhoto() {
        return facePhoto;
    }

    public void setFacePhoto(ImageResource facePhoto) {
        this.facePhoto = facePhoto;
    }

    public Dict getInOut() {
        return inOut;
    }

    public void setInOut(Dict inOut) {
        this.inOut = inOut;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public User getAttendanceUser() {
        return attendanceUser;
    }

    public void setAttendanceUser(User attendanceUser) {
        this.attendanceUser = attendanceUser;
    }

    public AttendDevice getDevice() {
        return device;
    }

    public void setDevice(AttendDevice device) {
        this.device = device;
    }

    public Hire getHire() {
        return hire;
    }

    public void setHire(Hire hire) {
        this.hire = hire;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }
}
