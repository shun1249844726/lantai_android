package com.lexinsmart.planmanager;

import static com.lexinsmart.util.Constants.ACTIVITYFLAG;
import static com.lexinsmart.util.Constants.EDITROADFLAG;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import cn.yydcdut.sdlv.Menu;
import cn.yydcdut.sdlv.MenuItem;
import cn.yydcdut.sdlv.SlideAndDragListView;

import com.lexinsmart.newplan.Map;
import com.lexinsmart.util.JsonTools;
import com.lexinsmart.util.StringUtil;
import com.lexinsmart_lantai.R;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.graphics.Color;
import android.media.ImageReader;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class RoadSet extends Activity implements
		SlideAndDragListView.OnListItemLongClickListener,
		SlideAndDragListView.OnDragListener,
		SlideAndDragListView.OnSlideListener,
		SlideAndDragListView.OnListItemClickListener,
		SlideAndDragListView.OnMenuItemClickListener,
		SlideAndDragListView.OnItemDeleteListener,
		SlideAndDragListView.OnListScrollListener {
	private static final String TAG = RoadSet.class.getSimpleName();
	public static final String ADDEDROADDETAILS = "ADDEDROADDETAILS";

	private Menu mMenu;
//	private List<ApplicationInfo> mAppList;
	private SlideAndDragListView<String[]> mListView;
	private Toast mToast;
	private ImageView addPlanImg;
	private Button sureBtn;
	public static final int REQUSET = 1;
	 ArrayList<String[]> newPlanDetailsList;
	ArrayList<String[]> RoadDetails;
	String roadDetails;
	String roadName = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_road_set);
		Intent intent = getIntent();
		roadDetails = intent.getStringExtra("roadDetails");
		roadName =  intent.getStringExtra("roadname");
		RoadDetails = JsonTools.changeJsonToArray(roadDetails);
//		initData();
		initMenu();
		initUiAndListener();
		addPlanImg = (ImageView) findViewById(R.id.roadset_addplanId);
		addPlanImg.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				ACTIVITYFLAG = 1;
				Intent intent = new Intent(RoadSet.this, Map.class);
				startActivityForResult(intent, REQUSET);
			}
		});
		sureBtn = (Button) findViewById(R.id.roadset_sure_BtnId);
		sureBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.putExtra(ADDEDROADDETAILS,
						(Serializable) RoadDetails);
				intent.putExtra("planName",roadName);
				setResult(RESULT_OK, intent);
				finish();
			}
		});
	}

