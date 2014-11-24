package com.ciriti.multifilesdownloader.util;

import android.util.Log;


/**
 * 
 * @author carmelo.iriti
 *
 */
public class AcoLog {
	
	public static LEVEL logType = DownloaderConfig.DEBUG?LEVEL.VERBOSE:LEVEL.NO_LOG;
	
	enum LEVEL{
		 NO_LOG, ASSERT, ERROR, WARNING, INFO, DEBUG, VERBOSE
	}
	
	public static int i(String tag, String msg){
		return ((logType.ordinal() >= LEVEL.INFO.ordinal()))?Log.i(tag, msg):0; 
	}
	
	public static int d(String tag, String msg){
		return ((logType.ordinal() >= LEVEL.DEBUG.ordinal()))?Log.d(tag, msg):0;
	}
	
	public static int e(String tag, String msg){
		return ((logType.ordinal() >= LEVEL.ERROR.ordinal()))?Log.e(tag, msg):0;
	}
	
	public static int v(String tag, String msg){
		return ((logType.ordinal() >= LEVEL.VERBOSE.ordinal()))?Log.v(tag, msg):0;
	}
	
	public static int w(String tag, String msg){
		return ((logType.ordinal() >= LEVEL.WARNING.ordinal()))?Log.w(tag, msg):0;
	}
	
	public static int wtf(String tag, String msg){
		return ((logType.ordinal() >= LEVEL.ASSERT.ordinal()))?Log.wtf(tag, msg):0;
	}
	
	public static int i(String tag, String msg, Throwable tr){
		return ((logType.ordinal() >= LEVEL.INFO.ordinal()))?Log.i(tag, msg, tr):0; 
	}
	
	public static int d(String tag, String msg, Throwable tr){
		return ((logType.ordinal() >= LEVEL.DEBUG.ordinal()))?Log.d(tag, msg, tr):0;
	}
	
	public static int e(String tag, String msg, Throwable tr){
		return ((logType.ordinal() >= LEVEL.ERROR.ordinal()))?Log.e(tag, msg, tr):0;
	}
	
	public static int v(String tag, String msg, Throwable tr){
		return ((logType.ordinal() >= LEVEL.VERBOSE.ordinal()))?Log.v(tag, msg, tr):0;
	}
	
	public static int w(String tag, String msg, Throwable tr){
		return ((logType.ordinal() >= LEVEL.WARNING.ordinal()))?Log.w(tag, msg, tr):0;
	}
	
	public static int wtf(String tag, String msg, Throwable tr){
		return ((logType.ordinal() >= LEVEL.ASSERT.ordinal()))?Log.wtf(tag, msg, tr):0;
	}

}
