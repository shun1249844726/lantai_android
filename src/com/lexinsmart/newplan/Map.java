package com.lexinsmart.newplan;

import static com.lexinsmart.util.Constants.ACTIVITYFLAG;
import static com.lexinsmart.util.Constants.Roads;
import java.io.Serializable;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMap.OnMapStatusChangeListener;
import com.baidu.mapapi.map.BaiduMap.OnMarkerClickListener;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration.LocationMode;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.CoordinateConverter;
import com.baidu.mapapi.utils.CoordinateConverter.CoordType;
import com.lexinsmart.gesture_lockpsd_demo.utils.ToastUtil;
import com.lexinsmart_lantai.R;

public class Map extends Activity {
	private Marker mMarkerA;
	private Marker mMarkerB;
	// 初始化全局 bitmap 信息，不用时及时 recycle
	BitmapDescriptor bdA;
	private MapView mMapView;

	private LocationMode mCurrentMode;
	TextView roadName;
	// UI相关
	Button requestLocButton;
	BaiduMap mBaiduMap;
	BitmapDescriptor mCurrentMarker;
	LocationClient mLocClient;
	private LinearLayout addedRoadLL;
	private TextView addedRoadTv;
	private Button savePlanBtn,chexiaoBtn;
	private EditText planNameEdt;
	private Context mContext;
	private static int lastZoomStatus = 0;
	public MyLocationListenner myListener = new MyLocationListenner();
	boolean isFirstLoc = true;// 是否首次定位
	public static int RoadsNum = 0;
	// public static String[][] Roads;
	public static final String ADDEDROADDETAILS = "ADDEDROADDETAILS";
	public ArrayList<String[]> addedRoadList = new ArrayList<String[]>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;
		SDKInitializer.initialize(getApplicationContext());
		setContentView(R.layout.activity_newplan_map);
		// //////////////////////
		for (int i = 0; i < Roads.length; i++) {
			for (int j = 0; j < Roads[0].length; j++) {
				System.out.print("Roads[i][j]:" + Roads[i][j] + '\n');
			}
		}

		RoadsNum = Roads.length;

		bdA = BitmapDescriptorFactory.fromResource(R.drawable.traffic_light);

		mCurrentMode = LocationMode.NORMAL;

		// mBaiduMap.setMyLocationConfigeration(new MyLocationConfiguration(
		// mCurrentMode, true, mCurrentMarker));

		addedRoadLL = (LinearLayout) findViewById(R.id.addroadLLId);
		addedRoadTv = (TextView) findViewById(R.id.addedroadId);
		addedRoadTv.setMovementMethod(new ScrollingMovementMethod());
		savePlanBtn = (Button) findViewById(R.id.saveplanId);
		chexiaoBtn = (Button) findViewById(R.id.add_chexiaoBtnId);
		planNameEdt = (EditText) findViewById(R.id.planNameEdtId);
		planNameEdt.requestFocus() ;
		// 地图初始化
		mMapView = (MapView) findViewById(R.id.bmapView);

		mBaiduMap = mMapView.getMap();
		MapStatusUpdate msu = MapStatusUpdateFactory.zoomTo(16.0f);
		
		mBaiduMap.setMapStatus(msu);
		// 开启定位图层
//		mBaiduMap.setMyLocationEnabled(true);
		// 定位初始化
//		mLocClient = new LocationClient(this);
//		mLocClient.registerLocationListener(myListener);
//		LocationClientOption option = new LocationClientOption();
//		option.setOpenGps(true);// 打开gps
//		option.setCoorType("bd09ll"); // 设置坐标类型
//		option.setScanSpan(1000);
//		mLocClient.setLocOption(option);
//		mLocClient.start();
		//设定中心点坐标 

