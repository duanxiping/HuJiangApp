package com.hujiang.hujiangapp.activity;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.hujiang.hujiangapp.R;
import com.hujiang.hujiangapp.api.ApiService;
import com.hujiang.hujiangapp.api.HuJiangServer;
import com.hujiang.hujiangapp.event.ProjectChangedEvent;
import com.hujiang.hujiangapp.misc.MyConstants;
import com.hujiang.hujiangapp.misc.MyUiUtils;
import com.hujiang.hujiangapp.model.ApiResp;
import com.hujiang.hujiangapp.model.Company;
import com.hujiang.hujiangapp.model.Dict;
import com.hujiang.hujiangapp.model.FaceLog;
import com.hujiang.hujiangapp.model.FaceLogNumber;
import com.hujiang.hujiangapp.model.Page;
import com.hujiang.hujiangapp.model.Project;
import com.hujiang.hujiangapp.model.User;
import com.hujiang.hujiangapp.shared.RegisterData;
import com.hujiang.hujiangapp.shared.SessionManager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.qqtheme.framework.picker.OptionPicker;
import cn.qqtheme.framework.picker.SinglePicker;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.RuntimePermissions;
import retrofit2.Call;

@RuntimePermissions
public class MainActivity extends BaseActivity {
    @BindView(R.id.textViewProject)
    protected TextView textViewProject;

    @BindView(R.id.textViewCompany)
    protected TextView textViewCompany;

    @BindView(R.id.funcLayout1)
    protected View funcLayout1;
    @BindView(R.id.funcImageView1)
    protected ImageView funcImageView1;
    @BindView(R.id.funcTextView1)
    protected TextView funcTextView1;

    @BindView(R.id.funcLayout2)
    protected View funcLayout2;
    @BindView(R.id.funcImageView2)
    protected ImageView funcImageView2;
    @BindView(R.id.funcTextView2)
    protected TextView funcTextView2;

    @BindView(R.id.funcLayout3)
    protected View funcLayout3;
    @BindView(R.id.funcImageView3)
    protected ImageView funcImageView3;
    @BindView(R.id.funcTextView3)
    protected TextView funcTextView3;

    @BindView(R.id.attendHistoryLayout)
    protected View attendHistoryLayout;

    @BindView(R.id.attendCountTextView)
    protected TextView attendCountTextView;

    @BindView(R.id.settingsImageButton)
    protected ImageButton settingsImageButton;

