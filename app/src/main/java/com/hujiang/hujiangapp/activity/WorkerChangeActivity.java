package com.hujiang.hujiangapp.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.InputType;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.blankj.utilcode.util.StringUtils;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.hujiang.hujiangapp.R;
import com.hujiang.hujiangapp.api.ApiService;
import com.hujiang.hujiangapp.api.HuJiangServer;
import com.hujiang.hujiangapp.event.UpdateHireEvent;
import com.hujiang.hujiangapp.misc.CameraUtils;
import com.hujiang.hujiangapp.misc.Constance;
import com.hujiang.hujiangapp.misc.CreatePictureDialog;
import com.hujiang.hujiangapp.misc.FileUtils;
import com.hujiang.hujiangapp.misc.MyConstants;
import com.hujiang.hujiangapp.misc.MyUiUtils;
import com.hujiang.hujiangapp.misc.MyUtils;
import com.hujiang.hujiangapp.misc.ObservableUtils;
import com.hujiang.hujiangapp.misc.PermissionManager;
import com.hujiang.hujiangapp.model.ApiResp;
import com.hujiang.hujiangapp.model.BuildCompany;
import com.hujiang.hujiangapp.model.Dict;
import com.hujiang.hujiangapp.model.Hire;
import com.hujiang.hujiangapp.model.ImageResource;
import com.hujiang.hujiangapp.model.Project;
import com.hujiang.hujiangapp.model.Team;
import com.hujiang.hujiangapp.model.User;
import com.hujiang.hujiangapp.shared.RegisterData;
import com.hujiang.hujiangapp.shared.SessionManager;
import com.kyleduo.switchbutton.SwitchButton;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.List;

import butterknife.BindView;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import cn.qqtheme.framework.picker.DatePicker;
import cn.qqtheme.framework.picker.SinglePicker;
import es.dmoral.toasty.Toasty;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.RuntimePermissions;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;
import retrofit2.Call;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

@RuntimePermissions
public class WorkerChangeActivity extends BaseActivity  {

    @BindView(R.id.bankLayout)
    protected View bankLayout;

    @BindView(R.id.faceImageView)
    protected ImageView faceImageView;
    @BindView(R.id.idFrontImageView)
    protected ImageView idFrontImageView;
    @BindView(R.id.idBackImageView)
    protected ImageView idBackImageView;
    @BindView(R.id.bankImageView)
    protected ImageView bankImageView;

    @BindView(R.id.idNoTextView)
    protected TextView idNoTextView;
    @BindView(R.id.nameTextView)
    protected TextView nameTextView;
    @BindView(R.id.sexTextView)
    protected TextView sexTextView;
    @BindView(R.id.nationTextView)
    protected TextView nationTextView;
    @BindView(R.id.birthdayTextView)
    protected TextView birthdayTextView;
    @BindView(R.id.addressTextView)
    protected TextView addressTextView;

    @BindView(R.id.idIssuerTextView)
    protected TextView idIssuerTextView;
    @BindView(R.id.idDateTextView)
    protected TextView idDateTextView;

    @BindView(R.id.bankNoTextView)
    protected TextView bankNoTextView;
    @BindView(R.id.bankNameTextView)
    protected TextView bankNameTextView;

    @BindView(R.id.projectTextView)
    protected TextView projectTextView;
    @BindView(R.id.companyTextView)
    protected TextView companyTextView;
    @BindView(R.id.teamTextView)
    protected TextView teamTextView;
    @BindView(R.id.workTypeTextView)
    protected TextView workTypeTextView;
    @BindView(R.id.entranceDateTextView)
    protected TextView entranceDateTextView;
    @BindView(R.id.phoneTextView)
    protected TextView phoneTextView;

    @BindView(R.id.leaderSwitch)
    protected SwitchButton leaderSwitch;

    @BindView(R.id.contractSwitch)
    protected SwitchButton contractSwitch;

    @BindView(R.id.entranceSwitch)
    protected SwitchButton entranceSwitch;

    @BindView(R.id.exitSwitch)
    protected SwitchButton exitSwitch;

    @BindView(R.id.workConfirmSwitch)
    protected SwitchButton workConfirmSwitch;

    @BindView(R.id.iDCardPDFSwitch)
    protected SwitchButton iDCardPDFSwitch;

    @BindView(R.id.empCategoryTextView)
    protected TextView empCategoryTextView;
    @BindView(R.id.jobTypeNameTextView)
    protected TextView jobTypeNameTextView;

