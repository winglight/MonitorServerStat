package com.omdasoft.monitor;

import java.util.ArrayList;
import java.util.List;
import net.yihabits.monitor.R;

import com.google.ads.AdRequest;
import com.google.ads.AdSize;
import com.google.ads.AdView;
import com.omdasoft.monitor.db.ServerDAO;
import com.omdasoft.monitor.db.ServerDBOpenHelper;
import com.omdasoft.monitor.db.ServerModel;

import android.app.ListActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

public class MonitorActivity extends ListActivity {
	private ServerDAO dba;
	private ProgressReceiver receiver;
	
	private Handler mHandler = new Handler();

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		dba = ServerDAO.getInstance(this);

		setContentView(R.layout.main);

		// ad initialization
		// Create the adView
		AdView adView = new AdView(this, AdSize.BANNER, "a14dd47e50f3b70");
		// Lookup your LinearLayout assuming it��s been given
		// the attribute android:id="@+id/mainLayout"
		LinearLayout layout = (LinearLayout) findViewById(R.id.ad_layout);
		// Add the adView to it
		layout.addView(adView);
		// Initiate a generic request to load it with an ad
		adView.loadAd(new AdRequest());

		ToggleButton stopAllBtn = (ToggleButton) findViewById(R.id.stopAllBtn);
		stopAllBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if(((ToggleButton)v).isChecked()){
					Intent myIntent = new Intent();
					ServerAdapter adapter = (ServerAdapter) MonitorActivity.this.getListAdapter();
					myIntent.putExtra("servers", adapter.getServers());
					myIntent.setClass(MonitorActivity.this, MonitorService.class);
					startService(myIntent);
				}else{
					Intent myIntent = new Intent();
					stopService(myIntent);
					
					//modify list items status
					ServerAdapter adapter = (ServerAdapter) MonitorActivity.this.getListAdapter();
					ArrayList<ServerModel> servers = adapter.getServers();
					for(ServerModel sm : servers){
						updateProgress(sm, 0, getString(R.string.monitorStop));
					}
				}
				

			}
		});
		
		Button clearLogsBtn = (Button) findViewById(R.id.clearLogsBtn);
		clearLogsBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				clearLogs();
			}
		});
		
		//listen phone state
		MyPhoneStateListener phoneListener=new MyPhoneStateListener();
    	phoneListener.setContext(this);

        TelephonyManager telephony = (TelephonyManager) 

        this.getSystemService(Context.TELEPHONY_SERVICE);

        telephony.listen(phoneListener,PhoneStateListener.LISTEN_CALL_STATE);

	}
	
	public class MyPhoneStateListener extends PhoneStateListener {
		
		private Context context;

		  public Context getContext() {
			return context;
		}

		public void setContext(Context context) {
			this.context = context;
		}

		public void onCallStateChanged(int state,String incomingNumber){

		  switch(state){

		    case TelephonyManager.CALL_STATE_IDLE:{

		      Log.d("DEBUG", "IDLE");
		      
		      //restart service
		      Intent myIntent = new Intent();
		    	myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				myIntent.setClass(context, MonitorService.class);
				context.startService(myIntent);
				Toast.makeText(context, context.getString(R.string.monitorStart), Toast.LENGTH_LONG).show();

		    break;
		    }

		    case TelephonyManager.CALL_STATE_OFFHOOK:

		      Log.d("DEBUG", "OFFHOOK");

		    case TelephonyManager.CALL_STATE_RINGING:

		      Log.d("DEBUG", "RINGING");
		      
		      //pause service
		      Intent myIntent = new Intent();
		    	myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				myIntent.setClass(context, MonitorService.class);
				context.stopService(myIntent);
				Toast.makeText(context, context.getString(R.string.monitorStop), Toast.LENGTH_LONG).show();

		    break;

		    }

		  } 

		}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();

		resetData();
	}

	private void resetData() {
		dba.open();
		try{
			Intent myIntent = new Intent();
			myIntent.setClass(this, MonitorService.class);
			stopService(myIntent);
		}catch(Exception e){
			e.printStackTrace();
		}
		
		ServerAdapter adapter = new ServerAdapter(this);

		Intent myIntent = new Intent();
		myIntent.putExtra("servers", adapter.getServers());
		myIntent.setClass(this, MonitorService.class);
		startService(myIntent);

		setListAdapter(adapter);
	}

	@Override
	public void onResume() {
		IntentFilter filter;
		filter = new IntentFilter(DownloadUtil.SERVER_MONITOR);
		receiver = new ProgressReceiver();
		registerReceiver(receiver, filter);
		super.onResume();
	}

	@Override
	public void onPause() {
		unregisterReceiver(receiver);
		super.onPause();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		MenuInflater mi = getMenuInflater();
		mi.inflate(R.menu.main_menu, menu);
		return true;
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_add_server: {
			// popup editor window of server
			Intent intent = new Intent(Intent.ACTION_VIEW);
			intent.setClass(MonitorActivity.this, ServerEditorActivity.class);
			startActivity(intent);
			return true;
		}
		case R.id.menu_help: {
			// popup the about window
			Intent intent = new Intent(Intent.ACTION_VIEW);
			intent.setClass(MonitorActivity.this, AboutActivity.class);
			startActivity(intent);
			return true;
		}
		}
		return super.onMenuItemSelected(featureId, item);
	}

	private void showHitoryOfServer(String logFile) {
		// popup the about window
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.putExtra("logfile", logFile);
		intent.setClass(this, AboutActivity.class);
		this.startActivity(intent);
	}

	private void showEditorOfServer(ServerModel sm) {
		// popup editor window of server
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.putExtra("ServerModel", sm);
		intent.setClass(this, ServerEditorActivity.class);
		this.startActivity(intent);
	}

	private void deleteServer(ServerModel sm) {
		dba.open();
		long flag = dba.delete(sm.getId());
		if (flag != -1) {
			// successful message
			toastMsg(getString(R.string.deleteSuccess));
			DownloadUtil du = new DownloadUtil(this, sm);
			du.clearLog();
			resetData();
		} else {
			// failed message
			toastMsg(getString(R.string.deleteFail));
		}
	}

	private void switchMonitorService(ServerModel sm, boolean isMonitored) {
		// update table
		dba.open();
		sm.setMonitored(isMonitored?1:0);
		long flag = dba.update(sm);
		if (flag != -1) {
			// successful message
			toastMsg(getString(R.string.saveSuccess));
		} else {
			// failed message
			toastMsg(getString(R.string.saveFail));
		}
		dba.close();

		// start or stop service
		resetData();
		
		if(!isMonitored){
			updateProgress(sm, 0, getString(R.string.monitorStop));
		}
	}

	private class ServerAdapter extends BaseAdapter {
		private LayoutInflater mInflater;
		private ArrayList<ServerModel> servers;

		public ArrayList<ServerModel> getServers() {
			return servers;
		}

		public ServerAdapter(Context context) {
			mInflater = LayoutInflater.from(context);
			servers = new ArrayList<ServerModel>();
			getdata();
		}
		
		public void getdata() {
			Cursor c = dba.getAllServer();
			startManagingCursor(c);
			if (c.moveToFirst()) {
				do {
					long id = c.getLong(0);
					String url = c.getString(c
							.getColumnIndex(ServerDBOpenHelper.SERVER_URL));
					int minutes = c.getInt(c
							.getColumnIndex(ServerDBOpenHelper.MINUTES));
					String verifyTitle = c.getString(c
							.getColumnIndex(ServerDBOpenHelper.VERIFY_TITLE));
					int flag = c.getInt(c
							.getColumnIndex(ServerDBOpenHelper.IS_MONITORED));
					ServerModel temp = new ServerModel(id, url, minutes, flag,
							verifyTitle);
					String checkList = c.getString(c
							.getColumnIndex(ServerDBOpenHelper.CHECK_LIST));
					String userId = c.getString(c
							.getColumnIndex(ServerDBOpenHelper.USER_ID));
					String password = c.getString(c
							.getColumnIndex(ServerDBOpenHelper.PASSWORD));
					temp.setCheckList(checkList);
					temp.setUserId(userId);
					temp.setPassword(password);
					servers.add(temp);
				}while (c.moveToNext());
			}
		}

		@Override
		public int getCount() {
			return servers.size();
		}

		public ServerModel getItem(int i) {
			return servers.get(i);
		}

		public long getItemId(int i) {
			return i;
		}

		public View getView(int position, View convertView, ViewGroup vg) {
			if (position < 0 || position > this.servers.size())
				return null;

			final ServerModel sm = this.servers.get(position);

			View row = convertView;
			if (row == null) {
				row = mInflater.inflate(R.layout.list_item, null);
			}
			row.setId((int) sm.getId());
			row.setClickable(false);

			// set servers of tutor to label
			TextView urlLbl = (TextView) row.findViewById(R.id.row_url);
			String url = sm.getUrl();
			urlLbl.setText(url);

			// get logfile
			final String logFile = sm.getPath();

			// initialize all of buttons
			ToggleButton isMonitorBtn = (ToggleButton) row
					.findViewById(R.id.startBtn);
			isMonitorBtn.setChecked((sm.isMonitored()));
			isMonitorBtn.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					switchMonitorService(sm,
							((ToggleButton) v).isChecked());

				}
			});

			Button hitoryBtn = (Button) row.findViewById(R.id.historyBtn);
			hitoryBtn.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					showHitoryOfServer(logFile);

				}
			});

			Button updateBtn = (Button) row.findViewById(R.id.updatBtn);
			updateBtn.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					showEditorOfServer(sm);

				}
			});

			Button deleteBtn = (Button) row.findViewById(R.id.deleteBtn);
			deleteBtn.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					deleteServer(sm);

				}
			});

			return (row);
		}

	}

	public void toastMsg(final String msg) {
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG)
						.show();
			}
		});
	}
	
	private void updateProgress(final ServerModel sm, final int progress, final String message){
		// Update the progress bar
        mHandler.post(new Runnable() {
            public void run() {
            	ListView lv = MonitorActivity.this.getListView();
            	for(int i=0 ; i < lv.getCount() ; i++){
    				ServerModel smtmp = (ServerModel) lv.getItemAtPosition(i);
    				if(smtmp.getId() == sm.getId()){
    					View view = lv.getChildAt(i);
    					
    					//get progress bar
    					ProgressBar pb = (ProgressBar) view.findViewById(R.id.progressBar);
    					pb.setProgress(progress);
    					
    					//get message textview
    					TextView msgLbl = (TextView) view.findViewById(R.id.messageLbl);
    					msgLbl.setText(getString(R.string.status) + message);
    					
    					//modify status textview
    					TextView statusLbl = (TextView) view.findViewById(R.id.statusTxt);
    					if(progress != 100){
    						statusLbl.setBackgroundResource(R.color.yellow);
    					}else{
    						if(message.endsWith("true")){
    							statusLbl.setBackgroundResource(R.color.green);
    						}else{
    							statusLbl.setBackgroundResource(R.color.red);
    						}
    					}
    					
    					break;
    				}
    			}
            }
        });
	}

	public class ProgressReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			// update status for servers
			ServerModel sm = (ServerModel) intent.getSerializableExtra("sm");
			int progress = intent.getIntExtra("progress", 0);
			String msg = intent.getStringExtra("message");
			
			updateProgress(sm, progress, msg);
		}
	}
	
	public void clearLogs(){
		boolean flag = true;
		ServerAdapter adapter = (ServerAdapter) MonitorActivity.this.getListAdapter();
		ArrayList<ServerModel> servers = adapter.getServers();
		for(ServerModel sm : servers){
			DownloadUtil du = new DownloadUtil(this, sm);
			flag = du.clearLog() && flag;
		}
		if(flag){
			// successful message
			toastMsg(getString(R.string.clearSuccess));
		} else {
			// failed message
			toastMsg(getString(R.string.clearFail));
		}
	}
}