//	public void initData() {
//		mAppList = getPackageManager().getInstalledApplications(0);
//	}

	public void initMenu() {
		mMenu = new Menu(true, true);
		mMenu.addItem(new MenuItem.Builder()
				.setWidth(
						(int) getResources().getDimension(
								R.dimen.slv_item_bg_btn_width) + 50)
				.setBackground(
						StringUtil.getDrawable(this, R.drawable.btn_right0))
				.setText("Delete").setDirection(MenuItem.DIRECTION_RIGHT)
				.setTextColor(Color.WHITE).setTextSize(14).build());
	}
	public void initUiAndListener() {
		mListView = (SlideAndDragListView) findViewById(R.id.lv_edit);
		mListView.setMenu(mMenu);
		mListView.setAdapter(mAdapter);
		mListView.setOnListItemLongClickListener(this);
		mListView.setOnDragListener(this,RoadDetails);
		mListView.setOnListItemClickListener(this);
		mListView.setOnSlideListener(this);
		mListView.setOnMenuItemClickListener(this);
		mListView.setOnItemDeleteListener(this);
		mListView.setOnListScrollListener(this);
	}

	private BaseAdapter mAdapter = new BaseAdapter() {
		@Override
		public int getCount() {
			return RoadDetails.size();
		}
		@Override
		public Object getItem(int position) {
			return RoadDetails.get(position);
		}
		@Override
		public long getItemId(int position) {
			return position;
		}
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			CustomViewHolder cvh;
			if (convertView == null) {
				cvh = new CustomViewHolder();
				convertView = LayoutInflater.from(RoadSet.this).inflate(
						R.layout.item_custom_btn, null);
				cvh.txtName = (TextView) convertView
						.findViewById(R.id.txt_item_edit);
				convertView.setTag(cvh);
			} else {
				cvh = (CustomViewHolder) convertView.getTag();
			}
			String[] item = (String[]) this.getItem(position);
			System.out.println("item["+position+"]: "+item[0]);
			cvh.txtName.setText(item[0]);
			return convertView;
		}
		class CustomViewHolder {
			public TextView txtName;
		}
	};

	/**
	 * item长按事件
	 */
	@Override
	public void onListItemLongClick(View view, int position) {
		// boolean bool = mListView.startDrag(position);
		// Toast.makeText(SimpleActivity.this, "onItemLongClick   position--->"
		// + position + "   drag-->" + bool, Toast.LENGTH_SHORT).show();
		if (mToast != null) {
			mToast.cancel();
		}
		view.setBackgroundResource(R.drawable.abc_btn_borderless_material);
		// mToast = Toast.makeText(RoadSet.this,
		// "onItemLongClick   position--->"
		// + position, Toast.LENGTH_SHORT);
		// mToast.show();
		Log.i(TAG, "onListItemLongClick   " + position);
	}

	@Override
	public void onDragViewStart(int position) {
		if (mToast != null) {
			mToast.cancel();
		}
		// mToast = Toast.makeText(RoadSet.this,
		// "onDragViewStart   position--->"
		// + position, Toast.LENGTH_SHORT);
		// mToast.show();
		Log.i(TAG, "onDragViewStart   " + position);
	}

	@Override
	public void onDragViewMoving(int position) {

		Log.i("yuyidong", "onDragViewMoving   " + position);
	}

	@Override
	public void onDragViewDown(int position) {
		if (mToast != null) {
			mToast.cancel();
		}
		// mToast = Toast.makeText(RoadSet.this, "onDragViewDown   position--->"
		// + position, Toast.LENGTH_SHORT);
		// mToast.show();
		Log.i(TAG, "onDragViewDown   " + position);
	}

	/**
	 * 单独item点击事件
	 */
	@Override
	public void onListItemClick(View v, int position) {
		if (mToast != null) {
			mToast.cancel();
		}
		// mToast = Toast.makeText(RoadSet.this, "onItemClick   position--->"
		// + position, Toast.LENGTH_SHORT);
		// mToast.show();
		Log.i(TAG, "onListItemClick   " + position);
	}

	@Override
	public void onSlideOpen(View view, View parentView, int position,
			int direction) {
		if (mToast != null) {
			mToast.cancel();
		}
		Log.i(TAG, "onSlideOpen   " + position);
	}

	@Override
	public void onSlideClose(View view, View parentView, int position,
			int direction) {
		if (mToast != null) {
			mToast.cancel();
		}
		Log.i(TAG, "onSlideClose   " + position);
	}

	/**
	 * 展开菜单项点击事件
	 */
	@Override
	public int onMenuItemClick(View v, int itemPosition, int buttonPosition,
			int direction) {
		Log.i(TAG, "onMenuItemClick   " + itemPosition + "   " + buttonPosition
				+ "   " + direction);
		switch (direction) {
		case MenuItem.DIRECTION_RIGHT:
			switch (buttonPosition) {
			case 0:
				/**
				 * 删除item
				 */
				return Menu.ITEM_DELETE_FROM_BOTTOM_TO_TOP;
			}
		}
		return Menu.ITEM_NOTHING;
	}

	/**
	 * 删除item处理事件
	 */
	@Override
	public void onItemDelete(View view, int position) {
		RoadDetails.remove(position - mListView.getHeaderViewsCount());
		mAdapter.notifyDataSetChanged();
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		switch (scrollState) {
		case SlideAndDragListView.OnListScrollListener.SCROLL_STATE_IDLE:
			break;
		case SlideAndDragListView.OnListScrollListener.SCROLL_STATE_TOUCH_SCROLL:
			break;
		case SlideAndDragListView.OnListScrollListener.SCROLL_STATE_FLING:
			break;
		}
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
	}

	/*
	 * 菜单项处理 (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
	 */
	@Override
	public boolean onCreateOptionsMenu(android.view.Menu menu) {
		// getMenuInflater().inflate(R.menu.menu_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(android.view.MenuItem item) {
		return true;
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		// requestCode标示请求的标示 resultCode表示有数据
		if (requestCode == PlanManager.REQUSET && resultCode == RESULT_OK) {
			String[] listTitle_1 = new String[4];
			newPlanDetailsList = (ArrayList<String[]>) data
					.getSerializableExtra("ADDEDROADDETAILS");
			roadName = data.getStringExtra("planName");
			RoadDetails .addAll(newPlanDetailsList);
			mAdapter.notifyDataSetChanged();
			for (int i = 0;i < RoadDetails.size(); i++) {
				for (int j = 0; j < RoadDetails.get(i).length; j++) {
					System.out.println("RoadDetails:"
							+ RoadDetails.get(i)[j]);
				}
			}
		}
	}
}