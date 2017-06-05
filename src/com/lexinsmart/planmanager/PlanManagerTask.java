package com.lexinsmart.planmanager;

import static com.lexinsmart.util.Constants.Roads;
import static com.lexinsmart.util.Constants.ONEROADDTL;

import java.io.File;
import java.io.FileInputStream;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.lexinsmart.util.Constants;
import com.lexinsmart.util.DatabaseTools;
import com.lexinsmart.util.StringUtil;
public class PlanManagerTask {
	static Context mContext;
	public static int RoadsNum = 0;
	public static String[][] Roads;

	public static void setActivity(Context context) {
		mContext = context;
	}

	static class SaveRodeToDb extends AsyncTask<String, Void, String> {
		@Override
		protected String doInBackground(String... params) {
			DatabaseTools.updateTable(mContext, Constants.DB_THING,
					Constants.DB_THING_COLUMNS, params, false);
			return null;
		}
		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
		}
	}
	public static class ReadRoads extends AsyncTask<String, Void, String> {
		private static  String FILENAME = "temp_file_1.txt";
		@Override
		public String doInBackground(String... params) {
			File file = new File(Environment.getExternalStorageDirectory(),
					FILENAME);
			if (Environment.getExternalStorageState().equals(
					Environment.MEDIA_MOUNTED)) {
				try {
					FileInputStream inputStream = new FileInputStream(file);
					byte[] b = new byte[inputStream.available()];
					inputStream.read(b);
					Constants.Roads = StringUtil.JsonStringToArray(new String(b));
					if (Roads != null) {
						return "读取文件成功";
					} else {
						return "数据内容错误";
					}
					// Toast.makeText(MainActivity.this, "读取文件成功",
					// Toast.LENGTH_LONG).show();
				} catch (Exception e) {
					return "读取失败";
					// Toast.makeText(MainActivity.this, "读取失败",
					// Toast.LENGTH_SHORT).show();
				}
			} else {
				// 此时SDcard不存在或者不能进行读写操作的
				return "此时SDcard不存在或者不能进行读写操作";
				// Toast.makeText(MainActivity.this,
				// "此时SDcard不存在或者不能进行读写操作", Toast.LENGTH_SHORT).show();
			}
		}
		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			System.out.println("result"+result);
//			Toast.makeText(mContext, result, Toast.LENGTH_SHORT).show();

		}
		// @Override
		// public String[][] doInBackground(String... params) {
		// try {
		// // new IniReader("RoadInfo.ini", mContext);
		// String roadnum = new IniReader("RoadInfo.ini", mContext).getValue(
		// "Set", "RoadNum");
		// System.out.println("roadnum:"+roadnum);
		// RoadsNum = Integer.valueOf(roadnum).intValue();
		// // System.out.println("RoadsNum:" + RoadsNum);
		// } catch (IOException e1) {
		// e1.printStackTrace();
		// }
		// try {
		// // new IniReader("RoadInfo.ini", mContext);
		// Roads = new IniReader("RoadInfo.ini", mContext)
		// .getAllValue(RoadsNum);
		// // for (int i = 0; i < Roads.length; i++) {
		// // for (int j = 0; j < Roads[0].length; j++) {
		// // System.out.print("allRoads[i][j]:" + Roads[i][j] + '\n');
		// // }
		// // }
		// } catch (IOException e1) {
		// e1.printStackTrace();
		// }
		// return Roads;
		// }
		// @Override
		// protected void onPostExecute(String[][] result) {
		// super.onPostExecute(result);
		// Constants.Roads = result;
		// }
	}
}
