package com.hujiang.hujiangapp.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.SystemClock;
import android.support.annotation.DrawableRes;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.blankj.utilcode.util.StringUtils;
import com.gyf.barlibrary.ImmersionBar;
import com.hujiang.hujiangapp.R;
import com.hujiang.hujiangapp.model.ApiResp;
import com.mikepenz.iconics.view.IconicsButton;

import org.json.JSONObject;

import butterknife.ButterKnife;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BaseActivity extends AppCompatActivity {
    private InputMethodManager mInputMethodManager;
    private boolean isActivityResumed = false;
    private long lastClickTime = 0;
    private Toolbar toolbar;

    public interface AsyncCallback {
        void doAction();
    }

    @Override
    public void setContentView(int layoutResID) {
        CoordinatorLayout coordinatorLayout = (CoordinatorLayout) getLayoutInflater().inflate(R.layout.activity_base, null);
        FrameLayout activityContainer = coordinatorLayout.findViewById(R.id.layout_container);
        toolbar = coordinatorLayout.findViewById(R.id.toolbar);

        getLayoutInflater().inflate(layoutResID, activityContainer, true);

        super.setContentView(coordinatorLayout);

        if (showToolbar()) {
            setupToolbar();
        } else {
            toolbar.setVisibility(View.GONE);
        }

        ButterKnife.bind(this);
        //初始化沉浸式statusBar
        if (isImmersionBarEnabled()) {
            initImmersionBar();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mInputMethodManager = null;
        if (isImmersionBarEnabled()) {
            ImmersionBar.with(this).destroy();
        }
    }

    protected void initImmersionBar() {
        if (showToolbar()) {
            ImmersionBar.with(this).titleBar(R.id.toolbar)
                    .navigationBarColor(R.color.colorPrimary)
                    .init();
        } else {
            ImmersionBar.with(this).navigationBarColor(R.color.colorPrimary).init();
        }
    }

    /**
     * 是否可以使用沉浸式
     * Is immersion bar enabled boolean.
     *
     * @return the boolean
     */
    protected boolean isImmersionBarEnabled() {
        return true;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (isImmersionBarEnabled()) {
            ImmersionBar.with(this).init();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        isActivityResumed = true;
    }

    @Override
    protected void onPause() {
        isActivityResumed = false;
        super.onPause();
    }

    public boolean isActivityResumed() {
        return isActivityResumed;
    }

    protected boolean preventDoubleClick() {
        if (SystemClock.elapsedRealtime() - lastClickTime < 1000){
            return true;
        }
        lastClickTime = SystemClock.elapsedRealtime();
        return false;
    }

    // loading dialog
    private MaterialDialog loadingDialog;

    protected void showLoadingDialog() {
        showLoadingDialog(null);
    }

    protected void showLoadingDialog(String text) {
        if (loadingDialog != null && loadingDialog.isShowing()) {
            return;
        }

        loadingDialog = new MaterialDialog.Builder(this)
                .customView(R.layout.loading_dialog, false)
                .cancelable(false)
                .canceledOnTouchOutside(false)
                .build();

        if (!StringUtils.isEmpty(text)) {
            View view = loadingDialog.getCustomView();
            TextView textView = view.findViewById(R.id.progressTextView);
            textView.setText(text);
        }

        try {
            loadingDialog.show();
        } catch (Exception ex) {
            // do nothing
        }

        // must set after showing the dialog
        loadingDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }

    protected void dismissLoadingDialog() {
        if (loadingDialog != null && loadingDialog.isShowing()) {
            loadingDialog.dismiss();
        }
    }

    // Alert dialog
    protected MaterialDialog showAlertDialog(CharSequence message) {
        return showAlertDialog(0, null, message, null, null, null, null);
    }

    protected MaterialDialog showAlertDialog(@DrawableRes int iconRes, CharSequence message) {
        return showAlertDialog(iconRes, null, message, null, null, null, null);
    }

    protected MaterialDialog showAlertDialog(@DrawableRes int iconRes, CharSequence title, CharSequence message) {
        return showAlertDialog(iconRes, title, message, null, null, null, null);
    }

    protected MaterialDialog showAlertDialog(@DrawableRes int iconRes, CharSequence title, CharSequence message, CharSequence positive, MaterialDialog.SingleButtonCallback positiveCallback, CharSequence negative, MaterialDialog.SingleButtonCallback negativeCallback) {
        return showAlertDialog(iconRes, title, message, positive, positiveCallback, negative, negativeCallback, null);
    }

    protected MaterialDialog showAlertDialog(@DrawableRes int iconRes, CharSequence title, CharSequence message, CharSequence positive, MaterialDialog.SingleButtonCallback positiveCallback, CharSequence negative, MaterialDialog.SingleButtonCallback negativeCallback, MaterialDialog.OnDismissListener onDismissListener) {
        if (StringUtils.isEmpty(positive)) {
            positive = getString(R.string.ok);
        }
        if (StringUtils.isEmpty(title)) {
            title = getString(R.string.kind_tip);
        }
        if (message == null) {
            message = "";
        }

        MaterialDialog.Builder builder = new MaterialDialog.Builder(this)
                .title(title)
                .content(message)
                .positiveText(positive)
                .positiveColor(getResources().getColor(R.color.app_blue))
                .negativeColor(getResources().getColor(R.color.app_dark_gray));

        if (iconRes != 0) {
            builder.iconRes(iconRes);
        }

        if (positiveCallback != null) {
            builder.onPositive(positiveCallback);
        }
        if (negative != null) {
            builder.negativeText(negative);
        }
        if (negativeCallback != null) {
            builder.onNegative(negativeCallback);
        }
        if (onDismissListener != null) {
            builder.dismissListener(onDismissListener);
        }

        try {
            return builder.show();
        } catch (Exception ex) {
            return builder.build();
        }
    }

    protected boolean showToolbar() {
        return true;
    }

    protected CharSequence toolbarTitle() {
        return null;
    }

    protected boolean toolbarHideLeftButton() {
        return false;
    }

    protected String toolbarLeftButtonIconicIcon() {
        return null;
    }

    protected View.OnClickListener toolbarLeftButtonListener() {
        return null;
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowTitleEnabled(false);
            TextView mTitle = toolbar.findViewById(R.id.toolbarTitle);

            CharSequence title = toolbarTitle();
            if (title == null) {
                mTitle.setText(getTitle());
            } else {
                mTitle.setText(title);
            }
        }

        IconicsButton leftButton = toolbar.findViewById(R.id.buttonLeft);
        String iconicsIcon = toolbarLeftButtonIconicIcon();
        if (iconicsIcon != null) {
            leftButton.setText(iconicsIcon);
        }

        View.OnClickListener buttonListener = toolbarLeftButtonListener();
        if (buttonListener != null) {
            leftButton.setOnClickListener(buttonListener);
        } else {
            leftButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        }

        if (toolbarHideLeftButton()) {
            leftButton.setVisibility(View.INVISIBLE);
        } else {
            leftButton.setVisibility(View.VISIBLE);
        }
    }

    protected void setToolbarTitle(String title) {
        TextView mTitle = toolbar.findViewById(R.id.toolbarTitle);
        mTitle.setText(title);
    }

    @Override
    public void finish() {
        super.finish();
        hideSoftKeyBoard();
    }

    protected void hideSoftKeyBoard() {
        View localView = getCurrentFocus();
        if (this.mInputMethodManager == null) {
            this.mInputMethodManager = ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE));
        }
        if ((localView != null) && (this.mInputMethodManager != null)) {
            this.mInputMethodManager.hideSoftInputFromWindow(localView.getWindowToken(), 2);
        }
    }

    public abstract class ApiResultHandler<T> {
        boolean showLoading() {
            return true;
        }
        boolean showError() {
            return true;
        }
        String defaultErrorMessage() {
            return null;
        }
        void apiFailed() { }
        abstract void apiSucceed(T t);
    }

    protected <T> void startApiCall(Call<ApiResp<T>> call, final ApiResultHandler<T> handler) {
        final boolean showLoading = (handler != null && handler.showLoading());
        if (showLoading) {
            showLoadingDialog();
        }

        call.enqueue(new Callback<ApiResp<T>>() {
            @Override
            public void onResponse(Call<ApiResp<T>> call, Response<ApiResp<T>> response) {
                if (showLoading) {
                    dismissLoadingDialog();
                }

                if (handler == null) {
                    return;
                }

                ApiResp<T> theResp = response.body();
                if (response.isSuccessful() && theResp != null) {
                    handler.apiSucceed(theResp.getData());
                } else {
                    String message;
                    if (theResp != null && !StringUtils.isEmpty(theResp.getMessage())) {
                        message = theResp.getMessage();
                    } else {
                        message = getErrorResponseMessage(response.errorBody());
                    }

                    if (StringUtils.isEmpty(message)) {
                        message = handler.defaultErrorMessage();
                    }

                    apiError(handler, message);
                }
            }

            @Override
            public void onFailure(Call<ApiResp<T>> call, Throwable t) {
                if (showLoading) {
                    dismissLoadingDialog();
                }

                if (handler == null) {
                    return;
                }

                apiError(handler, handler.defaultErrorMessage());
            }

            private String getErrorResponseMessage(ResponseBody errorBody) {
                try {
                    String str = errorBody.string();
                    if (!StringUtils.isEmpty(str)) {
                        JSONObject obj = new JSONObject(str);
                        return obj.optString("message");
                    }
                } catch (Exception ex) {
                    // do nothing
                }
                return "";
            }

            private void apiError(final ApiResultHandler<T> handler, String message) {
                if (handler.showError()) {
                    if (StringUtils.isEmpty(message)) {
                        message = getString(R.string.server_response_failure);
                    }

                    showAlertDialog(R.drawable.icon_error, null, message, null, null, null, null, new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            handler.apiFailed();
                        }
                    });
                } else {
                    handler.apiFailed();
                }
            }
        });
    }
}
