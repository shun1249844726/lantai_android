package com.lexinsmart.util;

import java.io.FileInputStream;
import static com.lexinsmart.util.Constants.ONEROADDTL;
import java.io.FileOutputStream;
import java.util.List;
import java.util.Map;

import org.apache.http.util.EncodingUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;

public class StringUtil {
	public static byte[] Str2Byte(String inputStr) {
		byte[] result = new byte[inputStr.length() / 2];
		for (int i = 0; i < inputStr.length() / 2; ++i)
			result[i] = (byte) (Integer.parseInt(
					inputStr.substring(i * 2, i * 2 + 2), 16) & 0xff);
		return result;
	}

	public static byte[][] getPhaseInfoArray(String inputStr) {
		byte[][] returned = new byte[inputStr.length() / 12][4];
		String woca = "";
		for (int i = 0; i < returned.length; i++) {
			for (int j = 0; j < returned[0].length; j++) {
				woca = inputStr.substring(j * 3 + 12 * i, 3 * (j + 1) + 12 * i);
				returned[i][j] = (byte) (Integer.parseInt(woca) & 0xff);
			}
		}
		return returned;
	}

	public static void writeFileData(String fileName, String message, Context context) {
		try {
			FileOutputStream fout = context.openFileOutput(fileName,
					context.MODE_PRIVATE);
			byte[] bytes = message.getBytes();
			fout.write(bytes);
			fout.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static String readFileData(String fileName, Context context) {
		String res = "";
		try {
			FileInputStream fin = context.openFileInput(fileName);
			int length = fin.available();
			byte[] buffer = new byte[1024*40];
			fin.read(buffer);
			res = EncodingUtils.getString(buffer, "UTF-8");
			fin.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return res;
	}
	public static String[][] JsonStringToArray(String jsonstring) {
		String[][] returnedArray = null;
		try {
            List<Map<String, Object>> listMap = JSON.parseObject(jsonstring, new TypeReference<List<Map<String,Object>>>(){});
            returnedArray = new String[listMap.size()][9];
			for (int i = 0; i < listMap.size(); i++) {
				for (int j = 0; j < 9; j++) {
					returnedArray[i][j] = (String) listMap.get(i).get(
							ONEROADDTL[j]);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("ÄãÂé±ÔµÄ");

		}
		return returnedArray;
	}
    @SuppressLint("NewApi")
	public static Drawable getDrawable(Context context, int res) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return context.getResources().getDrawable(res, context.getTheme());
        } else {
            return context.getResources().getDrawable(res);
        }
    }
}
