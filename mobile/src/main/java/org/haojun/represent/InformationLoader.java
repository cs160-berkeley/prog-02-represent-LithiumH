package org.haojun.represent;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

/** This class loads the information from server and parses them
 * Created by Haojun on 2/29/16.
 */
public class InformationLoader{

    static List<Map<String, String>> getCandidates(Context context, String zip) {
        List<Map<String, String>> result = new ArrayList<>();
        String url = "https://congress.api.sunlightfoundation.com/legislators/locate";
        try {
            JSONObject json = getFromURL(url, String.format("zip=%s", zip),
                    String.format("apikey=%s", getKey(context, "sunlight")));
            JSONArray candidates = json.getJSONArray("results");
            for (int i = 0; i < candidates.length(); i ++) {
                JSONObject candidate = candidates.getJSONObject(i);
                Map<String, String> candidateInfo = new HashMap<>();
                candidateInfo.put("name", String.format("%s %s", candidate.getString("first_name"),
                        candidate.getString("last_name")));
                candidateInfo.put("email", candidate.getString("oc_email"));
                candidateInfo.put("website", candidate.getString("website"));
                candidateInfo.put("id", candidate.getString("bioguide_id"));
                candidateInfo.put("party", "D".equals(candidate.getString("party")) ? "Democrat"
                        : "R".equals(candidate.getString("party")) ? "Republican" : "Independent");
                candidateInfo.put("twitter_id", candidate.getString("twitter_id"));
                candidateInfo.put("term", String.format("%s to %s",candidate.getString("term_start"),
                        candidate.getString("term_end")));
                Bitmap bitmap;
                URL profile = new URL(String.format("https://theunitedstates.io/images/congress/225x275/%s.jpg",
                        candidate.getString("bioguide_id")));
                bitmap = BitmapFactory.decodeStream(profile.openStream());
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                if (bitmap != null) {
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 80, stream);
                    byte[] arr = stream.toByteArray();
                    candidateInfo.put("pic", Arrays.toString(arr));
                }
                result.add(candidateInfo);
            }
        } catch (IOException|JSONException e) {
            e.printStackTrace();
        }
        result.add(getVoteData(context, zip));
        _results = result;
        return result;
    }

    static Map<String, String> getDetailedCandidate(Context context, String name) {
        Map<String, String> result = new HashMap<>();
        for (Map<String, String> candidate : _results) {
            if (name.equals(candidate.get("name"))) {
                result = candidate;
                String bioguideID = candidate.get("id");
                try {
                    String apikey = String.format("apikey=%s", getKey(context, "sunlight"));
                    JSONObject json = getFromURL("https://congress.api.sunlightfoundation.com/committees",
                            String.format("member_ids=%s", bioguideID),
                            apikey);
                    JSONArray committees = json.getJSONArray("results");
                    StringBuilder builder = new StringBuilder();
                    for (int i = 0; i < committees.length(); i ++) {
                        JSONObject committee = committees.getJSONObject(i);
                        builder.append(String.format("%d. %s\n", i + 1, committee.getString("name")));
                    }
                    result.put("committee", builder.toString());

                    json = getFromURL("https://congress.api.sunlightfoundation.com/bills",
                            String.format("sponsor_id=%s", bioguideID),
                            apikey);
                    JSONArray bills = json.getJSONArray("results");
                    builder = new StringBuilder();
                    for (int i = 0; i < bills.length() && i < 5; i ++ ) {
                        JSONObject bill = bills.getJSONObject(i);
                        builder.append(String.format("%d. (%s) %s\n", i+1, bill.getString("introduced_on"),
                                bill.isNull("short_title") ? bill.getString("official_title")
                                        : bill.getString("short_title")));
                    }
                    result.put("bills", builder.toString());
                } catch (JSONException e) {
                    Log.e("info", e.getMessage());
                }
                Bitmap bitmap = null;
                try {
                    URL profile = new URL(String.format("https://theunitedstates.io/images/congress/450x550/%s.jpg",
                            result.get("id")));
                    bitmap = BitmapFactory.decodeStream(profile.openStream());
                } catch (IOException e) {
                    Log.e("info", e.getMessage());
                }
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                if (bitmap != null) {
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                    byte[] arr = stream.toByteArray();
                    result.put("large-pic", Arrays.toString(arr));
                }
            }
        }
        return result;
    }

    static Map<String, String> getVoteData(Context context, String zip) {
        Map<String, String> result = new HashMap<>();
        String county = "";
        String locality = "";
        try {
            JSONObject json = getFromURL("https://maps.googleapis.com/maps/api/geocode/json",
                    String.format("address=%s", zip),
                    String.format("key=%s", getKey(context, "google")));
            JSONArray components = json.getJSONArray("results")
                    .getJSONObject(0)
                    .getJSONArray("address_components");
            String altCounty = "";
            for (int i = 0; i < components.length(); i++) {
                List<String> types = new ArrayList<>();
                JSONArray typesJSON = components.getJSONObject(i).getJSONArray("types");
                for (int j = 0; j < typesJSON.length(); j ++) {
                    types.add(typesJSON.getString(j));
                }
                if (types.contains("administrative_area_level_2")) {
                    county = components.getJSONObject(i).getString("short_name");
                    county = county.substring(0, county.length()-7);
                } else if (types.contains("administrative_area_level_1")) {
                    altCounty = components.getJSONObject(i).getString("long_name");
                } else if (types.contains("locality")) {
                    locality = components.getJSONObject(i).getString("short_name");
                }
            }
            if ("".equals(county)) {
                county = altCounty;
            }
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    context.getAssets().open("election-county-2012.json"), "UTF-8"));
            StringBuilder builder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null)
                builder.append(line);
            JSONArray voteData =  new JSONArray(builder.toString());
            for (int i = 0; i < voteData.length(); i ++) {
                JSONObject voteDatum = voteData.getJSONObject(i);
                if (county.equals(voteDatum.getString("county-name"))) {
                    result.put("state", voteDatum.getString("state-postal"));
                    result.put("obama", String.format("%.1f%%", voteDatum.getDouble("obama-percentage")));
                    result.put("romney", String.format("%.1f%%", voteDatum.getDouble("romney-percentage")));
                }
            }
        } catch (JSONException|IOException e) {
            Log.e("Info", e.getMessage());
        }
        result.put("county", county);
        result.put("locality", locality);
        result.put("id", "vote");
        return  result;
    }

    static JSONObject getFromURL(String url, String query, String apikey) {
        Log.d("Info", String.format("loading url %s?%s&%s", url, query, apikey));
        JSONObject json = null;
        try {
            HttpsURLConnection connection =
                    (HttpsURLConnection) new URL(String.format("%s?%s&%s",
                            url, query, apikey)).openConnection();
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    connection.getInputStream(), "UTF-8"));
            StringBuilder builder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }
            json = new JSONObject(builder.toString());
        } catch (IOException|JSONException e) {
            Log.e("Info", e.getMessage());
        }
        return json;
    }

    static String getKey(Context context, String api) {
        String apikey = "";
        try {
            InputStream s = context.getAssets().open("keys.json");
            BufferedReader reader = new BufferedReader(new InputStreamReader(s, "UTF-8"));
            StringBuilder builder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null)
                builder.append(line);
            apikey = new JSONObject(builder.toString()).getString(api);
        } catch (IOException|JSONException e) {
            e.printStackTrace();
        }
        return apikey;
    }

    private static List<Map<String, String>> _results;
}
