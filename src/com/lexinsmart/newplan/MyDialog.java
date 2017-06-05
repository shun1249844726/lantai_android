package com.lexinsmart.newplan;

import java.util.ArrayList;
import java.util.HashMap;

import android.R.string;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.baidu.location.e.p;
import com.lexinsmart_lantai.R;

public class MyDialog extends Dialog implements
		android.view.View.OnClickListener {
	private Context mContext;
	private ICustomDialogEventListener mCustomDialogEventListener;
	private String mStr;
	private TextView clickedItemTv;
	int[] gridImgIds = new int[] { R.drawable.northwest, R.drawable.north,
			R.drawable.northeast, R.drawable.west,
			R.drawable.traffic_light_high, R.drawable.east,
			R.drawable.southwest, R.drawable.south, R.drawable.southeast };
	private GridView gridView;
	String[] returned = new String[4];
	private static int itemClickPosition = 0;
	String[] orientations = new String[] { "西北","北" ,"东北", "西", "请选择进入路口方向", "东",
			"西南", "南", "东南" };

	private TextView orientation_title;

	// 增加一个回调函数,用以从外部接收返回值
	public interface ICustomDialogEventListener {
		public void customDialogEvent(String[] returned);
	}

	public MyDialog(Context context) {
		super(context);

	}

	public MyDialog(Context context, String str,
			ICustomDialogEventListener listener, int theme,String longitude,String latitude) {
		super(context, theme);
		mContext = context;
		mStr = str;
		mCustomDialogEventListener = listener;
		returned[2]=longitude;
		returned[3]=latitude;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		LayoutInflater inflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View layout = inflater.inflate(R.layout.activity_map_orientation, null);
		findViews(layout);
		orientation_title.setText(mStr);
		creatGridView();
		gridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				itemClickPosition = position;
				clickedItemTv.setText(orientations[position]);
			}
		});
		this.setContentView(layout);
	}

	private void creatGridView() {
		ArrayList<HashMap<String, Object>> lstImageItem = new ArrayList<HashMap<String, Object>>();
		for (int i = 0; i < gridImgIds.length; i++) {
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("ItemImage", gridImgIds[i]);// 添加图像资源的ID
			lstImageItem.add(map);
		}
		// 生成适配器的ImageItem <====> 动态数组的元素，两者一一对应
		SimpleAdapter saImageItems = new SimpleAdapter(mContext, lstImageItem,
				R.layout.map_dialog_grid_item, new String[] { "ItemImage" },
				new int[] { R.id.ItemImage });
		// 添加并且显示
		gridView.setAdapter(saImageItems);
		// 添加消息处理
		// gridView.setOnItemClickListener(new ItemClickListener());

	}

	private void findViews(View layout) {
		orientation_title = (TextView) layout
				.findViewById(R.id.orientation_titleId);
		gridView = (GridView) layout.findViewById(R.id.gridView1);
		Button orientationCancleID = (Button) layout
				.findViewById(R.id.orientationCancleID);
		Button orientationSureID = (Button) layout
				.findViewById(R.id.orientationSureID);
		clickedItemTv = (TextView)layout.findViewById(R.id.clickedItemTvId);
		orientationCancleID.setOnClickListener(this);
		orientationSureID.setOnClickListener(this);
	}

	@Override
	public void onClick(View view) {
		int id = view.getId();
		switch (id) {
		case R.id.orientationSureID:
			String clickedItemText = clickedItemTv.getText().toString();
			int temp = 0;

//			switch (clickedItemText) {
//			case "北":
//				temp = 1;
//				break;
//			case "西北":
//				temp = 2;
//				break;
//			case "西":
//				temp = 3;
//				break;
//			case "西南":
//				temp = 4;
//				break;
//			case "南":
//				temp = 5;
//				break;
//			case "东南":
//				temp = 6;
//				break;
//			case "东":
//				temp = 7;
//				break;
//			case "东北":
//				temp = 8;
//				break;
//				
//			default:
//				temp = 0;
//				break;
//			}
			returned[0]= mStr;
			returned[1]= clickedItemText;
			if (clickedItemText!="选择路口进入方向") {
				mCustomDialogEventListener.customDialogEvent(returned);

			}
			
			break;

		default:
			break;
		}
		dismiss();
	}

}
