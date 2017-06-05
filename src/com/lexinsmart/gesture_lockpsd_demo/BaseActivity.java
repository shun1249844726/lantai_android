package com.lexinsmart.gesture_lockpsd_demo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.lexinsmart.gesture_lockpsd_demo.activity.GestureVerifyActivity;
import com.lexinsmart.gesture_lockpsd_demo.utils.ActivityCollector;
import com.lexinsmart.gesture_lockpsd_demo.utils.AppUtils;
import com.lexinsmart.gesture_lockpsd_demo.utils.SPUtils;

public class BaseActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ActivityCollector.addActivity(this);
	}

	@Override
	protected void onStart() {
		super.onStart();
		Object object = false;
		// SPUtils.get(this, "showGesture", new Boolean(false));
		if ((Boolean) SPUtils.get(this, "isbackground", false)) {
			Intent intent = new Intent();
			intent.setClass(this, GestureVerifyActivity.class);
			startActivity(intent);
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		
		SPUtils.put(this, "isbackground", false);
		ActivityCollector.removeActivity(this);
	}

	@Override
	protected void onStop() {
		super.onStop();

		if (AppUtils.isRunningBackground(this)) {
			SPUtils.put(this, "isbackground", true);
		}

	}

}
