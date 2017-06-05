package com.lexinsmart.gesture_lockpsd_demo.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.lexinsmart.gesture_lockpsd_demo.application.MyApplication;
import com.lexinsmart.gesture_lockpsd_demo.utils.SPUtils;
import com.lexinsmart.gesture_lockpsd_demo.widget.GestureContentView;
import com.lexinsmart.gesture_lockpsd_demo.widget.GestureDrawline.GestureCallBack;
import com.lexinsmart.gesture_lockpsd_demo.widget.LockIndicator;
import com.lexinsmart.main.MainActivity;
import com.lexinsmart_lantai.R;

/**
 * 
 * �����������ý���
 * 
 */
public class GestureEditActivity extends Activity implements OnClickListener {
	/** �ֻ����� */
	public static final String PARAM_PHONE_NUMBER = "PARAM_PHONE_NUMBER";
	/** ��ͼ */
	public static final String PARAM_INTENT_CODE = "PARAM_INTENT_CODE";
	/** �״���ʾ�����������룬����ѡ������ */
	public static final String PARAM_IS_FIRST_ADVICE = "PARAM_IS_FIRST_ADVICE";
	private TextView mTextTitle;
	private TextView mTextCancel;
	private LockIndicator mLockIndicator;
	private TextView mTextTip;
	private FrameLayout mGestureContainer;
	private GestureContentView mGestureContentView;
	private TextView mTextReset;
	private String mParamSetUpcode = null;
	private String mParamPhoneNumber;
	private boolean mIsFirstInput = true;
	private String mFirstPassword = null;
	private String mConfirmPassword = null;
	private int mParamIntentCode;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_gesture_edit);

		setUpViews();
		setUpListeners();
	}

	private void setUpViews() {
		mTextTitle = (TextView) findViewById(R.id.text_title);
		mTextCancel = (TextView) findViewById(R.id.text_cancel);
		mTextReset = (TextView) findViewById(R.id.text_reset);
		mTextReset.setClickable(false);
		mLockIndicator = (LockIndicator) findViewById(R.id.lock_indicator);
		mTextTip = (TextView) findViewById(R.id.text_tip);
		mGestureContainer = (FrameLayout) findViewById(R.id.gesture_container);
		// ��ʼ��һ����ʾ�������viewGroup
		mGestureContentView = new GestureContentView(this, false, "",
				new GestureCallBack() {
					@Override
					public void onGestureCodeInput(String inputCode) {
						if (!isInputPassValidate(inputCode)) {
							mTextTip.setText(Html
									.fromHtml("<font color='#c70c1e'>��������4����, ����������</font>"));
							mGestureContentView.clearDrawlineState(0L);
							return;
						}
						if (mIsFirstInput) {
							mFirstPassword = inputCode;
							updateCodeList(inputCode);
							mGestureContentView.clearDrawlineState(0L);
							mTextReset.setClickable(true);
							mTextReset
									.setText(getString(R.string.reset_gesture_code));
							mTextTip.setText(Html
									.fromHtml("<font color='#c70c1e'>������һ��</font>"));
						} else {
							if (inputCode.equals(mFirstPassword)) {
								mTextTip.setText(Html
										.fromHtml("<font color='#c70c1e'>���óɹ�</font>"));
								SPUtils.put(MyApplication.getContext(),
										"gesturePsd", inputCode);

								mGestureContentView.clearDrawlineState(0L);
								Intent intent = new Intent();
								intent.setClass(GestureEditActivity.this,
										MainActivity.class);
								startActivity(intent);
								GestureEditActivity.this.finish();
							} else {
								mTextTip.setText(Html
										.fromHtml("<font color='#c70c1e'>����һ�λ��Ʋ�һ�£������»���</font>"));
								// �����ƶ�����
								Animation shakeAnimation = AnimationUtils
										.loadAnimation(
												GestureEditActivity.this,
												R.anim.shake);
								mTextTip.startAnimation(shakeAnimation);
								// ���ֻ��Ƶ��ߣ�1.5������
								mGestureContentView.clearDrawlineState(1300L);
							}
						}
						mIsFirstInput = false;
					}

					@Override
					public void checkedSuccess() {

					}

					@Override
					public void checkedFail() {

					}
				});
		// �������ƽ�����ʾ���ĸ���������
		mGestureContentView.setParentView(mGestureContainer);
		updateCodeList("");
	}

	private void setUpListeners() {
		mTextCancel.setOnClickListener(this);
		mTextReset.setOnClickListener(this);
	}

	private void updateCodeList(String inputCode) {
		// ����ѡ���ͼ��
		mLockIndicator.setPath(inputCode);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.text_cancel:
			this.finish();
			break;
		case R.id.text_reset:
			mIsFirstInput = true;
			updateCodeList("");
			mTextTip.setText(getString(R.string.set_gesture_pattern));
			break;
		default:
			break;
		}
	}

	private boolean isInputPassValidate(String inputPassword) {
		if (TextUtils.isEmpty(inputPassword) || inputPassword.length() < 4) {
			return false;
		}
		return true;
	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		switch (event.getKeyCode()) {
		case KeyEvent.KEYCODE_BACK:

			return false;

		}
		return true;
	}

}
