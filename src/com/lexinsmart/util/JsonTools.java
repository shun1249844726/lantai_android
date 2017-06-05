package com.lexinsmart.util;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class JsonTools {
	/**
	 * 将数组转换为JSON格式的数据。
	 * 
	 * @param stoneList
	 *            数据源
	 * @return JSON格式的数据
	 */
	public static String changeArrayDateToJson(
			ArrayList<String[]> newPlanDetailsList) {
		try {
			JSONArray array = new JSONArray();
			JSONObject object = new JSONObject();
			int length = newPlanDetailsList.size();
			for (int i = 0; i < length; i++) {
				String[] onecross = newPlanDetailsList.get(i);
				String name = onecross[0];
				String orientation = onecross[1];
				String longititude = onecross[2];
				String latitude = onecross[3];
				// System.out.println("name:"+name+orientation+longititude+latitude);
				JSONObject stoneObject = new JSONObject();
				stoneObject.put("crossname", name);
				stoneObject.put("orientation", orientation);
				stoneObject.put("longitude", longititude);
				stoneObject.put("latitude", latitude);

				array.put(stoneObject);
			}
			object.put("CrossRoad", array);
			return object.toString();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 将JSON转化为数组并返回。
	 * 
	 * @param Json
	 * @return ArrayList<Stone>
	 */
	public static ArrayList<String[]> changeJsonToArray(String Json) {
		ArrayList<String[]> CrossList = new ArrayList<String[]>();
		try {
			JSONObject jsonObject = new JSONObject(Json);
			if (!jsonObject.isNull("CrossRoad")) {
				String aString = jsonObject.getString("CrossRoad");
				JSONArray aJsonArray = new JSONArray(aString);
				int length = aJsonArray.length();
				for (int i = 0; i < length; i++) {
					JSONObject stoneJson = aJsonArray.getJSONObject(i);
					String name = stoneJson.getString("crossname");
					String orientation = stoneJson.getString("orientation");
					String longititude = stoneJson.getString("longitude");
					String latitude = stoneJson.getString("latitude");
					String[] oneCross = new String[4];
					oneCross[0] = name;
					oneCross[1] = orientation;
					oneCross[2] = longititude;
					oneCross[3] = latitude;
	//				System.out.println("name:" + name + orientation
	//						+ longititude + latitude);
					CrossList.add(oneCross);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return CrossList;
	}

}