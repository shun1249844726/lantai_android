package com.lexinsmart.planmanager;

/* 
 * 
 * 105 103 101 102		105��5   ��λ���
 103��3   ��λ����
 101��1   �������
 102��2   ��������
 * 108 103 101 111 
 * 104 103 102 101 
 * 107 105 101 111
 * 101 105 102 104
 * 106 107 101 111 
 * 102 107 102 101 
 * 103 107 103 100
 * */
import static com.lexinsmart.util.Constants.FLEETLENGTH;
import static com.lexinsmart.util.Constants.Roads;
import static com.lexinsmart.util.Constants.FLEETSPACING;
import static com.lexinsmart.util.Constants.GETGPS;
import static com.lexinsmart.util.Constants.IP;
import static com.lexinsmart.util.Constants.PORT;
import static com.lexinsmart.util.Constants.SOCKETSTATE;
import static com.lexinsmart.util.Constants.SENDCOMMOND_DISTENCE;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.Polyline;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.CoordinateConverter;
import com.baidu.mapapi.utils.CoordinateConverter.CoordType;
import com.baidu.mapapi.utils.DistanceUtil;
import com.lexinsmart.SocketTools.Client;
import com.lexinsmart.SocketTools.ISocketResponse;
import com.lexinsmart.SocketTools.Packet;
import com.lexinsmart.SocketTools.SendControlResponse;
import com.lexinsmart.util.CRC16;
import com.lexinsmart.util.JsonTools;
import com.lexinsmart.util.StringUtil;
import com.lexinsmart_lantai.R;

public class ExecutePlan extends Activity implements OnClickListener {
	Marker mMarkerA;
	Marker mMarkerB;
	ArrayList<String[]> RoadDetails;
	private ImageView layoutDetailsImg;
	private LinearLayout detailsLayout;
	private Button executeplan_execute, executeplan_stop, executeplan_exit;
	private TextView executeplan_nextcross, executeplan_distance,
			executeplan_orientation, executeplan_totlecross,
			executeplan_leftcross, executeplan_connectstatus;
	private int totleCross;
	Context mContext;
	ArrayList<String[]> roadDetails_list;
	String[][] RoadDetails_array;
	MapView mMapView = null;
	Polyline mPolyline;
	Polyline mPolyline_1;
	TextView roadName;
	BaiduMap mBaiduMap;
	private Client user = null;
	private int DELYED = 1000;
	private int i = 0;
	Packet packet;
	private int startCross, fleet_len, fleet_style;

	LatLng lastLocation = new LatLng(31, 120); // ��һ���Ķ�λ��Ϣ
	private LatLng myLocation;
//	double longtitude	= 121.08092777777777;
//	double latitude = 32.050325;
	double longtitude	= 121.0806;
	double latitude = 32.072027777778;
	int nowCrossNum = 0; // Ҫ������·�ڵ����ڵ�������±�
	LatLng nowCrossLL; // Ҫ������·�ڵľ�γ��
	LatLng lastCrossLL;
	private static int comeInRoadFlag = 0; // ����·�ڱ�־
	int fleetLength = 0;
	LatLng nextCross;
	String[][] SignallerIdADPhaseInfo;
	int[] roadDistance; // ����·��֮��ľ��룬���ȵ���·������-1
	int sendCancleNum = 0;

	int overFlag = 0;
	int startFlag = 0;
	MapStatusUpdate mMapStatusUpdate;
	MapStatus mMapStatus;
	int gameover = 0;// ��������
	int sendedRoadNum = 0; // �Ѿ����͹���·��

