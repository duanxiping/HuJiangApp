package com.hujiang.hujiangapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.InputType;
import android.view.View;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.hujiang.hujiangapp.R;
import com.hujiang.hujiangapp.api.ApiService;
import com.hujiang.hujiangapp.api.HuJiangServer;
import com.hujiang.hujiangapp.event.ProjectChangedEvent;
import com.hujiang.hujiangapp.misc.MyUiUtils;
import com.hujiang.hujiangapp.model.ApiResp;
import com.hujiang.hujiangapp.model.Empty;
import com.hujiang.hujiangapp.model.Project;
import com.hujiang.hujiangapp.model.User;
import com.hujiang.hujiangapp.shared.SessionManager;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.qqtheme.framework.picker.OptionPicker;
import cn.qqtheme.framework.picker.SinglePicker;
import es.dmoral.toasty.Toasty;
import retrofit2.Call;

public class SettingsActivity extends BaseActivity {
    @BindView(R.id.cameraTextView)
    protected TextView cameraTextView;

    @BindView(R.id.userTextView)
    protected TextView userTextView;

    @BindView(R.id.intervalTextView)
    protected TextView intervalTextView;

    @BindView(R.id.cellSyncWorker)
    protected View cellSyncWorker;

    @BindView(R.id.syncWorkerLine)
    protected View syncWorkerLine;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        ButterKnife.bind(this);

        updateCameraTextView();
        checkAuthority();
        updateTimeInterval();

        User user = SessionManager.shared().getCurrentUser();
        userTextView.setText(user == null ? null : user.getUserName());
    }

    private void updateCameraTextView() {
        cameraTextView.setText(SessionManager.shared().isUseFrontCamera() ? R.string.front_camera : R.string.back_camera);
    }

    private void checkAuthority() {
        User user = SessionManager.shared().getCurrentUser();
        if (user.isAuthority()) {
            cellSyncWorker.setVisibility(View.VISIBLE);
            syncWorkerLine.setVisibility(View.VISIBLE);
        } else {
            cellSyncWorker.setVisibility(View.GONE);
            syncWorkerLine.setVisibility(View.GONE);
        }
    }

    private void updateTimeInterval() {
        intervalTextView.setText(getString(R.string.time_seconds, SessionManager.shared().getAttendTimeInterval()));
    }

    @OnClick(R.id.signOutButton)
    protected void signOutButtonClicked(View view) {
        if (preventDoubleClick()) {
            return;
        }

        showAlertDialog(R.drawable.icon_warning, null, getString(R.string.sign_out_tip), getString(R.string.sign_out), new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                MyUiUtils.signOut(SettingsActivity.this);
            }
        }, getString(R.string.cancel), null, null);
    }

    @OnClick(R.id.cellProject)
    protected void cellProjectClicked(View view) {
        if (preventDoubleClick()) {
            return;
        }
        fetchMyProjects();
    }

    private void fetchMyProjects() {
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
            void apiSucceed(List<Project> projects) {
                showProjectsPicker(projects);
            }
        });
    }

    private void showProjectsPicker(List<Project> projectList) {
        Project project = SessionManager.shared().getCurrentProject();
        if (project == null) {
            MyUiUtils.signOut(this);
            return;
        }

        if (projectList == null || projectList.size() == 0) {
            return;
        }

        int index = projectList.indexOf(project);
        MyUiUtils.showPicker(this, projectList, index, new SinglePicker.OnItemPickListener<Project>() {
            @Override
            public void onItemPicked(int index, Project item) {
                projectChanged(item);
            }
        });
    }

    private void projectChanged(Project project) {
        SessionManager.shared().setCurrentProject(project);
        EventBus.getDefault().post(new ProjectChangedEvent());
    }

    @OnClick(R.id.cellCamera)
    protected void cellCameraClicked(View view) {
        if (preventDoubleClick()) {
            return;
        }

        int selectedIndex = SessionManager.shared().isUseFrontCamera() ? 0 : 1;
        MyUiUtils.showOptionPicker(this, getResources().getStringArray(R.array.face_camera), selectedIndex, new OptionPicker.OnOptionPickListener() {
            @Override
            public void onOptionPicked(int index, String item) {
                boolean useFront = (index == 0);
                SessionManager.shared().setUseFrontCamera(useFront);
                updateCameraTextView();
            }
        });
    }

    @OnClick(R.id.cellAboutUs)
    protected void cellAboutUsClicked(View view) {
        if (preventDoubleClick()) {
            return;
        }
        startActivity(new Intent(this, AboutUsActivity.class));
    }

    @OnClick(R.id.cellPassword)
    protected void cellPasswordClicked(View view) {
        if (preventDoubleClick()) {
            return;
        }
        startActivity(new Intent(this, ChangePasswordActivity.class));
    }

    @OnClick(R.id.cellInterval)
    protected void cellIntervalClicked(View view) {
        if (preventDoubleClick()) {
            return;
        }

        String interval = String.valueOf(SessionManager.shared().getAttendTimeInterval());
        new MaterialDialog.Builder(this)
                .iconRes(R.drawable.icon_info)
                .title(R.string.face_attend_time_interval)
                .input(getString(R.string.face_attend_time_interval), interval, new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {
                        try {
                            int seconds = Integer.parseInt(input.toString());
                            if (0 <= seconds) {
                                SessionManager.shared().setAttendTimeInterval(seconds);
                                updateTimeInterval();
                            }
                        } catch (Exception ex) {
                            // do nothing
                        }
                    }
                })
                .inputType(InputType.TYPE_CLASS_NUMBER)
                .negativeText(R.string.cancel)
                .positiveText(R.string.confirm)
                .show();
    }

    @OnClick(R.id.cellSyncWorker)
    protected void cellSyncWorkerClicked(View view) {
        if (preventDoubleClick()) {
            return;
        }

        ApiService apiService = HuJiangServer.createService();
        final Call<ApiResp<List<Empty>>> call = apiService.synchronizationHire();

        startApiCall(call, new ApiResultHandler<List<Empty>>() {
            @Override
            String defaultErrorMessage() {
                return getString(R.string.sync_worker_info_failed);
            }

            @Override
            void apiSucceed(List<Empty> empties) {
                Toasty.success(SettingsActivity.this, R.string.sync_worker_info_success).show();
            }
        });
    }
}
