package helixtech.fences.com;



import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.text.format.Formatter;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import helixtech.fences.com.supporters.WebserviceLinks;

public class Splash extends Activity {

	 TextView tvSplashText;
     Typeface typeFace;
	GCMClientManager pushClientManager;
	String reg_id;
	String PROJECT_NUMBER="11170478224";
	Boolean signedUp;
     
	// private static final int SPLASH_DISPLAY_TIME = 2000;  /* 2 seconds */
	 private static final int SPLASH_DISPLAY_TIME = 1000;  /* 1 seconds */
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.splash);                  
        tvSplashText = (TextView) findViewById(R.id.tvSplashText);
		//typeFace = Typeface.createFromAsset(getAssets(), getResources().getString(R.string.fontSplashScreen));
		//tvSplashText.setTypeface(typeFace);

		registerGCMCall();


//		if(reg_id.length()<20)
//			registerGCMCall();
//		else if(!signedUp) {
//			generateUrl();
//		}
//		else {
//			callMainActivity(3000);
//		}
	}

	private void generateUrl(){
		if(getWifiIp().length()<=7)
			Toast.makeText(this,"Not Connected with any of our Wifi",Toast.LENGTH_LONG).show();
		else if (reg_id.length()<20) {
			final RelativeLayout parent = (RelativeLayout)findViewById(R.id.parent);
			Snackbar snackbar = Snackbar.make(parent, "Registration Failed!", Snackbar.LENGTH_INDEFINITE)
					.setAction("RETRY", new View.OnClickListener() {
						@Override
						public void onClick(View view) {
							registerGCMCall();
						}
					});
			snackbar.setActionTextColor(Color.RED);
			View sbView = snackbar.getView();
			TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
			textView.setTextColor(Color.YELLOW);
			snackbar.show();
		}
		else if(getWifiConnectedStatus()==0)
			Toast.makeText(this,"No Wifi Connection",Toast.LENGTH_LONG).show();
		else {
			SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
			SharedPreferences.Editor edit = sp.edit();
			edit.putString("gcmreg_id", reg_id);
			edit.commit();
			regiter(getWifiIp() + "/device_token/" + reg_id + "/wififlag/" + getWifiConnectedStatus());
		}
	}

	private void regiter(final String urlParams){
		String url = WebserviceLinks.registerUrl+urlParams;
		final RelativeLayout parent = (RelativeLayout)findViewById(R.id.parent);
		final ProgressBar progress_bar = (ProgressBar)findViewById(R.id.progress_bar);
		progress_bar.setVisibility(View.VISIBLE);
		final String tag_json_obj = "Reqistration";
		StringRequest req = new StringRequest(Request.Method.GET, url,
			new Response.Listener<String>(){
				@Override
				public void onResponse(String response) {
					progress_bar.setVisibility(View.GONE);
					callSignUp(response);
				}
			},
			new Response.ErrorListener() {
				@Override
				public void onErrorResponse(VolleyError error) {
					progress_bar.setVisibility(View.GONE);
					Snackbar snackbar = Snackbar
							.make(parent, "No internet connection!", Snackbar.LENGTH_INDEFINITE)
							.setAction("RETRY", new View.OnClickListener() {
								@Override
								public void onClick(View view) {
									regiter(urlParams);
								}
							});
					snackbar.setActionTextColor(Color.RED);
					View sbView = snackbar.getView();
					TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
					textView.setTextColor(Color.YELLOW);
					snackbar.show();

				}
			});
		AppController.getInstance().addToRequestQueue(req, tag_json_obj);
	}

	public String getWifiIp(){
		final WifiManager manager = (WifiManager) super.getSystemService(WIFI_SERVICE);
		final DhcpInfo dhcp = manager.getDhcpInfo();
		String ipAddress = Formatter.formatIpAddress(dhcp.gateway);

		SharedPreferences sp1 = PreferenceManager.getDefaultSharedPreferences(this);
		SharedPreferences.Editor edit1 = sp1.edit();
		edit1.putString("wifi_ip", ipAddress);
		if(ipAddress.equals("0.0.0.0"))
			edit1.putInt("wifi_connected", 0);
		else
			edit1.putInt("wifi_connected", 1);
		edit1.commit();

		return ipAddress;
	}

	public int getWifiConnectedStatus(){
		SharedPreferences getData = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
		int wifiState = getData.getInt("wifi_connected", 0);
		return wifiState;
	}

	private void callSignUp(String response){
		try {
			JSONObject jo = new JSONObject(response);
			int resp = jo.getInt("response");
			if(resp==1){
				SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
				SharedPreferences.Editor edit = sp.edit();
				edit.putBoolean("signedup", true);
				edit.commit();
				callMainActivity(2000);
			}else
				Toast.makeText(this,jo.getString("message"),Toast.LENGTH_LONG).show();
		}catch (JSONException e) {
			Log.e("Resistration Exception", e.toString());
		}
	}

	private void callMainActivity(int duration){
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				Intent mainIntent = new Intent(Splash.this, MainActivity.class);
				Splash.this.startActivity(mainIntent);
				Splash.this.finish();
			}
		}, duration);
	}

	private void registerGCMCall(){
		pushClientManager = new GCMClientManager(this, PROJECT_NUMBER);
		pushClientManager.registerIfNeeded(new GCMClientManager.RegistrationCompletedHandler() {
			@Override
			public void onSuccess(String registrationId, boolean isNewRegistration) {
				Log.d("Registration id", registrationId);
				//send this registrationId to your server
			}
			@Override
			public void onFailure(String ex) {
				super.onFailure(ex);
			}
		});
		reg_id = pushClientManager.getRegistrationId(this);
		SharedPreferences getData = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
		signedUp = getData.getBoolean("signedup", false);
		if(!signedUp) {
			generateUrl();
		}
		else {
			callMainActivity(3000);
		}
	}

}
