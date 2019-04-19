package com.hujiang.hujiangapp.model;

import com.blankj.utilcode.util.StringUtils;
import com.hujiang.hujiangapp.misc.MyUtils;


public class Hire  {
    private long id;
    private String workNumber;
    private Project project;          	//所属项目
    private BuildCompany buildCompany;	//所属参建单位
    private Team team;             		//所属班组
    private boolean isLeader;       	//是否组长
    private WorkType workType;       	//从事工种

    private Area area;

    private String title;            	//人员名称
    private String idNumber;        	//身份证号

    private String nation;            	//民族
    private Dict sex;                	//性别
    private String dateOfBirth;           //出生日期
    private String address;             //身份证地址
    private String idAgency;			//身份证签发机关
    private String idValiddate;			//身份证有效期

    private String phone;               //联系电话
    private String telephone;           //手机号码

    private String bankCode;			//银行账号
    private Dict bank;					//开户行

    private String entranceDate; 			//入场日期

    private ImageResource facePhoto;    //人脸照片
    private ImageResource idPhotoFront;    //身份证正面
    private ImageResource idPhotoRear;    //身份证反面
    private ImageResource bankPhotoRear;    //银行卡照片

    private boolean contract = false;               //简易劳动合同上传状态
    private boolean entrance = false;               //工人进场承诺书上传状态
    private boolean exit = false;                   //工人退场承诺书上传状态
    private boolean workConfirm = false;            ///两制“工作”确认书上传状态
    private boolean iDCardPDF = false;              //身份证正反面文件上传状态


    private Dict empCategory;                       //人员类型
    private Dict jobTypeName;                       //岗位

    public String getWorkNumber() {
        return workNumber;
    }

    public void setWorkNumber(String workNumber) {
        this.workNumber = workNumber;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public BuildCompany getBuildCompany() {
        return buildCompany;
    }

    public void setBuildCompany(BuildCompany buildCompany) {
        this.buildCompany = buildCompany;
    }

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }

    public boolean isLeader() {
        return isLeader;
    }

    public void setLeader(boolean leader) {
        isLeader = leader;
    }

    public WorkType getWorkType() {
        return workType;
    }

    public void setWorkType(WorkType workType) {
        this.workType = workType;
    }

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

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getBankCode() {
        return bankCode;
    }

    public void setBankCode(String bankCode) {
        this.bankCode = bankCode;
    }

    public ImageResource getFacePhoto() {
        return facePhoto;
    }

    public void setFacePhoto(ImageResource facePhoto) {
        this.facePhoto = facePhoto;
    }

    public ImageResource getIdPhotoFront() {
        return idPhotoFront;
    }

    public void setIdPhotoFront(ImageResource idPhotoFront) {
        this.idPhotoFront = idPhotoFront;
    }

    public ImageResource getIdPhotoRear() {
        return idPhotoRear;
    }

    public void setIdPhotoRear(ImageResource idPhotoRear) {
        this.idPhotoRear = idPhotoRear;
    }

    public ImageResource getBankPhotoRear() {
        return bankPhotoRear;
    }

    public void setBankPhotoRear(ImageResource bankPhotoRear) {
        this.bankPhotoRear = bankPhotoRear;
    }

    public Area getArea() {
        return area;
    }

    public void setArea(Area area) {
        this.area = area;
    }

    public String getNation() {
        return nation;
    }

    public void setNation(String nation) {
        this.nation = nation;
    }

    public Dict getSex() {
        return sex;
    }

    public void setSex(Dict sex) {
        this.sex = sex;
    }

    public String getDateOfBirth() {
        if (!StringUtils.isEmpty(dateOfBirth) && !dateOfBirth.contains(" ")) {
            dateOfBirth = dateOfBirth + " 00:00:00";
        }
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = MyUtils.convertIdCardBirthDate(dateOfBirth);
    }

    public String getBirthdayNoTime() {
        return MyUtils.stripTimeFromDate(getDateOfBirth());
    }

    public String getEntranceDate() {
        if (!StringUtils.isEmpty(entranceDate) && !entranceDate.contains(" ")) {
            entranceDate = entranceDate + " 00:00:00";
        }
        return entranceDate;
    }

    public void setEntranceDate(String entranceDate) {
        this.entranceDate = MyUtils.convertIdCardBirthDate(entranceDate);
    }

    public String getEntranceDateNoTime() {
        return MyUtils.stripTimeFromDate(getEntranceDate());
    }

    public Dict getBank() {
        return bank;
    }

    public void setBank(Dict bank) {
        this.bank = bank;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getIdAgency() {
        return idAgency;
    }

    public void setIdAgency(String idAgency) {
        this.idAgency = idAgency;
    }

    public String getIdValiddate() {
        return idValiddate;
    }

    public void setIdValiddate(String idValiddate) {
        this.idValiddate = idValiddate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Hire hire = (Hire) o;

        return id == hire.id;
    }

    @Override
    public int hashCode() {
        return (int) (id ^ (id >>> 32));
    }

    public boolean isContract() {
        return contract;
    }

    public void setContract(boolean contract) {
        this.contract = contract;
    }

    public boolean isEntrance() {
        return entrance;
    }

    public void setEntrance(boolean entrance) {
        this.entrance = entrance;
    }

    public boolean isExit() {
        return exit;
    }

    public void setExit(boolean exit) {
        this.exit = exit;
    }

    public boolean isWorkConfirm() {
        return workConfirm;
    }

    public void setWorkConfirm(boolean workConfirm) {
        this.workConfirm = workConfirm;
    }

    public boolean isiDCardPDF() {
        return iDCardPDF;
    }

    public void setiDCardPDF(boolean iDCardPDF) {
        this.iDCardPDF = iDCardPDF;
    }

    public Dict getEmpCategory() {
        return empCategory;
    }

    public void setEmpCategory(Dict empCategory) {
        this.empCategory = empCategory;
    }

    public Dict getJobTypeName() {
        return jobTypeName;
    }

    public void setJobTypeName(Dict jobTypeName) {
        this.jobTypeName = jobTypeName;
    }
}
