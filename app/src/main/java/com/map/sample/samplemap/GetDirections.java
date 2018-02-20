package com.map.sample.samplemap;

import android.graphics.Color;
import android.os.AsyncTask;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.PolyUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

class GetDirections extends AsyncTask<Object, String[], String> {

    private final String BASE_URL = "https://maps.googleapis.com/maps/api/directions/json?";
    private String final_url;
    GoogleMap mMap;

    GetDirections(LatLng origin, LatLng destination) {

        final_url = BASE_URL + "origin=" + origin.latitude + "," + origin.longitude + "&destination=" + destination.latitude + "," + destination.longitude + "&key=AIzaSyBphoy0vCpUtrBIH4JkwPJyLIMJln6Ulvg";
        System.out.println(origin.latitude + "," + origin.longitude);
        System.out.println(destination.latitude + "," + destination.longitude);
        System.out.println(final_url);
    }

    public JSONArray getSteps() throws MalformedURLException {

        JSONArray steps = new JSONArray();
        URL url = new URL(final_url);
        InputStream in;
        BufferedReader bufferedReader;
        StringBuffer stringBuffer = new StringBuffer();

        try {
            in = url.openStream();
            bufferedReader = new BufferedReader(new InputStreamReader(in));

            String line;

            while ((line = bufferedReader.readLine()) != null) {
                stringBuffer.append(line);
            }

            JSONObject jsonObject = new JSONObject(stringBuffer.toString());

            steps = jsonObject.getJSONArray("routes").getJSONObject(0).getJSONArray("legs").getJSONObject(0).getJSONArray("steps");
            System.out.println(steps);
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }

        return steps;
    }


    @Override
    protected String doInBackground(Object... objects) {
        /*JSONArray steps = new JSONArray();
        Object[] objects1 = new Object[3];*/
        /*JSONObject bounds;
        JSONObject northeast, southwest;
        LatLng latLng;
        LatLng latLng1;
        LatLngBounds latLngBounds = null;*/
        InputStream in;
        BufferedReader bufferedReader;
        StringBuilder stringBuffer = new StringBuilder();

        try {
            URL url = new URL(final_url);
            in = url.openStream();
            bufferedReader = new BufferedReader(new InputStreamReader(in));

            String line;

            while ((line = bufferedReader.readLine()) != null) {
                stringBuffer.append(line);
            }

            /*JSONObject jsonObject = new JSONObject(stringBuffer.toString());
            bounds = jsonObject.getJSONArray("routes").getJSONObject(0).getJSONObject("bounds");
            northeast = bounds.getJSONObject("northeast");
            southwest = bounds.getJSONObject("southwest");
            latLng = new LatLng(northeast.getDouble("lat"), northeast.getDouble("lng"));
            latLng1 = new LatLng(southwest.getDouble("lat"), southwest.getDouble("lng"));
            latLngBounds = new LatLngBounds(latLng1,latLng);
            steps = jsonObject.getJSONArray("routes").getJSONObject(0).getJSONArray("legs").getJSONObject(0).getJSONArray("steps");
            System.out.println(steps);*/
        } catch (IOException e) {
            e.printStackTrace();
        }

        mMap = (GoogleMap) objects[0];

        /*objects1[0] = mMap;
        objects1[1] = steps;
        objects1[2] = latLngBounds;*/

        return stringBuffer.toString();
    }

    @Override
    protected void onPostExecute(String string) {
        String[] strings;

        DataParser dataParser = new DataParser();
        strings = dataParser.parseDirections(string);
        displayDirections(strings);
        /*JSONArray steps = (JSONArray) objects[1];
        GoogleMap mMap = (GoogleMap) objects[0];
        LatLngBounds latLngBounds = (LatLngBounds) objects[2];
        String[] polylines = new String[steps.length()];

        mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds,0));

        for(int i = 0; i< steps.length(); i++) {
            try {
                polylines[i] = steps.getJSONObject(0).getJSONObject("polyline").getString("points");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            mMap.addPolyline(new PolylineOptions()
                    .color(Color.BLUE)
                    .width(10)
                    .addAll(PolyUtil.decode(polylines[i]))
            );
        }*/
    }

    public void displayDirections(String[] directionsList)
    {

        int count = directionsList.length;
        for(int i = 0;i<count;i++)
        {
            PolylineOptions options = new PolylineOptions();
            options.color(Color.BLUE);
            options.width(10);
            options.addAll(PolyUtil.decode(directionsList[i]));

            mMap.addPolyline(options);
        }
    }
}
