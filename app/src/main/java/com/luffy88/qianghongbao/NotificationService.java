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

        PendingIntent pi = PendingIntent.getActivity(this, 100, new Intent(this, HomeActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder nfbuilder = new NotificationCompat.Builder(this);
        nfbuilder.setContentTitle("微信抢红包神器")
                .setContentText("微信抢红包神器")
                .setOngoing(true)
                .setLargeIcon(mIconBitmap)
                .setSmallIcon(R.drawable.sm_icon)
                .setColor(Color.parseColor("#ec1944"))
                .setContentIntent(pi);

        nm.notify(NF_ID, nfbuilder.build() );
    }

}
