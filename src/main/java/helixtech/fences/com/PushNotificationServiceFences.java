package helixtech.fences.com;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.gcm.GcmListenerService;

import java.util.Random;

import helixtech.fences.com.supporters.WebserviceLinks;

/**
 * Created by kundan on 10/22/2015.
 */
public class PushNotificationServiceFences extends GcmListenerService {
    //ChatActivity ca = new ChatActivity();
    @Override
    public void onMessageReceived(String from, Bundle data) {
        String title = "Fences";//data.getString("title");
        String message = data.getString("message");
        String serverip = data.getString("serverip");
        int postid = Integer.parseInt(data.getString("post_id"));
        checkNotification(title,message,serverip,postid);
    }

    private void checkNotification(String title,String message,String serverip,int postid){
        SharedPreferences getData = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        String signedUp = getData.getString("wifi_ip", "");
        //String registrationId = getData.getString("reg_id", "");
        String reg_id = getData.getString("gcmreg_id", "");
        int wifiState = getData.getInt("wifi_connected", 0);
        if(signedUp.equals(serverip))
            createNotification(title,message,serverip,postid);
        else
            reRegiter(serverip+"/device_token/"+reg_id+"/wififlag/"+wifiState);
    }

    public void createNotification(String tit,String msg, String ip, int postId) {
        //int icon = R.drawable.ic_media_play;
        //long when = System.currentTimeMillis();
//        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
//        Notification notification = new Notification(icon, msg, when);
//
//        //String title = "DashBoard Builder";//context.getString(R.string.app_name);
//
//        Intent notificationIntent = new Intent(this, SingleAdvPage.class);
//        notificationIntent.putExtra("id",postId);
//
//        // set intent so it does not start a new activity
//        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
//        PendingIntent intent = PendingIntent.getActivity(this,(int)System.currentTimeMillis(), notificationIntent, 0);
//        notification.setLatestEventInfo(this, "", "", intent);
//        notification.contentIntent = intent;
//        notification.flags |= Notification.FLAG_AUTO_CANCEL;
//
//        // Play default notification sound
//        notification.defaults |= Notification.DEFAULT_SOUND;
//
//        // Vibrate if vibrate is enabled
//        notification.defaults |= Notification.DEFAULT_VIBRATE;
//        notificationManager.notify(1, notification);

        Notification myNotication;
        Intent intent=null;
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if(postId==0)
            intent = new Intent(this, MainActivity.class);//"helixtech.fences.com.MainActivity");
        else {
            intent = new Intent(this, SingleAdvPage.class);//"helixtech.fences.com.SingleAdvPage");
            intent.putExtra("id",postId);
        }

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 1, intent, 0);

        Notification.Builder builder = new Notification.Builder(this);
        builder.setAutoCancel(false);
        builder.setTicker("Fences");
        builder.setContentTitle(tit);
        builder.setContentText(msg);
        builder.setSmallIcon(R.drawable.ic_media_play);
        builder.setContentIntent(pendingIntent);
        //builder.setOngoing(false);
        //builder.setSubText(msg);   //API level 16
        builder.setNumber(1);
        builder.build();

        myNotication = builder.getNotification();
        myNotication.flags |= Notification.FLAG_AUTO_CANCEL;
        myNotication.defaults |= Notification.DEFAULT_SOUND;

        Random random = new Random();
        int NOTIFICATION_ID = random.nextInt(9999 - 1000) + 1000;
        manager.notify(NOTIFICATION_ID, myNotication);

    }

    private void reRegiter(String urlParams){
        String url = WebserviceLinks.registerUrl+urlParams;
        final String tag_json_obj = "Reqistration";
        StringRequest req = new StringRequest(Request.Method.GET, url,
            new Response.Listener<String>(){
                @Override
                public void onResponse(String response) {
                }
            },
            new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                }
            });
        AppController.getInstance().addToRequestQueue(req, tag_json_obj);
    }
}