    private Hire hire;

    private List<Project> loadedProjects;
    private List<BuildCompany> loadedCompanies;
    private List<Team> loadedTeams;
    private List<Dict> loadedEmpCategories;
    private List<Dict> loadedJobTypeNames;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_worker_change);
        WorkerChangeActivityPermissionsDispatcher.permissCameraWithPermissionCheck(this);
        String json = getIntent().getStringExtra(MyConstants.ExtraHireJson);
        if (!StringUtils.isEmpty(json)) {
            Gson gson = new Gson();
            hire = gson.fromJson(json, Hire.class);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        setupImages();
        setupFrontInfo();
        setupBackInfo();
        setupBankInfo();
        setupAdditionalInfo();
    }

    private void setupImages() {
        if (hire == null) {
            return;
        }

        RegisterData data = RegisterData.shared();

        if (data.faceResource != null) {
            hire.setFacePhoto(data.faceResource);
        }
        if (data.idFrontResource != null) {
            hire.setIdPhotoFront(data.idFrontResource);
        }
        if (data.idBackResource != null) {
            hire.setIdPhotoRear(data.idBackResource);
        }
        if (data.bankResource != null) {
            hire.setBankPhotoRear(data.bankResource);
        }

        loadImage(hire.getFacePhoto(), faceImageView);
        loadImage(hire.getIdPhotoFront(), idFrontImageView);
        loadImage(hire.getIdPhotoRear(), idBackImageView);
        loadImage(hire.getBankPhotoRear(), bankImageView);
    }

    private void setupFrontInfo() {
        if (hire == null) {
            return;
        }

        RegisterData data = RegisterData.shared();
        if (data.idFrontInfo != null) {
            hire.setTitle(data.idFrontInfo.getTitle());
            hire.setIdNumber(data.idFrontInfo.getIdNumber());
            hire.setSex(data.idFrontInfo.getSexDict());
            hire.setNation(data.idFrontInfo.getNation());
            hire.setDateOfBirth(data.idFrontInfo.getDateOfBirth());
            hire.setAddress(data.idFrontInfo.getAddress());
        }

        nameTextView.setText(hire.getTitle());
        idNoTextView.setText(hire.getIdNumber());
        sexTextView.setText(hire.getSex() == null ? "" : hire.getSex().getText());
        nationTextView.setText(hire.getNation());
        birthdayTextView.setText(hire.getBirthdayNoTime());
        addressTextView.setText(hire.getAddress());
    }

    private void setupBackInfo() {
        if (hire == null) {
            return;
        }

        RegisterData data = RegisterData.shared();
        if (data.idBackInfo != null) {
            hire.setIdAgency(data.idBackInfo.getIssue());
            hire.setIdValiddate(data.idBackInfo.getIdCardValiditydate());
        }

        idIssuerTextView.setText(hire.getIdAgency());
        idDateTextView.setText(hire.getIdValiddate());
    }

    private void setupBankInfo() {
        if (hire == null) {
            return;
        }

        RegisterData data = RegisterData.shared();
        if (data.bankInfo != null) {
            hire.setBankCode(data.bankInfo.getCard_num());
        }
        if (data.bankDict != null) {
            hire.setBank(data.bankDict);
        }

        bankNoTextView.setText(hire.getBankCode());
        bankNameTextView.setText(hire.getBank() == null ? "" : hire.getBank().getText());
    }

    private void setupAdditionalInfo() {
        if (hire == null) {
            return;
        }

        projectTextView.setText(hire.getProject() == null ? "" : hire.getProject().getTitle());
        companyTextView.setText(hire.getBuildCompany() == null ? "" : hire.getBuildCompany().getTitle());
        teamTextView.setText(hire.getTeam() == null ? "" : hire.getTeam().getTitle());
        workTypeTextView.setText(hire.getWorkType() == null ? "" : hire.getWorkType().getTitle());
        phoneTextView.setText(hire.getTelephone());
        entranceDateTextView.setText(hire.getEntranceDateNoTime());

        leaderSwitch.setChecked(hire.isLeader());

        contractSwitch.setChecked(hire.isContract());
        entranceSwitch.setChecked(hire.isEntrance());
        exitSwitch.setChecked(hire.isExit());
        workConfirmSwitch.setChecked(hire.isWorkConfirm());
        iDCardPDFSwitch.setChecked(hire.isiDCardPDF());
        empCategoryTextView.setText(hire.getEmpCategory() == null ? "" : hire.getEmpCategory().getText());
        jobTypeNameTextView.setText(hire.getJobTypeName() == null ? "" : hire.getJobTypeName().getText());

    }

    private void loadImage(ImageResource imageResource, ImageView imageView) {
        if (imageResource != null && !StringUtils.isEmpty(imageResource.getUrl())) {
            Glide.with(this).load(imageResource.getUrl()).into(imageView);
        }
    }

