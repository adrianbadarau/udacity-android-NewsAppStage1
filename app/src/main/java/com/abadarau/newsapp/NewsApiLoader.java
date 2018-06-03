package com.abadarau.newsapp;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.provider.Settings;

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

    public static final int CONNECT_TIMEOUT = 5000;
    public static final String WEB_TITLE = "webTitle";
    public static final String API_URL1 = "apiUrl";
    public static final String SECTION_ID = "sectionId";
    public static final String SECTION_NAME = "sectionName";
    public static final String PUBLICATION_DATE = "webPublicationDate";
    public static final String ID = "id";
    public static final String WEB_URL = "webUrl";
    public static final String TAGS = "tags";


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
                newsApiItem.setId(jsonNewsApiItem.getString(ID));
                newsApiItem.setApiUrl(jsonNewsApiItem.getString(API_URL1));
                newsApiItem.setSectionId(jsonNewsApiItem.getString(SECTION_ID));
                newsApiItem.setSectionName(jsonNewsApiItem.getString(SECTION_NAME));
                newsApiItem.setWebPublicationDate(jsonNewsApiItem.getString(PUBLICATION_DATE));
                newsApiItem.setWebTitle(jsonNewsApiItem.getString(WEB_TITLE));
                newsApiItem.setWebUrl(jsonNewsApiItem.getString(WEB_URL));
                JSONArray tags = jsonNewsApiItem.getJSONArray(TAGS);
                if(tags != null && tags.length() > 0){
                    newsApiItem.setAuthorName(tags.getJSONObject(0).getString(WEB_TITLE));
                }
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
        SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        Uri.Builder builder = new Uri.Builder().scheme(getContext().getString(R.string.api_scheme)).authority(getContext().getString(R.string.api_authority)).appendPath(getContext().getString(R.string.api_search_path));
        // Now we begin to add the query params needed
        builder.appendQueryParameter(getContext().getString(R.string.api_key), getContext().getString(R.string.api_key_value));
        builder.appendQueryParameter(getContext().getString(R.string.api_search_from_date_key),getContext().getString(R.string.api_search_from_date_value));
        builder.appendQueryParameter(getContext().getString(R.string.api_show_tags_key),getContext().getString(R.string.api_show_tags_value));
        builder.appendQueryParameter(getContext().getString(R.string.api_format_key),getContext().getString(R.string.api_format_value));
        builder.appendQueryParameter(getContext().getString(R.string.api_show_fields_key),getContext().getString(R.string.api_show_fields_value));
        builder.appendQueryParameter(getContext().getString(R.string.api_search_query_key), defaultSharedPreferences.getString(SettingsActivity.QUERY_KEY, getContext().getString(R.string.pref_default_search_query)));
        builder.appendQueryParameter(getContext().getString(R.string.api_search_tag_key), defaultSharedPreferences.getString(SettingsActivity.TAG_KEY, getContext().getString(R.string.pref_default_title_tag_list)));
        StringBuilder responseString = new StringBuilder();
        URL url = new URL(builder.toString());
        HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
        httpURLConnection.setRequestMethod("GET");
        httpURLConnection.setConnectTimeout(CONNECT_TIMEOUT);
        httpURLConnection.setReadTimeout(CONNECT_TIMEOUT);
        httpURLConnection.connect();

        if (httpURLConnection.getResponseCode() == 200) {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                responseString.append(line).append("\n");
            }

            return responseString.toString();
        }

        throw new IOException(getContext().getString(R.string.api_error_msg) + httpURLConnection.getResponseCode());
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }
}
