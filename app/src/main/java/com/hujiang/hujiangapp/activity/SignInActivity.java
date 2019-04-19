package com.hujiang.hujiangapp.activity;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;

import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.StringUtils;
import com.hujiang.hujiangapp.R;
import com.hujiang.hujiangapp.api.ApiService;
import com.hujiang.hujiangapp.api.HuJiangServer;
import com.hujiang.hujiangapp.misc.MyConstants;
import com.hujiang.hujiangapp.misc.MyUiUtils;
import com.hujiang.hujiangapp.model.ApiResp;
import com.hujiang.hujiangapp.model.Project;
import com.hujiang.hujiangapp.model.User;
import com.hujiang.hujiangapp.shared.SessionManager;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.qqtheme.framework.picker.SinglePicker;
import es.dmoral.toasty.Toasty;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.RuntimePermissions;
import retrofit2.Call;

@RuntimePermissions
public class SignInActivity extends BaseActivity {
    @BindView(R.id.scrollView)
    protected ScrollView scrollView;

    @BindView(R.id.editTextAccount)
    protected EditText editTextAccount;

    @BindView(R.id.editTextPassword)
    protected EditText editTextPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        ButterKnife.bind(this);

        setupScrollView();

        String username = SPUtils.getInstance().getString(MyConstants.KeySavedUsername);
        if (!StringUtils.isEmpty(username)) {
            editTextAccount.setText(username);
        }

        if (!SessionManager.shared().isSerialOK()) {
            SignInActivityPermissionsDispatcher.prepareDeviceSerialWithPermissionCheck(this);
        }
//        SignInActivityPermissionsDispatcher.permissionsCameraWithPermissionCheck(this);
    }

    @Override
    protected boolean showToolbar() {
        return false;
    }

    private void setupScrollView() {
        scrollView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                MyUiUtils.scrollToBottom(scrollView);
            }
        });
    }

    @OnClick(R.id.imageButtonClearAccount)
    protected void imageButtonClearAccountClicked(View view) {
        editTextAccount.setText("");
    }

    @OnClick(R.id.imageButtonClearPassword)
    protected void imageButtonClearPasswordClicked(View view) {
        editTextPassword.setText("");
    }

    @OnClick(R.id.buttonSignIn)
    protected void buttonSignInClicked(View view) {
        if (preventDoubleClick()) {
            return;
        }

        if (!checkInputOK()) {
            return;
        }

        signIn();
    }

    private boolean checkInputOK() {
        if (StringUtils.isEmpty(editTextAccount.getText().toString().trim())) {
            Toasty.warning(this, R.string.account_hint).show();
            return false;
        }
        if (StringUtils.isEmpty(editTextPassword.getText().toString().trim())) {
            Toasty.warning(this, R.string.password_hint).show();
            return false;
        }

        return true;
    }

    private void signIn() {
        String username = editTextAccount.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        ApiService apiService = HuJiangServer.createService();
        Call<ApiResp<User>> call = apiService.signIn(username, password);

        startApiCall(call, new ApiResultHandler<User>() {
            @Override
            String defaultErrorMessage() {
                return getString(R.string.sign_in_failed);
            }

            @Override
            void apiSucceed(User user) {
                signInSucceed(user);
            }
        });
    }

    private void signInSucceed(User user) {
        SessionManager.shared().signIn(user);

        String username = editTextAccount.getText().toString().trim();
        SPUtils.getInstance().put(MyConstants.KeySavedUsername, username);

        showMainActivity();
    }

    private void showMainActivity() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    @NeedsPermission(Manifest.permission.READ_PHONE_STATE)
    protected void prepareDeviceSerial() {
        SessionManager.shared().prepareDeviceSerial(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // NOTE: delegate the permission handling to generated method
        SignInActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }

}
