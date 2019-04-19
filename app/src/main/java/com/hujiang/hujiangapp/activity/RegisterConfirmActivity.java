package com.hujiang.hujiangapp.activity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.bumptech.glide.Glide;
import com.hujiang.hujiangapp.R;
import com.hujiang.hujiangapp.api.ApiService;
import com.hujiang.hujiangapp.api.HuJiangServer;
import com.hujiang.hujiangapp.event.BackToMainEvent;
import com.hujiang.hujiangapp.model.ApiResp;
import com.hujiang.hujiangapp.model.CardOcrInfo;
import com.hujiang.hujiangapp.model.Dict;
import com.hujiang.hujiangapp.model.Hire;
import com.hujiang.hujiangapp.shared.RegisterData;
import com.kyleduo.switchbutton.SwitchButton;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;

public class RegisterConfirmActivity extends BaseActivity {
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_confirm);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);

        setupImages();
        setupFrontInfo();
        setupBackInfo();
        setupBankInfo();
        setupAdditionalInfo();
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

    private void setupImages() {
        loadImage(RegisterData.shared().faceImage, faceImageView);
        loadImage(RegisterData.shared().idFrontImage, idFrontImageView);
        loadImage(RegisterData.shared().idBackImage, idBackImageView);
        loadImage(RegisterData.shared().bankImage, bankImageView);
    }

    private void setupFrontInfo() {
        CardOcrInfo info = RegisterData.shared().idFrontInfo;
        if (info == null) {
            return;
        }

        nameTextView.setText(info.getTitle());
        idNoTextView.setText(info.getIdNumber());
        sexTextView.setText(info.getSex());
        nationTextView.setText(info.getNation());
        birthdayTextView.setText(info.getDateOfBirth());
        addressTextView.setText(info.getAddress());
    }

    private void setupBackInfo() {
        CardOcrInfo info = RegisterData.shared().idBackInfo;
        if (info == null) {
            return;
        }

        idIssuerTextView.setText(info.getIssue());
        idDateTextView.setText(String.format("%s-%s", info.getIdCardStartdate(), info.getIdCardValiditydate()));
    }

    private void setupBankInfo() {
        CardOcrInfo info = RegisterData.shared().bankInfo;
        if (info == null) {
            bankLayout.setVisibility(View.GONE);
        } else {
            bankLayout.setVisibility(View.VISIBLE);
            bankNoTextView.setText(info.getCard_num());

            Dict bankDict = RegisterData.shared().bankDict;
            bankNameTextView.setText(bankDict == null ? "" : bankDict.getText());
        }
    }

    private void setupAdditionalInfo() {
        RegisterData data = RegisterData.shared();
        projectTextView.setText(data.project == null ? null : data.project.getTitle());
        companyTextView.setText(data.buildCompany == null ? null : data.buildCompany.getTitle());
        teamTextView.setText(data.team == null ? null : data.team.getTitle());
        workTypeTextView.setText(data.workType == null ? null : data.workType.getTitle());
        phoneTextView.setText(data.phone);
        entranceDateTextView.setText(data.entranceDate);

        leaderSwitch.setChecked(data.isTeamLeader);
        contractSwitch.setChecked(data.isContract);
        entranceSwitch.setChecked(data.isEntrance);
        exitSwitch.setChecked(data.isExit);
        workConfirmSwitch.setChecked(data.isWorkConfirm);
        iDCardPDFSwitch.setChecked(data.isIDCardPDF);

        empCategoryTextView.setText(data.empCategory == null ? null : data.empCategory.getText());

        jobTypeNameTextView.setText(data.jobTypeName == null ? null : data.jobTypeName.getText());

    }

    private void loadImage(File imageFile, ImageView imageView) {
        if (imageFile != null) {
            Glide.with(this).load(imageFile).into(imageView);
        }
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

    @OnClick(R.id.nextButton)
    protected void nextButtonClicked(View view) {
        if (preventDoubleClick()) {
            return;
        }

        RegisterData data = RegisterData.shared();

        Hire hire = new Hire();

        hire.setProject(data.project);
        hire.setBuildCompany(data.buildCompany);
        hire.setTeam(data.team);
        hire.setLeader(data.isTeamLeader);
        hire.setWorkType(data.workType);
        hire.setEntranceDate(data.entranceDate);

        hire.setArea(data.project == null ? null : data.project.getArea());

        hire.setTitle(data.idFrontInfo.getTitle());
        hire.setIdNumber(data.idFrontInfo.getIdNumber());

        hire.setNation(data.idFrontInfo.getNation());
        hire.setSex(data.idFrontInfo.getSexDict());
        hire.setDateOfBirth(data.idFrontInfo.getDateOfBirth());
        hire.setAddress(data.idFrontInfo.getAddress());

        hire.setIdAgency(data.idBackInfo.getIssue());
        hire.setIdValiddate(data.idBackInfo.getIdCardValiditydate());

        hire.setPhone(data.phone);
        hire.setTelephone(data.phone);

        hire.setBank(data.bankDict);
        hire.setBankCode(data.bankInfo == null ? null : data.bankInfo.getCard_num());

        hire.setFacePhoto(data.faceResource);
        hire.setIdPhotoFront(data.idFrontResource);
        hire.setIdPhotoRear(data.idBackResource);
        hire.setBankPhotoRear(data.bankResource);
        hire.setJobTypeName(data.jobTypeName);
        hire.setEmpCategory(data.empCategory);
        hire.setContract(data.isContract);
        hire.setExit(data.isExit);
        hire.setEntrance(data.isEntrance);
        hire.setiDCardPDF(data.isIDCardPDF);
        hire.setWorkConfirm(data.isWorkConfirm);

        ApiService apiService = HuJiangServer.createService();
        Call<ApiResp<Hire>> call = apiService.saveHire(hire);

        startApiCall(call, new ApiResultHandler<Hire>() {
            @Override
            String defaultErrorMessage() {
                return getString(R.string.sign_up_failed);
            }

            @Override
            void apiSucceed(Hire hire) {
                registerSucceed();
            }
        });
    }

    private void registerSucceed() {
        showAlertDialog(R.drawable.icon_success, null, getString(R.string.sign_up_succeed), null, null, null, null, new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                EventBus.getDefault().post(new BackToMainEvent());
            }
        });
    }
}
