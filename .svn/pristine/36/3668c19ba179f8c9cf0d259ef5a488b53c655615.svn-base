package com.lexinsmart.util;

import static com.lexinsmart.MyApplication.MyApplication.gDb;
import static com.lexinsmart.util.Constants.DB_COLUMN_LEN;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class DatabaseTools {

	public static void updateTable(Context context, String tableName,
			String[] tableColumns, String[] values, boolean override) {
		// 打开数据库，如果不存在则创建并添加表格DB_TABLE_USER
		gDb = SQLiteDatabase.openDatabase(context.getFilesDir().getParent()
				+ "/mydb.db", null, SQLiteDatabase.OPEN_READWRITE
				| SQLiteDatabase.CREATE_IF_NECESSARY);
		String sql = "CREATE TABLE if not exists " + tableName + "(";
		for (int i = 0; i < tableColumns.length; i++) {
			sql += tableColumns[i] + " CHAR(" + DB_COLUMN_LEN + "), ";
		}
		sql = sql.substring(0, sql.length() - 2) + ")";
		gDb.execSQL(sql);
		// 清除表格DB_TABLE_USER中原有数据
		if (override) {
			gDb.delete(tableName, null, null);
		}

		// 向表格DB_TABLE_USER插入数据
		ContentValues cv = new ContentValues();
		for (int i = 0; i < values.length; i++) {
			cv.put(tableColumns[i], values[i]);
		}
		if (cv.size() > 0) {
			gDb.insert(tableName, null, cv);
		}

		// 关闭数据库
		gDb.close();
	}
	public static ArrayList<String[]> qurryAllRoads(Context context,
			String tableName) {
		ArrayList<String[]> allRoads = new ArrayList<String[]>();

		try {
			gDb = SQLiteDatabase.openDatabase(context.getFilesDir().getParent()
					+ "/mydb.db", null, SQLiteDatabase.OPEN_READWRITE
					| SQLiteDatabase.CREATE_IF_NECESSARY);
			Cursor cursor = gDb.query(tableName, Constants.DB_THING_COLUMNS,
					null, null, null, null, null);
			while (cursor.moveToNext()) {
				String[] oneRow = new String[4];

				for (int i = 0; i < oneRow.length; i++) {
					oneRow[i] = cursor.getString(cursor
							.getColumnIndex(Constants.DB_THING_COLUMNS[i]));
					System.out.println("oneRow:"+oneRow[i]);
				}
				allRoads.add(oneRow);
			}
//  			for (int i = 0; i < allRoads.size(); i++) {
//				System.out.println("allRoads.size():"+allRoads.size());
//
//				for (int j = 0; j < allRoads.get(i).length; j++) {
//					System.out.println("contentList:"+allRoads.get(i)[j]);
//				}
//			}	
			return allRoads;
		} catch (Exception e) {
			
		}	
		gDb.close();
		return allRoads;
	}
	public static void deleteOnwRow(Context context,String tableName,String[] Row){
		try {
			gDb = SQLiteDatabase.openDatabase(context.getFilesDir().getParent()
					+ "/mydb.db", null, SQLiteDatabase.OPEN_READWRITE
					| SQLiteDatabase.CREATE_IF_NECESSARY);
			gDb.delete(tableName, "plantNum=?",Row);
			gDb.close();
		} catch (Exception e) {
		}
	}
}