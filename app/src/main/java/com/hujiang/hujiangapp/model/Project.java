package com.hujiang.hujiangapp.model;

public class Project {
    private long id;
    private String projectId;			//同步编号
    private String title;				//项目名称
    private String shortTitle;			//项目简称
    private String projectAddress;		//项目地址
    private Dict status;				//项目状态
    private Dict projectType;			//项目类别
    private String buildLicense;		//施工许可证
    private String safetyReportNumber;	//安全报监编号
    private String qualityReportNumber;	//质量报监编号
    private String supervisoryAgency;	//监督机构
    private Area area;

    private Company company;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getShortTitle() {
        return shortTitle;
    }

    public void setShortTitle(String shortTitle) {
        this.shortTitle = shortTitle;
    }

    public String getProjectAddress() {
        return projectAddress;
    }

    public void setProjectAddress(String projectAddress) {
        this.projectAddress = projectAddress;
    }

    public Dict getStatus() {
        return status;
    }

    public void setStatus(Dict status) {
        this.status = status;
    }

    public Dict getProjectType() {
        return projectType;
    }

    public void setProjectType(Dict projectType) {
        this.projectType = projectType;
    }

    public String getBuildLicense() {
        return buildLicense;
    }

    public void setBuildLicense(String buildLicense) {
        this.buildLicense = buildLicense;
    }

    public String getSafetyReportNumber() {
        return safetyReportNumber;
    }

    public void setSafetyReportNumber(String safetyReportNumber) {
        this.safetyReportNumber = safetyReportNumber;
    }

    public String getQualityReportNumber() {
        return qualityReportNumber;
    }

    public void setQualityReportNumber(String qualityReportNumber) {
        this.qualityReportNumber = qualityReportNumber;
    }

    public String getSupervisoryAgency() {
        return supervisoryAgency;
    }

    public void setSupervisoryAgency(String supervisoryAgency) {
        this.supervisoryAgency = supervisoryAgency;
    }

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    public Area getArea() {
        return area;
    }

    public void setArea(Area area) {
        this.area = area;
    }

    @Override
    public String toString() {
        return title == null ? "" : title;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Project project = (Project) o;

        return id == project.id;
    }

    @Override
    public int hashCode() {
        return (int) (id ^ (id >>> 32));
    }
}
