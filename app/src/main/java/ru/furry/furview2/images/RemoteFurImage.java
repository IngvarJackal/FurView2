package ru.furry.furview2.images;

import java.net.URL;

public class RemoteFurImage {

    String searchQuery;
    String description;
    int score;
    Rating rating;
    String fileUrl;
    String fileExt;
    String pageUrl;

    public RemoteFurImage(
            String searchQuery,
            String description,
            int score,
            Rating rating,
            String file_url,
            String file_ext,
            String page_url) {
        this.searchQuery = searchQuery;
        this.description = description;
        this.score = score;
        this.rating = rating;
        this.fileUrl = file_url;
        this.fileExt = file_ext;
        this.pageUrl = page_url;
    }

    public String getSearchQuery() {
        return searchQuery;
    }

    public String getDescription() {
        return description;
    }

    public int getScore() {
        return score;
    }

    public Rating getRating() {
        return rating;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public String getFileExt() {
        return fileExt;
    }

    public String getPageUrl() {
        return pageUrl;
    }
}
