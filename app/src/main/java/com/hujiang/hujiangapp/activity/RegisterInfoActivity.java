package com.hujiang.hujiangapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.blankj.utilcode.util.StringUtils;
import com.hujiang.hujiangapp.R;
import com.hujiang.hujiangapp.api.ApiService;
import com.hujiang.hujiangapp.api.HuJiangServer;
import com.hujiang.hujiangapp.event.BackToMainEvent;
import com.hujiang.hujiangapp.misc.MyConstants;
import com.hujiang.hujiangapp.misc.MyUiUtils;
import com.hujiang.hujiangapp.misc.MyUtils;
import com.hujiang.hujiangapp.model.ApiResp;
import com.hujiang.hujiangapp.model.BuildCompany;
import com.hujiang.hujiangapp.model.Dict;
import com.hujiang.hujiangapp.model.Project;
import com.hujiang.hujiangapp.model.Team;
import com.hujiang.hujiangapp.model.User;
import com.hujiang.hujiangapp.shared.RegisterData;
import com.hujiang.hujiangapp.shared.SessionManager;
import com.kyleduo.switchbutton.SwitchButton;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Logger;
import org.greenrobot.eventbus.Subscribe;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import cn.qqtheme.framework.picker.DatePicker;
import cn.qqtheme.framework.picker.SinglePicker;
import es.dmoral.toasty.Toasty;
import retrofit2.Call;

public class RegisterInfoActivity extends BaseActivity {
    
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


    private List<Project> loadedProjects;
    private List<BuildCompany> loadedCompanies;
    private List<Team> loadedTeams;
    private List<Dict> loadedEmpCategories;
    private List<Dict> loadedJobTypeNames;

    @BindView(R.id.empCategoryTextView)
    protected TextView empCategoryTextView;

