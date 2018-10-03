package com.example.isas88.news;

public class NewsContent {
    private String story_title;
    private String author_name;
    private String date_published;
    private String section_name;
    private String news_url;

    public NewsContent(String story_title, String author_name,
                       String date_published,
                       String section_name, String news_url){
        this.story_title    = story_title;
        this.author_name    = author_name;
        this.date_published = date_published;
        this.section_name   = section_name;
        this.news_url       = news_url;
    }

    public String getStory_title() {
        return story_title;
    }

    public String getAuthor_name() {
        return author_name;
    }

    public String getDate_published() {
        return date_published;
    }

    public String getSection_name() {
        return section_name;
    }

    public String getNews_url() { return news_url;

    }
}