    private boolean isProjectUser = false;
    private boolean isDestroyed = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);
        EventBus.getDefault().register(this);

        setupUserFunctions();
        updateAttendStatus(0);

        if (SessionManager.shared().getCurrentProject() == null) {
            loadProjects(false);
        } else {
            setupProjectInfo();
            askPermissionToGetSerial();
            loadProjects(true);
        }
        MainActivityPermissionsDispatcher.permissionDisCameraWithPermissionCheck(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isDestroyed = true;
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        checkAuthority();
        fetchAttendCount();

    }


    @Override
    protected boolean showToolbar() {
        return false;
    }

    private void checkAuthority(){
        User user = SessionManager.shared().getCurrentUser();
        if (user.isAuthority()){
            //全部显示
            this.attendHistoryLayout.setVisibility(View.VISIBLE);
            this.funcLayout1.setVisibility(View.VISIBLE);
            this.funcLayout2.setVisibility(View.VISIBLE);
            this.funcLayout3.setVisibility(View.VISIBLE);

        }else{
            //只显示"人脸考勤"
            this.attendHistoryLayout.setVisibility(View.INVISIBLE);
            this.funcLayout1.setVisibility(View.INVISIBLE);
            this.funcLayout2.setVisibility(View.VISIBLE);
            this.funcLayout3.setVisibility(View.INVISIBLE);
        }
    }

    private void fetchAttendCount() {
        User user = SessionManager.shared().getCurrentUser();
        if (user == null) {
            return;
        }

        ApiService apiService = HuJiangServer.createService();
        Call<ApiResp<FaceLogNumber>> call = apiService.queryFaceLogNumber();

        startApiCall(call, new ApiResultHandler<FaceLogNumber>() {
            @Override
            boolean showLoading() {
                return false;
            }

            @Override
            boolean showError() {
                return false;
            }

            @Override
            void apiSucceed(FaceLogNumber faceLogNumber) {
                if (isDestroyed) {
                    return;
                }
                updateAttendStatus(faceLogNumber.getSize());
            }
        });
    }

    private void updateAttendStatus(int count) {
        attendCountTextView.setText(getString(R.string.today_attend_format, count));
    }

    private void loadProjects(final boolean isCheckingStatus) {
        User user = SessionManager.shared().getCurrentUser();
        if (user == null) {
            MyUiUtils.signOut(this);
            return;
        }

        ApiService apiService = HuJiangServer.createService();
        final Call<ApiResp<List<Project>>> call = apiService.queryProjects(user.getId());

        startApiCall(call, new ApiResultHandler<List<Project>>() {
            @Override
            String defaultErrorMessage() {
                return getString(R.string.load_projects_failed);
            }

            @Override
            boolean showLoading() {
                return !isCheckingStatus;
            }

            @Override
            void apiFailed() {
                MyUiUtils.signOut(MainActivity.this);
            }

            @Override
            void apiSucceed(List<Project> projects) {
                if (isDestroyed) {
                    return;
                }
                
                if (!isCheckingStatus) {
                    checkUserProjects(projects);
                }
            }
        });
    }

    private void checkUserProjects(List<Project> projectList) {
        if (projectList == null || projectList.size() == 0) {
            MyUiUtils.signOut(this);
            return;
        }

        SessionManager.shared().setCurrentProject(projectList.get(0));
        setupProjectInfo();

        askPermissionToGetSerial();

        if (1 < projectList.size()) {
            MyUiUtils.showPicker(this, projectList, 0, new SinglePicker.OnItemPickListener<Project>() {
                @Override
                public void onItemPicked(int index, Project item) {
                    SessionManager.shared().setCurrentProject(item);
                    setupProjectInfo();
                }
            });
        }
    }

    private void askPermissionToGetSerial() {
        if (!SessionManager.shared().isSerialOK()) {
            MainActivityPermissionsDispatcher.prepareDeviceSerialWithPermissionCheck(this);
        }
    }

    private void setupProjectInfo() {
        Project project = SessionManager.shared().getCurrentProject();
        if (project != null) {
            textViewProject.setText(project.getTitle());

            Company company = project.getCompany();
            if (company != null) {
                textViewCompany.setText(company.getTitle());
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onProjectChangedEvent(ProjectChangedEvent event) {
        setupProjectInfo();
    }

    private void setupUserFunctions() {
        User user = SessionManager.shared().getCurrentUser();
        if (user == null) {
            MyUiUtils.signOut(this);
            return;
        }

        isProjectUser = user.isProjectUser();
        if (!isProjectUser) {
            funcImageView1.setImageResource(R.drawable.btn_attend);
            funcTextView1.setText(R.string.face_attend);

            funcLayout2.setVisibility(View.INVISIBLE);
            funcLayout3.setVisibility(View.INVISIBLE);

            attendHistoryLayout.setVisibility(View.INVISIBLE);
        }
    }

    @OnClick(R.id.funcLayout1)
    protected void funcLayout1Clicked(View view) {
        if (preventDoubleClick()) {
            return;
        }

        if (isProjectUser) {
            registerUser();
        } else {
            faceAttend();
        }
    }

    @OnClick(R.id.funcLayout2)
    protected void funcLayout2Clicked(View view) {
        if (preventDoubleClick()) {
            return;
        }

        faceAttend();
    }

    @OnClick(R.id.funcLayout3)
    protected void funcLayout3Clicked(View view) {
        if (preventDoubleClick()) {
            return;
        }

        workerInfo();
    }

    private void registerUser() {
        RegisterData.shared().clearAllData();
        MainActivityPermissionsDispatcher.startCropIdFrontWithPermissionCheck(this);
    }

    private void faceAttend() {
        RegisterData.shared().clearAllData();

        MyUiUtils.showOptionPicker(this, getResources().getStringArray(R.array.attend_options), 0, new OptionPicker.OnOptionPickListener() {
            @Override
            public void onOptionPicked(int index, String item) {
                if (index == 0) {
                    SessionManager.shared().setAttendInOut(Dict.attendIn());
                } else {
                    SessionManager.shared().setAttendInOut(Dict.attendOut());
                }
                MainActivityPermissionsDispatcher.startFaceAttendWithPermissionCheck(MainActivity.this);
            }
        });
    }

    private void workerInfo() {
        RegisterData.shared().clearAllData();
        startActivity(new Intent(this, WorkerListActivity.class));
    }

    @OnClick(R.id.attendHistoryLayout)
    protected void attendHistoryLayoutClicked(View view) {
        if (preventDoubleClick()) {
            return;
        }
        startActivity(new Intent(this, AttendHistoryActivity.class));
    }

    @OnClick(R.id.settingsImageButton)
    protected void settingsImageButtonClicked(View view) {
        if (preventDoubleClick()) {
            return;
        }
        startActivity(new Intent(this, SettingsActivity.class));
    }

    @NeedsPermission(Manifest.permission.CAMERA)
    protected void startCropIdFront() {
        MyUiUtils.showCardCameraActivity(this, MyConstants.ScanType.IdCardFront);
    }

    @NeedsPermission(Manifest.permission.CAMERA)
    protected void startFaceAttend() {
        MyUiUtils.showFaceDetectActivity(this, SessionManager.shared().isUseFrontCamera());
    }

    @NeedsPermission(Manifest.permission.READ_PHONE_STATE)
    protected void prepareDeviceSerial() {
        SessionManager.shared().prepareDeviceSerial(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // NOTE: delegate the permission handling to generated method
        MainActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }

    @NeedsPermission({Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE})
    void permissionDisCamera() {
    }
}
