
package com.luffy88.qianghongbao;

import android.util.Log;

import java.util.ArrayList;

public class XLog {
    public static final String TAG = "QHB";

    private static final ArrayList<String> sLogTags = new ArrayList<String>();
    static {
        sLogTags.add("");
    }


    private static Integer sLogCount = 0;

    public static void d(String subTag, boolean[] values) {
        String valueStr = "";
        for (boolean value: values
             ) {
            valueStr += String.valueOf(value) + ",";
        }

        d(subTag, valueStr);
    }

    public static void d(String subTag, String[] values) {
        String valueStr = "";
        for (String value: values
                ) {
            valueStr += value  + ",";
        }

        d(subTag, valueStr);
    }


    public static void d(String subTag, String msg) {
        if (!Constants.DEG_D) {
            return ;
        }
        synchronized (sLogCount) {
            sLogCount++;
        }
        Log.d(TAG , "[" + sLogCount + "][" + subTag + "]" + msg);
        if (sLogTags.contains(subTag)) {
            FileLog.getIns().writeLogSDCard("logs.txt", "[" + subTag + "]" + msg);
        }
    }

    public static void d(String msg) {
        if (!Constants.DEG_D) {
            return ;
        }
        Log.d(TAG , "[" + sLogCount + "] " + getLogMsg(msg));
    }

    public static void i(String subTag, String msg) {
        if (!Constants.DEG_D) {
            return ;
        }
        synchronized (sLogCount) {
            sLogCount++;
        }
        Log.i(TAG, "[" + sLogCount + "][" + subTag + "]" + msg);
    }

    public static void i(String msg) {
        if (!Constants.DEG_D) {
            return ;
        }
        Log.i(TAG , "[" + sLogCount + "]"+ getLogMsg(msg));
    }

    public static void w(String subTag, String msg, Throwable t) {
        if (!Constants.DEG_D) {
            return ;
        }
        synchronized (sLogCount) {
            sLogCount++;
        }
        Log.w(TAG, "[" + sLogCount + "][" + subTag + "]" + msg, t);
    }

    public static void w(String subTag, String msg) {
        if (!Constants.DEG_D) {
            return ;
        }
        synchronized (sLogCount) {
            sLogCount++;
        }
        Log.w(TAG , "[" + sLogCount + "][" + subTag + "]" + msg);
    }

    public static void w(String msg) {
        if (!Constants.DEG_D) {
            return ;
        }
        Log.w(TAG, "[" + sLogCount + "]" + getLogMsg(msg));
    }

    public static void e(String subTag, String msg, Throwable t) {
        if (!Constants.DEG_D) {
            return ;
        }
        synchronized (sLogCount) {
            sLogCount++;
        }
        Log.e(TAG, "[" + sLogCount + "][" + subTag + "]" + msg, t);
    }

    public static void e(String subTag, String msg) {
        if (!Constants.DEG_D) {
            return ;
        }
        synchronized (sLogCount) {
            sLogCount++;
        }
        Log.e(TAG, "[" + sLogCount + "][" + subTag + "]" + msg );
        if (sLogTags.contains(subTag)) {
            FileLog.getIns().writeLogSDCard("logs.txt", "[" + subTag + "]" + msg);
        }
    }

    public static void e(String msg) {
        if (!Constants.DEG_D) {
            return ;
        }
        Log.e(TAG, "[" + sLogCount + "]"  + getLogMsg(msg));
    }

    private static String getLogMsg(String msg) {
        final Throwable t = new Throwable();
        final StackTraceElement[] elements = t.getStackTrace();

        String callerClassName = elements[2].getClassName();
        String callerMethodName = elements[2].getMethodName();

        int pos = callerClassName.lastIndexOf('.');
        if (pos >= 0) {
            callerClassName = callerClassName.substring(pos + 1);
        }

        synchronized (sLogCount) {
            sLogCount++;
        }
        final String subtag = callerClassName;
        return "[" + subtag + "." + callerMethodName + "] "+  msg;

    }

    public static void logStack(String subTag, int layer) {

        final Throwable t = new Throwable();
        final StackTraceElement[] elements = t.getStackTrace();

        String callerClassName = elements[layer].getClassName();
        String callerMethodName = elements[layer].getMethodName();

        int pos = callerClassName.lastIndexOf('.');
        if (pos >= 0) {
            callerClassName = callerClassName.substring(pos + 1);
        }

        synchronized (sLogCount) {
            sLogCount++;
        }
        final String subtag = callerClassName;
        Log.e(TAG, "[" + sLogCount + "]" + "[" + subTag + "]" + "[" + subtag + "." + callerMethodName + "] ");

    }

    public static void printCharInt(CharSequence msg) {
        int length = msg.length();
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < length; i++) {
            sb.append((int)msg.charAt(i));
            sb.append(" ");
        }
        Log.e("PrintChar", sb.toString());
    }
}
