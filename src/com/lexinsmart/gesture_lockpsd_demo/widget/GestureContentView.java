package com.lexinsmart.gesture_lockpsd_demo.widget;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.lexinsmart.gesture_lockpsd_demo.common.AppUtil;
import com.lexinsmart.gesture_lockpsd_demo.entity.GesturePoint;
import com.lexinsmart.gesture_lockpsd_demo.widget.GestureDrawline.GestureCallBack;
import com.lexinsmart_lantai.R;

/**
 * ��������������
 * 
 */
public class GestureContentView extends ViewGroup {

	private int baseNum = 6;

	private int[] screenDispaly;

	/**
	 * ÿ��������Ŀ���
	 */
	private int blockWidth;
	/**
	 * ����һ������������װ���꼯��
	 */
	private List<GesturePoint> list;
	private Context context;
	private boolean isVerify;
	private GestureDrawline gestureDrawline;

	/**
	 * ����9��ImageView����������ʼ��
	 * 
	 * @param context
	 * @param isVerify
	 *            �Ƿ�ΪУ����������
	 * @param passWord
	 *            �û���������
	 * @param callBack
	 *            ���ƻ�����ϵĻص�
	 */
	public GestureContentView(Context context, boolean isVerify,
			String passWord, GestureCallBack callBack) {
		super(context);
		screenDispaly = AppUtil.getScreenDispaly(context);
		blockWidth = screenDispaly[0] / 4;
		this.list = new ArrayList<GesturePoint>();
		this.context = context;
		this.isVerify = isVerify;
		// ����9��ͼ��
		addChild();
		// ��ʼ��һ�����Ի��ߵ�view
		gestureDrawline = new GestureDrawline(context, list, isVerify,
				passWord, callBack);
	}

	private void addChild() {
		for (int i = 0; i < 9; i++) {
			ImageView image = new ImageView(context);
			image.setBackgroundResource(R.drawable.gesture_node_normal);
			this.addView(image);
			invalidate();
			// �ڼ���
			int row = i / 3;
			// �ڼ���
			int col = i % 3;
			// ������ÿ������
			int leftX = col * blockWidth + blockWidth / baseNum;
			int topY = row * blockWidth + blockWidth / baseNum;
			int rightX = col * blockWidth + blockWidth - blockWidth / baseNum;
			int bottomY = row * blockWidth + blockWidth - blockWidth / baseNum;
			GesturePoint p = new GesturePoint(leftX, rightX, topY, bottomY,
					image, i + 1);
			this.list.add(p);
		}
	}

	public void setParentView(ViewGroup parent) {
		// �õ���Ļ�Ŀ���
		int width = screenDispaly[0]*3/4;
		FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(width, width);
		layoutParams.gravity = Gravity.CENTER;
		this.setLayoutParams(layoutParams);
		
		gestureDrawline.setLayoutParams(layoutParams);
		
		parent.addView(gestureDrawline);
		parent.addView(this);
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		for (int i = 0; i < getChildCount(); i++) {
			// �ڼ���
			int row = i / 3;
			// �ڼ���
			int col = i % 3;
			View v = getChildAt(i);
			v.layout(col * blockWidth + blockWidth / baseNum, row * blockWidth
					+ blockWidth / baseNum, col * blockWidth + blockWidth
					- blockWidth / baseNum, row * blockWidth + blockWidth
					- blockWidth / baseNum);
		}
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		// ��������ÿ����view�Ĵ�С
		for (int i = 0; i < getChildCount(); i++) {
			View v = getChildAt(i);
			v.measure(widthMeasureSpec, heightMeasureSpec);
		}
	}

	/**
	 * ����·��delayTimeʱ�䳤
	 * 
	 * @param delayTime
	 */
	public void clearDrawlineState(long delayTime) {
		gestureDrawline.clearDrawlineState(delayTime);
	}

}