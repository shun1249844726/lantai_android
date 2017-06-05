package com.lexinsmart.planmanager;

import static com.lexinsmart.util.Constants.ACTIVITYFLAG;
import static com.lexinsmart.util.Constants.EDITROADFLAG;
import static com.lexinsmart.util.Constants.Roads;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.lexinsmart.gesture_lockpsd_demo.utils.LogUtil;
import com.lexinsmart.gesture_lockpsd_demo.utils.ToastUtil;
import com.lexinsmart.newplan.Map;
import com.lexinsmart.planmanager.MyAdapter.Callback;
import com.lexinsmart.util.Constants;
import com.lexinsmart.util.DatabaseTools;
import com.lexinsmart.util.JsonTools;
import com.lexinsmart_lantai.R;

public class PlanManager extends Activity implements Callback {
	String tag = "PlanManager";
	private Context mContext;
	private ListView listView;
	private List<java.util.Map<String, Object>> listItems;
	private ImageButton executePlan;
	private TextView crosslist;
	// private String[] listTitle = new String[] { "201512301260", "测试方案", "n",
	// "南通，南阳" };
	private String[] listTitle = new String[] { "", "", "", "" };
	private String[] setList = new String[] { "编辑", "删除" };
	String addPlanName;
	private ImageView moreDetails, addPlan;
	private static int lastPosition = -1;
	public static final int REQUSET = 1;
	private ListView mListView;
	private static List<String[]> contentList = new ArrayList<String[]>();
	private MyAdapter adapter;
	// private static int iii;
	private static int longClickPosition = 0;
	private ArrayList<String[]> newPlanDetailsList;

