package com.hujiang.hujiangapp.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.blankj.utilcode.util.StringUtils;
import com.hujiang.hujiangapp.R;
import com.hujiang.hujiangapp.api.ApiService;
import com.hujiang.hujiangapp.api.HuJiangServer;
import com.hujiang.hujiangapp.misc.MyUiUtils;
import com.hujiang.hujiangapp.model.ApiResp;
import com.hujiang.hujiangapp.model.Empty;
import com.hujiang.hujiangapp.model.User;
import com.hujiang.hujiangapp.shared.SessionManager;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import es.dmoral.toasty.Toasty;
import retrofit2.Call;

public class ChangePasswordActivity extends BaseActivity {
    @BindView(R.id.editTextPassword)
    protected EditText editTextPassword;

    @BindView(R.id.editTextNewPassword)
    protected EditText editTextNewPassword;

    @BindView(R.id.editTextConfirmPassword)
    protected EditText editTextConfirmPassword;

    @BindView(R.id.buttonConfirm)
    protected Button buttonConfirm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.buttonConfirm)
    protected void buttonConfirm(View view) {
        if (preventDoubleClick()) {
            return;
        }

        String oldPassword = editTextPassword.getText().toString().trim();
        String newPassword = editTextNewPassword.getText().toString().trim();
        String confirmPassword = editTextConfirmPassword.getText().toString().trim();

        if (StringUtils.isEmpty(oldPassword) || StringUtils.isEmpty(newPassword) || StringUtils.isEmpty(confirmPassword)) {
            Toasty.error(this, R.string.password_empty).show();
            return;
        }

        if (!StringUtils.equals(newPassword, confirmPassword)) {
            Toasty.error(this, R.string.new_password_not_match).show();
            return;
        }

        User user = SessionManager.shared().getCurrentUser();
        if (user == null) {
            MyUiUtils.signOut(this);
            return;
        }

        ApiService apiService = HuJiangServer.createService();
        Call<ApiResp<Empty>> call = apiService.modifyPassword(user.getId(), oldPassword, newPassword);

        startApiCall(call, new ApiResultHandler<Empty>() {
            @Override
            String defaultErrorMessage() {
                return getString(R.string.change_password_failed);
            }

            @Override
            void apiSucceed(Empty empty) {
                Toasty.success(ChangePasswordActivity.this, R.string.change_password_success).show();
                finish();
            }
        });
    }
}
