package com.hujiang.hujiangapp.misc;

import android.app.Application;
import android.content.Context;

import com.baidu.aip.FaceEnvironment;
import com.baidu.aip.FaceSDKManager;
import com.baidu.idl.facesdk.FaceTracker;

import com.blankj.utilcode.util.FileUtils;
import com.blankj.utilcode.util.Utils;
import com.hujiang.hujiangapp.R;
import com.hujiang.hujiangapp.activity.MainActivity;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.Iconics;
import com.mikepenz.ionicons_typeface_library.Ionicons;
import com.mikepenz.material_design_iconic_typeface_library.MaterialDesignIconic;
import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.FormatStrategy;
import com.orhanobut.logger.Logger;
import com.orhanobut.logger.PrettyFormatStrategy;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.DefaultRefreshFooterCreator;
import com.scwang.smartrefresh.layout.api.DefaultRefreshHeaderCreator;
import com.scwang.smartrefresh.layout.api.RefreshFooter;
import com.scwang.smartrefresh.layout.api.RefreshHeader;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;
import com.zxy.recovery.callback.RecoveryCallback;
import com.zxy.recovery.core.Recovery;

public class MyApplication extends Application {
    private static MyApplication sharedApplication;

    public static Context applicationContext() {
        return sharedApplication;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        sharedApplication = this;
        Utils.init(this);

        initBaiduFaceLib();

        // 删除所有图片文件
        FileUtils.deleteDir(MyUtils.appImagesDir(this));

        FormatStrategy formatStrategy = PrettyFormatStrategy.newBuilder()
                .showThreadInfo(true)   // (Optional) Whether to show thread info or not. Default true
                .methodCount(3)         // (Optional) How many method line to show. Default 2
                .methodOffset(0)        // (Optional) Hides internal method calls up to offset. Default 5
                .tag("PrettyLogger")     // (Optional) Global tag for every log. Default PRETTY_LOGGER
                .build();

        Logger.addLogAdapter(new AndroidLogAdapter(formatStrategy));
        //Logger.addLogAdapter(new DiskLogAdapter());

        Recovery.getInstance()
                .debug(true)
                .recoverInBackground(false)
                .recoverStack(true)
                .mainPage(MainActivity.class)
                .recoverEnabled(true)
                .callback(new MyCrashCallback())
                .silent(false, Recovery.SilentMode.RECOVER_ACTIVITY_STACK)
                .init(this);

        registerIconFonts();

        setupRefreshLoadMore();
    }

    // NOTE: MUST register icon fonts if we use Gradle Flavors
    private void registerIconFonts() {
        Iconics.init(getApplicationContext());
        Iconics.registerFont(new MaterialDesignIconic());
        Iconics.registerFont(new GoogleMaterial());
        Iconics.registerFont(new Ionicons());
    }

    private void setupRefreshLoadMore() {
        ClassicsFooter.REFRESH_FOOTER_PULLING = getString(R.string.loading);//"上拉加载更多";
        ClassicsFooter.REFRESH_FOOTER_RELEASE = getString(R.string.loading);//"释放立即加载";
        ClassicsFooter.REFRESH_FOOTER_LOADING = getString(R.string.loading);//"正在加载...";
        ClassicsFooter.REFRESH_FOOTER_REFRESHING = getString(R.string.loading);//"正在刷新...";
        ClassicsFooter.REFRESH_FOOTER_FINISH = getString(R.string.loading);//"加载完成";
        ClassicsFooter.REFRESH_FOOTER_FAILED = getString(R.string.loading);//"加载失败";
        ClassicsFooter.REFRESH_FOOTER_NOTHING = getString(R.string.loading);//"没有更多数据了";
    }

    //static 代码段可以防止内存泄露
    static {
        //设置全局的Header构建器
        SmartRefreshLayout.setDefaultRefreshHeaderCreator(new DefaultRefreshHeaderCreator() {
            @Override
            public RefreshHeader createRefreshHeader(Context context, RefreshLayout layout) {
                layout.setPrimaryColorsId(R.color.colorPrimary, android.R.color.white);//全局设置主题颜色
                return new ClassicsHeader(context);//.setTimeFormat(new DynamicTimeFormat("更新于 %s"));//指定为经典Header，默认是 贝塞尔雷达Header
            }
        });
        //设置全局的Footer构建器
        SmartRefreshLayout.setDefaultRefreshFooterCreator(new DefaultRefreshFooterCreator() {
            @Override
            public RefreshFooter createRefreshFooter(Context context, RefreshLayout layout) {
                //指定为经典Footer，默认是 BallPulseFooter
                return new ClassicsFooter(context).setDrawableSize(20);
            }
        });
    }

    static final class MyCrashCallback implements RecoveryCallback {
        @Override
        public void stackTrace(String exceptionMessage) {
            Logger.e("exceptionMessage:" + exceptionMessage);
        }

        @Override
        public void cause(String cause) {
            Logger.e("cause:" + cause);
        }

        @Override
        public void exception(String exceptionType, String throwClassName, String throwMethodName, int throwLineNumber) {
            Logger.e("exceptionClassName:" + exceptionType);
            Logger.e("throwClassName:" + throwClassName);
            Logger.e("throwMethodName:" + throwMethodName);
            Logger.e("throwLineNumber:" + throwLineNumber);
        }

        @Override
        public void throwable(Throwable throwable) {

        }
    }

    private static final String BaiduFaceLicenseID = "HuJiangAndroidApp-face-android";
    private static final String BaiduFaceLicenseName = "idl-license.face-android";

    // 初始化百度人脸采集SDK
    private void initBaiduFaceLib() {
        FaceSDKManager.getInstance().init(this, BaiduFaceLicenseID, BaiduFaceLicenseName);
        setFaceConfig();
    }

    private void setFaceConfig() {
        FaceTracker tracker = FaceSDKManager.getInstance().getFaceTracker(this);
        // SDK初始化已经设置完默认参数（推荐参数），您也根据实际需求进行数值调整

        // 模糊度范围 (0-1) 推荐小于0.7
        tracker.set_blur_thr(FaceEnvironment.VALUE_BLURNESS);
        // 光照范围 (0-1) 推荐大于40
        tracker.set_illum_thr(FaceEnvironment.VALUE_BRIGHTNESS);
        // 裁剪人脸大小
        tracker.set_cropFaceSize(FaceEnvironment.VALUE_CROP_FACE_SIZE);
        // 人脸yaw,pitch,row 角度，范围（-45，45），推荐-15-15
        tracker.set_eulur_angle_thr(FaceEnvironment.VALUE_HEAD_PITCH, FaceEnvironment.VALUE_HEAD_ROLL,
                FaceEnvironment.VALUE_HEAD_YAW);

        // 最小检测人脸（在图片人脸能够被检测到最小值）80-200， 越小越耗性能，推荐120-200
        tracker.set_min_face_size(FaceEnvironment.VALUE_MIN_FACE_SIZE);
        //
        tracker.set_notFace_thr(FaceEnvironment.VALUE_NOT_FACE_THRESHOLD);
        // 人脸遮挡范围 （0-1） 推荐小于0.5
        tracker.set_occlu_thr(FaceEnvironment.VALUE_OCCLUSION);
        // 是否进行质量检测
        tracker.set_isCheckQuality(true);
        // 是否进行活体校验
        tracker.set_isVerifyLive(false);
    }
}
