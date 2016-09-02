package helixtech.fences.com.supporters;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import helixtech.fences.com.AppController;

/**
 * Created by helixtech-android on 4/7/16.
 */
public class VolleySingleton {
    private static VolleySingleton sInstance=null;
    private RequestQueue requestQueue;
    private VolleySingleton(){
        requestQueue= Volley.newRequestQueue(AppController.getInstance());
    }
    public RequestQueue getRequestQueue(){
        return requestQueue;
    }
    public static VolleySingleton getInstance(){
        if(sInstance==null){
            sInstance=new VolleySingleton();
        }
        return sInstance;
    }
}
