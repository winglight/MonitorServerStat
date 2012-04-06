package com.omdasoft.monitor;

import com.omdasoft.monitor.db.ServerDAO;
import com.omdasoft.monitor.db.ServerModel;
import net.yihabits.monitor.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class ServerEditorActivity extends Activity {
	
	private ServerModel m_sm = new ServerModel();
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.server);
        
        Button saveBtn = (Button) findViewById(R.id.saveBtn);
        saveBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				saveServer();
				
			}
		});
        
        Intent intent = getIntent();
		ServerModel sm = (ServerModel) intent.getSerializableExtra("ServerModel");
		if (sm == null) {
			//new server
			EditText urlTxt = (EditText) findViewById(R.id.urlTxt);
			urlTxt.setText("http://developer.mikandi.com/");
			
			EditText minuteTxt = (EditText) findViewById(R.id.minuteTxt);
			minuteTxt.setText("120");
			
			EditText checkListTxt = (EditText) findViewById(R.id.checkListTxt);
			checkListTxt.setText("/publishApp?appid=2593,/publishApp?appid=2601,/publishApp?appid=2616,/publishApp?appid=2697,/publishApp?appid=2698,/publishApp?appid=2593,/publishApp?appid=2714,/publishApp?appid=2726,/publishApp?appid=2802,/publishApp?appid=2808,/publishApp?appid=2814");
			
			EditText userIdTxt = (EditText) findViewById(R.id.userIdTxt);
			userIdTxt.setText("vncntkarl2");
			
			EditText passwordTxt = (EditText) findViewById(R.id.passwordTxt);
			passwordTxt.setText("1111111");
		}else{
			//modify server
			this.m_sm = sm;
			
			EditText urlTxt = (EditText) findViewById(R.id.urlTxt);
			urlTxt.setText(sm.getUrl());
			
			EditText minuteTxt = (EditText) findViewById(R.id.minuteTxt);
			minuteTxt.setText(String.valueOf(sm.getMinutes()));
			
			EditText verifyTxt = (EditText) findViewById(R.id.verifyTxt);
			verifyTxt.setText(sm.getVerifyTitle());
			
			EditText checkListTxt = (EditText) findViewById(R.id.checkListTxt);
			checkListTxt.setText(sm.getCheckList());
			
			EditText userIdTxt = (EditText) findViewById(R.id.userIdTxt);
			userIdTxt.setText(sm.getUserId());
			
			EditText passwordTxt = (EditText) findViewById(R.id.passwordTxt);
			passwordTxt.setText(sm.getPassword());
		}
    }
    
    private void saveServer(){
    	
    	EditText urlTxt = (EditText) findViewById(R.id.urlTxt);
    	m_sm.setUrl(urlTxt.getText().toString());
		
		EditText minuteTxt = (EditText) findViewById(R.id.minuteTxt);
		m_sm.setMinutes(Integer.valueOf((minuteTxt.getText().toString())));
		
		EditText verifyTxt = (EditText) findViewById(R.id.verifyTxt);
		m_sm.setVerifyTitle(verifyTxt.getText().toString());
		
		EditText checkListTxt = (EditText) findViewById(R.id.checkListTxt);
		m_sm.setCheckList(checkListTxt.getText().toString());
		
		EditText userIdTxt = (EditText) findViewById(R.id.userIdTxt);
		m_sm.setUserId(userIdTxt.getText().toString());
		
		EditText passwordTxt = (EditText) findViewById(R.id.passwordTxt);
		m_sm.setPassword(passwordTxt.getText().toString());
		
		//invoke DAO
		ServerDAO dba = ServerDAO.getInstance(this);
		dba.open();
		long flag;
		if(this.m_sm.getId() != -1){
			//update
			flag = dba.update(m_sm);
			 dba.close();
		}else{
			//insert
			 flag = dba.insert(m_sm);
			 dba.close();
		}
		
		if(flag != -1){
			//successful message
			toastMsg(getString(R.string.saveSuccess));
			this.finish();
		}else{
			//failed message
			toastMsg(getString(R.string.saveFail));
		}
		
    	
    }
    
    public void toastMsg(final String msg){
		runOnUiThread(new Runnable() {

	        @Override
	        public void run() {
	            Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
	        }
	    });
	}
}