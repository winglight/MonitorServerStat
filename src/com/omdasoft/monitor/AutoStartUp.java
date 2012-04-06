package com.omdasoft.monitor;

import java.util.ArrayList;
import net.yihabits.monitor.R;

import com.omdasoft.monitor.db.ServerDAO;
import com.omdasoft.monitor.db.ServerModel;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

public class AutoStartUp extends BroadcastReceiver
{
private static final String TAG = "AutoStartUp";
public void onReceive(Context context, Intent intent)
{
    String action = intent.getAction();
    if(action.equals(Intent.ACTION_BOOT_COMPLETED))
    {
    	Intent myIntent = new Intent();
    	myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		myIntent.setClass(context, MonitorActivity.class);
		context.startActivity(myIntent);
		Toast.makeText(context, context.getString(R.string.monitorStart), Toast.LENGTH_LONG).show();
		
    }
}


}