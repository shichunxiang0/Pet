package com.aidigame.hisun.imengstar.util;

import android.util.Log;

public class LogUtil {
	public static boolean isDebug=false;
	public static void i(String tag,String info){
		if(isDebug){
			Log.i(tag, info);
		}
	}
}
