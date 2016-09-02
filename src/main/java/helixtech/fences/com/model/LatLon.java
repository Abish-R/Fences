package helixtech.fences.com.model;

/**
 * Created by helixtech-android on 9/7/16.
 */
public class LatLon {
    double latitude,longitude;
    String wifiName;
    public LatLon(){}

    public double getLatitude() {
        return latitude;
    }
    public double getLongitude() {
        return longitude;
    }
    public String getWifiName() {
        return wifiName;
    }

    public void setLatitude(double lat) {
        this.latitude = lat;
    }
    public void setLongitude(double lon) {
        this.longitude = lon;
    }
    public void setWifiName(String wifi) {
        this.wifiName = wifi;
    }
}
