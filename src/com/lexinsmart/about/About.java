package com.lexinsmart.about;

import com.lexinsmart_lantai.R;

import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;

public class About extends Activity {
	private TextView versiontv ;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about);
		versiontv = (TextView) findViewById(R.id.version_code_id);
		try {
			versiontv.setText("�汾�ţ�"+getVersionName());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	private String getVersionName() throws Exception  
	{  
	        // ��ȡpackagemanager��ʵ��  
	        PackageManager packageManager = getPackageManager();  
	        // getPackageName()���㵱ǰ��İ�����0�����ǻ�ȡ�汾��Ϣ  
	        PackageInfo packInfo = packageManager.getPackageInfo(getPackageName(),0);  
	        String version = packInfo.versionName;  
	        System.out.println("version:"+version);
	        return version;  
	} 
}
