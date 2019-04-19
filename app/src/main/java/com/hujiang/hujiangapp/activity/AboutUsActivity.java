package com.hujiang.hujiangapp.activity;

import android.os.Bundle;
import android.widget.TextView;

import com.hujiang.hujiangapp.BuildConfig;
import com.hujiang.hujiangapp.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AboutUsActivity extends BaseActivity {
    @BindView(R.id.versionTextView)
    protected TextView versionTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);
        ButterKnife.bind(this);

        versionTextView.setText(getString(R.string.current_version_format, BuildConfig.VERSION_NAME));
    }
}