        LatLng cenpt = new LatLng(Double.valueOf(Roads[0][5]),Double.valueOf(Roads[0][4])); 
        //定义地图状态
        MapStatus mMapStatus = new MapStatus.Builder()
        .target(cenpt)
        .zoom(15)
        .build();
        //定义MapStatusUpdate对象，以便描述地图状态将要发生的变化
        MapStatusUpdate mMapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mMapStatus);
        //改变地图状态
        mBaiduMap.setMapStatus(mMapStatusUpdate);
		
		initOverlay();
		mBaiduMap.setOnMapStatusChangeListener(new OnMapStatusChangeListener() {

			@Override
			public void onMapStatusChangeStart(MapStatus arg0) {
			}
			@Override
			public void onMapStatusChangeFinish(MapStatus arg0) {
//				int zoomStatus = (int) mBaiduMap.getMapStatus().zoom;
//				// System.out.println("zoomStatus" + zoomStatus);
//				if (lastZoomStatus != zoomStatus) {
//					if (zoomStatus == 13) {
//						mBaiduMap.clear();
//						mMarkerA = null;
//						showZoomIn();
//					} else if (zoomStatus == 14) {
//						mBaiduMap.clear();
//						mMarkerB = null;
//						initOverlay();
//					}
//				}
//				lastZoomStatus = zoomStatus;
			}
			@Override
			public void onMapStatusChange(MapStatus arg0) {

			}
		});
		if (ACTIVITYFLAG == 1) {
			ACTIVITYFLAG = 0;
			mBaiduMap.setOnMarkerClickListener(new MarkerClickListener());
		}
		savePlanBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (!planNameEdt.getText().toString().equals("")) {
					Intent intent = new Intent();
					intent.putExtra(ADDEDROADDETAILS,
							(Serializable) addedRoadList);
					intent.putExtra("planName", planNameEdt.getText()
							.toString());
					setResult(RESULT_OK, intent);
					finish();
				} else {
					ToastUtil.showShort(mContext, "请输入方案名称");
				}

			}
		});
		chexiaoBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(addedRoadList.size()>0){
					addedRoadList.remove(addedRoadList.size()-1);
					addedRoadTv.setText("");
					for(int i = 0;i<addedRoadList.size();i++){
						addedRoadTv.setText(addedRoadTv.getText()+addedRoadList.get(i)[0] + "    "+addedRoadList.get(i)[1]+ ";" + '\n');
					}
				}			
			}
		});
	}
	class MarkerClickListener implements OnMarkerClickListener {

		@Override
		public boolean onMarkerClick(Marker marker) {
			String markerTitle = marker.getTitle();
			float longitude, latitude;
			longitude = (float) marker.getPosition().longitude;
			latitude = (float) marker.getPosition().latitude;
			System.out.println("longitude:" + longitude + ";" + "latitude:"
					+ latitude);
			MyDialog dialog = new MyDialog(Map.this, markerTitle,
					new MyDialog.ICustomDialogEventListener() {
						@Override
						public void customDialogEvent(String[] returned) {
							// TextView textView1 = (TextView)
							// findViewById(R.id.textView1);
							// textView1.setText(""+id);
							addedRoadLL.setVisibility(View.VISIBLE);
							addedRoadList.add(returned);
							// for (int i = 0; i < addedRoadList.size(); i++) {
							// System.out.println(addedRoadList.get(i)[0]);
							// System.out.println(addedRoadList.get(i)[1]);
							// }
							addedRoadTv.setText(addedRoadTv.getText() + 
									 returned[0] + "    "+returned[1] + ";" + '\n');
						}
					}, R.style.dialog, "" + longitude, "" + latitude);
			dialog.show();

			return true;
		}
	}

	private void showZoomIn() {
		double[][] coordinates = getCoordinates();
		// System.out.println("coordinates[i][0]:" + coordinates[0][0]
		// + ";coordinates[i][1]:" + coordinates[0][1]);
		LatLng llA = new LatLng(coordinates[0][1], coordinates[0][0]);
		// 将GPS设备采集的原始GPS坐标转换成百度坐标
		CoordinateConverter converter = new CoordinateConverter();
		converter.from(CoordType.GPS);
		// sourceLatLng待转换坐标
		converter.coord(llA);
		LatLng desLatLng = converter.convert();
		TextView tv = new TextView(Map.this);
		tv.setPadding(30, 16, 30, 30);
		tv.setGravity(Gravity.CENTER_HORIZONTAL);
		tv.setText("" + RoadsNum);
		tv.setTextColor(Color.parseColor("#ffffff"));
		tv.setBackgroundResource(R.drawable.icon_gcoding);
		BitmapDescriptor bd = BitmapDescriptorFactory
				.fromBitmap(getBitmapFromView(tv));
		MarkerOptions ooB = new MarkerOptions().position(desLatLng).icon(bd)
				.zIndex(9).draggable(true).title(Roads[0][2]);
		mMarkerA = (Marker) (mBaiduMap.addOverlay(ooB));

	}

	private void initOverlay() {
		double[][] coordinates = getCoordinates();
		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.marker, null);
		roadName = (TextView) view.findViewById(R.id.roadnameId);

		for (int i = 0; i < coordinates.length; i++) {

			// System.out.println("coordinates[i][0]:" + coordinates[i][0]
			// + ";coordinates[i][1]:" + coordinates[i][1]);
			LatLng llA = new LatLng(coordinates[i][1], coordinates[i][0]);

			// 将GPS设备采集的原始GPS坐标转换成百度坐标
			CoordinateConverter converter = new CoordinateConverter();
			converter.from(CoordType.GPS);
			// sourceLatLng待转换坐标
			converter.coord(llA);
			LatLng desLatLng = converter.convert();

			roadName.setText(Roads[i][2]);
			BitmapDescriptor bd1 = BitmapDescriptorFactory
					.fromBitmap(getBitmapFromView(view));
			MarkerOptions ooA = new MarkerOptions().position(desLatLng)
					.icon(bd1).zIndex(9).draggable(true).title(Roads[i][2]);
			mMarkerA = (Marker) (mBaiduMap.addOverlay(ooA));
		}
	}
	// private Bitmap getBitmapFromLayout(){
	//
	// }
	private Bitmap getBitmapFromView(View view) {
		view.destroyDrawingCache();
		view.measure(View.MeasureSpec.makeMeasureSpec(0,
				View.MeasureSpec.UNSPECIFIED), View.MeasureSpec
				.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
		view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
		view.setDrawingCacheEnabled(true);
		Bitmap bitmap = view.getDrawingCache(true);
		return bitmap;
	}

	private double[][] getCoordinates() {
		double[][] Coordinates = new double[RoadsNum][2];
		for (int i = 0; i < Coordinates.length; i++) {
			Coordinates[i][0] = Double.valueOf(Roads[i][4].toString());
			Coordinates[i][1] = Double.valueOf(Roads[i][5].toString());
		}
		return Coordinates;
	}

	/**
	 * 定位SDK监听函数
	 */
	public class MyLocationListenner implements BDLocationListener {
		@Override
		public void onReceiveLocation(BDLocation location) {
			// map view 销毁后不在处理新接收的位置
			if (location == null || mMapView == null) {
				return;
			}
			MyLocationData locData = new MyLocationData.Builder()
					.accuracy(location.getRadius())
					// 此处设置开发者获取到的方向信息，顺时针0-360
					.direction(100).latitude(location.getLatitude())
					.longitude(location.getLongitude()).build();
			mBaiduMap.setMyLocationData(locData);
			if (isFirstLoc) {
				isFirstLoc = false;
				LatLng ll = new LatLng(location.getLatitude(),
						location.getLongitude());
				MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(ll);
				mBaiduMap.animateMapStatus(u);
			}
		}

		public void onReceivePoi(BDLocation poiLocation) {
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		bdA.recycle();
		// 在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
		mMapView.onDestroy();
	}
	@Override
	protected void onResume() {
		super.onResume();
		// 在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
		mMapView.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
		// 在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
		mMapView.onPause();
	}

}