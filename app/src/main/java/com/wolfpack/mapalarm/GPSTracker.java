package com.wolfpack.mapalarm;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.RingtoneManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class GPSTracker extends Service implements LocationListener {
    String present_city;
    String trave;
    String rem="";
    String tot;
    String dest_city;
    String start_city;
    String url;
    double dis;
    private final Context mContext;
    NotificationManager notif;
    // flag for GPS status
    boolean isGPSEnabled = false;

    // flag for network status
    boolean isNetworkEnabled = false;



    // flag for GPS status
    boolean canGetLocation = false;

    Location location; // location
    double latitude; // latitude
    double longitude; // longitude
    // The minimum distance to change Updates in meters
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 1; // 1 meter

    // The minimum time between updates in milliseconds
    private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1; // 1 minute
    //Second s = new Second();
     // Declaring a Location Manager
    protected LocationManager locationManager;

    public GPSTracker(Context context,String dest_city,NotificationManager notif,double dis) {
        this.mContext = context;
        this.dest_city = dest_city;
        this.notif = notif;
        this.dis=dis;
        getLocation();
    }

    public Location getLocation() {
        try {
            locationManager = (LocationManager) mContext.getSystemService(LOCATION_SERVICE);

            // getting GPS status
            isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

            // getting network status
            isNetworkEnabled = locationManager
                    .isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (!isGPSEnabled && !isNetworkEnabled) {
                // no network provider is enabled
            } else {
                this.canGetLocation = true;
                // First get location from Network Provider
                if (isNetworkEnabled) {
                    locationManager.requestLocationUpdates(
                            LocationManager.NETWORK_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES, this);

                    Log.d("Network", "Network");
                    if (locationManager != null) {
                        location = locationManager
                                .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

                        if (location != null) {
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                        }
                    }
                }

                // if GPS Enabled get lat/long using GPS Services
                if (isGPSEnabled) {
                    if (location == null) {
                        locationManager.requestLocationUpdates(
                                LocationManager.GPS_PROVIDER,
                                MIN_TIME_BW_UPDATES,
                                MIN_DISTANCE_CHANGE_FOR_UPDATES, this);

                        Log.d("GPS Enabled", "GPS Enabled");
                        if (locationManager != null) {
                            location = locationManager
                                    .getLastKnownLocation(LocationManager.GPS_PROVIDER);

                            if (location != null) {
                                latitude = location.getLatitude();
                                longitude = location.getLongitude();
                            }
                        }
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return location;
    }

    /**
     * Stop using GPS listener
     * Calling this function will stop using GPS in your app
     * */

    public void stopUsingGPS(){
        if(locationManager != null){
            locationManager.removeUpdates(GPSTracker.this);
        }
    }

    /**
     * Function to get latitude
     * */

    public double getLatitude(){
        if(location != null){
            latitude = location.getLatitude();
        }

        // return latitude
        return latitude;
    }

    /**
     * Function to get longitude
     * */

    public double getLongitude(){
        if(location != null){
            longitude = location.getLongitude();
        }

        // return longitude
        return longitude;
    }

    /**
     * Function to check GPS/wifi enabled
     * @return boolean
     * */

    public boolean canGetLocation() {
        return this.canGetLocation;
    }

    /**
     * Function to show settings alert dialog
     * On pressing Settings button will lauch Settings Options
     * */

    public void showSettingsAlert(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);

        // Setting Dialog Title
        alertDialog.setTitle("GPS is settings");

        // Setting Dialog Message
        alertDialog.setMessage("GPS is not enabled. Do you want to go to settings menu?");

        // On pressing Settings button
        alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                mContext.startActivity(intent);
            }
        });

        // on pressing cancel button
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        // Showing Alert Message
        alertDialog.show();
    }

    @Override
    public void onLocationChanged(Location location) {
        double latitude = getLatitude();
        double longitude = getLongitude();
        //url="https://maps.googleapis.com/maps/api/geocode/json?latlng=28.4310026,77.0137029&key=AIzaSyBtPTf41ohzWJwBEJCyz69PNRDR9hhZOjE";
        url = String.format("https://maps.googleapis.com/maps/api/geocode/json?latlng=%s,%s&key=AIzaSyB-SDVj4tkh00ueYYtNHfBpvkpEFX3TMJ0",Double.toString(latitude),Double.toString(longitude));
        //https://maps.googleapis.com/maps/api/directions/json?origin=Jalandhar&destination=Pilani&key=AIzaSyBtPTf41ohzWJwBEJCyz69PNRDR9hhZ
        Log.e("change","change");
        new GetLoc().execute();
        if(!(rem.equals("")))
        {
            if(Double.parseDouble(rem.substring(0,rem.length()-3)) < this.dis)
            {
                NotificationManager mManager=notif;
                //mManager = (NotificationManager) this.getApplicationContext().getSystemService(this.getApplicationContext().NOTIFICATION_SERVICE);
                Intent intent1 = new Intent(mContext,Second.class);

                PendingIntent pendingNotificationIntent = PendingIntent.getActivity(mContext,0, intent1,PendingIntent.FLAG_UPDATE_CURRENT);

                Notification notification = new Notification.Builder(mContext)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle("You are about to reach your destination")
                        .setContentText("Reached"+present_city+"\n"+rem+" km remaining to your destination")
                        .setContentIntent(pendingNotificationIntent).getNotification();;
                intent1.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP| Intent.FLAG_ACTIVITY_CLEAR_TOP);
                notification.defaults |= Notification.DEFAULT_VIBRATE;
                notification.sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
                mManager.notify(0, notification);
                //stopSelf();
            }
        }
    }

    @Override
    public void onProviderDisabled(String provider) {
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    private class GetRemDist extends AsyncTask<String, String, String>
    {

        @Override
        protected String doInBackground(String... params)
        {
            String sloc=params[0];
            String dloc=params[1];
            HttpHandler sh = new HttpHandler();
            String u = String.format("https://maps.googleapis.com/maps/api/directions/json?origin=%s&destination=%s&key=AIzaSyBtPTf41ohzWJwBEJCyz69PNRDR9hhZOjE",sloc,dloc);
            String jsonStr = sh.makeServiceCall(u);
            String km = "";
            //Log.e(TAG, "Response from url: " + jsonStr);
            if (jsonStr != null)
            {
                try
                {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    JSONArray results = jsonObj.getJSONArray("routes");
                    JSONObject result = results.getJSONObject(0);
                    JSONArray dists = result.getJSONArray("legs");
                    JSONObject c = dists.getJSONObject(0);
                    JSONObject d = c.getJSONObject("distance");
                    km = d.getString("text");
                }
                catch (final JSONException e)
                {
                    Log.e("errr", "Json parsing error: " + e.getMessage());
                }
            }
            else
            {
                Log.e("errr", "Couldn't get json from server.");
            }
            return km;
        }

        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
            // Showing progress dialog
        }

        @Override
        protected void onPostExecute(String s)
        {
            super.onPostExecute(s);
            rem=s;

            /*NotificationManager mManager=notif;
            //mManager = (NotificationManager) this.getApplicationContext().getSystemService(this.getApplicationContext().NOTIFICATION_SERVICE);
            Intent intent1 = new Intent(mContext,Second.class);

            PendingIntent pendingNotificationIntent = PendingIntent.getActivity(mContext,0, intent1,PendingIntent.FLAG_UPDATE_CURRENT);

            Notification notification = new Notification.Builder(mContext)
                    .setContentTitle("Title").setContentText("Text")
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle("You are about to reach your destination")
                    .setContentText(s)
                    .setContentIntent(pendingNotificationIntent).getNotification();;
            intent1.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP| Intent.FLAG_ACTIVITY_CLEAR_TOP);


            //notification.flags |= Notification.FLAG_AUTO_CANCEL;
            notification.defaults |= Notification.DEFAULT_VIBRATE;
            notification.sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
            mManager.notify(0, notification);*/
        }
    }

    //############################http thing############################
    private class GetLoc extends AsyncTask<Void, String, String>
    {

        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... arg0)
        {
            HttpHandler sh = new HttpHandler();
            // Making a request to url and getting response
            String jsonStr = sh.makeServiceCall(url);
            //Log.e(TAG, "Response from url: " + jsonStr);
            String tex = "";
            if (jsonStr != null)
            {
                try
                {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    // Getting JSON Array node
                    JSONArray results = jsonObj.getJSONArray("results");
                    JSONObject result = results.getJSONObject(0);
                    JSONArray addr = result.getJSONArray("address_components");
                    for (int i = 0; i < addr.length(); i++)
                    {
                        //Log.e(TAG, "till address: " );
                        JSONObject c = addr.getJSONObject(i);
                        JSONArray d = c.getJSONArray("types");
                        for (int j = 0; j < d.length(); j++)
                        {
                            if(d.getString(j).equals("administrative_area_level_2"))
                            {
                                tex+=c.getString("long_name");
                            }
                        }
                    }

                }
                catch (final JSONException e)
                {
                    Log.e("errr", "Json parsing error: " + e.getMessage());
                }
            }
            else
            {
                Log.e("errr", "Couldn't get json from server.");
            }
            return tex;
        }

        @Override
        protected void onPostExecute(String result)
        {
            super.onPostExecute(result);
            // Dismiss the progress dialo
            present_city=result;
            new GetRemDist().execute(result,dest_city);

           /* NotificationManager mManager=notif;
            //mManager = (NotificationManager) this.getApplicationContext().getSystemService(this.getApplicationContext().NOTIFICATION_SERVICE);
            Intent intent1 = new Intent(mContext,Second.class);

            PendingIntent pendingNotificationIntent = PendingIntent.getActivity(mContext,0, intent1,PendingIntent.FLAG_UPDATE_CURRENT);

            Notification notification = new Notification.Builder(mContext)
                    .setContentTitle("Title").setContentText("Text")
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle("You are about to reach your destination")
                    .setContentText(present_city)
                    .setContentIntent(pendingNotificationIntent).getNotification();;
            intent1.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP| Intent.FLAG_ACTIVITY_CLEAR_TOP);


            //notification.flags |= Notification.FLAG_AUTO_CANCEL;
            notification.defaults |= Notification.DEFAULT_VIBRATE;
            notification.sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
            mManager.notify(0, notification);*/
        }

    }
}