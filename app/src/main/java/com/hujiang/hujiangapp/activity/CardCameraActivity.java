package com.hujiang.hujiangapp.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.blankj.utilcode.util.FileUtils;
import com.blankj.utilcode.util.ImageUtils;
import com.blankj.utilcode.util.StringUtils;
import com.google.android.cameraview.CameraImpl;
import com.hujiang.hujiangapp.R;
import com.hujiang.hujiangapp.api.ApiService;
import com.hujiang.hujiangapp.api.HuJiangServer;
import com.hujiang.hujiangapp.misc.MyConstants;
import com.hujiang.hujiangapp.misc.MyUiUtils;
import com.hujiang.hujiangapp.misc.MyUtils;
import com.hujiang.hujiangapp.model.ApiResp;
import com.hujiang.hujiangapp.model.CardOcrInfo;
import com.hujiang.hujiangapp.model.ImageResource;
import com.hujiang.hujiangapp.shared.RegisterData;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.pqpo.smartcameralib.MaskView;
import me.pqpo.smartcameralib.SmartCameraView;
import okhttp3.MultipartBody;
import retrofit2.Call;
import top.zibin.luban.Luban;
import top.zibin.luban.OnCompressListener;

public class CardCameraActivity extends BaseActivity {
    @BindView(R.id.cameraView)
    protected SmartCameraView cameraView;

    @BindView(R.id.scanTitle)
    protected TextView scanTitle;

