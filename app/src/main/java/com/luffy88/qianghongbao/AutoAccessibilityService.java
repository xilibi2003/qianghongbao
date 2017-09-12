package com.luffy88.qianghongbao;

import android.accessibilityservice.AccessibilityService;
import android.annotation.TargetApi;
import android.app.KeyguardManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class AutoAccessibilityService extends AccessibilityService {
    public static final String TAG = "AutoAccessibilityService";

    private List<AccessibilityNodeInfo> parents;
    private boolean autoCheckFromNf = false;
    private boolean mNeedLock = false;

    private boolean WXMAIN = false;

    private KeyguardManager km;
    private KeyguardManager.KeyguardLock kl;
    //唤醒屏幕相关
    private PowerManager pm;
    private PowerManager.WakeLock wl = null;

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        parents = new ArrayList<>();

        XLog.e(TAG, "onServiceConnected");
        ServiceStatus.getInstance(getApplication()).openService();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);

    }

    public static final int MSG_NF_SEND = 0x01;
    public static final int MSG_ITEM_CLICK = 0x02;
    public static final int MSG_BACK_CLICK = 0x03;

    private Handler myHandle = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == MSG_NF_SEND) {
                Notification notification = (Notification)msg.obj ;
                PendingIntent pendingIntent = notification.contentIntent;
                try {
                    autoCheckFromNf = true;
                    wakeAndUnlock();
                    pendingIntent.send();
                    XLog.e(TAG, "进入微信");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            else if (msg.what == MSG_ITEM_CLICK) {
                AccessibilityNodeInfo item = (AccessibilityNodeInfo)msg.obj ;
                if (item!=null) {
                    item.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                }
            }
            else if (msg.what == MSG_BACK_CLICK) {
                performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
            }
        }
    };

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {


        if(!ServiceStatus.getInstance(getApplication()).settingOn()) {
            ServiceStatus.getInstance(getApplication()).openService();
        }

        if (!"com.tencent.mm".equals(event.getPackageName()) ) {
            return;
        }

        int eventType = event.getEventType();

        switch (eventType) {
            //当通知栏发生改变时
            case AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED:

                List<CharSequence> texts = event.getText();
                if (!texts.isEmpty()) {
                    for (CharSequence text : texts) {
                        String content = text.toString();
                        XLog.e(TAG, "通知事件：" + content);

                        if (content.contains("微信红包")) {
                            if (event.getParcelableData() != null &&
                                    event.getParcelableData() instanceof Notification) {

                                Message message = myHandle.obtainMessage(MSG_NF_SEND, event.getParcelableData());
                                Random rand = new Random();
                                myHandle.sendMessageDelayed(message, 1000 + rand.nextInt(1000));   // delay1秒+ 时间，防止过快, 正常人点击应该改一秒以上
                            }
                        }
                    }
                }
                break;

            //当窗口的状态发生改变时
            case AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED:
                String className = event.getClassName().toString();
                XLog.e(TAG, "窗口的状态发生改变：" + className);
                if (className.equals("com.tencent.mm.ui.LauncherUI")) {
                    if (autoCheckFromNf) {   // 从状态栏进入
                        findHongbao();
                    }

                    autoCheckFromNf = false;
                    WXMAIN = true;
                } else if (className.contains("com.tencent.mm.plugin.luckymoney.ui.En_")) {
                    // com.tencent.mm.plugin.luckymoney.ui.En_fba4b94f  in 6.5.13
                    XLog.e(TAG, "开红包");
                    click("com.tencent.mm:id/bpe");
                    autoCheckFromNf = false;
                    WXMAIN = false;
                } else if (className.equals("com.tencent.mm.plugin.luckymoney.ui.LuckyMoneyDetailUI")) {
                    //退出红包
                    XLog.e(TAG, "退出红包");
                    if (!click("com.tencent.mm:id/hq")) {
                        sendBackMsg();
                    }

                } else {
                    WXMAIN = false;
                }
                break;

            case AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED:

                String pubclassName = event.getClassName().toString();

                XLog.e(TAG, "窗口内容改变" + pubclassName + ", from nf:" + autoCheckFromNf + ", WXMAIN:" + WXMAIN);
                int hongbaoNum = 0;
                if (!autoCheckFromNf && pubclassName.equals("android.widget.TextView")) {
                    hongbaoNum = findHongbao();
                } else if (autoCheckFromNf && WXMAIN) {
                    hongbaoNum = findHongbao();
                    autoCheckFromNf = false;
                }
                if (hongbaoNum == 0) {
                    lock();
                }
                break;
        }
    }


    private void sendClickMsg(AccessibilityNodeInfo info) {
        Message message = myHandle.obtainMessage(MSG_ITEM_CLICK, info);
        Random rand = new Random();
        myHandle.sendMessageDelayed(message, 300 + rand.nextInt(1000));   // delay 0.3s+ 时间，防止过快
    }

    private void sendBackMsg() {
        Message message = myHandle.obtainMessage(MSG_BACK_CLICK);
        Random rand = new Random();
        myHandle.sendMessageDelayed(message, 300 + rand.nextInt(1000));   // delay 0.3s+ 时间，防止过快
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    private boolean click(String clickId) {
        boolean clickok = false;
        AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();
        if (nodeInfo != null) {
            List<AccessibilityNodeInfo> list = nodeInfo.findAccessibilityNodeInfosByViewId(clickId);
            XLog.d(TAG, "clickid:" + clickId + ", list:" + list.size());
            for (AccessibilityNodeInfo item : list) {
                sendClickMsg(item);
                clickok = true;
            }
        }
        return clickok;
    }


    private int findHongbao() {

        AccessibilityNodeInfo rootNode = getRootInActiveWindow();
        recycle(rootNode);

        XLog.e(TAG, "界面红包数:" + parents.size());
        int size = parents.size();
        if (parents.size() > 0) {
            parents.get(parents.size() - 1).performAction(AccessibilityNodeInfo.ACTION_CLICK);
            parents.clear();
        }
        return size;
    }

    public void recycle(AccessibilityNodeInfo info) {
        try {
            if (info.getChildCount() == 0) {
                if (info.getText() != null) {
                    if ("领取红包".equals(info.getText().toString())) {
                        if (info.isClickable()) {
                            XLog.e(TAG, "领取红包 text click");
                            sendClickMsg(info);
                        }
                        AccessibilityNodeInfo parent = info.getParent();
                        while (parent != null) {
                            if (parent.isClickable()) {
                                parents.add(parent);
                                break;
                            }
                            parent = parent.getParent();
                        }
                    }
                }
            } else {
                for (int i = 0; i < info.getChildCount(); i++) {
                    if (info.getChild(i) != null) {
                        recycle(info.getChild(i));
                    }
                }
            }
        } catch (Exception e) {


        }
    }
    private void wakeAndUnlock()
    {
        //获取电源管理器对象
        pm=(PowerManager) getSystemService(Context.POWER_SERVICE);

        //获取PowerManager.WakeLock对象，后面的参数|表示同时传入两个值，最后的是调试用的Tag
        wl = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.SCREEN_BRIGHT_WAKE_LOCK, "bright");

        //点亮屏幕
        wl.acquire();

        //得到键盘锁管理器对象
        km= (KeyguardManager)getSystemService(Context.KEYGUARD_SERVICE);
        kl = km.newKeyguardLock("unLock");

        //解锁
        kl.disableKeyguard();
        mNeedLock = true;

    }

    private void lock() {
        if (kl != null && wl != null ) {
            //锁屏
            kl.reenableKeyguard();

            //释放wakeLock，关灯
            wl.release();
            mNeedLock = false;
        }
    }


    @Override
    public void onInterrupt() {

    }
}
