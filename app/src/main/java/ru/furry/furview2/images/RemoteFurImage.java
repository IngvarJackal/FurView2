package ru.furry.furview2.images;

import java.net.URL;

/**
 * Image class for not downloaded images for filtration
 */
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
            String fileUrl,
            String fileExt,
            String pageUrl) {
        this.searchQuery = searchQuery;
        this.description = description;
        this.score = score;
        this.rating = rating;
        this.fileUrl = fileUrl;
        this.fileExt = fileExt;
        this.pageUrl = pageUrl;
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
