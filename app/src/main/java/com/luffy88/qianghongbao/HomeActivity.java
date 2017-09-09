
package com.luffy88.qianghongbao;

import android.content.Intent;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;


public class HomeActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        findViewById(R.id.button).setOnClickListener(this);


    }


    @Override
    public void onClick(View v) {
        if (AutoAccessibilityService.ALL) {
            AutoAccessibilityService.ALL = false;
            ((Button) v).setText("对话内监控+关");
        } else {

            Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
            startActivity(intent);

            AutoAccessibilityService.ALL = true;
            ((Button) v).setText("对话内监控+开");
        }
    }
}
