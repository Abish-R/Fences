package helixtech.fences.com;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import helixtech.fences.com.supporters.GPSTracker;
import helixtech.fences.com.supporters.WebserviceLinks;

/**
 * Created by helixtech-android on 4/7/16.
 */
public class SingleAdvPage extends AppCompatActivity implements View.OnClickListener{
    public TextView adv_by,adv_title,adv_desc,adv_time,adv_route,adv_app,adv_webpage;
    public ImageView adv_img;
    GPSTracker gps;
    Double userLat,userLong;
    int gotid;
    LinearLayout parent;
    String latitude,longitude,websitelink,applink,apppackagename;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.row_adv_feed);

        adv_by = (TextView) findViewById(R.id.adv_by);
        adv_title = (TextView) findViewById(R.id.adv_title);
        adv_desc = (TextView) findViewById(R.id.adv_desc);
        adv_time = (TextView) findViewById(R.id.adv_time);
        adv_route = (TextView) findViewById(R.id.adv_route);
        adv_app = (TextView) findViewById(R.id.adv_app);
        adv_webpage = (TextView) findViewById(R.id.adv_webpage);
        adv_img=(ImageView) findViewById(R.id.adv_img);
        parent = (LinearLayout)findViewById(R.id.parent);

        adv_route.setOnClickListener(this);
        adv_app.setOnClickListener(this);
        adv_webpage.setOnClickListener(this);
        gps = new GPSTracker(SingleAdvPage.this);

        gotid=getIntent().getExtras().getInt("id");
        GetResponseString(gotid);

    }

    private void setText(String one,String two,String three,String four){
        adv_by.setText(one);
        adv_title.setText(two);
        adv_desc.setText(three);
        adv_time.setText(four);
    }

    private void addImage(String url) {
        Picasso.with(this).load(url)
                .placeholder(R.drawable.signup_icon_5)
                .error(R.drawable.exclamation)
                .into(adv_img);
    }

    public double againAskingGPS(){
        if(gps.canGetLocation()) {
            userLat = gps.getLatitude();
            userLong = gps.getLongitude();
            if(userLat==0 && userLong==0)
                return 0;
            else
                return 1;
        }
        else{
            gps.showSettingsAlert();
            return 0;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.adv_route:
                if (againAskingGPS() == 0)
                    Toast.makeText(this, "Location not detected. Try again", Toast.LENGTH_SHORT).show();
                else {
                    Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                            Uri.parse("http://maps.google.com/maps?saddr=" + userLat + "," + userLong + "&daddr="+latitude+","+longitude));
                    startActivity(intent);
                }
                break;
            case R.id.adv_app:
                //final boolean installed = appInstalledOrNot("com.Dominos");
                if (appInstalledOrNot(apppackagename)) {
                    Intent LaunchIntent = getPackageManager().getLaunchIntentForPackage(apppackagename);
                    startActivity(LaunchIntent);
                } else {
                    try {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id="+apppackagename)));
                    } catch (android.content.ActivityNotFoundException anfe) {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(applink)));
                    }
                    //Toast.makeText(activity, "Three "+installed, Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.adv_webpage:
                Intent callWebpage = new Intent(this, Webview.class);
                callWebpage.putExtra("webpage",websitelink);
                startActivity(callWebpage);
                break;
        }
    }

    private boolean appInstalledOrNot(String uri) {
        PackageManager pm = getPackageManager();
        boolean app_installed;
        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            app_installed = true;
        }
        catch (PackageManager.NameNotFoundException e) {
            app_installed = false;
        }
        return app_installed;
    }
    public void GetResponseString(int id){
        // Tag used to cancel the request
        final String tag_json_obj = "string_req";

        String url = WebserviceLinks.singlePageUrl+id;

        final ProgressDialog pDialog= new ProgressDialog(this);
            pDialog.setMessage("Getting Feeds...");
            pDialog.setCancelable(false);
            pDialog.show();

        StringRequest req = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>(){
                    @Override
                    public void onResponse(String response) {
                        pDialog.hide();
                        setValuesInList(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        pDialog.hide();
                        Snackbar snackbar = Snackbar
                                .make(parent, "No internet connection!", Snackbar.LENGTH_INDEFINITE)
                                .setAction("RETRY", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        GetResponseString(gotid);
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

    private void setValuesInList(String jsonResponse) {
        try {
            JSONObject obj = new JSONObject(jsonResponse);
            JSONArray array = new JSONArray();
            array = obj.getJSONArray("data");
            for (int i = 0; i < array.length(); i++) {

                String url = array.getJSONObject(i).getString("url");
                String firstName = array.getJSONObject(i).getString("firstName");//c.getString("firstName");
                String lastName = array.getJSONObject(i).getString("lastName");
                String title = array.getJSONObject(i).getString("title");//c.getString("title");
                String type = array.getJSONObject(i).getString("type");
                String desc = array.getJSONObject(i).getString("desc");
                String kind = array.getJSONObject(i).getString("kind");
                int postId = array.getJSONObject(i).getInt("postId");
                String postUserId = array.getJSONObject(i).getString("userId");
                String postedTime = array.getJSONObject(i).getString("postedTime");

                latitude = array.getJSONObject(i).getString("latitude");
                longitude = array.getJSONObject(i).getString("longitude");
                websitelink = array.getJSONObject(i).getString("websitelink");
                applink = array.getJSONObject(i).getString("applink");
                apppackagename = array.getJSONObject(i).getString("apppackagename");

                setText(kind,title,desc,postedTime);
                if (type.equals("photo"))
                    addImage(url);
                else
                    adv_img.setVisibility(View.GONE);
            }

        }catch(JSONException e){
            Log.e("JSON Error",e.toString());
        }
    }
}

