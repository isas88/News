package com.example.isas88.news;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class NewsActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<List<NewsContent>> {

    private static final int NEWS_LOADER_ID = 1;
    String url;
    private NewsAdapter adapter;
    ListView newslist;
    ProgressBar spin_wheel;
    TextView dft_text;
    boolean isConnected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);

        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

        spin_wheel = findViewById(R.id.loading_spinner);
        dft_text   = findViewById(R.id.empty);
        url = "https://content.guardianapis.com/search";

        //set adapter in main thread as it cannot be set in loader
        adapter = new NewsAdapter(this, new ArrayList<NewsContent>());
        newslist = findViewById(android.R.id.list);
        newslist.setAdapter(adapter);

        if (isConnected){
            // Get a reference to the LoaderManager, in order to interact with loaders.
            LoaderManager loaderManager = getLoaderManager();

            // Initialize the loader
            loaderManager.initLoader(NEWS_LOADER_ID, null, this);

        }else{
            //if no n/w connection show the no internet text
            spin_wheel.setVisibility(View.GONE);
            newslist.setEmptyView(findViewById(R.id.empty));
            dft_text.setText(getResources().getString(R.string.no_internet));
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public Loader<List<NewsContent>> onCreateLoader(int id, Bundle args) {

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);

        Uri baseUri = Uri.parse(url);

        Uri.Builder uriBuilder = baseUri.buildUpon();

        //Set the no of days where the news has to be displayed based on user preference
        DateFormat dateFormat   = new SimpleDateFormat("yyyy-MM-dd");
        Calendar cal = Calendar.getInstance();
        String currentDate = dateFormat.format(new Date());
        String fromDate;

        String noOfDays = sharedPrefs.getString(
                getString(R.string.settings_days_key),
                getString(R.string.settings_days_default));

        if (noOfDays.equals(getString(R.string.settings_days_today_value).toString())){
            fromDate = currentDate;
        }else{
            cal.add(Calendar.MONTH,1);
            cal.add(Calendar.DATE,(-1) *Integer.parseInt(noOfDays));
            fromDate = cal.get(Calendar.YEAR) + "-"
                    + cal.get(Calendar.MONTH) + "-" + cal.get(Calendar.DAY_OF_MONTH);
        }

        //get search text for news that needs to be searched based on user preference
        String searchText =
                sharedPrefs.getString(getString(R.string.settings_search_key)
                        ,getString(R.string.settings_search_default));

        // Append query parameter and its value
        uriBuilder.appendQueryParameter("from-date", fromDate);
        uriBuilder.appendQueryParameter("to-date", currentDate);
        uriBuilder.appendQueryParameter("q", searchText);
        uriBuilder.appendQueryParameter("api-key", "8023d2a8-34a0-4241-b275-51bb209666c9");
        uriBuilder.appendQueryParameter("show-tags","contributor");

        return new NewsLoader(this, uriBuilder.toString());
    }

    @Override
    public void onLoadFinished(Loader<List<NewsContent>> loader, final List<NewsContent> newsContents) {
        adapter.clear();

        spin_wheel.setVisibility(View.GONE);

        if (newsContents !=null && !newsContents.isEmpty()){
            adapter.addAll(newsContents);
        }else{
            newslist.setEmptyView(findViewById(R.id.empty));
        }

        // open the web url using explicit intent passing the details of the list item clicked
        newslist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Intent webPage_Intent = new Intent(Intent.ACTION_VIEW, Uri.parse(newsContents.get(position).getNews_url()));
                startActivity(webPage_Intent);

            }
        });
    }

    @Override
    public void onLoaderReset(Loader<List<NewsContent>> loader) {
        adapter.clear();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id=item.getItemId();
        if(id==R.id.action_settings){
            Intent settingsMenuIntent = new Intent(this,SettingsActivity.class);
            startActivity(settingsMenuIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}