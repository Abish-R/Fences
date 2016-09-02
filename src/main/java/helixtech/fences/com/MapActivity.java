package helixtech.fences.com;

/**
 * Created by helixtech-android on 9/6/16.
 */
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
        import android.os.Bundle;
import android.util.Log;
import android.view.View;
        import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.common.ConnectionResult;
        import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
        import com.google.android.gms.maps.CameraUpdateFactory;
        import com.google.android.gms.maps.GoogleMap;
        import com.google.android.gms.maps.OnMapReadyCallback;
        import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
        import com.google.android.gms.maps.model.Marker;
        import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

import helixtech.fences.com.model.LatLon;
import helixtech.fences.com.supporters.WebserviceLinks;

public class MapActivity extends FragmentActivity implements
        OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        GoogleMap.OnMarkerDragListener,
        GoogleMap.OnMapLongClickListener,
        View.OnClickListener{

    //Our Map
    private GoogleMap mMap;

    //To store longitude and latitude from map
    private double longitude;
    private double latitude;

    //Buttons
//    private ImageButton buttonSave;
//    private ImageButton buttonCurrent;
//    private ImageButton buttonView;

    //Google ApiClient
    private GoogleApiClient googleApiClient;
    private LocationRequest locationRequest;
    Location location;
    LocationServices fusedLocationProviderApi;
    double latFromServer[];
    double lonFromServer[];
    ArrayList<LatLon> array;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
//        SupportMapFragment mapFragment1 = (SupportMapFragment) getSupportFragmentManager()
//                .findFragmentById(R.id.map1);

        mapFragment.getMapAsync(this);
       // mapFragment1.getMapAsync(this);

//        array =  (ArrayList<LatLon>)getIntent().getExtras().getSerializable("locationData");

        //Initializing googleapi client
        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(3000);
        locationRequest.setFastestInterval(1000);

        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        if (googleApiClient != null) {
            googleApiClient.connect();
        }

        //Initializing views and adding onclick listeners
//        buttonSave = (ImageButton) findViewById(R.id.buttonSave);
//        buttonCurrent = (ImageButton) findViewById(R.id.buttonCurrent);
//        buttonView = (ImageButton) findViewById(R.id.buttonView);
//        buttonSave.setOnClickListener(this);
//        buttonCurrent.setOnClickListener(this);
//        buttonView.setOnClickListener(this);
    }

    @Override
    protected void onStart() {
        googleApiClient.connect();
        super.onStart();
    }

    @Override
    protected void onStop() {
        googleApiClient.disconnect();
        super.onStop();
    }

    //Getting current location
    private void getCurrentLocation() {
        mMap.clear();

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Check Permissions Now
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    1);
        } else {
            // permission has been granted, continue as usual
            location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
        }

        //Creating a location object
        //Location location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
        if (location != null) {
            //Getting longitude and latitude
            longitude = location.getLongitude();
            latitude = location.getLatitude();
            Log.i(""+longitude,""+latitude);

            //moving the map to location
            moveMap();
        }
    }

    private void addOverlay(double lat, double lon){
        LatLng latLng = new LatLng(lat, lon);
        mMap.addMarker(new MarkerOptions()
                .position(latLng) //setting position
                .draggable(true) //Making the marker draggable
                .title("Current Location")); //Adding a title
    }

    //Function to move the map
    private void moveMap() {
        //String to display current latitude and longitude
        String msg = latitude + ", "+longitude;

        //Creating a LatLng Object to store Coordinates
        LatLng latLng = new LatLng(latitude, longitude);

        //Adding marker to map
        mMap.addMarker(new MarkerOptions()
                .position(latLng) //setting position
                .draggable(true) //Making the marker draggable
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.cast_ic_notification_1))
                .title("Current Location")); //Adding a title

        //Moving the camera
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));

        //Animating the camera
        mMap.animateCamera(CameraUpdateFactory.zoomTo(10));

        //Displaying current coordinates in toast
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LatLng latLng = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(latLng).draggable(true));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.setOnMarkerDragListener(this);
        mMap.setOnMapLongClickListener(this);


    }

    @Override
    public void onConnected(Bundle bundle) {
        //location.requestLocationUpdates(googleApiClient,  locationRequest, this);
        getCurrentLocation();
        produceOverlay();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public void onMapLongClick(LatLng latLng) {
        //Clearing all the markers
        mMap.clear();

        //Adding a new marker to the current pressed position
        mMap.addMarker(new MarkerOptions()
                .position(latLng)
                .draggable(true));
    }

    @Override
    public void onMarkerDragStart(Marker marker) {

    }

    @Override
    public void onMarkerDrag(Marker marker) {

    }

    @Override
    public void onMarkerDragEnd(Marker marker) {
        //Getting the coordinates
        latitude = marker.getPosition().latitude;
        longitude = marker.getPosition().longitude;

        //Moving the map
        moveMap();
    }

    @Override
    public void onClick(View v) {
//        if(v == buttonCurrent){
//            getCurrentLocation();
//            moveMap();
//        }else if(v == buttonSave){
//            getCurrentLocation();
//            addOverlay(10,10);
//            addOverlay(10,20);
//            addOverlay(10,30);
//
//        }else if(v == buttonView){
//            getCurrentLocation();
//            addOverlay(1,1);
//            addOverlay(2,2);
//            addOverlay(3,3);
//            addOverlay(4,4);
//
//        }
    }

    private void locationData(){
        // Tag used to cancel the request
        final String tag_json_obj = "string_req_location";

        final ProgressDialog pDialog= new ProgressDialog(this);;
        pDialog.setMessage("Getting Feeds...");
        pDialog.setCancelable(false);
        pDialog.show();

//        Uri.Builder builder = Uri.parse(WebserviceLinks.feedsUrl).buildUpon();
//        builder.appendQueryParameter("user_ip", userConnectIp);
//        builder.appendQueryParameter("pageNo", String.valueOf(pageNo));
//        String loginUrl=builder.build().toString();

        StringRequest req = new StringRequest(Request.Method.GET, null,//loginUrl,
                new Response.Listener<String>(){
                    @Override
                    public void onResponse(String response) {
                        pDialog.hide();
                        produceOverlay();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        pDialog.hide();
                        Snackbar snackbar = Snackbar
                                .make(null, "No internet connection!", Snackbar.LENGTH_INDEFINITE)
                                .setAction("RETRY", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        produceOverlay();
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

    private void produceOverlay(){
        array =  new ArrayList<LatLon>();
        for(int i=1;i<10;i++){
            LatLon ll = new LatLon();
            ll.setWifiName("Helix"+i);
            ll.setLatitude((double)i*i);
            ll.setLongitude((double)i*i);
            array.add(ll);
        }
        for(int i=0;i<array.size();i++) {
            addOverlay(array.get(i).getLatitude(),array.get(i).getLongitude(),array.get(i).getWifiName());
        }
    }

    private void addOverlay(double lat, double lon, String title){
        LatLng latLng = new LatLng(lat, lon);
        mMap.addMarker(new MarkerOptions()
                .position(latLng) //setting position
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.map_marker))
                .draggable(true) //Making the marker draggable
                .title(title)); //Adding a title
        mMap.addMarker(new MarkerOptions().position(latLng).draggable(true));
    }
}