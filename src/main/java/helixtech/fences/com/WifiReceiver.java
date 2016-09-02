package helixtech.fences.com;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.ConnectivityManager;
import android.net.DhcpInfo;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.preference.PreferenceManager;
import android.text.format.Formatter;
import android.util.Log;

public class WifiReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {     
        ConnectivityManager conMan = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE); 
        NetworkInfo netInfo = conMan.getActiveNetworkInfo();
        if (netInfo != null && netInfo.getType() == ConnectivityManager.TYPE_WIFI){ 
            Log.d("WifiReceiver", "Have Wifi Connection");
            final WifiManager manager = (WifiManager) context.getSystemService(context.WIFI_SERVICE);
    		final DhcpInfo dhcp = manager.getDhcpInfo();
    		String ipAddress = Formatter.formatIpAddress(dhcp.gateway);
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
			Editor edit = sp.edit();	
			edit.putString("wifi_ip", ipAddress);
			edit.putInt("wifi_connected", 1);
			edit.commit();
        }
        else{
            Log.d("WifiReceiver", "Don't have Wifi Connection");
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
			Editor edit = sp.edit();		
			edit.putInt("wifi_connected", 0);
			edit.commit();
        }
    }   
};
