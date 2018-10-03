package com.example.isas88.news;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class NewsAdapter extends ArrayAdapter<NewsContent>{

    public NewsAdapter(Activity context, ArrayList<NewsContent> news) {
        super(context, 0, news);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        //inflate the custom list view layout that has the layout for news
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.news_layout, parent, false);
        }

        //get the current list items position
        NewsContent currentContent = getItem(position);

        //load the title content author publish date section and url
        TextView title = listItemView.findViewById(R.id.story_title);
        title.setText(currentContent.getStory_title());

        TextView author = listItemView.findViewById(R.id.author_name);
        author.setText(currentContent.getAuthor_name());

        TextView datePublished = listItemView.findViewById(R.id.date_published);
        datePublished.setText(currentContent.getDate_published().toString());

        TextView section = listItemView.findViewById(R.id.section_name);
        section.setText(currentContent.getSection_name());

        TextView news_url = listItemView.findViewById(R.id.news_url);
        news_url.setText(currentContent.getNews_url());
        news_url.setVisibility(View.GONE);

        return listItemView;
    }

}
