
package com.luffy88.qianghongbao;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.umeng.analytics.MobclickAgent;

import net.youmi.android.AdManager;
import net.youmi.android.nm.bn.BannerManager;
import net.youmi.android.nm.bn.BannerViewListener;
import net.youmi.android.nm.cm.ErrorCode;
import net.youmi.android.nm.sp.SpotListener;
import net.youmi.android.nm.sp.SpotManager;
import net.youmi.android.nm.sp.SpotRequestListener;


public class HomeActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "HomeActivity";
    public static final String youmiID = "34bd60e3009954cd";
    public static final String youmiKEY = "334bf39931ffd8f5";

    private Button mServiceBtn;
    private final int MSG_CHECK_SERVICE = 1;
    private final int MSG_SHOW_AD = 2;

    private RelativeLayout mSpotadlayout;

    Handler myHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == MSG_CHECK_SERVICE) {
                if (ServiceStatus.getInstance(getApplication()).serviceOn()) {

                    startService(new Intent(HomeActivity.this, NotificationService.class));
                    mServiceBtn.setText(R.string.service_opened);
                } else {
                    mServiceBtn.setText(R.string.open_service);
                }
            } else if (msg.what == MSG_SHOW_AD) {
                setupSpotAd();
            }
        }
    };

    private void setupSpotAd() {
        View spotView  = SpotManager.getInstance(getApplication()).getNativeSpot(getApplication(),
                new SpotListener() {
                    @Override
                    public void onShowSuccess() {
                        XLog.d(TAG, "onShowSuccess");
                    }

                    @Override
                    public void onShowFailed(int i) {
                        XLog.d(TAG, "onShowFailed");
                    }

                    @Override
                    public void onSpotClosed() {
                        XLog.d(TAG, "onSpotClosed");
                    }

                    @Override
                    public void onSpotClicked(boolean b) {

                    }
                });

        if (spotView != null) {
            RelativeLayout.LayoutParams layoutParams =
                    new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
            if (mSpotadlayout != null) {
                mSpotadlayout.removeAllViews();
                // 添加原生插屏控件到容器中
                mSpotadlayout.addView(spotView, layoutParams);
                if (mSpotadlayout.getVisibility() != View.VISIBLE) {
                    mSpotadlayout.setVisibility(View.VISIBLE);
                    findViewById(R.id.close_ad).setVisibility(View.VISIBLE);
                    findViewById(R.id.close_ad).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            SpotManager.getInstance(getApplication()).hideSpot();
                            mSpotadlayout.setVisibility(View.GONE);
                            findViewById(R.id.close_ad).setVisibility(View.GONE);
                        }
                    });

                }
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        AdManager.getInstance(getApplication()).init(youmiID, youmiKEY, false);

        mServiceBtn = (Button)findViewById(R.id.button);
        mServiceBtn.setOnClickListener(this);

        // 获取广告条
        View bannerView = BannerManager.getInstance(getApplication())
                .getBannerView(this, new BannerViewListener() {
                    @Override
                    public void onRequestSuccess() {
                    }

                    @Override
                    public void onSwitchBanner() {
                    }

                    @Override
                    public void onRequestFailed() {

                    }
                });

        // 获取要嵌入广告条的布局
        LinearLayout bannerLayout = (LinearLayout) findViewById(R.id.ll_banner);
        mSpotadlayout = (RelativeLayout) findViewById(R.id.spotad_layout);

        // 将广告条加入到布局中
        bannerLayout.addView(bannerView);

        startService(new Intent(this, NotificationService.class));

        boolean shAd = getIntent().getBooleanExtra("SHOWAD", false);
        XLog.d(TAG, "shAd:" + shAd);
        if (shAd) {
            SpotManager.getInstance(getApplication()).requestSpot(new SpotRequestListener() {
                @Override
                public void onRequestSuccess() {
                    XLog.d(TAG, "onRequestSuccess");
                }

                @Override
                public void onRequestFailed(int errorCode) {
                    XLog.d(TAG, "onRequestFailed");
                    switch (errorCode) {
                        case ErrorCode.NON_NETWORK:
                            XLog.d(TAG, "网络异常");
                            break;
                        case ErrorCode.NON_AD:
                            XLog.d(TAG, "暂无原生插屏广告");
                            break;
                        case ErrorCode.RESOURCE_NOT_READY:
                            XLog.d(TAG, "原生插屏资源还没准备好");
                            break;
                        case ErrorCode.SHOW_INTERVAL_LIMITED:
                            XLog.d(TAG, "请勿频繁展示");
                            break;
                        case ErrorCode.WIDGET_NOT_IN_VISIBILITY_STATE:
                            XLog.d(TAG, "请设置插屏为可见状态");
                            break;
                        default:
                            XLog.d(TAG, "请稍后再试");
                            break;
                    }

                }
            });

            SpotManager.getInstance(getApplication()).setImageType(SpotManager.IMAGE_TYPE_VERTICAL);
            SpotManager.getInstance(getApplication()).setAnimationType(SpotManager.ANIMATION_TYPE_SIMPLE);

            myHandler.sendEmptyMessageDelayed(MSG_SHOW_AD, 1000);

        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        SpotManager.getInstance(getApplication()).onPause();
        MobclickAgent.onPause(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        SpotManager.getInstance(getApplication()).onStop();
    }

    @Override
    public void onBackPressed() {
        XLog.d(TAG, "onBackPressed spotshow:" + SpotManager.getInstance(getApplication()).isSpotShowing());
        if (mSpotadlayout.getVisibility() == View.VISIBLE) {
            SpotManager.getInstance(getApplication()).hideSpot();
            mSpotadlayout.setVisibility(View.GONE);
            findViewById(R.id.close_ad).setVisibility(View.GONE);
        } else {
            super.onBackPressed();
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        myHandler.sendEmptyMessageDelayed(MSG_CHECK_SERVICE, 100);
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 展示广告条窗口的 onDestroy() 回调方法中调用
        BannerManager.getInstance(getApplication()).onDestroy();
        SpotManager.getInstance(getApplication()).onDestroy();

        SpotManager.getInstance(getApplication()).onAppExit();
    }

    @Override
    public void onClick(View v) {
        ServiceStatus ss = ServiceStatus.getInstance(getApplication());
        if (ss.serviceOn()) {
            ss.setSettingOn(false);
            ss.stopService();
            mServiceBtn.setText(R.string.service_closed);
            startService(new Intent(HomeActivity.this, NotificationService.class));
        } else {
            ss.setSettingOn(true);

            Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
            startActivity(intent);
            MobclickAgent.onEvent(getApplication(), "open_settings");
        }
    }
}
