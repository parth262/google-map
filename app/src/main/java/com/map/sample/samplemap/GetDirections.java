package com.map.sample.samplemap;

import android.graphics.Color;
import android.os.AsyncTask;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Cap;
import com.google.android.gms.maps.model.CustomCap;
import com.google.android.gms.maps.model.JointType;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.PolyUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

class GetDirections extends AsyncTask<Object, String[], String> {

    private final String BASE_URL = "https://maps.googleapis.com/maps/api/directions/json?";
    private String final_url;
    GoogleMap mMap;

    GetDirections(LatLng origin, LatLng destination) {

        final_url = BASE_URL + "origin=" + origin.latitude + "," + origin.longitude + "&destination=" + destination.latitude + "," + destination.longitude + "&alternatives=true&region=in&key=AIzaSyBphoy0vCpUtrBIH4JkwPJyLIMJln6Ulvg";
        System.out.println(origin.latitude + "," + origin.longitude);
        System.out.println(destination.latitude + "," + destination.longitude);
        System.out.println(final_url);
    }


    @Override
    protected String doInBackground(Object... objects) {
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
        } catch (IOException e) {
            e.printStackTrace();
        }

        mMap = (GoogleMap) objects[0];

        return stringBuffer.toString();
    }

    @Override
    protected void onPostExecute(String string) {
        String[] strings;

        DataParser dataParser = new DataParser();
        JSONArray jsonArray;
        JSONObject jsonObject;
        JSONArray routes;
        int[] colors = {Color.BLUE,Color.CYAN,Color.GRAY};

        try {
            jsonObject = new JSONObject(string);
            routes = jsonObject.getJSONArray("routes");
            for(int i=0;i<1;i++) {
                jsonArray = routes.getJSONObject(i).getJSONArray("legs").getJSONObject(0).getJSONArray("steps");
                strings = dataParser.getPaths(jsonArray);
                displayDirections(strings,colors[i]);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void displayDirections(String[] directionsList, int color)
    {
        PolylineOptions options = new PolylineOptions();
        for (String aDirectionsList : directionsList) {
            options.color(color);
            options.width(8);
            options.jointType(JointType.ROUND);
            options.addAll(PolyUtil.decode(aDirectionsList));
        }

        mMap.addPolyline(options).setEndCap(new CustomCap(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)));
    }
}