    @BindView(R.id.jobTypeNameTextView)
    protected TextView jobTypeNameTextView;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_info);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);

        updateInfo();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe
    public void onBackToMainEvent(BackToMainEvent event) {
        finish();
    }

    private void updateInfo() {
        RegisterData data = RegisterData.shared();

        updateTextView(projectTextView, data.project == null ? null : data.project.getTitle(), R.string.please_select_project);
        updateTextView(companyTextView, data.buildCompany == null ? null : data.buildCompany.getTitle(), R.string.please_select_company);
        updateTextView(teamTextView, data.team == null ? null : data.team.getTitle(), R.string.please_select_team);
        updateTextView(workTypeTextView, data.workType == null ? null : data.workType.getTitle(), R.string.please_select_work_type);
        updateTextView(phoneTextView, data.phone, R.string.please_input_phone);
        updateTextView(entranceDateTextView, data.entranceDate, R.string.please_select_entrance_date);
        updateTextView(empCategoryTextView, data.empCategory ==  null ? null:data.empCategory.getText(), R.string.please_select_empCategory);
        updateTextView(jobTypeNameTextView, data.jobTypeName ==  null ? null:data.jobTypeName.getText(), R.string.please_select_jobTypeName);

    }

    private void updateTextView(TextView textView, String text, @StringRes int stringRes) {
        if (StringUtils.isEmpty(text)) {
            textView.setText(stringRes);
            textView.setTextColor(getResources().getColor(R.color.black50PercentColor));
        } else {
            textView.setText(text);
            textView.setTextColor(getResources().getColor(R.color.black25PercentColor));
        }
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

    @OnClick(R.id.workTypeLayout)
    protected void workTypeLayoutClicked(View view) {
        if (preventDoubleClick()) {
            return;
        }

        Intent intent = new Intent(this, WorkTypeActivity.class);
        startActivityForResult(intent, MyConstants.RequestCodeWorkType);
    }

    @OnClick(R.id.entranceDateLayout)
    protected void entranceDateLayoutClicked(View view) {
        if (preventDoubleClick()) {
            return;
        }

        MyUiUtils.showYearMonthDayPicker(this, RegisterData.shared().entranceDate, new DatePicker.OnYearMonthDayPickListener() {
            @Override
            public void onDatePicked(String year, String month, String day) {
                RegisterData.shared().entranceDate = year + "-" + month + "-" + day;
                updateInfo();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == MyConstants.RequestCodeWorkType) {
            updateInfo();
        }
    }

    @OnClick(R.id.phoneLayout)
    protected void phoneLayoutClicked(View view) {
        if (preventDoubleClick()) {
            return;
        }

        new MaterialDialog.Builder(this)
                .iconRes(R.drawable.icon_info)
                .title(R.string.please_input_phone)
                .input(getString(R.string.please_input_phone), RegisterData.shared().phone, new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {
                        if (MyUtils.isValidMobile(input.toString())) {
                            RegisterData.shared().phone = input.toString();
                            updateInfo();
                        } else {
                            Toasty.warning(RegisterInfoActivity.this, R.string.please_input_phone).show();
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
        RegisterData.shared().isTeamLeader = button.isChecked();
    }

    @OnCheckedChanged(R.id.contractSwitch)
    protected void contractSwitchCheckedChanged(SwitchButton button) {
        RegisterData.shared().isContract = button.isChecked();
    }
    @OnCheckedChanged(R.id.exitSwitch)
    protected void exitSwitchCheckedChanged(SwitchButton button) {
        RegisterData.shared().isExit = button.isChecked();
    }
    @OnCheckedChanged(R.id.entranceSwitch)
    protected void entranceSwitchCheckedChanged(SwitchButton button) {
        RegisterData.shared().isEntrance = button.isChecked();
    }
    @OnCheckedChanged(R.id.workConfirmSwitch)
    protected void workConfirmSwitchCheckedChanged(SwitchButton button) {
        RegisterData.shared().isWorkConfirm = button.isChecked();
    }
    @OnCheckedChanged(R.id.iDCardPDFSwitch)
    protected void iDCardPDFSwitchCheckedChanged(SwitchButton button) {
        RegisterData.shared().isIDCardPDF = button.isChecked();
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
        if (RegisterData.shared().empCategory != null) {
            index = teamList.indexOf(RegisterData.shared().empCategory);
        }
        MyUiUtils.showPicker(RegisterInfoActivity.this, teamList, index, new SinglePicker.OnItemPickListener<Dict>() {
            @Override
            public void onItemPicked(int index, Dict item) {
                RegisterData.shared().empCategory = item;
                updateInfo();
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
        if (RegisterData.shared().jobTypeName != null) {
            index = teamList.indexOf(RegisterData.shared().jobTypeName);
        }
        MyUiUtils.showPicker(RegisterInfoActivity.this, teamList, index, new SinglePicker.OnItemPickListener<Dict>() {
            @Override
            public void onItemPicked(int index, Dict item) {
                RegisterData.shared().jobTypeName = item;
                updateInfo();
            }
        });
    }

    @OnClick(R.id.nextButton)
    protected void nextButtonClicked(View view) {
        if (preventDoubleClick()) {
            return;
        }

        RegisterData data = RegisterData.shared();
        if (data.project == null) {
            Toasty.warning(this, R.string.please_select_project).show();
            return;
        }

        if (data.buildCompany == null) {
            Toasty.warning(this, R.string.please_select_company).show();
            return;
        }

        if (data.team == null) {
            Toasty.warning(this, R.string.please_select_team).show();
            return;
        }

        if (data.workType == null) {
            Toasty.warning(this, R.string.please_select_work_type).show();
            return;
        }
        if (data.empCategory == null) {
            Toasty.warning(this, R.string.please_select_empCategory).show();
            return;
        }
        if (data.jobTypeName == null) {
            Toasty.warning(this, R.string.please_select_jobTypeName).show();
            return;
        }

        if (!MyUtils.isValidMobile(data.phone)) {
            Toasty.warning(this, R.string.please_input_phone).show();
            return;
        }

        startActivity(new Intent(this, RegisterConfirmActivity.class));
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
        if (RegisterData.shared().project != null) {
            index = projectList.indexOf(RegisterData.shared().project);
        }
        MyUiUtils.showPicker(RegisterInfoActivity.this, projectList, index, new SinglePicker.OnItemPickListener<Project>() {
            @Override
            public void onItemPicked(int index, Project item) {
                RegisterData.shared().project = item;
                updateInfo();
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
        if (RegisterData.shared().buildCompany != null) {
            index = companyList.indexOf(RegisterData.shared().buildCompany);
        }
        MyUiUtils.showPicker(RegisterInfoActivity.this, companyList, index, new SinglePicker.OnItemPickListener<BuildCompany>() {
            @Override
            public void onItemPicked(int index, BuildCompany item) {
                RegisterData.shared().buildCompany = item;
                updateInfo();
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
        if (RegisterData.shared().team != null) {
            index = teamList.indexOf(RegisterData.shared().team);
        }
        MyUiUtils.showPicker(RegisterInfoActivity.this, teamList, index, new SinglePicker.OnItemPickListener<Team>() {
            @Override
            public void onItemPicked(int index, Team item) {
                RegisterData.shared().team = item;
                updateInfo();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.reg_exit, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_cancel:
                showCancelRegisterAlert();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }

        return true;
    }

    private void showCancelRegisterAlert() {
        showAlertDialog(R.drawable.icon_info, null, getString(R.string.cancel_register_tip), getString(R.string.exit), new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                EventBus.getDefault().post(new BackToMainEvent());
            }
        }, getString(R.string.cancel), null, null);
    }
}
