package com.hujiang.hujiangapp.misc;


import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;

import com.hujiang.hujiangapp.R;

public class CreatePictureDialog extends AlertDialog implements View.OnClickListener  {
    public CreatePictureDialog(Context context) {
        super(context);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle();
        initView();
    }

    /**
     * 加载视图
     */
    private void initView() {
        View rootView = View.inflate(getContext(), R.layout.dialog_create_picture, null);
        rootView.findViewById(R.id.dialog_createpicture_cancle).setOnClickListener(this);
        rootView.findViewById(R.id.dialog_create_picture_careme).setOnClickListener(this);
        rootView.findViewById(R.id.dialog_createpicture_photoalbum).setOnClickListener(this);
        this.setContentView(rootView);
    }

    /**
     * 设置特征
     */
    private void setStyle() {
        WindowManager.LayoutParams layoutParams = this.getWindow().getAttributes();
        layoutParams.gravity = Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM;
        layoutParams.width = getPhoneSize().widthPixels * 3 / 4;
        layoutParams.y = 40;
        this.setCanceledOnTouchOutside(false);
    }
    private DisplayMetrics getPhoneSize() {
        return getContext().getResources().getDisplayMetrics();
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.dialog_createpicture_cancle:
                this.dismiss();
                break;
            case R.id.dialog_create_picture_careme:
                this.dismiss();
                if (getResultListener() != null) {
                    getResultListener().camera();
                }
                break;
            case R.id.dialog_createpicture_photoalbum:
                this.dismiss();
                if (getResultListener() != null) {
                    getResultListener().photoAlbum();
                }
                break;
            default:

                break;
        }
    }

    private ResultListener resultListener;

    public ResultListener getResultListener() {
        return resultListener;
    }

    public void setResultListener(ResultListener resultListener) {
        this.resultListener = resultListener;
    }

    public interface ResultListener {
        /**
         * 拍照动作
         */
        void camera();

        /**
         * 图库动作
         */
        void photoAlbum();
    }

}
