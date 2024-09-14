package com.learneasily.myapplication.bottom_nav;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.learneasily.myapplication.R;

import java.util.List;

public class ArticlesAdapter extends RecyclerView.Adapter<ArticlesAdapter.ArticleViewHolder> {

    private List<Article> articlesList;

    public ArticlesAdapter(List<Article> articlesList) {
        this.articlesList = articlesList;
    }

    @NonNull
    @Override
    public ArticleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_article, parent, false);
        return new ArticleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ArticleViewHolder holder, int position) {
        Article article = articlesList.get(position);
        holder.titleTextView.setText(article.getTitle());
        holder.bodyTextView.setText(article.getBody());
        holder.likesTextView.setText("Лайки: " + article.getLikes());
        holder.viewsTextView.setText("Просмотры: " + article.getViews());
    }

    @Override
    public int getItemCount() {
        return articlesList.size();
    }

    public static class ArticleViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView, bodyTextView, likesTextView, viewsTextView;

        public ArticleViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.title_text_view);
            bodyTextView = itemView.findViewById(R.id.body_text_view);
            likesTextView = itemView.findViewById(R.id.likes_text_view);
            viewsTextView = itemView.findViewById(R.id.views_text_view);
        }
    }
}
