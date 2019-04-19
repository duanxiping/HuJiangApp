package com.hujiang.hujiangapp.model;

import com.blankj.utilcode.util.StringUtils;

public class CardOcrInfo {
    // 身份证正面信息
    private String title;
    private String idNumber;
    private String sex;
    private String nation;
    private String dateOfBirth;
    private String address;

    // 身份证背面信息
    private String issue;
    private String idCardStartdate;
    private String idCardValiditydate;

    // 银行卡信息
    private String card_num;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getIdNumber() {
        return idNumber;
    }

    public void setIdNumber(String idNumber) {
        this.idNumber = idNumber;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getNation() {
        return nation;
    }

    public void setNation(String nation) {
        this.nation = nation;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getIssue() {
        return issue;
    }

    public void setIssue(String issue) {
        this.issue = issue;
    }

    public String getIdCardStartdate() {
        return idCardStartdate;
    }

    public void setIdCardStartdate(String idCardStartdate) {
        this.idCardStartdate = idCardStartdate;
    }

    public String getIdCardValiditydate() {
        return idCardValiditydate;
    }

    public void setIdCardValiditydate(String idCardValiditydate) {
        this.idCardValiditydate = idCardValiditydate;
    }

    public String getCard_num() {
        return card_num;
    }

    public void setCard_num(String card_num) {
        this.card_num = card_num;
    }

    public String getValidDateRange() {
        return String.format("%s-%s", getIdCardStartdate(), getIdCardValiditydate());
    }

    public boolean setValidDateRange(String range) {
        if (StringUtils.isEmpty(range)) {
            return false;
        }
        String[] parts = range.split("-");
        if (parts.length != 2) {
            return false;
        }
        String start = parts[0].trim();
        String end = parts[1].trim();
        if (start.length() != 8 || end.length() != 8) {
            return false;
        }
        setIdCardStartdate(start);
        setIdCardValiditydate(end);
        return true;
    }

    public Dict getSexDict() {
        return StringUtils.equals(sex, "男") ? Dict.sexMale() : Dict.sexFemale();
    }
}
