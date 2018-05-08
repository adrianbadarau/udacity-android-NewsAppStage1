package com.abadarau.newsapp;


import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class NewsItemAdapter extends ArrayAdapter<NewsApiItem> {
    private LayoutInflater inflater;
    private List<NewsApiItem> items;

    public NewsItemAdapter(@NonNull Context context, int resource, @NonNull List<NewsApiItem> objects) {
        super(context, resource, objects);
        this.items = objects;
        this.inflater = LayoutInflater.from(context);
    }

    @Nullable
    @Override
    public NewsApiItem getItem(int position) {
        return items.get(position);
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if(convertView == null){
            convertView = inflater.inflate(R.layout.activity_news_item_view, null);
        }
        NewsApiItem item = getItem(position);

        TextView headline = convertView.findViewById(R.id.news_item_headline_tv);
        headline.setText(item.getWebTitle());

        TextView author = convertView.findViewById(R.id.news_item_author_tv);
        author.setText(item.getAuthorName());

        TextView section = convertView.findViewById(R.id.news_item_section_tv);
        section.setText(item.getSectionName());

        TextView date = convertView.findViewById(R.id.news_item_date_published_tv);
        date.setText(item.getWebPublicationDate());

        return convertView;
    }

    public void setItems(List<NewsApiItem> items){
        this.items = items;
        notifyDataSetChanged();
    }


}