//    @Override
//    protected void onStart() {
//        super.onStart();
//        File imageFile = null;
//        imageFile = RegisterData.shared().faceImage;
//        if (imageFile != null) {
//            Glide.with(this).load(imageFile).into(faceImageView);
//        }
//    }

    @OnClick(R.id.faceImageView)
    protected void changeIdFaceImageInfo(){
        if (preventDoubleClick()) {
            return;
        }
        MyUiUtils.changeFaceDetectActivity(this,true,true,getIntent().getStringExtra(MyConstants.ExtraHireJson));
       // MyUiUtils.changeCardCameraActivity(this, MyConstants.ScanType.IdCardFront, true);


    }

    @OnClick({R.id.idFrontImageView, R.id.idNoLayout, R.id.nameLayout, R.id.sexLayout, R.id.nationLayout, R.id.birthdayLayout, R.id.addressLayout})
    protected void changeIdFrontInfo() {
        if (preventDoubleClick()) {
            return;
        }
        MyUiUtils.showCardCameraActivity(this, MyConstants.ScanType.IdCardFront);
    }

    @OnClick({R.id.idBackImageView, R.id.idIssuerLayout, R.id.idDateLayout})
    protected void changeIdBackInfo() {
        if (preventDoubleClick()) {
            return;
        }
        MyUiUtils.showCardCameraActivity(this, MyConstants.ScanType.IdCardBack);
    }

    @OnClick({R.id.bankImageView, R.id.bankNoLayout, R.id.bankNameLayout})
    protected void changeBankInfo() {
        if (preventDoubleClick()) {
            return;
        }
        MyUiUtils.showCardCameraActivity(this, MyConstants.ScanType.BankCard);
    }

    @OnClick(R.id.projectLayout)
    protected void projectLayoutClicked(View view) {
        if (preventDoubleClick()) {
            return;
        }
        fetchMyProjects();
    }

    @OnClick(R.id.companyLayout)
    protected void companyLayoutClicked(View view) {
        if (preventDoubleClick()) {
            return;
        }
        fetchBuildCompany();
    }

    @OnClick(R.id.teamLayout)
    protected void teamLayoutClicked(View view) {
        if (preventDoubleClick()) {
            return;
        }
        fetchTeam();
    }

    @OnClick(R.id.empCategoryLayout)
    protected void empCategoryLayoutClicked(View view) {
        if (preventDoubleClick()) {
            return;
        }
        fetchEmpCategory();
    }

    @OnClick(R.id.jobTypeNameLayout)
    protected void jobTypeNameLayoutClicked(View view) {
        if (preventDoubleClick()) {
            return;
        }
        fetchJobTypeName();
    }

    @OnClick(R.id.workTypeLayout)
    protected void workTypeLayoutClicked(View view) {
        if (preventDoubleClick()) {
            return;
        }

        RegisterData.shared().workType = hire.getWorkType();
        Intent intent = new Intent(this, WorkTypeActivity.class);
        startActivityForResult(intent, MyConstants.RequestCodeWorkType);
    }

    @OnClick(R.id.entranceDateLayout)
    protected void entranceDateLayoutClicked(View view) {
        if (preventDoubleClick()) {
            return;
        }

        String entranceDate = hire.getEntranceDateNoTime();
        MyUiUtils.showYearMonthDayPicker(this, entranceDate, new DatePicker.OnYearMonthDayPickListener() {
            @Override
            public void onDatePicked(String year, String month, String day) {
                hire.setEntranceDate(year + "-" + month + "-" + day);
                setupAdditionalInfo();
            }
        });
    }

    @OnClick(R.id.phoneLayout)
    protected void phoneLayoutClicked(View view) {
        if (preventDoubleClick()) {
            return;
        }

        new MaterialDialog.Builder(this)
                .iconRes(R.drawable.icon_info)
                .title(R.string.please_input_phone)
                .input(getString(R.string.please_input_phone), hire.getTelephone(), new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {
                        if (MyUtils.isValidMobile(input.toString())) {
                            hire.setTelephone(input.toString());
                            hire.setPhone(input.toString());
                            setupAdditionalInfo();
                        } else {
                            Toasty.warning(WorkerChangeActivity.this, R.string.please_input_phone).show();
                        }
                    }
                })
                .inputType(InputType.TYPE_CLASS_PHONE)
                .negativeText(R.string.cancel)
                .positiveText(R.string.confirm)
                .show();
    }

    @OnCheckedChanged(R.id.leaderSwitch)
    protected void leaderSwitchCheckedChanged(SwitchButton button) {
        hire.setLeader(button.isChecked());
        setupAdditionalInfo();
    }

    @OnCheckedChanged(R.id.contractSwitch)
    protected void contractSwitchCheckedChanged(SwitchButton button) {
        hire.setContract(button.isChecked());
        setupAdditionalInfo();
    }
    @OnCheckedChanged(R.id.exitSwitch)
    protected void exitSwitchCheckedChanged(SwitchButton button) {
        hire.setExit(button.isChecked());
        setupAdditionalInfo();
    }
    @OnCheckedChanged(R.id.entranceSwitch)
    protected void entranceSwitchCheckedChanged(SwitchButton button) {
        hire.setEntrance(button.isChecked());
        setupAdditionalInfo();
    }
    @OnCheckedChanged(R.id.workConfirmSwitch)
    protected void workConfirmSwitchCheckedChanged(SwitchButton button) {
        hire.setWorkConfirm(button.isChecked());
        setupAdditionalInfo();
    }
    @OnCheckedChanged(R.id.iDCardPDFSwitch)
    protected void iDCardPDFSwitchCheckedChanged(SwitchButton button) {
        hire.setiDCardPDF(button.isChecked());
        setupAdditionalInfo();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == MyConstants.RequestCodeWorkType) {
            if (RegisterData.shared().workType != null) {
                hire.setWorkType(RegisterData.shared().workType);
            }
            setupAdditionalInfo();
        }

        /*switch (requestCode) {
            //当从软件设置界面，返回当前程序时候
            case AppSettingsDialog.DEFAULT_SETTINGS_REQ_CODE:
                break;
            //拍照返回
            case Constance.PICTURE_CODE:
                if (resultCode == Activity.RESULT_OK) {
                    loadPictureBitmap();
                }
                break;
            //图库返回
            case Constance.GALLERY_CODE:
                if (resultCode == Activity.RESULT_OK) {
                    Uri uri = data.getData();
                    loadGalleryBitmap(uri);
                }
                break;
            default:

                break;
        }*/
    }

    private void fetchMyProjects() {
        if (loadedProjects != null && 0 < loadedProjects.size()) {
            showProjectPicker(loadedProjects);
            return;
        }

        User user = SessionManager.shared().getCurrentUser();
        if (user == null) {
            return;
        }

        ApiService apiService = HuJiangServer.createService();
        final Call<ApiResp<List<Project>>> call = apiService.queryProjects(user.getId());

        startApiCall(call, new ApiResultHandler<List<Project>>() {
            @Override
            String defaultErrorMessage() {
                return getString(R.string.loading_failed);
            }

            @Override
            void apiSucceed(List<Project> projectList) {
                loadedProjects = projectList;
                showProjectPicker(projectList);
            }
        });
    }

    private void showProjectPicker(List<Project> projectList) {
        int index = 0;
        if (hire.getProject() != null) {
            index = projectList.indexOf(hire.getProject());
        }
        MyUiUtils.showPicker(WorkerChangeActivity.this, projectList, index, new SinglePicker.OnItemPickListener<Project>() {
            @Override
            public void onItemPicked(int index, Project item) {
                hire.setProject(item);
                setupAdditionalInfo();
            }
        });
    }

    private void fetchBuildCompany() {
        if (loadedCompanies != null && 0 < loadedCompanies.size()) {
            showCompanyPicker(loadedCompanies);
            return;
        }

        Project project = SessionManager.shared().getCurrentProject();
        if (project == null) {
            return;
        }

        ApiService apiService = HuJiangServer.createService();
        Call<ApiResp<List<BuildCompany>>> call = apiService.queryBuildCompany(project.getId());

        startApiCall(call, new ApiResultHandler<List<BuildCompany>>() {
            @Override
            String defaultErrorMessage() {
                return getString(R.string.loading_failed);
            }

            @Override
            void apiSucceed(List<BuildCompany> companyList) {
                loadedCompanies = companyList;
                showCompanyPicker(companyList);
            }
        });
    }

    private void showCompanyPicker(List<BuildCompany> companyList) {
        int index = 0;
        if (hire.getBuildCompany() != null) {
            index = companyList.indexOf(hire.getBuildCompany());
        }
        MyUiUtils.showPicker(WorkerChangeActivity.this, companyList, index, new SinglePicker.OnItemPickListener<BuildCompany>() {
            @Override
            public void onItemPicked(int index, BuildCompany item) {
                hire.setBuildCompany(item);
                setupAdditionalInfo();
            }
        });
    }



    private void fetchTeam() {
        if (loadedTeams != null && 0 < loadedTeams.size()) {
            showTeamPicker(loadedTeams);
            return;
        }

        Project project = SessionManager.shared().getCurrentProject();
        if (project == null) {
            return;
        }

        ApiService apiService = HuJiangServer.createService();
        Call<ApiResp<List<Team>>> call = apiService.queryTeam(project.getId());

        startApiCall(call, new ApiResultHandler<List<Team>>() {
            @Override
            String defaultErrorMessage() {
                return getString(R.string.loading_failed);
            }

            @Override
            void apiSucceed(List<Team> teamList) {
                loadedTeams = teamList;
                showTeamPicker(teamList);
            }
        });
    }

    private void showTeamPicker(List<Team> teamList) {
        int index = 0;
        if (hire.getTeam() != null) {
            index = teamList.indexOf(hire.getTeam());
        }
        MyUiUtils.showPicker(WorkerChangeActivity.this, teamList, index, new SinglePicker.OnItemPickListener<Team>() {
            @Override
            public void onItemPicked(int index, Team item) {
                hire.setTeam(item);
                setupAdditionalInfo();
            }
        });
    }


    private void fetchEmpCategory() {
        if (loadedEmpCategories != null && 0 < loadedEmpCategories.size()) {
            showEmpCategoryPicker(loadedEmpCategories);
            return;
        }


        ApiService apiService = HuJiangServer.createService();
        Call<ApiResp<List<Dict>>> call = apiService.queryDict(MyConstants.DictEmpCategory);

        startApiCall(call, new ApiResultHandler<List<Dict>>() {
            @Override
            String defaultErrorMessage() {
                return getString(R.string.loading_failed);
            }

            @Override
            void apiSucceed(List<Dict> teamList) {
                loadedEmpCategories = teamList;
                showEmpCategoryPicker(teamList);
            }
        });
    }

    private void showEmpCategoryPicker(List<Dict> teamList) {
        int index = 0;
        if (hire.getEmpCategory() != null) {
            index = teamList.indexOf(hire.getEmpCategory());
        }
        MyUiUtils.showPicker(WorkerChangeActivity.this, teamList, index, new SinglePicker.OnItemPickListener<Dict>() {
            @Override
            public void onItemPicked(int index, Dict item) {
                hire.setEmpCategory(item);
                setupAdditionalInfo();
            }
        });
    }


    private void fetchJobTypeName() {
        if (loadedJobTypeNames != null && 0 < loadedJobTypeNames.size()) {
            showJobTypeNamePicker(loadedJobTypeNames);
            return;
        }


        ApiService apiService = HuJiangServer.createService();
        Call<ApiResp<List<Dict>>> call = apiService.queryDict(MyConstants.DictJobTypeName);

        startApiCall(call, new ApiResultHandler<List<Dict>>() {
            @Override
            String defaultErrorMessage() {
                return getString(R.string.loading_failed);
            }

            @Override
            void apiSucceed(List<Dict> teamList) {
                loadedJobTypeNames = teamList;
                showJobTypeNamePicker(teamList);
            }
        });
    }

    private void showJobTypeNamePicker(List<Dict> teamList) {
        int index = 0;
        if (hire.getJobTypeName() != null) {
            index = teamList.indexOf(hire.getJobTypeName());
        }
        MyUiUtils.showPicker(WorkerChangeActivity.this, teamList, index, new SinglePicker.OnItemPickListener<Dict>() {
            @Override
            public void onItemPicked(int index, Dict item) {
                hire.setJobTypeName(item);
                setupAdditionalInfo();
            }
        });
    }

    @OnClick(R.id.nextButton)
    protected void nextButtonClicked(View view) {
        if (preventDoubleClick()) {
            return;
        }
        ApiService apiService = HuJiangServer.createService();
        Call<ApiResp<Hire>> call = apiService.saveHire(hire);

        startApiCall(call, new ApiResultHandler<Hire>() {
            @Override
            String defaultErrorMessage() {
                return getString(R.string.save_failed);
            }

            @Override
            void apiSucceed(Hire hire) {
                saveSucceed();
            }
        });
    }

    private void saveSucceed() {
        showAlertDialog(R.drawable.icon_success, getString(R.string.save_success));
        EventBus.getDefault().post(new UpdateHireEvent(hire));
    }

    @NeedsPermission({Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE})
    void permissCamera() {
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        WorkerChangeActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }

