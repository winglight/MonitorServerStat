package com.omdasoft.monitor;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimerTask;

import net.htmlparser.jericho.Element;
import net.htmlparser.jericho.HTMLElementName;
import net.htmlparser.jericho.Source;
import net.yihabits.monitor.R;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import com.omdasoft.monitor.db.ServerModel;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;

public class DownloadUtil extends TimerTask{

	public static final String SERVER_MONITOR = "Server_Monitor";
	
	private Context context;
	private ServerModel sm;
	private String url;
	private String logFile;
	private String verifyTitle;
	private String basePath; // external storage path
	private String[] checkList;
	private String userId;
	private String password;

	public DownloadUtil(Context context, ServerModel sm) {
		this.context = context;
		this.sm = sm;
		this.url = sm.getUrl();
		this.logFile = sm.getPath();
		this.verifyTitle = sm.getVerifyTitle();
		this.basePath = initBaseDir();
		if(sm.getCheckList() != null){
			this.checkList = sm.getCheckList().split(",");
		}
		this.userId = sm.getUserId();
		this.password = sm.getPassword();
	}

	private void monitorUrl() {
		//send message to the activity
		notifyStatus(0, this.context.getString(R.string.startingMonitor), false);
		
		
		HttpEntity resEntity = null;
		String path = this.basePath + this.logFile;
		boolean flag = false;
		String message = this.context.getString(R.string.worksFine);
		DefaultHttpClient httpclient = null;
		try {
	        
			httpclient = new DefaultHttpClient();

			HttpGet httpget = new HttpGet(this.url);

			HttpResponse response = httpclient.execute(httpget);
			
			//send message to the activity
			notifyStatus(30, this.context.getString(R.string.gettingResult), false);

			int status = response.getStatusLine().getStatusCode();

			if (status == HttpStatus.SC_OK) {
				
				//check list
				if(checkList != null && checkList.length > 0){
					checkList();
				}

				// verify title of page
				if (this.verifyTitle != null && !"".equals(this.verifyTitle)) {
					resEntity = response.getEntity();

					// judge content type text or binary
					if (resEntity.getContentType() != null
							&& resEntity.getContentType().getValue()
									.startsWith("text")) {

						String result = EntityUtils.toString(resEntity);
						Source source = new Source(result);
							String title = source
									.getAllElements(HTMLElementName.TITLE)
									.get(0).getContent().getTextExtractor()
									.toString();
							
							if(title.contains(this.verifyTitle)){
								flag = true;
							}else{
								message = "Error: the title of response page is: " + title
								+ "<br>but the expected title is: " + this.verifyTitle;
							}
					} else {
						message = "Error: the response is binary file but text type.";
					}

				} else {
					flag = true;
				}

			} else {
				message = "Error: response status code is: " + status
						+ "<br>details:" + response.getStatusLine();
			}
		} catch (Exception e) {
			message = "Error:" + e + "<br>details:" + e.getMessage();
		} finally {

			if (httpclient != null && httpclient.getConnectionManager() != null) {
				httpclient.getConnectionManager().shutdown();
			}
			if (resEntity != null) {
				try {
					resEntity.consumeContent();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		//send message to the activity
		notifyStatus(50, message, !flag);
		
		
		//send message to the activity
		notifyStatus(80, this.context.getString(R.string.writeLog), false);

		// save sever status to logs
		saveLogfile(flag, message, path);
		
		//send message to the activity
		notifyStatus(100, this.context.getString(R.string.finishMonitor) + flag, false);
		
		
	}
	
	private String login(DefaultHttpClient httpclient, final String path) {
		String login_url = path;
		String res=null;
		try {
//			httpclient.setRedirectStrategy(new MyDefaultRedirectStrategy());
//			
//			((MyDefaultRedirectStrategy)(httpclient.getRedirectStrategy())).setLocation(path);
			
			//1.go to login page
			HttpGet httpget = new HttpGet(url);
			HttpResponse response = httpclient.execute(httpget);
            HttpEntity entity = response.getEntity();
            res = EntityUtils.toString(entity, "utf-8");
            entity.consumeContent();
            
			//submit lgoin form
			HttpPost httpost = new HttpPost(login_url);

			List<NameValuePair> nvps = new ArrayList<NameValuePair>();
			String salt = "1309579411";
					nvps.add(new BasicNameValuePair("passwordSalt", salt));
			
			nvps.add(new BasicNameValuePair("username", this.userId));
			nvps.add(new BasicNameValuePair("password", this.password));
			nvps.add(new BasicNameValuePair("passwordHash", "662d32c0081d5d9d9efe131b995a1705"));
			httpost.setEntity(new UrlEncodedFormEntity(nvps, "utf-8"));

			response = httpclient.execute(httpost);
			entity = response.getEntity();
			
			res = EntityUtils.toString(entity, "utf-8");
			entity.consumeContent();

		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return res;

	}
	
	private void checkList(){
		Thread saveUrl = new Thread() {
			DefaultHttpClient httpclient = new DefaultHttpClient();
			
				public void run() {
					
					
					//login
						login(httpclient, url);
					
					for(String checkUrl : checkList){
						
						try {
							HttpGet httpget = new HttpGet(url + checkUrl);
							
							HttpResponse response = httpclient.execute(httpget);
							
							//pause 10 minutes
							sleep(600000);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
					}
				}
			};
			new Thread(saveUrl).start();
	}

	private void save2card(String content, String path, String encode) {
		try {
			// save to sdcard
			FileOutputStream fos = new FileOutputStream(new File(path));
			IOUtils.write(content, fos, encode);

			// release all instances
			fos.flush();
			fos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public boolean clearLog() {
		boolean flag = false;
		try {
			String path = this.basePath + this.logFile;
			File file = new File(path);
			flag = file.delete();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return flag;
	}
	
	public String initBaseDir() {
		File sdDir = Environment.getExternalStorageDirectory();
		File uadDir = null;
		if (sdDir.exists() && sdDir.canWrite()) {

		} else {
			sdDir = Environment.getDataDirectory();

		}
		uadDir = new File(sdDir.getAbsolutePath() + "/monitor_log/");
		if (uadDir.exists() && uadDir.canWrite()) {

		} else {
			uadDir.mkdir();
		}
		return uadDir.getAbsolutePath() + "/";
	}
	
	private void iniFile(String path){
		String content = "<html><head><title>URL:" + this.url + "</title><meta " +
		"http-equiv='Content-Type' content='text/html; charset=UTF-8'>" +
		"</head><body><table border='0'><tr>Datetime<td></td><td>Flag</td><td>Message</td></tr><tr><td></td></tr></table><br><br></body></html>";
		
			save2card(content, 
					path, "UTF-8");
	}

	private void saveLogfile(boolean flag, String message, String path) {
		File file = new File(path);
		if(!file.exists()){
			iniFile(path);
		}
		try {
			String content = IOUtils.toString(new FileInputStream(
					new File(path)));
			SimpleDateFormat formatter4datetime = new SimpleDateFormat(
					"yyyy-MM-dd HH:mm:ss");

			if(!flag){
				content = content.replace(
						"<tr><td></td></tr>",
						"<tr><td></td></tr><tr bgcolor='#FF0000'><td nowrap='nowrap'>" + formatter4datetime.format(new Date()) + "</td><td>"
								+ flag
								+ "</td><td nowrap='nowrap'>" + message + "</td></tr>");
			}else{
			content = content.replace(
					"<tr><td></td></tr>",
					"<tr><td></td></tr><tr><td nowrap='nowrap'>" + formatter4datetime.format(new Date()) + "</td><td>"
							+ flag
							+ "</td><td nowrap='nowrap'>" + message + "</td></tr>");
			}
			save2card(content, path, "UTF-8");

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static String getLogDir(){
		File sdDir = Environment.getExternalStorageDirectory();
		if (sdDir.exists() && sdDir.canWrite()) {

		} else {
			sdDir = Environment.getDataDirectory();

		}
		return sdDir.getAbsolutePath() + "/monitor_log/";
	}

	@Override
	public void run() {
		monitorUrl();
		
	}
	
	private void notifyStatus(int progress, String message, boolean isAlarm){
		Intent intent = new Intent(SERVER_MONITOR);
		intent.putExtra("progress", progress);
		intent.putExtra("message", message);
		intent.putExtra("sm", this.sm);
		
		this.context.sendBroadcast(intent);
		
		if(isAlarm){
			String ns = Context.NOTIFICATION_SERVICE;
			NotificationManager mNotificationManager = (NotificationManager) this.context.getSystemService(ns);
			
			int icon = R.drawable.ic_menu_favorite;
			long when = System.currentTimeMillis();

			Notification notification = new Notification(icon, message, when);
			notification.defaults = Notification.DEFAULT_ALL;
			
			intent = new Intent(Intent.ACTION_VIEW);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.setClass(this.context, MonitorActivity.class);
			PendingIntent contentIntent = PendingIntent.getActivity(this.context, 0, intent, Intent.FLAG_ACTIVITY_NEW_TASK);

			notification.setLatestEventInfo(context, sm.getUrl(), message, contentIntent);
			
			mNotificationManager.notify(1, notification);
			
		}
	}
	

}
