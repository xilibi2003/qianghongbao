package com.luffy88.qianghongbao;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.IntDef;
import android.support.v4.app.NotificationCompat;

public class NotificationService extends Service {

    private static final int  NF_ID = 1;
    private static final String TAG = "NotificationService";
    private Bitmap mIconBitmap = null;

    public NotificationService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }


    @Override
    public void onCreate() {
        super.onCreate();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        updateNotification();
        return super.onStartCommand(intent, flags, startId);
    }


    private void updateNotification() {
        NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        mIconBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher);

        long passTime = System.currentTimeMillis() - ServiceStatus.getInstance(getApplication()).getLastActiveTime() ;

        Intent hi = new Intent(this, HomeActivity.class);
        String title = "微信抢红包神器";
        String msg="";

        if (ServiceStatus.getInstance(getApplication()).serviceOn()) {

            if (passTime > 0) {
                if (passTime < 1000 * 60 * 10) {  // 10分钟内
                    title = "微信抢红包神器已开启";
                    msg = "点击这里有惊喜，好玩的应用等着你";
                    hi.putExtra("SHOWAD", true);
                }
                else if (passTime < 1000 * 60 * 30)   // 30分钟内
                {
                    title = "微信抢红包神器已开启";
                    msg = "时刻准备着为主人抢红包";
                    hi.putExtra("SHOWAD", true);
                } else if (passTime < 1000 * 60 * 30) {
                    title = "微信抢红包神器迷失了自己";
                    msg = "进入应用看看神器是否正常工作";
                    hi.putExtra("SHOWAD", false);
                }

            } else {
                title = "微信抢红包神器迷失了自己";
                msg = "进入应用看看神器是否正常工作";
                hi.putExtra("SHOWAD", false);
            }

        } else {
            title = "微信抢红包神器已停用";
            msg = "进入应用重新打开吧";
            hi.putExtra("SHOWAD", false);
        }

        PendingIntent pi = PendingIntent.getActivity(this, 100, hi, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder nfbuilder = new NotificationCompat.Builder(this);
        nfbuilder.setContentTitle(title)
                .setContentText(msg)
                .setOngoing(true)
                .setLargeIcon(mIconBitmap)
                .setSmallIcon(R.drawable.sm_icon)
                .setColor(Color.parseColor("#ec1944"))
                .setContentIntent(pi);

        nm.notify(NF_ID, nfbuilder.build() );
    }

}