	// public static int RoadsNum = 0;
	// public static String[][] Roads;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_plan_manager);
		mContext = this;
		findViews();
		initListview();
		executePlan.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (lastPosition != -1) {
					Intent intent = new Intent(PlanManager.this, FleetSet.class);
					String getCrossListText = contentList.get(lastPosition)[3];
					intent.putExtra("roadDetails", getCrossListText);
					startActivity(intent);
				} else {
					ToastUtil.showShort(mContext, "请选择执行路线");
				}
			}
		});
		addPlan.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				ACTIVITYFLAG = 1;
				Intent intent = new Intent(PlanManager.this, Map.class);
				startActivityForResult(intent, REQUSET);
			}
		});
		mListView.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				longClickPosition = position;
				showListDialog();
				return false;
			}
		});
		mListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				adapter.setSelectedIndex(position);
				adapter.notifyDataSetChanged();
				lastPosition = position;

				// for (int i = 0; i < parent.getCount(); i++) {
				// View v = parent.getChildAt(i);
				// if (position == i) {
				// v.setBackgroundColor(Color.GRAY);
				// lastPosition = position;
				// } else {
				// v.setBackgroundColor(Color.TRANSPARENT);
				// }
				//
				// }
				// ToastUtil.showShort(mContext, "dddddddd");
				// listView.setItemChecked(position, true);
				// System.out.println("lastPosition:"+lastPosition+"   Position"+position);
				// if (lastPosition != position
				// || (lastPosition == position && position == 0)) {
				// // if (lastPosition != -1) {
				// // parent.getChildAt(lastPosition).setBackgroundColor(
				// // Color.TRANSPARENT);
				// // }
				// //
				// parent.getChildAt(position).setBackgroundColor(Color.GRAY);
				//
				//
				// }
			}
		});
		// for (int i = 0; i < contentList.size(); i++) {
		// for (int j = 0; j < contentList.get(i).length; j++) {
		// System.out.println("contentList---->" + contentList.get(i)[j]);
		// }
		// }

		// for (int i = 0; i < Roads.length; i++) {
		// for (int j = 0; j < Roads[0].length; j++) {
		// System.out.print("allRoads[i][j]:" + Roads[i][j] + '\n');
		// }
		// }
	}

	private void showListDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(PlanManager.this);
		builder.setTitle("请选择操作：");
		builder.setItems(setList, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
	//			ToastUtil.showShort(mContext, setList[which]);
				switch (which) {
				case 0:
					EDITROADFLAG = 1;
					Intent intent = new Intent(PlanManager.this, RoadSet.class);
					String getCrossListText = contentList
							.get(longClickPosition)[3];
					intent.putExtra("roadDetails", getCrossListText);
					intent.putExtra("roadname",
							contentList.get(longClickPosition)[2]);
					startActivityForResult(intent, REQUSET);
					break;
				case 1:
					LogUtil.d(tag, "" + longClickPosition);
					String[] Row = new String[1];
					Row[0] = contentList.get(longClickPosition)[0];
					contentList.remove(longClickPosition);
					// System.out.println("longClickPosition:"+longClickPosition+";Row[0]:"+Row[0]);
					DatabaseTools.deleteOnwRow(mContext, Constants.DB_THING,
							Row);
					adapter.notifyDataSetChanged();
					break;
				default:
					break;
				}
			}
		});
		builder.create().show();
	}

	private void initListview() {
		mListView = (ListView) findViewById(R.id.road_listId);
		// contentList.add(listTitle);
		contentList.clear();
		contentList = DatabaseTools.qurryAllRoads(PlanManager.this,
				Constants.DB_THING);

		adapter = new MyAdapter(this, contentList, this);
		mListView.setAdapter(adapter);
		// contentList.remove(0);
		// adapter.notifyDataSetChanged();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		// requestCode标示请求的标示 resultCode表示有数据
		if (requestCode == PlanManager.REQUSET && resultCode == RESULT_OK) {
			String[] listTitle_1 = new String[4];
			newPlanDetailsList = (ArrayList<String[]>) data
					.getSerializableExtra("ADDEDROADDETAILS");
			// for (int i = 0; i < newPlanDetailsList.size(); i++) {
			// for (int j = 0; j < newPlanDetailsList.get(i).length; j++) {
			// System.out.println("newPlanDetailsList:"
			// + newPlanDetailsList.get(i)[j]);
			//
			// }
			// }
			addPlanName = data.getStringExtra("planName");
			for (int i = 0; i < newPlanDetailsList.size(); i++) {
				// System.out.println("list" + i + ":"
				// + newPlanDetailsList.get(i)[0]);
				// System.out.println("list" + i + ":"
				// + newPlanDetailsList.get(i)[1]);
				// System.out.println("list" + i + ":"
				// + newPlanDetailsList.get(i)[2]);
				// System.out.println("list" + i + ":"
				// + newPlanDetailsList.get(i)[3]);
				listTitle_1[3] += newPlanDetailsList.get(i)[0] + ";";

			}
			Calendar c = Calendar.getInstance();// 可以对每个时间域单独修
			int year = c.get(Calendar.YEAR);
			int month = c.get(Calendar.MONTH);
			int day = c.get(Calendar.DAY_OF_MONTH);
			int hour = c.get(Calendar.HOUR_OF_DAY);
			int minute = c.get(Calendar.MINUTE);
			int second = c.get(Calendar.SECOND);
			listTitle_1[0] = "" + year + month + day + hour + minute + second;
			listTitle_1[1] = addPlanName;
			listTitle_1[2] = "" + newPlanDetailsList.size();
			// contentList.add(listTitle_1);
			// adapter.notifyDataSetChanged();
			// for (int i = 0; i < newPlanDetailsList.size(); i++) {
			// System.out.println("list" + i + ":"
			// + newPlanDetailsList.get(i)[0]);
			// System.out.println("list" + i + ":"
			// + newPlanDetailsList.get(i)[1]);
			// System.out.println("list" + i + ":"
			// + newPlanDetailsList.get(i)[2]);
			// System.out.println("list" + i + ":"
			// + newPlanDetailsList.get(i)[3]);
			// }
			String json = JsonTools.changeArrayDateToJson(newPlanDetailsList);
			System.out.println("JSON:" + json);
			listTitle[0] = listTitle_1[0];
			listTitle[1] = listTitle_1[1];
			listTitle[2] = listTitle_1[2];
			listTitle[3] = json;
			contentList.add(listTitle);
			if (EDITROADFLAG == 1) {
				EDITROADFLAG = 0;
				String[] Row = new String[1];
				Row[0] = contentList.get(longClickPosition)[0];
				contentList.remove(lastPosition);
				// System.out.println("longClickPosition:"+longClickPosition+";Row[0]:"+Row[0]);
				DatabaseTools.deleteOnwRow(mContext, Constants.DB_THING, Row);
			}
			for (int i = 0; i < contentList.size(); i++) {
				for (int j = 0; j < contentList.get(i).length; j++) {
					System.out.println("readcontentList:" + i + "    "
							+ contentList.get(i)[j]);
				}
			}
			adapter.notifyDataSetChanged();
			PlanManagerTask.setActivity(mContext);
			new PlanManagerTask.SaveRodeToDb().doInBackground(listTitle);
		}
		// String json = JsonTools.changeArrayDateToJson(newPlanDetailsList);
		// Log.e("JSON", json);

		// Toast.makeText(
		// this,
		// "requestCode=" + requestCode + ":" + "resultCode=" + resultCode,
		// Toast.LENGTH_LONG).show();
	}

	private void findViews() {
		executePlan = (ImageButton) findViewById(R.id.executeplanId);
		addPlan = (ImageView) findViewById(R.id.addplanId);
	}

	@Override
	public void click(View v) {
		String setMessageText = "路口列表" + '\n';
		AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
		String getCrossListText = contentList.get((Integer) v.getTag())[3];

//		System.out.println("getCrossListText:" + getCrossListText);

		ArrayList<String[]> CrossRoadList = JsonTools
				.changeJsonToArray(getCrossListText);
//		for (int i = 0; i < CrossRoadList.size(); i++) {
//			for (int j = 0; j < CrossRoadList.get(i).length; j++) {
//				System.out.println("CrossRoadList:" + CrossRoadList.get(i)[j]);
//			}
//		}
		for (int i = 0; i < CrossRoadList.size(); i++) {
			// System.out.println("crossRoadList"+CrossRoadList.get(i)[0]);
			// System.out.println("crossRoadList"+CrossRoadList.get(i)[1]);
			setMessageText += CrossRoadList.get(i)[0] + "    "
					+ CrossRoadList.get(i)[1] + '\n';
		}
		builder.setMessage(setMessageText);
		builder.create().show();
		// Toast.makeText(
		// PlanManager.this,
		// "listview的内部的按钮被点击了！，位置是-->" + (Integer) v.getTag() + ",内容是-->"
		// + contentList.get((Integer) v.getTag())[3],
		// Toast.LENGTH_SHORT).show();
	}
}
