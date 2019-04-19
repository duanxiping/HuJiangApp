package com.hujiang.hujiangapp.shared;

import com.blankj.utilcode.util.StringUtils;
import com.hujiang.hujiangapp.misc.MyUtils;
import com.hujiang.hujiangapp.model.BuildCompany;
import com.hujiang.hujiangapp.model.CardOcrInfo;
import com.hujiang.hujiangapp.model.Dict;
import com.hujiang.hujiangapp.model.ImageResource;
import com.hujiang.hujiangapp.model.Project;
import com.hujiang.hujiangapp.model.Team;
import com.hujiang.hujiangapp.model.WorkType;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class RegisterData {
    private static volatile RegisterData data = new RegisterData();

    private RegisterData() {}

    public static RegisterData shared() {
        return data;
    }

    public File idFrontImage = null;
    public File idBackImage = null;
    public File bankImage = null;
    public File faceImage = null;

    public CardOcrInfo idFrontInfo = null;
    public CardOcrInfo idBackInfo = null;
    public CardOcrInfo bankInfo = null;

    public ImageResource idFrontResource = null;
    public ImageResource idBackResource = null;
    public ImageResource bankResource = null;
    public ImageResource faceResource = null;

    public Project project = null;
    public BuildCompany buildCompany = null;
    public Team team = null;
    public WorkType workType = null;
    public boolean isTeamLeader = false;
    public String phone = "";
    public String entranceDate = null;

    public Dict bankDict = null;

    public boolean isChangingWorkerInfo = false;

    public boolean isContract = false;               //简易劳动合同上传状态
    public boolean isEntrance = false;               //工人进场承诺书上传状态
    public boolean isExit = false;                   //工人退场承诺书上传状态
    public boolean isWorkConfirm = false;            ///两制“工作”确认书上传状态
    public boolean isIDCardPDF = false;              //身份证正反面文件上传状态


    public Dict empCategory = null;                       //人员类型
    public Dict jobTypeName = null;                       //岗位

    public void clearAllData() {
        data = new RegisterData();

        SimpleDateFormat format = new SimpleDateFormat(MyUtils.DateFormat_10, Locale.getDefault());
        data.entranceDate = format.format(new Date());
    }

    public boolean isIdFrontInfoOK() {
        return (idFrontInfo != null &&
                !StringUtils.isEmpty(idFrontInfo.getTitle()) &&
                !StringUtils.isEmpty(idFrontInfo.getIdNumber()) &&
                !StringUtils.isEmpty(idFrontInfo.getSex()) &&
                !StringUtils.isEmpty(idFrontInfo.getNation()) &&
                !StringUtils.isEmpty(idFrontInfo.getDateOfBirth()) &&
                !StringUtils.isEmpty(idFrontInfo.getAddress()));
    }

    public boolean isIdBackInfoOK() {
        return (idBackInfo != null &&
                !StringUtils.isEmpty(idBackInfo.getIssue()) &&
                !StringUtils.isEmpty(idBackInfo.getIdCardStartdate()) &&
                !StringUtils.isEmpty(idBackInfo.getIdCardValiditydate()));
    }

    public boolean isBankInfoOK() {
        return (bankInfo != null &&
                !StringUtils.isEmpty(bankInfo.getCard_num()));
    }
}
