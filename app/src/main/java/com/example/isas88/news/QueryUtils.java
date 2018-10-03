package com.example.isas88.news;

import android.util.Log;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public final class QueryUtils {

    public static final String LOG_TAG = QueryUtils.class.getSimpleName();
    final static public int urlResponseCode = 200;
    final static public int urlReadTimeout = 10000;
    final static public int urlConnectTimeout = 15000;

    private QueryUtils() {
    }

    // Perform HTTP request to the URL and receive a JSON response back
    public static List<NewsContent> extractNewsContents(String urlInp) {

        URL url = createUrl(urlInp);
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error closing input stream", e);
        }
        return JSONparse(jsonResponse);

    }

    private static URL createUrl(String stringUrl) {

        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Error with creating URL ", e);
        }
        return url;

    }

    //Make an HTTP request to the given URL and return a String as the response.
    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(urlReadTimeout);
            urlConnection.setConnectTimeout(urlConnectTimeout);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            if (urlConnection.getResponseCode() == urlResponseCode ) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the earthquake JSON results.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    //convert JSON response to String
    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    //decode the response string to needed string values to display
    private static List<NewsContent> JSONparse(String response){

        List<NewsContent> newsContents = new ArrayList<>();

        try {

            JSONObject root = new JSONObject(response);
            JSONObject root2 = root.getJSONObject("response");
            JSONArray quakeArray = root2.getJSONArray("results");

            String title;
            String author="";
            String datePublished;
            String section;
            String newsURL;
            String storyDate;

            for (int i=0;i<quakeArray.length();i++){

                JSONObject data = quakeArray.getJSONObject(i);
                title           = data.getString("webTitle");
                storyDate       = data.getString("webPublicationDate").substring(0,10);

                if (storyDate!= null){
                    datePublished = storyDate;
                }else{
                    datePublished = "";
                }

                try{
                    section = data.getString("pillarName");
                }
                catch (JSONException e){
                    Log.e("QueryUtils", "PIllarName not available", e);
                    section = "News";
                }

                newsURL = data.getString("webUrl");

                //get author name if available
                JSONArray refArray = data.getJSONArray("tags");
                if (refArray.length()>0){
                    for (int j=0;j<refArray.length();j++){
                        JSONObject refData = refArray.getJSONObject(j);
                        if (refData.getString("webTitle")!=null){
                            author = refData.getString("webTitle");
                        }
                    }
                }

                newsContents.add(new NewsContent(title,author,datePublished,section,newsURL));
            }

        } catch (JSONException e) {
            Log.e("QueryUtils", "Problem parsing the news content JSON results", e);
        }

        // Return the news contents
        return newsContents;
    }
}

