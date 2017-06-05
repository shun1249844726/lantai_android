package com.lexinsmart.gesture_lockpsd_demo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.lexinsmart.gesture_lockpsd_demo.activity.GestureVerifyActivity;
import com.lexinsmart.gesture_lockpsd_demo.application.MyApplication;
import com.lexinsmart.gesture_lockpsd_demo.utils.SPUtils;
import com.lexinsmart_lantai.R;

public class SplashActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash);
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				if (!SPUtils.get(MyApplication.getContext(), "gesturePsd", "")
						.toString().equals("")) {
					Intent intent = new Intent();
					intent.setClass(MyApplication.getContext(),
							GestureVerifyActivity.class);
					startActivity(intent);
					SplashActivity.this.finish();
				} else {
					Intent intent = new Intent();
					intent.setClass(MyApplication.getContext(), LoginActivity.class);
					startActivity(intent);
					SplashActivity.this.finish();
				}
			}
		}, 500);
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub

	}
}
