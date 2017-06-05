package com.lexinsmart.gesture_lockpsd_demo;

import static com.lexinsmart.util.Constants.gUserName;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.lexinsmart.gesture_lockpsd_demo.activity.GestureEditActivity;
import com.lexinsmart.gesture_lockpsd_demo.application.MyApplication;
import com.lexinsmart.gesture_lockpsd_demo.utils.SPUtils;
import com.lexinsmart.gesture_lockpsd_demo.utils.ToastUtil;
import com.lexinsmart_lantai.R;

public class LoginActivity extends BaseActivity implements OnClickListener {

	private Button loginButton;
	private EditText usernameET, userpsdET;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_gesture_main);
		initView();
//		if (!SPUtils.get(MyApplication.getContext(), "gesturePsd", "")
//				.toString().equals("")) {
//			Intent intent = new Intent();
//			intent.setClass(this, GestureVerifyActivity.class);
//			startActivity(intent);
//		}
	}

	private void initView() {
		usernameET = (EditText) findViewById(R.id.editText_username);
		userpsdET = (EditText) findViewById(R.id.editText_userpsd);
		loginButton = (Button) findViewById(R.id.button_login);
		loginButton.setOnClickListener(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.button_login:
			if (usernameET.getText().toString().trim().equals("")
					|| userpsdET.getText().toString().trim().equals("")) {
				ToastUtil.showLong(this, "用户名或密码不能为空");
			} else if (usernameET.getText().toString().trim().equals("蓝泰交通")
					&& userpsdET.getText().toString().trim().equals("123456")){
				SPUtils.put(MyApplication.getContext(), "username", usernameET
						.getText().toString().trim());
				gUserName = usernameET.getText().toString();
				SPUtils.put(MyApplication.getContext(), "userpsd", userpsdET
						.getText().toString().trim());

				Intent intent = new Intent();
				intent.setClass(this, GestureEditActivity.class);
				startActivity(intent);
                finish();
			}
			else{
				ToastUtil.showLong(this, "用户名或密码错误！请联系蓝泰公司获取授权账户密码！");

			}
			break;

		default:
			break;
		}
	}

}