    private MyConstants.ScanType scanType = MyConstants.ScanType.IdCardFront;
    private File currentImageFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_camera);
        ButterKnife.bind(this);

        int type = getIntent().getIntExtra(MyConstants.ExtraScanType, 0);
        scanType = MyConstants.ScanType.values()[type];

        setupTitle();
        setupMaskView();
        setupCameraCallback();
    }

    @Override
    protected boolean showToolbar() {
        return false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        startCameraAndScan();
    }

    @Override
    protected void onPause() {
        stopCameraAndScan();
        super.onPause();
    }

    private void startCameraAndScan() {
        cameraView.start();
        cameraView.startScan();

        MaskView maskView = (MaskView)cameraView.getMaskView();
        maskView.setShowScanLine(true);
    }

    private void stopCameraAndScan() {
        cameraView.stopScan();
        cameraView.stop();

        MaskView maskView = (MaskView)cameraView.getMaskView();
        maskView.setShowScanLine(false);
    }

    private void setupTitle() {
        if (isScanIdFront()) {
            scanTitle.setText(getString(R.string.take_id_card_front));
        } else if (isScanIdBack()) {
            scanTitle.setText(getString(R.string.take_id_card_back));
        } else if (isScanBankCard()) {
            scanTitle.setText(getString(R.string.take_bank_card_front));
        } else {
            scanTitle.setText(getString(R.string.take_card));
        }
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

    @OnClick(R.id.cancelButton)
    protected void cancelButtonClicked(View view) {
        finish();
    }

    private void setupMaskView() {
        final MaskView maskView = (MaskView) cameraView.getMaskView();;
        maskView.setMaskLineColor(Color.GREEN);
        maskView.setScanLineGradient(0xff2ecc71, 0x0027ae60);
        maskView.setShowScanLine(true);
        maskView.setScanSpeed(6);
        maskView.setMaskLineWidth(2);
        maskView.setScanGradientSpread(10);
        cameraView.post(new Runnable() {
            @Override
            public void run() {
                int height = cameraView.getHeight();
                maskView.setMaskSize((int) (height * 0.7f / 0.63f), (int) (height * 0.7f));
                maskView.setMaskOffset(0, 30);
            }
        });
        cameraView.setMaskView(maskView);
    }

    private void setupCameraCallback() {
        cameraView.addCallback(new CameraImpl.Callback() {
            @Override
            public void onPictureTaken(CameraImpl camera, byte[] data) {
                super.onPictureTaken(camera, data);
                cameraView.cropJpegImage(data, new SmartCameraView.CropCallback() {
                    @Override
                    public void onCropped(Bitmap bitmap) {
                        if (bitmap != null) {
                            cropCardSucceed(bitmap);
                            stopCameraAndScan();
                        } else {
                            startCameraAndScan();
                        }
                    }
                });
            }
        });
    }

    private void cropCardSucceed(Bitmap bitmap) {
        File originalFile = MyUtils.newBitmapFile(this);
        ImageUtils.save(bitmap, originalFile, Bitmap.CompressFormat.JPEG);
        compressImageFile(originalFile);
    }

    private void compressImageFile(final File imageFile) {
        Luban.with(this)
                .load(imageFile)
                .ignoreBy(0)
                .setTargetDir(imageFile.getParent())
                .setCompressListener(new OnCompressListener() {
                    @Override
                    public void onStart() {
                    }

                    @Override
                    public void onSuccess(File file) {
                        FileUtils.deleteFile(imageFile);
                        uploadImageFile(file);
                    }

                    @Override
                    public void onError(Throwable e) {
                        uploadImageFile(imageFile);
                    }
                }).launch();
    }

    private void uploadImageFile(File imageFile) {
        currentImageFile = imageFile;
        showLoadingDialog(getString(R.string.ocr_loading));

        MultipartBody.Part part = MyUtils.imageMultipartFile(imageFile);
        ApiService apiService = HuJiangServer.createService();
        Call<ApiResp<ImageResource>> call = apiService.uploadImage(part);

        startApiCall(call, new ApiResultHandler<ImageResource>() {
            @Override
            boolean showLoading() {
                return false;
            }

            @Override
            void apiSucceed(ImageResource imageResource) {
                if (StringUtils.isEmpty(imageResource.getUrl())) {
                    dismissDialogAndRestart();
                } else {
                    ocrImageResource(imageResource);
                }
            }

            @Override
            void apiFailed() {
                dismissDialogAndRestart();
            }
        });
    }

    private void dismissDialogAndRestart() {
        dismissLoadingDialog();
        startCameraAndScan();
    }

    private void ocrImageResource(ImageResource imageResource) {
        String url = imageResource.getUrl();
        if (StringUtils.isEmpty(url)) {
            return;
        }

        ApiService apiService = HuJiangServer.createService();
        Call<ApiResp<CardOcrInfo>> call;
        if (isScanBankCard()) {
            call = apiService.ocrBankCard(url);
            RegisterData.shared().bankResource = imageResource;
        } else {
            call = apiService.ocrIdCard(url, scanType.getTypeString());
            if (isScanIdFront()) {
                RegisterData.shared().idFrontResource = imageResource;
            } else {
                RegisterData.shared().idBackResource = imageResource;
            }
        }

        startApiCall(call, new ApiResultHandler<CardOcrInfo>() {
            @Override
            boolean showLoading() {
                return false;
            }

            @Override
            String defaultErrorMessage() {
                return getString(R.string.ocr_info_invalid_tip);
            }

            @Override
            void apiSucceed(CardOcrInfo cardOcrInfo) {
                ocrSucceed(cardOcrInfo);
            }

            @Override
            void apiFailed() {
                dismissDialogAndRestart();
            }
        });
    }

    private void ocrSucceed(CardOcrInfo cardOcrInfo) {
        dismissLoadingDialog();
        if (isScanIdFront()) {
            RegisterData.shared().idFrontImage = currentImageFile;
            RegisterData.shared().idFrontInfo = cardOcrInfo;
            MyUiUtils.showOcrInfoActivity(this, MyConstants.ScanType.IdCardFront);

        } else if (isScanIdBack()) {
            RegisterData.shared().idBackImage = currentImageFile;
            RegisterData.shared().idBackInfo = cardOcrInfo;
            MyUiUtils.showOcrInfoActivity(this, MyConstants.ScanType.IdCardBack);

        } else if (isScanBankCard()) {
            RegisterData.shared().bankImage = currentImageFile;
            RegisterData.shared().bankInfo = cardOcrInfo;
            MyUiUtils.showOcrInfoActivity(this, MyConstants.ScanType.BankCard);

        }

        finish();
    }
}
