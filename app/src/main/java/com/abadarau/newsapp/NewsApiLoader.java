package com.abadarau.newsapp;

import android.content.AsyncTaskLoader;
import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


class NewsApiLoader extends AsyncTaskLoader<List<NewsApiItem>> {

    public static final String API_URL = "https://content.guardianapis.com/search?q=debate&tag=politics/politics&from-date=2014-01-01&api-key=test";

    private boolean apiIsDown = false;

    public NewsApiLoader(Context context) {
        super(context);
    }

    @Override
    public List<NewsApiItem> loadInBackground() {
        ArrayList<NewsApiItem> newsApiItems = new ArrayList<>();
        try {
            JSONObject apiJsonData = getApiJsonData();
            JSONArray jsonArrayItems = apiJsonData.getJSONObject("response").getJSONArray("results");
            for (int i = 0; i < jsonArrayItems.length(); i++) {
                NewsApiItem newsApiItem = new NewsApiItem();
                JSONObject jsonNewsApiItem = jsonArrayItems.getJSONObject(i);
                newsApiItem.setId(jsonNewsApiItem.getString("id"));
                newsApiItem.setApiUrl(jsonNewsApiItem.getString("apiUrl"));
                newsApiItem.setSectionId(jsonNewsApiItem.getString("sectionId"));
                newsApiItem.setSectionName(jsonNewsApiItem.getString("sectionName"));
                newsApiItem.setWebPublicationDate(jsonNewsApiItem.getString("webPublicationDate"));
                newsApiItem.setWebTitle(jsonNewsApiItem.getString("webTitle"));
                newsApiItem.setWebUrl(jsonNewsApiItem.getString("webUrl"));
                newsApiItems.add(newsApiItem);
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return newsApiItems;
    }

    private JSONObject getApiJsonData() throws IOException, JSONException {
        return new JSONObject(getApiData());
    }

    private String getApiData() throws IOException {
        StringBuilder responseString = new StringBuilder();
        URL url = new URL(NewsApiLoader.API_URL);
        HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
        httpURLConnection.setRequestMethod("GET");
        httpURLConnection.setDoOutput(true);
        httpURLConnection.setConnectTimeout(5000);
        httpURLConnection.setReadTimeout(5000);
        httpURLConnection.connect();

        if (httpURLConnection.getResponseCode() == 200) {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                responseString.append(line).append("\n");
            }
            return responseString.toString();
        } else if (apiIsDown) {
            return getDummyData();
        }

        throw new IOException("There was an error with the connecting, got response code: " + httpURLConnection.getResponseCode());
    }

    private String getDummyData() {
        return "{\n" +
                "    \"response\": {\n" +
                "        \"status\": \"ok\",\n" +
                "        \"userTier\": \"free\",\n" +
                "        \"total\": 1,\n" +
                "        \"startIndex\": 1,\n" +
                "        \"pageSize\": 10,\n" +
                "        \"currentPage\": 1,\n" +
                "        \"pages\": 1,\n" +
                "        \"orderBy\": \"newest\",\n" +
                "        \"results\": [\n" +
                "            {\n" +
                "                \"id\": \"politics/blog/2014/feb/17/alex-salmond-speech-first-minister-scottish-independence-eu-currency-live\",\n" +
                "                \"sectionId\": \"politics\",\n" +
                "                \"sectionName\": \"Politics\",\n" +
                "                \"webPublicationDate\": \"2014-02-17T12:05:47Z\",\n" +
                "                \"webTitle\": \"Alex Salmond speech – first minister hits back over Scottish independence – live\",\n" +
                "                \"webUrl\": \"https://www.theguardian.com/politics/blog/2014/feb/17/alex-salmond-speech-first-minister-scottish-independence-eu-currency-live\",\n" +
                "                \"apiUrl\": \"https://content.guardianapis.com/politics/blog/2014/feb/17/alex-salmond-speech-first-minister-scottish-independence-eu-currency-live\"\n" +
                "            }\n" +
                "        ]\n" +
                "    }\n" +
                "}";
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }
}
