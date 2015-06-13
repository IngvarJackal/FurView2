package ru.furry.furview2.images;

import java.net.URL;

/**
 * Image class for not downloaded images
 */
public class RemoteFurImage {

    private String searchQuery;
    private String description;
    private Rating rating;
    private String fileUrl;
    private String fileExt;
    private String pageUrl;

    public RemoteFurImage(
            String searchQuery,
            String description,
            Rating rating,
            String fileUrl,
            String fileExt,
            String pageUrl) {
        this.searchQuery = searchQuery;
        this.description = description;
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
