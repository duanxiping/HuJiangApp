package com.hujiang.hujiangapp.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.blankj.utilcode.util.StringUtils;
import com.bumptech.glide.Glide;
import com.hujiang.hujiangapp.R;
import com.hujiang.hujiangapp.api.ApiService;
import com.hujiang.hujiangapp.api.HuJiangServer;
import com.hujiang.hujiangapp.event.BackToMainEvent;
import com.hujiang.hujiangapp.misc.MyConstants;
import com.hujiang.hujiangapp.misc.MyUiUtils;
import com.hujiang.hujiangapp.model.ApiResp;
import com.hujiang.hujiangapp.model.CardOcrInfo;
import com.hujiang.hujiangapp.model.Dict;
import com.hujiang.hujiangapp.shared.RegisterData;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.File;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.qqtheme.framework.picker.OptionPicker;
import cn.qqtheme.framework.picker.SinglePicker;
import es.dmoral.toasty.Toasty;
import retrofit2.Call;

public class OcrInfoActivity extends BaseActivity {
    @BindView(R.id.imageView)
    protected ImageView imageView;

    @BindView(R.id.idFrontLayout)
    protected View idFrontLayout;

    @BindView(R.id.idBackLayout)
    protected View idBackLayout;

    @BindView(R.id.bankLayout)
    protected View bankLayout;

    @BindView(R.id.nextButton)
    protected Button nextButton;

    @BindView(R.id.nameTextView)
    protected TextView nameTextView;

    @BindView(R.id.idNoTextView)
    protected TextView idNoTextView;

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

    private MyConstants.ScanType scanType = MyConstants.ScanType.IdCardFront;

    private List<Dict> loadedBankDicts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int type = getIntent().getIntExtra(MyConstants.ExtraScanType, 0);
        scanType = MyConstants.ScanType.values()[type];