	int sendonetimecontrolflag = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// ��ʹ��SDK�����֮ǰ��ʼ��context��Ϣ������ApplicationContext
		// ע��÷���Ҫ��setContentView����֮ǰʵ��
		SDKInitializer.initialize(getApplicationContext());
		
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON); 
		setContentView(R.layout.activity_executeplan);
		mContext = this;
		// ��ȡ��ͼ�ؼ�����
		Intent intent = getIntent();
		String roadDetalis = intent.getStringExtra("roadDetails");

		startCross = intent.getIntExtra("startCross", -1);
		
		nowCrossNum = startCross;
		sendCancleNum = startCross;
		sendedRoadNum = startCross;
		fleet_len = intent.getIntExtra("fleet_len", -1);
		fleet_style = intent.getIntExtra("fleet_style", -1);
		System.out.println(roadDetalis + ", startCross" + startCross + ","
				+ fleet_len + "," + fleet_style);
		roadDetails_list = JsonTools.changeJsonToArray(roadDetalis);
		RoadDetails_array = (String[][]) roadDetails_list
				.toArray(new String[roadDetails_list.size()][]);

		for (int i = 0; i < RoadDetails_array.length; i++) {
			for (int j = 0; j < RoadDetails_array[0].length; j++) {
				System.out.println("RoadDetails_array"
						+ RoadDetails_array[i][j]);
			}
		}
		totleCross = roadDetails_list.size();
		/*
		 * �õ�·��֮��ľ���
		 */
		roadDistance = new int[totleCross - 1];
		double oneLongtitude = 0, oneLatitude = 0, nextLongtitude = 0, nextLatitude = 0;
		LatLng oneCross, nextCross;
		for (int i = 0; i < (roadDistance.length); i++) {
			oneLongtitude = Double.parseDouble(RoadDetails_array[i][2]);
			oneLatitude = Double.parseDouble(RoadDetails_array[i][3]);
			nextLongtitude = Double.parseDouble(RoadDetails_array[i + 1][2]);
			nextLatitude = Double.parseDouble(RoadDetails_array[i + 1][3]);

			oneCross = new LatLng(oneLatitude, oneLongtitude);
			nextCross = new LatLng(nextLatitude, nextLongtitude);
			roadDistance[i] = (int) DistanceUtil.getDistance(oneCross,
					nextCross);
			System.out.println("roadDistance-->" + roadDistance[i]);
		}
		findViews();
		LatLng cenpt = new LatLng(Double.valueOf(RoadDetails_array[i][3]
				.toString()),
				Double.valueOf(RoadDetails_array[i][2].toString()));
		// �����ͼ״̬
		mMapStatus = new MapStatus.Builder().target(cenpt).zoom(14).build();
		// ����MapStatusUpdate�����Ա�������ͼ״̬��Ҫ�����ı仯
		mMapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mMapStatus);
		// �ı��ͼ״̬
		mBaiduMap.setMapStatus(mMapStatusUpdate);

		addMarkers();
		addCustomElementsDemo();
		addLocation();
		fleetLength = (FLEETLENGTH + FLEETSPACING) * (fleet_len * 5 - 2) + 50;

		SignallerIdADPhaseInfo = new String[RoadDetails_array.length][2];
		SignallerIdADPhaseInfo = getSignallerIdADPhaseInfo(RoadDetails_array);

		for (int i = 0; i < Roads[0].length; i++) {
			System.out.println("Roads[0]" + Roads[0][i]);
		}

		for (int i = 0; i < SignallerIdADPhaseInfo.length; i++) {
			System.out.println("SignallerIdADPhaseInfo-->"
					+ SignallerIdADPhaseInfo[i][0] + ";"
					+ SignallerIdADPhaseInfo[i][1]);
		}
	}

	/*
	 * ��ȡ�źŻ�Id����λ����
	 */

	private String[][] getSignallerIdADPhaseInfo(String[][] roaddetailsarray) {

		String[][] returned = new String[roaddetailsarray.length][2];

		for (int i = 0; i < roaddetailsarray.length; i++) {

			for (int j = 0; j < Roads.length; j++) {
				if (roaddetailsarray[i][0].equals(Roads[j][2])) {
					returned[i][0] = Roads[j][3];
					returned[i][1] = Roads[j][8];
				}
			}

		}
		return returned;
	}

	private void addLocation() {// ��λ������ TODO

		LatLng point = new LatLng(0, 0);

		// // ��GPS�豸�ɼ���ԭʼGPS����ת���ɰٶ�����
		// CoordinateConverter converter = new CoordinateConverter();
		// converter.from(CoordType.GPS);
		// // sourceLatLng��ת������
		// converter.coord(point);
		// LatLng LL = converter.convert();

		// ����Markerͼ��
		BitmapDescriptor bitmap = BitmapDescriptorFactory
				.fromResource(R.drawable.roadflag);
		// ����MarkerOption�������ڵ�ͼ������Marker
		OverlayOptions option = new MarkerOptions().icon(bitmap)
				.position(point);
		// �ڵ�ͼ������Marker������ʾ
		mMarkerB = (Marker) mBaiduMap.addOverlay(option);
	}

	/**
	 * ����
	 * 
	 */
	private void addCustomElementsDemo() {
		List<LatLng> points = new ArrayList<LatLng>();
		for (int i = startCross; i < RoadDetails_array.length; i++) {
			LatLng p = new LatLng(Double.valueOf(RoadDetails_array[i][3]
					.toString()), Double.valueOf(RoadDetails_array[i][2]
					.toString()));
			points.add(p);
		}
		OverlayOptions ooPolyline = new PolylineOptions().width(10)
				.color(0xAAFF0000).points(points);
		mPolyline_1 = (Polyline) mBaiduMap.addOverlay(ooPolyline);
	}
	private void drawLine(LatLng location, LatLng nextCross, int style) {
		List<LatLng> points = new ArrayList<LatLng>();
		points.add(location);
		points.add(nextCross);
		OverlayOptions ooPolyline = null;
		switch (style) {
		case 1:
			ooPolyline = new PolylineOptions().width(10).color(0xAAFF0000)
					.points(points);
			mPolyline = (Polyline) mBaiduMap.addOverlay(ooPolyline);

			break;
		case 2:
			ooPolyline = new PolylineOptions().width(20).color(0x3300ffff)
					.points(points);
			mPolyline_1 = (Polyline) mBaiduMap.addOverlay(ooPolyline);
			break;
		case 3:
			ooPolyline = new PolylineOptions().width(10).color(0x00000000)
					.points(points);
			mPolyline.remove();
			break;
		default:
			break;
		}

	}
	/**
	 * ��ȡ��·�ھ�γ��
	 * 
	 */
	private double[][] getCoordinates() {
		double[][] Coordinates = new double[RoadDetails_array.length][2];
		for (int i = 0; i < RoadDetails_array.length; i++) {
			Coordinates[i][0] = Double.valueOf(RoadDetails_array[i][2]
					.toString());
			Coordinates[i][1] = Double.valueOf(RoadDetails_array[i][3]
					.toString());
		}
		return Coordinates;
	}
	private void addMarkers() {
		double[][] coordinates = getCoordinates();
		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.marker, null);
		roadName = (TextView) view.findViewById(R.id.roadnameId);
		for (int i = startCross; i < coordinates.length; i++) {
			System.out.println("coordinates[i][0]:" + coordinates[i][0]
					+ ";coordinates[i][1]:" + coordinates[i][1]);
			LatLng llA = new LatLng(coordinates[i][1], coordinates[i][0]);
			roadName.setText(RoadDetails_array[i][0]);
			BitmapDescriptor bd1 = BitmapDescriptorFactory
					.fromBitmap(getBitmapFromView(view));
			MarkerOptions ooA = new MarkerOptions().position(llA).icon(bd1)
					.zIndex(9).draggable(true).title(RoadDetails_array[i][0]);
			mMarkerA = (Marker) (mBaiduMap.addOverlay(ooA));
		}
	}
	private void findViews() {
		mMapView = (MapView) findViewById(R.id.executeplan_mapId);
		mBaiduMap = mMapView.getMap();
		layoutDetailsImg = (ImageView) findViewById(R.id.executeplan_layoutmoreId);
		layoutDetailsImg.setBackgroundResource(R.drawable.layoutshow);
		detailsLayout = (LinearLayout) findViewById(R.id.executeplan_layoutdetailsId);
		layoutDetailsImg.setOnClickListener(this);
		executeplan_nextcross = (TextView) findViewById(R.id.executeplan_nextcrossId);// �¸�·��
		executeplan_distance = (TextView) findViewById(R.id.executeplan_distanceId);// ����
		executeplan_orientation = (TextView) findViewById(R.id.executeplan_orientationId);// ����
		executeplan_totlecross = (TextView) findViewById(R.id.executeplan_totlecrossId);// ����·��
		executeplan_totlecross.setText("" + (totleCross-startCross));
		executeplan_leftcross = (TextView) findViewById(R.id.executeplan_leftcrossId);// ʣ��·��
		executeplan_connectstatus = (TextView) findViewById(R.id.executeplan_connectstatusId);// ����״̬

		executeplan_execute = (Button) findViewById(R.id.executeplan_executeId);
		executeplan_execute.setOnClickListener(this);
		executeplan_stop = (Button) findViewById(R.id.executeplan_stopId);
		executeplan_stop.setOnClickListener(this);
		executeplan_exit = (Button) findViewById(R.id.executeplan_exitId);
		executeplan_exit.setOnClickListener(this);
		layoutDetailsImg.setBackgroundResource(R.drawable.layouthide);
		user = new Client(this.getApplicationContext(), socketListener,
				sendControlResponse);
		packet = new Packet();
		executeplan_nextcross.setText("" + RoadDetails_array[nowCrossNum][0]);
		executeplan_orientation.setText("" + RoadDetails_array[nowCrossNum][1]);
		executeplan_leftcross.setText(""
				+ (RoadDetails_array.length - nowCrossNum));
	}

	/**
	 * ��ʱ������
	 * 
	 * @author xu
	 * 
	 */
	Handler handler = new Handler();
	Runnable runnable = new Runnable() {
		@Override
		public void run() {
			// handler�Դ�����ʵ�ֶ�ʱ��
			try {
				handler.postDelayed(this, DELYED);
				executeplan_connectstatus.setText(SOCKETSTATE);
				packet.pack_byte(GETGPS);
				user.send(packet);
				// LatLng ll = mMarkerB.getPosition();
				//
				//
				// LatLng llNew = new LatLng(ll.latitude + 0.00005,
				// ll.longitude + 0.00005);
				System.out.println("do..." + i++);
				double nowLongititude = Double
						.parseDouble(RoadDetails_array[nowCrossNum][2]);
				double nowLatitude = Double
						.parseDouble(RoadDetails_array[nowCrossNum][3]);
				double lastLongititude;
				double lastLatitude;
				if (nowCrossNum == 0) {
					lastLongititude = Double
							.parseDouble(RoadDetails_array[0][2]);
					lastLatitude = Double.parseDouble(RoadDetails_array[0][3]);
				} else {
					lastLongititude = Double
							.parseDouble(RoadDetails_array[nowCrossNum - 1][2]);
					lastLatitude = Double
							.parseDouble(RoadDetails_array[nowCrossNum - 1][3]);
				}
				nowCrossLL = new LatLng(nowLatitude, nowLongititude);
				lastCrossLL = new LatLng(lastLatitude, lastLongititude);
				int Distance = (int) DistanceUtil.getDistance(myLocation,
						nowCrossLL);
				int lastDistance = (int) DistanceUtil.getDistance(myLocation,
						lastCrossLL);
				executeplan_distance.setText("" + Distance);
				System.out.println("Distance:" + Distance);
				if (Distance < SENDCOMMOND_DISTENCE && Distance != -1) {
					startFlag++;
					System.out.println("sendedRoadNum--->" + sendedRoadNum);
					if (startFlag == 1) {
						if (sendedRoadNum <= nowCrossNum) {
							packet.pack_byte(sendLightControl(nowCrossNum));
							for (int i = 0;i<4;i++)
							{
								user.send(packet);
							}
							executeplan_distance.setTextColor(Color.GREEN);
							sendedRoadNum++;
							System.out.println("���Ϳ��ƺ��̵�  "
									+ RoadDetails_array[nowCrossNum][0]);
						}
					}
					int[] Distance_two = new int[2];
					/*
					 * ���ж�������·�ڵľ���
					 */
					if (nowCrossNum < RoadDetails_array.length - 2) {

						Distance_two[0] = Distance + roadDistance[nowCrossNum];
						Distance_two[1] = Distance + roadDistance[nowCrossNum]
								+ roadDistance[nowCrossNum + 1];
						System.out.println("Distance_two--->" + Distance_two[0]
								+ ";" + Distance_two[1]);
						if (Distance_two[0] < SENDCOMMOND_DISTENCE) {
							sendonetimecontrolflag++;
							if (sendonetimecontrolflag == 1) {
								packet.pack_byte(sendLightControl(nowCrossNum + 1));
								for (int i = 0;i<4;i++)
								{
									user.send(packet);
								}
								executeplan_distance.setTextColor(Color.GREEN);
								System.out
										.println("���Ϳ��ƺ��̵�  "
												+ RoadDetails_array[nowCrossNum + 1][0]);
								sendedRoadNum++;
							}
						}
						if (Distance_two[1] < SENDCOMMOND_DISTENCE) {
							sendonetimecontrolflag++;
							if (sendonetimecontrolflag == 1) {
								packet.pack_byte(sendLightControl(nowCrossNum + 2));
								for (int i = 0;i<4;i++)
								{
									user.send(packet);
								}
								executeplan_distance.setTextColor(Color.GREEN);
								System.out
										.println("���Ϳ��ƺ��̵�  "
												+ RoadDetails_array[(nowCrossNum + 2)][0]);
								sendedRoadNum++;
							}
						}
					} else if (nowCrossNum == RoadDetails_array.length - 2) {
						Distance_two[0] = Distance + roadDistance[nowCrossNum];
						System.out
								.println("Distance_two--->" + Distance_two[0]);
						if (Distance_two[0] < SENDCOMMOND_DISTENCE) {
							sendonetimecontrolflag++;
							if (sendonetimecontrolflag == 1) {
								packet.pack_byte(sendLightControl(nowCrossNum + 1));
								for (int i = 0;i<4;i++)
								{
									user.send(packet);
								}
								executeplan_distance.setTextColor(Color.GREEN);
								System.out
										.println("���Ϳ��ƺ��̵�  "
												+ RoadDetails_array[(nowCrossNum + 1)][0]);
								sendedRoadNum++;
							}
						}
					}
					// if (startFlag == nowCrossNum) {
					// // TODO ��ѯsignallerId,���ݷ���� phaseInfo
					// // ������������������
					// // TODO ���Ϳ��ƺ��̵�
					// packet.pack_byte(sendLightControl(nowCrossNum));
					// user.send(packet);
					// executeplan_distance.setTextColor(Color.GREEN);
					//
					// startFlag++;
					// System.out.println("���Ϳ��ƺ��̵�  " +
					// RoadDetails_array[nowCrossNum][0]);
					// // System.out.println("Id_byte-->"
					// // + SignallerIdADPhaseInfo[nowCrossNum][0]);
					// // String id = SignallerIdADPhaseInfo[nowCrossNum][0];
					// // String PhaseInfo =
					// // SignallerIdADPhaseInfo[nowCrossNum][1];
					// // byte[] Id_byte = new byte[8]; // �õ�Id�Ķ�Ӧ��byte������
					// // byte[][] PhaseInfoArray = StringUtil
					// // .getPhaseInfoArray(PhaseInfo);
					// // Id_byte = StringUtil.Str2Byte(id);
					//
					// // for (int i = 0; i < PhaseInfoArray.length; i++) {
					// // for (int j = 0; j < PhaseInfoArray[0].length; j++) {
					// // System.out.println("phasearray:"
					// // + (PhaseInfoArray[i][j] & 0xff));
					// // }
					// //
					// // }
					// // for (int i = 0; i < Id_byte.length; i++) {
					// // System.out.println(Id_byte[i]&0xff);
					// // }
					// }
					System.out.println("comeInRoadFlag-->" + comeInRoadFlag
							+ ";nowCrossNum-->" + nowCrossNum
							+ ";fleetLength-->" + fleetLength
							+ " ; startFlag -->" + startFlag
							+ " ; overflag --> " + overFlag);
					if (Distance <= 50) {
						comeInRoadFlag = 1;
					}
					if (Distance > fleetLength && gameover == 1) // ��������
					{
						executeplan_nextcross.setText("");
						executeplan_orientation.setText("");
						executeplan_leftcross.setText("ִ�н���");
						executeplan_leftcross.setTextColor(Color.RED);
						executeplan_leftcross.setTextSize(15);
						handler.removeCallbacks(runnable);
						user.close();
					}
					if (Distance > 50 && comeInRoadFlag == 1) {
						sendonetimecontrolflag = 0;// //////////////
						nowCrossNum++;
						if (nowCrossNum == RoadDetails_array.length) // ��������
						{
							nowCrossNum = RoadDetails_array.length - 1;
							gameover = 1;
						}
						if (nowCrossNum < RoadDetails_array.length) { // �����������±��һ��ע���±�Խ��
							executeplan_nextcross.setText(""
									+ RoadDetails_array[nowCrossNum][0]);
							executeplan_orientation.setText(""
									+ RoadDetails_array[nowCrossNum][1]);
							executeplan_leftcross.setText(""
									+ (RoadDetails_array.length - nowCrossNum));
							LatLng lastCrossLL = new LatLng(
									Double.parseDouble(RoadDetails_array[nowCrossNum - 1][3]),
									Double.parseDouble(RoadDetails_array[nowCrossNum - 1][2]));
							LatLng onCrossLL = new LatLng(
									Double.parseDouble(RoadDetails_array[nowCrossNum][3]),
									Double.parseDouble(RoadDetails_array[nowCrossNum][2]));
							drawLine(lastLocation, nextCross, 3);
							drawLine(onCrossLL, lastCrossLL, 2);
							comeInRoadFlag = 0;
							overFlag = 1;
						}
					}
					if (lastDistance > fleetLength && overFlag == 1) {
						// �����źţ�ֹͣ���ƺ��̵ƣ�
						// TODO
						packet.pack_byte(sendCancle(sendCancleNum));
						for (int i = 0;i<4;i++)
						{
							user.send(packet);
						}
						System.out.println("�����źţ�ֹͣ���ƺ��̵�  "
								+ RoadDetails_array[sendCancleNum][0]);
						sendCancleNum++; // �����ټ�
						executeplan_distance.setTextColor(Color.RED);
						overFlag = 0;
						startFlag = 0;
					}
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				System.out.println("exception...");
			}
		}

		public byte[] sendLightControl(int nowcrossnum) {
			int orientation = 0;
			ArrayList<Byte> messagePackage = new ArrayList<Byte>();
			byte[] PackageHeader = { 0x7E, 0x40, 0x00, 0x00, 0x00, 0x00, 0x00,
					0x06 }; // 1.��ͷ+������
			for (int i = 0; i < PackageHeader.length; i++) {
				messagePackage.add(PackageHeader[i]);
			}
			messagePackage.add((byte) 0x09); // 2���ظ�����
			byte[] signallerId = StringUtil
					.Str2Byte(SignallerIdADPhaseInfo[nowcrossnum][0]); // ����������
			for (int i = 0; i < signallerId.length; i++) {
				messagePackage.add(signallerId[i]);
			} // 3.�źŻ����
			messagePackage.add((byte) 0x6C); // 4����������
			messagePackage.add((byte) 0xFF); // 5.����ʱ��
			switch (RoadDetails_array[nowcrossnum][1]) {
			case "��":
				orientation = 101;
				break;
			case "����":
				orientation = 102;
				break;
			case "��":
				orientation = 103;
				break;
			case "����":
				orientation = 104;
				break;
			case "��":
				orientation = 105;
				break;
			case "����":
				orientation = 106;
				break;
			case "��":
				orientation = 107;
				break;
			case "����":
				orientation = 108;
				break;
			default:
				break;
			}
			ArrayList<Byte> PhaseArray = getPhaseArray(orientation, nowcrossnum);
			messagePackage.add((byte) PhaseArray.size()); // 6.��λ����
			for (int j = 0; j < PhaseArray.size(); j++) {
					messagePackage.add(PhaseArray.get(j)); // 7s����λ����
			}
			messagePackage.add(CRC16.getCRC16(messagePackage, 1,
					messagePackage.size() - 1));
			messagePackage.add((byte) 0x7E);
			byte[] returned = new byte[messagePackage.size()];
			String sendPackageContent = "";
			for (int j2 = 0; j2 < messagePackage.size(); j2++) {
				returned[j2] = messagePackage.get(j2);
				sendPackageContent += (Integer.toHexString(messagePackage
						.get(j2) & 0xff)) + " ";
			}
			System.out.println("sendPackageContent--->" + sendPackageContent);
			return returned;
		}

		private ArrayList<Byte> getPhaseArray(int orientation, int nowcrossnum) {
			ArrayList<Byte> okPhaseArray = new ArrayList<Byte>();
			byte[][] PhaseArray = StringUtil
					.getPhaseInfoArray(SignallerIdADPhaseInfo[nowcrossnum][1]);
			for (int i = 0; i < PhaseArray.length; i++) {
				if (PhaseArray[i][1] == orientation) {
					okPhaseArray.add((byte) (PhaseArray[i][0]-100));
				}
			}
			return okPhaseArray;
		}
	};
	/**
	 * �������ݻص�
	 * 
	 */
	public SendControlResponse sendControlResponse = new SendControlResponse() {

		@Override
		public void onSocketResponse(final String txt) {
			runOnUiThread(new Runnable() {
				public void run() {
					String temp = txt.trim().toString();
					System.out.println("SendControl.trim():" + temp);
				}
			});
		}
	};
	/**
	 * ���� GPS ���ݻص�
	 * 
	 */
	public ISocketResponse socketListener = new ISocketResponse() {

		@Override
		public void onSocketResponse(final String txt) {
			runOnUiThread(new Runnable() {
				public void run() {
					String temp = txt.trim().toString();
					System.out.println("ISocket.trim():" + temp);

					if (txt.startsWith("7E")) {
						String[] array_temp = temp.split(" ");
						int[] array_temp_byte = new int[array_temp.length];

						for (int i = 0; i < array_temp_byte.length; i++) {
							array_temp_byte[i] = Integer.valueOf(array_temp[i],
									16);
						}
						if (array_temp_byte[17] == 0x7E) {
							// executeplan_connectstatus.setText(""
							// + array_temp_byte[0] + ","
							// + array_temp_byte[1] + ","
							// + array_temp_byte[2] + ","
							// + array_temp_byte[3] + ","
							// + array_temp_byte[4] + ","
							// + array_temp_byte[5] + ","
							// + array_temp_byte[6] + ","
							// + array_temp_byte[7] + ","
							// + array_temp_byte[8] + ","
							// + array_temp_byte[9] + ","
							// + array_temp_byte[10] + ","
							// + array_temp_byte[11] + ","
							// + array_temp_byte[12] + ","
							// + array_temp_byte[13] + ","
							// + array_temp_byte[14] + ","
							// + array_temp_byte[15] + ","
							// + array_temp_byte[16] + ","
							// + array_temp_byte[17]);
							
							
							 longtitude = (double) ((array_temp_byte[8] * 256
							 * 256 * 256 + array_temp_byte[9] * 256
							 * 256 + array_temp_byte[10] * 256 +
							 array_temp_byte[11])) / 1000000;
							 latitude = (double) ((array_temp_byte[12] * 256
							 * 256 * 256 + array_temp_byte[13] * 256
							 * 256 + array_temp_byte[14] * 256 +
						 	 array_temp_byte[15])) / 1000000;
							 int longtitudeZH = (int) longtitude;
							 double longtitudeXI = (longtitude - longtitudeZH)
							 / 0.6d;
							 longtitude = longtitudeZH + longtitudeXI;
							
							 int latitudeZH = (int) latitude;
							 double latitudeXI = (latitude - latitudeZH) /
							 0.6d;
							 latitude = latitudeZH + latitudeXI;
							 LatLng llA = new LatLng(latitude, longtitude);

							/*---------------------------*/


//							executeplan_connectstatus.setText("" + latitude
//									+ '\n' + longtitude);
//
//							LatLng llA = new LatLng(latitude - 0.0003 * i,
//									longtitude);
//
//							// longtitude = 121.08063888888888;
//							// latitude = 32.074669444444446;
//
//							executeplan_connectstatus.setText("" + latitude
//									+ '\n' + longtitude);
							/*---------------------------*/
							 
							 
							 
							// ��GPS�豸�ɼ���ԭʼGPS����ת���ɰٶ�����
							CoordinateConverter converter = new CoordinateConverter();
							converter.from(CoordType.GPS);
							// sourceLatLng��ת������
							converter.coord(llA);
							myLocation = converter.convert();
							double nowLongititude = 0, nowLatitude = 0;
							if (nowCrossNum < RoadDetails_array.length) {
								nowLongititude = Double
										.parseDouble(RoadDetails_array[nowCrossNum][2]);
								nowLatitude = Double
										.parseDouble(RoadDetails_array[nowCrossNum][3]);
							} else {
								nowLongititude = Double
										.parseDouble(RoadDetails_array[nowCrossNum - 1][2]);
								nowLatitude = Double
										.parseDouble(RoadDetails_array[nowCrossNum - 1][3]);
							}
							nextCross = new LatLng(nowLatitude, nowLongititude);
							if (mPolyline != null) {
								drawLine(lastLocation, nextCross, 3);
							}
							mMarkerB.setPosition(myLocation);
							/*
							 * �ı��ͼ����λ��
							 */
//							int zoomStatus = (int) mBaiduMap.getMapStatus().zoom;
//							mMapStatus = new MapStatus.Builder()
//									.target(myLocation).zoom(zoomStatus)
//									.build();
//							// ����MapStatusUpdate�����Ա�������ͼ״̬��Ҫ�����ı仯
//							mMapStatusUpdate = MapStatusUpdateFactory
//									.newMapStatus(mMapStatus);
//							mBaiduMap.setMapStatus(mMapStatusUpdate);

							drawLine(myLocation, nextCross, 1);
							lastLocation = myLocation;
							// myLocation = new LatLng(latitude, longtitude);
						}
						if (array_temp_byte[9] == 0x7E) {
						}
					}
					// recContent.getText().append(txt).append("\r\n");
				}
			});
		}
	};

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.executeplan_layoutmoreId: // ����ͼƬ
			if (detailsLayout.getVisibility() == View.GONE) {
				detailsLayout.setVisibility(View.VISIBLE);
				layoutDetailsImg.setBackgroundResource(R.drawable.layouthide);
			} else if (detailsLayout.getVisibility() == View.VISIBLE) {
				detailsLayout.setVisibility(View.GONE);
				layoutDetailsImg.setBackgroundResource(R.drawable.layoutshow);
			}
			break;
		case R.id.executeplan_executeId: // ��ʼ
			// ToastUtil.showShort(mContext, "IP:" + IP + ":" + PORT);
			user.open(IP, PORT);
			handler.postDelayed(runnable, DELYED); // ÿ��1sִ��
			break;
		case R.id.executeplan_stopId: // ��ͣ
			handler.removeCallbacks(runnable);
			break;
		case R.id.executeplan_exitId: // ����
			handler.removeCallbacks(runnable);
			user.close();
			break;
		case R.id.executeplan_nextcrossId: // �¸�·��
			break;
		case R.id.executeplan_distanceId: // ����
			break;
		case R.id.executeplan_orientationId:// ����
			break;
		case R.id.executeplan_totlecrossId:// ����·��
			break;
		case R.id.executeplan_leftcrossId:// ʣ��·��
			break;
		case R.id.executeplan_connectstatusId:// ����״̬
			break;
		default:
			break;
		}
	}

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

	@Override
	protected void onDestroy() {
		super.onDestroy();

		handler.removeCallbacks(runnable);
		user.close();
		// ��activityִ��onDestroyʱִ��mMapView.onDestroy()��ʵ�ֵ�ͼ�������ڹ���
		mMapView.onDestroy();
		
	}

	@Override
	protected void onResume() {
		super.onResume();
		// ��activityִ��onResumeʱִ��mMapView. onResume ()��ʵ�ֵ�ͼ�������ڹ���
		mMapView.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
		// ��activityִ��onPauseʱִ��mMapView. onPause ()��ʵ�ֵ�ͼ�������ڹ���
		mMapView.onPause();
		for (int i = 0; i < RoadDetails_array.length-startCross; i++) {
//			sendCancle(i);
			packet.pack_byte(sendCancle(i));
			for (int j = 0;i<4;i++)
			{
				user.send(packet);
			}
		}
		
	}
	public byte[] sendCancle(int sendcancleflag) {
		ArrayList<Byte> messagePackage = new ArrayList<Byte>();
		byte[] PackageHeader = { 0x7E, 0x40, 0x00, 0x00, 0x00, 0x00, 0x00,
				0x06 }; // 1.��ͷ+������
		for (int i = 0; i < PackageHeader.length; i++) {
			messagePackage.add(PackageHeader[i]);
		}
		messagePackage.add((byte) 0x09); // 2���ظ�����
		byte[] signallerId = StringUtil
				.Str2Byte(SignallerIdADPhaseInfo[sendcancleflag][0]); // ����������
		for (int i = 0; i < signallerId.length; i++) {
			messagePackage.add(signallerId[i]);
		} // 3.�źŻ����
		messagePackage.add((byte) 0x00); // 3����������
		messagePackage.add((byte) 0x00); // 4.����ʱ��
		messagePackage.add((byte) 0x00); // 5.��λ����  
		messagePackage.add(CRC16.getCRC16(messagePackage, 1,
				messagePackage.size() - 1));
		messagePackage.add((byte) 0x7E);
		byte[] returned = new byte[messagePackage.size()];
		String sendCancleContent = "";
		for (int j2 = 0; j2 < messagePackage.size(); j2++) {
			returned[j2] = messagePackage.get(j2);
			sendCancleContent += (Integer.toHexString(messagePackage
					.get(j2) & 0xff)) + " ";
		}
		System.out.println("sendPackageCancleContent-->" + sendCancleContent);
		return returned;
	}


}