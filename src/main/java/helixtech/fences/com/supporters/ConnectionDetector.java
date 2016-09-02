package helixtech.fences.com.supporters;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class ConnectionDetector{
 
    private static final String LOG_TAG = "Connection Detector";
	private Context _context;
 
    public ConnectionDetector(Context context){
        this._context = context;
    }
 
    /**
     * Checking for all possible internet providers
     * **/
    public boolean isConnectingToInternet(){
        ConnectivityManager connectivity = (ConnectivityManager) _context.getSystemService(Context.CONNECTIVITY_SERVICE);
          if (connectivity != null)
          {
              NetworkInfo[] info = connectivity.getAllNetworkInfo();
              if (info != null)
                  for (int i = 0; i < info.length; i++)
                      if (info[i].getState() == NetworkInfo.State.CONNECTED)
                      {
                          return true;
                      }
 
          }
          return false;
    }
    
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager 
             = (ConnectivityManager) _context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null;
    }
    
    public  boolean hasActiveInternetConnection(Context context) {
        if (isNetworkAvailable()) {
            try {
                HttpURLConnection urlc = (HttpURLConnection) (new URL("http://www.google.com").openConnection());
                urlc.setRequestProperty("User-Agent", "Test");
                urlc.setRequestProperty("Connection", "close");
                urlc.setConnectTimeout(1500); 
                urlc.connect();
                return (urlc.getResponseCode() == 200);
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error checking internet connection", e);
            }
        } else {
            Log.d(LOG_TAG, "No network available!");
        }
        return false;
    }
    
    public final boolean isInternetOn()
    {
      ConnectivityManager connec = (ConnectivityManager)
        _context.getSystemService(Context.CONNECTIVITY_SERVICE);

      // ARE WE CONNECTED TO THE NET
      if ( connec.getNetworkInfo(0).getState() == NetworkInfo.State.CONNECTED ||
           connec.getNetworkInfo(1).getState() == NetworkInfo.State.CONNECTED )
      {
        // MESSAGE TO SCREEN FOR TESTING (IF REQ)
        //Toast.makeText(this, connectionType + � connected�, Toast.LENGTH_SHORT).show();
        return true;
      }
      else if ( connec.getNetworkInfo(0).getState() == NetworkInfo.State.DISCONNECTED
        ||  connec.getNetworkInfo(1).getState() == NetworkInfo.State.DISCONNECTED  )
      {
        return false;
      }

      return false;
    }
    
}