        setContentView(R.layout.activity_ocr_info);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);

        setupLayout();
        setupImageView();
        setupInfo();
        if (RegisterData.shared().isChangingWorkerInfo) {
            nextButton.setText(R.string.confirm);
        } else {
            nextButton.setText(R.string.next);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected CharSequence toolbarTitle() {
        if(isScanIdFront()){
            return getString( R.string.id_card_front_info);
        }else if(isScanIdBack()){
            return getString( R.string.id_card_back_info);
        }else if(isScanBankCard()){
            return getString( R.string.bank_card_info);
        }else{
            return null;
        }
    }

    @Subscribe
    public void onBackToMainEvent(BackToMainEvent event) {
        finish();
    }

    private boolean isScanIdFront() {
        return scanType == MyConstants.ScanType.IdCardFront;
    }

    private boolean isScanIdBack() {
        return scanType == MyConstants.ScanType.IdCardBack;
    }

    private boolean isScanBankCard() {
        return scanType == MyConstants.ScanType.BankCard;
    }

    private void setupLayout() {
        idFrontLayout.setVisibility(isScanIdFront() ? View.VISIBLE : View.GONE);
        idBackLayout.setVisibility(isScanIdBack() ? View.VISIBLE : View.GONE);
        bankLayout.setVisibility(isScanBankCard() ? View.VISIBLE : View.GONE);
    }

    private void setupImageView() {
        File imageFile = null;
        if (isScanIdFront()) {
            imageFile = RegisterData.shared().idFrontImage;
        } else if (isScanIdBack()) {
            imageFile = RegisterData.shared().idBackImage;
        } else {
            imageFile = RegisterData.shared().bankImage;
        }

        if (imageFile != null) {
            Glide.with(this).load(imageFile).into(imageView);
        }
    }

    private void setupInfo() {
        if (isScanIdFront()) {
            setupFrontInfo();
        } else if (isScanIdBack()) {
            setupBackInfo();
        } else {
            setupBankInfo();
        }
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
        idDateTextView.setText(info.getValidDateRange());
    }

    private void setupBankInfo() {
        RegisterData data = RegisterData.shared();
        updateTextView(bankNameTextView, data.bankDict == null ? null : data.bankDict.getText(), R.string.please_select_bank);

        CardOcrInfo info = data.bankInfo;
        if (info == null) {
            return;
        }

        bankNoTextView.setText(info.getCard_num());
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (RegisterData.shared().isChangingWorkerInfo) {
            getMenuInflater().inflate(R.menu.rescan, menu);
        } else {
            getMenuInflater().inflate(R.menu.rescan_exit, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_rescan:
                rescanInfo();
                break;
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

    @OnClick(R.id.bankNameInfo)
    protected void bankNameInfoClicked(View view) {
        if (preventDoubleClick()) {
            return;
        }

        if (loadedBankDicts != null && 0 < loadedBankDicts.size()) {
            showBankPicker(loadedBankDicts);
            return;
        }

        ApiService apiService = HuJiangServer.createService();
        Call<ApiResp<List<Dict>>> call = apiService.queryDict(MyConstants.DictCategoryBank);

        startApiCall(call, new ApiResultHandler<List<Dict>>() {
            @Override
            String defaultErrorMessage() {
                return getString(R.string.loading_failed);
            }

            @Override
            void apiSucceed(List<Dict> dictList) {
                loadedBankDicts = dictList;
                showBankPicker(dictList);
            }
        });
    }

    private void showBankPicker(List<Dict> dictList) {
        int index = 0;
        if (RegisterData.shared().bankDict != null) {
            index = dictList.indexOf(RegisterData.shared().bankDict);
        }
        MyUiUtils.showPicker(OcrInfoActivity.this, dictList, index, new SinglePicker.OnItemPickListener<Dict>() {
            @Override
            public void onItemPicked(int index, Dict item) {
                RegisterData.shared().bankDict = item;
                setupBankInfo();
            }
        });
    }

    @OnClick(R.id.nextButton)
    protected void nextButtonClicked(View view) {
        if (preventDoubleClick()) {
            return;
        }

        if (!isInfoOK()) {
            showAlertDialog(R.drawable.icon_warning, getString(R.string.ocr_info_invalid_tip));
            return;
        }

        if (isScanBankCard() && RegisterData.shared().bankDict == null) {
            showAlertDialog(R.drawable.icon_warning, getString(R.string.please_select_bank));
            return;
        }
            infoOKClickNext();
    }

    private boolean isInfoOK() {
        if (isScanIdFront()) {
            return RegisterData.shared().isIdFrontInfoOK();
        } else if (isScanIdBack()) {
            return RegisterData.shared().isIdBackInfoOK();
        } else if (isScanBankCard()) {
            return RegisterData.shared().isBankInfoOK();
        }

        return false;
    }

    private void infoOKClickNext() {
        if (RegisterData.shared().isChangingWorkerInfo) {
            finish();
            return;
        }

        if (isScanIdFront()) {
            MyUiUtils.showFaceDetectActivity(this, true);
        } else if (isScanIdBack()) {
            showNeedBankCardAlert();
        } else {
            showAdditionalInfoActivity();
        }
    }

    private boolean skipBank = false;

    private void showNeedBankCardAlert() {
        skipBank = false;

        showAlertDialog(R.drawable.icon_info, null, getString(R.string.need_collect_bank_card_prompt), getString(R.string.collect), new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                skipBank = false;
            }
        }, getString(R.string.skip), new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                skipBank = true;
            }
        }, new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if (skipBank) {
                    showAdditionalInfoActivity();
                } else {
                    collectBankCard();
                }
            }
        });
    }

    private void collectBankCard() {
        MyUiUtils.showCardCameraActivity(this, MyConstants.ScanType.BankCard);
    }

    private void rescanInfo() {
        MyUiUtils.showCardCameraActivity(this, scanType);
    }

    private void showAdditionalInfoActivity() {
        startActivity(new Intent(this, RegisterInfoActivity.class));
    }

    @OnClick(R.id.idNoInfo)
    protected void changeIdNo() {
        if (preventDoubleClick()) {
            return;
        }

        new MaterialDialog.Builder(this)
                .iconRes(R.drawable.icon_info)
                .title(R.string.id_no)
                .input(getString(R.string.id_no), RegisterData.shared().idFrontInfo.getIdNumber(), new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {
                        String str = input.toString();
                        if (str.length() == 18 || str.length() == 15){
                            RegisterData.shared().idFrontInfo.setIdNumber(str);
                            setupFrontInfo();
                        } else {
                            Toasty.warning(OcrInfoActivity.this, R.string.input_invalid).show();
                        }
                    }
                })
                .inputType(InputType.TYPE_CLASS_NUMBER)
                .negativeText(R.string.cancel)
                .positiveText(R.string.confirm)
                .show();
    }

    @OnClick(R.id.nameInfo)
    protected void changeName() {
        if (preventDoubleClick()) {
            return;
        }

        new MaterialDialog.Builder(this)
                .iconRes(R.drawable.icon_info)
                .title(R.string.name)
                .input(getString(R.string.name), RegisterData.shared().idFrontInfo.getTitle(), new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {
                        String str = input.toString();
                        if (0 < str.length()){
                            RegisterData.shared().idFrontInfo.setTitle(str);
                            setupFrontInfo();
                        } else {
                            Toasty.warning(OcrInfoActivity.this, R.string.input_invalid).show();
                        }
                    }
                })
                .inputType(InputType.TYPE_TEXT_VARIATION_PERSON_NAME)
                .negativeText(R.string.cancel)
                .positiveText(R.string.confirm)
                .show();
    }

    @OnClick(R.id.sexInfo)
    protected void changeSex() {
        if (preventDoubleClick()) {
            return;
        }

        int selectedIndex = StringUtils.equals(RegisterData.shared().idFrontInfo.getSex(), "男") ? 0 : 1;
        MyUiUtils.showOptionPicker(this, getResources().getStringArray(R.array.sex), selectedIndex, new OptionPicker.OnOptionPickListener() {
            @Override
            public void onOptionPicked(int index, String item) {
                RegisterData.shared().idFrontInfo.setSex(item);
                setupFrontInfo();
            }
        });
    }

    @OnClick(R.id.nationInfo)
    protected void changeNation() {
        if (preventDoubleClick()) {
            return;
        }

        new MaterialDialog.Builder(this)
                .iconRes(R.drawable.icon_info)
                .title(R.string.nation)
                .input(getString(R.string.nation), RegisterData.shared().idFrontInfo.getNation(), new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {
                        String str = input.toString();
                        if (0 < str.length()){
                            RegisterData.shared().idFrontInfo.setNation(str);
                            setupFrontInfo();
                        } else {
                            Toasty.warning(OcrInfoActivity.this, R.string.input_invalid).show();
                        }
                    }
                })
                .inputType(InputType.TYPE_CLASS_TEXT)
                .negativeText(R.string.cancel)
                .positiveText(R.string.confirm)
                .show();
    }

    @OnClick(R.id.birthdayInfo)
    protected void changeBirthday() {
        if (preventDoubleClick()) {
            return;
        }

        new MaterialDialog.Builder(this)
                .iconRes(R.drawable.icon_info)
                .title(R.string.birthday)
                .input(getString(R.string.birthday), RegisterData.shared().idFrontInfo.getDateOfBirth(), new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {
                        String str = input.toString();
                        if (8 == str.length()){
                            RegisterData.shared().idFrontInfo.setDateOfBirth(str);
                            setupFrontInfo();
                        } else {
                            Toasty.warning(OcrInfoActivity.this, R.string.input_invalid).show();
                        }
                    }
                })
                .inputType(InputType.TYPE_CLASS_NUMBER)
                .negativeText(R.string.cancel)
                .positiveText(R.string.confirm)
                .show();
    }

    @OnClick(R.id.addressInfo)
    protected void changeAddress() {
        if (preventDoubleClick()) {
            return;
        }

        new MaterialDialog.Builder(this)
                .iconRes(R.drawable.icon_info)
                .title(R.string.live_address)
                .input(getString(R.string.live_address), RegisterData.shared().idFrontInfo.getAddress(), new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {
                        String str = input.toString();
                        if (0 < str.length()){
                            RegisterData.shared().idFrontInfo.setAddress(str);
                            setupFrontInfo();
                        } else {
                            Toasty.warning(OcrInfoActivity.this, R.string.input_invalid).show();
                        }
                    }
                })
                .inputType(InputType.TYPE_CLASS_TEXT)
                .negativeText(R.string.cancel)
                .positiveText(R.string.confirm)
                .show();
    }

    @OnClick(R.id.issuerInfo)
    protected void changeIssuer() {
        if (preventDoubleClick()) {
            return;
        }

        new MaterialDialog.Builder(this)
                .iconRes(R.drawable.icon_info)
                .title(R.string.id_issuer)
                .input(getString(R.string.id_issuer), RegisterData.shared().idBackInfo.getIssue(), new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {
                        String str = input.toString();
                        if (0 < str.length()){
                            RegisterData.shared().idFrontInfo.setIssue(str);
                            setupBackInfo();
                        } else {
                            Toasty.warning(OcrInfoActivity.this, R.string.input_invalid).show();
                        }
                    }
                })
                .inputType(InputType.TYPE_CLASS_TEXT)
                .negativeText(R.string.cancel)
                .positiveText(R.string.confirm)
                .show();
    }

    @OnClick(R.id.idDateInfo)
    protected void changeIdDate() {
        if (preventDoubleClick()) {
            return;
        }

        new MaterialDialog.Builder(this)
                .iconRes(R.drawable.icon_info)
                .title(R.string.id_valid_date)
                .input(getString(R.string.id_valid_date), RegisterData.shared().idBackInfo.getValidDateRange(), new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {
                        boolean isValid = RegisterData.shared().idBackInfo.setValidDateRange(input.toString());
                        if (isValid) {
                            setupBackInfo();
                        } else {
                            Toasty.warning(OcrInfoActivity.this, R.string.input_invalid).show();
                        }
                    }
                })
                .inputType(InputType.TYPE_CLASS_NUMBER)
                .negativeText(R.string.cancel)
                .positiveText(R.string.confirm)
                .show();
    }

    @OnClick(R.id.bankNoInfo)
    protected void changeBankNo() {
        if (preventDoubleClick()) {
            return;
        }

        new MaterialDialog.Builder(this)
                .iconRes(R.drawable.icon_info)
                .title(R.string.bank_no)
                .input(getString(R.string.bank_no), RegisterData.shared().bankInfo.getCard_num(), new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {
                        String str = input.toString();
                        if (10 < str.length()){
                            RegisterData.shared().bankInfo.setCard_num(str);
                            setupBankInfo();
                        } else {
                            Toasty.warning(OcrInfoActivity.this, R.string.input_invalid).show();
                        }
                    }
                })
                .inputType(InputType.TYPE_CLASS_NUMBER)
                .negativeText(R.string.cancel)
                .positiveText(R.string.confirm)
                .show();
    }
}
