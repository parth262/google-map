package com.map.sample.samplemap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

class DataParser {

    /*public String[] parseDirections(String jsonData)
    {
        JSONArray jsonArray = null;
        JSONObject jsonObject;
        JSONArray routes;

        try {
            jsonObject = new JSONObject(jsonData);
            routes = jsonObject.getJSONArray("routes");
            for(int i=0;i<routes.length();i++) {
                jsonArray = routes.getJSONObject(i).getJSONArray("legs").getJSONObject(0).getJSONArray("steps");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return getPaths(jsonArray);
    }*/

    String[] getPaths(JSONArray googleStepsJson)
    {
        int count = googleStepsJson.length();
        String[] polylines = new String[count];

        for(int i = 0;i<count;i++)
        {
            try {
                polylines[i] = getPath(googleStepsJson.getJSONObject(i));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return polylines;
    }

    private String getPath(JSONObject googlePathJson)
    {
        String polyline = "";
        try {
            polyline = googlePathJson.getJSONObject("polyline").getString("points");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return polyline;
    }
}
