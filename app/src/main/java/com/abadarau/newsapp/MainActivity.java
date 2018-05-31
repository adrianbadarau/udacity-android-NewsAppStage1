package com.abadarau.newsapp;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<NewsApiItem>> {
    public static final int LOADER_ID = 1;
    private NewsItemAdapter newsItemAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences sharedPreferences = getSharedPreferences(SettingsActivity.QUERY_KEY, MODE_PRIVATE);
        sharedPreferences.getAll();
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.app_toolbar);
        setSupportActionBar(toolbar);
        ConnectivityManager cm = (ConnectivityManager) this.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        if(isConnected){
            LoaderManager loaderManager = getLoaderManager();
            loaderManager.initLoader(LOADER_ID, null, this);
        }
        ListView newsList = findViewById(R.id.news_items_lv);
        newsItemAdapter = new NewsItemAdapter(this, R.layout.activity_news_item_view, new ArrayList<NewsApiItem>());
        newsList.setAdapter(newsItemAdapter);
        newsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                NewsItemAdapter adapter = ((NewsItemAdapter) parent.getAdapter());
                NewsApiItem item = adapter.getItem(position);
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(item.getWebUrl()));
                startActivity(intent);
            }
        });

        TextView noItems = findViewById(R.id.items_empty_tv);
        newsList.setEmptyView(noItems);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.settings_menu_item:
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
            break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<List<NewsApiItem>> onCreateLoader(int id, Bundle args) {
        return new NewsApiLoader(MainActivity.this);
    }

    @Override
    public void onLoadFinished(Loader<List<NewsApiItem>> loader, List<NewsApiItem> data) {
        newsItemAdapter.setItems(data);
    }

    @Override
    public void onLoaderReset(Loader<List<NewsApiItem>> loader) {
        newsItemAdapter.setItems(new ArrayList<NewsApiItem>());
    }

}
