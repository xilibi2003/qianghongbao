package com.luffy88.qianghongbao;

import android.os.Environment;
import android.text.format.DateFormat;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;


public class FileLog {
	private static boolean DEG = Constants.DEG_D && true;
	static int LOG_MAX_SIZE = 1 * 1024 * 1024;
	
	boolean mbChanged = false;
	synchronized public static FileLog getIns() {
		if (sIns == null) 
			sIns = new FileLog();
		
		return sIns;
	}
	
	private static FileLog sIns = null;
	private int mMyPid = android.os.Process.myPid();
	
	@SuppressWarnings("unused")
	synchronized public void chmodFile(String path, String mod){
		try {
			Runtime.getRuntime().exec("chmod " + mod + " " + path);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	

	synchronized public void writeLogSDCard(String filename, String msg) {
		
		try {
			String sdcardPath = Environment.getExternalStorageDirectory().getPath() + "/wdlauncher/";
			File fileSdDir = new File(sdcardPath);
			fileSdDir.mkdir();
			
			String line = "";
			long ltime = System.currentTimeMillis();
			line = DateFormat.format("[yyyy-MM-dd kk:mm:ss] ", ltime).toString();
			//line += "[PID:" + String.valueOf(mMyPid) + "]";
			//line += "[TID:" + String.valueOf(mMyTid) + "]";
			line += msg + "\r\n";

			File fileLog = new File(fileSdDir.getAbsolutePath() + "/" + filename);

			FileWriter fw = new FileWriter(fileLog.getAbsolutePath(), true);
			fw.write(line);
			fw.flush();
			fw.close();
		} catch (Exception e) {
			Log.e("FileLog", "FileWriter writeLog exception....");
			e.printStackTrace();
		}
	}

	public boolean copyFile(String src, String dst) {
		FileOutputStream out = null;
		FileInputStream in = null;
		
		try {
			
			out = new FileOutputStream(dst, false);
			in = new FileInputStream(src);
			
			byte buf[] = new byte[1024];
			int len;
			
			while ((len = in.read(buf)) > 0) {
			
				out.write(buf, 0, len);
			}
			
		} catch (Exception e) {
			return false;
			
		} finally {
			
			try {
			
				if (out != null)
					out.close();
				
				if (in != null)
					in.close();
				
			} catch (IOException e) {}
		}
		
		return true;
	}
}
