
package com.luffy88.qianghongbao;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;


public class HomeActivity extends AppCompatActivity implements View.OnClickListener {

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

        mServiceBtn = (Button)findViewById(R.id.button);
        mServiceBtn.setOnClickListener(this);
    }


    @Override
    protected void onResume() {
        super.onResume();
        myHandler.sendEmptyMessageDelayed(MSG_CHECK_SERVICE, 100);
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
