package com.demo.sdk6x.utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.widget.Toast;

import com.hikvision.vmsnetsdk.VMSNetSDK;

public final class UIUtil {
	private static ProgressDialog dialog;

	private UIUtil() {
	}

	public static void showToast(Context c, int resId) {
		Toast.makeText(c, resId, Toast.LENGTH_SHORT).show();
	}

	public static void showToast(Context c, String desc) {
		Toast.makeText(c, desc, Toast.LENGTH_SHORT).show();
	}

	public static void showProgressDialog(Context c, String msg) {
		dialog = ProgressDialog.show(c, "", msg);
	}

	public static void showProgressDialog(Context c, int resId) {
		dialog = ProgressDialog.show(c, "", c.getString(resId));
	}

	public static void cancelProgressDialog() {
		if (dialog != null) {
			dialog.cancel();
			dialog = null;
		}
	}

	public static String getErrorDesc() {
		int errorCode = VMSNetSDK.getInstance().getLastErrorCode();
		String errorDesc = VMSNetSDK.getInstance().getLastErrorDesc();
		return "errorCode:" + errorCode + ",errorDesc:" + errorDesc;
	}
}
