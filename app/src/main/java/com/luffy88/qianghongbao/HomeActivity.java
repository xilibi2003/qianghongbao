
package com.luffy88.qianghongbao;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import net.youmi.android.AdManager;
import net.youmi.android.nm.bn.BannerManager;
import net.youmi.android.nm.bn.BannerViewListener;


public class HomeActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "HomeActivity";

    private Button mServiceBtn;
    private final int MSG_CHECK_SERVICE = 1;

    Handler myHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == MSG_CHECK_SERVICE) {
                if (ServiceStatus.getInstance(getApplication()).serviceOn()) {
                    mServiceBtn.setText(R.string.service_opened);
                } else {
                    mServiceBtn.setText(R.string.open_service);
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        AdManager.getInstance(getApplication()).init(BuildConfig.youmiID, BuildConfig.youmiKEY, false);

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
        // 将广告条加入到布局中
        bannerLayout.addView(bannerView);

        startService(new Intent(this, NotificationService.class));
    }


    @Override
    protected void onResume() {
        super.onResume();
        myHandler.sendEmptyMessageDelayed(MSG_CHECK_SERVICE, 100);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 展示广告条窗口的 onDestroy() 回调方法中调用
        BannerManager.getInstance(getApplication()).onDestroy();
    }

    @Override
    public void onClick(View v) {
        ServiceStatus ss = ServiceStatus.getInstance(getApplication());
        if (ss.serviceOn()) {
            ss.setSettingOn(false);
            ss.stopService();
            mServiceBtn.setText(R.string.service_closed);
        } else {
            ss.setSettingOn(true);

            Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
            startActivity(intent);
        }
    }
}
