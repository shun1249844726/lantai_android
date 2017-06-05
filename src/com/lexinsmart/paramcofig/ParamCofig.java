package com.lexinsmart.paramcofig;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.lexinsmart.planmanager.ImportFile;
import com.lexinsmart.util.BMapApiDemoMain;
import com.lexinsmart.util.Constants;
import com.lexinsmart_lantai.R;

public class ParamCofig extends Activity {
	TextView fleetLength_tv, fleetSpacing_tv, sendcommond_distence_tv;
	SeekBar fleetLength, fleetSpacing, sendcommond_distence;
	Button offlinemapButton,importFileBtn;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_parameter_config);
		findViews();
		fleetLength.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				Constants.FLEETLENGTH = (seekBar.getProgress())/10+5;
			}
			
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				
			}
			
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				fleetLength_tv.setText("��������:"+((seekBar.getProgress())/10+5) +"��");

			}
		});
		fleetSpacing.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				Constants.FLEETSPACING = (seekBar.getProgress()/5+10);
			}
			
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				fleetSpacing_tv.setText("�������:"+((seekBar.getProgress())/5+10) +"��");
				
			}
		});
		sendcommond_distence.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				Constants.SENDCOMMOND_DISTENCE = (seekBar.getProgress()*3+300);
			}
			
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				
			}
			
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				sendcommond_distence_tv.setText("����;���:"+((seekBar.getProgress())*3+300) +"��");
			}
		});
		offlinemapButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				intent.setClass(ParamCofig.this, BMapApiDemoMain.class);
				startActivity(intent);
			}
		});
		importFileBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				intent.setClass(ParamCofig.this, ImportFile.class);
				startActivity(intent);
			}
		});
	}

	private void findViews() {
		fleetLength_tv = (TextView) findViewById(R.id.fleetLength_tvId);
		fleetLength_tv.setText("��������:"+5 +"��");

		fleetSpacing_tv = (TextView) findViewById(R.id.fleetSpacing_tvId);
		fleetSpacing_tv.setText("�������:"+15 +"��");
		sendcommond_distence_tv = (TextView) findViewById(R.id.sendcommond_distence_tvId);
		sendcommond_distence_tv.setText("����;���:"+400 +"��");

		fleetLength = (SeekBar) findViewById(R.id.fleetLengthId);
		fleetSpacing = (SeekBar) findViewById(R.id.fleetSpacingId);
		fleetSpacing.setProgress(25);
		sendcommond_distence = (SeekBar) findViewById(R.id.sendcommond_distenceId);
		sendcommond_distence.setProgress(33);
		
		offlinemapButton = (Button) findViewById(R.id.offlinemapButtonId);
		importFileBtn = (Button)findViewById(R.id.import_file_btnId);
	}
}