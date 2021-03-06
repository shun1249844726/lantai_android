package com.lexinsmart.planmanager;

import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.lexinsmart_lantai.R;
public class MyAdapter extends BaseAdapter implements OnClickListener {

	private static final String TAG = "ContentAdapter";
	private List<String[]> mContentList;
	private LayoutInflater mInflater;
	private Callback mCallback;
	private int selectedIndex = -1;

	public interface Callback {
		public void click(View v);
	}

	public MyAdapter(Context context, List<String[]> contentList,
			Callback callback) {
		mContentList = contentList;
		mInflater = LayoutInflater.from(context);
		mCallback = callback;
	}
	public void setSelectedIndex(int index){
	    selectedIndex = index;
	}
	@Override
	public int getCount() {
//		Log.i(TAG, "getCount");
		return mContentList.size();
	}

	@Override
	public Object getItem(int position) {
//		Log.i(TAG, "getItem");
		return mContentList.get(position);
	}

	@Override
	public long getItemId(int position) {
//		Log.i(TAG, "getItemId");
		return position;
	}
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
//		Log.i(TAG, "getView");
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.plandetail_item, null);
	//		convertView.setBackgroundColor(Color.TRANSPARENT);
			holder = new ViewHolder();
			holder.plannum = (TextView) convertView
					.findViewById(R.id.plannumId);
			holder.planname = (TextView) convertView
					.findViewById(R.id.plannameId);
			holder.crossnum = (TextView) convertView
					.findViewById(R.id.crossnumId);	
			holder.crosslist = (TextView) convertView
					.findViewById(R.id.crosslistId);

			holder.moredetails = (ImageView) convertView.findViewById(R.id.moreDetailsId);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.plannum.setText(mContentList.get(position)[0]);
		holder.planname.setText(mContentList.get(position)[1]);
		holder.crossnum.setText(mContentList.get(position)[2]);
		holder.crosslist.setText(mContentList.get(position)[3]);
		
		holder.moredetails.setOnClickListener(this);
		holder.moredetails.setTag(position);
		
		if(selectedIndex != position) {
			convertView.setBackgroundColor(Color.TRANSPARENT);
		}else {
			convertView.setBackgroundColor(Color.GRAY);
		}
		return convertView;
	}
	public class ViewHolder {
		public TextView planname,plannum,crosslist,crossnum;
		public ImageView moredetails;
	}
	// 响应按钮点击事件,调用子定义接口，并传入View
	@Override
	public void onClick(View v) {
		mCallback.click(v);
	}
}
