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

    @Override
    public String toString() {
        return  new StringBuilder().append("getFileUrl ")
                .append(this.getFileUrl()).append("\ngetFileExt ")
                .append(this.getFileExt()).append("\ngetRating ")
                .append(this.getRating()).append("\n getSearchQuery ")
                .append(this.getSearchQuery()).append("\n getPageUrl ")
                .append(this.getPageUrl()).append("\n getDescription ")
                .append(this.getDescription()).toString();
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
