package com.omdasoft.monitor;

import java.util.ArrayList;
import java.util.Timer;
import net.yihabits.monitor.R;

import com.omdasoft.monitor.db.ServerModel;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class MonitorService extends Service {
	
	private ArrayList<Timer> timers;
	private ArrayList<ServerModel> servers;
	
	@Override
	public void onCreate() {
		timers = new ArrayList<Timer>();
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO: Replace with service binding implementation.
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if(intent == null){
			Intent nintent = new Intent(Intent.ACTION_VIEW);
			nintent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			nintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			nintent.setClass(this, MonitorActivity.class);
			startActivity(nintent);
			
			return Service.START_STICKY;
			
		}
		// reset timers
		for(Timer timer : timers){
			timer.cancel();
			timer = null;
		}
		timers = new ArrayList<Timer>();
		
		//get all ServerModels
		ArrayList<ServerModel> tmp = (ArrayList<ServerModel>) intent.getSerializableExtra("servers");
		if(tmp == null){
			if(this.servers == null){
			servers = new ArrayList<ServerModel>();
			}
		}else{
			servers = tmp;
		}
		
		for(ServerModel server : servers){
			if(server.isMonitored()){
				Timer timer = new Timer();
				timer.scheduleAtFixedRate(new DownloadUtil(this, server), 0,
					server.getMinutes()*60*1000);
				timers.add(timer);
			}
		}
		
		return Service.START_STICKY;
	}
}