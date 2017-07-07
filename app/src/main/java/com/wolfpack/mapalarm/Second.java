package com.wolfpack.mapalarm;

import android.Manifest;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.test.mock.MockPackageManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Second extends Activity
{

    private static final String TAG ="Second" ;
    Button btnShowLocation;
    TextView present_city;
    TextView trave;
    TextView rem;
    TextView tot;
    EditText dest_city;
    EditText start_city;
    EditText dist;

    private static final int REQUEST_CODE_PERMISSION = 2;
    String mPermission = Manifest.permission.ACCESS_FINE_LOCATION;

    // GPSTracker class
    GPSTracker gps;
    private ProgressDialog pDialog;
    // URL to get contacts JSON
    private static String url ;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        try
        {
            if (ActivityCompat.checkSelfPermission(this, mPermission)
                    != MockPackageManager.PERMISSION_GRANTED)
            {
                ActivityCompat.requestPermissions(this, new String[]{mPermission},
                        REQUEST_CODE_PERMISSION);

                // If any permission above not allowed by user, this condition will
                //execute every time, else your else part will work
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        btnShowLocation = (Button) findViewById(R.id.button);
        present_city = (TextView) findViewById(R.id.city);
        trave = (TextView) findViewById(R.id.kmtrav);
        rem = (TextView) findViewById(R.id.kmrem);
        tot = (TextView) findViewById(R.id.tot);
        dest_city = (EditText) findViewById(R.id.dest);
        start_city = (EditText) findViewById(R.id.start);
        dist = (EditText) findViewById(R.id.dist);

        //startService(new Intent(getBaseContext(), FService.class));
        // show location button click event
        btnShowLocation.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View arg0)
            {
                // create class object
                //NotificationManager notif=(NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
                NotificationManager notif =(NotificationManager) Second.this.getApplicationContext().getSystemService(Second.this.getApplicationContext().NOTIFICATION_SERVICE);

                gps = new GPSTracker(Second.this,dest_city.getText().toString(),notif,Double.parseDouble(dist.getText().toString()));

                // check if GPS enabled
                double lat=gps.getLatitude();
                double lon=gps.getLongitude();
                url=String.format("https://maps.googleapis.com/maps/api/geocode/json?latlng=%s,%s&key=AIzaSyBtPTf41ohzWJwBEJCyz69PNRDR9hhZOjE",lat,lon);

                new GetLoc().execute();
            }
        });
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
                    Log.e(TAG, "Json parsing error: " + e.getMessage());
                    runOnUiThread(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            Toast.makeText(getApplicationContext(),
                                    "Json parsing error: " + e.getMessage(),
                                    Toast.LENGTH_LONG)
                                    .show();
                        }
                    });

                }
            }
            else
            {
                Log.e(TAG, "Couldn't get json from server.");
                runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        Toast.makeText(getApplicationContext(),
                                "Couldn't get json from server. Check LogCat for possible errors!",
                                Toast.LENGTH_LONG)
                                .show();
                    }
                });

            }
            return km;
        }

        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(Second.this);
            pDialog.setMessage("Getting distance");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected void onPostExecute(String s)
        {
            super.onPostExecute(s);
            if (pDialog.isShowing())
                pDialog.dismiss();
            rem.setText("KM remaining - "+s);
            startService(new Intent(Second.this,GPSTracker.class));
        }
    }


    private class GetTravDist extends AsyncTask<String, String, String>
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
                    Log.e(TAG, "Json parsing error: " + e.getMessage());
                    runOnUiThread(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            Toast.makeText(getApplicationContext(),
                                    "Json parsing error: " + e.getMessage(),
                                    Toast.LENGTH_LONG)
                                    .show();
                        }
                    });

                }
            }
            else
            {
                Log.e(TAG, "Couldn't get json from server.");
                runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        Toast.makeText(getApplicationContext(),
                                "Couldn't get json from server. Check LogCat for possible errors!",
                                Toast.LENGTH_LONG)
                                .show();
                    }
                });

            }
            return km;
        }

        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(Second.this);
            pDialog.setMessage("Getting distance");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected void onPostExecute(String s)
        {
            super.onPostExecute(s);
            if (pDialog.isShowing())
                pDialog.dismiss();
            trave.setText("KM travelled"+" - "+s);
            new GetRemDist().execute(present_city.getText().toString(),dest_city.getText().toString());
        }
    }


    private class GetTotDist extends AsyncTask<String, String, String>
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
                    Log.e(TAG, "Json parsing error: " + e.getMessage());
                    runOnUiThread(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            Toast.makeText(getApplicationContext(),
                                    "Json parsing error: " + e.getMessage(),
                                    Toast.LENGTH_LONG)
                                    .show();
                        }
                    });

                }
            }
            else
            {
                Log.e(TAG, "Couldn't get json from server.");
                runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        Toast.makeText(getApplicationContext(),
                                "Couldn't get json from server. Check LogCat for possible errors!",
                                Toast.LENGTH_LONG)
                                .show();
                    }
                });

            }
            return km;
        }

        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(Second.this);
            pDialog.setMessage("Getting distance");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected void onPostExecute(String s)
        {
            super.onPostExecute(s);
            if (pDialog.isShowing())
                pDialog.dismiss();
            tot.setText("Total KM - "+s);
            new GetTravDist().execute(start_city.getText().toString(),present_city.getText().toString());
        }
    }



    //############################http thing############################
    private class GetLoc extends AsyncTask<Void, String, String>
    {

        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(Second.this);
            pDialog.setMessage("Getting location details");
            pDialog.setCancelable(false);
            pDialog.show();
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
                    Log.e(TAG, "Json parsing error: " + e.getMessage());
                    runOnUiThread(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            Toast.makeText(getApplicationContext(),
                                    "Json parsing error: " + e.getMessage(),
                                    Toast.LENGTH_LONG)
                                    .show();
                        }
                    });

                }
            }
            else
            {
                Log.e(TAG, "Couldn't get json from server.");
                runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        Toast.makeText(getApplicationContext(),
                                "Couldn't get json from server. Check LogCat for possible errors!",
                                Toast.LENGTH_LONG)
                                .show();
                    }
                });

            }
            return tex;
        }

        @Override
        protected void onPostExecute(String result)
        {
            super.onPostExecute(result);
            // Dismiss the progress dialog
            if (pDialog.isShowing())
                pDialog.dismiss();
            present_city.setText(result);
            new GetTotDist().execute(start_city.getText().toString(),dest_city.getText().toString());
        }

    }

}
