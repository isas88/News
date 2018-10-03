package com.example.isas88.news;

import android.content.AsyncTaskLoader;
import android.content.Context;
import java.util.List;

public class NewsLoader extends AsyncTaskLoader<List<NewsContent>> {

    private String url;

    public NewsLoader(Context context, String url) {
        super(context);
        this.url = url;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public List<NewsContent> loadInBackground() {
        if (url != null){
            List<NewsContent> newsContents = QueryUtils.extractNewsContents(url);
            return newsContents;
        }
        return null;
    }
}
