package com.lexinsmart.planmanager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import org.apache.http.util.EncodingUtils;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.lexinsmart.main.MainActivity;
import com.lexinsmart.util.Constants;
import com.lexinsmart.util.StringUtil;
import com.lexinsmart_lantai.R;

public class ImportFile extends Activity {
	private Button importFileBtn;
	private EditText fileContent;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_impor_file);
		fileContent = (EditText) findViewById(R.id.import_file_content_EdtId);
		importFileBtn = (Button) findViewById(R.id.import_BtnId);
		importFileBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String FILENAME = "RoadConf.txt";
				File file = new File(Environment.getExternalStorageDirectory(),
						FILENAME);
				
                if (Environment.getExternalStorageState().equals(
                        Environment.MEDIA_MOUNTED)) {
                    try {
                    	if (StringUtil.JsonStringToArray(fileContent.getText().toString()) != null){
                            FileOutputStream fos = new FileOutputStream(file);
                            fos.write(fileContent.getText().toString().getBytes());
                            fos.close();
                            Constants.Roads = StringUtil.JsonStringToArray(fileContent.getText().toString());
                            Toast.makeText(ImportFile.this, "д���ļ��ɹ�",
                                    Toast.LENGTH_LONG).show();
                            
                    	}else {
                            Toast.makeText(ImportFile.this, "�����ļ���ʽ��",
                                    Toast.LENGTH_LONG).show();		
						}

                    } catch (Exception e) {
                        Toast.makeText(ImportFile.this, "д���ļ�ʧ��",
                                Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // ��ʱSDcard�����ڻ��߲��ܽ��ж�д������
                    Toast.makeText(ImportFile.this,
                            "��ʱSDcard�����ڻ��߲��ܽ��ж�д����", Toast.LENGTH_SHORT).show();
                }
			}
		});
	}
}