//    private static final String TAG = WorkerChangeActivity.class.getSimpleName();
//    private final CompositeSubscription compositeSubscription = new CompositeSubscription();
//    private ImageView show_iv;
//    private CreatePictureDialog picture_dialog;
//    /**
//     * 选择拍照或者图库的弹窗
//     */
//    private void showPictureDialog() {
//        if (picture_dialog == null) {
//            this.picture_dialog = new CreatePictureDialog(this);
//            this.picture_dialog.setResultListener(this);
//        }
//        if (!picture_dialog.isShowing()) {
//            picture_dialog.show();
//        }
//    }
//
//    private void dismissPictureDialog(){
//        if (picture_dialog != null && picture_dialog.isShowing()) {
//            picture_dialog.dismiss();
//        }
//    }
//
//    /**
//     * 释放资源：
//     * 关闭线程，dialog等
//     */
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        this.dismissPictureDialog();
//        this.compositeSubscription.clear();
//    }
//    /**
//     * 检查读写权限权限
//     */
//    private void checkWritePermission() {
//        boolean result = PermissionManager.checkPermission(this, Constance.PERMS_WRITE);
//        if (!result) {
//            PermissionManager.requestPermission(this, Constance.WRITE_PERMISSION_TIP, Constance.WRITE_PERMISSION_CODE, Constance.PERMS_WRITE);
//        }
//    }
//
//    /**
//     * 请求权限成功
//     *
//     * @param requestCode
//     * @param perms
//     */
//    @Override
//    public void onPermissionsGranted(int requestCode, List<String> perms) {
////        ToastUtils.showToast(getApplicationContext(), "用户授权成功");
//
//    }
//    /**
//     * 请求权限失败
//     *
//     * @param requestCode
//     * @param perms
//     */
//    @Override
//    public void onPermissionsDenied(int requestCode, List<String> perms) {
////        ToastUtils.showToast(getApplicationContext(), "用户授权失败");
//        /**
//         * 若是在权限弹窗中，用户勾选了'NEVER ASK AGAIN.'或者'不在提示'，且拒绝权限。
//         * 这时候，需要跳转到设置界面去，让用户手动开启。
//         */
//        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
//            new AppSettingsDialog.Builder(this).build().show();
//        }
//    }
//
//    private void loadGalleryBitmap(Uri uri) {
//        Observable<Bitmap> bitmapObservable= ObservableUtils.loadGalleryBitmap(getApplicationContext(),uri,show_iv);
//        executeObservableTask(bitmapObservable);
//    }
//    private void loadPictureBitmap() {
//        Observable<Bitmap> bitmapObservable= ObservableUtils.loadPictureBitmap(getApplicationContext(), picturePath, show_iv);
//        executeObservableTask(bitmapObservable);
//    }
//    private void executeObservableTask(Observable<Bitmap> observable) {
//        Subscription subscription = observable
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(bitmap ->
//                                show_iv.setImageBitmap(bitmap)
//                        , error ->
//                                //ToastUtils.showToast(getApplicationContext(), "加载图片出错")
//                                Toasty.error(this,"加载图片出错")
//                );
//        this.compositeSubscription.add(subscription);
//    }
//    private String picturePath;
//    @Override
//    public void camera() {
//        this.picturePath = FileUtils.getBitmapDiskFile(this.getApplicationContext());
//        CameraUtils.openCamera(this, Constance.PICTURE_CODE, this.picturePath);
//    }
//
//    @Override
//    public void photoAlbum() {
//
//        CameraUtils.openGallery(this, Constance.GALLERY_CODE);
//    }
//
//    /**
//     * 防止系统内存不足销毁Activity
//     * ,这里保存数据，便于恢复。
//     * @param outState
//     */
//    @Override
//    protected void onSaveInstanceState(Bundle outState) {
//        super.onSaveInstanceState(outState);
//        outState.putString(TAG, picturePath);
//    }

}
