package helixtech.fences.com;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import helixtech.fences.com.adapter.CustomAdapter;
import helixtech.fences.com.model.CallMapActivity;
import helixtech.fences.com.model.LatLon;
import helixtech.fences.com.model.Post;
import helixtech.fences.com.supporters.RecyclerViewPositionHelper;
import helixtech.fences.com.supporters.WebserviceLinks;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener{

    RecyclerView recycler_view;
    ProgressBar progress_bar;
    View parent;
    CustomAdapter adapter;List<Post> list = new ArrayList<Post>();
    int pageNo=0;
    String userConnectIp="192.168.0.1";//"0.0.0.0"
    boolean loading=true;
    int previousTotal=0,visibleItemCount,totalItemCount,firstVisibleItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        recycler_view= (RecyclerView)findViewById(R.id.recycler_view);
        progress_bar = (ProgressBar)findViewById(R.id.progress_bar);
        parent = findViewById(R.id.parent);

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recycler_view.setLayoutManager(mLayoutManager);
        recycler_view.setItemAnimator(new DefaultItemAnimator());
        final RecyclerViewPositionHelper rvph = new RecyclerViewPositionHelper(recycler_view);
        adapter = new CustomAdapter(this,list);
        recycler_view.setAdapter(adapter);
        GetResponseString(pageNo);

        recycler_view.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

            }
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                visibleItemCount = recycler_view.getChildCount();
                totalItemCount = rvph.getItemCount();
                firstVisibleItem = rvph.findFirstVisibleItemPosition();

                if (loading) {
                    if (totalItemCount > previousTotal) {
                        loading = false;
                        previousTotal = totalItemCount;
                    }
                }
                if (!loading && (totalItemCount - visibleItemCount)
                        <= (firstVisibleItem + 2)) {//2 is bottom remaining items.
                    // End has been reached
                    Log.i("Feeds", "end reached");
                    pageNo++;
                    if(pageNo>=1)
                        progress_bar.setVisibility(View.VISIBLE);
                    GetResponseString(pageNo);
                    loading = true;
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.map) {
            Toast.makeText(this,"Map Invoking",Toast.LENGTH_SHORT).show();
            //locationData();

            startActivity(new Intent(this,MapActivity.class));
        }
        /*if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }*/

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void GetResponseString(int pageno){
        // Tag used to cancel the request
        final String tag_json_obj = "string_req";

        //String url = WebserviceLinks.feedsUrl;

        final ProgressDialog pDialog= new ProgressDialog(this);;
        if(pageNo==0) {
            pDialog.setMessage("Getting Feeds...");
            pDialog.setCancelable(false);
            pDialog.show();
        }
        Uri.Builder builder = Uri.parse(WebserviceLinks.feedsUrl).buildUpon();
        builder.appendQueryParameter("user_ip", userConnectIp);
        builder.appendQueryParameter("pageNo", String.valueOf(pageNo));
        String loginUrl=builder.build().toString();

        StringRequest req = new StringRequest(Request.Method.GET, loginUrl,
                new Response.Listener<String>(){
                    @Override
                    public void onResponse(String response) {
                        if(pageNo==0)
                            pDialog.hide();
                        else
                            progress_bar.setVisibility(View.GONE);
                        setValuesInList(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if(pageNo==0)
                            pDialog.hide();
                        else
                            progress_bar.setVisibility(View.GONE);
                        Snackbar snackbar = Snackbar
                                .make(parent, "No internet connection!", Snackbar.LENGTH_INDEFINITE)
                                .setAction("RETRY", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        GetResponseString(pageNo);
                                    }
                                });
                        snackbar.setActionTextColor(Color.RED);
                        View sbView = snackbar.getView();
                        TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
                        textView.setTextColor(Color.YELLOW);
                        snackbar.show();

                    }
                });
//        {  @Override
//            protected Map<String, String> getParams() throws AuthFailureError {
//                Map<String, String> params = new HashMap<String, String>();
//                params.put("user_ip", userConnectIp);
//                params.put("pageNo", String.valueOf(pageNo));
//                return params;
//            }
//        };

// Adding request to request queue
        AppController.getInstance().addToRequestQueue(req, tag_json_obj);
        //return valiue;
    }

    private void setValuesInList(String jsonResponse) {
        try {
            JSONObject obj = new JSONObject(jsonResponse);
            JSONArray array = new JSONArray();
            array = obj.getJSONArray("data");
            for (int i = 1; i < array.length(); i++) {
                Post p1 = new Post();
                //kind,type,title,url,desc,postedTime,noOfComments,noOfLikes,postId,userId,firstName,lastName,likeStatus

                String url = array.getJSONObject(i).getString("url");
                String firstName = array.getJSONObject(i).getString("firstName");//c.getString("firstName");
                String lastName = array.getJSONObject(i).getString("lastName");
                String title = array.getJSONObject(i).getString("title");//c.getString("title");
                String type = array.getJSONObject(i).getString("type");
                String desc = array.getJSONObject(i).getString("desc");
                String kind = array.getJSONObject(i).getString("kind");
                //int noOfLikes = c.getInt("noOfLikes");
                //int noOfComments = c.getInt("noOfComments");
                //int likeStatus = c.getInt("likeStatus");
                int postId = array.getJSONObject(i).getInt("postId");
                String postUserId = array.getJSONObject(i).getString("userId");
                String postedTime = array.getJSONObject(i).getString("postedTime");

                //String location = array.getJSONObject(i).getString("location");
                String latitude = array.getJSONObject(i).getString("latitude");
                String longitude = array.getJSONObject(i).getString("longitude");
                String websitelink = array.getJSONObject(i).getString("websitelink");
                String applink = array.getJSONObject(i).getString("applink");
                String apppackagename = array.getJSONObject(i).getString("apppackagename");

                p1.setUrl(url);
                p1.setFirstName(firstName);
                p1.setLastName(lastName);
                p1.setTitle(title);
                p1.setType(type);
                p1.setDesc(desc);
                p1.setKind(kind);
                p1.setPostedTime(postedTime);
                //p1.setNoOfComments(noOfComments);
                //p1.setNoOfLikes(noOfLikes);
                //p1.setLikeStatus(likeStatus);
                p1.setPostId(postId);
                p1.setUserId(postUserId);

                //p1.setLocation(location);
                p1.setLatitude(latitude);
                p1.setLongitude(longitude);
                p1.setWebpageLink(websitelink);
                p1.setAppGoogleLink(applink);
                p1.setAppPackage(apppackagename);

                list.add(p1);
                adapter.notifyDataSetChanged();
            }

        }catch(JSONException e){
            Log.e("JSON Error",e.toString());
        }
    }



//    @Override
//    public void callMapActivity(String json) {
//        ArrayList<LatLon> list = new ArrayList<LatLon>();
//        for(int i=1;i<10;i++){
//            LatLon ll = new LatLon();
//            ll.setWifiName("Helix"+1);
//            ll.setLatitude((double)i*i);
//            ll.setLongitude((double)i*i);
//            list.add(ll);
//        }
//        Intent intent = new Intent(this, MapActivity.class);
//        intent.putExtra("locationData",list);
//        startActivity(intent);
//    }

//    @Override
//    public void onRefresh() {
//        GetResponseString();
//    }
}
