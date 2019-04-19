package com.hujiang.hujiangapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.hujiang.hujiangapp.R;
import com.hujiang.hujiangapp.shared.SessionManager;

public class WelcomeActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                showMainOrSignInActivity();
            }
        }, 2000);
    }

    @Override
    protected boolean showToolbar() {
        return false;
    }

    private void showMainOrSignInActivity() {
        if (SessionManager.shared().isSignIn()) {
            startActivity(new Intent(this, MainActivity.class));
        } else {
            startActivity(new Intent(this, SignInActivity.class));
        }
        finish();
    }
}
