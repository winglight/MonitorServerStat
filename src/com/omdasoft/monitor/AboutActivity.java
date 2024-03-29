package com.omdasoft.monitor;

import com.google.ads.AdRequest;
import com.google.ads.AdSize;
import com.google.ads.AdView;
import net.yihabits.monitor.R;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.LinearLayout;

public class AboutActivity extends Activity {
	
	private String LOGTAG = "AboutActivity";

	/** Called when the activity is first created. */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.history);
		
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
		
		//set text of about content
		WebView about = (WebView)findViewById(R.id.about_content);
		registerForContextMenu(about);
//		about.getSettings().setJavaScriptEnabled(true);
		
		Intent intent = getIntent();
		String path = intent.getStringExtra("logfile");
		if (path == null) {
			about.loadUrl("file:///android_asset/help.html");
		}else{
			about.loadUrl("file://" + DownloadUtil.getLogDir() + path);
		}
		
		Button backBtn = (Button) findViewById(R.id.backBtn);
		backBtn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(Intent.ACTION_VIEW);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				intent.setClass(AboutActivity.this, MonitorActivity.class);
				startActivity(intent);
				AboutActivity.this.finish();
			}
		});
	}
	
}
