package helixtech.fences.com.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.List;

import helixtech.fences.com.supporters.GPSTracker;
import helixtech.fences.com.R;
import helixtech.fences.com.SingleAdvPage;
import helixtech.fences.com.Webview;
import helixtech.fences.com.model.Post;

/**
 * Created by helixtech-android on 29/6/16.
 */
public class CustomAdapter extends RecyclerView.Adapter{
    private List<Post> items;
    Activity context;
    GPSTracker gps;
    Double userLat,userLong;

    public CustomAdapter(Activity con, List<Post> itemslist) {
        context=con;
        this.items = itemslist;
        gps = new GPSTracker(context);
    }

    @Override
    public int getItemViewType(int position) {
            return items.get(position).getLikeStatus();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.row_adv_feed, parent, false));

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            try{
            ((MyViewHolder)holder).bindViewHolder(position);
            }catch (Exception e){
                Log.e("Custom_Adapter",e.toString());
            }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder{
        public TextView adv_by,adv_title,adv_desc,adv_time,adv_route,adv_app,adv_webpage;
        //public RelativeLayout rl;
        public ImageView adv_img;
        public View mCardView;

        public MyViewHolder(View view) {
            super(view);
            //text = (TextView) view.findViewById(R.id.text);
            adv_by = (TextView) view.findViewById(R.id.adv_by);
            adv_title = (TextView) view.findViewById(R.id.adv_title);
            adv_desc = (TextView) view.findViewById(R.id.adv_desc);
            adv_time = (TextView) view.findViewById(R.id.adv_time);

            adv_route = (TextView) view.findViewById(R.id.adv_route);
            adv_app = (TextView) view.findViewById(R.id.adv_app);
            adv_webpage = (TextView) view.findViewById(R.id.adv_webpage);

            //rl = (RelativeLayout) view.findViewById(R.id.rl);
            adv_img=(ImageView) view.findViewById(R.id.adv_img);
            //text5 = (TextView) view.findViewById(R.id.text5);
            mCardView = (CardView) view.findViewById(R.id.card);

        }

        public void bindViewHolder(final int position) {
            Picasso.with(context).load(items.get(position).getUrl())
                    .placeholder(R.drawable.signup_icon_5)
                    .error(R.drawable.exclamation)
                    .into(adv_img);
            adv_by.setText(items.get(position).getKind());
            adv_title.setText(items.get(position).getTitle());
            adv_desc.setText(items.get(position).getDesc());
            adv_time.setText(items.get(position).getPostedTime());

            adv_route.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String lat = items.get(position).getLatitude();
                    String lon = items.get(position).getLongitude();
                    if(againAskingGPS()==0)
                        Toast.makeText(context, "Location not detected. Try again", Toast.LENGTH_SHORT).show();
                    else{
                        Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                                Uri.parse("http://maps.google.com/maps?saddr="+userLat+","+userLong+"&daddr="+lat+","+lon));
                        context.startActivity(intent);
                    }
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
            });
            adv_app.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String appPackage = items.get(position).getAppPackage();
                    String appGLink = items.get(position).getAppGoogleLink();
                    //final boolean installed = appInstalledOrNot(appPackage);
                    if(appInstalledOrNot(appPackage)) {
                        Intent LaunchIntent = context.getPackageManager().getLaunchIntentForPackage(appPackage);
                        context.startActivity(LaunchIntent);
                    } else {
                        try {
                            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id="+appPackage)));
                        } catch (android.content.ActivityNotFoundException anfe) {
                            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(appGLink)));
                        }
                    }

                }

                private boolean appInstalledOrNot(String uri) {
                    PackageManager pm = context.getPackageManager();
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
            });
            adv_webpage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent callWebpage = new Intent(context, Webview.class);
                    callWebpage.putExtra("webpage",items.get(position).getWebpageLink());
                    context.startActivity(callWebpage);
                }
            });
            //holder.img.setImageResource(R.drawable.ic_menu_camera);
            mCardView.setTag(position);
            adv_img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    loadSinglePage(items.get(position).getPostId());
                }
            });
        }

        private void loadSinglePage(int postId){
            Toast.makeText(context,""+postId,Toast.LENGTH_LONG).show();
            Intent intent = new Intent(context,SingleAdvPage.class);
            intent.putExtra("id", postId);
            context.startActivity(intent);
        }
    }
}