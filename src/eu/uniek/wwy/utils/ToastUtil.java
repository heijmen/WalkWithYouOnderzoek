package eu.uniek.wwy.utils;

import android.content.Context;
import android.widget.Toast;

public class ToastUtil {
	
	private static Toast toast;
	
	public static void showToast(Context context, String message) {
		
			cancelCurrentToasts();
			toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
			toast.show();
		
	}
	private static void cancelCurrentToasts() {
		if (toast != null) {
			toast.cancel();
		}
	}

}
