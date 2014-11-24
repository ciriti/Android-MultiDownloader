package com.ciriti.multifilesdownloader.util;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.regex.Pattern;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Environment;

import com.ciriti.multifilesdownloader.bean.GameItemBean;
import com.ciriti.multifilesdownloader.bean.ItemCurrentProgress;

/**
 * 
 * @author carmelo.iriti
 *
 */
public class Utils {
	public static int getNumCores() {

		int numOfCore = 0;
		//Private Class to display only CPU devices in the directory listing
		class CpuFilter implements FileFilter {
			@Override
			public boolean accept(File pathname) {
				//Check if filename is "cpu", followed by a single digit number
				if(Pattern.matches("cpu[0-9]+", pathname.getName())) {
					return true;
				}
				return false;
			}      
		}

		try {
			//Get directory containing CPU info
			File dir = new File("/sys/devices/system/cpu/");
			//Filter to only list the devices we care about
			File[] files = dir.listFiles(new CpuFilter());
			//Return the number of cores (virtual CPU devices)
			numOfCore =  files.length;
		} catch(Exception e) {
			//Default to return 1 core
			numOfCore = 1;
		}

		AcoLog.i(Utils.class.getCanonicalName(), "numOfCore[" + numOfCore + "]");

		return numOfCore;
	}
	
	public static void startInstallGame(Context context, GameItemBean gameItemBean){
		startInstallGame(context, gameItemBean.getAbsolutePathFile());
	}

	public static void startInstallGame(Context context, ItemCurrentProgress itemCurrentProgress){
		startInstallGame(context, itemCurrentProgress.getAbsolutePathFile());
	}
	
	public static void startInstallGame(Context context, String absolutePathFile){
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setDataAndType(Uri.fromFile(new File(absolutePathFile)), "application/vnd.android.package-archive");
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(intent);
	}
	
	public static void uninstallApp(Context context, String packageName){
		Intent intent = new Intent(Intent.ACTION_DELETE);
		intent.setData(Uri.parse("package:" + packageName));
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(intent);
	}
	
	public static boolean renameFileComplete(ItemCurrentProgress item){
		AcoLog.i(Utils.class.getName(), "Status file Tmp[" + item.getAbsolutePathFile() + "]");
		AcoLog.i(Utils.class.getName(), "Status file Completed[" + Environment.getExternalStorageDirectory().getPath() + "/" + Environment.DIRECTORY_DOWNLOADS + "/" + item.getName() + ".apk" + "]");
		return new File(item.getAbsolutePathFile()).renameTo(new File(Environment.getExternalStorageDirectory().getPath() + "/" + Environment.DIRECTORY_DOWNLOADS + "/" + item.getName() + ".apk"));
	}
	
	public static String buildPathFileTmp(String name){
		return Environment.getExternalStorageDirectory().getPath() + "/" + Environment.DIRECTORY_DOWNLOADS + "/" + name + "_tmp.apk";
	}
	
	public static String buildPathFile(String name){
		return Environment.getExternalStorageDirectory().getPath() + "/" + Environment.DIRECTORY_DOWNLOADS + "/" + name + ".apk";
	}
	
	public static boolean isPackageInstalled(String packagename, Context context) {
	    PackageManager pm = context.getPackageManager();
	    try {
	        pm.getPackageInfo(packagename, PackageManager.GET_ACTIVITIES);
	        return true;
	    } catch (NameNotFoundException e) {
	        return false;
	    }
	}
	
	public static void copy(File src, File dst) throws IOException {
	    InputStream in = new FileInputStream(src);
	    OutputStream out = new FileOutputStream(dst);

	    // Transfer bytes from in to out
	    byte[] buf = new byte[1024];
	    int len;
	    while ((len = in.read(buf)) > 0) {
	        out.write(buf, 0, len);
	    }
	    in.close();
	    out.close();
	}
	
	public static boolean isFileExist(String gameName){
		boolean res = false;
		if(new File(Utils.buildPathFile(gameName)).exists())
			res = true;
		return res;
	}


}
